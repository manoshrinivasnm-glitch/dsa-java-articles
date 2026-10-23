import java.util.*;

/** TUF 96 - BST iterator (LeetCode 173). An iterator over a BST's keys in ascending order with next() and hasNext(). */
public class P96_BSTIterator {

    static class TreeNode {
        int val;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    /** Common contract so every implementation can be driven by the same test script. */
    interface InorderIterator {
        boolean hasNext();                               // is there a key we have not returned yet?
        int next();                                      // the smallest key not returned yet
    }

    /** Approach 1: flatten the whole in-order sequence up front. O(n) constructor, O(1) next and hasNext, O(n) space. */
    static class FlattenedIterator implements InorderIterator {
        private final List<Integer> keys = new ArrayList<>();   // every key, in ascending order
        private int pos = 0;                             // index of the next key to hand out

        FlattenedIterator(TreeNode root) {
            fill(root);                                  // do all the work up front
        }

        private void fill(TreeNode node) {
            if (node == null) return;                    // empty subtree adds nothing
            fill(node.left);                             // smaller keys first
            keys.add(node.val);                          // then this key
            fill(node.right);                            // then the larger keys
        }

        public boolean hasNext() {
            return pos < keys.size();                    // some keys are still unread
        }

        public int next() {
            if (!hasNext()) throw new NoSuchElementException("no more keys");
            return keys.get(pos++);                      // hand out this key and advance
        }
    }

    /** Approach 2: optimal. A stack holds the path of ancestors still waiting to be visited. Amortised O(1) next, O(1) hasNext, O(h) space. */
    static class StackIterator implements InorderIterator {
        private final Deque<TreeNode> stack = new ArrayDeque<>();   // nodes not yet returned; top = next key

        StackIterator(TreeNode root) {
            pushLeftChain(root);                         // the smallest key is at the end of the left path
        }

        private void pushLeftChain(TreeNode node) {
            while (node != null) {                       // the smallest unvisited key ends up on top
                stack.push(node);                        // returned after all of its left subtree
                node = node.left;
            }
        }

        public boolean hasNext() {
            return !stack.isEmpty();                     // an empty stack means every key was returned
        }

        public int next() {
            if (stack.isEmpty()) throw new NoSuchElementException("no more keys");
            TreeNode node = stack.pop();                 // the smallest key not returned yet
            pushLeftChain(node.right);                   // the successor is the leftmost node of the right subtree
            return node.val;
        }
    }

    /** Approach 3: Morris threading. Amortised O(1) next, O(1) extra space, but the tree is temporarily modified until iteration ends. */
    static class MorrisIterator implements InorderIterator {
        private TreeNode cur;                            // the next key comes from cur or from its left subtree

        MorrisIterator(TreeNode root) {
            cur = root;                                  // start the walk at the root
        }

        public boolean hasNext() {
            return cur != null;                          // the walk ends when cur runs off the tree
        }

        public int next() {
            while (cur != null) {                        // walk until the next key is found
                if (cur.left == null) {                  // nothing smaller is pending: cur is next
                    int val = cur.val;
                    cur = cur.right;                     // a real right child or a thread to the successor
                    return val;
                }
                TreeNode pred = cur.left;                // in-order predecessor: rightmost node of the left subtree
                // go right until the last node, or until the thread we left earlier
                while (pred.right != null && pred.right != cur) pred = pred.right;
                if (pred.right == null) {
                    pred.right = cur;                    // first arrival: thread back, then go left
                    cur = cur.left;
                } else {
                    pred.right = null;                   // second arrival: left subtree is done, restore the tree
                    int val = cur.val;                   // cur comes right after its left subtree
                    cur = cur.right;                     // resume from the larger keys next time
                    return val;
                }
            }
            // cur ran off the tree: every key has been returned
            throw new NoSuchElementException("no more keys");
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Build a tree from LeetCode-style level order, where null marks a missing child. */
    static TreeNode fromLevelOrder(Integer... a) {
        if (a.length == 0 || a[0] == null) return null;
        TreeNode root = new TreeNode(a[0]);
        Queue<TreeNode> q = new ArrayDeque<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < a.length) {
            TreeNode cur = q.poll();
            if (a[i] != null) { cur.left = new TreeNode(a[i]); q.add(cur.left); }
            i++;
            if (i < a.length && a[i] != null) { cur.right = new TreeNode(a[i]); q.add(cur.right); }
            i++;
        }
        return root;
    }

