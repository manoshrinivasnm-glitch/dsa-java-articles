import java.util.*;
import java.util.stream.*;

/**
 * TUF 412 - Pattern 2 (Right-angled triangle).
 * Row i (1-based) holds i stars separated by single spaces, so row i is 2i-1 characters wide.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P412_Pattern2 {

    /** Approach 1: row i prints i stars; the inner loop bound depends on the outer index. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (j > 1) sb.append(' ');
                sb.append('*');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: each row is the previous row plus one more star, so grow one buffer and copy it out. O(n^2) time. */
    static String growingRow(int n) {
        StringBuilder out = new StringBuilder();
        StringBuilder row = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            if (i > 1) row.append(' ');
            row.append('*');
            out.append(row).append('\n');
        }
        return out.toString();
    }

    /** Approach 3: describe each row declaratively and join them with a stream. O(n^2) time. */
    static String streams(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(i -> String.join(" ", Collections.nCopies(i, "*")) + "\n")
                .collect(Collectors.joining());
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(growingRow(n).equals(expected), "growingRow(" + n + ") gave\n" + growingRow(n));
        check(streams(n).equals(expected), "streams(" + n + ") gave\n" + streams(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "*\n");                                // edge: one star, no separator
        verify(2, "*\n* *\n");
        verify(3, "*\n* *\n* * *\n");
        verify(4, "*\n* *\n* * *\n* * * *\n");

        // Structural checks for larger n: row i (1-based) is 2i-1 wide and holds exactly i stars.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 1; i <= n; i++) {
                String row = rows[i - 1];
                check(row.length() == 2 * i - 1, "row width for n = " + n + ", row " + i);
                check(row.chars().filter(ch -> ch == '*').count() == i, "star count for n = " + n + ", row " + i);
                check(row.charAt(0) == '*' && row.charAt(row.length() - 1) == '*', "row must start and end with a star");
            }
        }

        System.out.print(nestedLoops(5));
        System.out.println("OK P412_Pattern2");
    }
}
