import java.util.*;

/**
 * TUF 410 - Pattern 18 (Alpha-Triangle).
 * Every row ends with the n-th letter; row i (1-based) starts i letters before it, so the rows grow leftwards:
 * E / D E / C D E / B C D E / A B C D E   (letters separated by single spaces)
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P410_Pattern18 {

    /** Approach 1: nested loops; row i runs from letter n-i to letter n-1 (0-based). O(n^2) time, O(n^2) output. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {          // row i shows the last i of the first n letters
            for (int j = n - i; j < n; j++) {   // from letter n-i up to letter n-1 (0-based)
                if (j > n - i) sb.append(' ');  // space before every letter except the first
                sb.append((char) ('A' + j));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: each row is the previous row with one more letter glued on the front. O(n^2) time. */
    static String growLeft(int n) {
        StringBuilder sb = new StringBuilder();
        StringBuilder row = new StringBuilder();  // current row, grown at its left end
        for (int i = 1; i <= n; i++) {
            char ch = (char) ('A' + n - i);       // the new letter, one step earlier in the alphabet each row
            if (i > 1) row.insert(0, ' ');        // separate it from the letters already there
            row.insert(0, ch);                    // prepend the new letter; the rest of the row is reused
            sb.append(row).append('\n');          // emit the row as it stands now
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(growLeft(n).equals(expected), "growLeft(" + n + ") gave\n" + growLeft(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "A\n");
        verify(2, "B\nA B\n");
        verify(3, "C\nB C\nA B C\n");
        verify(4, "D\nC D\nB C D\nA B C D\n");

        // Structural checks for every alphabet size up to 26.
        for (int n = 1; n <= 26; n++) {
            String s = nestedLoops(n);
            check(s.equals(growLeft(n)), "approaches differ for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            char last = (char) ('A' + n - 1);
            for (int i = 0; i < n; i++) {
                check(rows[i].length() == 2 * i + 1, "row width for n = " + n + ", row " + i);
                check(rows[i].charAt(rows[i].length() - 1) == last, "every row ends with the n-th letter");
                check(rows[i].charAt(0) == (char) ('A' + n - 1 - i), "row " + i + " starts with the wrong letter");
            }
            check(rows[n - 1].charAt(0) == 'A', "last row starts with A");
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P410_Pattern18");
    }
}
