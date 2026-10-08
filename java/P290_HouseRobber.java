import java.util.*;

/** TUF 290 - House Robber (the circular version, LeetCode 213 House Robber II). The first and last houses are neighbours. */
public class P290_HouseRobber {

    /** Approach 1: try every subset of houses and keep the best one with no two neighbours on the circle. O(2^n * n) time, O(1) space. */
    static long bruteForce(int[] nums) {
        int n = nums.length;
        long best = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            boolean ok = true;
            long sum = 0;
            for (int i = 0; i < n && ok; i++) {
                if ((mask >> i & 1) == 0) continue;
                int next = (i + 1) % n;                         // the neighbour on the circle (house 0 after house n-1)
                if (next != i && (mask >> next & 1) == 1) ok = false;
                sum += nums[i];
            }
            if (ok) best = Math.max(best, sum);
        }
        return best;
    }

    /** Approach 2: split the circle into two lines and solve each with memoized pick / not-pick. O(n) time, O(n) space. */
    static long memoization(int[] nums) {
        int n = nums.length;
        if (n == 0) return 0;
        if (n == 1) return nums[0];                             // a single house has no neighbour
        long[] dpA = new long[n], dpB = new long[n];
        Arrays.fill(dpA, -1);
        Arrays.fill(dpB, -1);
        long skipLast = robRange(nums, 0, n - 2, dpA);          // houses 0 .. n-2
        long skipFirst = robRange(nums, 1, n - 1, dpB);         // houses 1 .. n-1
        return Math.max(skipLast, skipFirst);
    }

    /** Best sum from the line of houses lo..i (i is the last house still allowed). */
    static long robRange(int[] nums, int lo, int i, long[] dp) {
        if (i < lo) return 0;
        if (dp[i] != -1) return dp[i];
        long pick = nums[i] + robRange(nums, lo, i - 2, dp);
        long skip = robRange(nums, lo, i - 1, dp);
        return dp[i] = Math.max(pick, skip);
    }

    /** Approach 3: the same split, each line solved with two rolling variables. O(n) time, O(1) space. */
    static long optimal(int[] nums) {
        int n = nums.length;
        if (n == 0) return 0;
        if (n == 1) return nums[0];
        return Math.max(robLine(nums, 0, n - 2), robLine(nums, 1, n - 1));
    }

    /** Linear House Robber on nums[lo..hi]. */
    static long robLine(int[] nums, int lo, int hi) {
        long prev2 = 0, prev = 0;                               // best up to i - 2 and up to i - 1
        for (int i = lo; i <= hi; i++) {
            long cur = Math.max(prev, prev2 + nums[i]);
            prev2 = prev;
            prev = cur;
        }
        return prev;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, long expected) {
        String in = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce failed for " + in);
        check(memoization(nums) == expected, "memoization failed for " + in);
        check(optimal(nums) == expected, "optimal failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 2}, 3);                          // houses 0 and 2 touch, so only one 2 can go
        verify(new int[]{1, 2, 3, 1}, 4);                       // 1 + 3
        verify(new int[]{1, 2, 3}, 3);
        verify(new int[]{1, 3, 1, 3, 100}, 103);                // 3 + 100, the first house is dropped
        verify(new int[]{200, 3, 140, 20, 10}, 340);            // 200 + 140, the last house is dropped
        verify(new int[]{2, 7, 9, 3, 1}, 11);                   // linear answer 12 (2 + 9 + 1) is illegal on a circle
        verify(new int[]{5}, 5);                                // edge: one house
        verify(new int[]{4, 9}, 9);                             // edge: two houses are neighbours both ways
        verify(new int[]{}, 0);                                 // edge: no houses

        // Cross-check against the subset brute force on seeded random circles.
        Random rnd = new Random(290);
        for (int t = 0; t < 300; t++) {
            int[] nums = new int[rnd.nextInt(14)];
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(50);
            verify(nums, bruteForce(nums));
        }
        System.out.println("OK P290_HouseRobber");
    }
}
