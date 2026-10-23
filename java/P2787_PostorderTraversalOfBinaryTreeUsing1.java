import java.util.*;

/** TUF 2787 - Post-order Traversal of Binary Tree using 1 stack. Return the node values in postorder (left, right, root) using one explicit stack. */
public class P2787_PostorderTraversalOfBinaryTreeUsing1 {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: recursion, the definition of postorder. O(n) time, O(h) call-stack space. */
    static List<Integer> recursive(TreeNode root) {
        List<Integer> out = new ArrayList<>();           // values collected in postorder
        postorder(root, out);
        return out;
    }

    static void postorder(TreeNode node, List<Integer> out) {
        if (node == null) return;                        // empty subtree: nothing to add
        postorder(node.left, out);                       // finish the whole left subtree first
        postorder(node.right, out);                      // then the whole right subtree
        out.add(node.val);                               // the node itself comes last
    }

    /** Approach 2: two stacks. The first stack produces root, right, left; the second stack reverses that into left, right, root. O(n) time, O(n) space. */
    static List<Integer> twoStacks(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;                    // empty tree: nothing to visit
        // st1 drives the walk, st2 collects root, right, left
        Deque<TreeNode> st1 = new ArrayDeque<>(), st2 = new ArrayDeque<>();
        st1.push(root);
        while (!st1.isEmpty()) {                         // each pop handles one node
            TreeNode node = st1.pop();
            st2.push(node);                              // st2 receives nodes in root, right, left order
            if (node.left != null) st1.push(node.left);  // push left before right, so right is popped first
            if (node.right != null) st1.push(node.right);
        }
        while (!st2.isEmpty()) out.add(st2.pop().val);   // popping st2 reverses it into left, right, root
        return out;
    }

    /** Approach 3: one stack. Push the left spine; when the top has no right child, pop it and keep popping while we are coming back up from a right child. O(n) time, O(h) space. */
    static List<Integer> oneStack(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> st = new ArrayDeque<>();         // path of nodes whose subtrees are not finished
        TreeNode cur = root;                             // next node to descend into (null = climb back up)
        while (cur != null || !st.isEmpty()) {           // until every node has been emitted
            if (cur != null) {                           // going down: push the node and walk left
                st.push(cur);
                cur = cur.left;
            } else {
                TreeNode temp = st.peek().right;         // left side is done: look at the top's right child
                if (temp == null) {                      // no right subtree, so the top node is finished
                    temp = st.pop();
                    out.add(temp.val);                   // emit it in postorder
                    // if the finished node was its parent's right child, the parent is finished too
                    while (!st.isEmpty() && temp == st.peek().right) {
                        temp = st.pop();
                        out.add(temp.val);
                    }
                } else {
                    cur = temp;                          // right subtree exists: walk into it next
                }
            }
        }
        return out;
    }

    /** Approach 3 (variant): one stack plus a lastVisited pointer. The top is emitted only when its right child is null or was the node emitted just before it. O(n) time, O(h) space. */
    static List<Integer> oneStackLastVisited(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        Deque<TreeNode> st = new ArrayDeque<>();         // nodes whose subtrees are not finished yet
        TreeNode cur = root, lastVisited = null;         // lastVisited = the node emitted most recently
        while (cur != null || !st.isEmpty()) {           // until every node has been emitted
            if (cur != null) {                           // going down: push the node and walk left
                st.push(cur);
                cur = cur.left;
            } else {
                TreeNode top = st.peek();                // deepest node still waiting to be emitted
                // right child exists and is not finished yet
                if (top.right != null && top.right != lastVisited) {
                    cur = top.right;                     // right subtree not done yet: go there first
                } else {
                    out.add(top.val);                    // both subtrees finished: emit the node
                    lastVisited = st.pop();                  // mark it done so its parent can be emitted next
                }
            }
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

    static void verify(Integer[] level, List<Integer> expected) {
        TreeNode root = build(level);
        String in = Arrays.toString(level);
        check(recursive(root).equals(expected), "recursive " + in);
        check(twoStacks(root).equals(expected), "twoStacks " + in);
        check(oneStack(root).equals(expected), "oneStack " + in);
        check(oneStackLastVisited(root).equals(expected), "oneStackLastVisited " + in);
    }

    public static void main(String[] args) {
        verify(new Integer[]{1, 2, 3, 4, 5, 6, 7}, List.of(4, 5, 2, 6, 7, 3, 1));            // perfect tree
        verify(new Integer[]{1, null, 2, 3}, List.of(3, 2, 1));                             // LeetCode example
        verify(new Integer[]{}, List.of());                                                  // edge: empty tree
        verify(new Integer[]{1}, List.of(1));                                                // single node
        verify(new Integer[]{1, 2, null, 3, null, 4}, List.of(4, 3, 2, 1));                // left-skewed
        verify(new Integer[]{1, null, 2, null, 3}, List.of(3, 2, 1));                       // right-skewed: one long pop chain
        verify(new Integer[]{1, 2, 3, null, 4, 5, null, null, 6}, List.of(6, 4, 2, 5, 3, 1));
        verify(new Integer[]{1, 2, 3, 4, 5, null, 6, null, null, 7, 8}, List.of(4, 7, 8, 5, 2, 6, 3, 1));

        // seeded random trees: every approach must agree with the recursive definition
        Random rnd = new Random(2787);
        for (int t = 0; t < 300; t++) {
            TreeNode root = randomTree(rnd, 8);
            List<Integer> expected = recursive(root);
            check(twoStacks(root).equals(expected), "twoStacks random #" + t);
            check(oneStack(root).equals(expected), "oneStack random #" + t);
            check(oneStackLastVisited(root).equals(expected), "oneStackLastVisited random #" + t);
        }

        // a 100000-node right chain: too deep for comfortable recursion, fine for the iterative versions
        TreeNode chain = null;
        for (int v = 100_000; v >= 1; v--) {
            TreeNode node = new TreeNode(v);
            node.right = chain;
            chain = node;
        }
        for (List<Integer> r : List.of(twoStacks(chain), oneStack(chain), oneStackLastVisited(chain))) {
            check(r.size() == 100_000 && r.get(0) == 100_000 && r.get(99_999) == 1, "deep right chain");
        }
        System.out.println("OK P2787_PostorderTraversalOfBinaryTreeUsing1");
    }
}
