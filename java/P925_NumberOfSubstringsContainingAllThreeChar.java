import java.util.*;

/** TUF 925 - Number of Substrings Containing All Three Characters. Count substrings of an a/b/c string that contain each of a, b and c. */
public class P925_NumberOfSubstringsContainingAllThreeChar {

    /** Approach 1: check every substring with a running "seen" table. O(n^2) time, O(1) space. */
    static int bruteForce(String s) {
        int n = s.length(), count = 0;
        for (int i = 0; i < n; i++) {
            boolean[] seen = new boolean[3];
            for (int j = i; j < n; j++) {
                seen[s.charAt(j) - 'a'] = true;
                if (seen[0] && seen[1] && seen[2]) count++;
            }
        }
        return count;
    }

    /** Approach 2: for each start, stop at the first end that completes a, b, c and count all longer ends at once. O(n^2) worst case, O(1) space. */
    static int better(String s) {
        int n = s.length(), count = 0;
        for (int i = 0; i < n; i++) {
            boolean[] seen = new boolean[3];
            for (int j = i; j < n; j++) {
                seen[s.charAt(j) - 'a'] = true;
                if (seen[0] && seen[1] && seen[2]) {
                    count += n - j;                    // ends j, j+1, ..., n-1 all work for this start
                    break;
                }
            }
        }
        return count;
    }

    /** Approach 3: for each end, the valid starts are 0..min(last a, last b, last c). O(n) time, O(1) space. */
    static int optimal(String s) {
        int[] last = {-1, -1, -1};                     // last index of 'a', 'b', 'c' seen so far
        int count = 0;
        for (int r = 0; r < s.length(); r++) {
            last[s.charAt(r) - 'a'] = r;
            count += 1 + Math.min(last[0], Math.min(last[1], last[2]));   // adds 0 until all three have appeared
        }
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        String tag = "\"" + s + "\" expected " + expected;
        check(bruteForce(s) == expected, "bruteForce " + tag + " got " + bruteForce(s));
        check(better(s) == expected, "better " + tag + " got " + better(s));
        check(optimal(s) == expected, "optimal " + tag + " got " + optimal(s));
    }

    public static void main(String[] args) {
        verify("abcabc", 10);
        verify("aaacb", 3);
        verify("abc", 1);
        verify("bbacba", 9);
        verify("aab", 0);                              // 'c' never appears
        verify("", 0);                                 // empty string
        System.out.println("OK P925_NumberOfSubstringsContainingAllThreeChar");
    }
}
