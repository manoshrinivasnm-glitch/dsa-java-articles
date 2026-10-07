import java.util.*;

/** TUF 34 - Rearrange array elements by sign. Alternate positives and negatives, positive first, keeping the relative order of each kind. */
public class P34_RearrangeArrayElementsBySign {

    /** Approach 1: split into a positive list and a negative list, then interleave them. O(n) time, O(n) extra space for the two lists. */
    static int[] bruteForce(int[] nums) {
        int n = nums.length;
        List<Integer> pos = new ArrayList<>();
        List<Integer> neg = new ArrayList<>();
        for (int x : nums) {
            if (x > 0) pos.add(x); else neg.add(x);
        }
        int[] ans = new int[n];
        for (int i = 0; i < n / 2; i++) {
            ans[2 * i] = pos.get(i);                 // positives go to even indices
            ans[2 * i + 1] = neg.get(i);             // negatives go to odd indices
        }
        return ans;
    }

    /** Approach 2: one pass, dropping each element at the next free even (positive) or odd (negative) slot. O(n) time, O(n) for the answer only. */
    static int[] optimal(int[] nums) {
        int n = nums.length;
        int[] ans = new int[n];
        int posIdx = 0, negIdx = 1;
        for (int x : nums) {
            if (x > 0) {
                ans[posIdx] = x;
                posIdx += 2;
            } else {
                ans[negIdx] = x;
                negIdx += 2;
            }
        }
        return ans;
    }

    /** Variant: the two counts may differ. Alternate while both kinds remain, then append the leftovers in their original order. O(n) time, O(n) space. */
    static int[] unequalCounts(int[] nums) {
        int n = nums.length;
        List<Integer> pos = new ArrayList<>();
        List<Integer> neg = new ArrayList<>();
        for (int x : nums) {
            if (x > 0) pos.add(x); else neg.add(x);
        }
        int[] ans = new int[n];
        int k = Math.min(pos.size(), neg.size());   // number of complete (positive, negative) pairs
        for (int i = 0; i < k; i++) {
            ans[2 * i] = pos.get(i);
            ans[2 * i + 1] = neg.get(i);
        }
        int idx = 2 * k;
        for (int i = k; i < pos.size(); i++) ans[idx++] = pos.get(i);
        for (int i = k; i < neg.size(); i++) ans[idx++] = neg.get(i);
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
