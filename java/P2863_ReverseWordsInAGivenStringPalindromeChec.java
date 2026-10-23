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
        String[] parts = s.trim().split("\\s+");         // trim the ends, then split on any run of spaces
        List<String> words = new ArrayList<>(Arrays.asList(parts));   // mutable copy so it can be reversed
        Collections.reverse(words);                      // last word first
        return String.join(" ", words);                  // single spaces between words
    }

    /** Approach 2: one char array: squeeze the spaces, reverse everything, then reverse each word back. O(n) time, O(1) extra space beyond the array. */
    static String reverseWordsOptimal(String s) {
        char[] a = s.toCharArray();                      // work in place on a char array
        int n = a.length;
        int w = 0;                                       // write index: a[0..w) is the squeezed sentence
        int i = 0;                                       // read index
        while (i < n) {                                  // squeeze: copy words left, one space between them
            while (i < n && a[i] == ' ') i++;            // skip a run of spaces
            if (i == n) break;                           // only trailing spaces were left
            if (w > 0) a[w++] = ' ';                     // one separator before every word except the first
            while (i < n && a[i] != ' ') a[w++] = a[i++];   // copy the word
        }
        reverse(a, 0, w - 1);                            // whole sentence reversed: the words are backwards too
        int start = 0;                                   // start of the current word
        for (int k = 0; k <= w; k++) {                   // k == w acts as a final separator
            if (k == w || a[k] == ' ') {                 // a[start..k) is one reversed word
                reverse(a, start, k - 1);                // flip the word back to reading order
                start = k + 1;                           // the next word starts after the space
            }
        }
        return new String(a, 0, w);                      // only the first w chars are the answer
    }

    static void reverse(char[] a, int lo, int hi) {
        while (lo < hi) {                                // until the two pointers meet
            char t = a[lo]; a[lo] = a[hi]; a[hi] = t;    // swap the two ends
            lo++; hi--;
        }
    }

    // ------------------------------------------------------------ Part B: palindrome check

    /** Approach 3: build the cleaned string (letters and digits, lower-cased) and compare it with its reverse. O(n) time, O(n) space. */
    static boolean isPalindromeBruteForce(String s) {
        StringBuilder clean = new StringBuilder();       // the string without spaces and punctuation
        for (char c : s.toCharArray()) {                 // keep letters and digits only, lower-cased
            if (Character.isLetterOrDigit(c)) clean.append(Character.toLowerCase(c));
        }
        String forward = clean.toString();
        String backward = clean.reverse().toString();    // reverse() mutates clean: forward was saved first
        return forward.equals(backward);                 // a palindrome reads the same both ways
    }

    /** Approach 4: two pointers that skip everything that is not a letter or digit. O(n) time, O(1) space. */
    static boolean isPalindromeOptimal(String s) {
        int lo = 0, hi = s.length() - 1;                 // pointers at both ends
        while (lo < hi) {                                // until they meet
            while (lo < hi && !Character.isLetterOrDigit(s.charAt(lo))) lo++;   // skip junk on the left
            while (lo < hi && !Character.isLetterOrDigit(s.charAt(hi))) hi--;   // skip junk on the right
            // the two ends differ (ignoring case): not a palindrome
            if (Character.toLowerCase(s.charAt(lo)) != Character.toLowerCase(s.charAt(hi))) return false;
            lo++;                                        // both match: move inwards
            hi--;
        }
        return true;                                     // every pair matched
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
