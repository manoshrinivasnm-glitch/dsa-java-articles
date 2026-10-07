import java.util.*;

/**
 * TUF 401 - Pattern 1 (Rectangular star pattern).
 * n rows, each holding n stars separated by single spaces, so every row is 2n-1 characters wide.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P401_Pattern1 {

    /** Approach 1: outer loop over rows, inner loop over columns, one cell per inner step. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j > 0) sb.append(' ');
                sb.append('*');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: one loop over the n*n cells, recovering the column from the flat index. O(n^2) time. */
    static String singleLoop(int n) {
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < n * n; k++) {
            int j = k % n;                       // column of cell k (the row would be k / n)
            if (j > 0) sb.append(' ');
            sb.append('*');
            if (j == n - 1) sb.append('\n');     // last column: close the row
        }
        return sb.toString();
    }

    /** Approach 3: build one row with join, then repeat that row n times. O(n^2) time. */
    static String repeatRow(int n) {
        String row = String.join(" ", Collections.nCopies(n, "*"));
        return (row + "\n").repeat(n);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(singleLoop(n).equals(expected), "singleLoop(" + n + ") gave\n" + singleLoop(n));
        check(repeatRow(n).equals(expected), "repeatRow(" + n + ") gave\n" + repeatRow(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "*\n");                                // edge: a single cell, no separator
        verify(2, "* *\n* *\n");
        verify(3, "* * *\n* * *\n* * *\n");
        verify(4, "* * * *\n* * * *\n* * * *\n* * * *\n");

        // Structural checks for larger n: n rows, each 2n-1 wide, stars on even columns, spaces on odd ones.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (String row : rows) {
                check(row.length() == 2 * n - 1, "row width for n = " + n);
                for (int c = 0; c < row.length(); c++) {
                    check(row.charAt(c) == (c % 2 == 0 ? '*' : ' '), "cell " + c + " for n = " + n);
                }
            }
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P401_Pattern1");
    }
}
