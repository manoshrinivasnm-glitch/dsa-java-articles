import java.util.*;

/** TUF 710 - Meeting Rooms II. Given meetings [start, end), return the minimum number of rooms needed to hold them all. */
public class P710_MeetingRoomsII {

    /** Approach 1: the busiest moment is always some meeting's start; count the meetings running at every start. O(n^2) time, O(1) space. */
    static int bruteForce(int[][] intervals) {
        int best = 0;
        for (int[] m : intervals) {
            int t = m[0], running = 0;
            for (int[] other : intervals) {
                if (other[0] <= t && t < other[1]) running++;   // other is in progress at time t
            }
            best = Math.max(best, running);
        }
        return best;
    }

    /** Approach 2: sort by start and keep a min-heap of end times, one entry per room in use. O(n log n) time, O(n) space. */
    static int minHeap(int[][] intervals) {
        int[][] a = intervals.clone();
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        PriorityQueue<Integer> ends = new PriorityQueue<>();      // end time of the meeting in each occupied room
        for (int[] m : a) {
            if (!ends.isEmpty() && ends.peek() <= m[0]) ends.poll(); // the room that frees up first is free: reuse it
            ends.add(m[1]);
        }
        return ends.size();                                        // the heap never shrinks, so this is its peak size
    }

    /** Approach 3: chronological sweep. Sort starts and ends separately and merge them as events. O(n log n) time, O(n) space. */
    static int sweep(int[][] intervals) {
        int n = intervals.length;
        int[] starts = new int[n], ends = new int[n];
        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }
        Arrays.sort(starts);
        Arrays.sort(ends);
        int inUse = 0, best = 0, j = 0;
        for (int i = 0; i < n; i++) {
            while (ends[j] <= starts[i]) {   // meetings that ended by now give their rooms back (ends win ties)
                inUse--;
                j++;
            }
            inUse++;                         // the meeting starting at starts[i] takes a room
            best = Math.max(best, inUse);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] intervals, int expected) {
        String s = Arrays.deepToString(intervals);
        check(bruteForce(intervals) == expected, "bruteForce " + s);
        check(minHeap(intervals) == expected, "minHeap " + s);
        check(sweep(intervals) == expected, "sweep " + s);
        check(Arrays.deepToString(intervals).equals(s), "input was modified " + s);
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 30}, {5, 10}, {15, 20}}, 2);
        verify(new int[][]{{7, 10}, {2, 4}}, 1);
        verify(new int[][]{}, 0);                                          // edge: no meetings, no rooms
        verify(new int[][]{{2, 7}}, 1);                                    // edge: one meeting
        verify(new int[][]{{1, 5}, {5, 10}}, 1);                           // back to back: one room is enough
        verify(new int[][]{{1, 10}, {2, 9}, {3, 8}, {4, 7}}, 4);           // all nested
        verify(new int[][]{{9, 10}, {4, 9}, {4, 17}}, 2);
        verify(new int[][]{{1, 4}, {1, 4}, {1, 4}}, 3);                    // identical meetings
        verify(new int[][]{{1, 3}, {2, 4}, {3, 5}, {4, 6}}, 2);            // a chain of overlaps
        Random rnd = new Random(710);
        for (int k = 0; k < 2000; k++) {
            int n = rnd.nextInt(10);
            int[][] m = new int[n][];
            for (int i = 0; i < n; i++) {
                int s = rnd.nextInt(30);
                m[i] = new int[]{s, s + 1 + rnd.nextInt(10)};
            }
            int expected = bruteForce(m);
            check(minHeap(m) == expected && sweep(m) == expected, "random " + Arrays.deepToString(m));
        }
        System.out.println("OK P710_MeetingRoomsII");
    }
}
