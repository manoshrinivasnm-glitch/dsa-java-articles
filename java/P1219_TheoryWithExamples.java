import java.util.*;

/** TUF 1219 - Theory with examples (time and space complexity). Each method runs an algorithm while counting
 *  its basic operations, so the growth rates discussed in the article can be checked against real numbers. */
public class P1219_TheoryWithExamples {

    /** O(n): scan until the target appears; the number of comparisons is n when it is absent. */
    static long linearSearchSteps(int[] a, int target) {
        long steps = 0;
        for (int i = 0; i < a.length; i++) {
            steps++;
            if (a[i] == target) break;
        }
        return steps;
    }

    /** O(log n): every probe halves the range, so at most floor(log2 n) + 1 probes happen. */
    static long binarySearchSteps(int[] sorted, int target) {
        long steps = 0;
        int lo = 0, hi = sorted.length - 1;
        while (lo <= hi) {
            steps++;
            int mid = lo + (hi - lo) / 2;
            if (sorted[mid] == target) break;
            if (sorted[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return steps;
    }

    /** O(n^2): the inner loop runs n-1, n-2, ..., 0 times, which sums to n(n-1)/2. */
    static long pairLoopSteps(int n) {
        long steps = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) steps++;
        }
        return steps;
    }

    /** O(n^3): three nested loops over n give exactly n^3 inner iterations. */
    static long tripleLoopSteps(int n) {
        long steps = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) steps++;
            }
        }
        return steps;
    }

    /** O(log n): halving until 1 takes floor(log2 n) steps. */
    static long halvingSteps(long n) {
        long steps = 0;
        while (n > 1) {
            n /= 2;
            steps++;
        }
        return steps;
    }

    /** O(n log n): T(n) = T(n/2) + T(n - n/2) + n, the work of merge sort; for powers of two it is exactly n log2 n. */
    static long mergeSortSteps(int n) {
        if (n <= 1) return 0;
        int half = n / 2;
        return mergeSortSteps(half) + mergeSortSteps(n - half) + n;
    }

    /** O(2^n): include-or-exclude recursion visits one leaf per subset. */
    static long subsetLeaves(int n) {
        if (n == 0) return 1;
        return subsetLeaves(n - 1) + subsetLeaves(n - 1);
    }

    /** Rule of thumb: about 10^8 simple operations per second. Given n, which complexity still fits? */
    static String feasibleComplexity(long n) {
        if (n <= 20) return "O(2^n)";
        if (n <= 500) return "O(n^3)";
        if (n <= 10_000) return "O(n^2)";
        if (n <= 1_000_000) return "O(n log n)";
        if (n <= 100_000_000L) return "O(n)";
        return "O(log n) or O(1)";
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
