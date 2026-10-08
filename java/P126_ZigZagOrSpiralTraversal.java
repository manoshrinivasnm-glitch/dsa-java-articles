import java.util.*;

/** TUF 126 - Zig Zag or Spiral Traversal. Return the levels top to bottom, the first left to right, the next right to left, and so on alternately. */
public class P126_ZigZagOrSpiralTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: ordinary level-order traversal, then reverse every second level. O(n) time, O(w) extra space. */
    static List<List<Integer>> reverseAlternateLevels(TreeNode root) {
        List<List<Integer>> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        boolean leftToRight = true;
        while (!q.isEmpty()) {
            int size = q.size();
            List<Integer> row = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                row.add(node.val);
                if (node.left != null) q.add(node.left);
                if (node.right != null) q.add(node.right);
            }
            if (!leftToRight) Collections.reverse(row);
            out.add(row);
            leftToRight = !leftToRight;
        }
        return out;
    }

    /** Approach 2: two stacks. Popping a stack reverses the order, and pushing children left-then-right or right-then-left decides the next level's direction. O(n) time, O(w) space. */
    static List<List<Integer>> twoStacks(TreeNode root) {
        List<List<Integer>> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> cur = new ArrayDeque<>();
        cur.push(root);
        boolean leftToRight = true;
        while (!cur.isEmpty()) {
            Deque<TreeNode> next = new ArrayDeque<>();
            List<Integer> row = new ArrayList<>();
            while (!cur.isEmpty()) {
                TreeNode node = cur.pop();
                row.add(node.val);
                if (leftToRight) {
                    if (node.left != null) next.push(node.left);
                    if (node.right != null) next.push(node.right);
                } else {
                    if (node.right != null) next.push(node.right);
                    if (node.left != null) next.push(node.left);
                }
            }
            out.add(row);
            cur = next;
            leftToRight = !leftToRight;
        }
        return out;
    }

    /** Approach 3: level-order traversal that writes each value straight into its final slot, index i going left to right or size - 1 - i going right to left. O(n) time, O(w) space, no reversal pass. */
    static List<List<Integer>> optimal(TreeNode root) {
        List<List<Integer>> out = new ArrayList<>();
        if (root == null) return out;
        Deque<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        boolean leftToRight = true;
        while (!q.isEmpty()) {
            int size = q.size();
            Integer[] row = new Integer[size];
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                row[leftToRight ? i : size - 1 - i] = node.val;
                if (node.left != null) q.add(node.left);
                if (node.right != null) q.add(node.right);
            }
            out.add(new ArrayList<>(Arrays.asList(row)));
            leftToRight = !leftToRight;
        }
        return out;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode build(Integer[] a) {
        if (a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Deque<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < a.length) {
            TreeNode node = q.poll();
            if (i < a.length && a[i] != null) { node.left = new TreeNode(a[i]); q.add(node.left); }
            i++;
            if (i < a.length && a[i] != null) { node.right = new TreeNode(a[i]); q.add(node.right); }
            i++;
        }
        return root;
    }

    static TreeNode randomTree(Random rnd, int depth) {
        if (depth == 0 || rnd.nextInt(4) == 0) return null;
        TreeNode node = new TreeNode(rnd.nextInt(100));
        node.left = randomTree(rnd, depth - 1);
        node.right = randomTree(rnd, depth - 1);
        return node;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] level, List<List<Integer>> expected) {
        TreeNode root = build(level);
        String s = Arrays.toString(level);
        check(reverseAlternateLevels(root).equals(expected), "reverseAlternateLevels " + s);
        check(twoStacks(root).equals(expected), "twoStacks " + s);
        check(optimal(root).equals(expected), "optimal " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{3, 9, 20, null, null, 15, 7}, List.of(List.of(3), List.of(20, 9), List.of(15, 7)));
        verify(new Integer[]{1}, List.of(List.of(1)));                                       // single node
        verify(new Integer[]{}, List.of());                                                  // edge: empty tree
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7}, List.of(List.of(1), List.of(3, 2), List.of(4, 5, 6, 7)));
        verify(new Integer[]{1, 2, 3, 4, null, null, 5}, List.of(List.of(1), List.of(3, 2), List.of(4, 5)));
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
                List.of(List.of(1), List.of(3, 2), List.of(4, 5, 6, 7), List.of(15, 14, 13, 12, 11, 10, 9, 8)));
        verify(new Integer[]{1, 2, 3, 4, null, null, 5, 6, null, null, 7},
                List.of(List.of(1), List.of(3, 2), List.of(4, 5), List.of(7, 6)));

        // seeded random trees: all three methods must agree
        Random rnd = new Random(126);
        for (int t = 0; t < 300; t++) {
            TreeNode root = randomTree(rnd, 8);
            List<List<Integer>> expected = reverseAlternateLevels(root);
            check(twoStacks(root).equals(expected), "twoStacks random #" + t);
            check(optimal(root).equals(expected), "optimal random #" + t);
        }
        System.out.println("OK P126_ZigZagOrSpiralTraversal");
    }
}
