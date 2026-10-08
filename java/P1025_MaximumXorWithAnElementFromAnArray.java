import java.util.*;

/** TUF 1025 - Maximum Xor with an element from an array (LeetCode 1707). For each query {x, m} return the largest
 *  x ^ nums[j] over nums[j] <= m, or -1 if every element exceeds m. All values are non-negative ints. */
public class P1025_MaximumXorWithAnElementFromAnArray {

    static final int HIGH_BIT = 30;                        // values are non-negative, so bit 31 is always 0

    /** Approach 1: for each query scan the whole array. O(n * q) time, O(1) extra space. */
    static int[] bruteForce(int[] nums, int[][] queries) {
        int[] ans = new int[queries.length];
        for (int q = 0; q < queries.length; q++) {
            int x = queries[q][0], m = queries[q][1], best = -1;
            for (int v : nums) {
                if (v <= m) best = Math.max(best, v ^ x);  // v ^ x >= 0, so -1 survives only if nothing qualifies
            }
            ans[q] = best;
        }
        return ans;
    }

    static class Node {
        final Node[] child = new Node[2];
    }

    static void insert(Node root, int v) {
        Node cur = root;
        for (int b = HIGH_BIT; b >= 0; b--) {
            int bit = (v >> b) & 1;
            if (cur.child[bit] == null) cur.child[bit] = new Node();
            cur = cur.child[bit];
        }
    }

    /** Largest x ^ y over all y in the (non-empty) trie: prefer the opposite bit at every level, highest first. */
    static int maxXorWith(Node root, int x) {
        Node cur = root;
        int result = 0;
        for (int b = HIGH_BIT; b >= 0; b--) {
            int want = 1 - ((x >> b) & 1);
            if (cur.child[want] != null) {
                result |= 1 << b;
                cur = cur.child[want];
            } else {
                cur = cur.child[1 - want];
            }
        }
        return result;
    }

    /** Approach 2: answer queries in increasing order of m, inserting each number once it becomes allowed.
     *  O(n log n + q log q + 31 * (n + q)) time, O(31 * n) nodes. */
    static int[] optimalOffline(int[] nums, int[][] queries) {
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        Integer[] order = new Integer[queries.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(queries[a][1], queries[b][1]));   // by limit m
        Node root = new Node();
        int[] ans = new int[queries.length];
        int inserted = 0;                                  // sorted[0 .. inserted - 1] are in the trie
        for (int q : order) {
            int x = queries[q][0], m = queries[q][1];
            while (inserted < sorted.length && sorted[inserted] <= m) insert(root, sorted[inserted++]);
            ans[q] = inserted == 0 ? -1 : maxXorWith(root, x);
        }
        return ans;
    }

    static class MinNode {
        final MinNode[] child = new MinNode[2];
        int min = Integer.MAX_VALUE;                       // smallest number stored in this subtree
    }

    /** Approach 3: one trie whose nodes remember their subtree minimum; a branch is usable only if min <= m.
     *  Answers queries online in the given order. O(31 * (n + q)) time, O(31 * n) nodes. */
    static int[] optimalOnline(int[] nums, int[][] queries) {
        MinNode root = new MinNode();
        for (int v : nums) {
            MinNode cur = root;
            cur.min = Math.min(cur.min, v);
            for (int b = HIGH_BIT; b >= 0; b--) {
                int bit = (v >> b) & 1;
                if (cur.child[bit] == null) cur.child[bit] = new MinNode();
                cur = cur.child[bit];
                cur.min = Math.min(cur.min, v);
            }
        }
        int[] ans = new int[queries.length];
        for (int q = 0; q < queries.length; q++) {
            int x = queries[q][0], m = queries[q][1];
            if (nums.length == 0 || root.min > m) {        // every number is above the limit
                ans[q] = -1;
                continue;
            }
            MinNode cur = root;                            // invariant: cur.min <= m
            int result = 0;
            for (int b = HIGH_BIT; b >= 0; b--) {
                int want = 1 - ((x >> b) & 1);
                MinNode preferred = cur.child[want];
                if (preferred != null && preferred.min <= m) {
                    result |= 1 << b;
                    cur = preferred;
                } else {
                    cur = cur.child[1 - want];             // must hold the number that gave cur.min <= m
                }
            }
            ans[q] = result;
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int[][] queries, int[] expected) {
        int[][] got = {bruteForce(nums, queries), optimalOffline(nums, queries), optimalOnline(nums, queries)};
        String[] names = {"bruteForce", "optimalOffline", "optimalOnline"};
        for (int k = 0; k < 3; k++) {
            check(Arrays.equals(got[k], expected), names[k] + " on " + Arrays.toString(nums) + " got "
                  + Arrays.toString(got[k]) + " expected " + Arrays.toString(expected));
        }
    }

    public static void main(String[] args) {
        verify(new int[]{0, 1, 2, 3, 4}, new int[][]{{3, 1}, {1, 3}, {5, 6}}, new int[]{3, 3, 7});
        verify(new int[]{5, 2, 4, 6, 6, 3}, new int[][]{{12, 4}, {8, 1}, {6, 3}}, new int[]{15, -1, 5});
        verify(new int[]{7}, new int[][]{{7, 7}, {7, 6}, {0, 100}}, new int[]{0, -1, 7});          // single element
        verify(new int[]{1_000_000_000}, new int[][]{{0, 999_999_999}, {0, 1_000_000_000}},
               new int[]{-1, 1_000_000_000});                                                       // limit just below / at the value
        verify(new int[]{4, 9, 1}, new int[][]{}, new int[]{});                                     // edge: no queries
        verify(new int[]{}, new int[][]{{5, Integer.MAX_VALUE}}, new int[]{-1});                    // edge: no numbers at all
        verify(new int[]{0, Integer.MAX_VALUE}, new int[][]{{Integer.MAX_VALUE, Integer.MAX_VALUE}, {Integer.MAX_VALUE, 0}},
               new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE});
        // deterministic random cases: all three must agree
        Random rnd = new Random(1025);
        for (int t = 0; t < 300; t++) {
            int bound = t % 2 == 0 ? 50 : 1_000_000_001;
            int[] nums = new int[rnd.nextInt(30)];
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(bound);
            int[][] queries = new int[rnd.nextInt(30)][];
            for (int i = 0; i < queries.length; i++) queries[i] = new int[]{rnd.nextInt(bound), rnd.nextInt(bound)};
            verify(nums, queries, bruteForce(nums, queries));
        }
        System.out.println("OK P1025_MaximumXorWithAnElementFromAnArray");
    }
}
