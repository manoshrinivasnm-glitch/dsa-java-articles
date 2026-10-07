import java.util.*;

/** TUF 493 - Generate Binary Strings Without Consecutive 1s. All binary strings of length n with no two adjacent 1s, in lexicographic order. */
public class P493_GenerateBinaryStringsWithoutConsecutive1 {

    /** Approach 1: enumerate all 2^n strings and keep those without "11". O(2^n * n) time. */
    static List<String> bruteForce(int n) {
        List<String> result = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {       // counting upward is lexicographic order for fixed-width strings
            StringBuilder sb = new StringBuilder();
            for (int bit = n - 1; bit >= 0; bit--) sb.append((mask >> bit) & 1);
            String s = sb.toString();
            if (!s.contains("11")) result.add(s);
        }
        return result;
    }

    /** Approach 2: build position by position; after a 1 the only legal next character is 0. O(F(n+2) * n) time, O(n) stack. */
    static List<String> optimal(int n) {
        List<String> result = new ArrayList<>();
        build(new char[n], 0, false, result);
        return result;
    }

    static void build(char[] current, int pos, boolean lastWasOne, List<String> result) {
        if (pos == current.length) {                         // base case: every position is filled
            result.add(new String(current));
            return;
        }
        current[pos] = '0';                                  // choice 1: a 0 is always legal, and sorts first
        build(current, pos + 1, false, result);
        if (!lastWasOne) {                                   // choice 2: a 1, only when the previous character was not 1
            current[pos] = '1';
            build(current, pos + 1, true, result);
        }
    }

    /** Approach 3: count without generating. endsWith0 and endsWith1 follow the Fibonacci recurrence. O(n) time, O(1) space. */
    static long countOnly(int n) {
        if (n == 0) return 1;                                // the empty string
        long endsWith0 = 1, endsWith1 = 1;                   // the strings "0" and "1"
        for (int len = 2; len <= n; len++) {
            long nextEndsWith0 = endsWith0 + endsWith1;      // a 0 may follow anything
            long nextEndsWith1 = endsWith0;                  // a 1 may only follow a 0
            endsWith0 = nextEndsWith0;
            endsWith1 = nextEndsWith1;
        }
        return endsWith0 + endsWith1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, List<String> expected) {
        check(bruteForce(n).equals(expected), "bruteForce(" + n + ") = " + bruteForce(n));
        check(optimal(n).equals(expected), "optimal(" + n + ") = " + optimal(n));
        check(countOnly(n) == expected.size(), "countOnly(" + n + ") = " + countOnly(n));
    }

    public static void main(String[] args) {
        verify(0, List.of(""));                               // one string of length 0
        verify(1, List.of("0", "1"));
        verify(2, List.of("00", "01", "10"));
        verify(3, List.of("000", "001", "010", "100", "101"));
        verify(4, List.of("0000", "0001", "0010", "0100", "0101", "1000", "1001", "1010"));
        for (int n = 5; n <= 16; n++) {                        // both generators must agree exactly, and the count must match
            List<String> expected = bruteForce(n);
            verify(n, expected);
        }
        check(countOnly(10) == 144, "F(12) = 144");
        check(countOnly(20) == 17711, "F(22) = 17711");
        List<String> ten = optimal(10);
        for (int i = 1; i < ten.size(); i++) check(ten.get(i - 1).compareTo(ten.get(i)) < 0, "lexicographic order at " + i);
        for (String s : ten) check(!s.contains("11"), "no consecutive 1s: " + s);
        System.out.println("OK P493_GenerateBinaryStringsWithoutConsecutive1");
    }
}
