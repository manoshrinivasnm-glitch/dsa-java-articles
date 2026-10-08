import java.util.*;

/** TUF 2856 - Power Set (this is very important). All non-empty subsequences of s, sorted lexicographically. */
public class P2856_PowerSetThisIsVeryImportant {

    /** Approach 1: pick / not-pick recursion. O(n * 2^n) to build plus the final sort, O(n) stack. */
    static List<String> recursion(String s) {
        List<String> out = new ArrayList<>();
        generate(0, s, new StringBuilder(), out);
        Collections.sort(out);
        return out;
    }

    static void generate(int i, String s, StringBuilder cur, List<String> out) {
        if (i == s.length()) {
            if (cur.length() > 0) out.add(cur.toString());     // every leaf is one subsequence; skip the empty one
            return;
        }
        cur.append(s.charAt(i));                               // pick s[i]
        generate(i + 1, s, cur, out);
        cur.deleteCharAt(cur.length() - 1);                    // undo the pick
        generate(i + 1, s, cur, out);                          // do not pick s[i]
    }

    /** Approach 2: every mask from 1 to 2^n - 1 is one subsequence; bit i set means s[i] is in it. Same cost, no recursion. */
    static List<String> bitmask(String s) {
        int n = s.length();
        List<String> out = new ArrayList<>();
        for (int mask = 1; mask < (1 << n); mask++) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) sb.append(s.charAt(i));
            }
            out.add(sb.toString());
        }
        Collections.sort(out);
        return out;
    }

    /** Approach 3: start from {""}; each character doubles the list by appending itself to every existing entry. */
    static List<String> iterative(String s) {
        List<String> all = new ArrayList<>();
        all.add("");
        for (char c : s.toCharArray()) {
            int size = all.size();                             // only extend what existed before this character
            for (int k = 0; k < size; k++) all.add(all.get(k) + c);
        }
        all.remove(0);                                         // drop the empty subsequence
        Collections.sort(all);
        return all;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, List<String> expected) {
        check(recursion(s).equals(expected), "recursion " + s + " -> " + recursion(s));
        check(bitmask(s).equals(expected), "bitmask " + s + " -> " + bitmask(s));
        check(iterative(s).equals(expected), "iterative " + s + " -> " + iterative(s));
    }

    public static void main(String[] args) {
        verify("abc", List.of("a", "ab", "abc", "ac", "b", "bc", "c"));
        verify("ba", List.of("a", "b", "ba"));                 // order inside a subsequence follows s, not the alphabet
        verify("a", List.of("a"));
        verify("", List.of());                                 // empty string: only the empty subsequence, which is excluded
        verify("aa", List.of("a", "a", "aa"));                 // positions are distinct, so "a" appears twice
        verify("xyz", List.of("x", "xy", "xyz", "xz", "y", "yz", "z"));
        String s = "abcdefghijklmnop";                         // 16 characters: 65535 subsequences
        List<String> r = recursion(s), b = bitmask(s), it = iterative(s);
        check(r.size() == (1 << 16) - 1, "size " + r.size());
        check(r.equals(b) && b.equals(it), "approaches disagree on 16 characters");
        check(r.get(0).equals("a") && r.get(1).equals("ab") && r.get(r.size() - 1).equals("p"), "sorted order");
        System.out.println("OK P2856_PowerSetThisIsVeryImportant");
    }
}
