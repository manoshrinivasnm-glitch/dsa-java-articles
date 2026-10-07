import java.util.*;

/**
 * TUF 422 - Pattern 9 (Diamond star pattern).
 * The star pyramid of Pattern 7 followed by the inverted pyramid of Pattern 8: 2n rows, the two middle rows both 2n-1 stars wide.
 * Row r (0-based) uses k = r for r < n and k = 2n-1-r otherwise, then prints n-1-k spaces and 2k+1 stars. No trailing spaces.
 * Each method returns the whole pattern as one String, each line ending in '\n'.
 */
public class P422_Pattern9 {

    /** Approach 1: two blocks of nested loops, the widening upper half then the narrowing lower half. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {                    // upper half: row i has 2i+1 stars
            for (int j = 0; j < n - 1 - i; j++) sb.append(' ');
            for (int j = 0; j < 2 * i + 1; j++) sb.append('*');
            sb.append('\n');
        }
        for (int i = 0; i < n; i++) {                    // lower half: row i has 2(n-i)-1 stars
            for (int j = 0; j < i; j++) sb.append(' ');
            for (int j = 0; j < 2 * (n - i) - 1; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: build the upper half once, then emit it forwards and backwards. O(n^2) time. */
    static String mirror(int n) {
        List<String> upper = new ArrayList<>();
        for (int i = 0; i < n; i++) upper.add(" ".repeat(n - 1 - i) + "*".repeat(2 * i + 1));
        StringBuilder out = new StringBuilder();
        for (String row : upper) out.append(row).append('\n');
        for (int i = n - 1; i >= 0; i--) out.append(upper.get(i)).append('\n');
        return out.toString();
    }

    /** Approach 3: one loop over all 2n rows; fold the row index so k rises 0..n-1 and then falls n-1..0. O(n^2) time. */
    static String foldedIndex(int n) {
        StringBuilder out = new StringBuilder();
        for (int r = 0; r < 2 * n; r++) {
            int k = r < n ? r : 2 * n - 1 - r;           // distance from the nearer tip
            out.append(" ".repeat(n - 1 - k)).append("*".repeat(2 * k + 1)).append('\n');
        }
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(mirror(n).equals(expected), "mirror(" + n + ") gave\n" + mirror(n));
        check(foldedIndex(n).equals(expected), "foldedIndex(" + n + ") gave\n" + foldedIndex(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "*\n*\n");                             // edge: the two tips touch, two single stars
        verify(2, " *\n***\n***\n *\n");
        verify(3, "  *\n ***\n*****\n*****\n ***\n  *\n");
        verify(4, "   *\n  ***\n *****\n*******\n*******\n *****\n  ***\n   *\n");

        // Structural checks for larger n: 2n rows, vertical symmetry, two full middle rows, no trailing spaces.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == 2 * n, "row count for n = " + n);
            for (int r = 0; r < 2 * n; r++) {
                check(rows[r].equals(rows[2 * n - 1 - r]), "row " + r + " must mirror row " + (2 * n - 1 - r) + " for n = " + n);
                check(rows[r].charAt(rows[r].length() - 1) == '*', "no trailing space for n = " + n + ", row " + r);
            }
            check(rows[n - 1].equals("*".repeat(2 * n - 1)) && rows[n].equals("*".repeat(2 * n - 1)), "middle rows for n = " + n);
            check(rows[0].equals(" ".repeat(n - 1) + "*"), "top tip for n = " + n);
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P422_Pattern9");
    }
}
