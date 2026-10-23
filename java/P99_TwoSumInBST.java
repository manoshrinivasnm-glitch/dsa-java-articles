import java.util.*;

/** TUF 99 - Two sum in BST (LeetCode 653). Return true if two different nodes of the BST have keys adding up to k. Keys are distinct. */
public class P99_TwoSumInBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: for every node, binary-search the tree for its complement. O(n * h) time, O(h) stack. */
    static boolean bruteForce(TreeNode root, int k) {
        return hasPartner(root, root, k);        // try every node as the first of the pair
    }

    static boolean hasPartner(TreeNode node, TreeNode root, int k) {
        if (node == null) return false;                  // empty subtree: no pair starts here
        long need = (long) k - node.val;                 // long: k - val can overflow int
        // distinct keys: need == val would be this same node, so it does not count
        if (need != node.val && contains(root, need)) return true;
        return hasPartner(node.left, root, k) || hasPartner(node.right, root, k);   // try the rest of the tree
    }

    static boolean contains(TreeNode root, long key) {
        TreeNode cur = root;
        while (cur != null) {                            // ordinary BST search from the root
            if (cur.val == key) return true;
            cur = key < cur.val ? cur.left : cur.right;  // only one side can hold key
        }
        return false;                                    // fell off the tree: key is absent
    }

    /** Approach 2: in-order gives the keys sorted, then the classic two-pointer sweep. O(n) time, O(n) space. */
    static boolean better(TreeNode root, int k) {
        List<Integer> sorted = new ArrayList<>();        // every key, in increasing order
        inorder(root, sorted);
        int lo = 0, hi = sorted.size() - 1;              // smallest and largest keys not ruled out yet
        while (lo < hi) {                                // two different positions still left
            long sum = (long) sorted.get(lo) + sorted.get(hi);   // long: the sum can overflow int
            if (sum == k) return true;
            if (sum < k) lo++; else hi--;                // sum too small: raise lo; too big: lower hi
        }
        return false;                                    // the pointers met without a match
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;                        // empty subtree adds nothing
        inorder(node.left, out);                         // smaller keys first
        out.add(node.val);                               // then this key
        inorder(node.right, out);                        // then the larger keys
    }

    /** Approach 3: two lazy BST iterators (ascending and descending) act as the two pointers without building the list. O(n) time, O(h) space. */
    static boolean optimal(TreeNode root, int k) {
        if (root == null) return false;                  // empty tree has no pair
        BSTIterator ascending = new BSTIterator(root, false);   // yields keys smallest first
        BSTIterator descending = new BSTIterator(root, true);   // yields keys largest first
        int lo = ascending.next(), hi = descending.next();      // smallest and largest keys
        while (lo < hi) {                                // distinct keys: lo < hi means two different nodes
            long sum = (long) lo + hi;                   // long: the sum can overflow int
            if (sum == k) return true;
            // too small -> next bigger low key; too big -> next smaller high key
            if (sum < k) lo = ascending.next(); else hi = descending.next();
        }
        return false;                                    // the two pointers met without a match
    }

    /** In-order iterator holding at most h nodes; reverse = true yields keys from largest to smallest. */
    static class BSTIterator {
        private final Deque<TreeNode> stack = new ArrayDeque<>();   // top is always the next key to return
        private final boolean reverse;                   // false: ascending, true: descending

        BSTIterator(TreeNode root, boolean reverse) {
            this.reverse = reverse;
            pushChain(root);                             // the first key is at the end of this path
        }

        int next() {
            TreeNode node = stack.pop();                 // the next key in this iterator's order
            pushChain(reverse ? node.left : node.right); // the keys right after node live in this subtree
            return node.val;
        }

        private void pushChain(TreeNode node) {      // the leftmost (or rightmost) path below node
            while (node != null) {
                stack.push(node);                        // returned later, after everything below it
                node = reverse ? node.right : node.left; // step toward the smallest (or largest) key
            }
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Build a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode fromLevelOrder(Integer... a) {
        if (a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < a.length) {
            TreeNode cur = q.poll();
            if (a[i] != null) { cur.left = new TreeNode(a[i]); q.add(cur.left); }
            i++;
            if (i < a.length && a[i] != null) { cur.right = new TreeNode(a[i]); q.add(cur.right); }
            i++;
        }
        return root;
    }

    static TreeNode insert(TreeNode root, int key) {
        if (root == null) return new TreeNode(key);
        if (key < root.val) root.left = insert(root.left, key);
        else if (key > root.val) root.right = insert(root.right, key);
        return root;
    }

    static void verify(TreeNode root, int k, boolean expected, String label) {
        boolean a = bruteForce(root, k), b = better(root, k), c = optimal(root, k);
        check(a == expected && b == expected && c == expected,
                "k=" + k + " in " + label + ": got " + a + ", " + b + ", " + c + ", expected " + expected);
    }

    static void verify(Integer[] level, int k, boolean expected) {
        verify(fromLevelOrder(level), k, expected, Arrays.toString(level));
    }

    public static void main(String[] args) {
        Integer[] t = {5, 3, 6, 2, 4, null, 7};
        verify(t, 9, true);                              // 2 + 7, 3 + 6, 4 + 5
        verify(t, 28, false);
        verify(t, 13, true);                             // 6 + 7: the two largest keys
        verify(t, 5, true);                              // 2 + 3: the two smallest keys
        verify(t, 10, true);                             // 3 + 7, even though 5 + 5 is not allowed
        verify(t, 14, false);                            // 7 + 7 would reuse one node
        verify(t, 4, false);                             // below the smallest possible pair
        verify(new Integer[]{2, 1, 3}, 4, true);
        verify(new Integer[]{2, 1, 3}, 6, false);        // 3 + 3 is not allowed
        verify(new Integer[]{0, -4, 4, -6, -1}, 0, true);   // negative keys: -4 + 4
        verify(new Integer[]{0, -4, 4, -6, -1}, -10, true); // -6 + -4
        verify(new Integer[]{1}, 2, false);              // single node
        verify(new Integer[]{}, 0, false);               // empty tree
        verify(new Integer[]{2_000_000_000, 1_999_999_999}, -294967297, false); // naive int addition wraps to exactly this k

        // cross-check against a brute pair scan on seeded random BSTs
        Random rnd = new Random(99);
        for (int trial = 0; trial < 200; trial++) {
            TreeNode root = null;
            TreeSet<Integer> keys = new TreeSet<>();
            int n = rnd.nextInt(25);
            for (int i = 0; i < n; i++) {
                int key = rnd.nextInt(101) - 50;
                keys.add(key);
                root = insert(root, key);
            }
            List<Integer> list = new ArrayList<>(keys);
            for (int k = -101; k <= 101; k += 7) {
                boolean expected = false;
                for (int i = 0; i < list.size() && !expected; i++)
                    for (int j = i + 1; j < list.size(); j++)
                        if (list.get(i) + list.get(j) == k) { expected = true; break; }
                verify(root, k, expected, "random tree " + keys);
            }
        }
        System.out.println("OK P99_TwoSumInBST");
    }
}
