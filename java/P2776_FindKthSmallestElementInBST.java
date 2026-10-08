import java.util.*;

/** TUF 2776 - Find K-th smallest element in BST (LeetCode 230). Return the k-th smallest key (1-indexed), or -1 if the tree has fewer than k nodes. */
public class P2776_FindKthSmallestElementInBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: ignore the BST property, collect every key, sort, index. O(n log n) time, O(n) space. */
    static int bruteForce(TreeNode root, int k) {
        List<Integer> keys = new ArrayList<>();
        collect(root, keys);
        Collections.sort(keys);
        return k >= 1 && k <= keys.size() ? keys.get(k - 1) : -1;
    }

    static void collect(TreeNode node, List<Integer> out) {
        if (node == null) return;
        out.add(node.val);                       // any traversal order works: we sort afterwards
        collect(node.left, out);
        collect(node.right, out);
    }

    /** Approach 2: recursive in-order (which visits keys in sorted order) that stops at the k-th visit. O(h + k) time, O(h) stack. */
    static int recursiveInorder(TreeNode root, int k) {
        int[] state = {k, -1};                   // {visits still needed, answer}
        visit(root, state);
        return state[1];
    }

    static void visit(TreeNode node, int[] state) {
        if (node == null || state[0] == 0) return;
        visit(node.left, state);
        if (state[0] == 0) return;               // the answer was found in the left subtree
        if (--state[0] == 0) {                   // this node is the k-th in sorted order
            state[1] = node.val;
            return;
        }
        visit(node.right, state);
    }

    /** Approach 3: iterative in-order with an explicit stack; the k-th pop is the answer. O(h + k) time, O(h) space. */
    static int iterativeInorder(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {                // walk to the smallest key not yet visited
                stack.push(cur);
                cur = cur.left;
            }
            TreeNode node = stack.pop();
            if (--k == 0) return node.val;       // pops come out in ascending order
            cur = node.right;
        }
        return -1;
    }

    /** Approach 4: Morris in-order. Threads replace the stack; the walk runs to the end so every thread is removed. O(n) time, O(1) extra space. */
    static int morris(TreeNode root, int k) {
        int count = 0, answer = -1;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) {
                if (++count == k) answer = cur.val;
                cur = cur.right;                 // a real right child or a thread back to the successor
            } else {
                TreeNode pred = cur.left;        // in-order predecessor: rightmost node of the left subtree
                while (pred.right != null && pred.right != cur) pred = pred.right;
                if (pred.right == null) {
                    pred.right = cur;            // first arrival: leave a thread so we can come back
                    cur = cur.left;
                } else {
                    pred.right = null;           // second arrival: left subtree done, remove the thread
                    if (++count == k) answer = cur.val;
                    cur = cur.right;
                }
            }
        }
        return answer;
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

    /** Pre-order with null markers: two trees are identical exactly when their shapes match. */
    static String shape(TreeNode node) {
        return node == null ? "#" : node.val + "(" + shape(node.left) + "," + shape(node.right) + ")";
    }

    static void verify(TreeNode root, int k, int expected, String label) {
        String before = shape(root);
        int a = bruteForce(root, k), b = recursiveInorder(root, k), c = iterativeInorder(root, k), d = morris(root, k);
        check(a == expected && b == expected && c == expected && d == expected,
                "k=" + k + " in " + label + ": got " + a + ", " + b + ", " + c + ", " + d + ", expected " + expected);
        check(shape(root).equals(before), "Morris left the tree modified: " + label);
    }

    static void verify(Integer[] level, int k, int expected) {
        verify(fromLevelOrder(level), k, expected, Arrays.toString(level));
    }

    public static void main(String[] args) {
        Integer[] t1 = {3, 1, 4, null, 2};
        verify(t1, 1, 1);
        verify(t1, 2, 2);
        verify(t1, 4, 4);                                // k = n: the maximum
        Integer[] t2 = {5, 3, 6, 2, 4, null, null, 1};
        verify(t2, 3, 3);
        verify(t2, 6, 6);
        verify(t2, 1, 1);                                // deepest-left node
        verify(new Integer[]{7}, 1, 7);                  // single node
        verify(new Integer[]{0}, 1, 0);                  // 0 is a real key, not "missing"
        verify(new Integer[]{2, 1, 3}, 4, -1);           // k larger than n
        verify(new Integer[]{}, 1, -1);                  // empty tree
        verify(new Integer[]{1, null, 2, null, 3, null, 4, null, 5}, 4, 4);   // right-skewed chain
        verify(new Integer[]{5, 4, null, 3, null, 2, null, 1}, 2, 2);         // left-skewed chain

        // cross-check against sorted keys on seeded random BSTs
        Random rnd = new Random(2776);
        for (int trial = 0; trial < 200; trial++) {
            TreeNode root = null;
            TreeSet<Integer> keys = new TreeSet<>();
            int n = 1 + rnd.nextInt(40);
            for (int i = 0; i < n; i++) {
                int key = rnd.nextInt(1000);
                keys.add(key);
                root = insert(root, key);
            }
            List<Integer> sorted = new ArrayList<>(keys);
            for (int k = 1; k <= sorted.size(); k++) verify(root, k, sorted.get(k - 1), "random tree " + keys);
        }
        System.out.println("OK P2776_FindKthSmallestElementInBST");
    }
}
