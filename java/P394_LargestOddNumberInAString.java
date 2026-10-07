import java.util.*;

/** TUF 394 - Largest Odd Number in a String (LeetCode 1903). Return the largest odd number that is a substring of a digit string, or "" if there is none. */
public class P394_LargestOddNumberInAString {

    /** Approach 1: enumerate every substring and keep the numerically largest odd one. O(n^3) time, O(n) space. */
    static String bruteForce(String num) {
        String best = "";
        int n = num.length();
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if ((num.charAt(j) - '0') % 2 == 1) {              // num[i..j] ends in an odd digit, so it is odd
                    String cand = num.substring(i, j + 1);
                    if (best.isEmpty() || isGreater(cand, best)) best = cand;
                }
            }
        }
        return best;
    }

    /** Numeric comparison of two digit strings without parsing them (they may not fit in a long). */
    static boolean isGreater(String a, String b) {
        String x = stripLeadingZeros(a), y = stripLeadingZeros(b);
        if (x.length() != y.length()) return x.length() > y.length();
        return x.compareTo(y) > 0;
    }

    static String stripLeadingZeros(String s) {
        int k = 0;
        while (k < s.length() - 1 && s.charAt(k) == '0') k++;
        return s.substring(k);
    }

    /** Approach 2: the answer is the prefix that ends at the rightmost odd digit. O(n) time, O(1) extra space. */
    static String optimal(String num) {
        for (int i = num.length() - 1; i >= 0; i--) {
            if ((num.charAt(i) - '0') % 2 == 1) return num.substring(0, i + 1);
        }
        return "";
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String num, String expected) {
        check(expected.equals(bruteForce(num)), "bruteForce failed on " + num + ": " + bruteForce(num));
        check(expected.equals(optimal(num)), "optimal failed on " + num + ": " + optimal(num));
    }

    public static void main(String[] args) {
        verify("52", "5");
        verify("4206", "");                                        // no odd digit at all
        verify("35427", "35427");                                  // whole string already odd
        verify("7", "7");                                          // single digit
        verify("2468", "");
        verify("123456", "12345");
        verify("10", "1");
        verify("9876543210", "987654321");
        verify("1000000000000000000001", "1000000000000000000001"); // 22 digits: far beyond a long
        System.out.println("OK P394_LargestOddNumberInAString");
    }
}
