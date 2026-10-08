import java.util.*;

/** TUF 970 - Remove K Digits. Delete exactly k digits from num so the remaining number is as small as possible. */
public class P970_RemoveKDigits {

    /** Approach 1: k rounds; each round deletes the first digit that is larger than its right neighbour. O(n * k) time, O(n) space. */
    static String bruteForce(String num, int k) {
        StringBuilder sb = new StringBuilder(num);
        for (int round = 0; round < k; round++) {
            int i = 0;
            while (i + 1 < sb.length() && sb.charAt(i) <= sb.charAt(i + 1)) i++;
            sb.deleteCharAt(i);                       // first "peak", or the last digit if the string is non-decreasing
        }
        return stripLeadingZeros(sb);
    }

    static String stripLeadingZeros(StringBuilder sb) {
        int i = 0;
        while (i < sb.length() && sb.charAt(i) == '0') i++;
        String s = sb.substring(i);
        return s.isEmpty() ? "0" : s;
    }

    /** Approach 2: monotonic stack; pop larger digits while deletions remain. O(n) time, O(n) space. */
    static String optimal(String num, int k) {
        StringBuilder st = new StringBuilder();      // used as a stack of digits
        for (char c : num.toCharArray()) {
            while (k > 0 && st.length() > 0 && st.charAt(st.length() - 1) > c) {
                st.deleteCharAt(st.length() - 1);
                k--;
            }
            st.append(c);
        }
        st.setLength(st.length() - k);               // deletions still owed come off the end
        return stripLeadingZeros(st);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String num, int k, String expected) {
        String b = bruteForce(num, k), o = optimal(num, k);
        check(b.equals(expected), "bruteForce(" + num + ", " + k + ") -> " + b);
        check(o.equals(expected), "optimal(" + num + ", " + k + ") -> " + o);
    }

    public static void main(String[] args) {
        verify("1432219", 3, "1219");
        verify("10200", 1, "200");                   // leading zeros removed
        verify("10", 2, "0");                        // everything removed
        verify("112", 1, "11");                      // non-decreasing: drop from the end
        verify("12345", 0, "12345");                 // nothing to remove
        verify("100", 1, "0");                       // "00" becomes "0"
        verify("9", 1, "0");                         // single digit
        verify("1234567890", 9, "0");
        verify("43214321", 4, "1321");
        verify("5337", 2, "33");                     // equal digits are kept
        System.out.println("OK P970_RemoveKDigits");
    }
}
