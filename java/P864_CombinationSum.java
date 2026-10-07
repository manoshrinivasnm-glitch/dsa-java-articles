import java.util.*;

/** TUF 864 - Combination Sum. All unique combinations of distinct positive candidates (each reusable any number of times) that sum to target. */
public class P864_CombinationSum {

    /** Approach 1: decide how many copies of each candidate to take, check the sum only at the end. */
    static List<List<Integer>> bruteForce(int[] candidates, int target) {
        List<List<Integer>> out = new ArrayList<>();
        bruteRec(candidates, 0, target, new ArrayList<>(), out);
        return out;
    }

    static void bruteRec(int[] c, int i, int target, List<Integer> cur, List<List<Integer>> out) {
        if (i == c.length) {
            int sum = 0;
            for (int x : cur) sum += x;
            if (sum == target) out.add(new ArrayList<>(cur));
            return;
        }
        int maxCopies = target / c[i];                // more copies than this can never fit under target
        for (int copies = 0; copies <= maxCopies; copies++) {
            bruteRec(c, i + 1, target, cur, out);
            cur.add(c[i]);                            // one more copy of c[i] for the next round
        }
        for (int copies = 0; copies <= maxCopies; copies++) cur.remove(cur.size() - 1);
    }

    /** Approach 2: pick (stay at i, so it can be picked again) / not-pick (move to i + 1), pruning when the target would go negative. */
    static List<List<Integer>> pickNotPick(int[] candidates, int target) {
        List<List<Integer>> out = new ArrayList<>();
        pickRec(candidates, 0, target, new ArrayList<>(), out);
        return out;
    }

    static void pickRec(int[] c, int i, int target, List<Integer> cur, List<List<Integer>> out) {
        if (i == c.length) {
            if (target == 0) out.add(new ArrayList<>(cur));
            return;
        }
        if (c[i] <= target) {                          // pick c[i] and stay at i: the same value may be reused
            cur.add(c[i]);
            pickRec(c, i, target - c[i], cur, out);
            cur.remove(cur.size() - 1);
        }
        pickRec(c, i + 1, target, cur, out);           // leave c[i] out for good
    }

    /** Approach 3: sort, then at each level loop over candidates j >= start and break as soon as c[j] > target. */
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
            if (c[j] > target) break;                  // sorted: every later candidate is too big as well
            cur.add(c[j]);
            loopRec(c, j, target - c[j], cur, out);    // j, not j + 1: the same value may be reused
            cur.remove(cur.size() - 1);
        }
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
        check(canon(pickNotPick(candidates, target)).equals(exp), "pickNotPick " + label);
        check(canon(optimal(candidates, target)).equals(exp), "optimal " + label);
    }

    /** For inputs with many answers: all approaches agree, every combination sums to target, none repeats, and the count is right. */
    static void verifyLarge(int[] candidates, int target, int expectedCount) {
        String label = Arrays.toString(candidates) + " target=" + target;
        List<String> a = canon(bruteForce(candidates, target));
        List<String> b = canon(pickNotPick(candidates, target));
        List<String> c = canon(optimal(candidates, target));
        check(a.equals(b) && b.equals(c), "approaches disagree on " + label);
        check(a.size() == expectedCount, "expected " + expectedCount + " combinations for " + label + ", got " + a.size());
        check(new HashSet<>(a).size() == a.size(), "duplicate combination for " + label);
        for (List<Integer> combo : optimal(candidates, target)) {
            int sum = 0;
            for (int x : combo) sum += x;
            check(sum == target, "combination " + combo + " does not sum to " + target);
        }
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 6, 7}, 7, "[2, 2, 3]", "[7]");
        verify(new int[]{2, 3, 5}, 8, "[2, 2, 2, 2]", "[2, 3, 3]", "[3, 5]");
        verify(new int[]{2}, 1);                                            // nothing fits
        verify(new int[]{1}, 3, "[1, 1, 1]");                               // one candidate reused three times
        verify(new int[]{7, 3, 2}, 7, "[2, 2, 3]", "[7]");                  // unsorted input gives the same set
        verify(new int[]{2, 4}, 7);                                         // parity makes it impossible
        verify(new int[]{8, 7, 4, 3}, 11, "[3, 4, 4]", "[3, 8]", "[4, 7]");
        verifyLarge(new int[]{2, 3, 5, 7, 11}, 30, 64);                    // too many to list: check count, sums and uniqueness
        System.out.println("OK P864_CombinationSum");
    }
}
