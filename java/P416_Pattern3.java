import java.util.*;
import java.util.stream.*;

/**
 * TUF 416 - Pattern 3 (Right-angled number pyramid).
 * Row i (1-based) holds the numbers 1 2 ... i separated by single spaces.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P416_Pattern3 {

    /** Approach 1: the inner loop counter j is exactly the value to print. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (j > 1) sb.append(' ');
                sb.append(j);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: row i is row i-1 followed by " i", so extend one buffer and copy it out each time. O(n^2) time. */
    static String growingRow(int n) {
        StringBuilder out = new StringBuilder();
        StringBuilder row = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            if (i > 1) row.append(' ');
            row.append(i);
            out.append(row).append('\n');
        }
        return out.toString();
    }

    /** Approach 3: build each row from an IntStream of 1..i and join the rows. O(n^2) time. */
    static String streams(int n) {
        return IntStream.rangeClosed(1, n)
                .mapToObj(i -> IntStream.rangeClosed(1, i)
                        .mapToObj(j -> Integer.toString(j))
                        .collect(Collectors.joining(" ")) + "\n")
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
        verify(1, "1\n");                                // edge: one number, no separator
        verify(2, "1\n1 2\n");
        verify(3, "1\n1 2\n1 2 3\n");
        verify(4, "1\n1 2\n1 2 3\n1 2 3 4\n");

        // Structural checks for larger n: row i has i tokens and token j equals j.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 1; i <= n; i++) {
                String[] tokens = rows[i - 1].split(" ");
                check(tokens.length == i, "token count for n = " + n + ", row " + i);
                for (int j = 1; j <= i; j++) {
                    check(Integer.parseInt(tokens[j - 1]) == j, "token " + j + " in row " + i + " for n = " + n);
                }
            }
        }
        // Two-digit numbers must stay separated by single spaces (no fixed-width assumptions).
        check(nestedLoops(11).endsWith("1 2 3 4 5 6 7 8 9 10 11\n"), "two-digit row");

        System.out.print(nestedLoops(5));
        System.out.println("OK P416_Pattern3");
    }
}
