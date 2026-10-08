import java.util.*;

/** TUF 544 - Candy. Minimum candies so each child gets at least one and a higher-rated child gets more than a lower-rated neighbour. */
public class P544_Candy {

    /** Approach 1: start everyone at 1 and keep repairing violated rules until a full sweep changes nothing. O(n^2) time, O(n) space. */
    static int bruteForce(int[] ratings) {
        int n = ratings.length;
        int[] candies = new int[n];
        Arrays.fill(candies, 1);
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < n; i++) {
                if (i > 0 && ratings[i] > ratings[i - 1] && candies[i] <= candies[i - 1]) {
                    candies[i] = candies[i - 1] + 1;
                    changed = true;
                }
                if (i < n - 1 && ratings[i] > ratings[i + 1] && candies[i] <= candies[i + 1]) {
                    candies[i] = candies[i + 1] + 1;
                    changed = true;
                }
            }
        }
        int total = 0;
        for (int c : candies) total += c;
        return total;
    }

    /** Approach 2: a left-to-right pass for the left rule, a right-to-left pass for the right rule, take the max. O(n) time, O(n) space. */
    static int better(int[] ratings) {
        int n = ratings.length;
        int[] left = new int[n], right = new int[n];
        Arrays.fill(left, 1);
        Arrays.fill(right, 1);
        for (int i = 1; i < n; i++) {
            if (ratings[i] > ratings[i - 1]) left[i] = left[i - 1] + 1;
        }
        for (int i = n - 2; i >= 0; i--) {
            if (ratings[i] > ratings[i + 1]) right[i] = right[i + 1] + 1;
        }
        int total = 0;
        for (int i = 0; i < n; i++) total += Math.max(left[i], right[i]);
        return total;
    }

    /** Approach 3: walk the ratings as up-slopes and down-slopes, counting candies on the fly. O(n) time, O(1) space. */
    static int optimal(int[] ratings) {
        int n = ratings.length;
        if (n == 0) return 0;
        int total = 1, i = 1;                                  // child 0 gets 1 candy for now
        while (i < n) {
            if (ratings[i] == ratings[i - 1]) {                // flat step: no rule applies, restart at 1
                total += 1;
                i++;
                continue;
            }
            int peak = 1;
            while (i < n && ratings[i] > ratings[i - 1]) {     // climbing: 2, 3, 4, ...
                peak++;
                total += peak;
                i++;
            }
            int down = 1;
            while (i < n && ratings[i] < ratings[i - 1]) {     // descending: count 1, 2, 3, ... from the bottom
                total += down;
                i++;
                down++;
            }
            if (down > peak) total += down - peak;             // the peak must beat the longer side
        }
        return total;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] ratings, int expected) {
        String in = Arrays.toString(ratings);
        check(bruteForce(ratings) == expected, "bruteForce " + in + " -> " + bruteForce(ratings));
        check(better(ratings) == expected, "better " + in + " -> " + better(ratings));
        check(optimal(ratings) == expected, "optimal " + in + " -> " + optimal(ratings));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 0, 2}, 5);
        verify(new int[]{1, 2, 2}, 4);                         // equal neighbours impose nothing
        verify(new int[]{1, 2, 87, 87, 87, 2, 1}, 13);
        verify(new int[]{1, 3, 4, 5, 2}, 11);                  // up-slope longer than down-slope
        verify(new int[]{1, 3, 2, 1}, 7);                      // down-slope longer: the peak is raised
        verify(new int[]{3, 1, 3}, 5);                         // valley
        verify(new int[]{1, 2, 3, 4, 5}, 15);                  // strictly increasing
        verify(new int[]{5, 4, 3, 2, 1}, 15);                  // strictly decreasing
        verify(new int[]{2, 2, 2}, 3);                         // all equal
        verify(new int[]{5}, 1);                               // single child
        verify(new int[]{}, 0);                                // no children
        System.out.println("OK P544_Candy");
    }
}
