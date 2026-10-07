import java.util.*;

/**
 * TUF 407 - Pattern 15 (Reverse Letter Triangle).
 * Row i (1-based) prints the first n-i+1 capital letters separated by single spaces, so the rows shrink:
 * A B C D E / A B C D / A B C / A B / A
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P407_Pattern15 {

    /** Approach 1: nested loops, the inner bound shrinks with the row. O(n^2) time, O(n^2) output. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            int letters = n - i + 1;
            for (int j = 0; j < letters; j++) {
                if (j > 0) sb.append(' ');
                sb.append((char) ('A' + j));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: build the full first row, then chop two characters (space + letter) off its end for each later row. O(n^2) time. */
    static String shrinking(int n) {
        StringBuilder row = new StringBuilder();           // "A B C ... " with n letters
        for (int j = 0; j < n; j++) {
            if (j > 0) row.append(' ');
            row.append((char) ('A' + j));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(row).append('\n');
            if (row.length() >= 2) row.setLength(row.length() - 2);   // drop " X"
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(shrinking(n).equals(expected), "shrinking(" + n + ") gave\n" + shrinking(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "A\n");
        verify(2, "A B\nA\n");
        verify(3, "A B C\nA B\nA\n");
        verify(4, "A B C D\nA B C\nA B\nA\n");

        // Structural checks for every alphabet size up to 26.
        for (int n = 1; n <= 26; n++) {
            String s = nestedLoops(n);
            check(s.equals(shrinking(n)), "approaches differ for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                int letters = n - i;
                check(rows[i].length() == 2 * letters - 1, "row width for n = " + n + ", row " + i);
                check(rows[i].charAt(0) == 'A', "every row starts with A");
                check(rows[i].charAt(rows[i].length() - 1) == (char) ('A' + letters - 1), "row " + i + " ends with the wrong letter");
            }
            check(rows[n - 1].equals("A"), "last row is a single A");
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P407_Pattern15");
    }
}
