import java.util.*;

/** TUF 821 - Permutations of a String. Return every distinct rearrangement of the characters of s (any order). */
public class P821_PermutationsOfAString {

    /** Approach 1: place any unused character at each position; a set drops repeats when characters repeat. O(n! * n) time. */
    static List<String> bruteForce(String s) {
        Set<String> found = new LinkedHashSet<>();           // keeps generation order, drops duplicate arrangements
        build(s.toCharArray(), new boolean[s.length()], new StringBuilder(), found);
        return new ArrayList<>(found);
    }

    private static void build(char[] chars, boolean[] used, StringBuilder cur, Set<String> found) {
        if (cur.length() == chars.length) {                  // every character placed exactly once
            found.add(cur.toString());
            return;
        }
        for (int i = 0; i < chars.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            cur.append(chars[i]);
            build(chars, used, cur, found);
            cur.deleteCharAt(cur.length() - 1);              // undo: free the slot and the character
            used[i] = false;
        }
    }

    /** Approach 2: swap each candidate into position `index`, recurse, swap back; skip a value already tried at this position. */
    static List<String> better(String s) {
        List<String> out = new ArrayList<>();
        permuteBySwapping(s.toCharArray(), 0, out);
        return out;
    }

    private static void permuteBySwapping(char[] a, int index, List<String> out) {
        if (index == a.length) {                             // positions 0..n-1 are all fixed
            out.add(new String(a));
            return;
        }
        Set<Character> tried = new HashSet<>();              // values already placed at position `index`
        for (int i = index; i < a.length; i++) {
            if (!tried.add(a[i])) continue;                  // same value here would repeat a whole subtree
            swap(a, index, i);
            permuteBySwapping(a, index + 1, out);
            swap(a, index, i);                               // restore so the next candidate sees the same suffix
        }
    }

    private static void swap(char[] a, int i, int j) {
        char t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /** Approach 3: start from the sorted string and step with next permutation until none is left. Lexicographic order, O(1) extra space. */
    static List<String> optimal(String s) {
        char[] a = s.toCharArray();
        Arrays.sort(a);                                      // the smallest arrangement comes first
        List<String> out = new ArrayList<>();
        do {
            out.add(new String(a));
        } while (nextPermutation(a));
        return out;
    }

    private static boolean nextPermutation(char[] a) {
        int i = a.length - 2;
        while (i >= 0 && a[i] >= a[i + 1]) i--;              // a[i+1..] is the longest non-increasing suffix
        if (i < 0) return false;                             // whole array non-increasing: this was the last one
        int j = a.length - 1;
        while (a[j] <= a[i]) j--;                            // rightmost character strictly bigger than a[i]
        swap(a, i, j);
        for (int lo = i + 1, hi = a.length - 1; lo < hi; lo++, hi--) swap(a, lo, hi);   // make the suffix ascending
        return true;
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

    /** expected must be in lexicographic order; optimal must produce exactly that order, the others any order. */
    static void verify(String s, List<String> expected) {
        check(sorted(bruteForce(s)).equals(expected), "bruteForce \"" + s + "\"");
        check(sorted(better(s)).equals(expected), "better \"" + s + "\"");
        check(optimal(s).equals(expected), "optimal \"" + s + "\"");
    }

    /** For longer inputs: same set from every approach, right count, no duplicates, each string uses the same letters as s. */
    static void verifyCount(String s, int expectedCount) {
        List<String> a = sorted(bruteForce(s)), b = sorted(better(s)), c = optimal(s);
        check(a.equals(b) && b.equals(c), "approaches disagree on \"" + s + "\"");
        check(c.size() == expectedCount, "expected " + expectedCount + " permutations of \"" + s + "\", got " + c.size());
        check(new HashSet<>(c).size() == c.size(), "duplicate permutation for \"" + s + "\"");
        char[] key = s.toCharArray();
        Arrays.sort(key);
        for (String p : c) {
            char[] k = p.toCharArray();
            Arrays.sort(k);
            check(Arrays.equals(k, key), p + " is not a rearrangement of " + s);
        }
    }

    public static void main(String[] args) {
        verify("abc", List.of("abc", "acb", "bac", "bca", "cab", "cba"));
        verify("cba", List.of("abc", "acb", "bac", "bca", "cab", "cba"));        // input order does not matter
        verify("aab", List.of("aab", "aba", "baa"));                             // repeated letter: 3!/2! = 3
        verify("ab", List.of("ab", "ba"));
        verify("a", List.of("a"));                                               // single character
        verify("", List.of(""));                                                 // empty string: one empty arrangement
        verify("aaaaaaa", List.of("aaaaaaa"));                                   // all equal: brute force makes 5040, keeps 1
        verify("aBa", List.of("Baa", "aBa", "aaB"));                              // uppercase sorts before lowercase
        verifyCount("abcd", 24);
        verifyCount("aabbcc", 90);                                               // 6! / (2! 2! 2!)
        verifyCount("abcdefgh", 40320);                                          // 8!, the largest size we test
        verifyCount("mississi", 280);                                            // 8! / (4! 3! 1!)
        System.out.println("OK P821_PermutationsOfAString");
    }
}
