import java.util.*;

/**
 * TUF 409 - Pattern 17 (Alpha-Hill).
 * Row i (1-based) is centred: n-i leading spaces, then the letters A..(i-th letter) climbing and coming back down,
 * 2i-1 letters in total. Trailing spaces are not emitted.
 *     A
 *    ABA
 *   ABCBA
 *  ABCDCBA
 * ABCDEDCBA
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P409_Pattern17 {

    /** Approach 1: walk 2i-1 columns with a letter that climbs until the middle and then descends. O(n^2) time. */
    static String twoPhase(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int s = 0; s < n - i; s++) sb.append(' ');
            char ch = 'A';
            for (int j = 1; j <= 2 * i - 1; j++) {
                sb.append(ch);
                if (j < i) ch++; else ch--;     // climb for the first i-1 steps, then descend
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: keep the rising half "AB..X" and append its mirror image without the peak. O(n^2) time. */
    static String mirror(int n) {
        StringBuilder sb = new StringBuilder();
        StringBuilder up = new StringBuilder();            // grows by one letter per row
        for (int i = 1; i <= n; i++) {
            up.append((char) ('A' + i - 1));
            String down = new StringBuilder(up).reverse().substring(1);   // "X..BA" minus the peak X
            sb.append(" ".repeat(n - i)).append(up).append(down).append('\n');
        }
        return sb.toString();
    }

    /** Approach 3: the letter at a column is decided by its distance from the row's centre. O(n^2) time. */
    static String formula(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            int centre = i - 1;                            // index of the peak within the 2i-1 letters
            for (int s = 0; s < n - i; s++) sb.append(' ');
            for (int c = 0; c < 2 * i - 1; c++) {
                int dist = Math.abs(c - centre);
                sb.append((char) ('A' + (centre - dist)));
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
        check(twoPhase(n).equals(expected), "twoPhase(" + n + ") gave\n" + twoPhase(n));
        check(mirror(n).equals(expected), "mirror(" + n + ") gave\n" + mirror(n));
        check(formula(n).equals(expected), "formula(" + n + ") gave\n" + formula(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "A\n");
        verify(2, " A\nABA\n");
        verify(3, "  A\n ABA\nABCBA\n");
        verify(4, "   A\n  ABA\n ABCBA\nABCDCBA\n");

        // Structural checks for every alphabet size up to 26.
        for (int n = 1; n <= 26; n++) {
            String s = twoPhase(n);
            check(s.equals(mirror(n)), "mirror differs for n = " + n);
            check(s.equals(formula(n)), "formula differs for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                String row = rows[i];
                check(row.length() == (n - i - 1) + (2 * i + 1), "row width for n = " + n + ", row " + i);
                String letters = row.substring(n - i - 1);
                check(letters.indexOf(' ') < 0, "no spaces inside the letters");
                check(letters.charAt(i) == (char) ('A' + i), "peak letter of row " + i);
                check(new StringBuilder(letters).reverse().toString().equals(letters), "each row is a palindrome");
            }
        }

        System.out.print(twoPhase(5));
        System.out.println("OK P409_Pattern17");
    }
}
