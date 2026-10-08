import java.util.*;

/** TUF 633 - Longest Bitonic Subsequence. Longest subsequence that strictly increases, then strictly decreases. */
public class P633_LongestBitonicSubsequence {

    /** Approach 1: check every subsequence. O(2^n * n) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length, best = 0;
        for (int mask = 1; mask < (1 << n); mask++) {
            int len = 0, prev = 0;
            boolean falling = false, ok = true;
            for (int i = 0; i < n && ok; i++) {
                if ((mask & (1 << i)) == 0) continue;
                if (len > 0) {
                    if (nums[i] == prev) ok = false;                // equal neighbours are never allowed
                    else if (nums[i] < prev) falling = true;        // we are past the peak
                    else if (falling) ok = false;                   // a rise after the peak
                }
                prev = nums[i];
                len++;
            }
            if (ok) best = Math.max(best, len);
        }
        return best;
    }

    /** Approach 2: LIS ending at i from the left plus LIS ending at i from the right. O(n^2) time, O(n) space. */
    static int better(int[] nums) {
        int n = nums.length, best = 0;
        int[] inc = new int[n], dec = new int[n];
        for (int i = 0; i < n; i++) {
            inc[i] = 1;
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) inc[i] = Math.max(inc[i], inc[j] + 1);
            }
        }
        for (int i = n - 1; i >= 0; i--) {
            dec[i] = 1;
            for (int j = n - 1; j > i; j--) {
                if (nums[j] < nums[i]) dec[i] = Math.max(dec[i], dec[j] + 1);
            }
        }
        for (int i = 0; i < n; i++) best = Math.max(best, inc[i] + dec[i] - 1);  // nums[i] is the peak, counted once
        return best;
    }

    /** Approach 3: the same two arrays, each built in O(n log n) with binary search. O(n log n) time, O(n) space. */
    static int optimal(int[] nums) {
        int n = nums.length, best = 0;
        int[] inc = lisEndingAt(nums, false), dec = lisEndingAt(nums, true);
        for (int i = 0; i < n; i++) best = Math.max(best, inc[i] + dec[i] - 1);
        return best;
    }

    /** len[i] = longest strictly increasing subsequence ending at i, scanning right to left when fromRight. */
    static int[] lisEndingAt(int[] nums, boolean fromRight) {
        int n = nums.length, size = 0;
        int[] tails = new int[n], len = new int[n];             // tails[k] = smallest tail of a run of length k + 1
        for (int step = 0; step < n; step++) {
            int i = fromRight ? n - 1 - step : step;
            int lo = 0, hi = size;                              // find the first tail >= nums[i]
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (tails[mid] < nums[i]) lo = mid + 1; else hi = mid;
            }
            tails[lo] = nums[i];
            if (lo == size) size++;
            len[i] = lo + 1;                                    // lo tails are smaller than nums[i]
        }
        return len;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String in = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce failed for " + in);
        check(better(nums) == expected, "better failed for " + in);
        check(optimal(nums) == expected, "optimal failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 11, 2, 10, 4, 5, 2, 1}, 6);         // 1 2 10 4 2 1
        verify(new int[]{1, 2, 5, 3, 2}, 5);                    // the whole array
        verify(new int[]{12, 11, 40, 5, 3, 1}, 5);              // 12 40 5 3 1
        verify(new int[]{80, 60, 30, 40, 20, 10}, 5);           // purely decreasing: 80 60 30 20 10
        verify(new int[]{1, 2, 3, 4}, 4);                       // purely increasing counts too
        verify(new int[]{2, 2, 2}, 1);                          // equal values cannot be chained
        verify(new int[]{-5, 0, -1, 7, -3}, 4);                 // negatives: -5 0 7 -3
        verify(new int[]{5}, 1);                                // edge: single element
        verify(new int[]{}, 0);                                 // edge: empty array

        Random rnd = new Random(633);
        for (int t = 0; t < 300; t++) {
            int[] nums = new int[rnd.nextInt(13)];
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(9);
            verify(nums, bruteForce(nums));
        }
        System.out.println("OK P633_LongestBitonicSubsequence");
    }
}
