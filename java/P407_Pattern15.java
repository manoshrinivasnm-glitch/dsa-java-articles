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
        for (int i = 1; i <= n; i++) {           // rows get one letter shorter going down
            int letters = n - i + 1;             // row i has n-i+1 letters: n, n-1, ..., 1
            for (int j = 0; j < letters; j++) {  // always start from A
                if (j > 0) sb.append(' ');       // space between letters, none before the first
                sb.append((char) ('A' + j));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: build the full first row, then chop two characters (space + letter) off its end for each later row. O(n^2) time. */
    static String shrinking(int n) {
        StringBuilder row = new StringBuilder();  // current row; starts as the widest one "A B C ..."
        for (int j = 0; j < n; j++) {             // build that first row with all n letters
            if (j > 0) row.append(' ');           // space between letters, none before the first
            row.append((char) ('A' + j));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {            // n rows in total
            sb.append(row).append('\n');          // emit the current row
            // drop the trailing " X" so the next row is one letter shorter
            if (row.length() >= 2) row.setLength(row.length() - 2);
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
