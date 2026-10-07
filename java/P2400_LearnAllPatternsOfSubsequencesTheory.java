import java.util.*;

/** TUF 2400 - Learn All Patterns of Subsequences (Theory). The take / not-take recursion and the ways it is reshaped: print all, print those with a sum, print one, count. */
public class P2400_LearnAllPatternsOfSubsequencesTheory {

    /** Pattern 1: print all subsequences. One decision per index: take it or leave it. O(2^n * n) time, O(n) stack. */
    static List<List<Integer>> allSubsequences(int[] a) {
        List<List<Integer>> result = new ArrayList<>();
        collectAll(a, 0, new ArrayList<>(), result);
        return result;
    }

    static void collectAll(int[] a, int index, List<Integer> current, List<List<Integer>> result) {
        if (index == a.length) {                                   // base case: every index has been decided
            result.add(new ArrayList<>(current));                  // copy: current keeps changing after we return
            return;
        }
        current.add(a[index]);                                     // take a[index]
        collectAll(a, index + 1, current, result);
        current.remove(current.size() - 1);                        // undo, so the other branch sees the same prefix
        collectAll(a, index + 1, current, result);                 // not take
    }

    /** Pattern 2: print all subsequences whose sum is k. Carry the running sum down and test it at the leaf. */
    static List<List<Integer>> subsequencesWithSum(int[] a, int k) {
        List<List<Integer>> result = new ArrayList<>();
        collectWithSum(a, 0, 0, k, new ArrayList<>(), result);
        return result;
    }

    static void collectWithSum(int[] a, int index, long sum, int k, List<Integer> current, List<List<Integer>> result) {
        if (index == a.length) {
            if (sum == k) result.add(new ArrayList<>(current));   // only leaves with the right sum are answers
            return;
        }
        current.add(a[index]);
        collectWithSum(a, index + 1, sum + a[index], k, current, result);
        current.remove(current.size() - 1);
        collectWithSum(a, index + 1, sum, k, current, result);
    }

    /** Pattern 3: print any one subsequence with sum k. Return a boolean so the search stops at the first hit. */
    static Optional<List<Integer>> anyOneWithSum(int[] a, int k) {
        List<Integer> current = new ArrayList<>();
        if (findOne(a, 0, 0, k, current)) return Optional.of(current);
        return Optional.empty();
    }

    static boolean findOne(int[] a, int index, long sum, int k, List<Integer> current) {
        if (index == a.length) return sum == k;                   // base case: report whether this leaf is an answer
        current.add(a[index]);
        if (findOne(a, index + 1, sum + a[index], k, current)) return true;   // found on the take branch: keep current as it is
        current.remove(current.size() - 1);
        return findOne(a, index + 1, sum, k, current);            // otherwise the answer, if any, is on the not-take branch
    }

    /** Pattern 4: count the subsequences with sum k. Leaves return 1 or 0 and every frame adds its two branches. */
    static int countWithSum(int[] a, int k) {
        return count(a, 0, 0, k);
    }

    static int count(int[] a, int index, long sum, int k) {
        if (index == a.length) return sum == k ? 1 : 0;
        int take = count(a, index + 1, sum + a[index], k);
        int notTake = count(a, index + 1, sum, k);
        return take + notTake;
    }

    /** Pattern 5: the pick-next-index loop. Each frame chooses which element comes next; every node of the tree is an answer. Same 2^n subsequences, different order. */
    static List<List<Integer>> allSubsequencesPickLoop(int[] a) {
        List<List<Integer>> result = new ArrayList<>();
        pickLoop(a, 0, new ArrayList<>(), result);
        return result;
    }

