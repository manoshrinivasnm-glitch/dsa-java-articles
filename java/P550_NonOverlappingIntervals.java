import java.util.*;

/** TUF 550 - Non-overlapping Intervals. Minimum removals so the rest do not overlap; [1,2] and [2,3] do not overlap. */
public class P550_NonOverlappingIntervals {

    /** Approach 1: try every subset and keep the largest pairwise non-overlapping one. O(2^n * n log n) time, O(n) space. */
    static int bruteForce(int[][] intervals) {
        int n = intervals.length;
        int bestKept = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            List<int[]> chosen = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) chosen.add(intervals[i]);
            }
            chosen.sort((x, y) -> Integer.compare(x[0], y[0]));
            boolean ok = true;
            for (int k = 0; k + 1 < chosen.size(); k++) {
                if (chosen.get(k)[1] > chosen.get(k + 1)[0]) {    // the next one starts before this one ends
                    ok = false;
                    break;
                }
            }
            if (ok) bestKept = Math.max(bestKept, chosen.size());
        }
        return n - bestKept;
    }

    /** Approach 2: sort by start, then the longest chain of compatible intervals by DP (like LIS). O(n^2) time, O(n) space. */
    static int better(int[][] intervals) {
        int n = intervals.length;
        if (n == 0) return 0;
        int[][] a = intervals.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        int[] dp = new int[n];                                    // dp[i] = most intervals we can keep ending with a[i]
        int bestKept = 0;
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (a[j][1] <= a[i][0]) dp[i] = Math.max(dp[i], dp[j] + 1);
            }
            bestKept = Math.max(bestKept, dp[i]);
        }
        return n - bestKept;
    }

    /** Approach 3: greedy by earliest end; keep an interval whenever it starts at or after the last kept end. O(n log n) time, O(n) space. */
    static int optimal(int[][] intervals) {
        int n = intervals.length;
        if (n == 0) return 0;
        int[][] a = intervals.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[1], y[1]));   // compare, never subtract: x[1] - y[1] can overflow
        int kept = 1, lastEnd = a[0][1];
        for (int i = 1; i < n; i++) {
            if (a[i][0] >= lastEnd) {                             // touching is allowed
                kept++;
                lastEnd = a[i][1];
            }
        }
        return n - kept;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] intervals, int expected) {
        String in = Arrays.deepToString(intervals);
        check(bruteForce(intervals) == expected, "bruteForce " + in + " -> " + bruteForce(intervals));
        check(better(intervals) == expected, "better " + in + " -> " + better(intervals));
        check(optimal(intervals) == expected, "optimal " + in + " -> " + optimal(intervals));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 3}}, 1);
        verify(new int[][]{{1, 2}, {1, 2}, {1, 2}}, 2);                       // duplicates: keep one
        verify(new int[][]{{1, 2}, {2, 3}}, 0);                               // touching is not overlapping
        verify(new int[][]{{1, 100}, {11, 22}, {1, 11}, {2, 12}}, 2);         // one long interval blocks many
        verify(new int[][]{{0, 2}, {1, 3}, {2, 4}, {3, 5}, {4, 6}}, 2);
        verify(new int[][]{{-2147483648, -2147483000}, {-2147483000, 2147483000}, {0, 2147483647}, {-10, 0}}, 1); // a subtracting comparator overflows here
        verify(new int[][]{{1, 2}}, 0);                                       // single interval
        verify(new int[][]{}, 0);                                             // empty input
        System.out.println("OK P550_NonOverlappingIntervals");
    }
}
