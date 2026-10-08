import java.util.*;

/** TUF 509 - Word ladder I. Length (in words) of the shortest beginWord -> endWord ladder changing one letter at a time, or 0. */
public class P509_WordLadderI {

    /** Approach 1: BFS where neighbours are found by comparing against every word in the list. O(N^2 * L) time, O(N) space. */
    static int bruteForce(String beginWord, String endWord, List<String> wordList) {
        int n = wordList.size();
        boolean[] used = new boolean[n];
        Deque<String> q = new ArrayDeque<>();
        q.offer(beginWord);
        int steps = 1;                                   // words in the ladder so far
        while (!q.isEmpty()) {
            for (int size = q.size(); size > 0; size--) {
                String word = q.poll();
                if (word.equals(endWord)) return steps;
                for (int i = 0; i < n; i++) {
                    if (!used[i] && differByOne(word, wordList.get(i))) {
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
        for (int i = 0; i < a.length() && diff < 2; i++) {
            if (a.charAt(i) != b.charAt(i)) diff++;
        }
        return diff == 1;
    }

    /** Approach 2: BFS that generates neighbours by trying all 26 letters at each position. O(N * L * 26 * L) time, O(N * L) space. */
    static int optimal(String beginWord, String endWord, List<String> wordList) {
        Set<String> unvisited = new HashSet<>(wordList);
        if (!unvisited.contains(endWord)) return 0;
        unvisited.remove(beginWord);
        Deque<String> q = new ArrayDeque<>();
        q.offer(beginWord);
        int steps = 1;
        while (!q.isEmpty()) {
            for (int size = q.size(); size > 0; size--) {
                String word = q.poll();
                if (word.equals(endWord)) return steps;
                char[] w = word.toCharArray();
                for (int i = 0; i < w.length; i++) {
                    char original = w[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        w[i] = ch;
                        String next = new String(w);
                        if (unvisited.remove(next)) q.offer(next);   // remove() doubles as "mark visited"
                    }
                    w[i] = original;
                }
            }
            steps++;
        }
        return 0;
    }

    /** Approach 3: bidirectional BFS, always expanding the smaller frontier. Same worst case, far fewer words in practice. */
    static int bidirectional(String beginWord, String endWord, List<String> wordList) {
        Set<String> dict = new HashSet<>(wordList);
        if (!dict.contains(endWord)) return 0;
        Set<String> front = new HashSet<>(List.of(beginWord)), back = new HashSet<>(List.of(endWord));
        dict.remove(beginWord);
        dict.remove(endWord);
        int steps = 1;                                   // words on both partial ladders, minus one
        while (!front.isEmpty() && !back.isEmpty()) {
            if (front.size() > back.size()) { Set<String> t = front; front = back; back = t; }
            Set<String> next = new HashSet<>();
            for (String word : front) {
                char[] w = word.toCharArray();
                for (int i = 0; i < w.length; i++) {
                    char original = w[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        w[i] = ch;
                        String cand = new String(w);
                        if (back.contains(cand)) return steps + 1;   // the two searches meet
                        if (dict.remove(cand)) next.add(cand);
                    }
                    w[i] = original;
                }
            }
            front = next;
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
