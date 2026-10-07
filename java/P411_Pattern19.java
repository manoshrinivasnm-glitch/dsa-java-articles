import java.util.*;

/**
 * TUF 411 - Pattern 19 (Symmetric-Void).
 * 2n rows, each 2n characters wide. The top half starts full and opens a gap in the middle that grows by two
 * each row; the bottom half is the top half upside down.
 * **********
 * ****  ****
 * ***    ***
 * **      **
 * *        *
 * *        *
 * **      **
 * ***    ***
 * ****  ****
 * **********
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P411_Pattern19 {

    /** Approach 1: two halves, each with three inner loops (stars, gap, stars). O(n^2) time, O(n^2) output. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {                       // top half: n-i stars each side, 2i spaces
            for (int j = 0; j < n - i; j++) sb.append('*');
            for (int j = 0; j < 2 * i; j++) sb.append(' ');
            for (int j = 0; j < n - i; j++) sb.append('*');
            sb.append('\n');
        }
        for (int i = 0; i < n; i++) {                       // bottom half: i+1 stars each side, 2(n-i-1) spaces
            for (int j = 0; j < i + 1; j++) sb.append('*');
            for (int j = 0; j < 2 * (n - i - 1); j++) sb.append(' ');
            for (int j = 0; j < i + 1; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: one loop over all 2n rows; a formula gives the star count, String.repeat builds the row. O(n^2) time. */
    static String formula(int n) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 2 * n; r++) {
            int stars = r < n ? n - r : r - n + 1;          // n, n-1, ..., 1, 1, ..., n-1, n
            String side = "*".repeat(stars);
            sb.append(side).append(" ".repeat(2 * (n - stars))).append(side).append('\n');
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(formula(n).equals(expected), "formula(" + n + ") gave\n" + formula(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "**\n**\n");
        verify(2, "****\n*  *\n*  *\n****\n");
        verify(3, "******\n**  **\n*    *\n*    *\n**  **\n******\n");

        // Structural checks: 2n rows of width 2n, each row a palindrome, mirror symmetry top/bottom.
        for (int n = 1; n <= 20; n++) {
            String s = nestedLoops(n);
            check(s.equals(formula(n)), "approaches differ for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == 2 * n, "row count for n = " + n);
            for (int r = 0; r < 2 * n; r++) {
                String row = rows[r];
                check(row.length() == 2 * n, "row width for n = " + n + ", row " + r);
                check(row.equals(rows[2 * n - 1 - r]), "top and bottom halves must mirror, n = " + n);
                check(new StringBuilder(row).reverse().toString().equals(row), "each row is a palindrome, n = " + n);
                int stars = row.length() - row.replace("*", "").length();
                int expectStars = r < n ? 2 * (n - r) : 2 * (r - n + 1);
                check(stars == expectStars, "star count in row " + r + " for n = " + n);
                check(row.charAt(0) == '*' && row.charAt(2 * n - 1) == '*', "rows start and end with a star");
            }
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P411_Pattern19");
    }
}
