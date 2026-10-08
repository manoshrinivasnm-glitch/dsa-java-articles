import java.util.*;

/** TUF 2766 - Allocate Minimum Number of Pages. Split books (in order) among m students, minimise the largest share. */
public class P2766_AllocateMinimumNumberOfPages {

    /** Greedy: how many students are needed if nobody may read more than maxPages. Assumes maxPages >= every book. */
    static int studentsNeeded(int[] pages, long maxPages) {
        int students = 1;
        long current = 0;
        for (int p : pages) {
            if (current + p > maxPages) {                  // this book does not fit: start the next student
                students++;
                current = p;
            } else {
                current += p;
            }
        }
        return students;
    }

    /** Approach 1: try every limit from max(pages) upward; the first one that works is the answer. O(n * (sum - max)) time, O(1) space. */
    static long bruteForce(int[] pages, int m) {
        if (m > pages.length) return -1;                   // someone would get no book
        long lo = 0, hi = 0;
        for (int p : pages) {
            lo = Math.max(lo, p);
            hi += p;
        }
        for (long limit = lo; limit <= hi; limit++) {
            if (studentsNeeded(pages, limit) <= m) return limit;
        }
        return -1;
    }

    /** Approach 2: binary search the smallest feasible limit in [max, sum]. O(n log(sum)) time, O(1) space. */
    static long optimal(int[] pages, int m) {
        if (m > pages.length) return -1;
        long lo = 0, hi = 0;
        for (int p : pages) {
            lo = Math.max(lo, p);
            hi += p;
        }
        while (lo < hi) {
            long mid = lo + (hi - lo) / 2;
            if (studentsNeeded(pages, mid) <= m) hi = mid;  // feasible: the answer is mid or smaller
            else lo = mid + 1;                              // infeasible: the answer is bigger than mid
        }
        return lo;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] pages, int m, long expected) {
        check(bruteForce(pages, m) == expected, "bruteForce " + Arrays.toString(pages) + " m=" + m + " got " + bruteForce(pages, m));
        check(optimal(pages, m) == expected, "optimal " + Arrays.toString(pages) + " m=" + m + " got " + optimal(pages, m));
    }

    public static void main(String[] args) {
        verify(new int[]{12, 34, 67, 90}, 2, 113);                 // [12, 34, 67] | [90]
        verify(new int[]{25, 46, 28, 49, 24}, 4, 71);              // [25, 46] | [28] | [49] | [24]
        verify(new int[]{15, 10, 19, 10, 5, 18, 7}, 5, 25);
        verify(new int[]{15, 17, 20}, 5, -1);                      // edge: more students than books
        verify(new int[]{15, 17, 20}, 1, 52);                      // one student reads everything
        verify(new int[]{15, 17, 20}, 3, 20);                      // one book each: the largest book
        verify(new int[]{7}, 1, 7);                                // edge: single book
        verify(new int[]{10, 20, 30, 40}, 2, 60);                  // [10, 20, 30] | [40]
        verify(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000}, 3, 1_000_000_000L); // sum overflows int
        check(optimal(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000}, 1) == 3_000_000_000L, "large sum");
        System.out.println("OK P2766_AllocateMinimumNumberOfPages");
    }
}
