import java.util.*;

/** TUF 880 - Count all subsequences with sum K. Return how many subsequences of nums add up to exactly k. */
public class P880_CountAllSubsequencesWithSumK {

    /** Approach 1: enumerate every subsequence as a bitmask and add it up. O(2^n * n) time, O(1) extra space. */
    static long bruteForce(int[] nums, int k) {
        int n = nums.length;
        long count = 0;                                      // subsequences that sum to k
        for (int mask = 0; mask < (1 << n); mask++) {        // each mask is one subsequence
            long sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) sum += nums[i];  // bit i set: nums[i] is in it
            }
            if (sum == k) count++;                           // this subsequence hits k
        }
        return count;
    }

    /** Approach 2: pick / not-pick recursion that returns a count. O(2^n) time, O(n) recursion depth. */
    static long optimal(int[] nums, int k) {
        return countRec(nums, 0, k);  // all of nums, full target k
    }

    /** Number of subsequences of nums[i..n-1] whose sum is exactly target. */
    static long countRec(int[] nums, int i, long target) {
        if (i == nums.length) return target == 0 ? 1 : 0;     // base case: 1 if the target is used up
        long pick = countRec(nums, i + 1, target - nums[i]);  // use nums[i]: less target remains
        long notPick = countRec(nums, i + 1, target);         // skip nums[i]: same target
        return pick + notPick;                                // the two groups never overlap
    }

    /** Approach 3: the same recursion memoised on (index, remaining target). O(n * S) time and space, S = distinct targets seen. */
    static long memoized(int[] nums, int k) {
        List<Map<Long, Long>> memo = new ArrayList<>();                    // memo[i]: remaining target -> count
        for (int i = 0; i <= nums.length; i++) memo.add(new HashMap<>());  // one map per index
        return countMemo(nums, 0, k, memo);
    }

    static long countMemo(int[] nums, int i, long target, List<Map<Long, Long>> memo) {
        if (i == nums.length) return target == 0 ? 1 : 0;  // base case: same as countRec
        Long cached = memo.get(i).get(target);             // solved this (i, target) before?
        if (cached != null) return cached;                 // yes: reuse it
        // pick nums[i] plus skip nums[i], as in countRec
        long result = countMemo(nums, i + 1, target - nums[i], memo) + countMemo(nums, i + 1, target, memo);
        memo.get(i).put(target, result);                   // remember it for next time
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, long expected) {
        String label = Arrays.toString(nums) + " k=" + k;
        check(bruteForce(nums, k) == expected, "bruteForce " + label);
        check(optimal(nums, k) == expected, "optimal " + label);
        check(memoized(nums, k) == expected, "memoized " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 1}, 2, 2);                 // {1, 1} and {2}
        verify(new int[]{1, 2, 3, 4, 5}, 5, 3);           // {5}, {1, 4}, {2, 3}
        verify(new int[]{1, 2, 3}, 7, 0);                 // nothing adds up to 7
        verify(new int[]{}, 0, 1);                        // the empty subsequence has sum 0
        verify(new int[]{}, 3, 0);
        verify(new int[]{0, 0}, 0, 4);                    // zeros: all four subsequences (including the empty one) sum to 0
        verify(new int[]{-1, 1, 2, -2}, 0, 4);            // negatives: {}, {-1, 1}, {2, -2}, {-1, 1, 2, -2}
        verify(new int[]{2_000_000_000, 2_000_000_000, -294967296}, -294967296, 1); // int addition would wrap and also count {2e9, 2e9}
        int[] ones = new int[20];
        Arrays.fill(ones, 1);
        verify(ones, 10, 184756);                         // C(20, 10) ways to choose ten 1s
        System.out.println("OK P880_CountAllSubsequencesWithSumK");
    }
}
