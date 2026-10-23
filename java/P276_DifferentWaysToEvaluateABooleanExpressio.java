import java.util.*;

/** TUF 276 - Different Ways to Evaluate a Boolean Expression. Count bracketings of T/F with &, |, ^ that give true, mod 1e9+7. */
public class P276_DifferentWaysToEvaluateABooleanExpressio {

    static final long MOD = 1_000_000_007L;                     // counts are reported modulo 1e9+7

    /** Ways the combined expression equals wantTrue, given true/false counts of the left and right parts. */
    static long combine(char op, boolean wantTrue, long lt, long lf, long rt, long rf) {
        // ways for each (left value, right value) pair, e.g. tf = left true and right false
        long tt = lt * rt % MOD, tf = lt * rf % MOD, ft = lf * rt % MOD, ff = lf * rf % MOD;
        long ways = switch (op) {
            case '&' -> wantTrue ? tt : tf + ft + ff;           // '&': true only when both sides are true
            case '|' -> wantTrue ? tt + tf + ft : ff;           // '|': false only when both sides are false
            default -> wantTrue ? tf + ft : tt + ff;            // '^': true when the sides differ
        };
        return ways % MOD;
    }

    /** Approach 1: split at every operator, counting true and false ways of each side. Exponential time, O(n) stack. */
    static int recursive(String exp) {
        return (int) solve(0, exp.length() - 1, true, exp);     // count the ways the whole string becomes true
    }

    static long solve(int i, int j, boolean wantTrue, String exp) {
        if (i == j) return (exp.charAt(i) == 'T') == wantTrue ? 1 : 0;   // one operand: 1 if it matches, or 0
        long ways = 0;                                          // ways summed over every last operator
        for (int k = i + 1; k < j; k += 2) {                    // exp[k] is the operator evaluated last
            long lt = solve(i, k - 1, true, exp), lf = solve(i, k - 1, false, exp);   // ways the left part is T / F
            long rt = solve(k + 1, j, true, exp), rf = solve(k + 1, j, false, exp);   // ways the right part is T / F
            // add the ways this operator gives the wanted value
            ways = (ways + combine(exp.charAt(k), wantTrue, lt, lf, rt, rf)) % MOD;
        }
        return ways;
    }

    /** Approach 2: cache (i, j, wantTrue). O(n^3) time, O(n^2) space. */
    static int memoization(String exp) {
        int n = exp.length();
        long[][][] dp = new long[n][n][2];                      // [i][j][1] = ways true, [i][j][0] = ways false
        for (long[][] plane : dp) for (long[] row : plane) Arrays.fill(row, -1);   // -1 = not solved yet
        return (int) memo(0, n - 1, 1, exp, dp);                // count the ways the whole string becomes true
    }

    static long memo(int i, int j, int want, String exp, long[][][] dp) {
        if (i == j) return (exp.charAt(i) == 'T') == (want == 1) ? 1 : 0;   // one operand: 1 if it matches, or 0
        if (dp[i][j][want] != -1) return dp[i][j][want];        // already solved: reuse it
        long ways = 0;
        for (int k = i + 1; k < j; k += 2) {                    // exp[k] is the operator evaluated last
            long lt = memo(i, k - 1, 1, exp, dp), lf = memo(i, k - 1, 0, exp, dp);   // ways the left part is T / F
            long rt = memo(k + 1, j, 1, exp, dp), rf = memo(k + 1, j, 0, exp, dp);   // ways the right part is T / F
            // add the ways this operator gives the wanted value
            ways = (ways + combine(exp.charAt(k), want == 1, lt, lf, rt, rf)) % MOD;
        }
        return dp[i][j][want] = ways;                           // store the answer before returning it
    }

    /** Approach 3: bottom-up over operand positions i (descending) and j (ascending). O(n^3) time, O(n^2) space. */
    static int tabulation(String exp) {
        int n = exp.length();
        long[][][] dp = new long[n][n][2];                      // [i][j][1] = ways true, [i][j][0] = ways false
        for (int i = n - 1; i >= 0; i -= 2) {                   // operands sit at even positions; i goes down
            dp[i][i][1] = exp.charAt(i) == 'T' ? 1 : 0;         // a single operand: T is true one way
            dp[i][i][0] = exp.charAt(i) == 'F' ? 1 : 0;         // F is false one way
            for (int j = i + 2; j < n; j += 2) {                // end operand j; shorter spans come first
                for (int want = 0; want <= 1; want++) {         // fill both "true" and "false" counts
                    long ways = 0;
                    for (int k = i + 1; k < j; k += 2) {        // exp[k] is the operator evaluated last
                        // both parts are shorter spans, so their counts are already filled
                        ways = (ways + combine(exp.charAt(k), want == 1,
                                dp[i][k - 1][1], dp[i][k - 1][0], dp[k + 1][j][1], dp[k + 1][j][0])) % MOD;
                    }
                    dp[i][j][want] = ways;
                }
            }
        }
        return (int) dp[0][n - 1][1];                           // ways the whole string becomes true
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String exp, int expected, boolean small) {
        if (small) check(recursive(exp) == expected, "recursive failed for " + exp);
        check(memoization(exp) == expected, "memoization failed for " + exp);
        check(tabulation(exp) == expected, "tabulation failed for " + exp);
    }

    public static void main(String[] args) {
        verify("T|T&F^T", 4, true);                             // 4 of the 5 bracketings are true
        verify("T^F|F", 2, true);                               // both bracketings are true
        verify("F|T^F", 2, true);
        verify("T^T^F", 0, true);                               // every bracketing is false
        verify("T", 1, true);                                   // edge: a single operand
        verify("F", 0, true);
        verify("F&F&F|T", 2, true);                             // true only when the | is applied last
        // 60 operands joined by '|': every one of Catalan(59) bracketings is true.
        StringBuilder sb = new StringBuilder("T");
        for (int i = 1; i < 60; i++) sb.append("|T");
        long[] cat = new long[60];
        cat[0] = 1;
        for (int m = 1; m < 60; m++) for (int a = 0; a < m; a++) cat[m] = (cat[m] + cat[a] * cat[m - 1 - a]) % MOD;
        verify(sb.toString(), (int) cat[59], false);

        Random rnd = new Random(276);
        String ops = "&|^";
        for (int t = 0; t < 200; t++) {
            int operands = 1 + rnd.nextInt(7);
            StringBuilder e = new StringBuilder();
            for (int i = 0; i < operands; i++) {
                if (i > 0) e.append(ops.charAt(rnd.nextInt(3)));
                e.append(rnd.nextBoolean() ? 'T' : 'F');
            }
            verify(e.toString(), recursive(e.toString()), true);
        }
        System.out.println("OK P276_DifferentWaysToEvaluateABooleanExpressio");
    }
}
