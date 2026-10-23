import java.util.*;

/** TUF 1027 - Trie Implementation and Advanced Operations (Implement Trie II, LeetCode 1804).
 *  Support insert(word), countWordsEqualTo(word), countWordsStartingWith(prefix) and erase(word) on a multiset of words. */
public class P1027_TrieImplementationAndAdvancedOperations {

    /** Common contract so every implementation can be driven by the same test script. */
    interface WordCounter {
        void insert(String word);                          // add one copy of word (duplicates allowed)
        int countWordsEqualTo(String word);                // how many copies of word are stored
        int countWordsStartingWith(String prefix);         // how many stored words begin with prefix
        void erase(String word);                           // remove one copy of word, if any
    }

    /** Approach 1: a plain list of words, scanned on every query. O(1) insert, O(n * L) per query and erase. */
    static class ListCounter implements WordCounter {
        private final List<String> words = new ArrayList<>();   // every inserted copy, duplicates included

        public void insert(String word) {
            words.add(word);                               // O(1): just remember it
        }

        public int countWordsEqualTo(String word) {
            int count = 0;
            for (String w : words) {                       // scan every stored word
                if (w.equals(word)) count++;               // one more exact copy
            }
            return count;
        }

        public int countWordsStartingWith(String prefix) {
            int count = 0;
            for (String w : words) {                       // scan every stored word
                if (w.startsWith(prefix)) count++;         // this word has the prefix
            }
            return count;
        }

        public void erase(String word) {
            words.remove(word);                            // removes one copy; does nothing if the word is absent
        }
    }

    /** Approach 2: a count per whole word and a count per prefix (including ""), both in hash maps. O(L^2) insert and erase, O(L) per query. */
    static class MapCounter implements WordCounter {
        private final Map<String, Integer> wordCount = new HashMap<>();   // whole word -> copies stored
        private final Map<String, Integer> prefixCount = new HashMap<>();   // prefix -> words that start with it

        public void insert(String word) {
            wordCount.merge(word, 1, Integer::sum);        // one more copy of the whole word
            for (int len = 0; len <= word.length(); len++) {   // every prefix, from "" up to the whole word
                // each substring is a fresh copy of len characters
                prefixCount.merge(word.substring(0, len), 1, Integer::sum);
            }
        }

        public int countWordsEqualTo(String word) {
            return wordCount.getOrDefault(word, 0);        // 0 when the word was never inserted
        }

        public int countWordsStartingWith(String prefix) {
            return prefixCount.getOrDefault(prefix, 0);    // 0 when no word has this prefix
        }

        public void erase(String word) {
            if (countWordsEqualTo(word) == 0) return;      // nothing to erase: leave every count untouched
            wordCount.computeIfPresent(word, (k, v) -> v == 1 ? null : v - 1);   // one fewer copy; 0 drops the key
            for (int len = 0; len <= word.length(); len++) {   // the same prefixes insert counted
                // one fewer word has this prefix; drop keys that reach 0
                prefixCount.computeIfPresent(word.substring(0, len), (k, v) -> v == 1 ? null : v - 1);
            }
        }
    }

    /** Approach 3: a trie whose nodes count the words ending there and the words passing through. O(L) for every operation. */
    static class TrieCounter implements WordCounter {
        private static class Node {
            final Node[] child = new Node[26];             // child[c - 'a'] is the edge labelled c
            int endCount;                                  // words that end exactly at this node
            int prefixCount;                               // words that pass through or end at this node
        }

        private final Node root = new Node();

        public void insert(String word) {
            Node cur = root;
            cur.prefixCount++;                             // every word has the empty prefix
            for (int i = 0; i < word.length(); i++) {      // one level down per letter
                int c = word.charAt(i) - 'a';              // letter -> child index 0..25
                if (cur.child[c] == null) cur.child[c] = new Node();   // create the edge only when it is missing
                cur = cur.child[c];
                cur.prefixCount++;                         // one more word passes through this node
            }
            cur.endCount++;                                // and one more word ends here
        }

        public int countWordsEqualTo(String word) {
            Node node = walk(word);                        // null if some letter has no edge
            return node == null ? 0 : node.endCount;       // copies of word that end at its node
        }

        public int countWordsStartingWith(String prefix) {
            Node node = walk(prefix);                      // null if some letter has no edge
            return node == null ? 0 : node.prefixCount;    // words passing through the prefix's node
        }

        public void erase(String word) {
            if (countWordsEqualTo(word) == 0) return;      // nothing to erase: leave every count untouched
            Node cur = root;
            cur.prefixCount--;                             // one fewer word has the empty prefix
            for (int i = 0; i < word.length(); i++) {
                int c = word.charAt(i) - 'a';
                Node next = cur.child[c];                  // exists, since the word is in the trie
                if (--next.prefixCount == 0) {             // no word uses this branch any more: drop all of it
                    cur.child[c] = null;
                    return;
                }
                cur = next;
            }
            cur.endCount--;                                // the word's last node: one fewer copy ends here
        }

        /** Follows s from the root; returns the node reached, or null as soon as a letter has no edge. */
        private Node walk(String s) {
            Node cur = root;
            for (int i = 0; i < s.length(); i++) {         // one level down per letter
                cur = cur.child[s.charAt(i) - 'a'];        // follow the edge for this letter
                if (cur == null) return null;              // missing edge: s is not in the trie
            }
            return cur;                                    // the node where s ends
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
