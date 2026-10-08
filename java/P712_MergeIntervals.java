import java.util.*;

/** TUF 712 - Merge Intervals. Merge all overlapping closed intervals and return them sorted by start. */
public class P712_MergeIntervals {

    /** Approach 1: no sorting; repeatedly find any overlapping pair and fuse it until none is left. O(n^3) time, O(n) space. */
    static int[][] bruteForce(int[][] intervals) {
        List<int[]> list = new ArrayList<>();
        for (int[] iv : intervals) list.add(iv.clone());
        boolean merged = true;
        while (merged) {
            merged = false;
            search:
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    int[] a = list.get(i), b = list.get(j);
                    if (a[0] <= b[1] && b[0] <= a[1]) {           // closed intervals share at least one point
                        a[0] = Math.min(a[0], b[0]);
                        a[1] = Math.max(a[1], b[1]);
                        list.remove(j);
                        merged = true;
                        break search;
                    }
                }
            }
        }
        list.sort((x, y) -> Integer.compare(x[0], y[0]));
        return list.toArray(new int[0][]);
    }

    /** Approach 2: sort by start, then one pass that stretches the last block or opens a new one. O(n log n) time, O(n) space. */
    static int[][] optimal(int[][] intervals) {
        int[][] a = new int[intervals.length][];
        for (int i = 0; i < a.length; i++) a[i] = intervals[i].clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        List<int[]> res = new ArrayList<>();
        for (int[] cur : a) {
            int[] last = res.isEmpty() ? null : res.get(res.size() - 1);
            if (last == null || cur[0] > last[1]) {
                res.add(cur);                                     // gap: start a new block
            } else {
                last[1] = Math.max(last[1], cur[1]);              // overlap: stretch the current block
            }
        }
        return res.toArray(new int[0][]);
    }

    /** Approach 3: sort starts and ends separately; a block closes wherever the next start lies beyond the current end. O(n log n) time, O(n) space. */
    static int[][] optimalSeparateSort(int[][] intervals) {
        int n = intervals.length;
        int[] starts = new int[n], ends = new int[n];
        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }
        Arrays.sort(starts);
        Arrays.sort(ends);
        List<int[]> res = new ArrayList<>();
        int blockStart = 0;                                       // index in starts of the current block's first interval
        for (int i = 0; i < n; i++) {
            if (i == n - 1 || starts[i + 1] > ends[i]) {          // the first i+1 intervals all end before the next starts
                res.add(new int[]{starts[blockStart], ends[i]});
                blockStart = i + 1;
            }
        }
        return res.toArray(new int[0][]);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] intervals, int[][] expected) {
        String in = Arrays.deepToString(intervals);
        check(Arrays.deepEquals(bruteForce(intervals), expected), "bruteForce " + in + " -> " + Arrays.deepToString(bruteForce(intervals)));
        check(Arrays.deepEquals(optimal(intervals), expected), "optimal " + in + " -> " + Arrays.deepToString(optimal(intervals)));
        check(Arrays.deepEquals(optimalSeparateSort(intervals), expected),
              "optimalSeparateSort " + in + " -> " + Arrays.deepToString(optimalSeparateSort(intervals)));
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}}, new int[][]{{1, 6}, {8, 10}, {15, 18}});
        verify(new int[][]{{1, 4}, {4, 5}}, new int[][]{{1, 5}});                              // touching endpoints merge
        verify(new int[][]{{5, 7}, {1, 3}, {2, 4}, {6, 8}}, new int[][]{{1, 4}, {5, 8}});      // unsorted input
        verify(new int[][]{{1, 3}, {5, 6}, {2, 5}}, new int[][]{{1, 6}});                      // a later interval bridges two
        verify(new int[][]{{1, 10}, {2, 3}, {4, 5}, {11, 12}}, new int[][]{{1, 10}, {11, 12}}); // containment
        verify(new int[][]{{1, 4}, {0, 0}}, new int[][]{{0, 0}, {1, 4}});
        verify(new int[][]{{1, 2}}, new int[][]{{1, 2}});                                      // single interval
        verify(new int[][]{}, new int[][]{});                                                  // empty input
        System.out.println("OK P712_MergeIntervals");
    }
}
