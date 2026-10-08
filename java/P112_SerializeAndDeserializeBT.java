import java.util.*;

/** TUF 112 - Serialize and De-serialize BT. Turn a tree into a string and rebuild the identical tree from that string. */
public class P112_SerializeAndDeserializeBT {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Approach 1: level order (BFS); every missing child is written as "#". O(n) time and space for both directions. */
    static String serializeLevelOrder(TreeNode root) {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();             // LinkedList accepts null entries, ArrayDeque does not
        queue.add(root);
        while (!queue.isEmpty()) {
            TreeNode cur = queue.poll();
            if (cur == null) {
                sb.append("#,");
                continue;
            }
            sb.append(cur.val).append(',');
            queue.add(cur.left);
            queue.add(cur.right);
        }
        return sb.toString();
    }

    static TreeNode deserializeLevelOrder(String data) {
        if (data.isEmpty()) return null;
        String[] parts = data.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(parts[0]));
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int i = 1;
        while (!queue.isEmpty()) {
            TreeNode cur = queue.poll();                         // the next two tokens are its children
            if (!parts[i].equals("#")) {
                cur.left = new TreeNode(Integer.parseInt(parts[i]));
                queue.add(cur.left);
            }
            i++;
            if (!parts[i].equals("#")) {
                cur.right = new TreeNode(Integer.parseInt(parts[i]));
                queue.add(cur.right);
            }
            i++;
        }
        return root;
    }

    /** Approach 2: preorder DFS with "#" for null; the string is read back with the same recursion. O(n) time, O(n) space. */
    static String serializePreorder(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        writePreorder(root, sb);
        return sb.toString();
    }

    static void writePreorder(TreeNode node, StringBuilder sb) {
        if (sb.length() > 0) sb.append(',');
        if (node == null) {
            sb.append('#');
            return;
        }
        sb.append(node.val);
        writePreorder(node.left, sb);
        writePreorder(node.right, sb);
    }

    static TreeNode deserializePreorder(String data) {
        Iterator<String> tokens = Arrays.asList(data.split(",")).iterator();
        return readPreorder(tokens);
    }

    static TreeNode readPreorder(Iterator<String> tokens) {
        String token = tokens.next();
        if (token.equals("#")) return null;
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = readPreorder(tokens);                        // consumes exactly the left subtree's tokens
        node.right = readPreorder(tokens);
        return node;
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

    static TreeNode balanced(int lo, int hi) {
        if (lo > hi) return null;
        int mid = lo + (hi - lo) / 2;
        TreeNode node = new TreeNode(mid);
        node.left = balanced(lo, mid - 1);
        node.right = balanced(mid + 1, hi);
        return node;
    }

    static boolean same(TreeNode a, TreeNode b) {
        if (a == null || b == null) return a == b;
        return a.val == b.val && same(a.left, b.left) && same(a.right, b.right);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Both formats must rebuild the identical tree, and re-serialising the rebuilt tree must give the same string. */
    static void verify(TreeNode tree, String label) {
        String a = serializeLevelOrder(tree);
        TreeNode fromA = deserializeLevelOrder(a);
        check(same(fromA, tree), "level order round trip " + label);
        check(serializeLevelOrder(fromA).equals(a), "level order string stable " + label);
        String b = serializePreorder(tree);
        TreeNode fromB = deserializePreorder(b);
        check(same(fromB, tree), "preorder round trip " + label);
        check(serializePreorder(fromB).equals(b), "preorder string stable " + label);
    }

    public static void main(String[] args) {
        TreeNode example = build(1, 2, 3, null, null, 4, 5);
        check(serializeLevelOrder(example).equals("1,2,3,#,#,4,5,#,#,#,#,"), "level order format");
        check(serializePreorder(example).equals("1,2,#,#,3,4,#,#,5,#,#"), "preorder format");
        check(serializeLevelOrder(null).equals(""), "empty tree, level order");
        check(serializePreorder(null).equals("#"), "empty tree, preorder");

        verify(example, "example");
        verify(null, "empty tree");                                              // edge case
        verify(build(0), "single node");
        verify(build(-1, -2, -3, null, -4), "negative values");
        verify(build(Integer.MAX_VALUE, Integer.MIN_VALUE, 7), "int extremes");
        verify(build(1, 1, 1, 1, null, null, 1), "duplicate values");
        verify(build(1, 2, null, 3, null, 4, null, 5), "left chain");
        verify(build(1, null, 2, null, 3, null, 4), "right chain");
        verify(balanced(1, 5000), "balanced tree of 5000 nodes");
        System.out.println("OK P112_SerializeAndDeserializeBT");
    }
}
