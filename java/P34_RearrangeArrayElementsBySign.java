import java.util.*;

/** TUF 34 - Rearrange array elements by sign. Alternate positives and negatives, positive first, keeping the relative order of each kind. */
public class P34_RearrangeArrayElementsBySign {

    /** Approach 1: split into a positive list and a negative list, then interleave them. O(n) time, O(n) extra space for the two lists. */
    static int[] bruteForce(int[] nums) {
        int n = nums.length;
        List<Integer> pos = new ArrayList<>();       // positives, in their original order
        List<Integer> neg = new ArrayList<>();       // negatives, in their original order
        for (int x : nums) {
            if (x > 0) pos.add(x); else neg.add(x);  // sort each element into its bucket
        }
        int[] ans = new int[n];
        for (int i = 0; i < n / 2; i++) {            // pair i: i-th positive, then i-th negative
            ans[2 * i] = pos.get(i);                 // positives go to even indices
            ans[2 * i + 1] = neg.get(i);             // negatives go to odd indices
        }
        return ans;
    }

    /** Approach 2: one pass, dropping each element at the next free even (positive) or odd (negative) slot. O(n) time, O(n) for the answer only. */
    static int[] optimal(int[] nums) {
        int n = nums.length;
        int[] ans = new int[n];
        int posIdx = 0, negIdx = 1;                  // next free even slot and next free odd slot
        for (int x : nums) {
            if (x > 0) {                             // positive: place it at the next even index
                ans[posIdx] = x;
                posIdx += 2;                         // skip the odd slot reserved for a negative
            } else {                                 // negative: place it at the next odd index
                ans[negIdx] = x;
                negIdx += 2;                         // skip the even slot reserved for a positive
            }
        }
        return ans;
    }

    /** Variant: the two counts may differ. Alternate while both kinds remain, then append the leftovers in their original order. O(n) time, O(n) space. */
    static int[] unequalCounts(int[] nums) {
        int n = nums.length;
        List<Integer> pos = new ArrayList<>();       // positives, in their original order
        List<Integer> neg = new ArrayList<>();       // negatives, in their original order
        for (int x : nums) {
            if (x > 0) pos.add(x); else neg.add(x);  // sort each element into its bucket
        }
        int[] ans = new int[n];
        int k = Math.min(pos.size(), neg.size());   // number of complete (positive, negative) pairs
        for (int i = 0; i < k; i++) {
            ans[2 * i] = pos.get(i);                 // positive at the even index of pair i
            ans[2 * i + 1] = neg.get(i);             // negative right after it
        }
        int idx = 2 * k;                             // first slot after the alternating part
        for (int i = k; i < pos.size(); i++) ans[idx++] = pos.get(i);   // leftover positives, if any
        for (int i = k; i < neg.size(); i++) ans[idx++] = neg.get(i);   // leftover negatives, if any
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Equal counts: all three methods must agree with the expected array. */
    static void verifyEqualCounts(int[] nums, int[] expected) {
        check(Arrays.equals(bruteForce(nums.clone()), expected), "bruteForce " + Arrays.toString(nums));
        check(Arrays.equals(optimal(nums.clone()), expected), "optimal " + Arrays.toString(nums));
        check(Arrays.equals(unequalCounts(nums.clone()), expected), "unequalCounts " + Arrays.toString(nums));
    }

    public static void main(String[] args) {
        verifyEqualCounts(new int[]{3, 1, -2, -5, 2, -4}, new int[]{3, -2, 1, -5, 2, -4});
        verifyEqualCounts(new int[]{-1, 1}, new int[]{1, -1});
        verifyEqualCounts(new int[]{1, 2, 3, -1, -2, -3}, new int[]{1, -1, 2, -2, 3, -3});
        verifyEqualCounts(new int[]{-5, -6, 7, 8}, new int[]{7, -5, 8, -6});
        verifyEqualCounts(new int[]{}, new int[]{});                                            // empty
        verifyEqualCounts(new int[]{2_000_000_000, -2_000_000_000}, new int[]{2_000_000_000, -2_000_000_000});
        // unequal counts: only the variant applies
        check(Arrays.equals(unequalCounts(new int[]{1, 2, -4, -5, 3, 4, 5}), new int[]{1, -4, 2, -5, 3, 4, 5}), "unequal: more positives");
        check(Arrays.equals(unequalCounts(new int[]{-1, -2, -3, 4}), new int[]{4, -1, -2, -3}), "unequal: more negatives");
        check(Arrays.equals(unequalCounts(new int[]{-7, -8}), new int[]{-7, -8}), "unequal: no positives");
        check(Arrays.equals(unequalCounts(new int[]{9}), new int[]{9}), "unequal: single positive");
        System.out.println("OK P34_RearrangeArrayElementsBySign");
    }
}
