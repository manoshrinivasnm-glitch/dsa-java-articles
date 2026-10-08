import java.util.*;

/** TUF 307 - Edit Distance (LeetCode 72). Fewest single-character inserts, deletes and replacements that turn s1 into s2. */
public class P307_EditDistance {

    /** Approach 1: recursion on prefix lengths. O(3^(n+m)) time, O(n + m) stack. */
    static int recursive(String s1, String s2) {
        return solve(s1.length(), s2.length(), s1, s2);
    }

    /** Fewest operations that turn s1[0..i) into s2[0..j). */
    static int solve(int i, int j, String s1, String s2) {
        if (i == 0) return j;                                    // insert the j remaining characters of s2
        if (j == 0) return i;                                    // delete the i remaining characters of s1
        if (s1.charAt(i - 1) == s2.charAt(j - 1)) return solve(i - 1, j - 1, s1, s2);   // last characters agree: free
        int insert = solve(i, j - 1, s1, s2);                    // append s2[j - 1]; s2 shrinks
        int delete = solve(i - 1, j, s1, s2);                    // remove s1[i - 1]; s1 shrinks
        int replace = solve(i - 1, j - 1, s1, s2);               // overwrite s1[i - 1] with s2[j - 1]; both shrink
        return 1 + Math.min(insert, Math.min(delete, replace));
    }

    /** Approach 2: memoization on (i, j). O(n * m) time, O(n * m) space plus the stack. */
    static int memoization(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(n, m, s1, s2, dp);
    }

    static int memo(int i, int j, String s1, String s2, int[][] dp) {
        if (i == 0) return j;
        if (j == 0) return i;
        if (dp[i][j] != -1) return dp[i][j];
        if (s1.charAt(i - 1) == s2.charAt(j - 1)) return dp[i][j] = memo(i - 1, j - 1, s1, s2, dp);
        int insert = memo(i, j - 1, s1, s2, dp);
        int delete = memo(i - 1, j, s1, s2, dp);
        int replace = memo(i - 1, j - 1, s1, s2, dp);
        return dp[i][j] = 1 + Math.min(insert, Math.min(delete, replace));
    }

    /** Approach 3: tabulation with row 0 = 0..m and column 0 = 0..n. O(n * m) time and space. */
    static int tabulation(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
                else dp[i][j] = 1 + Math.min(dp[i][j - 1], Math.min(dp[i - 1][j], dp[i - 1][j - 1]));
            }
        }
        return dp[n][m];
    }

    /** Approach 4: two rows; each new row starts with its own base case cur[0] = i. O(n * m) time, O(m) space. */
    static int spaceOptimized(String s1, String s2) {
        int n = s1.length(), m = s2.length();
        int[] prev = new int[m + 1], cur = new int[m + 1];
        for (int j = 0; j <= m; j++) prev[j] = j;
        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            for (int j = 1; j <= m; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) cur[j] = prev[j - 1];
                else cur[j] = 1 + Math.min(cur[j - 1], Math.min(prev[j], prev[j - 1]));
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s1, String s2, int expected) {
        String in = "\"" + s1 + "\" -> \"" + s2 + "\"";
        check(recursive(s1, s2) == expected, "recursive failed for " + in);
        check(memoization(s1, s2) == expected, "memoization failed for " + in);
        check(tabulation(s1, s2) == expected, "tabulation failed for " + in);
        check(spaceOptimized(s1, s2) == expected, "spaceOptimized failed for " + in);
    }

    public static void main(String[] args) {
        verify("horse", "ros", 3);                  // replace h->r, delete r, delete e
        verify("intention", "execution", 5);
        verify("kitten", "sitting", 3);             // k->s, e->i, insert g
        verify("abc", "abc", 0);                    // already equal
        verify("abc", "xyz", 3);                    // three replacements beat delete + insert
        verify("", "abc", 3);                       // edge: insert everything
        verify("abc", "", 3);                       // edge: delete everything
        verify("", "", 0);                          // edge: both empty

        // Cross-check every approach on seeded random strings (short: plain recursion branches three ways).
        Random rnd = new Random(307);
        for (int t = 0; t < 200; t++) {
            String a = randomString(rnd, rnd.nextInt(7)), b = randomString(rnd, rnd.nextInt(7));
            verify(a, b, tabulation(a, b));
        }
        System.out.println("OK P307_EditDistance");
    }

    static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
        return sb.toString();
    }
}
