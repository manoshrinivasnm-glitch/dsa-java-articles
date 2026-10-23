import java.util.*;

/** TUF 78 - Minimize Max Distance to Gas Station. Add k new stations to a sorted line of stations so that the largest distance between neighbours is as small as possible. */
public class P78_MinimizeMaxDistanceToGasStation {

    /** Approach 1: k times, drop one station into the gap whose current sections are the longest. O(k * n) time, O(n) space. */
    static double bruteForce(int[] stations, int k) {
        int n = stations.length;
        if (n < 2) return 0;                             // fewer than two stations: no gap to shrink
        int[] placed = new int[n - 1];                   // new stations already dropped into gap i
        for (int t = 0; t < k; t++) {                    // place the k new stations one at a time
            int best = 0;                                // gap whose sections are currently longest
            double bestLen = -1;
            for (int i = 0; i < n - 1; i++) {            // scan every gap
                // section length if gap i is split evenly
                double len = (double) (stations[i + 1] - stations[i]) / (placed[i] + 1);
                if (len > bestLen) { bestLen = len; best = i; }   // remember the gap with the longest section
            }
            placed[best]++;                              // the new station goes into that gap
        }
        double ans = 0;                                  // answer = longest section after all placements
        for (int i = 0; i < n - 1; i++) {
            // section length in gap i after all placements
            ans = Math.max(ans, (double) (stations[i + 1] - stations[i]) / (placed[i] + 1));
        }
        return ans;
    }

    /** Approach 2: the same greedy, but a max-heap hands over the longest section in O(log n). O((n + k) log n) time, O(n) space. */
    static double better(int[] stations, int k) {
        int n = stations.length;
        if (n < 2) return 0;
        int[] placed = new int[n - 1];                   // new stations already dropped into gap i
        PriorityQueue<double[]> heap = new PriorityQueue<>((a, b) -> Double.compare(b[0], a[0]));   // {sectionLength, gapIndex}
        // every gap starts as one whole section
        for (int i = 0; i < n - 1; i++) heap.offer(new double[]{stations[i + 1] - stations[i], i});
        for (int t = 0; t < k; t++) {
            int gap = (int) heap.poll()[1];              // gap with the longest section right now
            placed[gap]++;                               // put one more station into it
            // push the gap back with its shorter section length
            heap.offer(new double[]{(double) (stations[gap + 1] - stations[gap]) / (placed[gap] + 1), gap});
        }
        return heap.peek()[0];                           // the longest section left is the answer
    }

    /** Can every section be made at most d long using at most k new stations? O(n). */
    static boolean feasible(int[] stations, int k, double d) {
        long needed = 0;                                 // new stations used so far
        for (int i = 0; i < stations.length - 1; i++) {   // look at each gap between neighbours
            int gap = stations[i + 1] - stations[i];
            long inside = (long) Math.ceil(gap / d) - 1;   // ceil(gap / d) sections need one fewer new stations
            if (inside > k - needed) return false;       // more than the stations left: d is too small
            needed += inside;                            // spend them on this gap
        }
        return true;                                     // every gap fits within d using at most k
    }

    /** Approach 3: binary search on the answer d in [0, largest gap]; feasibility is monotone in d. O(n log(maxGap / eps)) time, O(1) space. */
    static double optimal(int[] stations, int k) {
        int n = stations.length;
        if (n < 2) return 0;                             // fewer than two stations: no gap to shrink
        double lo = 0, hi = 0;                           // answer lies in [0, largest gap]
        // hi starts at the largest existing gap
        for (int i = 0; i < n - 1; i++) hi = Math.max(hi, stations[i + 1] - stations[i]);
        for (int iter = 0; iter < 100; iter++) {         // 100 halvings shrink any gap far below 1e-6
            double mid = (lo + hi) / 2;                  // candidate maximum distance
            if (feasible(stations, k, mid)) hi = mid; else lo = mid;   // feasible: try smaller, else go larger
        }
        return hi;                                       // smallest feasible distance found
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] stations, int k, double expected) {
        String in = Arrays.toString(stations) + " k=" + k;
        check(Math.abs(bruteForce(stations, k) - expected) < 1e-6, "bruteForce " + in);
        check(Math.abs(better(stations, k) - expected) < 1e-6, "better " + in);
        check(Math.abs(optimal(stations, k) - expected) < 1e-6, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 4, 0.5);
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 1, 1.0);
        verify(new int[]{1, 13, 17, 23}, 5, 3.0);
        verify(new int[]{3, 6, 12, 19, 33, 44, 67, 72, 89, 95}, 2, 14.0);
        verify(new int[]{3, 6, 12, 19, 33, 44, 67, 72, 89, 95}, 3, 11.5);
        verify(new int[]{0, 100}, 3, 25.0);                  // one gap split into k+1 equal sections
        verify(new int[]{1, 4}, 1, 1.5);
        verify(new int[]{1, 2, 10}, 3, 2.0);                 // all new stations go into the single big gap
        verify(new int[]{1, 2, 3, 4, 5}, 0, 1.0);            // no new stations: the largest existing gap
        verify(new int[]{10}, 3, 0.0);                       // a single station has no gaps
        System.out.println("OK P78_MinimizeMaxDistanceToGasStation");
    }
}
