import java.util.*;

/**
 * TUF 983 - Minimum number of bracket reversals to make an expression balanced.
 * The string holds only '{' and '}'. Return the fewest reversals ('{' to '}' or back) that balance it, or -1.
 */
public class P983_MinimumNumberOfBracketReversalsToMakeAnE {

    /** Approach 1: try every subset of positions to reverse. O(2^n * n) time, O(1) extra space. Tiny inputs only. */
    static int bruteForce(String s) {
        int n = s.length();
        if (n % 2 == 1) return -1;
        if (n > 20) throw new IllegalArgumentException("brute force is for tiny inputs only");
        int best = Integer.MAX_VALUE;
        for (int mask = 0; mask < (1 << n); mask++) {
            int flips = Integer.bitCount(mask);
            if (flips >= best) continue;
            int balance = 0;
            boolean ok = true;
            for (int i = 0; i < n && ok; i++) {
                char c = s.charAt(i);
                if ((mask >> i & 1) == 1) c = (c == '{') ? '}' : '{';
                balance += (c == '{') ? 1 : -1;
                if (balance < 0) ok = false;
            }
            if (ok && balance == 0) best = flips;
        }
        return best;
    }

    /** Approach 2: cancel matched pairs with a stack, then count what is left. O(n) time, O(n) space. */
    static int withStack(String s) {
        if (s.length() % 2 == 1) return -1;
        Deque<Character> st = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '}' && !st.isEmpty() && st.peek() == '{') st.pop();   // cancel a matched pair
            else st.push(c);
        }
        int open = 0, close = 0;                 // the leftover looks like }}}...{{{
        for (char c : st) {
            if (c == '{') open++;
            else close++;
        }
        return (open + 1) / 2 + (close + 1) / 2;
    }

    /** Approach 3: the same counting with two integers instead of a stack. O(n) time, O(1) space. */
    static int optimal(String s) {
        if (s.length() % 2 == 1) return -1;
        int open = 0, close = 0;                 // unmatched '{' and unmatched '}' seen so far
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '{') open++;
            else if (open > 0) open--;           // this '}' closes an earlier '{'
            else close++;                        // nothing to close: an unmatched '}'
        }
        return (open + 1) / 2 + (close + 1) / 2;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        if (s.length() <= 20) check(bruteForce(s) == expected, "bruteForce " + s);
        check(withStack(s) == expected, "withStack " + s);
        check(optimal(s) == expected, "optimal " + s);
    }

    public static void main(String[] args) {
        verify("}{{}}{{{", 3);
        verify("{{}{{{}{{}}{{", -1);           // odd length can never balance
        verify("", 0);                           // empty string is already balanced
        verify("}{", 2);                         // both characters must be reversed
        verify("{{{{", 2);
        verify("}}}}", 2);
        verify("{}{}", 0);
        verify("}}}{{{", 4);
        verify("{{{{}}}}", 0);
        verify("{".repeat(50_000) + "}".repeat(30_000), 10_000);   // large input, fast methods only

        // exhaustive cross-check of all strings up to length 10
        for (int n = 0; n <= 10; n++) {
            for (int bits = 0; bits < (1 << n); bits++) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < n; i++) sb.append((bits >> i & 1) == 1 ? '{' : '}');
                String s = sb.toString();
                int b = bruteForce(s);
                check(withStack(s) == b && optimal(s) == b, "mismatch on " + s);
            }
        }
        System.out.println("OK P983_MinimumNumberOfBracketReversalsToMakeAnE");
    }
}
