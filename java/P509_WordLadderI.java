import java.util.*;

/** TUF 509 - Word ladder I. Length (in words) of the shortest beginWord -> endWord ladder changing one letter at a time, or 0. */
public class P509_WordLadderI {

    /** Approach 1: BFS where neighbours are found by comparing against every word in the list. O(N^2 * L) time, O(N) space. */
    static int bruteForce(String beginWord, String endWord, List<String> wordList) {
        int n = wordList.size();
        boolean[] used = new boolean[n];                 // used[i] = wordList[i] was already queued
        Deque<String> q = new ArrayDeque<>();            // BFS queue: one level per ladder length
        q.offer(beginWord);
        int steps = 1;                                   // words in the ladder so far
        while (!q.isEmpty()) {
            for (int size = q.size(); size > 0; size--) {   // process exactly one level
                String word = q.poll();
                if (word.equals(endWord)) return steps;   // first time endWord appears = shortest ladder
                for (int i = 0; i < n; i++) {            // scan the whole word list: O(N) per word
                    if (!used[i] && differByOne(word, wordList.get(i))) {   // one letter away -> next rung
                        used[i] = true;
                        q.offer(wordList.get(i));
                    }
                }
            }
            steps++;
        }
        return 0;
    }

    static boolean differByOne(String a, String b) {
        if (a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length() && diff < 2; i++) {   // stop early once two letters differ
            if (a.charAt(i) != b.charAt(i)) diff++;
        }
        return diff == 1;
    }

    /** Approach 2: BFS that generates neighbours by trying all 26 letters at each position. O(N * L * 26 * L) time, O(N * L) space. */
    static int optimal(String beginWord, String endWord, List<String> wordList) {
        Set<String> unvisited = new HashSet<>(wordList);   // words not reached yet
        if (!unvisited.contains(endWord)) return 0;      // endWord unreachable if not in the list
        unvisited.remove(beginWord);                     // never come back to the start word
        Deque<String> q = new ArrayDeque<>();
        q.offer(beginWord);
        int steps = 1;                                   // words in the ladder so far
        while (!q.isEmpty()) {
            for (int size = q.size(); size > 0; size--) {   // process exactly one level
                String word = q.poll();
                if (word.equals(endWord)) return steps;   // BFS reaches endWord first along a shortest ladder
                char[] w = word.toCharArray();
                for (int i = 0; i < w.length; i++) {     // try every position...
                    char original = w[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {   // ...with every letter
                        w[i] = ch;
                        String next = new String(w);
                        if (unvisited.remove(next)) q.offer(next);   // remove() doubles as "mark visited"
                    }
                    w[i] = original;                     // restore before moving to the next position
                }
            }
            steps++;
        }
        return 0;
    }

    /** Approach 3: bidirectional BFS, always expanding the smaller frontier. Same worst case, far fewer words in practice. */
    static int bidirectional(String beginWord, String endWord, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);      // words neither search has reached yet
        if (!dict.contains(endWord)) return 0;
        // frontiers growing from each end
        Set<String> front = new HashSet<>(List.of(beginWord)), back = new HashSet<>(List.of(endWord));
        dict.remove(beginWord);
        dict.remove(endWord);
        int steps = 1;                                   // words on both partial ladders, minus one
        while (!front.isEmpty() && !back.isEmpty()) {
            // always expand the smaller frontier
            if (front.size() > back.size()) { Set<String> t = front; front = back; back = t; }
            Set<String> next = new HashSet<>();
            for (String word : front) {                  // expand every word of the frontier by one step
                char[] w = word.toCharArray();
                for (int i = 0; i < w.length; i++) {
                    char original = w[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {   // all one-letter changes at position i
                        w[i] = ch;
                        String cand = new String(w);
                        if (back.contains(cand)) return steps + 1;   // the two searches meet
                        if (dict.remove(cand)) next.add(cand);   // new word: add it to the next frontier
                    }
                    w[i] = original;                     // restore before moving to the next position
                }
            }
            front = next;                                // the frontier moves one level further
            steps++;
        }
        return 0;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String begin, String end, String[] words, int expected) {
        List<String> list = List.of(words);
        String tag = begin + " -> " + end + " " + list;
        check(bruteForce(begin, end, list) == expected, "bruteForce " + tag);
        check(optimal(begin, end, list) == expected, "optimal " + tag);
        check(bidirectional(begin, end, list) == expected, "bidirectional " + tag);
    }

    public static void main(String[] args) {
        verify("hit", "cog", new String[]{"hot", "dot", "dog", "lot", "log", "cog"}, 5);
        verify("hit", "cog", new String[]{"hot", "dot", "dog", "lot", "log"}, 0);       // endWord missing
        verify("der", "dfs", new String[]{"des", "der", "dfr", "dgt", "dfs"}, 3);       // beginWord is also in the list
        verify("red", "tax", new String[]{"ted", "tex", "red", "tax", "tad", "den", "rex", "pee"}, 4);
        verify("a", "c", new String[]{"a", "b", "c"}, 2);                               // one-letter words
        verify("hot", "dog", new String[]{"hot", "dog"}, 0);                            // differ in two letters, no bridge
        verify("hot", "dot", new String[]{"dot"}, 2);                                   // a single step
        verify("lost", "miss", new String[]{"most", "mist", "miss", "lost", "fist", "fish"}, 4);
        System.out.println("OK P509_WordLadderI");
    }
}
