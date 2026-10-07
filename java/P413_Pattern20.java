import java.util.*;

/**
 * TUF 413 - Pattern 20 (Symmetric-Butterfly).
 * 2n-1 rows, each 2n characters wide. Row r has s stars on each side with a gap of 2(n-s) spaces between,
 * where s grows 1..n and then shrinks back to 1; the middle row is solid.
 * *        *
 * **      **
 * ***    ***
 * ****  ****
 * **********
 * ****  ****
 * ***    ***
 * **      **
 * *        *
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P413_Pattern20 {

    /** Approach 1: two loops, one for the growing upper wing and one for the shrinking lower wing. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {                      // upper wing, i stars each side
            for (int j = 0; j < i; j++) sb.append('*');
            for (int j = 0; j < 2 * (n - i); j++) sb.append(' ');
            for (int j = 0; j < i; j++) sb.append('*');
            sb.append('\n');
        }
        for (int i = n - 1; i >= 1; i--) {                  // lower wing, i stars each side
            for (int j = 0; j < i; j++) sb.append('*');
            for (int j = 0; j < 2 * (n - i); j++) sb.append(' ');
            for (int j = 0; j < i; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: one loop over 2n-1 rows; stars = n - |n - r| folds both wings into one formula. O(n^2) time. */
    static String formula(int n) {
        StringBuilder sb = new StringBuilder();
        for (int r = 1; r <= 2 * n - 1; r++) {
            int stars = n - Math.abs(n - r);                // 1, 2, ..., n, ..., 2, 1
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
        verify(1, "**\n");
        verify(2, "*  *\n****\n*  *\n");
        verify(3, "*    *\n**  **\n******\n**  **\n*    *\n");

        // Structural checks: 2n-1 rows of width 2n, palindromic rows, vertical mirror symmetry, solid middle row.
        for (int n = 1; n <= 20; n++) {
            String s = nestedLoops(n);
            check(s.equals(formula(n)), "approaches differ for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == 2 * n - 1, "row count for n = " + n);
            check(rows[n - 1].equals("*".repeat(2 * n)), "middle row is solid for n = " + n);
            for (int r = 0; r < 2 * n - 1; r++) {
                String row = rows[r];
                check(row.length() == 2 * n, "row width for n = " + n + ", row " + r);
                check(row.equals(rows[2 * n - 2 - r]), "upper and lower wings must mirror, n = " + n);
                check(new StringBuilder(row).reverse().toString().equals(row), "each row is a palindrome, n = " + n);
                int stars = row.length() - row.replace("*", "").length();
                int side = r < n ? r + 1 : 2 * n - 1 - r;
                check(stars == 2 * side, "star count in row " + r + " for n = " + n);
                check(row.charAt(0) == '*' && row.charAt(2 * n - 1) == '*', "rows start and end with a star");
            }
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P413_Pattern20");
    }
}
