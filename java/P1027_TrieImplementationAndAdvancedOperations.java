import java.util.*;

/** TUF 1027 - Trie Implementation and Advanced Operations (Implement Trie II, LeetCode 1804).
 *  Support insert(word), countWordsEqualTo(word), countWordsStartingWith(prefix) and erase(word) on a multiset of words. */
public class P1027_TrieImplementationAndAdvancedOperations {

    /** Common contract so every implementation can be driven by the same test script. */
    interface WordCounter {
        void insert(String word);
        int countWordsEqualTo(String word);
        int countWordsStartingWith(String prefix);
        void erase(String word);
    }

    /** Approach 1: a plain list of words, scanned on every query. O(1) insert, O(n * L) per query and erase. */
    static class ListCounter implements WordCounter {
        private final List<String> words = new ArrayList<>();

        public void insert(String word) {
            words.add(word);
        }

        public int countWordsEqualTo(String word) {
            int count = 0;
            for (String w : words) {
                if (w.equals(word)) count++;
            }
            return count;
        }

        public int countWordsStartingWith(String prefix) {
            int count = 0;
            for (String w : words) {
                if (w.startsWith(prefix)) count++;
            }
            return count;
        }

        public void erase(String word) {
            words.remove(word);                            // removes one copy; does nothing if the word is absent
        }
    }

    /** Approach 2: a count per whole word and a count per prefix (including ""), both in hash maps. O(L^2) insert and erase, O(L) per query. */
    static class MapCounter implements WordCounter {
        private final Map<String, Integer> wordCount = new HashMap<>();
        private final Map<String, Integer> prefixCount = new HashMap<>();

        public void insert(String word) {
            wordCount.merge(word, 1, Integer::sum);
            for (int len = 0; len <= word.length(); len++) {
                prefixCount.merge(word.substring(0, len), 1, Integer::sum);
            }
        }

        public int countWordsEqualTo(String word) {
            return wordCount.getOrDefault(word, 0);
        }

        public int countWordsStartingWith(String prefix) {
            return prefixCount.getOrDefault(prefix, 0);
        }

        public void erase(String word) {
            if (countWordsEqualTo(word) == 0) return;      // nothing to erase: leave every count untouched
            wordCount.computeIfPresent(word, (k, v) -> v == 1 ? null : v - 1);
            for (int len = 0; len <= word.length(); len++) {
                prefixCount.computeIfPresent(word.substring(0, len), (k, v) -> v == 1 ? null : v - 1);
            }
        }
    }

    /** Approach 3: a trie whose nodes count the words ending there and the words passing through. O(L) for every operation. */
    static class TrieCounter implements WordCounter {
        private static class Node {
            final Node[] child = new Node[26];
            int endCount;                                  // words that end exactly at this node
            int prefixCount;                               // words that pass through or end at this node
        }

        private final Node root = new Node();

        public void insert(String word) {
            Node cur = root;
            cur.prefixCount++;                             // every word has the empty prefix
            for (int i = 0; i < word.length(); i++) {
                int c = word.charAt(i) - 'a';
                if (cur.child[c] == null) cur.child[c] = new Node();
                cur = cur.child[c];
                cur.prefixCount++;
            }
            cur.endCount++;
        }

        public int countWordsEqualTo(String word) {
            Node node = walk(word);
            return node == null ? 0 : node.endCount;
        }

        public int countWordsStartingWith(String prefix) {
            Node node = walk(prefix);
            return node == null ? 0 : node.prefixCount;
        }

        public void erase(String word) {
            if (countWordsEqualTo(word) == 0) return;      // nothing to erase: leave every count untouched
            Node cur = root;
            cur.prefixCount--;
            for (int i = 0; i < word.length(); i++) {
                int c = word.charAt(i) - 'a';
                Node next = cur.child[c];
                if (--next.prefixCount == 0) {             // no word uses this branch any more: drop all of it
                    cur.child[c] = null;
                    return;
                }
                cur = next;
            }
            cur.endCount--;
        }

