import java.util.*;

/**
 * TUF 406 - Pattern 14 (Increasing Letter Triangle).
 * Row i (1-based) prints the first i capital letters separated by single spaces:
 * A / A B / A B C / A B C D / ...
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P406_Pattern14 {

    /** Approach 1: nested loops with char arithmetic ('A' + j). O(n^2) time, O(n^2) output. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (j > 0) sb.append(' ');
                sb.append((char) ('A' + j));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: build the longest row once; every other row is a prefix of it. O(n^2) time. */
    static String prefixOfLastRow(int n) {
        StringBuilder full = new StringBuilder();          // "A B C ... " with n letters
        for (int j = 0; j < n; j++) {
            if (j > 0) full.append(' ');
            full.append((char) ('A' + j));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(full, 0, 2 * i - 1).append('\n');   // i letters and i-1 spaces
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(prefixOfLastRow(n).equals(expected), "prefixOfLastRow(" + n + ") gave\n" + prefixOfLastRow(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "A\n");
        verify(2, "A\nA B\n");
        verify(3, "A\nA B\nA B C\n");
        verify(4, "A\nA B\nA B C\nA B C D\n");

        // Structural checks for every alphabet size up to 26.
        for (int n = 1; n <= 26; n++) {
            String s = nestedLoops(n);
            check(s.equals(prefixOfLastRow(n)), "approaches differ for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                check(rows[i].length() == 2 * i + 1, "row width for n = " + n + ", row " + i);
                check(rows[i].charAt(0) == 'A', "every row starts with A");
                check(rows[i].charAt(rows[i].length() - 1) == (char) ('A' + i), "row " + i + " must end with letter " + i);
            }
        }
        check(nestedLoops(26).endsWith("Y Z\n"), "26 rows end with Z");

        System.out.print(nestedLoops(5));
        System.out.println("OK P406_Pattern14");
    }
}
