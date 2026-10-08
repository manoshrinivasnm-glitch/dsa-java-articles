import java.util.*;

/** TUF 818 - Permutation Sequence. Return the k-th (1-based) permutation of 1..n in lexicographic order. */
public class P818_PermutationSequence {

    /** Approach 1: generate all n! permutations, sort them, pick index k - 1. O(n! * n log(n!)) time, O(n! * n) space. */
    static String bruteForce(int n, int k) {
        char[] digits = firstPermutation(n);
        List<String> all = new ArrayList<>();
        permute(digits, 0, all);
        Collections.sort(all);                          // swapping does not generate them in order
        return all.get(k - 1);
    }

    static void permute(char[] digits, int index, List<String> all) {
        if (index == digits.length) {
            all.add(new String(digits));
            return;
        }
        for (int i = index; i < digits.length; i++) {
            swap(digits, index, i);                     // choose digits[i] for this position
            permute(digits, index + 1, all);
            swap(digits, index, i);                     // undo the choice before trying the next one
        }
    }

    /** Approach 2: start from 12...n and apply next permutation k - 1 times. O(k * n) time, O(n) space. */
    static String better(int n, int k) {
        char[] digits = firstPermutation(n);
        for (int step = 1; step < k; step++) nextPermutation(digits);
        return new String(digits);
    }

    static void nextPermutation(char[] a) {
        int i = a.length - 2;
        while (i >= 0 && a[i] >= a[i + 1]) i--;         // rightmost position that can still grow
        if (i >= 0) {
            int j = a.length - 1;
            while (a[j] <= a[i]) j--;                   // smallest digit to its right that is larger
            swap(a, i, j);
        }
        reverse(a, i + 1, a.length - 1);                // smallest arrangement of the suffix
    }

    /** Approach 3: factorial number system; each block of (n-1)! permutations shares a first digit. O(n^2) time, O(n) space. */
    static String optimal(int n, int k) {
        List<Integer> remaining = new ArrayList<>();
        int blockSize = 1;
        for (int i = 1; i <= n; i++) {
            remaining.add(i);
            if (i < n) blockSize *= i;                  // ends as (n - 1)!
        }
        k--;                                            // 0-based rank is easier to divide
        StringBuilder sb = new StringBuilder();
        while (true) {
            int index = k / blockSize;                  // how many whole blocks to skip
            sb.append(remaining.remove(index));
            if (remaining.isEmpty()) break;
            k %= blockSize;                             // rank inside the chosen block
            blockSize /= remaining.size();              // (m - 1)! for the m digits that are left
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- helpers
    static char[] firstPermutation(int n) {
        char[] digits = new char[n];
        for (int i = 0; i < n; i++) digits[i] = (char) ('1' + i);
        return digits;
    }

    static void swap(char[] a, int i, int j) {
        char t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    static void reverse(char[] a, int lo, int hi) {
        while (lo < hi) swap(a, lo++, hi--);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int k, String expected) {
        String in = "n=" + n + " k=" + k;
        check(bruteForce(n, k).equals(expected), "bruteForce " + in);
        check(better(n, k).equals(expected), "better " + in);
        check(optimal(n, k).equals(expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(3, 3, "213");
        verify(4, 9, "2314");
        verify(3, 1, "123");                            // first permutation
        verify(3, 6, "321");                            // last permutation
        verify(1, 1, "1");                              // edge: n = 1
        verify(5, 50, "31254");
        verify(9, 362_880, "987654321");                // largest input: k = 9!
        verify(9, 100_000, "358926471");

        for (int n = 1; n <= 6; n++) {                  // every rank of small n, all methods agree
            int total = 1;
            for (int i = 2; i <= n; i++) total *= i;
            for (int k = 1; k <= total; k++) {
                String expected = bruteForce(n, k);
                check(better(n, k).equals(expected) && optimal(n, k).equals(expected), "exhaustive n=" + n + " k=" + k);
            }
        }
        System.out.println("OK P818_PermutationSequence");
    }
}
