import java.util.*;

/** TUF 97 - Correct BST with two nodes swapped. The keys of exactly two nodes were swapped; restore the BST in place without changing its shape. */
public class P97_CorrectBSTWithTwoNodesSwapped {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. Copy the in-order keys, sort them, and write them back in in-order. O(n log n) time, O(n) space. */
    static void bruteForce(TreeNode root) {
        List<Integer> keys = new ArrayList<>();
        collectInorder(root, keys);
        Collections.sort(keys);
        int[] next = {0};
        writeInorder(root, keys, next);
    }

    static void collectInorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        collectInorder(node.left, out);
        out.add(node.val);
        collectInorder(node.right, out);
    }

    static void writeInorder(TreeNode node, List<Integer> keys, int[] next) {
        if (node == null) return;
        writeInorder(node.left, keys, next);
        node.val = keys.get(next[0]++);              // the k-th in-order node gets the k-th smallest key
        writeInorder(node.right, keys, next);
    }

    /** State of an in-order scan: the previous node and the nodes around each drop (prev.val > node.val). */
    static class Scan {
        TreeNode prev, first, middle, last;

        void visit(TreeNode node) {
            if (prev != null && prev.val > node.val) {
                if (first == null) { first = prev; middle = node; }   // first drop: prev is the too-large key
                else last = node;                                     // second drop: node is the too-small key
            }
            prev = node;
        }

        void repair() {
            if (first == null) return;                       // no drop: the tree is already valid
            TreeNode other = last != null ? last : middle;   // one drop means the swapped keys were neighbours
            int t = first.val;
            first.val = other.val;
            other.val = t;
        }
    }

    /** Approach 2: optimal. One recursive in-order pass finds the one or two drops; swap the misplaced pair back. O(n) time, O(h) space. */
    static void optimal(TreeNode root) {
        Scan scan = new Scan();
        inorder(root, scan);
        scan.repair();
    }

    static void inorder(TreeNode node, Scan scan) {
        if (node == null) return;
        inorder(node.left, scan);
        scan.visit(node);
        inorder(node.right, scan);
    }

    /** Approach 3: optimal in O(1) space. Same drop detection, but the in-order walk is a Morris traversal. O(n) time, O(1) space. */
    static void morris(TreeNode root) {
        Scan scan = new Scan();
        TreeNode cur = root;
        while (cur != null) {
            if (cur.left == null) {
                scan.visit(cur);
                cur = cur.right;
            } else {
                TreeNode pred = cur.left;                       // rightmost node of the left subtree
                while (pred.right != null && pred.right != cur) pred = pred.right;
                if (pred.right == null) {
                    pred.right = cur;                           // first arrival: thread back to cur, go left
                    cur = cur.left;
                } else {
                    pred.right = null;                          // second arrival: left side done, remove thread
                    scan.visit(cur);
                    cur = cur.right;
                }
            }
        }
        scan.repair();
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, where null means "no child here". */
    static TreeNode build(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < vals.length) {
            TreeNode cur = queue.poll();
            if (i < vals.length && vals[i] != null) { cur.left = new TreeNode(vals[i]); queue.add(cur.left); }
            i++;
            if (i < vals.length && vals[i] != null) { cur.right = new TreeNode(vals[i]); queue.add(cur.right); }
            i++;
        }
        return root;
    }

    /** LeetCode-style level order with trailing nulls removed, used to compare trees. */
    static List<Integer> levelOrder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();             // LinkedList accepts null entries
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode cur = queue.poll();
            if (cur == null) { out.add(null); continue; }
            out.add(cur.val);
            queue.add(cur.left);
            queue.add(cur.right);
        }
        while (!out.isEmpty() && out.get(out.size() - 1) == null) out.remove(out.size() - 1);
        return out;
    }

    /** Copy of a level-order array with the keys a and b exchanged. */
    static Integer[] swapKeys(Integer[] level, int a, int b) {
        Integer[] out = level.clone();
        for (int i = 0; i < out.length; i++) {
            if (out[i] == null) continue;
            if (out[i] == a) out[i] = b;
            else if (out[i] == b) out[i] = a;
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** correct is a valid BST; the test swaps keys a and b, runs each approach, and expects the original tree back. */
    static void verify(Integer[] correct, int a, int b) {
        List<Integer> expected = levelOrder(build(correct));
        Integer[] broken = swapKeys(correct, a, b);
        String in = Arrays.toString(broken);
        TreeNode t1 = build(broken);
        bruteForce(t1);
        check(levelOrder(t1).equals(expected), "bruteForce " + in);
        TreeNode t2 = build(broken);
        optimal(t2);
        check(levelOrder(t2).equals(expected), "optimal " + in);
        TreeNode t3 = build(broken);
        morris(t3);
        check(levelOrder(t3).equals(expected), "morris " + in);
    }

    public static void main(String[] args) {
        verify(new Integer[]{3, 1, null, null, 2}, 1, 3);             // broken input [1, 3, null, null, 2]
        verify(new Integer[]{2, 1, 4, null, null, 3}, 2, 3);          // broken input [3, 1, 4, null, null, 2]
        verify(new Integer[]{2, 1, 3}, 1, 2);                         // neighbours in in-order: one drop
        verify(new Integer[]{2, 1}, 1, 2);                            // two nodes
        Integer[] big = {10, 5, 15, 2, 7, 12, 20};
        verify(big, 5, 20);                                           // far apart: two drops
        verify(big, 10, 12);                                          // root involved, neighbours
        verify(big, 2, 20);                                           // smallest and largest keys
        verify(new Integer[]{0, Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MIN_VALUE, Integer.MAX_VALUE);
        verify(new Integer[]{5}, 5, 5);                               // single node, nothing to fix
        System.out.println("OK P97_CorrectBSTWithTwoNodesSwapped");
    }
}
