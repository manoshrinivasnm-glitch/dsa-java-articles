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
        return hasPartner(root, root, k);
    }

    static boolean hasPartner(TreeNode node, TreeNode root, int k) {
        if (node == null) return false;
        long need = (long) k - node.val;                 // long: k - val can overflow int
        if (need != node.val && contains(root, need)) return true;   // distinct keys: need == val would be this same node
        return hasPartner(node.left, root, k) || hasPartner(node.right, root, k);
    }

    static boolean contains(TreeNode root, long key) {
        TreeNode cur = root;
        while (cur != null) {
            if (cur.val == key) return true;
            cur = key < cur.val ? cur.left : cur.right;
        }
        return false;
    }

    /** Approach 2: in-order gives the keys sorted, then the classic two-pointer sweep. O(n) time, O(n) space. */
    static boolean better(TreeNode root, int k) {
        List<Integer> sorted = new ArrayList<>();
        inorder(root, sorted);
        int lo = 0, hi = sorted.size() - 1;
        while (lo < hi) {
            long sum = (long) sorted.get(lo) + sorted.get(hi);
            if (sum == k) return true;
            if (sum < k) lo++; else hi--;
        }
        return false;
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    /** Approach 3: two lazy BST iterators (ascending and descending) act as the two pointers without building the list. O(n) time, O(h) space. */
    static boolean optimal(TreeNode root, int k) {
        if (root == null) return false;
        BSTIterator ascending = new BSTIterator(root, false);
        BSTIterator descending = new BSTIterator(root, true);
        int lo = ascending.next(), hi = descending.next();
        while (lo < hi) {                                // distinct keys: lo < hi means two different nodes
            long sum = (long) lo + hi;
            if (sum == k) return true;
            if (sum < k) lo = ascending.next(); else hi = descending.next();
        }
        return false;
    }

    /** In-order iterator holding at most h nodes; reverse = true yields keys from largest to smallest. */
    static class BSTIterator {
        private final Deque<TreeNode> stack = new ArrayDeque<>();
        private final boolean reverse;

        BSTIterator(TreeNode root, boolean reverse) {
            this.reverse = reverse;
            pushChain(root);
        }

        int next() {
            TreeNode node = stack.pop();
            pushChain(reverse ? node.left : node.right);
            return node.val;
        }

        private void pushChain(TreeNode node) {      // the leftmost (or rightmost) path below node
            while (node != null) {
                stack.push(node);
                node = reverse ? node.right : node.left;
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
