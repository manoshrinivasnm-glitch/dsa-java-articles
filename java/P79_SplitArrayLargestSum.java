import java.util.*;

/** TUF 79 - Split array - largest sum. Cut nums into k non-empty contiguous pieces so that the largest piece sum is as small as possible; -1 if k > n. */
public class P79_SplitArrayLargestSum {

    /** Pieces needed when no piece may sum to more than limit, cutting greedily from the left. O(n). */
    static int piecesNeeded(int[] nums, int limit) {
        int pieces = 1;
        long sum = 0;
        for (int x : nums) {
            if (sum + x > limit) { pieces++; sum = 0; }
            sum += x;
        }
        return pieces;
    }

    /** Approach 1: try every limit from the largest element up to the total sum. O(n * (sum - max)) time, O(1) space. */
    static int bruteForce(int[] nums, int k) {
        int n = nums.length;
        if (k > n) return -1;
        int max = 0, sum = 0;
        for (int x : nums) { max = Math.max(max, x); sum += x; }
        for (int limit = max; limit <= sum; limit++) {
            if (piecesNeeded(nums, limit) <= k) return limit;
        }
        return sum;
    }

    /** Approach 2: dynamic programming. dp[i][j] = minimal largest sum when the first i elements form j pieces. O(k * n^2) time, O(k * n) space. */
    static int better(int[] nums, int k) {
        int n = nums.length;
        if (k > n) return -1;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
        long[][] dp = new long[n + 1][k + 1];
        for (long[] row : dp) Arrays.fill(row, Long.MAX_VALUE);
        dp[0][0] = 0;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= Math.min(i, k); j++) {
                for (int p = j - 1; p < i; p++) {               // last piece is nums[p .. i-1]
                    if (dp[p][j - 1] == Long.MAX_VALUE) continue;
                    long candidate = Math.max(dp[p][j - 1], prefix[i] - prefix[p]);
                    dp[i][j] = Math.min(dp[i][j], candidate);
                }
            }
        }
        return (int) dp[n][k];
    }

    /** Approach 3: binary search on the limit in [max, sum]; piecesNeeded only falls as the limit grows. O(n log(sum)) time, O(1) space. */
    static int optimal(int[] nums, int k) {
        int n = nums.length;
        if (k > n) return -1;
        int max = 0, sum = 0;
        for (int x : nums) { max = Math.max(max, x); sum += x; }
        int lo = max, hi = sum, ans = sum;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (piecesNeeded(nums, mid) <= k) { ans = mid; hi = mid - 1; }
            else lo = mid + 1;
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int expected) {
        String in = Arrays.toString(nums) + " k=" + k;
        check(bruteForce(nums, k) == expected, "bruteForce " + in);
        check(better(nums, k) == expected, "better " + in);
        check(optimal(nums, k) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{7, 2, 5, 10, 8}, 2, 18);
        verify(new int[]{1, 2, 3, 4, 5}, 2, 9);
        verify(new int[]{1, 4, 4}, 3, 4);
        verify(new int[]{5, 1, 5, 1, 5}, 3, 6);
        verify(new int[]{2, 3, 1, 2, 4, 3}, 1, 15);          // one piece: the answer is the total
        verify(new int[]{1, 1, 1, 1}, 4, 1);                 // one element per piece: the answer is the maximum
        verify(new int[]{10}, 1, 10);                        // single element
        verify(new int[]{1, 2, 3}, 4, -1);                   // more pieces than elements
        verify(new int[]{0, 0, 0, 5}, 2, 5);                 // zeros never force a cut
        System.out.println("OK P79_SplitArrayLargestSum");
    }
}
