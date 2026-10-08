import java.util.*;

/**
 * TUF 111 - Requirements needed to construct a unique BT (concept).
 * With distinct values, two traversals identify exactly one binary tree iff one of them is inorder and the other is not.
 */
public class P111_RequirementsNeededToConstructAUniqueBT {

    static final int PRE = 1, IN = 2, POST = 3, LEVEL = 4;

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** The rule. Codes: 1 = preorder, 2 = inorder, 3 = postorder, 4 = level order. O(1). */
    static boolean canConstructUnique(int a, int b) {
        return a != b && (a == IN || b == IN);
    }

    /** One traversal of the tree as a list of values. */
    static List<Integer> traversal(TreeNode root, int type) {
        List<Integer> out = new ArrayList<>();
        if (type == LEVEL) {
            Deque<TreeNode> queue = new ArrayDeque<>();
            if (root != null) queue.add(root);
            while (!queue.isEmpty()) {
                TreeNode cur = queue.poll();
                out.add(cur.val);
                if (cur.left != null) queue.add(cur.left);
                if (cur.right != null) queue.add(cur.right);
            }
        } else {
            depthFirst(root, type, out);
        }
        return out;
    }

    static void depthFirst(TreeNode node, int type, List<Integer> out) {
        if (node == null) return;
        if (type == PRE) out.add(node.val);
        depthFirst(node.left, type, out);
        if (type == IN) out.add(node.val);
        depthFirst(node.right, type, out);
        if (type == POST) out.add(node.val);
    }

    /** Why inorder + level order is enough: level[0] is the root, inorder splits the rest. O(n^2) time. */
    static TreeNode buildFromLevelAndInorder(List<Integer> level, List<Integer> inorder) {
        if (inorder.isEmpty()) return null;
        int rootVal = level.get(0);
        int split = inorder.indexOf(rootVal);                      // everything before split is in the left subtree
        Set<Integer> leftValues = new HashSet<>(inorder.subList(0, split));
        List<Integer> leftLevel = new ArrayList<>(), rightLevel = new ArrayList<>();
        for (int i = 1; i < level.size(); i++) {
            int v = level.get(i);
            if (leftValues.contains(v)) leftLevel.add(v); else rightLevel.add(v);
        }
        TreeNode root = new TreeNode(rootVal);
        root.left = buildFromLevelAndInorder(leftLevel, inorder.subList(0, split));
        root.right = buildFromLevelAndInorder(rightLevel, inorder.subList(split + 1, inorder.size()));
        return root;
    }

    /** Every binary tree shape with n nodes, each exactly once (there are Catalan(n) of them). */
    static List<TreeNode> allShapes(int n) {
        List<TreeNode> out = new ArrayList<>();
        if (n == 0) {
            out.add(null);
            return out;
        }
        for (int leftSize = 0; leftSize < n; leftSize++) {
            for (TreeNode left : allShapes(leftSize)) {
                for (TreeNode right : allShapes(n - 1 - leftSize)) {
                    TreeNode root = new TreeNode(0);
                    root.left = left;
                    root.right = right;
                    out.add(root);
                }
            }
        }
        return out;
    }

    /** Copies a shape, writing perm[0], perm[1], ... into its nodes in preorder. */
    static TreeNode labelled(TreeNode shape, int[] perm, int[] next) {
        if (shape == null) return null;
        TreeNode node = new TreeNode(perm[next[0]++]);
        node.left = labelled(shape.left, perm, next);
        node.right = labelled(shape.right, perm, next);
        return node;
    }

    static void permutations(int[] a, int k, List<int[]> out) {
        if (k == a.length) {
            out.add(a.clone());
            return;
        }
        for (int i = k; i < a.length; i++) {
            int t = a[k]; a[k] = a[i]; a[i] = t;
            permutations(a, k + 1, out);
            t = a[k]; a[k] = a[i]; a[i] = t;
        }
    }

    static boolean isFull(TreeNode node) {
        if (node == null) return true;
        if ((node.left == null) != (node.right == null)) return false;
        return isFull(node.left) && isFull(node.right);
    }

