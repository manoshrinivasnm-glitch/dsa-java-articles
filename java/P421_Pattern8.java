import java.util.*;

/**
 * TUF 421 - Pattern 8 (Inverted star pyramid).
 * Row i (0-based) holds i leading spaces followed by 2(n-i)-1 stars: the first row is 2n-1 stars, the last row is one centred star.
 * No trailing spaces are emitted. Each method returns the whole pattern as one String, each line ending in '\n'.
 */
public class P421_Pattern8 {

    /** Approach 1: i leading spaces, then 2(n-i)-1 stars, one character per inner-loop step. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) sb.append(' ');
            for (int j = 0; j < 2 * (n - i) - 1; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: String.repeat per row, spaces then stars. O(n^2) time. */
    static String repeat(int n) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < n; i++) {
            out.append(" ".repeat(i)).append("*".repeat(2 * (n - i) - 1)).append('\n');
        }
        return out.toString();
    }

    /** Approach 3: build the rows of the upright pyramid (Pattern 7) and emit them bottom-up. O(n^2) time. */
    static String reversedPyramid(int n) {
        List<String> rows = new ArrayList<>();
        for (int i = 0; i < n; i++) rows.add(" ".repeat(n - 1 - i) + "*".repeat(2 * i + 1));
        StringBuilder out = new StringBuilder();
        for (int i = n - 1; i >= 0; i--) out.append(rows.get(i)).append('\n');
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(repeat(n).equals(expected), "repeat(" + n + ") gave\n" + repeat(n));
        check(reversedPyramid(n).equals(expected), "reversedPyramid(" + n + ") gave\n" + reversedPyramid(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "*\n");                                // edge: a single star
        verify(2, "***\n *\n");
        verify(3, "*****\n ***\n  *\n");
        verify(4, "*******\n *****\n  ***\n   *\n");

        // Structural checks for larger n: row i is 2n-1-i wide, starts with i spaces, ends with a star.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                String row = rows[i];
                check(row.length() == 2 * n - 1 - i, "row width for n = " + n + ", row " + i);
                check(row.indexOf('*') == i, "leading spaces for n = " + n + ", row " + i);
                check(row.charAt(row.length() - 1) == '*', "no trailing space for n = " + n + ", row " + i);
                check(row.chars().filter(ch -> ch == '*').count() == 2 * (n - i) - 1, "star count for n = " + n + ", row " + i);
            }
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P421_Pattern8");
    }
}
