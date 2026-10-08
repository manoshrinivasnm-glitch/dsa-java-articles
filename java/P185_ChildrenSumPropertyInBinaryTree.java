import java.util.*;

/**
 * TUF 185 - Children Sum Property in Binary Tree. Every non-leaf node must equal the sum of its children.
 * Check the property, and make any tree satisfy it by only ever increasing node values (values are non-negative).
 */
public class P185_ChildrenSumPropertyInBinaryTree {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Check: does every non-leaf node equal the sum of its children? O(n) time, O(h) space. */
    static boolean satisfies(TreeNode node) {
        if (node == null || (node.left == null && node.right == null)) return true;   // leaves have nothing to match
        int sum = (node.left == null ? 0 : node.left.val) + (node.right == null ? 0 : node.right.val);
        return node.val == sum && satisfies(node.left) && satisfies(node.right);
    }

    /** Approach 1: post-order fix-up; a parent that is too small is raised, a parent that is too big pushes its surplus down one path. O(n * h) time, O(h) space. */
    static void bruteForce(TreeNode node) {
        if (node == null || (node.left == null && node.right == null)) return;
        bruteForce(node.left);                                    // both subtrees already satisfy the property
        bruteForce(node.right);
        int sum = (node.left == null ? 0 : node.left.val) + (node.right == null ? 0 : node.right.val);
        if (sum > node.val) node.val = sum;                       // raise the parent
        else if (sum < node.val) pushDown(node, node.val - sum);  // raise a child instead, and repair below it
    }

    static void pushDown(TreeNode node, int diff) {
        TreeNode child = node.left != null ? node.left : node.right;
        while (child != null) {                                   // walk one path down to a leaf
            child.val += diff;                                    // each raised node needs one raised child below it
            child = child.left != null ? child.left : child.right;
        }
    }

    /** Approach 2: one DFS. Going down, lift the children to at least the parent's value; coming up, set the parent to the children's sum. O(n) time, O(h) space. */
    static void optimal(TreeNode node) {
        if (node == null) return;
        int childSum = (node.left == null ? 0 : node.left.val) + (node.right == null ? 0 : node.right.val);
        if (childSum >= node.val) {
            node.val = childSum;                                  // children already cover the parent
        } else {
            if (node.left != null) node.left.val = node.val;      // lift each child to the parent's value
            if (node.right != null) node.right.val = node.val;
        }
        optimal(node.left);
        optimal(node.right);
        if (node.left != null || node.right != null) {            // a leaf keeps its value
            node.val = (node.left == null ? 0 : node.left.val) + (node.right == null ? 0 : node.right.val);
        }
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode build(Integer[] vals) {
        if (vals.length == 0 || vals[0] == null) return null;
        TreeNode root = new TreeNode(vals[0]);
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < vals.length) {
            TreeNode cur = q.poll();
            if (i < vals.length && vals[i] != null) { cur.left = new TreeNode(vals[i]); q.add(cur.left); }
            i++;
            if (i < vals.length && vals[i] != null) { cur.right = new TreeNode(vals[i]); q.add(cur.right); }
            i++;
        }
        return root;
    }

    /** Node values in level order (shape is unchanged by the algorithms, so positions line up). */
    static List<Integer> levelValues(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            TreeNode node = q.poll();
            out.add(node.val);
            if (node.left != null) q.add(node.left);
            if (node.right != null) q.add(node.right);
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyCheck(Integer[] vals, boolean expected) {
        check(satisfies(build(vals)) == expected, "satisfies " + Arrays.toString(vals));
    }

    /** Runs one conversion on a fresh copy and checks the result: property holds, nothing decreased, exact values match. */
    static void verifyConvert(Integer[] vals, boolean useOptimal, List<Integer> expected) {
        TreeNode root = build(vals);
        List<Integer> before = levelValues(root);
        if (useOptimal) optimal(root); else bruteForce(root);
        List<Integer> after = levelValues(root);
        String in = (useOptimal ? "optimal " : "bruteForce ") + Arrays.toString(vals) + " got " + after;
        check(satisfies(root), "property fails after " + in);
        check(after.size() == before.size(), "shape changed " + in);
        for (int i = 0; i < after.size(); i++) check(after.get(i) >= before.get(i), "value decreased " + in);
        check(after.equals(expected), "unexpected values " + in);
    }

    public static void main(String[] args) {
        verifyCheck(new Integer[]{10, 8, 2, 3, 5, 2}, true);
        verifyCheck(new Integer[]{35, 5, 30}, true);
        verifyCheck(new Integer[]{3, 1, 2, null, 1}, true);       // a node with one child must equal that child
        verifyCheck(new Integer[]{3, 1, 2, null, 0}, false);
        verifyCheck(new Integer[]{1, 4, 3, 5}, false);
        verifyCheck(new Integer[]{5, 3, 1}, false);
        verifyCheck(new Integer[]{1}, true);                      // a single leaf trivially holds
        verifyCheck(new Integer[]{}, true);                       // edge: empty tree

        // The two conversions are both valid but produce different trees, so each has its own expected values.
        Integer[] a = {50, 7, 2, 3, 5, 1, 30};
        verifyConvert(a, false, List.of(50, 19, 31, 14, 5, 1, 30));
        verifyConvert(a, true, List.of(200, 100, 100, 50, 50, 50, 50));
        Integer[] b = {2, 35, 10, 2, 3, 5, 2};
        verifyConvert(b, false, List.of(45, 35, 10, 32, 3, 8, 2));
        verifyConvert(b, true, List.of(90, 70, 20, 35, 35, 10, 10));
        Integer[] c = {10, 2, 3};
        verifyConvert(c, false, List.of(10, 7, 3));
        verifyConvert(c, true, List.of(20, 10, 10));
        Integer[] d = {100, 1, null, 1, null, 1};                 // a chain: the surplus travels to the leaf
        verifyConvert(d, false, List.of(100, 100, 100, 100));
        verifyConvert(d, true, List.of(100, 100, 100, 100));
        Integer[] e = {1, 2, 3, 4, 5, 6, 7};                      // parents only need raising
        verifyConvert(e, false, List.of(22, 9, 13, 4, 5, 6, 7));
        verifyConvert(e, true, List.of(22, 9, 13, 4, 5, 6, 7));
        Integer[] f = {7};
        verifyConvert(f, false, List.of(7));
        verifyConvert(f, true, List.of(7));
        Integer[] g = {};                                         // edge: empty tree
        verifyConvert(g, false, List.of());
        verifyConvert(g, true, List.of());
        System.out.println("OK P185_ChildrenSumPropertyInBinaryTree");
    }
}
