import java.util.*;

/** TUF 546 - Insert Interval. Insert newInterval into sorted, pairwise disjoint intervals, merging where needed. */
public class P546_InsertInterval {

    /** Approach 1: append the new interval and run a general sort-and-merge. O(n log n) time, O(n) space. */
    static int[][] bruteForce(int[][] intervals, int[] newInterval) {
        int n = intervals.length;
        int[][] all = new int[n + 1][];
        for (int i = 0; i < n; i++) all[i] = intervals[i].clone();
        all[n] = newInterval.clone();
        Arrays.sort(all, (x, y) -> Integer.compare(x[0], y[0]));
        List<int[]> res = new ArrayList<>();
        for (int[] cur : all) {
            if (res.isEmpty() || cur[0] > res.get(res.size() - 1)[1]) {
                res.add(cur);                                     // gap: start a new block
            } else {
                int[] last = res.get(res.size() - 1);
                last[1] = Math.max(last[1], cur[1]);              // overlap: stretch the last block
            }
        }
        return res.toArray(new int[0][]);
    }

    /** Approach 2: one pass in three phases: copy what ends before it, absorb what overlaps it, copy the rest. O(n) time, O(n) space for the output. */
    static int[][] optimal(int[][] intervals, int[] newInterval) {
        List<int[]> res = new ArrayList<>();
        int n = intervals.length, i = 0;
        int start = newInterval[0], end = newInterval[1];
        while (i < n && intervals[i][1] < start) {                // entirely to the left: no contact
            res.add(intervals[i].clone());
            i++;
        }
        while (i < n && intervals[i][0] <= end) {                 // overlaps or touches the growing new interval
            start = Math.min(start, intervals[i][0]);
            end = Math.max(end, intervals[i][1]);
            i++;
        }
        res.add(new int[]{start, end});
        while (i < n) {                                           // entirely to the right
            res.add(intervals[i].clone());
            i++;
        }
        return res.toArray(new int[0][]);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] intervals, int[] newInterval, int[][] expected) {
        String in = Arrays.deepToString(intervals) + " + " + Arrays.toString(newInterval);
        check(Arrays.deepEquals(bruteForce(intervals, newInterval), expected),
              "bruteForce " + in + " -> " + Arrays.deepToString(bruteForce(intervals, newInterval)));
        check(Arrays.deepEquals(optimal(intervals, newInterval), expected),
              "optimal " + in + " -> " + Arrays.deepToString(optimal(intervals, newInterval)));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 3}, {6, 9}}, new int[]{2, 5}, new int[][]{{1, 5}, {6, 9}});
        verify(new int[][]{{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}}, new int[]{4, 8},
               new int[][]{{1, 2}, {3, 10}, {12, 16}});
        verify(new int[][]{{1, 2}, {8, 9}}, new int[]{4, 5}, new int[][]{{1, 2}, {4, 5}, {8, 9}});   // fits in a gap
        verify(new int[][]{{1, 5}}, new int[]{6, 8}, new int[][]{{1, 5}, {6, 8}});                  // goes at the end
        verify(new int[][]{{3, 5}}, new int[]{1, 2}, new int[][]{{1, 2}, {3, 5}});                  // goes at the front
        verify(new int[][]{{1, 2}, {5, 6}}, new int[]{2, 5}, new int[][]{{1, 6}});                  // touches both neighbours
        verify(new int[][]{{2, 3}, {4, 5}}, new int[]{1, 10}, new int[][]{{1, 10}});                // swallows everything
        verify(new int[][]{{1, 5}}, new int[]{2, 3}, new int[][]{{1, 5}});                          // already covered
        verify(new int[][]{}, new int[]{5, 7}, new int[][]{{5, 7}});                                // empty list
        System.out.println("OK P546_InsertInterval");
    }
}
