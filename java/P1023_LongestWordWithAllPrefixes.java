import java.util.*;

/** TUF 1023 - Longest Word with All Prefixes (a "complete string"). Return the longest word all of whose prefixes
 *  are also in the array, the lexicographically smallest among equal lengths, or "None" if there is no such word. */
public class P1023_LongestWordWithAllPrefixes {

    /** Longer wins; at equal length the lexicographically smaller string wins. */
    static boolean isBetter(String candidate, String best) {
        if (candidate.length() != best.length()) return candidate.length() > best.length();   // longer one wins
        return candidate.compareTo(best) < 0;                   // same length: alphabetical order decides
    }

    /** Approach 1: for every word, look for each of its prefixes by scanning the whole array. O(n^2 * L^2) time, O(L) space. */
    static String bruteForce(String[] words) {
        String best = "";                                       // "" loses to any real word
        for (String w : words) {
            boolean complete = true;                            // stays true while every prefix is found
            for (int len = 1; len <= w.length() && complete; len++) {   // each prefix; stop at the first miss
                String prefix = w.substring(0, len);
                boolean found = false;
                for (String other : words) {                    // scan the whole array for this prefix
                    if (other.equals(prefix)) {
                        found = true;
                        break;
                    }
                }
                complete = found;                               // a missing prefix ends the check for w
            }
            if (complete && isBetter(w, best)) best = w;        // keep the best complete word
        }
        return best.isEmpty() ? "None" : best;                  // no complete word at all
    }

    /** Approach 2: put every word in a hash set, then test each proper prefix in O(L). O(n * L^2) time, O(n * L) space. */
    static String better(String[] words) {
        Set<String> set = new HashSet<>(Arrays.asList(words));   // O(1) average lookups for prefixes
        String best = "";
        for (String w : words) {
            boolean complete = true;
            for (int len = 1; len < w.length() && complete; len++) {   // w itself is in the set already
                complete = set.contains(w.substring(0, len));   // one hash lookup per prefix
            }
            if (complete && isBetter(w, best)) best = w;        // keep the best complete word
        }
        return best.isEmpty() ? "None" : best;                  // no complete word at all
    }

    static class Node {
        final Node[] child = new Node[26];                 // child[c - 'a'] is the edge labelled c
        boolean isEnd;                                     // some word in the array ends exactly here
    }

    static void insert(Node root, String word) {
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {          // one level down per letter
            int c = word.charAt(i) - 'a';                  // letter -> child index 0..25
            if (cur.child[c] == null) cur.child[c] = new Node();   // create the edge only when it is missing
            cur = cur.child[c];
        }
        cur.isEnd = true;                                  // mark the end of a whole word
    }

    /** Walks the word's path; every node on it must end a word, i.e. every prefix must be in the array. */
    static boolean allPrefixesPresent(Node root, String word) {
        Node cur = root;
        for (int i = 0; i < word.length(); i++) {
            cur = cur.child[word.charAt(i) - 'a'];         // step to the node of the next longer prefix
            if (cur == null || !cur.isEnd) return false;   // the prefix of length i + 1 is not a word
        }
        return true;                                       // every prefix is a word in the array
    }

    /** Approach 3: build a trie of all words, then check each word with one walk of its path. O(n * L) time and space. */
    static String optimal(String[] words) {
        Node root = new Node();                                 // the root stands for the empty prefix
        for (String w : words) insert(root, w);                 // every word goes into the trie first
        String best = "";
        for (String w : words) {                                // then check each word with one walk
            if (allPrefixesPresent(root, w) && isBetter(w, best)) best = w;   // complete and better: keep it
        }
        return best.isEmpty() ? "None" : best;                  // no complete word at all
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String[] words, String expected) {
        String[] got = {bruteForce(words), better(words), optimal(words)};
        String[] names = {"bruteForce", "better", "optimal"};
        for (int k = 0; k < 3; k++) {
            check(got[k].equals(expected), names[k] + " on " + Arrays.toString(words) + " got " + got[k] + " expected " + expected);
        }
    }

    public static void main(String[] args) {
        verify(new String[]{"n", "ni", "nin", "ninj", "ninja", "ninga"}, "ninja");           // "ninga" lacks "ning"
        verify(new String[]{"a", "banana", "app", "appl", "ap", "apply", "apple"}, "apple"); // tie at length 5: apple < apply
        verify(new String[]{"k", "ki", "kir", "kira", "kiran"}, "kiran");
        verify(new String[]{"abc", "bc", "ab", "qwe"}, "None");                             // no word has its 1-letter prefix
        verify(new String[]{"ab", "bc"}, "None");
        verify(new String[]{"b", "b", "a"}, "a");                                           // duplicates; tie broken lexicographically
        verify(new String[]{"z"}, "z");                                                     // edge: single word
        verify(new String[]{}, "None");                                                     // edge: empty array
        verify(new String[]{"cat", "c", "ca", "dog", "d", "do", "cats"}, "cats");
        // deterministic random arrays over a 2-letter alphabet: all three must agree
        Random rnd = new Random(1023);
        for (int t = 0; t < 300; t++) {
            String[] words = new String[rnd.nextInt(12)];
            for (int i = 0; i < words.length; i++) {
                int len = 1 + rnd.nextInt(4);
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < len; k++) sb.append((char) ('a' + rnd.nextInt(2)));
                words[i] = sb.toString();
            }
            verify(words, bruteForce(words));
        }
        System.out.println("OK P1023_LongestWordWithAllPrefixes");
    }
}
