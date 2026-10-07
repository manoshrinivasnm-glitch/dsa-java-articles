import java.util.stream.*;

/**
 * TUF 402 - Pattern 10 (Half diamond star pattern).
 * 2n-1 rows with 1, 2, ..., n, n-1, ..., 1 stars and no spaces: a triangle that grows to n and then shrinks back.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P402_Pattern10 {

    /** Approach 1: one loop climbs to n stars, a second loop descends from n-1 back to 1. O(n^2) time. */
    static String twoLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) sb.append('*');
            sb.append('\n');
        }
        for (int i = n - 1; i >= 1; i--) {
            for (int j = 0; j < i; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: one loop over the 2n-1 rows; the star count is i while climbing and 2n-i while descending. O(n^2) time. */
    static String foldedIndex(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 2 * n - 1; i++) {
            int stars = i <= n ? i : 2 * n - i;
            for (int j = 0; j < stars; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 3: the same fold written as Math.min(i, 2n - i) inside a stream pipeline. O(n^2) time. */
    static String streams(int n) {
        return IntStream.rangeClosed(1, 2 * n - 1)
                .map(i -> Math.min(i, 2 * n - i))
                .mapToObj(stars -> "*".repeat(stars) + "\n")
                .collect(Collectors.joining());
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(twoLoops(n).equals(expected), "twoLoops(" + n + ") gave\n" + twoLoops(n));
        check(foldedIndex(n).equals(expected), "foldedIndex(" + n + ") gave\n" + foldedIndex(n));
        check(streams(n).equals(expected), "streams(" + n + ") gave\n" + streams(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "*\n");                                // edge: 2n-1 = 1 row, the climb and descent share it
        verify(2, "*\n**\n*\n");
        verify(3, "*\n**\n***\n**\n*\n");
        verify(4, "*\n**\n***\n****\n***\n**\n*\n");

        // Structural checks for larger n: 2n-1 rows, row i (0-based) has min(i+1, 2n-1-i) stars, vertical symmetry.
        for (int n = 5; n <= 12; n++) {
            String s = twoLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == 2 * n - 1, "row count for n = " + n);
            for (int i = 0; i < rows.length; i++) {
                check(rows[i].length() == Math.min(i + 1, 2 * n - 1 - i), "row width for n = " + n + ", row " + i);
                check(rows[i].equals(rows[2 * n - 2 - i]), "row " + i + " must mirror row " + (2 * n - 2 - i) + " for n = " + n);
                check(rows[i].indexOf(' ') < 0, "no spaces anywhere for n = " + n);
            }
            check(rows[n - 1].equals("*".repeat(n)), "widest row for n = " + n);
        }

        System.out.print(twoLoops(5));
        System.out.println("OK P402_Pattern10");
    }
}
