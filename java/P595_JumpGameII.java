import java.util.*;

/** TUF 595 - Jump Game II. Fewest jumps from index 0 to the last index (jump up to nums[i] from i); -1 if unreachable. */
public class P595_JumpGameII {

    static final int INF = Integer.MAX_VALUE;

    /** Approach 1: recursion, try every jump from every index and keep the fewest. Exponential time, O(n) stack. */
    static int bruteForce(int[] nums) {
        int best = minJumpsFrom(nums, 0);
        return best == INF ? -1 : best;
    }

    static int minJumpsFrom(int[] nums, int i) {
        if (i >= nums.length - 1) return 0;
        int best = INF;
        int far = Math.min(nums.length - 1, i + nums[i]);
        for (int j = i + 1; j <= far; j++) {
            int sub = minJumpsFrom(nums, j);
            if (sub != INF) best = Math.min(best, sub + 1);
        }
        return best;
    }

    /** Approach 2: DP from the right; dp[i] = fewest jumps from i to the last index. O(n^2) time, O(n) space. */
    static int better(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        dp[n - 1] = 0;
        for (int i = n - 2; i >= 0; i--) {
            dp[i] = INF;
            int far = Math.min(n - 1, i + nums[i]);
            for (int j = i + 1; j <= far; j++) {
                if (dp[j] != INF) dp[i] = Math.min(dp[i], dp[j] + 1);
            }
        }
        return dp[0] == INF ? -1 : dp[0];
    }

    /** Approach 3: greedy BFS by levels; [l, r] holds the indices reachable with exactly `jumps` jumps. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        int n = nums.length, jumps = 0, l = 0, r = 0;
        while (r < n - 1) {
            int farthest = r;
            for (int i = l; i <= r; i++) farthest = Math.max(farthest, i + nums[i]);
            if (farthest == r) return -1;           // this level cannot push past r: stuck
            l = r + 1;
            r = farthest;
            jumps++;
        }
        return jumps;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String in = Arrays.toString(nums);
        int b = bruteForce(nums), m = better(nums), o = optimal(nums);
        check(b == expected, "bruteForce gave " + b + ", expected " + expected + " for " + in);
        check(m == expected, "better gave " + m + ", expected " + expected + " for " + in);
        check(o == expected, "optimal gave " + o + ", expected " + expected + " for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 1, 1, 4}, 2);                          // 0 -> 1 -> 4
        verify(new int[]{2, 3, 0, 1, 4}, 2);
        verify(new int[]{1, 1, 1, 1}, 3);                             // every jump is forced
        verify(new int[]{1, 2, 1, 1, 1}, 3);                          // 0 -> 1 -> 3 -> 4
        verify(new int[]{10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1, 0}, 2);    // first jump reaches index 10, not 11
        verify(new int[]{2, 1}, 1);                                   // a jump may overshoot the end
        verify(new int[]{3, 2, 1, 0, 4}, -1);                         // unreachable
        verify(new int[]{1, 2, 3, 1, 1, 0, 2, 5}, -1);                // stuck on the 0 at index 5
        verify(new int[]{0}, 0);                                      // already at the last index
        System.out.println("OK P595_JumpGameII");
    }
}
