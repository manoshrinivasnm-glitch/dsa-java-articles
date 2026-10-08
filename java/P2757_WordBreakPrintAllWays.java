import java.util.*;

/** TUF 2757 - Word Break (print all ways). Every way to insert spaces into s so that each piece is a dictionary word. */
public class P2757_WordBreakPrintAllWays {

    /** Approach 1: try every dictionary word as the next piece and recurse; record a sentence when s is used up. Exponential. */
    static List<String> bruteForce(String s, List<String> wordDict) {
        List<String> out = new ArrayList<>();
        backtrack(s, 0, wordDict, new ArrayList<>(), out);
        return out;
    }

    private static void backtrack(String s, int start, List<String> dict, List<String> words, List<String> out) {
        if (start == s.length()) {                           // the chosen words cover s exactly
            out.add(String.join(" ", words));
            return;
        }
        for (String w : dict) {
            if (w.isEmpty() || !s.startsWith(w, start)) continue;
            words.add(w);
            backtrack(s, start + w.length(), dict, words, out);
            words.remove(words.size() - 1);                  // undo the choice before trying the next word
        }
    }

    /** Approach 2: memoise the list of sentences for each suffix start (top-down DP), so each suffix is solved once. */
    static List<String> better(String s, List<String> wordDict) {
        return sentencesFrom(s, 0, new HashSet<>(wordDict), new HashMap<>());
    }

    private static List<String> sentencesFrom(String s, int start, Set<String> dict, Map<Integer, List<String>> memo) {
        if (start == s.length()) return List.of("");         // exactly one way to split nothing: use no words
        List<String> cached = memo.get(start);
        if (cached != null) return cached;
        List<String> result = new ArrayList<>();
        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);
            if (!dict.contains(word)) continue;
            for (String rest : sentencesFrom(s, end, dict, memo)) {
                result.add(rest.isEmpty() ? word : word + " " + rest);
            }
        }
        memo.put(start, result);
        return result;
    }

    /** Approach 3: Word Break DP marks which suffixes can be split; backtracking enters only those, so no branch is wasted. */
    static List<String> optimal(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);
        int maxLen = 0;
        for (String w : dict) maxLen = Math.max(maxLen, w.length());
        int n = s.length();
        boolean[] canBreak = new boolean[n + 1];             // canBreak[i]: the suffix s[i, n) splits into words
        canBreak[n] = true;
        for (int i = n - 1; i >= 0; i--) {
            for (int end = i + 1; end <= n && end - i <= maxLen; end++) {
                if (canBreak[end] && dict.contains(s.substring(i, end))) {
                    canBreak[i] = true;
                    break;
                }
            }
        }
        List<String> out = new ArrayList<>();
        if (canBreak[0]) collect(s, 0, dict, maxLen, canBreak, new StringBuilder(), out);
        return out;
    }

    private static void collect(String s, int start, Set<String> dict, int maxLen, boolean[] canBreak,
                                StringBuilder sentence, List<String> out) {
        if (start == s.length()) {
            out.add(sentence.toString());
            return;
        }
        int before = sentence.length();
        for (int end = start + 1; end <= s.length() && end - start <= maxLen; end++) {
            if (!canBreak[end] || !dict.contains(s.substring(start, end))) continue;   // dead suffix or not a word
            if (start > 0) sentence.append(' ');
            sentence.append(s, start, end);
            collect(s, end, dict, maxLen, canBreak, sentence, out);
            sentence.setLength(before);                      // undo: drop the word and its space
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<String> sorted(List<String> list) {
        List<String> copy = new ArrayList<>(list);
        Collections.sort(copy);
        return copy;
    }

    /** Sentences may come out in any order, so every result is sorted before comparing with the sorted expectation. */
    static void verify(String s, List<String> dict, List<String> expected) {
        List<String> want = sorted(expected);
        check(sorted(bruteForce(s, dict)).equals(want), "bruteForce \"" + s + "\"");
        check(sorted(better(s, dict)).equals(want), "better \"" + s + "\"");
        check(sorted(optimal(s, dict)).equals(want), "optimal \"" + s + "\"");
    }

    /** For inputs with many answers: the approaches agree, the count is right, and every sentence is valid and distinct. */
    static void verifyCount(String s, List<String> dict, int expectedCount) {
        List<String> a = sorted(bruteForce(s, dict)), b = sorted(better(s, dict)), c = sorted(optimal(s, dict));
        check(a.equals(b) && b.equals(c), "approaches disagree on \"" + s + "\"");
        check(c.size() == expectedCount, "expected " + expectedCount + " sentences for \"" + s + "\", got " + c.size());
        check(new HashSet<>(c).size() == c.size(), "duplicate sentence for \"" + s + "\"");
        Set<String> words = new HashSet<>(dict);
        for (String sentence : c) {
            check(sentence.replace(" ", "").equals(s), "\"" + sentence + "\" does not rebuild \"" + s + "\"");
            for (String w : sentence.split(" ")) check(words.contains(w), "\"" + w + "\" is not in the dictionary");
        }
    }

    public static void main(String[] args) {
        verify("catsanddog", List.of("cat", "cats", "and", "sand", "dog"),
                List.of("cats and dog", "cat sand dog"));
        verify("pineapplepenapple", List.of("apple", "pen", "applepen", "pine", "pineapple"),
                List.of("pine apple pen apple", "pineapple pen apple", "pine applepen apple"));
        verify("catsandog", List.of("cats", "dog", "sand", "and", "cat"), List.of());     // no segmentation at all
        verify("aaaa", List.of("a", "aa"), List.of("a a a a", "a a aa", "a aa a", "aa a a", "aa aa"));
        verify("abc", List.of("abc"), List.of("abc"));                                    // the whole string is one word
        verify("abc", List.of("x", "y"), List.of());                                      // nothing matches
        verify("", List.of("a"), List.of(""));                                            // empty string: the empty sentence
        verify("aaaaaaaaaaaaaaaaaaab", List.of("a", "aa", "aaa", "aaaa"), List.of());     // ~147,000 dead-end splits for brute force
        verifyCount("aaaaaaaaaa", List.of("a", "aa", "aaa"), 274);                         // tribonacci: compositions of 10 into 1..3
        verifyCount("catsanddogcatsanddog", List.of("cat", "cats", "and", "sand", "dog"), 4);
        System.out.println("OK P2757_WordBreakPrintAllWays");
    }
}
