import java.util.*;

/** TUF 2772 - Merge 2 BST's. Return every key of both BSTs in one ascending list (keys present in both trees appear twice). */
public class P2772_Merge2BSTs {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: brute force. Dump the keys of both trees into one list and sort it. O((m + n) log(m + n)) time, O(m + n) space. */
    static List<Integer> bruteForce(TreeNode root1, TreeNode root2) {
        List<Integer> all = new ArrayList<>();
        collectInorder(root1, all);
        collectInorder(root2, all);
        Collections.sort(all);
        return all;
    }

    static void collectInorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        collectInorder(node.left, out);
        out.add(node.val);
        collectInorder(node.right, out);
    }

    /** Approach 2: better. In-order gives each tree's keys already sorted; merge the two sorted lists. O(m + n) time, O(m + n) space. */
    static List<Integer> better(TreeNode root1, TreeNode root2) {
        List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
        collectInorder(root1, a);
        collectInorder(root2, b);
        List<Integer> out = new ArrayList<>(a.size() + b.size());
        int i = 0, j = 0;
        while (i < a.size() && j < b.size()) {
            if (a.get(i) <= b.get(j)) out.add(a.get(i++));
            else out.add(b.get(j++));
        }
        while (i < a.size()) out.add(a.get(i++));
        while (j < b.size()) out.add(b.get(j++));
        return out;
    }

    /** Approach 3: optimal. Run two iterative in-order traversals side by side and always emit the smaller stack top. O(m + n) time, O(h1 + h2) extra space. */
    static List<Integer> optimal(TreeNode root1, TreeNode root2) {
        Deque<TreeNode> s1 = new ArrayDeque<>(), s2 = new ArrayDeque<>();
        pushLeft(s1, root1);
        pushLeft(s2, root2);
        List<Integer> out = new ArrayList<>();
        while (!s1.isEmpty() || !s2.isEmpty()) {
            Deque<TreeNode> from;
            if (s2.isEmpty() || (!s1.isEmpty() && s1.peek().val <= s2.peek().val)) from = s1;
            else from = s2;
            TreeNode node = from.pop();
            out.add(node.val);
            pushLeft(from, node.right);              // the next key of that tree is the leftmost node of node.right
        }
        return out;
    }

    static void pushLeft(Deque<TreeNode> stack, TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
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

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode root1, TreeNode root2, List<Integer> expected) {
        String in = "expected " + expected;
        check(bruteForce(root1, root2).equals(expected), "bruteForce " + in);
        check(better(root1, root2).equals(expected), "better " + in);
        check(optimal(root1, root2).equals(expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(build(2, 1, 4), build(1, 0, 3), List.of(0, 1, 1, 2, 3, 4));
        verify(build(0, -10, 10), build(5, 1, 7, 0, 2), List.of(-10, 0, 0, 1, 2, 5, 7, 10));
        verify(build(3, 1, 5), build(4, 2, 6), List.of(1, 2, 3, 4, 5, 6));                 // keys interleave
        verify(build(5, 4, null, 3), build(1, null, 2, null, 6), List.of(1, 2, 3, 4, 5, 6)); // skewed trees
        verify(build(), build(5, 1, 7, 0, 2), List.of(0, 1, 2, 5, 7));                     // first tree empty
        verify(build(1, null, 8), build(), List.of(1, 8));                                  // second tree empty
        verify(build(), build(), List.of());                                                // both empty
        System.out.println("OK P2772_Merge2BSTs");
    }
}
