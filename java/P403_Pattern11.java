/**
 * TUF 403 - Pattern 11 (Binary number triangle).
 * Row i (0-based) holds i+1 digits that alternate between 1 and 0; even rows start with 1, odd rows start with 0,
 * so every row ends with 1 and the digit at (i, j) is 1 exactly when i + j is even. Digits are printed without separators.
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P403_Pattern11 {

    /** Approach 1: keep a running bit, reset it at the start of each row and flip it after every print. O(n^2) time. */
    static String toggle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int bit = (i % 2 == 0) ? 1 : 0;              // even rows start with 1, odd rows with 0
            for (int j = 0; j <= i; j++) {
                sb.append(bit);
                bit = 1 - bit;                           // flip for the next column
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 2: the digit at (i, j) is 1 exactly when i + j is even, so compute it straight from the indices. O(n^2) time. */
    static String parityFormula(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                sb.append((i + j) % 2 == 0 ? '1' : '0');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Approach 3: every row is a suffix of the last row ("10101" for n = 5), so build the last row once and slice it. O(n^2) time. */
    static String suffixes(int n) {
        StringBuilder last = new StringBuilder();
        for (int k = 0; k < n; k++) last.append((n - 1 - k) % 2 == 0 ? '1' : '0');   // ends with 1, alternates leftwards
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < n; i++) out.append(last, n - 1 - i, n).append('\n');     // the last i+1 characters
        return out.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(toggle(n).equals(expected), "toggle(" + n + ") gave\n" + toggle(n));
        check(parityFormula(n).equals(expected), "parityFormula(" + n + ") gave\n" + parityFormula(n));
        check(suffixes(n).equals(expected), "suffixes(" + n + ") gave\n" + suffixes(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "1\n");                                // edge: a single digit
        verify(2, "1\n01\n");
        verify(3, "1\n01\n101\n");
        verify(4, "1\n01\n101\n0101\n");
        verify(5, "1\n01\n101\n0101\n10101\n");

        // Structural checks for larger n: row i has i+1 digits, starts with 1 on even rows, ends with 1, and alternates.
        for (int n = 6; n <= 12; n++) {
            String s = toggle(n);
            verify(n, s);                                // all three approaches must agree
            String[] rows = s.split("\n");
            check(rows.length == n, "row count for n = " + n);
            for (int i = 0; i < n; i++) {
                String row = rows[i];
                check(row.length() == i + 1, "row width for n = " + n + ", row " + i);
                check(row.charAt(0) == (i % 2 == 0 ? '1' : '0'), "first digit for n = " + n + ", row " + i);
                check(row.charAt(row.length() - 1) == '1', "last digit for n = " + n + ", row " + i);
                for (int j = 1; j < row.length(); j++) {
                    check(row.charAt(j) != row.charAt(j - 1), "digits must alternate for n = " + n + ", row " + i);
                }
            }
        }

        System.out.print(toggle(5));
        System.out.println("OK P403_Pattern11");
    }
}
