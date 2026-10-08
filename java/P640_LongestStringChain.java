import java.util.*;

/** TUF 640 - Longest String Chain. A word is a predecessor of another if inserting exactly one letter anywhere turns it into the other; return the longest chain length. */
public class P640_LongestStringChain {

    /** Approach 1: for every word, recursively try deleting each letter and follow every predecessor that exists. Exponential in the word length. */
    static int bruteForce(String[] words) {
        Set<String> dict = new HashSet<>(Arrays.asList(words));
        int best = 0;
        for (String w : words) best = Math.max(best, chainEndingAt(w, dict));
        return best;
    }

    /** Longest chain that ends with w, without caching. */
    static int chainEndingAt(String w, Set<String> dict) {
        int best = 1;
        for (int i = 0; i < w.length(); i++) {
            String shorter = w.substring(0, i) + w.substring(i + 1);   // delete letter i
            if (dict.contains(shorter)) best = Math.max(best, 1 + chainEndingAt(shorter, dict));
        }
        return best;
    }

    /** Approach 2: sort by length and run LIS where "a[j] < a[i]" becomes "words[j] is a predecessor of words[i]". O(n^2 * L) time, O(n) space. */
    static int lisStyle(String[] words) {
        String[] w = words.clone();
        Arrays.sort(w, Comparator.comparingInt(String::length));
        int n = w.length, best = 0;
        int[] dp = new int[n];                                          // dp[i] = longest chain ending at w[i]
        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            for (int j = 0; j < i; j++) {
                if (isPredecessor(w[j], w[i])) dp[i] = Math.max(dp[i], dp[j] + 1);
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    /** True if inserting exactly one letter into shorter gives longer. Two pointers, O(L). */
    static boolean isPredecessor(String shorter, String longer) {
        if (longer.length() != shorter.length() + 1) return false;
        int i = 0;                                                      // next letter of shorter to match
        for (int j = 0; j < longer.length(); j++) {
            if (i < shorter.length() && shorter.charAt(i) == longer.charAt(j)) i++;
        }
        return i == shorter.length();                                   // all of shorter matched; one letter of longer skipped
    }

    /** Approach 3: sort by length, and for each word look up its L one-letter deletions in a map. O(n log n + n * L^2) time, O(n * L) space. */
    static int hashMapDp(String[] words) {
        String[] w = words.clone();
        Arrays.sort(w, Comparator.comparingInt(String::length));
        Map<String, Integer> longest = new HashMap<>();                 // word -> longest chain ending at it
        int best = 0;
        for (String word : w) {
            int cur = 1;
            for (int i = 0; i < word.length(); i++) {
                String shorter = word.substring(0, i) + word.substring(i + 1);
                Integer prev = longest.get(shorter);
                if (prev != null) cur = Math.max(cur, prev + 1);
            }
            longest.put(word, cur);
            best = Math.max(best, cur);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String[] words, int expected) {
        String in = Arrays.toString(words);
        check(bruteForce(words) == expected, "bruteForce failed for " + in);
        check(lisStyle(words) == expected, "lisStyle failed for " + in);
        check(hashMapDp(words) == expected, "hashMapDp failed for " + in);
    }

    public static void main(String[] args) {
        verify(new String[]{"a", "b", "ba", "bca", "bda", "bdca"}, 4);          // a -> ba -> bda -> bdca
        verify(new String[]{"xbc", "pcxbcf", "xb", "cxbc", "pcxbc"}, 5);        // xb -> xbc -> cxbc -> pcxbc -> pcxbcf
        verify(new String[]{"abcd", "dbqca"}, 1);                               // same letters, but not one insertion apart
        verify(new String[]{"ab", "ba", "abc"}, 2);                             // ab -> abc; ba is not a predecessor of abc
        verify(new String[]{"a", "ab", "ac", "abc", "abd", "abcd", "x"}, 4);    // several chains of length 4
        verify(new String[]{"bdca", "bda", "ba", "a"}, 4);                      // unsorted input
        verify(new String[]{"a", "aa", "aaa", "aaaa"}, 4);                      // repeated letters
        verify(new String[]{"z"}, 1);                                           // edge: one word
        verify(new String[]{}, 0);                                              // edge: no words

        check(isPredecessor("abc", "abxc") && isPredecessor("abc", "xabc") && isPredecessor("abc", "abcx"), "insert anywhere");
        check(!isPredecessor("abc", "acbx") && !isPredecessor("abc", "abc") && !isPredecessor("ab", "abcd"), "not predecessors");

        Random rnd = new Random(640);
        for (int t = 0; t < 300; t++) {
            Set<String> s = new LinkedHashSet<>();
            int size = rnd.nextInt(14);
            while (s.size() < size) {
                StringBuilder sb = new StringBuilder();
                int len = 1 + rnd.nextInt(5);
                for (int i = 0; i < len; i++) sb.append((char) ('a' + rnd.nextInt(3)));
                s.add(sb.toString());
            }
            String[] words = s.toArray(new String[0]);
            verify(words, hashMapDp(words));
        }
        System.out.println("OK P640_LongestStringChain");
    }
}
