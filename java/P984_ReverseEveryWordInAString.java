import java.util.*;

/** TUF 984 - Reverse every word in a string. Return the words of s in reverse order, joined by single spaces, with no leading or trailing spaces. */
public class P984_ReverseEveryWordInAString {

    /** Approach 1: split on spaces, drop the empty pieces, reverse the list, join. O(n) time, O(n) space. */
    static String bruteForce(String s) {
        List<String> words = new ArrayList<>();
        for (String w : s.split(" ")) {
            if (!w.isEmpty()) words.add(w);
        }
        Collections.reverse(words);
        return String.join(" ", words);
    }

    /** Approach 2: scan left to right pushing each word on a stack, then pop them all. O(n) time, O(n) space. */
    static String better(String s) {
        Deque<String> stack = new ArrayDeque<>();
        int n = s.length(), i = 0;
        while (i < n) {
            while (i < n && s.charAt(i) == ' ') i++;       // skip separators
            int start = i;
            while (i < n && s.charAt(i) != ' ') i++;       // consume one word
            if (i > start) stack.push(s.substring(start, i));
        }
        StringBuilder sb = new StringBuilder(n);
        while (!stack.isEmpty()) {
            sb.append(stack.pop());
            if (!stack.isEmpty()) sb.append(' ');
        }
        return sb.toString();
    }

    /** Approach 3: scan from the right and append each word as soon as its start is found; no intermediate collection. O(n) time, O(n) for the output only. */
    static String optimal(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        int j = s.length() - 1;
        while (j >= 0) {
            while (j >= 0 && s.charAt(j) == ' ') j--;      // skip separators from the right
            if (j < 0) break;
            int end = j;                                   // last letter of the word
            while (j >= 0 && s.charAt(j) != ' ') j--;      // j + 1 is now the first letter
            if (sb.length() > 0) sb.append(' ');
            sb.append(s, j + 1, end + 1);
        }
        return sb.toString();
    }

    /** Approach 4: the in-place version on a char array: squeeze out extra spaces, reverse everything, then reverse each word back. O(n) time, O(1) extra space beyond the array. */
    static String optimalInPlace(String s) {
        char[] a = s.toCharArray();
        int n = a.length;
        int w = 0;                                         // 1. squeeze: write index for the cleaned string
        for (int r = 0; r < n; r++) {
            if (a[r] == ' ') {
                if (w > 0 && a[w - 1] != ' ') a[w++] = ' ';   // keep exactly one space after a word
            } else {
                a[w++] = a[r];
            }
        }
        if (w > 0 && a[w - 1] == ' ') w--;                 // drop the separator left after the last word
        reverse(a, 0, w - 1);                              // 2. reverse the whole cleaned string
        int start = 0;                                     // 3. reverse each word back to normal
        for (int i = 0; i <= w; i++) {
            if (i == w || a[i] == ' ') {
                reverse(a, start, i - 1);
                start = i + 1;
            }
        }
        return new String(a, 0, w);
    }

    /** Reverses a[i..j] in place. */
    private static void reverse(char[] a, int i, int j) {
        while (i < j) {
            char tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
            i++;
            j--;
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, String expected) {
        check(bruteForce(s).equals(expected), "bruteForce(\"" + s + "\") = \"" + bruteForce(s) + "\" != \"" + expected + "\"");
        check(better(s).equals(expected), "better(\"" + s + "\") = \"" + better(s) + "\" != \"" + expected + "\"");
        check(optimal(s).equals(expected), "optimal(\"" + s + "\") = \"" + optimal(s) + "\" != \"" + expected + "\"");
        check(optimalInPlace(s).equals(expected), "optimalInPlace(\"" + s + "\") = \"" + optimalInPlace(s) + "\" != \"" + expected + "\"");
    }

    public static void main(String[] args) {
        verify("this is an amazing program", "program amazing an is this");
        verify("the sky is blue", "blue is sky the");
        verify("  hello world  ", "world hello");          // leading and trailing spaces are dropped
        verify("a good   example", "example good a");      // a run of spaces collapses to one
        verify("", "");                                    // empty input
        verify("   ", "");                                 // spaces only: no words at all
        verify("word", "word");                            // a single word is unchanged
        verify("  single  ", "single");
        verify("a b", "b a");
        verify("F R I E N D S", "S D N E I R F");          // single-letter words
        verify("Alice does not even like bob", "bob like even not does Alice");
        verify("ab  cd ef", "ef cd ab");
        System.out.println("OK P984_ReverseEveryWordInAString");
    }
}
