import java.util.*;

/** TUF 510 - Word ladder II. Return every shortest beginWord -> endWord ladder (one letter changed per step), in any order. */
public class P510_WordLadderII {

    /** Approach 1: DFS over all simple ladders, keeping only the shortest ones found. Exponential time, O(N * L) space. */
    static List<List<String>> bruteForce(String beginWord, String endWord, List<String> wordList) {
        List<List<String>> best = new ArrayList<>();
        if (!wordList.contains(endWord)) return best;
        List<String> path = new ArrayList<>(List.of(beginWord));
        Set<String> onPath = new HashSet<>(path);
        explore(beginWord, endWord, wordList, path, onPath, best);
        return best;
    }

    static void explore(String word, String endWord, List<String> words, List<String> path,
                        Set<String> onPath, List<List<String>> best) {
        if (!best.isEmpty() && path.size() > best.get(0).size()) return;   // already longer than the best ladder
        if (word.equals(endWord)) {
            if (!best.isEmpty() && path.size() < best.get(0).size()) best.clear();
            best.add(new ArrayList<>(path));
            return;
        }
        for (String next : words) {
            if (!onPath.contains(next) && differByOne(word, next)) {
                path.add(next);
                onPath.add(next);
                explore(next, endWord, words, path, onPath, best);
                path.remove(path.size() - 1);
                onPath.remove(next);
            }
        }
    }

    static boolean differByOne(String a, String b) {
        if (a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length() && diff < 2; i++) {
            if (a.charAt(i) != b.charAt(i)) diff++;
        }
        return diff == 1;
    }

    /** Approach 2: BFS whose queue holds whole ladders; words are retired only after their level ends. Can blow up in memory. */
    static List<List<String>> bfsPaths(String beginWord, String endWord, List<String> wordList) {
        Set<String> unvisited = new HashSet<>(wordList);
        List<List<String>> result = new ArrayList<>();
        if (!unvisited.contains(endWord)) return result;
        unvisited.remove(beginWord);
        Deque<List<String>> q = new ArrayDeque<>();
        q.offer(new ArrayList<>(List.of(beginWord)));
        while (!q.isEmpty() && result.isEmpty()) {
            List<String> usedThisLevel = new ArrayList<>();
            for (int size = q.size(); size > 0; size--) {
                List<String> seq = q.poll();
                String last = seq.get(seq.size() - 1);
                if (last.equals(endWord)) { result.add(seq); continue; }
                char[] w = last.toCharArray();
                for (int i = 0; i < w.length; i++) {
                    char original = w[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        w[i] = ch;
                        String next = new String(w);
                        if (unvisited.contains(next)) {
                            List<String> longer = new ArrayList<>(seq);
                            longer.add(next);
                            q.offer(longer);
                            usedThisLevel.add(next);
                        }
                    }
                    w[i] = original;
                }
            }
            unvisited.removeAll(usedThisLevel);          // only now: ladders on the same level may share a word
        }
        return result;
    }

    /** Approach 3: BFS records each word's depth, then backtracking from endWord walks depth d -> d-1 only. Output-sensitive. */
    static List<List<String>> optimal(String beginWord, String endWord, List<String> wordList) {
        Set<String> unvisited = new HashSet<>(wordList);
        List<List<String>> result = new ArrayList<>();
        if (!unvisited.contains(endWord)) return result;
        unvisited.remove(beginWord);
        Map<String, Integer> depth = new HashMap<>();
        depth.put(beginWord, 0);
        Deque<String> q = new ArrayDeque<>();
        q.offer(beginWord);
        while (!q.isEmpty() && !depth.containsKey(endWord)) {
            String word = q.poll();
            int d = depth.get(word);
            char[] w = word.toCharArray();
            for (int i = 0; i < w.length; i++) {
                char original = w[i];
                for (char ch = 'a'; ch <= 'z'; ch++) {
                    w[i] = ch;
                    String next = new String(w);
                    if (unvisited.remove(next)) {
                        depth.put(next, d + 1);
                        q.offer(next);
                    }
                }
                w[i] = original;
            }
        }
        if (!depth.containsKey(endWord)) return result;
        List<String> path = new ArrayList<>(List.of(endWord));
        backtrack(endWord, beginWord, depth, path, result);
        return result;
    }

    static void backtrack(String word, String beginWord, Map<String, Integer> depth,
                          List<String> path, List<List<String>> result) {
        if (word.equals(beginWord)) {
            List<String> seq = new ArrayList<>(path);
            Collections.reverse(seq);                    // path was built from endWord backwards
            result.add(seq);
            return;
        }
        int d = depth.get(word);
        char[] w = word.toCharArray();
        for (int i = 0; i < w.length; i++) {
            char original = w[i];
            for (char ch = 'a'; ch <= 'z'; ch++) {
                w[i] = ch;
                String prev = new String(w);
                Integer pd = depth.get(prev);
                if (pd != null && pd == d - 1) {         // one level closer to beginWord
                    path.add(prev);
                    backtrack(prev, beginWord, depth, path, result);
                    path.remove(path.size() - 1);
                }
            }
            w[i] = original;
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<String> normalise(List<List<String>> ladders) {
        List<String> out = new ArrayList<>();
        for (List<String> l : ladders) out.add(String.join(">", l));
        Collections.sort(out);
        return out;
    }

    static void verify(String begin, String end, String[] words, String[][] expected) {
        List<String> list = List.of(words);
        List<List<String>> exp = new ArrayList<>();
        for (String[] e : expected) exp.add(List.of(e));
        List<String> want = normalise(exp);
        String tag = begin + " -> " + end + " " + list;
        check(normalise(bruteForce(begin, end, list)).equals(want), "bruteForce " + tag);
        check(normalise(bfsPaths(begin, end, list)).equals(want), "bfsPaths " + tag);
        check(normalise(optimal(begin, end, list)).equals(want), "optimal " + tag);
    }

    public static void main(String[] args) {
        verify("hit", "cog", new String[]{"hot", "dot", "dog", "lot", "log", "cog"},
               new String[][]{{"hit", "hot", "dot", "dog", "cog"}, {"hit", "hot", "lot", "log", "cog"}});
        verify("hit", "cog", new String[]{"hot", "dot", "dog", "lot", "log"}, new String[][]{});   // endWord missing
        verify("red", "tax", new String[]{"ted", "tex", "red", "tax", "tad", "den", "rex", "pee"},
               new String[][]{{"red", "ted", "tad", "tax"}, {"red", "ted", "tex", "tax"}, {"red", "rex", "tex", "tax"}});
        verify("der", "dfs", new String[]{"des", "der", "dfr", "dgt", "dfs"},
               new String[][]{{"der", "des", "dfs"}, {"der", "dfr", "dfs"}});
        verify("a", "c", new String[]{"a", "b", "c"}, new String[][]{{"a", "c"}});
        verify("hot", "dog", new String[]{"hot", "dog"}, new String[][]{});                      // unreachable
        verify("aa", "bb", new String[]{"ab", "ba", "bb"}, new String[][]{{"aa", "ab", "bb"}, {"aa", "ba", "bb"}});
        System.out.println("OK P510_WordLadderII");
    }
}
