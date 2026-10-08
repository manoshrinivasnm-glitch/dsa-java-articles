import java.util.*;

/** TUF 503 - Alien Dictionary. words is sorted in an alien order of the first k lowercase letters. Return one order of those k letters consistent with the list, or "" if none exists. */
public class P503_AlienDictionary {

    /** Approach 1: try every ordering of the k letters and return the first one under which the list is sorted. O(k! * N * L) time. */
    static String bruteForce(String[] words, int k) {
        char[] letters = new char[k];
        for (int i = 0; i < k; i++) letters[i] = (char) ('a' + i);
        String found = tryAll(letters, 0, words);
        return found == null ? "" : found;
    }

    /** Swap-based permutation: fixes letters[0..pos-1] and tries every remaining letter at position pos. */
    static String tryAll(char[] letters, int pos, String[] words) {
        if (pos == letters.length) return isSortedUnder(words, letters) ? new String(letters) : null;
        for (int i = pos; i < letters.length; i++) {
            swap(letters, pos, i);
            String found = tryAll(letters, pos + 1, words);
            swap(letters, pos, i);
            if (found != null) return found;
        }
        return null;
    }

    static void swap(char[] a, int i, int j) {
        char t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /** True if every adjacent pair of words is in non-decreasing order when letters rank as in order. */
    static boolean isSortedUnder(String[] words, char[] order) {
        int[] rank = new int[26];
        for (int i = 0; i < order.length; i++) rank[order[i] - 'a'] = i;
        for (int w = 0; w + 1 < words.length; w++) {
            String a = words[w], b = words[w + 1];
            int len = Math.min(a.length(), b.length()), i = 0;
            while (i < len && a.charAt(i) == b.charAt(i)) i++;
            if (i == len) {
                if (a.length() > b.length()) return false;    // a longer word may not precede its own prefix
            } else if (rank[a.charAt(i) - 'a'] > rank[b.charAt(i) - 'a']) {
                return false;
            }
        }
        return true;
    }

    /** Approach 2: one edge per adjacent word pair (first differing letter), then Kahn's algorithm. O(N * L + k) time, O(k + N) space. */
    static String optimal(String[] words, int k) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < k; i++) adj.add(new ArrayList<>());
        int[] indegree = new int[k];
        for (int w = 0; w + 1 < words.length; w++) {
            String a = words[w], b = words[w + 1];
            int len = Math.min(a.length(), b.length()), i = 0;
            while (i < len && a.charAt(i) == b.charAt(i)) i++;
            if (i == len) {
                if (a.length() > b.length()) return "";       // "abc" before "ab": no order can explain it
                continue;                                     // a is a prefix of b: no information
            }
            int u = a.charAt(i) - 'a', v = b.charAt(i) - 'a';
            adj.get(u).add(v);                                // letter u comes before letter v
            indegree[v]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int c = 0; c < k; c++) if (indegree[c] == 0) queue.add(c);
        StringBuilder order = new StringBuilder();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.append((char) ('a' + u));
            for (int v : adj.get(u)) {
                if (--indegree[v] == 0) queue.add(v);
            }
        }
        return order.length() == k ? order.toString() : "";   // letters left over sit on a cycle
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** A valid answer uses each of the first k letters once and sorts the list. */
    static boolean isValidAnswer(String[] words, int k, String order) {
        if (order.length() != k) return false;
        boolean[] used = new boolean[k];
        for (char ch : order.toCharArray()) {
            int c = ch - 'a';
            if (c < 0 || c >= k || used[c]) return false;
            used[c] = true;
        }
        return isSortedUnder(words, order.toCharArray());
    }

    static void verify(String[] words, int k, boolean possible, String uniqueOrder) {
        String name = Arrays.toString(words) + " k=" + k;
        for (String r : new String[]{bruteForce(words, k), optimal(words, k)}) {
            if (!possible) {
                check(r.isEmpty(), "expected \"\" for " + name + " got " + r);
            } else {
                check(isValidAnswer(words, k, r), "invalid order " + r + " for " + name);
                if (uniqueOrder != null) check(r.equals(uniqueOrder), "expected " + uniqueOrder + " for " + name + " got " + r);
            }
        }
    }

    public static void main(String[] args) {
        verify(new String[]{"baa", "abcd", "abca", "cab", "cad"}, 4, true, "bdac");
        verify(new String[]{"caa", "aaa", "aab"}, 3, true, "cab");
        verify(new String[]{"abc", "ab"}, 3, false, null);                    // longer word before its prefix
        verify(new String[]{"a", "b", "a"}, 2, false, null);                  // a < b and b < a
        verify(new String[]{"cba"}, 3, true, null);                           // one word: any order works
        verify(new String[]{"ab", "ab", "abc"}, 3, true, null);               // duplicates and prefixes give no edges
        verify(new String[]{"b", "a"}, 3, true, null);                        // c is unconstrained
        verify(new String[]{"ca", "cb", "ba", "bc"}, 3, true, "acb");         // a < b, c < b, a < c
        verify(new String[]{"f", "e", "d", "c", "b", "a"}, 6, true, "fedcba");  // fully reversed alphabet
        System.out.println("OK P503_AlienDictionary");
    }
}
