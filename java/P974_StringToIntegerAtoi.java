import java.util.*;

/** TUF 974 - String to Integer (atoi). Parse the leading integer of a string the way C's atoi does, clamped to the 32-bit range. */
public class P974_StringToIntegerAtoi {

    /** Approach 1: accumulate into a long and stop as soon as the value leaves the int range. O(n) time, O(1) space. */
    static int usingLong(String s) {
        int i = 0, n = s.length();
        while (i < n && s.charAt(i) == ' ') i++;                       // 1. leading spaces (only ' ')
        int sign = 1;                                                  // +1 or -1, applied at the end
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {     // 2. one optional sign
            if (s.charAt(i) == '-') sign = -1;
            i++;                                                       // step past the sign character
        }
        long result = 0;                                               // magnitude; a long can pass MAX safely
        while (i < n && s.charAt(i) >= '0' && s.charAt(i) <= '9') {   // 3. digits until the first non-digit
            result = result * 10 + (s.charAt(i) - '0');                // append this digit on the right
            if (sign * result > Integer.MAX_VALUE) return Integer.MAX_VALUE;   // 4. clamp as soon as we overflow
            if (sign * result < Integer.MIN_VALUE) return Integer.MIN_VALUE;   // same clamp for negatives
            i++;
        }
        return (int) (sign * result);                                  // fits in an int, so the cast is safe
    }

    /** Approach 2: int only; before appending a digit, test whether result * 10 + digit would exceed the limit. O(n) time, O(1) space. */
    static int optimal(String s) {
        int i = 0, n = s.length();
        while (i < n && s.charAt(i) == ' ') i++;                       // skip leading spaces
        int sign = 1;
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {     // one optional sign
            if (s.charAt(i) == '-') sign = -1;
            i++;
        }
        int result = 0;                                                // magnitude so far, never above MAX
        while (i < n && s.charAt(i) >= '0' && s.charAt(i) <= '9') {   // read digits until a non-digit
            int digit = s.charAt(i) - '0';
            // result * 10 + digit > MAX  <=>  result > (MAX - digit) / 10, and MIN = -(MAX + 1) so the same test clamps negatives
            if (result > (Integer.MAX_VALUE - digit) / 10) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;   // would overflow: clamp by sign
            }
            result = result * 10 + digit;                              // safe to append now
            i++;
        }
        return sign * result;
    }

    /** Approach 3: the same parse written recursively, one digit per call. O(n) time, O(d) stack for d digits. */
    static int recursive(String s) {
        int i = 0, n = s.length();
        while (i < n && s.charAt(i) == ' ') i++;                       // skip leading spaces
        int sign = 1;
        if (i < n && (s.charAt(i) == '+' || s.charAt(i) == '-')) {     // one optional sign
            if (s.charAt(i) == '-') sign = -1;
            i++;
        }
        return parseDigits(s, i, sign, 0);                             // recursion reads the digits from i on
    }

    /** Folds s[i] into acc (the magnitude read so far) if it is a digit and recurses; clamps when the value leaves the int range. */
    private static int parseDigits(String s, int i, int sign, long acc) {
        // base case: end of string or a non-digit, so the number is complete
        if (i == s.length() || s.charAt(i) < '0' || s.charAt(i) > '9') return (int) (sign * acc);
        long next = acc * 10 + (s.charAt(i) - '0');                    // append this digit
        if (sign == 1 && next > Integer.MAX_VALUE) return Integer.MAX_VALUE;     // too big: clamp
        if (sign == -1 && -next < Integer.MIN_VALUE) return Integer.MIN_VALUE;   // too small: clamp
        return parseDigits(s, i + 1, sign, next);                      // move on to the next character
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        check(usingLong(s) == expected, "usingLong(\"" + s + "\") = " + usingLong(s) + " != " + expected);
        check(optimal(s) == expected, "optimal(\"" + s + "\") = " + optimal(s) + " != " + expected);
        check(recursive(s) == expected, "recursive(\"" + s + "\") = " + recursive(s) + " != " + expected);
    }

    public static void main(String[] args) {
        verify("42", 42);
        verify("   -42", -42);
        verify("4193 with words", 4193);
        verify("words and 987", 0);
        verify("-91283472332", Integer.MIN_VALUE);
        verify("", 0);                                               // empty input
        verify("   ", 0);                                            // spaces only
        verify("+1", 1);
        verify("+-12", 0);                                           // only one sign is allowed
        verify("-+12", 0);
        verify("   +0 123", 0);                                      // stops at the space after the 0
        verify("00000-42a1234", 0);                                  // leading zeros, then a '-' ends the number
        verify("  0000000000012345678", 12345678);
        verify("2147483647", Integer.MAX_VALUE);
        verify("2147483648", Integer.MAX_VALUE);                     // one past MAX clamps
        verify("-2147483648", Integer.MIN_VALUE);                    // exactly MIN is representable
        verify("-2147483647", -2147483647);
        verify("21474836460", Integer.MAX_VALUE);
        verify("99999999999999999999999", Integer.MAX_VALUE);        // far beyond long as well, clamp must happen early
        verify("-99999999999999999999999", Integer.MIN_VALUE);
        verify("3.14159", 3);
        verify("-", 0);
        verify("\t42", 0);                                           // only ' ' counts as leading whitespace
        System.out.println("OK P974_StringToIntegerAtoi");
    }
}
