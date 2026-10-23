import java.util.*;

/** TUF 74 - Book Allocation Problem. Give each of m students a contiguous block of books so the largest page total is minimal; -1 if m > n. */
public class P74_BookAllocationProblem {

    /** Students needed if nobody may receive more than maxPages pages, assigning greedily from the left. O(n). */
    static int studentsNeeded(int[] pages, int maxPages) {
        int students = 1;                                       // the first student starts taking books
        long load = 0;                                          // pages given to the current student
        for (int p : pages) {
            if (load + p > maxPages) { students++; load = 0; }  // too many: next student takes this book
            load += p;
        }
        return students;
    }

    /** Approach 1: try every limit from the thickest book up to the total page count. O(n * (sum - max)) time, O(1) space. */
    static int bruteForce(int[] pages, int m) {
        int n = pages.length;
        if (m > n) return -1;                           // each student needs at least one book
        int max = 0, sum = 0;
        for (int p : pages) { max = Math.max(max, p); sum += p; }   // thickest book and total pages
        for (int limit = max; limit <= sum; limit++) {  // below max the thickest book fits nobody
            if (studentsNeeded(pages, limit) <= m) return limit;   // first limit that works is the minimum
        }
        return sum;                                     // limit sum lets one student take everything
    }

    /** Approach 2: binary search on the limit in [max, sum]; studentsNeeded only falls as the limit grows. O(n log(sum)) time, O(1) space. */
    static int optimal(int[] pages, int m) {
        int n = pages.length;
        if (m > n) return -1;                                      // each student needs at least one book
        int max = 0, sum = 0;
        for (int p : pages) { max = Math.max(max, p); sum += p; }  // thickest book and total pages
        int lo = max, hi = sum, ans = sum;                         // limit sum always works
        while (lo <= hi) {                                         // search until the range is empty
            int mid = lo + (hi - lo) / 2;
            if (studentsNeeded(pages, mid) <= m) { ans = mid; hi = mid - 1; }   // works: try smaller
            else lo = mid + 1;                                     // needs too many students: raise the limit
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] pages, int m, int expected) {
        String in = Arrays.toString(pages) + " m=" + m;
        check(bruteForce(pages, m) == expected, "bruteForce " + in);
        check(optimal(pages, m) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{12, 34, 67, 90}, 2, 113);
        verify(new int[]{25, 46, 28, 49, 24}, 4, 71);
        verify(new int[]{15, 17, 20}, 2, 32);
        verify(new int[]{10, 20, 30, 40}, 2, 60);        // [10,20,30 | 40] beats [10,20 | 30,40]
        verify(new int[]{1, 2, 3}, 4, -1);                // more students than books
        verify(new int[]{5, 5, 5}, 3, 5);                 // one book each: answer is the max
        verify(new int[]{100}, 1, 100);                   // single book, single student
        verify(new int[]{12, 34, 67, 90}, 1, 203);        // one student takes everything: answer is the sum
        System.out.println("OK P74_BookAllocationProblem");
    }
}
