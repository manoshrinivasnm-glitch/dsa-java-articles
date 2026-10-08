import java.util.*;

/** TUF 125 - Vertical Order Traversal. Root at (row 0, col 0); a left child is (row + 1, col - 1), a right child (row + 1, col + 1). Return the columns left to right, each sorted by row, ties by value. */
public class P125_VerticalOrderTraversal {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** A queue entry for the BFS: a node and its coordinates. */
    static class Tuple {
        TreeNode node;
        int row, col;
        Tuple(TreeNode node, int row, int col) { this.node = node; this.row = row; this.col = col; }
    }

    /** Approach 1: record {col, row, value} for every node, sort the records once, then cut the sorted list into columns. O(n log n) time, O(n) space. */
    static List<List<Integer>> sortTriples(TreeNode root) {
        List<int[]> triples = new ArrayList<>();
        collect(root, 0, 0, triples);
        triples.sort((a, b) -> a[0] != b[0] ? Integer.compare(a[0], b[0])
                : a[1] != b[1] ? Integer.compare(a[1], b[1])
                : Integer.compare(a[2], b[2]));
        List<List<Integer>> out = new ArrayList<>();
        int prevCol = 0;
        for (int[] t : triples) {
            if (out.isEmpty() || t[0] != prevCol) {
                out.add(new ArrayList<>());          // a new column starts
                prevCol = t[0];
            }
            out.get(out.size() - 1).add(t[2]);
        }
        return out;
    }

    static void collect(TreeNode node, int row, int col, List<int[]> triples) {
        if (node == null) return;
        triples.add(new int[]{col, row, node.val});
        collect(node.left, row + 1, col - 1, triples);
        collect(node.right, row + 1, col + 1, triples);
    }

    /** Approach 2: BFS into TreeMap col -> TreeMap row -> min-heap of values. The maps keep columns and rows sorted, the heap orders values that share a cell. O(n log n) time, O(n) space. */
    static List<List<Integer>> treeMapBfs(TreeNode root) {
        TreeMap<Integer, TreeMap<Integer, PriorityQueue<Integer>>> map = new TreeMap<>();
        Deque<Tuple> q = new ArrayDeque<>();
        if (root != null) q.add(new Tuple(root, 0, 0));
        while (!q.isEmpty()) {
            Tuple t = q.poll();
            map.computeIfAbsent(t.col, k -> new TreeMap<>())
               .computeIfAbsent(t.row, k -> new PriorityQueue<>())
               .add(t.node.val);
            if (t.node.left != null) q.add(new Tuple(t.node.left, t.row + 1, t.col - 1));
            if (t.node.right != null) q.add(new Tuple(t.node.right, t.row + 1, t.col + 1));
        }
        List<List<Integer>> out = new ArrayList<>();
        for (TreeMap<Integer, PriorityQueue<Integer>> rows : map.values()) {
            List<Integer> column = new ArrayList<>();
            for (PriorityQueue<Integer> cell : rows.values()) {
                while (!cell.isEmpty()) column.add(cell.poll());
            }
            out.add(column);
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
        TreeNode node = new TreeNode(rnd.nextInt(20));       // small range: many equal values share cells
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
        check(sortTriples(root).equals(expected), "sortTriples " + s);
        check(treeMapBfs(root).equals(expected), "treeMapBfs " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{3, 9, 20, null, null, 15, 7},
                List.of(List.of(9), List.of(3, 15), List.of(20), List.of(7)));
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7},
                List.of(List.of(4), List.of(2), List.of(1, 5, 6), List.of(3), List.of(7)));
        verify(new Integer[]{1, 2, 3, 4, 6, 5, 7},                                     // 6 and 5 share (row 2, col 0): sorted by value
                List.of(List.of(4), List.of(2), List.of(1, 5, 6), List.of(3), List.of(7)));
        verify(new Integer[]{1}, List.of(List.of(1)));                                  // single node
        verify(new Integer[]{}, List.of());                                             // edge: empty tree
        verify(new Integer[]{5, 3, null, null, 1}, List.of(List.of(3), List.of(5, 1)));  // row order beats value order
        verify(new Integer[]{1, 2, 3, null, 9, 5}, List.of(List.of(2), List.of(1, 5, 9), List.of(3))); // BFS meets 9 before 5

        // seeded random trees with many duplicate values: both methods must agree
        Random rnd = new Random(125);
        for (int t = 0; t < 300; t++) {
            TreeNode root = randomTree(rnd, 8);
            check(treeMapBfs(root).equals(sortTriples(root)), "random tree #" + t);
        }
        System.out.println("OK P125_VerticalOrderTraversal");
    }
}