    /** Over all trees holding the values 1..n, the most distinct trees that share one pair of traversals. 1 means "always unique". */
    static int maxTreesSharingTraversals(int n, int a, int b, boolean fullTreesOnly) {
        int[] values = new int[n];
        for (int i = 0; i < n; i++) values[i] = i + 1;
        List<int[]> perms = new ArrayList<>();
        permutations(values, 0, perms);
        Map<List<List<Integer>>, Integer> groups = new HashMap<>();
        int best = 0;
        for (TreeNode shape : allShapes(n)) {
            if (fullTreesOnly && !isFull(shape)) continue;
            for (int[] perm : perms) {
                TreeNode tree = labelled(shape, perm, new int[]{0});
                List<List<Integer>> key = List.of(traversal(tree, a), traversal(tree, b));
                best = Math.max(best, groups.merge(key, 1, Integer::sum));
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- helpers
    static boolean same(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && same(a.left, b.left) && same(a.right, b.right);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // the rule itself
        check(canConstructUnique(PRE, IN), "pre + in");
        check(canConstructUnique(IN, POST), "in + post");
        check(canConstructUnique(LEVEL, IN), "level + in");
        check(!canConstructUnique(PRE, POST), "pre + post");
        check(!canConstructUnique(PRE, LEVEL), "pre + level");
        check(!canConstructUnique(POST, LEVEL), "post + level");
        check(!canConstructUnique(IN, IN), "in + in");                  // edge case: the same traversal twice

        // smallest counterexample: root 1 with child 2 on the left versus on the right
        TreeNode leftChild = new TreeNode(1), rightChild = new TreeNode(1);
        leftChild.left = new TreeNode(2);
        rightChild.right = new TreeNode(2);
        for (int type : new int[]{PRE, POST, LEVEL}) {
            check(traversal(leftChild, type).equals(traversal(rightChild, type)), "same traversal, type " + type);
        }
        check(traversal(leftChild, PRE).equals(List.of(1, 2)), "preorder of both");
        check(traversal(leftChild, POST).equals(List.of(2, 1)), "postorder of both");
        check(traversal(leftChild, IN).equals(List.of(2, 1)), "inorder, child on the left");
        check(traversal(rightChild, IN).equals(List.of(1, 2)), "inorder, child on the right");

        // the rule agrees with an exhaustive search over every tree with up to 5 nodes
        int[] catalan = {1, 1, 2, 5, 14, 42};
        for (int n = 1; n <= 5; n++) {
            check(allShapes(n).size() == catalan[n], "Catalan count, n = " + n);
            for (int a = PRE; a <= LEVEL; a++) {
                for (int b = PRE; b <= LEVEL; b++) {
                    int worst = maxTreesSharingTraversals(n, a, b, false);
                    if (canConstructUnique(a, b)) check(worst == 1, "unique pair " + a + "," + b + ", n = " + n);
                    else if (n >= 2) check(worst > 1, "ambiguous pair " + a + "," + b + ", n = " + n);
                }
                check(maxTreesSharingTraversals(n, a, a, false) == catalan[n], "one traversal alone, n = " + n);
            }
            check(maxTreesSharingTraversals(n, PRE, POST, false) == 1 << (n - 1), "pre + post worst case is a chain, n = " + n);
        }
        // pre + post does identify the tree once every node has 0 or 2 children
        for (int n = 1; n <= 5; n += 2) check(maxTreesSharingTraversals(n, PRE, POST, true) == 1, "full trees, n = " + n);

        // level order + inorder rebuilds a hand-made example ...
        TreeNode example = buildFromLevelAndInorder(List.of(1, 2, 3, 4, 5), List.of(4, 2, 5, 1, 3));
        check(traversal(example, PRE).equals(List.of(1, 2, 4, 5, 3)), "level + in example");
        // ... and every tree with up to 5 nodes, including the empty tree
        for (int n = 0; n <= 5; n++) {
            int[] values = new int[n];
            for (int i = 0; i < n; i++) values[i] = i + 1;
            List<int[]> perms = new ArrayList<>();
            permutations(values, 0, perms);
            for (TreeNode shape : allShapes(n)) {
                for (int[] perm : perms) {
                    TreeNode tree = labelled(shape, perm, new int[]{0});
                    TreeNode rebuilt = buildFromLevelAndInorder(traversal(tree, LEVEL), traversal(tree, IN));
                    check(same(tree, rebuilt), "level + in round trip, n = " + n);
                }
            }
        }
        System.out.println("OK P111_RequirementsNeededToConstructAUniqueBT");
    }
}
