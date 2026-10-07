import java.util.*;

/**
 * TUF 404 - Pattern 12 (Number Crown).
 * Row i (1-based) prints 1..i, then 2*(n-i) spaces, then i..1, so every row is 2n characters wide.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P404_Pattern12 {

    /** Approach 1: three inner loops per row (left digits, gap, right digits). O(n^2) time, O(n^2) output. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) sb.append(j);          // 1 2 ... i
            for (int j = 0; j < 2 * (n - i); j++) sb.append(' '); // shrinking gap
            for (int j = i; j >= 1; j--) sb.append(j);          // i ... 2 1
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: grow the two halves incrementally and reuse them row after row. O(n^2) time. */
    static String incremental(int n) {
        StringBuilder sb = new StringBuilder();
        StringBuilder left = new StringBuilder();   // "12...i"
        StringBuilder right = new StringBuilder();  // "i...21"
        for (int i = 1; i <= n; i++) {
            left.append(i);
            right.insert(0, i);
            sb.append(left).append(" ".repeat(2 * (n - i))).append(right).append('\n');
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(incremental(n).equals(expected), "incremental(" + n + ") gave\n" + incremental(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "11\n");
        verify(2, "1  1\n1221\n");
        verify(3, "1    1\n12  21\n123321\n");
        verify(4, "1      1\n12    21\n123  321\n12344321\n");

        // Structural checks for every n up to 9 (single-digit numbers keep the rows aligned).
        for (int n = 1; n <= 9; n++) {
            String s = nestedLoops(n);
            check(s.equals(incremental(n)), "approaches differ for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                check(rows[i].length() == 2 * n, "row width for n = " + n + ", row " + i);
                check(rows[i].charAt(0) == '1' && rows[i].charAt(2 * n - 1) == '1', "row must start and end with 1");
            }
            check(rows[n - 1].indexOf(' ') < 0, "last row has no gap");
        }
        // Beyond 9 the digits become two characters wide; both approaches must still agree.
        for (int n = 10; n <= 12; n++) check(nestedLoops(n).equals(incremental(n)), "approaches differ for n = " + n);

        System.out.print(nestedLoops(5));
        System.out.println("OK P404_Pattern12");
    }
}
