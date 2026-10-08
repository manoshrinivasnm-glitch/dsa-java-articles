import java.util.*;

/** TUF 1026 - Number of distinct substrings in a string. Count the distinct substrings of a lowercase string,
 *  counting the empty substring as one of them (subtract 1 for the LeetCode 1698 convention). */
public class P1026_NumberOfDistinctSubstringsInAString {

    /** Approach 1: materialise every substring and let a hash set remove duplicates. O(n^3) time and space. */
    static long bruteForce(String s) {
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                seen.add(s.substring(i, j));               // copying s[i..j-1] costs O(j - i)
            }
        }
        return seen.size() + 1L;                           // + 1 for the empty substring
    }

    static class TrieNode {
        final TrieNode[] child = new TrieNode[26];
    }

    /** Approach 2: insert every suffix into a trie; each node created is a substring never seen before. O(n^2) time and space. */
    static long better(String s) {
        TrieNode root = new TrieNode();
        long created = 0;
        for (int i = 0; i < s.length(); i++) {             // suffix starting at i
            TrieNode cur = root;
            for (int j = i; j < s.length(); j++) {         // its prefixes are the substrings s[i..j]
                int c = s.charAt(j) - 'a';
                if (cur.child[c] == null) {
                    cur.child[c] = new TrieNode();
                    created++;                             // s[i..j] has not appeared before
                }
                cur = cur.child[c];
            }
        }
        return created + 1;                                // the root stands for the empty substring
    }

    /** Suffix array by prefix doubling: after the round for step k, suffixes are sorted by their first 2k characters. */
    static int[] suffixArray(String s) {
        int n = s.length();
        Integer[] order = new Integer[n];
        int[] rank = new int[n], next = new int[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
            rank[i] = s.charAt(i);
        }
        for (int k = 1; n > 1; k <<= 1) {
            final int step = k;
            Comparator<Integer> byPair = (a, b) -> {       // compare (rank of first k chars, rank of next k chars)
                if (rank[a] != rank[b]) return Integer.compare(rank[a], rank[b]);
                int ra = a + step < n ? rank[a + step] : -1;   // a suffix that runs out sorts first
                int rb = b + step < n ? rank[b + step] : -1;
                return Integer.compare(ra, rb);
            };
            Arrays.sort(order, byPair);
            next[order[0]] = 0;
            for (int i = 1; i < n; i++) {
                next[order[i]] = next[order[i - 1]] + (byPair.compare(order[i - 1], order[i]) < 0 ? 1 : 0);
            }
            System.arraycopy(next, 0, rank, 0, n);
            if (rank[order[n - 1]] == n - 1) break;        // all ranks distinct: fully sorted
        }
        int[] sa = new int[n];
        for (int i = 0; i < n; i++) sa[i] = order[i];
        return sa;
    }

    /** Kasai: lcp[i] = longest common prefix of suffixes sa[i - 1] and sa[i] (lcp[0] = 0). O(n). */
    static int[] lcpArray(String s, int[] sa) {
        int n = s.length();
        int[] pos = new int[n], lcp = new int[n];
        for (int i = 0; i < n; i++) pos[sa[i]] = i;
        int h = 0;
        for (int i = 0; i < n; i++) {                      // suffixes in text order: h drops by at most 1 each step
            if (pos[i] == 0) {
                h = 0;
                continue;
            }
            int j = sa[pos[i] - 1];                        // the suffix just before suffix i in sorted order
            while (i + h < n && j + h < n && s.charAt(i + h) == s.charAt(j + h)) h++;
            lcp[pos[i]] = h;
            if (h > 0) h--;
        }
        return lcp;
    }

    /** Approach 3: suffix array + LCP. Each suffix adds its prefixes that the previous sorted suffix does not share. O(n log^2 n) time, O(n) space. */
    static long optimal(String s) {
        int n = s.length();
        int[] sa = suffixArray(s);
        int[] lcp = lcpArray(s, sa);
        long total = 1;                                    // the empty substring
        for (int i = 0; i < n; i++) {
            total += (n - sa[i]) - lcp[i];                 // prefixes of suffix sa[i] not already counted
        }
        return total;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, long expected) {
        long[] got = {bruteForce(s), better(s), optimal(s)};
        String[] names = {"bruteForce", "better", "optimal"};
        for (int k = 0; k < 3; k++) {
            check(got[k] == expected, names[k] + "(\"" + s + "\") = " + got[k] + ", expected " + expected);
        }
    }

    public static void main(String[] args) {
        verify("aba", 6);          // "", a, b, ab, ba, aba
        verify("abc", 7);          // all 6 non-empty substrings differ, plus ""
        verify("abab", 8);
        verify("banana", 16);
        verify("aaaa", 5);         // one substring of each length 1..4, plus ""
        verify("z", 2);
        verify("", 1);             // edge: only the empty substring
        // deterministic random strings over a 3-letter alphabet: all three must agree
        Random rnd = new Random(1026);
        for (int t = 0; t < 200; t++) {
            int len = rnd.nextInt(60);
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < len; k++) sb.append((char) ('a' + rnd.nextInt(3)));
            String s = sb.toString();
            verify(s, bruteForce(s));
        }
        // scale check for the suffix array only (the trie walks n^2 / 2 = 2 * 10^8 characters here):
        // (ab)^10000 has exactly 2 distinct substrings of every length below n and 1 of length n, so 2n - 1 + 1 in total
        String ab = "ab".repeat(10_000);
        check(optimal(ab) == 2L * ab.length(), "(ab)^10000");
        check(optimal("q".repeat(20_000)) == 20_001, "q^20000");
        System.out.println("OK P1026_NumberOfDistinctSubstringsInAString");
    }
}
