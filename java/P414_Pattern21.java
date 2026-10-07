import java.util.*;

/**
 * TUF 414 - Pattern 21 (Hollow Rectangle).
 * An n x n box drawn with stars on the border and spaces inside:
 * *****
 * *   *
 * *   *
 * *   *
 * *****
 * A general rows x cols version is included as well.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P414_Pattern21 {

    /** Approach 1: visit every cell and decide star-or-space from its coordinates. O(n^2) time, O(n^2) output. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                boolean border = i == 0 || i == n - 1 || j == 0 || j == n - 1;
                sb.append(border ? '*' : ' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: only two distinct rows exist (solid and hollow), so build each once and repeat. O(n^2) time. */
    static String withRepeat(int n) {
        if (n <= 0) return "";
        if (n == 1) return "*\n";
        String solid = "*".repeat(n) + "\n";
        String hollow = "*" + " ".repeat(n - 2) + "*\n";
        return solid + hollow.repeat(n - 2) + solid;
    }

    /** General version: rows x cols hollow rectangle, same cell rule as Approach 1. O(rows * cols) time. */
    static String rectangle(int rows, int cols) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean border = i == 0 || i == rows - 1 || j == 0 || j == cols - 1;
                sb.append(border ? '*' : ' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(withRepeat(n).equals(expected), "withRepeat(" + n + ") gave\n" + withRepeat(n));
        check(rectangle(n, n).equals(expected), "rectangle(" + n + ", " + n + ") gave\n" + rectangle(n, n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "*\n");                                // edge: a single cell is all border
        verify(2, "**\n**\n");                           // edge: no interior yet
        verify(3, "***\n* *\n***\n");
        verify(4, "****\n*  *\n*  *\n****\n");
        verify(5, "*****\n*   *\n*   *\n*   *\n*****\n");

        // Non-square rectangles.
        check(rectangle(2, 5).equals("*****\n*****\n"), "2 x 5");
        check(rectangle(3, 5).equals("*****\n*   *\n*****\n"), "3 x 5");
        check(rectangle(4, 2).equals("**\n**\n**\n**\n"), "4 x 2");
        check(rectangle(1, 4).equals("****\n"), "1 x 4");

        // Structural checks: exactly 4n-4 stars for n >= 2, every cell inside the border is a space.
        for (int n = 1; n <= 20; n++) {
            String s = nestedLoops(n);
            check(s.equals(withRepeat(n)), "approaches differ for n = " + n);
            check(s.equals(rectangle(n, n)), "rectangle differs for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            int stars = s.length() - s.replace("*", "").length();
            check(stars == (n == 1 ? 1 : 4 * n - 4), "star count for n = " + n);
            for (int i = 1; i < n - 1; i++) {
                check(rows[i].substring(1, n - 1).isBlank(), "interior must be blank, n = " + n);
            }
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P414_Pattern21");
    }
}
