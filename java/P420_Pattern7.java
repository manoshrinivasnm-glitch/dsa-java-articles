/**
 * TUF 420 - Pattern 7 (Star pyramid).
 * Row i (0-based) holds n-1-i leading spaces followed by 2i+1 stars, so the stars are centred and the last row is 2n-1 wide.
 * No trailing spaces are emitted. Each method returns the whole pattern as one String, each line ending in '\n'.
 */
public class P420_Pattern7 {

    /** Approach 1: two inner loops per row, one for the leading spaces and one for the stars. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1 - i; j++) sb.append(' ');
            for (int j = 0; j < 2 * i + 1; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: treat the row as cells 0..n-1+i and print a star exactly when the cell is within i of the centre. O(n^2) time. */
    static String centerDistance(int n) {
        StringBuilder sb = new StringBuilder();
        int mid = n - 1;                                 // column of the top star
        for (int i = 0; i < n; i++) {
            for (int c = 0; c <= mid + i; c++) {
                sb.append(Math.abs(c - mid) <= i ? '*' : ' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 3: String.repeat makes the row description literal, spaces then stars. O(n^2) time. */
    static String repeat(int n) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < n; i++) {
            out.append(" ".repeat(n - 1 - i)).append("*".repeat(2 * i + 1)).append('\n');
        }
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(centerDistance(n).equals(expected), "centerDistance(" + n + ") gave\n" + centerDistance(n));
        check(repeat(n).equals(expected), "repeat(" + n + ") gave\n" + repeat(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "*\n");                                // edge: a single star, no leading space
        verify(2, " *\n***\n");
        verify(3, "  *\n ***\n*****\n");
        verify(4, "   *\n  ***\n *****\n*******\n");

        // Structural checks for larger n: row i is n+i wide, starts with n-1-i spaces, ends with a star.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                String row = rows[i];
                check(row.length() == n + i, "row width for n = " + n + ", row " + i);
                check(row.indexOf('*') == n - 1 - i, "leading spaces for n = " + n + ", row " + i);
                check(row.charAt(row.length() - 1) == '*', "no trailing space for n = " + n + ", row " + i);
                check(row.chars().filter(ch -> ch == '*').count() == 2 * i + 1, "star count for n = " + n + ", row " + i);
            }
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P420_Pattern7");
    }
}
