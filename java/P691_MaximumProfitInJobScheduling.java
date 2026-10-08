import java.util.*;

/** TUF 691 - Maximum Profit in Job Scheduling. Pick non-overlapping jobs (end == next start is fine) for maximum profit. */
public class P691_MaximumProfitInJobScheduling {

    /** Jobs as {start, end, profit}, sorted by start time. */
    static int[][] sortedByStart(int[] start, int[] end, int[] profit) {
        int n = start.length;
        int[][] jobs = new int[n][];
        for (int i = 0; i < n; i++) jobs[i] = new int[]{start[i], end[i], profit[i]};
        Arrays.sort(jobs, (a, b) -> Integer.compare(a[0], b[0]));
        return jobs;
    }

    /** First index whose start time is >= time (jobs.length if none). Binary search, O(log n). */
    static int firstStartingAtOrAfter(int[][] jobs, int time) {
        int lo = 0, hi = jobs.length;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (jobs[mid][0] >= time) hi = mid; else lo = mid + 1;
        }
        return lo;
    }

    /** Approach 1: skip or take job i, scanning linearly for the next compatible job. O(2^n) time, O(n) stack. */
    static int recursion(int[] start, int[] end, int[] profit) {
        return best(0, sortedByStart(start, end, profit));
    }

    static int best(int i, int[][] jobs) {
        if (i == jobs.length) return 0;
        int skip = best(i + 1, jobs);
        int next = i + 1;
        while (next < jobs.length && jobs[next][0] < jobs[i][1]) next++;   // first job starting once job i has ended
        int take = jobs[i][2] + best(next, jobs);
        return Math.max(skip, take);
    }

    /** Approach 2: memoise best(i) and find the next job by binary search. O(n log n) time, O(n) space plus stack. */
    static int memoization(int[] start, int[] end, int[] profit) {
        int[][] jobs = sortedByStart(start, end, profit);
        int[] dp = new int[jobs.length];
        Arrays.fill(dp, -1);
        return memo(0, jobs, dp);
    }

    static int memo(int i, int[][] jobs, int[] dp) {
        if (i == jobs.length) return 0;
        if (dp[i] != -1) return dp[i];
        int skip = memo(i + 1, jobs, dp);
        int take = jobs[i][2] + memo(firstStartingAtOrAfter(jobs, jobs[i][1]), jobs, dp);
        return dp[i] = Math.max(skip, take);
    }

    /** Approach 3: the same recurrence filled from the last job backwards. O(n log n) time, O(n) space, no recursion. */
    static int tabulation(int[] start, int[] end, int[] profit) {
        int[][] jobs = sortedByStart(start, end, profit);
        int n = jobs.length;
        int[] dp = new int[n + 1];                         // dp[i]: best profit using only jobs[i..n-1]; dp[n] = 0
        for (int i = n - 1; i >= 0; i--) {
            int next = firstStartingAtOrAfter(jobs, jobs[i][1]);
            dp[i] = Math.max(dp[i + 1], jobs[i][2] + dp[next]);
        }
        return dp[0];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] s, int[] e, int[] p, int expected) {
        check(recursion(s, e, p) == expected, "recursion " + Arrays.toString(s));
        verifyFast(s, e, p, expected);
    }

    static void verifyFast(int[] s, int[] e, int[] p, int expected) {
        check(memoization(s, e, p) == expected, "memoization n=" + s.length);
        check(tabulation(s, e, p) == expected, "tabulation n=" + s.length);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 3}, new int[]{3, 4, 5, 6}, new int[]{50, 10, 40, 70}, 120);
        verify(new int[]{1, 2, 3, 4, 6}, new int[]{3, 5, 10, 6, 9}, new int[]{20, 20, 100, 70, 60}, 150);
        verify(new int[]{1, 1, 1}, new int[]{2, 3, 4}, new int[]{5, 6, 4}, 6);          // all overlap: best single job
        verify(new int[]{1}, new int[]{2}, new int[]{5}, 5);                             // single job
        verify(new int[]{3, 1}, new int[]{5, 3}, new int[]{10, 20}, 30);                 // unsorted input, touching jobs
        verify(new int[]{1, 2, 4}, new int[]{10, 3, 5}, new int[]{100, 30, 30}, 100);    // one long job beats two short ones
        verify(new int[]{}, new int[]{}, new int[]{}, 0);                                // no jobs
        int n = 2000;
        int[] s = new int[n], e = new int[n], p = new int[n];
        for (int i = 0; i < n; i++) { s[i] = n - i; e[i] = n - i + 1; p[i] = 1; }       // a chain of touching unit jobs
        verifyFast(s, e, p, n);
        int big = 50_000;
        int[] bs = new int[big], be = new int[big], bp = new int[big];
        for (int i = 0; i < big; i++) { bs[i] = i; be[i] = i + 2; bp[i] = 10_000; }     // each job overlaps its neighbours
        check(tabulation(bs, be, bp) == 25_000 * 10_000, "tabulation large");          // take every other job
        System.out.println("OK P691_MaximumProfitInJobScheduling");
    }
}
