import java.util.*;

/** TUF 709 - Meeting Rooms. Given meetings [start, end), decide whether one person can attend all of them (no two overlap). */
public class P709_MeetingRooms {

    /** Approach 1: compare every pair of meetings. O(n^2) time, O(1) space. */
    static boolean bruteForce(int[][] intervals) {
        int n = intervals.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int latestStart = Math.max(intervals[i][0], intervals[j][0]);
                int earliestEnd = Math.min(intervals[i][1], intervals[j][1]);
                if (latestStart < earliestEnd) return false;   // both meetings run during [latestStart, earliestEnd)
            }
        }
        return true;
    }

    /** Approach 2: sort by start time; a clash, if any, shows up between two neighbours. O(n log n) time, O(n) space. */
    static boolean optimal(int[][] intervals) {
        int[][] a = intervals.clone();                         // sort a copy, keep the caller's order
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        for (int i = 1; i < a.length; i++) {
            if (a[i][0] < a[i - 1][1]) return false;           // starts before the previous meeting has ended
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] intervals, boolean expected) {
        String s = Arrays.deepToString(intervals);
        check(bruteForce(intervals) == expected, "bruteForce " + s);
        check(optimal(intervals) == expected, "optimal " + s);
        check(Arrays.deepToString(intervals).equals(s), "input was modified " + s);
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 30}, {5, 10}, {15, 20}}, false);
        verify(new int[][]{{7, 10}, {2, 4}}, true);
        verify(new int[][]{}, true);                                   // edge: no meetings
        verify(new int[][]{{5, 8}}, true);                             // edge: one meeting
        verify(new int[][]{{1, 5}, {5, 10}}, true);                    // touching end and start do not clash
        verify(new int[][]{{1, 10}, {2, 3}}, false);                   // nested meeting
        verify(new int[][]{{3, 4}, {1, 2}, {2, 3}, {4, 5}}, true);     // unsorted, back to back
        verify(new int[][]{{1, 3}, {8, 9}, {2, 6}}, false);            // clash hidden by the input order
        verify(new int[][]{{0, 1_000_000_000}, {999_999_999, 1_000_000_000}}, false);
        Random rnd = new Random(709);
        for (int k = 0; k < 2000; k++) {
            int n = rnd.nextInt(6);
            int[][] m = new int[n][];
            for (int i = 0; i < n; i++) {
                int s = rnd.nextInt(40);
                m[i] = new int[]{s, s + 1 + rnd.nextInt(8)};
            }
            check(bruteForce(m) == optimal(m), "random " + Arrays.deepToString(m));
        }
        System.out.println("OK P709_MeetingRooms");
    }
}
