import java.util.*;

/**
 * TUF 408 - Pattern 16 (Alpha-Ramp).
 * Row i (1-based) prints the i-th capital letter i times, separated by single spaces:
 * A / B B / C C C / D D D D / ...
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P408_Pattern16 {

    /** Approach 1: nested loops; the letter is fixed per row and depends on i only. O(n^2) time, O(n^2) output. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            char ch = (char) ('A' + i - 1);
            for (int j = 0; j < i; j++) {
                if (j > 0) sb.append(' ');
                sb.append(ch);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: one library call per row with String.repeat, then drop the trailing space. O(n^2) time. */
    static String withRepeat(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            char ch = (char) ('A' + i - 1);
            sb.append((ch + " ").repeat(i).stripTrailing()).append('\n');
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
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "A\n");
        verify(2, "A\nB B\n");
        verify(3, "A\nB B\nC C C\n");
        verify(4, "A\nB B\nC C C\nD D D D\n");

        // Structural checks for every alphabet size up to 26.
        for (int n = 1; n <= 26; n++) {
            String s = nestedLoops(n);
            check(s.equals(withRepeat(n)), "approaches differ for n = " + n);
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                check(rows[i].length() == 2 * i + 1, "row width for n = " + n + ", row " + i);
                char expect = (char) ('A' + i);
                for (int k = 0; k < rows[i].length(); k++) {
                    char want = (k % 2 == 0) ? expect : ' ';
                    check(rows[i].charAt(k) == want, "row " + i + " must alternate letter and space");
                }
            }
        }
        check(nestedLoops(26).endsWith("Z\n"), "26 rows end with a row of Z");

        System.out.print(nestedLoops(5));
        System.out.println("OK P408_Pattern16");
    }
}