    static void pickLoop(int[] a, int start, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));                      // record on entry, not only at a leaf
        for (int i = start; i < a.length; i++) {
            current.add(a[i]);                                     // a[i] is the next element of the subsequence
            pickLoop(a, i + 1, current, result);                   // later elements must come from after i
            current.remove(current.size() - 1);
        }
    }

    /** Baseline without recursion: bit i of a counter says whether a[i] is taken. Used to cross-check the recursive patterns. */
    static List<List<Integer>> allSubsequencesBitmask(int[] a) {
        List<List<Integer>> result = new ArrayList<>();
        for (int mask = 0; mask < (1 << a.length); mask++) {
            List<Integer> subsequence = new ArrayList<>();
            for (int i = 0; i < a.length; i++) {
                if ((mask & (1 << i)) != 0) subsequence.add(a[i]);
            }
            result.add(subsequence);
        }
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Order-independent view of a list of subsequences. */
    static List<String> canonical(List<List<Integer>> lists) {
        List<String> out = new ArrayList<>();
        for (List<Integer> l : lists) out.add(l.toString());
        Collections.sort(out);
        return out;
    }

    static void verifyAll(int[] a) {
        List<String> expected = canonical(allSubsequencesBitmask(a));
        check(expected.size() == (1 << a.length), "2^n subsequences for " + Arrays.toString(a));
        check(canonical(allSubsequences(a)).equals(expected), "allSubsequences " + Arrays.toString(a));
        check(canonical(allSubsequencesPickLoop(a)).equals(expected), "pickLoop " + Arrays.toString(a));
    }

    static void verifySum(int[] a, int k, List<List<Integer>> expected) {
        List<List<Integer>> found = subsequencesWithSum(a, k);
        check(found.equals(expected), "subsequencesWithSum " + Arrays.toString(a) + " k=" + k + " -> " + found);
        check(countWithSum(a, k) == expected.size(), "countWithSum " + Arrays.toString(a) + " k=" + k);
        Optional<List<Integer>> one = anyOneWithSum(a, k);
        if (expected.isEmpty()) check(one.isEmpty(), "anyOne should be empty for " + Arrays.toString(a) + " k=" + k);
        else check(one.isPresent() && one.get().equals(expected.get(0)), "anyOne is the first answer in take-first order for " + Arrays.toString(a) + " k=" + k);
    }

    public static void main(String[] args) {
        int[] a = {3, 1, 2};
        check(allSubsequences(a).equals(List.of(List.of(3, 1, 2), List.of(3, 1), List.of(3, 2), List.of(3), List.of(1, 2), List.of(1), List.of(2), List.of())), "take-first order");
        check(allSubsequencesPickLoop(a).equals(List.of(List.of(), List.of(3), List.of(3, 1), List.of(3, 1, 2), List.of(3, 2), List.of(1), List.of(1, 2), List.of(2))), "pick-loop order");
        check(allSubsequencesBitmask(a).equals(List.of(List.of(), List.of(3), List.of(1), List.of(3, 1), List.of(2), List.of(3, 2), List.of(1, 2), List.of(3, 1, 2))), "bitmask order");
        verifyAll(a);
        verifyAll(new int[]{});                                   // only the empty subsequence
        verifyAll(new int[]{7});
        verifyAll(new int[]{1, 1});                               // equal values are still different indices
        verifyAll(new int[]{5, -2, 9, 0, 4, 4, -7, 3, 8, 1});     // 1024 subsequences

        verifySum(a, 3, List.of(List.of(3), List.of(1, 2)));
        verifySum(new int[]{1, 2, 3, 4, 5}, 5, List.of(List.of(1, 4), List.of(2, 3), List.of(5)));
        verifySum(new int[]{-1, 1, 0}, 0, List.of(List.of(-1, 1, 0), List.of(-1, 1), List.of(0), List.of()));   // negatives and zero
        verifySum(new int[]{2, 2, 3}, 4, List.of(List.of(2, 2)));
        verifySum(new int[]{1, 2, 1}, 3, List.of(List.of(1, 2), List.of(2, 1)));
        verifySum(new int[]{1, 2, 3}, 10, List.of());               // impossible target
        verifySum(new int[]{}, 0, List.of(List.of()));              // the empty subsequence has sum 0
        verifySum(new int[]{}, 5, List.of());
        int[] oneToTwelve = new int[12];
        for (int i = 0; i < 12; i++) oneToTwelve[i] = i + 1;
        check(countWithSum(oneToTwelve, 20) == 45, "45 subsequences of 1..12 sum to 20");
        check(countWithSum(oneToTwelve, 20) == subsequencesWithSum(oneToTwelve, 20).size(), "count matches the list");
        System.out.println("OK P2400_LearnAllPatternsOfSubsequencesTheory");
    }
}
