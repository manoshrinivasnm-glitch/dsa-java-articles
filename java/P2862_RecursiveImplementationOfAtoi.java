import java.util.*;

/** TUF 2862 - Recursive Implementation of atoi(). Convert a string to a 32-bit signed integer (C atoi / LeetCode 8 rules) with recursion instead of loops. */
public class P2862_RecursiveImplementationOfAtoi {

    /** Only the ASCII digits count; Character.isDigit would also accept other scripts. */
    static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /** Baseline: the iterative parser. O(n) time, O(1) space. */
    static int iterative(String s) {
        int n = s.length(), i = 0;
        while (i < n && s.charAt(i) == ' ') i++;                      // 1. skip leading spaces
        int sign = 1;
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {    // 2. one optional sign
            if (s.charAt(i) == '-') sign = -1;
            i++;
        }
        long value = 0;
        while (i < n && isDigit(s.charAt(i))) {                        // 3. digits, until the first non-digit
            value = value * 10 + (s.charAt(i) - '0');
            if (sign * value > Integer.MAX_VALUE) return Integer.MAX_VALUE;   // 4. clamp as soon as the int range is left
            if (sign * value < Integer.MIN_VALUE) return Integer.MIN_VALUE;
            i++;
        }
        return (int) (sign * value);
    }

    /** Approach 1: recursion with an accumulator. Each call consumes one digit and passes the running value forward. O(n) time, O(n) stack. */
    static int recursiveAccumulate(String s) {
        int i = skipSpaces(s, 0);
        int sign = 1;
        if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            if (s.charAt(i) == '-') sign = -1;
            i++;
        }
        return parseDigits(s, i, sign, 0);
    }

    /** Index of the first non-space character at or after i. */
    static int skipSpaces(String s, int i) {
        if (i < s.length() && s.charAt(i) == ' ') return skipSpaces(s, i + 1);
        return i;
    }

    /** acc is the value of the digits before index i. Consume s[i] if it is a digit, otherwise the number is complete. */
    static int parseDigits(String s, int i, int sign, long acc) {
        if (i == s.length() || !isDigit(s.charAt(i))) return (int) (sign * acc);   // base case: the digit run has ended
        long next = acc * 10 + (s.charAt(i) - '0');
        if (sign * next > Integer.MAX_VALUE) return Integer.MAX_VALUE;              // clamp now, before acc could ever overflow a long
        if (sign * next < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return parseDigits(s, i + 1, sign, next);
    }

    /** Approach 2: recursion that returns a value. The number in s[start, end) is ten times the number in s[start, end - 1) plus the last digit. O(n) time, O(n) stack. */
    static int recursiveReturn(String s) {
        int i = skipSpaces(s, 0);
        int sign = 1;
        if (i < s.length() && (s.charAt(i) == '+' || s.charAt(i) == '-')) {
            if (s.charAt(i) == '-') sign = -1;
            i++;
        }
        int end = endOfDigits(s, i);
        long signed = sign * valueOf(s, i, end);
        if (signed > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (signed < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) signed;
    }

    /** Index just past the run of digits that starts at i. */
    static int endOfDigits(String s, int i) {
        if (i < s.length() && isDigit(s.charAt(i))) return endOfDigits(s, i + 1);
        return i;
    }

    /** Value of the digits in s[start, end), capped at 10^10 so the long can never overflow; the cap is already far outside the int range. */
    static long valueOf(String s, int start, int end) {
        if (end == start) return 0;                                   // base case: no digits means zero
        long prefix = valueOf(s, start, end - 1);                     // everything except the last digit
        long value = prefix * 10 + (s.charAt(end - 1) - '0');
        return Math.min(value, 10_000_000_000L);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        check(iterative(s) == expected, "iterative(\"" + s + "\") = " + iterative(s));
        check(recursiveAccumulate(s) == expected, "recursiveAccumulate(\"" + s + "\") = " + recursiveAccumulate(s));
        check(recursiveReturn(s) == expected, "recursiveReturn(\"" + s + "\") = " + recursiveReturn(s));
    }

    public static void main(String[] args) {
        verify("42", 42);
        verify("   -42", -42);
        verify("4193 with words", 4193);
        verify("words and 987", 0);                        // no digits before the first letter
        verify("-91283472332", Integer.MIN_VALUE);         // below the int range: clamp
        verify("2147483648", Integer.MAX_VALUE);           // one past the int range
        verify("-2147483648", Integer.MIN_VALUE);          // exactly the smallest int
        verify("2147483647", Integer.MAX_VALUE);           // exactly the largest int
        verify("+-12", 0);                                 // only one sign character is allowed
        verify("", 0);                                     // empty string
        verify("   ", 0);                                  // spaces only
        verify("00000-42a1234", 0);                        // leading zeros, then '-' ends the number
        verify("  0000000000012345678", 12345678);
        verify("+1", 1);
        verify("-0", 0);
        verify(" +0 123", 0);                              // the space after the 0 ends the number
        verify("9223372036854775808", Integer.MAX_VALUE);  // does not even fit in a long: the clamp must happen early
        verify("-" + "9".repeat(3000), Integer.MIN_VALUE); // 3000 digits: recursion depth stays comfortable
        verify("1".repeat(3000) + "x", Integer.MAX_VALUE);
        System.out.println("OK P2862_RecursiveImplementationOfAtoi");
    }
}
