import java.util.*;

/** TUF 715 - Merge Overlapping Subintervals. Merge every group of overlapping closed intervals into one. */
public class P715_MergeOverlappingSubintervals {

    /** Approach 1: sort, then for each interval not yet covered scan forward and swallow everything that overlaps. O(n log n + n^2) time, O(n) space. */
    static int[][] bruteForce(int[][] intervals) {
        int n = intervals.length;
        int[][] a = sortedCopy(intervals);
        List<int[]> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!res.isEmpty() && a[i][1] <= res.get(res.size() - 1)[1]) continue;   // already inside the last merged interval
            int start = a[i][0], end = a[i][1];
            for (int j = i + 1; j < n; j++) {
                if (a[j][0] <= end) end = Math.max(end, a[j][1]);
                else break;                                                          // sorted by start: nothing later can overlap
            }
            res.add(new int[]{start, end});
        }
        return res.toArray(new int[0][]);
    }

    /** Approach 2: sort, then one pass that either extends the last merged interval or starts a new one. O(n log n) time, O(n) space. */
    static int[][] optimal(int[][] intervals) {
        int[][] a = sortedCopy(intervals);
        List<int[]> res = new ArrayList<>();
        for (int[] cur : a) {
            if (res.isEmpty() || cur[0] > res.get(res.size() - 1)[1]) {
                res.add(new int[]{cur[0], cur[1]});                  // gap before cur: start a new interval
            } else {
                int[] last = res.get(res.size() - 1);
                last[1] = Math.max(last[1], cur[1]);                 // overlap: extend the last interval
            }
        }
        return res.toArray(new int[0][]);
    }

    /** Copy of the input sorted by start, then by end, so the caller's array is untouched. */
    static int[][] sortedCopy(int[][] intervals) {
        int[][] a = new int[intervals.length][];
        for (int i = 0; i < intervals.length; i++) a[i] = intervals[i].clone();
        Arrays.sort(a, (x, y) -> x[0] != y[0] ? Integer.compare(x[0], y[0]) : Integer.compare(x[1], y[1]));
        return a;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] intervals, int[][] expected) {
        String in = Arrays.deepToString(intervals);
        check(Arrays.deepEquals(bruteForce(intervals), expected), "bruteForce " + in + " -> " + Arrays.deepToString(bruteForce(intervals)));
        check(Arrays.deepEquals(optimal(intervals), expected), "optimal " + in + " -> " + Arrays.deepToString(optimal(intervals)));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}}, new int[][]{{1, 6}, {8, 10}, {15, 18}});
        verify(new int[][]{{1, 4}, {4, 5}}, new int[][]{{1, 5}});                         // touching endpoints merge
        verify(new int[][]{{1, 4}, {2, 3}}, new int[][]{{1, 4}});                         // containment
        verify(new int[][]{{5, 7}, {1, 3}, {2, 4}, {6, 8}}, new int[][]{{1, 4}, {5, 8}}); // unsorted input
        verify(new int[][]{{1, 2}}, new int[][]{{1, 2}});                                 // single interval
        verify(new int[][]{}, new int[][]{});                                             // empty input
        verify(new int[][]{{1, 10}, {2, 3}, {4, 5}, {6, 7}}, new int[][]{{1, 10}});       // one interval covers all
        verify(new int[][]{{1, 2}, {3, 4}}, new int[][]{{1, 2}, {3, 4}});                 // no overlap at all
        verify(new int[][]{{1, 4}, {0, 4}}, new int[][]{{0, 4}});
        verify(new int[][]{{1, 4}, {0, 0}}, new int[][]{{0, 0}, {1, 4}});
        System.out.println("OK P715_MergeOverlappingSubintervals");
    }
}
