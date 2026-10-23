import java.util.*;

/** TUF 1024 - Maximum XOR of two numbers in an array (LeetCode 421). Return max(nums[i] ^ nums[j]) over all i, j
 *  for non-negative ints (0 for an array with fewer than two elements). */
public class P1024_MaximumXOROfTwoNumbersInAnArray {

    static final int HIGH_BIT = 30;                        // inputs are non-negative, so bit 31 is always 0

    /** Approach 1: try every pair. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int best = 0;                                      // largest XOR seen over all pairs
        for (int i = 0; i < nums.length; i++) {            // first number of the pair
            for (int j = i + 1; j < nums.length; j++) {    // every later number as the second
                best = Math.max(best, nums[i] ^ nums[j]);   // keep the larger XOR
            }
        }
        return best;
    }

    static class Node {
        final Node[] child = new Node[2];                  // child[0] for bit 0, child[1] for bit 1
    }

    static void insert(Node root, int x) {
        Node cur = root;
        for (int b = HIGH_BIT; b >= 0; b--) {              // one level per bit, highest bit first
            int bit = (x >> b) & 1;                        // bit b of x picks the branch
            if (cur.child[bit] == null) cur.child[bit] = new Node();   // create the branch only if missing
            cur = cur.child[bit];
        }
    }

    /** Largest x ^ y over all y in the (non-empty) trie: prefer the opposite bit at every level, highest first. */
    static int maxXorWith(Node root, int x) {
        Node cur = root;
        int result = 0;                                    // the XOR built so far, bit by bit
        for (int b = HIGH_BIT; b >= 0; b--) {              // highest bit first: it outweighs all lower ones
            int want = 1 - ((x >> b) & 1);                 // the bit that would make this bit of the XOR 1
            if (cur.child[want] != null) {                 // some stored number has the opposite bit
                result |= 1 << b;                          // so bit b of the XOR is 1
                cur = cur.child[want];
            } else {
                cur = cur.child[1 - want];                 // forced: bit b of the XOR stays 0
            }
        }
        return result;
    }

    /** Approach 2: binary trie of all numbers, then a greedy walk for each number. O(31 * n) time and O(31 * n) nodes. */
    static int optimalTrie(int[] nums) {
        if (nums.length == 0) return 0;                    // empty array: nothing to XOR
        Node root = new Node();
        for (int x : nums) insert(root, x);                // store every number in the trie
        int best = 0;
        for (int x : nums) best = Math.max(best, maxXorWith(root, x));   // best partner for each x, found greedily
        return best;
    }

    /** Approach 3: decide the answer bit by bit with a hash set of high-bit prefixes. O(31 * n) time, O(n) space. */
    static int optimalPrefixSet(int[] nums) {
        int answer = 0, mask = 0;                          // answer: best bits fixed so far; mask: bits looked at
        for (int b = HIGH_BIT; b >= 0; b--) {
            mask |= 1 << b;                                // keep bits HIGH_BIT..b of every number
            Set<Integer> prefixes = new HashSet<>();
            for (int x : nums) prefixes.add(x & mask);     // every number cut down to its top bits
            int candidate = answer | (1 << b);             // can bit b of the answer be 1 as well?
            for (int p : prefixes) {                       // try each prefix as one side of the pair
                if (prefixes.contains(p ^ candidate)) {    // p ^ q == candidate  <=>  q == p ^ candidate
                    answer = candidate;                    // two prefixes reach it: keep bit b as 1
                    break;
                }
            }
        }
        return answer;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        int[] got = {bruteForce(nums), optimalTrie(nums), optimalPrefixSet(nums)};
        String[] names = {"bruteForce", "optimalTrie", "optimalPrefixSet"};
        for (int k = 0; k < 3; k++) {
            check(got[k] == expected, names[k] + " on " + Arrays.toString(nums) + " got " + got[k] + " expected " + expected);
        }
    }

    public static void main(String[] args) {
        verify(new int[]{3, 10, 5, 25, 2, 8}, 28);                                  // 5 ^ 25
        verify(new int[]{14, 70, 53, 83, 49, 91, 36, 80, 92, 51, 66, 70}, 127);
        verify(new int[]{1, 2}, 3);
        verify(new int[]{8, 8, 8}, 0);                                              // all equal
        verify(new int[]{0}, 0);                                                    // edge: single element
        verify(new int[]{}, 0);                                                     // edge: empty array
        verify(new int[]{Integer.MAX_VALUE, 0}, Integer.MAX_VALUE);                 // edge: all 31 bits differ
        verify(new int[]{Integer.MAX_VALUE, 1 << 30, (1 << 30) - 1}, Integer.MAX_VALUE);
        // deterministic random arrays: all three must agree
        Random rnd = new Random(1024);
        for (int t = 0; t < 300; t++) {
            int[] nums = new int[rnd.nextInt(40)];
            int bound = t % 2 == 0 ? 64 : Integer.MAX_VALUE;
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(bound);
            verify(nums, bruteForce(nums));
        }
        System.out.println("OK P1024_MaximumXOROfTwoNumbersInAnArray");
    }
}
