import java.util.*;

/** TUF 489 - Fractional Knapsack. Items may be split; maximise total value carried in a bag of the given capacity. */
public class P489_FractionalKnapsack {

    /** Approach 1: greedy, finding the best remaining value/weight ratio with a linear scan every round. O(n^2) time, O(n) space. */
    static double greedyScan(int[] val, int[] wt, int capacity) {
        int n = val.length;
        boolean[] taken = new boolean[n];
        double total = 0;
        int left = capacity;
        while (left > 0) {
            int best = -1;
            for (int i = 0; i < n; i++) {
                if (taken[i]) continue;
                // val[i]/wt[i] > val[best]/wt[best], cross-multiplied in long to avoid division
                if (best == -1 || (long) val[i] * wt[best] > (long) val[best] * wt[i]) best = i;
            }
            if (best == -1) break;                  // every item is already in the bag
            taken[best] = true;
            if (wt[best] <= left) {
                total += val[best];
                left -= wt[best];
            } else {
                total += (double) val[best] * left / wt[best];
                left = 0;
            }
        }
        return total;
    }

    /** Approach 2: sort items once by value per unit weight, then fill the bag in that order. O(n log n) time, O(n) space. */
    static double optimal(int[] val, int[] wt, int capacity) {
        int n = val.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        // descending by val/wt; comparing val[a]*wt[b] with val[b]*wt[a] keeps the order exact
        Arrays.sort(order, (a, b) -> Long.compare((long) val[b] * wt[a], (long) val[a] * wt[b]));
        double total = 0;
        int left = capacity;
        for (int i : order) {
            if (left == 0) break;
            if (wt[i] <= left) {
                total += val[i];
                left -= wt[i];
            } else {
                total += (double) val[i] * left / wt[i];
                left = 0;
            }
        }
        return total;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] val, int[] wt, int capacity, double expected) {
        String in = Arrays.toString(val) + " / " + Arrays.toString(wt) + " W=" + capacity;
        double g = greedyScan(val, wt, capacity);
        double o = optimal(val, wt, capacity);
        check(Math.abs(g - expected) < 1e-6, "greedyScan gave " + g + ", expected " + expected + " for " + in);
        check(Math.abs(o - expected) < 1e-6, "optimal gave " + o + ", expected " + expected + " for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{60, 100, 120}, new int[]{10, 20, 30}, 50, 240.0);   // last item taken as 2/3
        verify(new int[]{60, 100}, new int[]{10, 20}, 50, 160.0);             // everything fits
        verify(new int[]{10, 20, 30}, new int[]{5, 10, 15}, 12, 24.0);        // all ratios equal
        verify(new int[]{500}, new int[]{30}, 10, 500.0 / 3);                 // fractional result
        verify(new int[]{8, 2, 10, 1, 9, 7, 2, 6, 4, 9}, new int[]{10, 1, 7, 7, 5, 1, 8, 6, 8, 7}, 21, 37.0);
        verify(new int[]{1_000_000_000, 1_000_000_000}, new int[]{1, 1_000_000_000}, 2, 1_000_000_001.0); // products need long
        verify(new int[]{60, 100, 120}, new int[]{10, 20, 30}, 0, 0.0);       // zero capacity
        verify(new int[]{}, new int[]{}, 10, 0.0);                            // no items
        System.out.println("OK P489_FractionalKnapsack");
    }
}