    static TreeNode insert(TreeNode root, int key) {
        if (root == null) return new TreeNode(key);
        if (key < root.val) root.left = insert(root.left, key);
        else if (key > root.val) root.right = insert(root.right, key);
        return root;
    }

    /** Pre-order with null markers: two trees are identical exactly when their shapes match. */
    static String shape(TreeNode node) {
        return node == null ? "#" : node.val + "(" + shape(node.left) + "," + shape(node.right) + ")";
    }

    /** Runs a script of "next" / "hasNext" calls and records each result as a string. */
    static List<String> simulate(InorderIterator it, String[] ops) {
        List<String> out = new ArrayList<>();
        for (String op : ops) {
            switch (op) {
                case "next" -> out.add(String.valueOf(it.next()));
                case "hasNext" -> out.add(String.valueOf(it.hasNext()));
                default -> throw new IllegalArgumentException("unknown op " + op);
            }
        }
        return out;
    }

    /** Each iterator gets its own fresh tree, so a Morris bug cannot hide behind or break the others. */
    static void verify(Integer[] level, String[] ops, List<String> expected) {
        List<InorderIterator> its = List.of(new FlattenedIterator(fromLevelOrder(level)),
                new StackIterator(fromLevelOrder(level)), new MorrisIterator(fromLevelOrder(level)));
        for (InorderIterator it : its) {
            List<String> got = simulate(it, ops);
            check(got.equals(expected), it.getClass().getSimpleName() + " on " + Arrays.toString(level)
                    + ": got " + got + ", expected " + expected);
        }
    }

    /** Drains every iterator and compares with the sorted keys; also checks Morris restores the tree. */
    static void verifyDrain(TreeNode root, List<Integer> sortedKeys) {
        String before = shape(root);
        for (InorderIterator it : List.of(new FlattenedIterator(root), new StackIterator(root), new MorrisIterator(root))) {
            List<Integer> got = new ArrayList<>();
            while (it.hasNext()) got.add(it.next());
            check(got.equals(sortedKeys), it.getClass().getSimpleName() + ": got " + got + ", expected " + sortedKeys);
            check(shape(root).equals(before), it.getClass().getSimpleName() + " left the tree modified");
        }
    }

    public static void main(String[] args) {
        // LeetCode example
        verify(new Integer[]{7, 3, 15, null, null, 9, 20},
                new String[]{"next", "next", "hasNext", "next", "hasNext", "next", "hasNext", "next", "hasNext"},
                List.of("3", "7", "true", "9", "true", "15", "true", "20", "false"));
        // repeated hasNext calls must not consume anything
        verify(new Integer[]{2, 1, 3},
                new String[]{"hasNext", "hasNext", "next", "hasNext", "next", "next", "hasNext", "hasNext"},
                List.of("true", "true", "1", "true", "2", "3", "false", "false"));
        // single node
        verify(new Integer[]{42}, new String[]{"hasNext", "next", "hasNext"}, List.of("true", "42", "false"));
        // empty tree
        verify(new Integer[]{}, new String[]{"hasNext"}, List.of("false"));
        // left-skewed and right-skewed chains
        verify(new Integer[]{4, 3, null, 2, null, 1}, new String[]{"next", "next", "next", "next", "hasNext"},
                List.of("1", "2", "3", "4", "false"));
        verify(new Integer[]{1, null, 2, null, 3}, new String[]{"next", "hasNext", "next", "next", "hasNext"},
                List.of("1", "true", "2", "3", "false"));
        // negative keys
        verify(new Integer[]{0, -5, 5, -8, -2}, new String[]{"next", "next", "next", "next", "next", "hasNext"},
                List.of("-8", "-5", "-2", "0", "5", "false"));

        // next() past the end must throw, never return garbage
        for (InorderIterator it : List.of(new FlattenedIterator(null), new StackIterator(null), new MorrisIterator(null))) {
            boolean threw = false;
            try { it.next(); } catch (NoSuchElementException e) { threw = true; }
            check(threw, it.getClass().getSimpleName() + " should throw on an exhausted iterator");
        }

        // full drains on seeded random BSTs
        Random rnd = new Random(96);
        for (int trial = 0; trial < 300; trial++) {
            TreeNode root = null;
            TreeSet<Integer> keys = new TreeSet<>();
            int n = rnd.nextInt(40);
            for (int i = 0; i < n; i++) {
                int key = rnd.nextInt(1000) - 500;
                keys.add(key);
                root = insert(root, key);
            }
            verifyDrain(root, new ArrayList<>(keys));
        }
        System.out.println("OK P96_BSTIterator");
    }
}
