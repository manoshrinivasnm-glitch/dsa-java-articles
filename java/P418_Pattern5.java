import java.util.*;

/**
 * TUF 418 - Pattern 5 (Inverted right pyramid).
 * Row i (0-based) holds n - i stars separated by single spaces: the first row is full, the last row is one star.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P418_Pattern5 {

    /** Approach 1: the inner bound shrinks with the row index, row i has n - i stars. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {          // one row per i
            for (int j = 0; j < n - i; j++) {  // row i has n - i stars, one fewer each row
                if (j > 0) sb.append(' ');     // a space between stars, none before the first
                sb.append('*');
            }
            sb.append('\n');                   // end the row
        }
        return sb.toString();
    }

    /** Approach 2: start from the full row and chop off " *" (two characters) after emitting each row. O(n^2) time. */
    static String shrinkingRow(int n) {
        StringBuilder out = new StringBuilder();
        // start from the widest row: n stars
        StringBuilder row = new StringBuilder(String.join(" ", Collections.nCopies(n, "*")));
        for (int i = 0; i < n; i++) {                      // one row per pass
            out.append(row).append('\n');                  // emit the current row
            row.setLength(Math.max(0, row.length() - 2));  // drop the last ' *': one star fewer next row
        }
        return out.toString();
    }

    /** Approach 3: this is Pattern 2 upside down, so build the growing rows and emit them in reverse. O(n^2) time. */
    static String reversedTriangle(int n) {
        List<String> rows = new ArrayList<>();                 // the growing rows of Pattern 2
        // row i of Pattern 2: i stars joined by spaces
        for (int i = 1; i <= n; i++) rows.add(String.join(" ", Collections.nCopies(i, "*")));
        Collections.reverse(rows);                             // now the longest row comes first
        StringBuilder out = new StringBuilder();
        for (String row : rows) out.append(row).append('\n');  // emit each row on its own line
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(shrinkingRow(n).equals(expected), "shrinkingRow(" + n + ") gave\n" + shrinkingRow(n));
        check(reversedTriangle(n).equals(expected), "reversedTriangle(" + n + ") gave\n" + reversedTriangle(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "*\n");                                // edge: one star, no separator
        verify(2, "* *\n*\n");
        verify(3, "* * *\n* *\n*\n");
        verify(4, "* * * *\n* * *\n* *\n*\n");

        // Structural checks for larger n: row i (0-based) is 2(n-i)-1 wide and holds n-i stars.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                String row = rows[i];
                check(row.length() == 2 * (n - i) - 1, "row width for n = " + n + ", row " + i);
                check(row.chars().filter(ch -> ch == '*').count() == n - i, "star count for n = " + n + ", row " + i);
                check(row.charAt(row.length() - 1) == '*', "no trailing space for n = " + n + ", row " + i);
            }
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P418_Pattern5");
    }
}
