import java.util.*;

/** TUF 378 - Check if String is Palindrome or Not. A string is a palindrome when it reads the same backwards. */
public class P378_CheckIfStringIsPalindromeOrNot {

    /** Approach 1: build the reversed string and compare. O(n) time, O(n) extra space. */
    static boolean bruteForce(String s) {
        String reversed = new StringBuilder(s).reverse().toString();
        return s.equals(reversed);
    }

    /** Approach 2: two pointers walking inward, stop at the first mismatch. O(n) time, O(1) space. */
    static boolean iterative(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            if (s.charAt(l) != s.charAt(r)) return false;
            l++;
            r--;
        }
        return true;
    }

    /** Approach 3: recursion on a single index i, mirrored against n - 1 - i. O(n) time, O(n) stack. */
    static boolean recursive(String s) {
        return isPalindromeFrom(s, 0);
    }

    private static boolean isPalindromeFrom(String s, int i) {
        int n = s.length();
        if (i >= n / 2) return true;                        // base case: reached the middle without a mismatch
        if (s.charAt(i) != s.charAt(n - 1 - i)) return false;
        return isPalindromeFrom(s, i + 1);                  // the outer pair matches, check the rest
    }

    /** LeetCode 125 flavour: ignore characters that are not letters or digits and compare case-insensitively. O(n) time, O(n) space. */
    static boolean recursiveAlphanumeric(String s) {
        StringBuilder clean = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetterOrDigit(c)) clean.append(Character.toLowerCase(c));
        }
        return isPalindromeFrom(clean.toString(), 0);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, boolean expected) {
        check(bruteForce(s) == expected, "bruteForce failed for \"" + s + "\"");
        check(iterative(s) == expected, "iterative failed for \"" + s + "\"");
        check(recursive(s) == expected, "recursive failed for \"" + s + "\"");
    }

    public static void main(String[] args) {
        verify("", true);              // edge: the empty string reads the same both ways
        verify("a", true);             // edge: single character
        verify("ab", false);
        verify("aba", true);
        verify("abba", true);
        verify("hannah", true);
        verify("abc", false);
        verify("abca", false);         // ends match, middle does not
        verify("Aba", false);          // plain version is case-sensitive

        check(recursiveAlphanumeric("A man, a plan, a canal: Panama"), "alphanumeric panama");
        check(!recursiveAlphanumeric("race a car"), "alphanumeric race a car");
        check(recursiveAlphanumeric(" "), "alphanumeric: only separators means empty, which is a palindrome");
        check(!recursiveAlphanumeric("0P"), "alphanumeric: digit vs letter");
        check(recursiveAlphanumeric("Aba"), "alphanumeric is case-insensitive");
        System.out.println("OK P378_CheckIfStringIsPalindromeOrNot");
    }
}
