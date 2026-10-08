import java.util.*;

/** TUF 103 - Inorder successor and predecessor in BST. Return {largest key < key, smallest key > key}, -1 when missing. */
public class P103_InorderSuccessorAndPredecessorInBST {

    static final int NONE = -1;                                  // keys are positive, so -1 never collides

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: write the inorder (sorted) sequence to a list and scan it. O(n) time, O(n) space. */
    static int[] bruteForce(TreeNode root, int key) {
        List<Integer> sorted = new ArrayList<>();
        collect(root, sorted);
        int pred = NONE, succ = NONE;
        for (int v : sorted) {
            if (v < key) pred = v;                               // last value below key wins
            else if (v > key) { succ = v; break; }               // first value above key wins
        }
        return new int[]{pred, succ};
    }

    static void collect(TreeNode node, List<Integer> out) {
        if (node == null) return;
        collect(node.left, out);
        out.add(node.val);
        collect(node.right, out);
    }

    /** Approach 2: one descent; when key is found, finish with the extremes of its two subtrees. O(h) time, O(1) space. */
    static int[] oneDescent(TreeNode root, int key) {
        int pred = NONE, succ = NONE;
        TreeNode cur = root;
        while (cur != null) {
            if (cur.val < key) {                                 // candidate predecessor, look for a larger one
                pred = cur.val;
                cur = cur.right;
            } else if (cur.val > key) {                          // candidate successor, look for a smaller one
                succ = cur.val;
                cur = cur.left;
            } else {                                             // found key: neighbours are inside its subtrees, if any
                if (cur.left != null) {
                    TreeNode t = cur.left;
                    while (t.right != null) t = t.right;         // maximum of the left subtree
                    pred = t.val;
                }
                if (cur.right != null) {
                    TreeNode t = cur.right;
                    while (t.left != null) t = t.left;           // minimum of the right subtree
                    succ = t.val;
                }
                break;
            }
        }
        return new int[]{pred, succ};
    }

    /** Approach 3: two independent descents, one per answer; never needs the "found" case. O(h) time, O(1) space. */
    static int[] twoDescents(TreeNode root, int key) {
        return new int[]{predecessor(root, key), successor(root, key)};
    }

    static int predecessor(TreeNode root, int key) {
        int best = NONE;
        for (TreeNode cur = root; cur != null; ) {
            if (cur.val < key) {
                best = cur.val;                                  // valid, and everything left of it is smaller still
                cur = cur.right;
            } else {
                cur = cur.left;                                  // too big (or equal): answer is further left
            }
        }
        return best;
    }

    static int successor(TreeNode root, int key) {
        int best = NONE;
        for (TreeNode cur = root; cur != null; ) {
            if (cur.val > key) {
                best = cur.val;
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a tree from LeetCode-style level order; null means "no node here". */
    static TreeNode build(Integer... level) {
        if (level.length == 0 || level[0] == null) return null;
        TreeNode root = new TreeNode(level[0]);
        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty() && i < level.length) {
            TreeNode cur = queue.poll();
            if (i < level.length && level[i] != null) {
                cur.left = new TreeNode(level[i]);
                queue.add(cur.left);
            }
            i++;
            if (i < level.length && level[i] != null) {
                cur.right = new TreeNode(level[i]);
                queue.add(cur.right);
            }
            i++;
        }
        return root;
    }

    /** Balanced BST holding the even numbers 2, 4, ..., 2 * count. */
    static TreeNode evens(int lo, int hi) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;
        TreeNode node = new TreeNode(2 * mid);
        node.left = evens(lo, mid - 1);
        node.right = evens(mid + 1, hi);
        return node;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(TreeNode root, int key, int pred, int succ) {
        int[] expected = {pred, succ};
        check(Arrays.equals(bruteForce(root, key), expected), "bruteForce key " + key);
        check(Arrays.equals(oneDescent(root, key), expected), "oneDescent key " + key);
        check(Arrays.equals(twoDescents(root, key), expected), "twoDescents key " + key);
    }

    public static void main(String[] args) {
        TreeNode t = build(50, 30, 70, 20, 40, 60, 80);
        verify(t, 50, 40, 60);           // example 1: root, both answers inside its subtrees
        verify(t, 40, 30, 50);           // example 2: leaf, successor is an ancestor
        verify(t, 65, 60, 70);           // key absent from the tree
        verify(t, 20, NONE, 30);         // smallest key: no predecessor
        verify(t, 80, 70, NONE);         // largest key: no successor
        verify(t, 10, NONE, 20);         // below everything
        verify(t, 90, 80, NONE);         // above everything
        verify(null, 5, NONE, NONE);     // edge case: empty tree
        verify(build(5), 5, NONE, NONE); // single node equal to key
        verify(build(5), 3, NONE, 5);
        verify(build(8, 3, null, null, 6, 4, 7), 6, 4, 7);    // neighbours deep inside the subtrees
        verify(build(8, 3, null, null, 6, 4, 7), 7, 6, 8);    // successor is an ancestor two levels up

        TreeNode big = evens(1, 100_000);                      // keys 2, 4, ..., 200000
        verify(big, 2, NONE, 4);
        verify(big, 100_000, 99_998, 100_002);
        verify(big, 77_777, 77_776, 77_778);                   // odd key: never in the tree
        verify(big, 200_000, 199_998, NONE);
        System.out.println("OK P103_InorderSuccessorAndPredecessorInBST");
    }
}
