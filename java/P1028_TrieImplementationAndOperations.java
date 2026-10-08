import java.util.*;

/** TUF 1028 - Trie Implementation and Operations (LeetCode 208). Support insert(word), search(word) and
 *  startsWith(prefix) on non-empty lowercase words. */
public class P1028_TrieImplementationAndOperations {

    /** Common contract so every implementation can be driven by the same test script. */
    interface WordStore {
        void insert(String word);
        boolean search(String word);
        boolean startsWith(String prefix);
    }

    /** Approach 1: keep the words in a list and scan all of them on every query. O(1) insert, O(n * L) per query. */
    static class ListStore implements WordStore {
        private final List<String> words = new ArrayList<>();

        public void insert(String word) {
            words.add(word);
        }

        public boolean search(String word) {
            for (String w : words) {
                if (w.equals(word)) return true;
            }
            return false;
        }

        public boolean startsWith(String prefix) {
            for (String w : words) {
                if (w.startsWith(prefix)) return true;
            }
            return false;
        }
    }

    /** Approach 2: one hash set of whole words and one of every prefix of every word. O(L^2) insert, O(L) per query. */
    static class PrefixSetStore implements WordStore {
        private final Set<String> words = new HashSet<>();
        private final Set<String> prefixes = new HashSet<>();

        public void insert(String word) {
            words.add(word);
            for (int len = 1; len <= word.length(); len++) {
                prefixes.add(word.substring(0, len));      // each prefix is a fresh copy of len characters
            }
        }

        public boolean search(String word) {
            return words.contains(word);
        }

        public boolean startsWith(String prefix) {
            return prefixes.contains(prefix);
        }
    }

    /** Approach 3: a trie. Every node has 26 child links and an end-of-word flag. O(L) for every operation. */
    static class Trie implements WordStore {
        private static class Node {
            final Node[] child = new Node[26];             // child[c - 'a'] is the edge labelled c
            boolean isEnd;                                 // some inserted word ends exactly here
        }

        private final Node root = new Node();

        public void insert(String word) {
            Node cur = root;
            for (int i = 0; i < word.length(); i++) {
                int c = word.charAt(i) - 'a';
                if (cur.child[c] == null) cur.child[c] = new Node();   // create the edge only when it is missing
                cur = cur.child[c];
            }
            cur.isEnd = true;
        }

        public boolean search(String word) {
            Node node = walk(word);
            return node != null && node.isEnd;             // the path must exist AND a word must end on it
        }

        public boolean startsWith(String prefix) {
            return walk(prefix) != null;                   // the path existing is enough
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

    /** Runs a script against a store and records what each operation returns ("-" for insert). */
    static List<String> simulate(WordStore store, String[] ops, String[] args) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case "insert" -> { store.insert(args[i]); out.add("-"); }
                case "search" -> out.add(String.valueOf(store.search(args[i])));
                case "startsWith" -> out.add(String.valueOf(store.startsWith(args[i])));
                default -> throw new IllegalArgumentException("unknown op " + ops[i]);
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<WordStore> freshStores() {
        return List.<WordStore>of(new ListStore(), new PrefixSetStore(), new Trie());
    }

    static void verify(String[] ops, String[] args, List<String> expected) {
        for (WordStore store : freshStores()) {
            List<String> got = simulate(store, ops, args);
            check(got.equals(expected), store.getClass().getSimpleName() + " got " + got + " expected " + expected);
        }
    }

    public static void main(String[] args) {
        // 1. LeetCode 208 example
        verify(new String[]{"insert", "search", "search", "startsWith", "insert", "search"},
               new String[]{"apple", "apple", "app", "app", "app", "app"},
               List.of("-", "true", "false", "true", "-", "true"));
        // 2. a word that is only a prefix of another is not found by search, but is by startsWith
        verify(new String[]{"insert", "insert", "search", "search", "startsWith", "startsWith", "search"},
               new String[]{"striver", "strike", "stri", "strike", "stri", "strix", "striker"},
               List.of("-", "-", "false", "true", "true", "false", "false"));
        // 3. edge: every query on an empty store is false
        verify(new String[]{"search", "startsWith", "insert", "search", "startsWith"},
               new String[]{"a", "a", "z", "a", "z"},
               List.of("false", "false", "-", "false", "true"));
        // 4. duplicates and the extreme letters 'a' and 'z'
        verify(new String[]{"insert", "insert", "search", "startsWith", "search", "startsWith"},
               new String[]{"zza", "zza", "zza", "zz", "zz", "zzaz"},
               List.of("-", "-", "true", "true", "false", "false"));
        // 5. single-letter words and branching at the root
        verify(new String[]{"insert", "insert", "insert", "search", "search", "startsWith", "search"},
               new String[]{"a", "b", "ab", "a", "b", "ab", "ba"},
               List.of("-", "-", "-", "true", "true", "true", "false"));
        // 6. deterministic random workload: every store must agree with the list scan
        Random rnd = new Random(1028);
        List<WordStore> stores = freshStores();
        for (int step = 0; step < 4000; step++) {
            int len = 1 + rnd.nextInt(5);
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < len; k++) sb.append((char) ('a' + rnd.nextInt(3)));
            String w = sb.toString();
            int op = rnd.nextInt(3);
            List<String> answers = new ArrayList<>();
            for (WordStore store : stores) {
                if (op == 0) { store.insert(w); answers.add("-"); }
                else if (op == 1) answers.add(String.valueOf(store.search(w)));
                else answers.add(String.valueOf(store.startsWith(w)));
            }
            check(answers.get(0).equals(answers.get(1)) && answers.get(0).equals(answers.get(2)),
                  "random step " + step + " disagreement " + answers + " on " + w);
        }
        System.out.println("OK P1028_TrieImplementationAndOperations");
    }
}
