import java.util.*;

/**
 * TUF 231 - Count Palindromic Subsequences. Count the non-empty subsequences of s that read the same both ways.
 * Subsequences are counted by position, so equal strings from different index sets count separately. Modulo 1e9+7.
 */
public class P231_CountPalindromicSubsequences {

    static final int MOD = 1_000_000_007;

    /** Approach 1: enumerate every non-empty subsequence with a bit mask. O(2^n * n) time. Tiny inputs only. */
    static int bruteForce(String s) {
        int n = s.length();
        if (n > 20) throw new IllegalArgumentException("brute force is for tiny inputs only");
        long count = 0;                                  // palindromic subsequences found
        for (int mask = 1; mask < (1 << n); mask++) {    // every non-empty set of positions
            StringBuilder sb = new StringBuilder();      // the subsequence this mask picks
            for (int i = 0; i < n; i++) {
                if ((mask >> i & 1) == 1) sb.append(s.charAt(i));   // keep s[i] when bit i is set
            }
            String t = sb.toString();
            if (t.equals(sb.reverse().toString())) count++;   // reads the same backwards
        }
        return (int) (count % MOD);
    }

    /** Approach 2: recursion on the interval [i, j] with memoization. O(n^2) time, O(n^2) space. */
    static int memoized(String s) {
        int n = s.length();
        if (n == 0) return 0;                            // empty string has no subsequences
        int[][] memo = new int[n][n];                    // memo[i][j] = answer for s[i..j]
        for (int[] row : memo) Arrays.fill(row, -1);     // -1 = not computed yet
        return count(s, 0, n - 1, memo);                 // answer for the whole string
    }

    static int count(String s, int i, int j, int[][] memo) {
        if (i > j) return 0;                             // empty interval: nothing to count
        if (i == j) return 1;                            // a single char is one palindrome
        if (memo[i][j] != -1) return memo[i][j];         // solved before: reuse it
        long res;                                        // long so the sum cannot overflow before % MOD
        if (s.charAt(i) == s.charAt(j)) {                // equal ends can wrap every inner palindrome
            // inner is counted twice: once as is, once wrapped as s[i] + inner + s[j]; +1 for s[i]s[j]
            res = (long) count(s, i + 1, j, memo) + count(s, i, j - 1, memo) + 1;
        } else {                                         // different ends: inclusion-exclusion
            // drop s[i] or drop s[j], minus the inner part s[i+1..j-1] that both of them counted
            res = (long) count(s, i + 1, j, memo) + count(s, i, j - 1, memo)
                    - count(s, i + 1, j - 1, memo) + MOD;   // + MOD keeps the value non-negative
        }
        return memo[i][j] = (int) (res % MOD);           // store, then return
    }

    /** Approach 3: the same recurrence filled bottom-up, shorter intervals first. O(n^2) time and space. */
    static int tabulation(String s) {
        int n = s.length();
        if (n == 0) return 0;                          // empty string has no subsequences
        int[][] dp = new int[n][n];                    // dp[i][j] = answer for s[i..j]
        for (int i = n - 1; i >= 0; i--) {             // i from the right, so row i + 1 is ready
            dp[i][i] = 1;                              // a single char is one palindrome
            for (int j = i + 1; j < n; j++) {          // j to the right, so dp[i][j - 1] is ready
                long inner = (i + 1 <= j - 1) ? dp[i + 1][j - 1] : 0;   // s[i+1..j-1], or 0 if it is empty
                long v;
                // equal ends: the doubled inner count covers wrapping with s[i], s[j]; +1 for the pair alone
                if (s.charAt(i) == s.charAt(j)) v = (long) dp[i + 1][j] + dp[i][j - 1] + 1;
                else v = (long) dp[i + 1][j] + dp[i][j - 1] - inner + MOD;   // remove the double count
                dp[i][j] = (int) (v % MOD);
            }
        }
        return dp[0][n - 1];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        if (s.length() <= 20) check(bruteForce(s) == expected, "bruteForce " + s);
        check(memoized(s) == expected, "memoized " + s);
        check(tabulation(s) == expected, "tabulation " + s);
    }

    public static void main(String[] args) {
        verify("abcd", 4);                    // only the four single letters
        verify("aab", 4);                     // a, a, b, aa
        verify("aaaa", 15);                   // every non-empty subset of 4 equal letters
        verify("abcb", 6);                    // a, b, c, b, bb, bcb
        verify("a", 1);
        verify("", 0);                        // edge: empty string has no non-empty subsequence

        long expectedAll = 1;                 // n equal letters: 2^n - 1 palindromes
        for (int i = 0; i < 1000; i++) expectedAll = expectedAll * 2 % MOD;
        verify("a".repeat(1000), (int) ((expectedAll - 1 + MOD) % MOD));

        Random rnd = new Random(231);
        for (int trial = 0; trial < 300; trial++) {
            int n = rnd.nextInt(13);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) sb.append((char) ('a' + rnd.nextInt(3)));
            String s = sb.toString();
            int b = bruteForce(s);
            check(memoized(s) == b && tabulation(s) == b, "random " + s);
        }
        StringBuilder big = new StringBuilder();
        for (int i = 0; i < 1000; i++) big.append((char) ('a' + rnd.nextInt(4)));
        check(memoized(big.toString()) == tabulation(big.toString()), "large random input");
        System.out.println("OK P231_CountPalindromicSubsequences");
    }
}
