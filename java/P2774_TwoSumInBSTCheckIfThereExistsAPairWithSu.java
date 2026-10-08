import java.util.*;

/** TUF 2774 - Two Sum In BST | Check if there exists a pair with Sum K. Return true if two different nodes of the BST have keys adding up to k. */
public class P2774_TwoSumInBSTCheckIfThereExistsAPairWithSu {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: hash set. Traverse once and ask at each node whether its complement was already seen. O(n) time, O(n) space. */
    static boolean withHashSet(TreeNode root, int k) {
        return findWithSet(root, k, new HashSet<>());
    }

    static boolean findWithSet(TreeNode node, int k, Set<Long> seen) {
        if (node == null) return false;
        if (seen.contains((long) k - node.val)) return true;     // long: k - val can overflow int
        seen.add((long) node.val);                               // add after the check so a node never pairs with itself
        return findWithSet(node.left, k, seen) || findWithSet(node.right, k, seen);
    }

    /** Approach 2: in-order list plus two pointers. The in-order list is sorted, so the classic two-pointer sweep applies. O(n) time, O(n) space. */
    static boolean inorderTwoPointers(TreeNode root, int k) {
        List<Integer> sorted = new ArrayList<>();
        collectInorder(root, sorted);
        int lo = 0, hi = sorted.size() - 1;
        while (lo < hi) {
            long sum = (long) sorted.get(lo) + sorted.get(hi);
            if (sum == k) return true;
            if (sum < k) lo++; else hi--;
        }
        return false;
    }

    static void collectInorder(TreeNode node, List<Integer> out) {
        if (node == null) return;
        collectInorder(node.left, out);
        out.add(node.val);
        collectInorder(node.right, out);
    }

    /** Approach 3: optimal. Two lazy BST iterators (ascending and descending) play the two pointers without building the list. O(n) time, O(h) space. */
    static boolean bstIterators(TreeNode root, int k) {
        if (root == null) return false;
        BSTIterator asc = new BSTIterator(root, false);
        BSTIterator desc = new BSTIterator(root, true);
        int lo = asc.next(), hi = desc.next();
        while (lo < hi) {                                        // distinct keys: lo < hi means two different nodes
            long sum = (long) lo + hi;
            if (sum == k) return true;
            if (sum < k) lo = asc.next(); else hi = desc.next();
        }
        return false;
    }

    /** In-order iterator over a BST using a stack of at most h nodes; reverse = true walks from the largest key down. */
    static class BSTIterator {
        private final Deque<TreeNode> stack = new ArrayDeque<>();
        private final boolean reverse;

        BSTIterator(TreeNode root, boolean reverse) {
            this.reverse = reverse;
            pushAll(root);
        }

        int next() {
            TreeNode node = stack.pop();
            pushAll(reverse ? node.left : node.right);
            return node.val;
        }

        private void pushAll(TreeNode node) {
            while (node != null) {
                stack.push(node);
                node = reverse ? node.right : node.left;
            }
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

    static void verify(TreeNode root, int k, boolean expected) {
        check(withHashSet(root, k) == expected, "withHashSet k=" + k);
        check(inorderTwoPointers(root, k) == expected, "inorderTwoPointers k=" + k);
        check(bstIterators(root, k) == expected, "bstIterators k=" + k);
    }

    public static void main(String[] args) {
        TreeNode t = build(5, 3, 6, 2, 4, null, 7);
        verify(t, 9, true);           // 2 + 7, 3 + 6, 4 + 5
        verify(t, 13, true);          // 6 + 7, the two largest keys
        verify(t, 5, true);           // 2 + 3, the two smallest keys
        verify(t, 28, false);
        verify(t, 4, false);          // smaller than any pair
        verify(t, 14, false);         // 7 + 7 would need the same node twice
        TreeNode small = build(2, 1, 3);
        verify(small, 4, true);
        verify(small, 6, false);      // 3 + 3 is not allowed
        verify(small, 2, false);
        verify(build(0, -5, 5), 0, true);                           // negative keys
        verify(build(1), 2, false);                                 // single node
        verify(build(), 0, false);                                  // empty tree
        verify(build(2_000_000_000, 1_999_999_999), -294967297, false); // naive int addition wraps to exactly this k
        System.out.println("OK P2774_TwoSumInBSTCheckIfThereExistsAPairWithSu");
    }
}
