import java.util.*;

/** TUF 559 - Group Words by Anagrams (LeetCode 49). Groups are listed in order of first appearance, words inside a group in input order. */
public class P559_GroupWordsByAnagrams {

    /** Approach 1: open a group for each unused word and scan the rest for its anagrams. O(n^2 * k) time, O(n) extra space. */
    static List<List<String>> bruteForce(String[] words) {
        int n = words.length;
        boolean[] used = new boolean[n];
        List<List<String>> groups = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (used[i]) continue;
            List<String> group = new ArrayList<>();
            group.add(words[i]);
            used[i] = true;
            for (int j = i + 1; j < n; j++) {
                if (!used[j] && isAnagram(words[i], words[j])) {
                    group.add(words[j]);
                    used[j] = true;
                }
            }
            groups.add(group);
        }
        return groups;
    }

    /** Two lowercase words are anagrams when every letter occurs equally often in both. */
    static boolean isAnagram(String a, String b) {
        if (a.length() != b.length()) return false;
        int[] count = new int[26];
        for (int i = 0; i < a.length(); i++) {
            count[a.charAt(i) - 'a']++;
            count[b.charAt(i) - 'a']--;
        }
        for (int c : count) if (c != 0) return false;
        return true;
    }

    /** Approach 2: the sorted letters of a word are its key; equal keys mean anagrams. O(n * k log k) time, O(n * k) space. */
    static List<List<String>> better(String[] words) {
        Map<String, List<String>> groups = new LinkedHashMap<>();    // LinkedHashMap keeps first-appearance order
        for (String w : words) {
            char[] letters = w.toCharArray();
            Arrays.sort(letters);
            String key = new String(letters);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(w);
        }
        return new ArrayList<>(groups.values());
    }

    /** Approach 3: the 26 letter counts are the key, built in O(k) without sorting. O(n * k) time, O(n * k) space. */
    static List<List<String>> optimal(String[] words) {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        for (String w : words) {
            int[] count = new int[26];
            for (int i = 0; i < w.length(); i++) count[w.charAt(i) - 'a']++;
            StringBuilder key = new StringBuilder();
            for (int c : count) key.append(c).append('#');         // '#' keeps counts 1,11 apart from 11,1
            groups.computeIfAbsent(key.toString(), k -> new ArrayList<>()).add(w);
        }
        return new ArrayList<>(groups.values());
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String[] words, List<List<String>> expected) {
        String in = Arrays.toString(words);
        check(bruteForce(words).equals(expected), "bruteForce failed for " + in + ": " + bruteForce(words));
        check(better(words).equals(expected), "better failed for " + in + ": " + better(words));
        check(optimal(words).equals(expected), "optimal failed for " + in + ": " + optimal(words));
    }

    public static void main(String[] args) {
        verify(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"},
               List.of(List.of("eat", "tea", "ate"), List.of("tan", "nat"), List.of("bat")));
        verify(new String[]{"act", "god", "cat", "dog", "tac"},
               List.of(List.of("act", "cat", "tac"), List.of("god", "dog")));
        verify(new String[]{"aab", "abb", "bab", "aba"},          // same letters, different counts
               List.of(List.of("aab", "aba"), List.of("abb", "bab")));
        verify(new String[]{"abc", "abc", "cba"},                 // duplicates stay in the group
               List.of(List.of("abc", "abc", "cba")));
        verify(new String[]{"", "b", ""},                         // empty words are anagrams of each other
               List.of(List.of("", ""), List.of("b")));
        verify(new String[]{"a"}, List.of(List.of("a")));
        verify(new String[]{}, List.of());                       // edge: no words at all

        // Cross-check on seeded random words over a tiny alphabet so that many anagrams occur.
        Random rnd = new Random(559);
        for (int t = 0; t < 300; t++) {
            String[] words = new String[rnd.nextInt(12)];
            for (int i = 0; i < words.length; i++) {
                StringBuilder sb = new StringBuilder();
                int len = rnd.nextInt(5);
                for (int j = 0; j < len; j++) sb.append((char) ('a' + rnd.nextInt(3)));
                words[i] = sb.toString();
            }
            verify(words, bruteForce(words));
        }
        System.out.println("OK P559_GroupWordsByAnagrams");
    }
}
