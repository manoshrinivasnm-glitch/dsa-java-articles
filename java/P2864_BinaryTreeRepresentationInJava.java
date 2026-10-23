import java.util.*;

/** TUF 2864 - Binary Tree Representation in Java. The node class, building trees by hand and from arrays, and converting back. */
public class P2864_BinaryTreeRepresentationInJava {

    /** One node of a binary tree: a value plus references to the left and right child (null when absent). */
    static class TreeNode {
        int val;                                  // the value stored here
        TreeNode left;                            // root of the left subtree, or null
        TreeNode right;                           // root of the right subtree, or null

        TreeNode(int val) {
            this.val = val;                       // left and right start as null: a new node is a leaf
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    /** Build the tree 1 -> (2 -> (null, 5), 3) one link at a time. */
    static TreeNode buildByHand() {
        TreeNode root = new TreeNode(1);          // the root
        root.left = new TreeNode(2);              // 2 becomes the left child of 1
        root.right = new TreeNode(3);             // 3 becomes the right child of 1
        root.left.right = new TreeNode(5);        // 2 has no left child, so its left stays null
        return root;
    }

    /** The same tree as one nested expression, using the three-argument constructor. */
    static TreeNode buildNested() {
        return new TreeNode(1,                           // root 1
                new TreeNode(2, null, new TreeNode(5)),  // left child 2, which has only a right child 5
                new TreeNode(3));                        // right child 3, a leaf
    }

    /** Build from LeetCode-style level order: values level by level, null for a missing child, no entries below a null. O(n). */
    static TreeNode fromLevelOrder(Integer... vals) {
        if (vals.length == 0 || vals[0] == null) return null;  // empty input or a null root: empty tree
        TreeNode root = new TreeNode(vals[0]);            // the first value is always the root
        Queue<TreeNode> parents = new ArrayDeque<>();     // nodes still waiting for their children
        parents.offer(root);
        int i = 1;                                        // index of the next value to hand out
        while (i < vals.length && !parents.isEmpty()) {   // until the values or the waiting parents run out
            TreeNode parent = parents.poll();             // the next parent, in level order
            if (vals[i] != null) {                        // its left slot holds a real node
                parent.left = new TreeNode(vals[i]);
                parents.offer(parent.left);               // the new child will need children of its own
            }
            i++;                                          // move past the left slot, used or null
            if (i < vals.length && vals[i] != null) {     // its right slot exists and holds a real node
                parent.right = new TreeNode(vals[i]);
                parents.offer(parent.right);
            }
            i++;                                          // move past the right slot
        }
        return root;
    }

    /** The inverse: write the tree in the same level-order format, trimming trailing nulls. O(n). */
    static List<Integer> toLevelOrder(TreeNode root) {
        List<Integer> out = new ArrayList<>();               // values in level order, null for gaps
        Queue<TreeNode> queue = new LinkedList<>();          // LinkedList, because ArrayDeque rejects null
        queue.add(root);                                     // start at the root
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                out.add(null);                               // a missing child is written, its children are not
                continue;
            }
            out.add(node.val);                               // a real node: write its value
            queue.add(node.left);                            // queue both child slots, even null ones
            queue.add(node.right);
        }
        // drop the nulls at the end; they carry no information
        while (!out.isEmpty() && out.get(out.size() - 1) == null) out.remove(out.size() - 1);
        return out;
    }

    /** Index arithmetic of the array (heap) layout: the node at index i has its children at 2i+1 and 2i+2. */
    static int leftIndex(int i) { return 2 * i + 1; }      // left child sits at 2i + 1
    static int rightIndex(int i) { return 2 * i + 2; }     // right child sits at 2i + 2
    static int parentIndex(int i) { return (i - 1) / 2; }  // parent of i, by integer division

    /** Build from the array layout, where every position exists even under a missing node. O(length of array). */
    static TreeNode fromArray(Integer[] arr) {
        return fromArray(arr, 0);  // the root lives at index 0
    }

    private static TreeNode fromArray(Integer[] arr, int i) {
        if (i >= arr.length || arr[i] == null) return null;  // past the end or an empty slot: no node
        TreeNode node = new TreeNode(arr[i]);                // the value at index i
        node.left = fromArray(arr, leftIndex(i));            // left child is at 2i + 1
        node.right = fromArray(arr, rightIndex(i));          // right child is at 2i + 2
        return node;
    }

    /** Two trees are the same when both are empty, or both roots match and so do both pairs of subtrees. O(n). */
    static boolean sameTree(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;  // if either is empty, both must be
        // same root value, and both pairs of subtrees match
        return a.val == b.val && sameTree(a.left, b.left) && sameTree(a.right, b.right);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Level order -> tree -> level order must give back the input. */
    static void roundTrip(Integer... vals) {
        List<Integer> back = toLevelOrder(fromLevelOrder(vals));
        check(back.equals(Arrays.asList(vals)), "round trip of " + Arrays.toString(vals) + " gave " + back);
    }

    public static void main(String[] args) {
        TreeNode byHand = buildByHand();
        check(byHand.val == 1 && byHand.left.val == 2 && byHand.right.val == 3, "root and children");
        check(byHand.left.left == null && byHand.left.right.val == 5, "2 has only a right child");
        check(byHand.right.left == null && byHand.right.right == null, "3 is a leaf");
        check(sameTree(byHand, buildNested()), "nested constructors build the same tree");
        check(toLevelOrder(byHand).equals(Arrays.asList(1, 2, 3, null, 5)), "level order of the hand-built tree");
        check(sameTree(byHand, fromLevelOrder(1, 2, 3, null, 5)), "level order builds the same tree");

        roundTrip(1, 2, 3, 4, 5, 6, 7);                         // perfect tree
        roundTrip(1, null, 2, null, 3);                         // right-skewed
        roundTrip(1, 2, null, 3, null, 4);                      // left-skewed
        roundTrip(3, 9, 20, null, null, 15, 7);
        roundTrip(42);                                          // single node
        roundTrip();                                            // edge: empty tree
        check(fromLevelOrder() == null && fromLevelOrder((Integer) null) == null, "empty input means no tree");

        // array layout: identical to level order for complete trees, but keeps slots under missing nodes
        check(sameTree(fromArray(new Integer[]{1, 2, 3, 4, 5, 6, 7}), fromLevelOrder(1, 2, 3, 4, 5, 6, 7)), "complete tree, both layouts");
        check(sameTree(fromArray(new Integer[]{1, null, 3, null, null, null, 7}), fromLevelOrder(1, null, 3, null, 7)), "right chain, array layout");
        check(!sameTree(fromArray(new Integer[]{1, null, 3, null, 7}), fromLevelOrder(1, null, 3, null, 7)), "the two layouts differ under a gap");
        check(fromArray(new Integer[]{}) == null && fromArray(new Integer[]{null}) == null, "empty array layout");
        check(leftIndex(0) == 1 && rightIndex(0) == 2 && leftIndex(2) == 5 && rightIndex(2) == 6, "child indices");
        check(parentIndex(1) == 0 && parentIndex(2) == 0 && parentIndex(5) == 2 && parentIndex(6) == 2, "parent indices");

        check(sameTree(null, null) && !sameTree(byHand, null) && !sameTree(null, byHand), "sameTree on empty trees");
        check(!sameTree(fromLevelOrder(1, 2), fromLevelOrder(1, null, 2)), "left child is not the same as right child");
        System.out.println("OK P2864_BinaryTreeRepresentationInJava");
    }
}
