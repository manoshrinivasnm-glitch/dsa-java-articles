import java.util.*;

/** TUF 551 - Shortest Job First. All processes arrive at time 0; return the floor of the average waiting time under non-preemptive SJF. */
public class P551_ShortestJobFirst {

    /** Approach 1: simulate the scheduler, scanning for the shortest unfinished job each time. O(n^2) time, O(n) space. */
    static long bruteForce(int[] bt) {
        int n = bt.length;
        if (n == 0) return 0;
        boolean[] done = new boolean[n];
        long clock = 0, totalWait = 0;
        for (int step = 0; step < n; step++) {
            int pick = -1;
            for (int i = 0; i < n; i++) {
                if (!done[i] && (pick == -1 || bt[i] < bt[pick])) pick = i;
            }
            totalWait += clock;                  // job 'pick' has waited from time 0 until now
            clock += bt[pick];
            done[pick] = true;
        }
        return totalWait / n;
    }

    /** Approach 2: sort the burst times once, then accumulate waiting times with a running clock. O(n log n) time, O(n) space for the copy. */
    static long optimal(int[] bt) {
        int n = bt.length;
        if (n == 0) return 0;
        int[] sorted = bt.clone();
        Arrays.sort(sorted);
        long clock = 0, totalWait = 0;
        for (int b : sorted) {
            totalWait += clock;
            clock += b;
        }
        return totalWait / n;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] bt, long expected) {
        String in = bt.length <= 10 ? Arrays.toString(bt) : "n=" + bt.length;
        check(bruteForce(bt) == expected, "bruteForce " + in + " -> " + bruteForce(bt));
        check(optimal(bt) == expected, "optimal " + in + " -> " + optimal(bt));
    }

    public static void main(String[] args) {
        verify(new int[]{4, 3, 7, 1, 2}, 4);                   // waits 0, 1, 3, 6, 10 -> 20 / 5
        verify(new int[]{1, 2, 3, 4}, 2);                      // 10 / 4 = 2.5, floor 2
        verify(new int[]{3, 3, 3}, 3);                         // ties: waits 0, 3, 6
        verify(new int[]{10, 1}, 0);                           // 1 / 2 = 0.5, floor 0
        verify(new int[]{5}, 0);                               // a single process never waits
        verify(new int[]{}, 0);                                // no processes
        int[] big = new int[3000];
        Arrays.fill(big, 100_000);
        verify(big, 149_950_000L);                             // total wait 449,850,000,000 overflows int
        System.out.println("OK P551_ShortestJobFirst");
    }
}
