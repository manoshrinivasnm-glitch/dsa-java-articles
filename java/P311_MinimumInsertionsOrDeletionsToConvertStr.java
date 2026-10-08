import java.util.*;

/** TUF 311 - Minimum insertions or deletions to convert string a into string b. Only single-character inserts and deletes are allowed; return their minimum total. */
public class P311_MinimumInsertionsOrDeletionsToConvertStr {

    /** Approach 1: recursion on prefix lengths. O(2^(n+m)) time, O(n + m) stack. */
    static int recursive(String a, String b) {
        return solve(a.length(), b.length(), a, b);
    }

    /** Fewest operations that turn the prefix a[0..i) into the prefix b[0..j). */
    static int solve(int i, int j, String a, String b) {
        if (i == 0) return j;                                    // insert all of b[0..j)
        if (j == 0) return i;                                    // delete all of a[0..i)
        if (a.charAt(i - 1) == b.charAt(j - 1)) return solve(i - 1, j - 1, a, b);   // keep the matching last character
        return 1 + Math.min(solve(i - 1, j, a, b),               // delete a[i - 1]
                            solve(i, j - 1, a, b));              // insert b[j - 1] at the end
    }

    /** Approach 2: memoization on (i, j). O(n * m) time, O(n * m) space plus the stack. */
    static int memoization(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int[] row : dp) Arrays.fill(row, -1);
        return memo(n, m, a, b, dp);
    }

    static int memo(int i, int j, String a, String b, int[][] dp) {
        if (i == 0) return j;
        if (j == 0) return i;
        if (dp[i][j] != -1) return dp[i][j];
        if (a.charAt(i - 1) == b.charAt(j - 1)) return dp[i][j] = memo(i - 1, j - 1, a, b, dp);
        return dp[i][j] = 1 + Math.min(memo(i - 1, j, a, b, dp), memo(i, j - 1, a, b, dp));
    }

    /** Approach 3: tabulation with the empty-prefix row and column filled in first. O(n * m) time and space. */
    static int tabulation(String a, String b) {
        int n = a.length(), m = b.length();
        int[][] dp = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) dp[i][0] = i;
        for (int j = 0; j <= m; j++) dp[0][j] = j;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1];
                else dp[i][j] = 1 + Math.min(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[n][m];
    }

    /** Approach 4: two rows; column 0 of each new row is its own base case. O(n * m) time, O(m) space. */
    static int spaceOptimized(String a, String b) {
        int n = a.length(), m = b.length();
        int[] prev = new int[m + 1], cur = new int[m + 1];
        for (int j = 0; j <= m; j++) prev[j] = j;
        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) cur[j] = prev[j - 1];
                else cur[j] = 1 + Math.min(prev[j], cur[j - 1]);
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m];
    }

    /** Approach 5: keep a longest common subsequence, delete the rest of a, insert the rest of b. O(n * m) time, O(m) space. */
    static int viaLcs(String a, String b) {
        int n = a.length(), m = b.length();
        int[] prev = new int[m + 1], cur = new int[m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) cur[j] = 1 + prev[j - 1];
                else cur[j] = Math.max(prev[j], cur[j - 1]);
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        int lcs = prev[m];
        return (n - lcs) + (m - lcs);                            // deletions + insertions
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String a, String b, int expected) {
        String in = "\"" + a + "\" -> \"" + b + "\"";
        check(recursive(a, b) == expected, "recursive failed for " + in);
        check(memoization(a, b) == expected, "memoization failed for " + in);
        check(tabulation(a, b) == expected, "tabulation failed for " + in);
        check(spaceOptimized(a, b) == expected, "spaceOptimized failed for " + in);
        check(viaLcs(a, b) == expected, "viaLcs failed for " + in);
    }

    public static void main(String[] args) {
        verify("heap", "pea", 3);                   // delete h and p, insert p: LCS "ea"
        verify("abcd", "anc", 3);                   // LCS "ac": delete b, d; insert n
        verify("geeksforgeeks", "geeks", 8);        // delete the 8 extra characters
        verify("sea", "eat", 2);                    // delete s, insert t
        verify("abc", "abc", 0);                    // already equal
        verify("", "abc", 3);                       // edge: insert everything
        verify("abc", "", 3);                       // edge: delete everything
        verify("", "", 0);                          // edge: both empty

        // Cross-check every approach on seeded random strings over a small alphabet.
        Random rnd = new Random(311);
        for (int t = 0; t < 200; t++) {
            String a = randomString(rnd, rnd.nextInt(9)), b = randomString(rnd, rnd.nextInt(9));
            verify(a, b, tabulation(a, b));
        }
        System.out.println("OK P311_MinimumInsertionsOrDeletionsToConvertStr");
    }

    static String randomString(Random rnd, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
        return sb.toString();
    }
}
