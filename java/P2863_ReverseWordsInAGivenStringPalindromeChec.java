import java.util.*;

/**
 * TUF 2863 - Reverse words in a given string / Palindrome Check.
 * Part A reverses the order of the words in a sentence (LeetCode 151).
 * Part B checks whether a string is a palindrome ignoring case and non-alphanumerics (LeetCode 125).
 */
public class P2863_ReverseWordsInAGivenStringPalindromeChec {

    // ------------------------------------------------------------ Part A: reverse the words

    /** Approach 1: split on runs of spaces, reverse the list, join with single spaces. O(n) time, O(n) space. */
    static String reverseWordsBruteForce(String s) {
        String[] parts = s.trim().split("\\s+");
        List<String> words = new ArrayList<>(Arrays.asList(parts));
        Collections.reverse(words);
        return String.join(" ", words);
    }

    /** Approach 2: one char array: squeeze the spaces, reverse everything, then reverse each word back. O(n) time, O(1) extra space beyond the array. */
    static String reverseWordsOptimal(String s) {
        char[] a = s.toCharArray();
        int n = a.length;
        int w = 0;                                          // write index: a[0..w) is the squeezed sentence
        int i = 0;
        while (i < n) {
            while (i < n && a[i] == ' ') i++;               // skip a run of spaces
            if (i == n) break;
            if (w > 0) a[w++] = ' ';                        // one separator before every word except the first
            while (i < n && a[i] != ' ') a[w++] = a[i++];   // copy the word
        }
        reverse(a, 0, w - 1);                               // whole sentence reversed: the words are backwards too
        int start = 0;
        for (int k = 0; k <= w; k++) {
            if (k == w || a[k] == ' ') {                    // a[start..k) is one reversed word
                reverse(a, start, k - 1);
                start = k + 1;
            }
        }
        return new String(a, 0, w);
    }

    static void reverse(char[] a, int lo, int hi) {
        while (lo < hi) {
            char t = a[lo]; a[lo] = a[hi]; a[hi] = t;
            lo++; hi--;
        }
    }

    // ------------------------------------------------------------ Part B: palindrome check

    /** Approach 3: build the cleaned string (letters and digits, lower-cased) and compare it with its reverse. O(n) time, O(n) space. */
    static boolean isPalindromeBruteForce(String s) {
        StringBuilder clean = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) clean.append(Character.toLowerCase(c));
        }
        String forward = clean.toString();
        String backward = clean.reverse().toString();
        return forward.equals(backward);
    }

    /** Approach 4: two pointers that skip everything that is not a letter or digit. O(n) time, O(1) space. */
    static boolean isPalindromeOptimal(String s) {
        int lo = 0, hi = s.length() - 1;
        while (lo < hi) {
            while (lo < hi && !Character.isLetterOrDigit(s.charAt(lo))) lo++;
            while (lo < hi && !Character.isLetterOrDigit(s.charAt(hi))) hi--;
            if (Character.toLowerCase(s.charAt(lo)) != Character.toLowerCase(s.charAt(hi))) return false;
            lo++;
            hi--;
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyReverse(String s, String expected) {
        check(expected.equals(reverseWordsBruteForce(s)), "reverseWordsBruteForce failed on \"" + s + "\": " + reverseWordsBruteForce(s));
        check(expected.equals(reverseWordsOptimal(s)), "reverseWordsOptimal failed on \"" + s + "\": " + reverseWordsOptimal(s));
    }

    static void verifyPalindrome(String s, boolean expected) {
        check(isPalindromeBruteForce(s) == expected, "isPalindromeBruteForce failed on \"" + s + "\"");
        check(isPalindromeOptimal(s) == expected, "isPalindromeOptimal failed on \"" + s + "\"");
    }

    public static void main(String[] args) {
        verifyReverse("the sky is blue", "blue is sky the");
        verifyReverse("  hello world  ", "world hello");         // leading and trailing spaces vanish
        verifyReverse("a good   example", "example good a");     // runs of spaces collapse to one
        verifyReverse("single", "single");                       // one word
        verifyReverse("", "");                                   // empty
        verifyReverse("     ", "");                              // only spaces
        verifyReverse("ab  cd ef", "ef cd ab");

        verifyPalindrome("A man, a plan, a canal: Panama", true);
        verifyPalindrome("race a car", false);
        verifyPalindrome(" ", true);                             // nothing left after cleaning
        verifyPalindrome("", true);
        verifyPalindrome("0P", false);                           // a digit and a letter are never equal
        verifyPalindrome("ab@Ba", true);                         // case-insensitive, punctuation ignored
        verifyPalindrome("Was it a car or a cat I saw?", true);
        System.out.println("OK P2863_ReverseWordsInAGivenStringPalindromeChec");
    }
}
