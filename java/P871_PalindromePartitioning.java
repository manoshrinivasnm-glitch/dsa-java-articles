import java.util.*;

/** TUF 871 - Palindrome partitioning. Every way to cut s into pieces that are all palindromes. */
public class P871_PalindromePartitioning {

    /** Approach 1: try every end for the next piece; check each candidate piece with a fresh two-pointer scan. O(2^n * n) time. */
    static List<List<String>> bruteForce(String s) {
        List<List<String>> out = new ArrayList<>();
        cutRec(s, 0, new ArrayList<>(), out);
        return out;
    }

    static void cutRec(String s, int start, List<String> cur, List<List<String>> out) {
        if (start == s.length()) {                       // every character has been placed in some palindrome
            out.add(new ArrayList<>(cur));
            return;
        }
        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {
                cur.add(s.substring(start, end + 1));
                cutRec(s, end + 1, cur, out);
                cur.remove(cur.size() - 1);
            }
        }
    }

    static boolean isPalindrome(String s, int lo, int hi) {
        while (lo < hi) {
            if (s.charAt(lo++) != s.charAt(hi--)) return false;
        }
        return true;
    }

    /** Approach 2: precompute pal[i][j] with interval DP in O(n^2), then the same backtracking with O(1) checks. */
    static List<List<String>> optimal(String s) {
        int n = s.length();
        boolean[][] pal = new boolean[n][n];
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                pal[i][j] = s.charAt(i) == s.charAt(j) && (j - i < 2 || pal[i + 1][j - 1]);
            }
        }
        List<List<String>> out = new ArrayList<>();
        cutRecDp(s, 0, pal, new ArrayList<>(), out);
        return out;
    }

    static void cutRecDp(String s, int start, boolean[][] pal, List<String> cur, List<List<String>> out) {
        if (start == s.length()) {
            out.add(new ArrayList<>(cur));
            return;
        }
        for (int end = start; end < s.length(); end++) {
            if (pal[start][end]) {
                cur.add(s.substring(start, end + 1));
                cutRecDp(s, end + 1, pal, cur, out);
                cur.remove(cur.size() - 1);
            }
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, List<List<String>> expected) {
        check(bruteForce(s).equals(expected), "bruteForce \"" + s + "\"");
        check(optimal(s).equals(expected), "optimal \"" + s + "\"");
    }

    /** For longer inputs: both approaches agree, the count is right, and every partition is a valid palindromic cover of s. */
    static void verifyCount(String s, int expectedCount) {
        List<List<String>> a = bruteForce(s);
        List<List<String>> b = optimal(s);
        check(a.equals(b), "approaches disagree on \"" + s + "\"");
        check(a.size() == expectedCount, "expected " + expectedCount + " partitions of \"" + s + "\", got " + a.size());
        check(new HashSet<>(a).size() == a.size(), "duplicate partition for \"" + s + "\"");
        for (List<String> parts : a) {
            StringBuilder joined = new StringBuilder();
            for (String p : parts) {
                check(isPalindrome(p, 0, p.length() - 1), "piece " + p + " is not a palindrome");
                joined.append(p);
            }
            check(joined.toString().equals(s), "pieces " + parts + " do not rebuild \"" + s + "\"");
        }
    }

    public static void main(String[] args) {
        verify("aab", List.of(List.of("a", "a", "b"), List.of("aa", "b")));
        verify("aba", List.of(List.of("a", "b", "a"), List.of("aba")));
        verify("abc", List.of(List.of("a", "b", "c")));                           // only single letters work
        verify("a", List.of(List.of("a")));                                       // single character
        verify("abba", List.of(List.of("a", "b", "b", "a"), List.of("a", "bb", "a"), List.of("abba")));
        verify("abacdc", List.of(List.of("a", "b", "a", "c", "d", "c"), List.of("a", "b", "a", "cdc"),
                List.of("aba", "c", "d", "c"), List.of("aba", "cdc")));
        verifyCount("", 1);                                                       // empty string: the empty partition only
        verifyCount("aaaa", 8);                                                   // every cut pattern works: 2^(n-1)
        verifyCount("aabaa", 6);
        verifyCount("aaaaaaaaaaaaaaaa", 1 << 15);                                 // n = 16, the worst case
        verifyCount("abcdefghijklmnop", 1);                                       // all distinct: one partition
        System.out.println("OK P871_PalindromePartitioning");
    }
}
