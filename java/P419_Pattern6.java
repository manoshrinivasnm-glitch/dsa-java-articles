import java.util.*;
import java.util.stream.*;

/**
 * TUF 419 - Pattern 6 (Inverted numbered right pyramid).
 * Row i (0-based) holds the numbers 1 2 ... n-i separated by single spaces: the first row counts to n, the last row is "1".
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P419_Pattern6 {

    /** Approach 1: row i counts from 1 up to n - i; the inner counter is the value printed. O(n^2) time. */
    static String nestedLoops(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 1; j <= n - i; j++) {
                if (j > 1) sb.append(' ');
                sb.append(j);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: build the rows of the upright pyramid ("1", "1 2", ...) and emit them longest first. O(n^2) time. */
    static String reversedRows(int n) {
        List<String> rows = new ArrayList<>();
        StringBuilder row = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            if (i > 1) row.append(' ');
            row.append(i);
            rows.add(row.toString());
        }
        StringBuilder out = new StringBuilder();
        for (int i = rows.size() - 1; i >= 0; i--) out.append(rows.get(i)).append('\n');
        return out.toString();
    }

    /** Approach 3: a stream over the row lengths n, n-1, ..., 1, each mapped to "1 2 ... len". O(n^2) time. */
    static String streams(int n) {
        return IntStream.iterate(n, len -> len >= 1, len -> len - 1)
                .mapToObj(len -> IntStream.rangeClosed(1, len)
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
        check(reversedRows(n).equals(expected), "reversedRows(" + n + ") gave\n" + reversedRows(n));
        check(streams(n).equals(expected), "streams(" + n + ") gave\n" + streams(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "1\n");                                // edge: one number, no separator
        verify(2, "1 2\n1\n");
        verify(3, "1 2 3\n1 2\n1\n");
        verify(4, "1 2 3 4\n1 2 3\n1 2\n1\n");

        // Structural checks for larger n: row i (0-based) has n-i tokens and token j equals j.
        for (int n = 5; n <= 12; n++) {
            String s = nestedLoops(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                String[] tokens = rows[i].split(" ");
                check(tokens.length == n - i, "token count for n = " + n + ", row " + i);
                for (int j = 1; j <= n - i; j++) {
                    check(Integer.parseInt(tokens[j - 1]) == j, "token " + j + " in row " + i + " for n = " + n);
                }
            }
        }
        // Two-digit numbers must stay separated by single spaces.
        check(nestedLoops(11).startsWith("1 2 3 4 5 6 7 8 9 10 11\n"), "two-digit row");

        System.out.print(nestedLoops(5));
        System.out.println("OK P419_Pattern6");
    }
}
