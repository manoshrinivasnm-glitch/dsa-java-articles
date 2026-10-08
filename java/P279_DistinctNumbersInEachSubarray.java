import java.util.*;

/** TUF 279 - Distinct Numbers in Each Subarray. ans[i] = number of distinct values in nums[i..i+k-1]. */
public class P279_DistinctNumbersInEachSubarray {

    /** Approach 1: brute force, build a fresh set for every window. O(n * k) time, O(k) space. */
    static int[] bruteForce(int[] nums, int k) {
        int n = nums.length;
        if (k <= 0 || k > n) return new int[0];
        int[] ans = new int[n - k + 1];
        for (int i = 0; i + k <= n; i++) {
            Set<Integer> seen = new HashSet<>();
            for (int j = i; j < i + k; j++) seen.add(nums[j]);
            ans[i] = seen.size();
        }
        return ans;
    }

    /** Approach 2: optimal, slide the window and keep value -> count in a HashMap; distinct = map size. O(n) time, O(k) space. */
    static int[] optimal(int[] nums, int k) {
        int n = nums.length;
        if (k <= 0 || k > n) return new int[0];
        int[] ans = new int[n - k + 1];
        Map<Integer, Integer> freq = new HashMap<>();
        for (int r = 0; r < n; r++) {
            freq.merge(nums[r], 1, Integer::sum);               // nums[r] enters the window
            int l = r - k + 1;                                  // left end of the window ending at r
            if (l < 0) continue;                                // window not full yet
            ans[l] = freq.size();
            freq.computeIfPresent(nums[l], (v, c) -> c == 1 ? null : c - 1);   // nums[l] leaves; null removes the key
        }
        return ans;
    }

    /** Approach 3: optimal for a small value range, same window with a plain count array. O(n + range) time. */
    static int[] optimalCountArray(int[] nums, int k) {
        int n = nums.length;
        if (k <= 0 || k > n) return new int[0];
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int x : nums) { min = Math.min(min, x); max = Math.max(max, x); }
        if ((long) max - min + 1 > 10_000_000) return optimal(nums, k);   // range too wide for an array
        int[] count = new int[max - min + 1];                 // count[x - min] = copies of x in the window
        int[] ans = new int[n - k + 1];
        int distinct = 0;
        for (int r = 0; r < n; r++) {
            if (count[nums[r] - min]++ == 0) distinct++;        // first copy entering
            int l = r - k + 1;
            if (l < 0) continue;
            ans[l] = distinct;
            if (--count[nums[l] - min] == 0) distinct--;        // last copy leaving
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int[] expected) {
        int[][] results = {bruteForce(nums, k), optimal(nums, k), optimalCountArray(nums, k)};
        String[] names = {"bruteForce", "optimal", "optimalCountArray"};
        for (int i = 0; i < results.length; i++) {
            check(Arrays.equals(results[i], expected),
                  names[i] + " on " + Arrays.toString(nums) + ", k=" + k + " gave " + Arrays.toString(results[i]));
        }
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 2, 2, 1, 3}, 3, new int[]{3, 2, 2, 2, 3});
        verify(new int[]{1, 1, 1, 1, 2, 3, 4}, 4, new int[]{1, 2, 3, 4});
        verify(new int[]{1, 2, 1, 3, 4, 2, 3}, 4, new int[]{3, 4, 4, 3});
        verify(new int[]{5, 5, 7}, 1, new int[]{1, 1, 1});                          // k = 1: every window has one value
        verify(new int[]{4, -1, 4, -1}, 4, new int[]{2});                           // k = n: one window, negatives
        verify(new int[]{1, 2}, 3, new int[]{});                                    // k > n: no window at all
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE}, 2, new int[]{2, 2}); // huge range

        // seeded larger input: the three methods must agree
        Random rng = new Random(279);
        int[] big = new int[3000];
        for (int i = 0; i < big.length; i++) big[i] = rng.nextInt(50);
        for (int k : new int[]{1, 7, 64, 3000}) {
            int[] expected = bruteForce(big, k);
            check(Arrays.equals(optimal(big, k), expected), "optimal disagrees for k=" + k);
            check(Arrays.equals(optimalCountArray(big, k), expected), "optimalCountArray disagrees for k=" + k);
        }
        System.out.println("OK P279_DistinctNumbersInEachSubarray");
    }
}
