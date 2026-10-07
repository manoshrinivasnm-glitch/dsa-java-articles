import java.util.*;
import java.util.stream.*;

/**
 * TUF 417 - Pattern 4 (Right-angled number pyramid II).
 * Row i (1-based) holds the number i written i times, separated by single spaces.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P417_Pattern4 {

    /** Approach 1: the inner loop only counts; the value printed is the outer index i. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (j > 1) sb.append(' ');
                sb.append(i);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: a row is one token repeated i times, so build it with nCopies and join. O(n^2) time. */
    static String repeatToken(int n) {
        StringBuilder out = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            String token = Integer.toString(i);
            out.append(String.join(" ", Collections.nCopies(i, token))).append('\n');
        }
        return out.toString();
    }

    /** Approach 3: the same idea written as a stream pipeline over the row numbers. O(n^2) time. */
    static String streams(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(i -> String.join(" ", Collections.nCopies(i, Integer.toString(i))) + "\n")
                .collect(Collectors.joining());
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(nestedLoops(n).equals(expected), "nestedLoops(" + n + ") gave\n" + nestedLoops(n));
        check(repeatToken(n).equals(expected), "repeatToken(" + n + ") gave\n" + repeatToken(n));
        check(streams(n).equals(expected), "streams(" + n + ") gave\n" + streams(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "1\n");                                // edge: one number, no separator
        verify(2, "1\n2 2\n");
        verify(3, "1\n2 2\n3 3 3\n");
        verify(4, "1\n2 2\n3 3 3\n4 4 4 4\n");

        // Structural checks for larger n: row i has i tokens and every token equals i.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 1; i <= n; i++) {
                String[] tokens = rows[i - 1].split(" ");
                check(tokens.length == i, "token count for n = " + n + ", row " + i);
                for (String t : tokens) check(Integer.parseInt(t) == i, "token in row " + i + " for n = " + n);
            }
        }
        // Two-digit numbers must stay separated by single spaces.
        check(nestedLoops(10).endsWith("10 10 10 10 10 10 10 10 10 10\n"), "two-digit row");

        System.out.print(nestedLoops(5));
        System.out.println("OK P417_Pattern4");
    }
}
