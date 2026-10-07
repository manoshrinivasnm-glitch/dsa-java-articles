import java.util.*;

/**
 * TUF 405 - Pattern 13 (Increasing Number Triangle).
 * Row i (1-based) holds the next i integers of a running count, separated by single spaces:
 * 1 / 2 3 / 4 5 6 / 7 8 9 10 / ...
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P405_Pattern13 {

    /** Approach 1: a running counter that never resets. O(n^2) time, O(n^2) output. */
    static String counter(int n) {
        StringBuilder sb = new StringBuilder();
        int next = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (j > 1) sb.append(' ');
                sb.append(next++);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: the first value of row i is the triangular number T(i-1) + 1, so every row is independent. O(n^2) time. */
    static String formula(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            long first = (long) i * (i - 1) / 2 + 1;   // T(i-1) + 1
            for (int j = 0; j < i; j++) {
                if (j > 0) sb.append(' ');
                sb.append(first + j);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Value printed at row r, position c (both 1-based), without building anything. O(1). */
    static long valueAt(int r, int c) {
        return (long) r * (r - 1) / 2 + c;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(counter(n).equals(expected), "counter(" + n + ") gave\n" + counter(n));
        check(formula(n).equals(expected), "formula(" + n + ") gave\n" + formula(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "1\n");
        verify(2, "1\n2 3\n");
        verify(3, "1\n2 3\n4 5 6\n");
        verify(4, "1\n2 3\n4 5 6\n7 8 9 10\n");
        verify(5, "1\n2 3\n4 5 6\n7 8 9 10\n11 12 13 14 15\n");

        // Structural checks: row i has i tokens, the tokens count 1, 2, 3, ... with no gaps, and valueAt agrees.
        for (int n = 1; n <= 30; n++) {
            String s = counter(n);
            check(s.equals(formula(n)), "approaches differ for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            long expect = 1;
            for (int i = 0; i < n; i++) {
                String[] tok = rows[i].split(" ");
                check(tok.length == i + 1, "row " + i + " token count for n = " + n);
                for (int c = 0; c < tok.length; c++) {
                    check(Long.parseLong(tok[c]) == expect, "sequence broken at row " + i + " for n = " + n);
                    check(valueAt(i + 1, c + 1) == expect, "valueAt disagrees at row " + (i + 1) + " col " + (c + 1));
                    expect++;
                }
            }
            check(expect - 1 == (long) n * (n + 1) / 2, "total count must be the triangular number T(n)");
        }
        check(valueAt(100000, 1) == 4999950001L, "valueAt must not overflow int for large rows");

        System.out.print(counter(5));
        System.out.println("OK P405_Pattern13");
    }
}
