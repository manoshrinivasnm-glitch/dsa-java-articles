import java.util.*;

/** TUF 105 - Kth Smallest and Largest element in BST. Return {kth smallest, kth largest}, or {-1, -1} when k is not in 1..n. */
public class P105_KthSmallestAndLargestElementInBST {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: collect the keys in any order, sort, index. O(n log n) time, O(n) space. */
    static int[] bruteForce(TreeNode root, int k) {
        List<Integer> vals = new ArrayList<>();
        preorder(root, vals);
        Collections.sort(vals);
        int n = vals.size();
        if (k < 1 || k > n) return new int[]{-1, -1};
        return new int[]{vals.get(k - 1), vals.get(n - k)};
    }

    static void preorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        out.add(node.val);
        preorder(node.left, out);
        preorder(node.right, out);
    }

    /** Approach 2: inorder already yields the keys sorted, so no sort is needed. O(n) time, O(n) space. */
    static int[] better(TreeNode root, int k) {
        List<Integer> sorted = new ArrayList<>();
        inorder(root, sorted);
        int n = sorted.size();
        if (k < 1 || k > n) return new int[]{-1, -1};
        return new int[]{sorted.get(k - 1), sorted.get(n - k)};
    }

    static void inorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        inorder(node.left, out);
        out.add(node.val);
        inorder(node.right, out);
    }

    /** Approach 3: iterative inorder that stops at the kth node, and its mirror image for the largest. O(h + k) time, O(h) space. */
    static int[] optimal(TreeNode root, int k) {
        return new int[]{kthSmallest(root, k), kthLargest(root, k)};
    }

    static int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        int count = 0;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {                        // go as far left as possible
                stack.push(cur);
                cur = cur.left;
            }
            cur = stack.pop();                           // the next key in increasing order
            if (++count == k) return cur.val;
            cur = cur.right;
        }
        return -1;                                       // fewer than k keys (or k < 1)
    }

    static int kthLargest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode cur = root;
        int count = 0;
        while (cur != null || !stack.isEmpty()) {
            while (cur != null) {                        // mirror image: go as far right as possible
                stack.push(cur);
                cur = cur.right;
            }
            cur = stack.pop();                           // the next key in decreasing order
            if (++count == k) return cur.val;
            cur = cur.left;
        }
        return -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Build a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode fromLevelOrder(Integer... a) {
        if (a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Queue<TreeNode> q = new LinkedList<>();
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

    static void verify(TreeNode root, int k, int smallest, int largest, String label) {
        int[] expected = {smallest, largest};
        int[][] results = {bruteForce(root, k), better(root, k), optimal(root, k)};
        for (int[] r : results) {
            check(Arrays.equals(r, expected), label + " k=" + k + " gave " + Arrays.toString(r) + ", expected " + Arrays.toString(expected));
        }
    }

    public static void main(String[] args) {
        TreeNode t1 = fromLevelOrder(5, 3, 6, 2, 4, null, null, 1);   // keys 1..6
        verify(t1, 3, 3, 4, "t1");
        verify(t1, 1, 1, 6, "t1");
        verify(t1, 6, 6, 1, "t1");                       // k = n: smallest and largest swap roles
        TreeNode t2 = fromLevelOrder(3, 1, 4, null, 2);                 // keys 1..4
        verify(t2, 1, 1, 4, "t2");
        verify(t2, 2, 2, 3, "t2");
        verify(t2, 5, -1, -1, "t2");                     // k > n
        verify(t2, 0, -1, -1, "t2");                     // k < 1
        verify(fromLevelOrder(7), 1, 7, 7, "single");
        verify(null, 1, -1, -1, "empty");

        // a right chain 1 -> 2 -> ... -> 50
        TreeNode chain = null;
        for (int v = 50; v >= 1; v--) {
            TreeNode node = new TreeNode(v);
            node.right = chain;
            chain = node;
        }
        verify(chain, 10, 10, 41, "chain");
        verify(chain, 50, 50, 1, "chain");
        System.out.println("OK P105_KthSmallestAndLargestElementInBST");
    }
}
