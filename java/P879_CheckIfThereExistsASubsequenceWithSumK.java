import java.util.*;

/** TUF 879 - Check if there exists a subsequence with sum K. Return true if some subsequence of nums adds up to exactly k. */
public class P879_CheckIfThereExistsASubsequenceWithSumK {

    /** Approach 1: enumerate every subsequence as a bitmask and compare its sum with k. O(2^n * n) time, O(1) extra space. */
    static boolean bruteForce(int[] nums, int k) {
        int n = nums.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            long sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) sum += nums[i];
            }
            if (sum == k) return true;
        }
        return false;
    }

    /** Approach 2: pick / not-pick recursion that stops as soon as one branch succeeds. O(2^n) worst case, O(n) depth. */
    static boolean optimal(int[] nums, int k) {
        return existsRec(nums, 0, k);
    }

    /** True if some subsequence of nums[i..n-1] sums to target. */
    static boolean existsRec(int[] nums, int i, long target) {
        if (i == nums.length) return target == 0;
        if (existsRec(nums, i + 1, target - nums[i])) return true;   // pick nums[i]; if that works we are done
        return existsRec(nums, i + 1, target);                        // otherwise try leaving nums[i] out
    }

    /** Approach 3: memoise (index, remaining target) so every repeated state is solved once. O(n * S) time and space. */
    static boolean memoized(int[] nums, int k) {
        List<Map<Long, Boolean>> memo = new ArrayList<>();
        for (int i = 0; i <= nums.length; i++) memo.add(new HashMap<>());
        return existsMemo(nums, 0, k, memo);
    }

    static boolean existsMemo(int[] nums, int i, long target, List<Map<Long, Boolean>> memo) {
        if (i == nums.length) return target == 0;
        Boolean cached = memo.get(i).get(target);
        if (cached != null) return cached;
        boolean result = existsMemo(nums, i + 1, target - nums[i], memo) || existsMemo(nums, i + 1, target, memo);
        memo.get(i).put(target, result);
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, boolean expected) {
        String label = Arrays.toString(nums) + " k=" + k;
        check(bruteForce(nums, k) == expected, "bruteForce " + label);
        check(optimal(nums, k) == expected, "optimal " + label);
        check(memoized(nums, k) == expected, "memoized " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, 5, true);              // 2 + 3
        verify(new int[]{1, 2, 3}, 7, false);             // 1 + 2 + 3 = 6 is the most we can make
        verify(new int[]{}, 0, true);                     // the empty subsequence has sum 0
        verify(new int[]{}, 1, false);
        verify(new int[]{-5, 3, 8}, 3, true);             // {3} or {-5, 8}
        verify(new int[]{-5, 3, 8}, -2, true);            // {-5, 3}
        verify(new int[]{-5, 3, 8}, 4, false);
        verify(new int[]{10, 20, 15}, 36, false);
        verify(new int[]{2_000_000_000, 2_000_000_000}, -294967296, false); // int addition would wrap to exactly this k
        int[] twos = new int[20];
        Arrays.fill(twos, 2);
        verify(twos, 7, false);                           // odd target, even elements: the whole tree is explored
        verify(twos, 40, true);                           // take everything
        verify(twos, 0, true);                            // take nothing
        System.out.println("OK P879_CheckIfThereExistsASubsequenceWithSumK");
    }
}
