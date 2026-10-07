import java.util.*;

/** TUF 865 - Combination Sum II. Unique combinations of candidates (each index used at most once, duplicates allowed in the input) that sum to target. */
public class P865_CombinationSumII {

    /** Approach 1: pick / not-pick over the sorted array, generate every combination and let a set remove the duplicates. */
    static List<List<Integer>> bruteForce(int[] candidates, int target) {
        int[] c = candidates.clone();
        Arrays.sort(c);
        Set<List<Integer>> seen = new LinkedHashSet<>();
        bruteRec(c, 0, target, new ArrayList<>(), seen);
        return new ArrayList<>(seen);
    }

    static void bruteRec(int[] c, int i, int target, List<Integer> cur, Set<List<Integer>> seen) {
        if (i == c.length) {
            if (target == 0) seen.add(new ArrayList<>(cur));
            return;
        }
        if (c[i] <= target) {                          // pick index i, then move on: an index is used at most once
            cur.add(c[i]);
            bruteRec(c, i + 1, target - c[i], cur, seen);
            cur.remove(cur.size() - 1);
        }
        bruteRec(c, i + 1, target, cur, seen);         // skip index i
    }

    /** Approach 2: sorted loop recursion; equal values are skipped at the same level, so every combination is produced exactly once. */
    static List<List<Integer>> optimal(int[] candidates, int target) {
        int[] c = candidates.clone();
        Arrays.sort(c);
        List<List<Integer>> out = new ArrayList<>();
        loopRec(c, 0, target, new ArrayList<>(), out);
        return out;
    }

    static void loopRec(int[] c, int start, int target, List<Integer> cur, List<List<Integer>> out) {
        if (target == 0) {
            out.add(new ArrayList<>(cur));
            return;
        }
        for (int j = start; j < c.length; j++) {
            if (j > start && c[j] == c[j - 1]) continue;  // this value was already tried at this level
            if (c[j] > target) break;                      // sorted: later values are larger still
            cur.add(c[j]);
            loopRec(c, j + 1, target - c[j], cur, out);    // j + 1: each index is used at most once
            cur.remove(cur.size() - 1);
        }
    }

    /** Approach 3: pick / not-pick where "not pick" jumps past every remaining copy of the current value. */
    static List<List<Integer>> pickNotPickSkip(int[] candidates, int target) {
        int[] c = candidates.clone();
        Arrays.sort(c);
        List<List<Integer>> out = new ArrayList<>();
        skipRec(c, 0, target, new ArrayList<>(), out);
        return out;
    }

    static void skipRec(int[] c, int i, int target, List<Integer> cur, List<List<Integer>> out) {
        if (target == 0) {
            out.add(new ArrayList<>(cur));
            return;
        }
        if (i == c.length || c[i] > target) return;     // sorted: nothing from here on can fit
        cur.add(c[i]);                                  // pick c[i]
        skipRec(c, i + 1, target - c[i], cur, out);
        cur.remove(cur.size() - 1);
        int next = i + 1;                               // not pick: also skip every later element equal to c[i]
        while (next < c.length && c[next] == c[i]) next++;
        skipRec(c, next, target, cur, out);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Order-independent fingerprint: each combination sorted and printed, then the list of prints sorted. */
    static List<String> canon(List<List<Integer>> lists) {
        List<String> keys = new ArrayList<>();
        for (List<Integer> l : lists) {
            List<Integer> sorted = new ArrayList<>(l);
            Collections.sort(sorted);
            keys.add(sorted.toString());
        }
        Collections.sort(keys);
        return keys;
    }

    static void verify(int[] candidates, int target, String... expected) {
        List<String> exp = new ArrayList<>(Arrays.asList(expected));
        Collections.sort(exp);
        String label = Arrays.toString(candidates) + " target=" + target;
        check(canon(bruteForce(candidates, target)).equals(exp), "bruteForce " + label);
        check(canon(optimal(candidates, target)).equals(exp), "optimal " + label);
        check(canon(pickNotPickSkip(candidates, target)).equals(exp), "pickNotPickSkip " + label);
        check(optimal(candidates, target).size() == expected.length, "optimal emitted a duplicate for " + label);
        check(pickNotPickSkip(candidates, target).size() == expected.length, "pickNotPickSkip emitted a duplicate for " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{10, 1, 2, 7, 6, 1, 5}, 8, "[1, 1, 6]", "[1, 2, 5]", "[1, 7]", "[2, 6]");
        verify(new int[]{2, 5, 2, 1, 2}, 5, "[1, 2, 2]", "[5]");
        verify(new int[]{1, 1, 1, 1}, 2, "[1, 1]");                       // six index pairs, one combination
        verify(new int[]{3, 4}, 2);                                        // nothing fits
        verify(new int[]{1}, 1, "[1]");                                    // single element
        verify(new int[]{2, 2, 2}, 7);                                     // an index cannot be reused: 2 + 2 + 2 = 6 is the most
        verify(new int[]{4, 4, 2, 1, 4, 2, 2, 1, 3}, 5, "[1, 1, 3]", "[1, 2, 2]", "[1, 4]", "[2, 3]");
        System.out.println("OK P865_CombinationSumII");
    }
}