        /** Follows s from the root; returns the node reached, or null as soon as a letter has no edge. */
        private Node walk(String s) {
            Node cur = root;
            for (int i = 0; i < s.length(); i++) {
                cur = cur.child[s.charAt(i) - 'a'];
                if (cur == null) return null;
            }
            return cur;
        }
    }

    /** Runs a script and records what each operation returns ("-" for insert and erase). */
    static List<String> simulate(WordCounter wc, String[] ops, String[] args) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case "insert" -> { wc.insert(args[i]); out.add("-"); }
                case "erase" -> { wc.erase(args[i]); out.add("-"); }
                case "countWordsEqualTo" -> out.add(String.valueOf(wc.countWordsEqualTo(args[i])));
                case "countWordsStartingWith" -> out.add(String.valueOf(wc.countWordsStartingWith(args[i])));
                default -> throw new IllegalArgumentException("unknown op " + ops[i]);
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<WordCounter> freshCounters() {
        return List.<WordCounter>of(new ListCounter(), new MapCounter(), new TrieCounter());
    }

    static void verify(String[] ops, String[] args, List<String> expected) {
        for (WordCounter wc : freshCounters()) {
            List<String> got = simulate(wc, ops, args);
            check(got.equals(expected), wc.getClass().getSimpleName() + " got " + got + " expected " + expected);
        }
    }

    public static void main(String[] args) {
        final String I = "insert", E = "erase", EQ = "countWordsEqualTo", SW = "countWordsStartingWith";
        // 1. LeetCode 1804 example
        verify(new String[]{I, I, EQ, SW, E, EQ, SW, E, SW},
               new String[]{"apple", "apple", "apple", "app", "apple", "apple", "app", "apple", "app"},
               List.of("-", "-", "2", "2", "-", "1", "1", "-", "0"));
        // 2. words that are prefixes of each other share a path but keep separate end counts
        verify(new String[]{I, I, I, EQ, EQ, SW, SW, E, SW, EQ},
               new String[]{"ab", "abc", "abcd", "ab", "abcd", "ab", "abc", "abc", "ab", "abcd"},
               List.of("-", "-", "-", "1", "1", "3", "2", "-", "2", "1"));
        // 3. edge: erasing a word that was never inserted (or is only a prefix) changes nothing
        verify(new String[]{E, I, E, E, EQ, SW, SW},
               new String[]{"x", "apple", "app", "apples", "apple", "app", "b"},
               List.of("-", "-", "-", "-", "1", "1", "0"));
        // 4. edge: empty store, and the empty prefix counts every word
        verify(new String[]{EQ, SW, I, I, I, SW, E, SW, EQ},
               new String[]{"a", "", "z", "zz", "a", "", "zz", "", "zz"},
               List.of("0", "0", "-", "-", "-", "3", "-", "2", "0"));
        // 5. erase until a branch dies, then rebuild it
        verify(new String[]{I, E, SW, EQ, I, SW, EQ},
               new String[]{"tree", "tree", "t", "tree", "trie", "tr", "tree"},
               List.of("-", "-", "0", "0", "-", "1", "0"));
        // 6. deterministic random workload: every counter must agree with the list scan
        Random rnd = new Random(1027);
        List<WordCounter> counters = freshCounters();
        for (int step = 0; step < 5000; step++) {
            int len = rnd.nextInt(5);                     // length 0 exercises the empty word as well
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < len; k++) sb.append((char) ('a' + rnd.nextInt(2)));
            String w = sb.toString();
            int op = rnd.nextInt(4);
            List<Integer> answers = new ArrayList<>();
            for (WordCounter wc : counters) {
                switch (op) {
                    case 0 -> { wc.insert(w); answers.add(-1); }
                    case 1 -> { wc.erase(w); answers.add(-1); }
                    case 2 -> answers.add(wc.countWordsEqualTo(w));
                    default -> answers.add(wc.countWordsStartingWith(w));
                }
            }
            check(answers.get(0).equals(answers.get(1)) && answers.get(0).equals(answers.get(2)),
                  "random step " + step + " disagreement " + answers + " on '" + w + "'");
        }
        System.out.println("OK P1027_TrieImplementationAndAdvancedOperations");
    }
}
