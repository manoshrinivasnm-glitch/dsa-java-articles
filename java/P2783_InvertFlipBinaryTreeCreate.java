import java.util.*;

/** TUF 2783 - Invert/Flip Binary Tree. Mirror the tree so every left child becomes a right child and vice versa. */
public class P2783_InvertFlipBinaryTreeCreate {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion, in place. Invert both subtrees, then swap them. O(n) time, O(h) stack. */
    static TreeNode recursive(TreeNode root) {
        if (root == null) return null;
        TreeNode left = recursive(root.left);
        TreeNode right = recursive(root.right);
        root.left = right;
        root.right = left;
        return root;
    }

    /** Approach 2: iterative BFS, in place. Visit every node once and swap its two children. O(n) time, O(w) space. */
    static TreeNode iterative(TreeNode root) {
        if (root == null) return null;
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            TreeNode tmp = node.left;           // swap the children of this node
            node.left = node.right;
            node.right = tmp;
            if (node.left != null) queue.add(node.left);
            if (node.right != null) queue.add(node.right);
        }
        return root;
    }

    /** Approach 3: create a new mirrored tree and leave the input untouched. O(n) time, O(n) new nodes plus O(h) stack. */
    static TreeNode createMirror(TreeNode root) {
        if (root == null) return null;
        TreeNode copy = new TreeNode(root.val);
        copy.left = createMirror(root.right);   // the mirror's left side is built from the original's right side
        copy.right = createMirror(root.left);
        return copy;
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

    /** Level order with null for missing children, trailing nulls removed (the inverse of build). */
    static List<Integer> toLevelOrder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        List<TreeNode> level = new ArrayList<>();
        level.add(root);
        while (!level.isEmpty()) {
            List<TreeNode> next = new ArrayList<>();
            for (TreeNode node : level) {
                out.add(node == null ? null : node.val);
                if (node != null) { next.add(node.left); next.add(node.right); }
            }
            level = next;
        }
        while (!out.isEmpty() && out.get(out.size() - 1) == null) out.remove(out.size() - 1);
        return out;
    }

    static boolean sharesNodes(TreeNode a, TreeNode b) {
        Set<TreeNode> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<TreeNode> stack = new ArrayDeque<>();
        if (a != null) stack.push(a);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            seen.add(node);
            if (node.left != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
        }
        if (b != null) stack.push(b);
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            if (seen.contains(node)) return true;
            if (node.left != null) stack.push(node.left);
            if (node.right != null) stack.push(node.right);
        }
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(Integer[] input, Integer[] expected) {
        List<Integer> want = Arrays.asList(expected);
        String s = Arrays.toString(input);
        check(toLevelOrder(recursive(build(input))).equals(want), "recursive " + s);
        check(toLevelOrder(iterative(build(input))).equals(want), "iterative " + s);

        TreeNode original = build(input);
        TreeNode mirror = createMirror(original);
        check(toLevelOrder(mirror).equals(want), "createMirror " + s);
        check(toLevelOrder(original).equals(Arrays.asList(input)), "createMirror changed its input " + s);
        check(!sharesNodes(original, mirror), "createMirror reused an input node " + s);

        TreeNode twice = recursive(iterative(build(input)));
        check(toLevelOrder(twice).equals(Arrays.asList(input)), "inverting twice gives the tree back " + s);
    }

    public static void main(String[] args) {
        verify(new Integer[]{4, 2, 7, 1, 3, 6, 9}, new Integer[]{4, 7, 2, 9, 6, 3, 1});
        verify(new Integer[]{2, 1, 3}, new Integer[]{2, 3, 1});
        verify(new Integer[]{}, new Integer[]{});                                    // edge: empty tree
        verify(new Integer[]{1}, new Integer[]{1});                                  // edge: single node
        verify(new Integer[]{1, 2}, new Integer[]{1, null, 2});                      // a lone left child moves right
        verify(new Integer[]{1, 2, null, 3}, new Integer[]{1, null, 2, null, 3});    // left chain becomes right chain
        verify(new Integer[]{1, 2, 3, null, 4, 5}, new Integer[]{1, 3, 2, null, 5, 4});

        TreeNode chain = new TreeNode(0), cur = chain;                               // a 200000-node left chain
        for (int i = 1; i < 200_000; i++) { cur.left = new TreeNode(i); cur = cur.left; }
        TreeNode inv = iterative(chain);
        int depth = 0;
        for (TreeNode t = inv; t != null; t = t.right) { check(t.left == null && t.val == depth, "chain"); depth++; }
        check(depth == 200_000, "the iterative method handles a degenerate tree without stack overflow");
        System.out.println("OK P2783_InvertFlipBinaryTreeCreate");
    }
}
