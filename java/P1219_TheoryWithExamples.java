import java.util.*;

/** TUF 1219 - Theory with examples (time and space complexity). Each method runs an algorithm while counting
 *  its basic operations, so the growth rates discussed in the article can be checked against real numbers. */
public class P1219_TheoryWithExamples {

    /** O(n): scan until the target appears; the number of comparisons is n when it is absent. */
    static long linearSearchSteps(int[] a, int target) {
        long steps = 0;                       // counts the comparisons made
        for (int i = 0; i < a.length; i++) {  // walk the array from the left
            steps++;                          // one comparison per element visited
            if (a[i] == target) break;        // found: stop early (best case is 1 step)
        }
        return steps;
    }

    /** O(log n): every probe halves the range, so at most floor(log2 n) + 1 probes happen. */
    static long binarySearchSteps(int[] sorted, int target) {
        long steps = 0;
        int lo = 0, hi = sorted.length - 1;          // the target can only be in sorted[lo..hi]
        while (lo <= hi) {                           // while that range is not empty
            steps++;                                 // one probe per halving
            int mid = lo + (hi - lo) / 2;            // middle index, written so it cannot overflow
            if (sorted[mid] == target) break;        // found the target
            if (sorted[mid] < target) lo = mid + 1;  // target is right of mid: drop the left half
            else hi = mid - 1;                       // otherwise drop the right half
        }
        return steps;
    }

    /** O(n^2): the inner loop runs n-1, n-2, ..., 0 times, which sums to n(n-1)/2. */
    static long pairLoopSteps(int n) {
        long steps = 0;
        for (int i = 0; i < n; i++) {                 // i takes every value 0..n-1
            for (int j = i + 1; j < n; j++) steps++;  // j only takes values after i: n-1-i steps
        }
        return steps;
    }

    /** O(n^3): three nested loops over n give exactly n^3 inner iterations. */
    static long tripleLoopSteps(int n) {
        long steps = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) steps++;  // runs n times for each (i, j), n^3 in total
            }
        }
        return steps;
    }

    /** O(log n): halving until 1 takes floor(log2 n) steps. */
    static long halvingSteps(long n) {
        long steps = 0;
        while (n > 1) {  // stop when one is left
            n /= 2;      // halving happens about log2 n times
            steps++;
        }
        return steps;
    }

    /** O(n log n): T(n) = T(n/2) + T(n - n/2) + n, the work of merge sort; for powers of two it is exactly n log2 n. */
    static long mergeSortSteps(int n) {
        if (n <= 1) return 0;  // base case: 0 or 1 element needs no merging
        int half = n / 2;      // split into two halves
        return mergeSortSteps(half) + mergeSortSteps(n - half) + n;   // both halves, plus n steps to merge
    }

    /** O(2^n): include-or-exclude recursion visits one leaf per subset. */
    static long subsetLeaves(int n) {
        if (n == 0) return 1;                              // base case: one leaf, one finished subset
        return subsetLeaves(n - 1) + subsetLeaves(n - 1);  // skip element n, or take it: two branches
    }

    /** Rule of thumb: about 10^8 simple operations per second. Given n, which complexity still fits? */
    static String feasibleComplexity(long n) {
        if (n <= 20) return "O(2^n)";             // 2^20 is about 10^6
        if (n <= 500) return "O(n^3)";            // 500^3 is about 10^8
        if (n <= 10_000) return "O(n^2)";         // 10^4 squared is 10^8
        if (n <= 1_000_000) return "O(n log n)";  // 10^6 * 20 is about 2 * 10^7
        if (n <= 100_000_000L) return "O(n)";     // about 10^8 single steps
        return "O(log n) or O(1)";                // beyond 10^8, only sublinear work fits
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] big = new int[100000];
        for (int i = 0; i < big.length; i++) big[i] = i;

        check(linearSearchSteps(a, 1) == 1, "best case: one comparison");
        check(linearSearchSteps(a, 8) == 8, "worst case present: n comparisons");
        check(linearSearchSteps(a, 42) == 8, "absent: n comparisons");
        check(linearSearchSteps(new int[0], 1) == 0, "empty array: zero comparisons");
        check(linearSearchSteps(big, -1) == 100000, "linear growth: 10^5 comparisons for 10^5 elements");

        check(binarySearchSteps(a, 4) == 1, "middle element is found at once");
        check(binarySearchSteps(a, 42) == 4, "absent in 8 elements: floor(log2 8) + 1 = 4 probes");
        check(binarySearchSteps(big, -1) <= 17, "absent in 10^5 elements: at most 17 probes");
        check(binarySearchSteps(new int[0], 1) == 0, "empty array: zero probes");
        check(binarySearchSteps(big, 99999) <= 17 && binarySearchSteps(big, 0) <= 17, "the ends are found within the bound too");

        check(pairLoopSteps(0) == 0 && pairLoopSteps(1) == 0, "no pairs below two elements");
        check(pairLoopSteps(2) == 1, "one pair");
        check(pairLoopSteps(10) == 45, "10 * 9 / 2");
        check(pairLoopSteps(3000) == 3000L * 2999 / 2, "matches the closed form");

        check(tripleLoopSteps(0) == 0 && tripleLoopSteps(1) == 1, "0^3 and 1^3");
        check(tripleLoopSteps(2) == 8, "2^3");
        check(tripleLoopSteps(10) == 1000, "10^3");
        check(tripleLoopSteps(200) == 8_000_000L, "200^3");

        check(halvingSteps(1) == 0, "1 needs no halving");
        check(halvingSteps(2) == 1 && halvingSteps(3) == 1, "2 and 3");
        check(halvingSteps(1024) == 10, "2^10");
        check(halvingSteps(1_000_000_000_000L) == 39, "10^12 halves 39 times");
        check(halvingSteps(1_000_000) == 63 - Long.numberOfLeadingZeros(1_000_000L), "equals floor(log2 n)");

        check(mergeSortSteps(0) == 0 && mergeSortSteps(1) == 0, "nothing to sort");
        check(mergeSortSteps(2) == 2, "n = 2");
        check(mergeSortSteps(8) == 24, "8 * log2 8");
        check(mergeSortSteps(1024) == 10240, "1024 * 10");
        check(mergeSortSteps(1000) >= 9000 && mergeSortSteps(1000) <= 10000, "between n * floor(log n) and n * ceil(log n)");

        check(subsetLeaves(0) == 1, "the empty set has one subset");
        check(subsetLeaves(1) == 2 && subsetLeaves(3) == 8, "2^1 and 2^3");
        check(subsetLeaves(10) == 1024, "2^10");
        check(subsetLeaves(20) == 1_048_576, "2^20, about a million leaves");

        check(feasibleComplexity(15).equals("O(2^n)"), "n = 15");
        check(feasibleComplexity(400).equals("O(n^3)"), "n = 400");
        check(feasibleComplexity(5000).equals("O(n^2)"), "n = 5000");
        check(feasibleComplexity(200_000).equals("O(n log n)"), "n = 2 * 10^5");
        check(feasibleComplexity(50_000_000).equals("O(n)"), "n = 5 * 10^7");
        check(feasibleComplexity(1_000_000_000_000L).equals("O(log n) or O(1)"), "n = 10^12");

        System.out.println("OK P1219_TheoryWithExamples");
    }
}
