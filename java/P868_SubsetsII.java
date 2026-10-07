import java.util.*;

/** TUF 868 - Subsets II. All unique subsets of an array that may contain duplicates. */
public class P868_SubsetsII {

    /** Approach 1: generate all 2^n subsets of the sorted array and let a set drop the duplicates. O(2^n * n) time. */
    static List<List<Integer>> bruteForce(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        Set<List<Integer>> seen = new LinkedHashSet<>();
        int n = a.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) subset.add(a[i]);
            }
            seen.add(subset);
        }
        return new ArrayList<>(seen);
    }

    /** Approach 2: sorted loop recursion; every node of the tree is a subset, and equal values are skipped at the same level. */
    static List<List<Integer>> optimal(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        List<List<Integer>> out = new ArrayList<>();
        loopRec(a, 0, new ArrayList<>(), out);
        return out;
    }

    static void loopRec(int[] a, int start, List<Integer> cur, List<List<Integer>> out) {
        out.add(new ArrayList<>(cur));                     // the current prefix of choices is itself a subset
        for (int j = start; j < a.length; j++) {
            if (j > start && a[j] == a[j - 1]) continue;   // this value was already extended at this level
            cur.add(a[j]);
            loopRec(a, j + 1, cur, out);
            cur.remove(cur.size() - 1);
        }
    }

    /** Approach 3: pick / not-pick where "not pick" jumps past every remaining copy of the current value. */
    static List<List<Integer>> pickNotPickSkip(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        List<List<Integer>> out = new ArrayList<>();
        skipRec(a, 0, new ArrayList<>(), out);
        return out;
    }

    static void skipRec(int[] a, int i, List<Integer> cur, List<List<Integer>> out) {
        if (i == a.length) {
            out.add(new ArrayList<>(cur));
            return;
        }
        cur.add(a[i]);                                     // pick a[i]
        skipRec(a, i + 1, cur, out);
        cur.remove(cur.size() - 1);
        int next = i + 1;                                  // not pick: skip every later element equal to a[i]
        while (next < a.length && a[next] == a[i]) next++;
        skipRec(a, next, cur, out);
    }

    /** Approach 4: iterative doubling; when a[i] repeats a[i - 1], only the subsets created in the previous round are extended. */
    static List<List<Integer>> iterative(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        List<List<Integer>> out = new ArrayList<>();
        out.add(new ArrayList<>());
        int prevStart = 0;                                 // index of the first subset added in the previous round
        for (int i = 0; i < a.length; i++) {
            int from = (i > 0 && a[i] == a[i - 1]) ? prevStart : 0;
            int size = out.size();
            for (int j = from; j < size; j++) {
                List<Integer> extended = new ArrayList<>(out.get(j));
                extended.add(a[i]);
                out.add(extended);
            }
            prevStart = size;
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Order-independent fingerprint: each subset sorted and printed, then the list of prints sorted. */
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

    static void verify(int[] nums, String... expected) {
        List<String> exp = new ArrayList<>(Arrays.asList(expected));
        Collections.sort(exp);
        String label = Arrays.toString(nums);
        check(canon(bruteForce(nums)).equals(exp), "bruteForce " + label);
        check(canon(optimal(nums)).equals(exp), "optimal " + label);
        check(canon(pickNotPickSkip(nums)).equals(exp), "pickNotPickSkip " + label);
        check(canon(iterative(nums)).equals(exp), "iterative " + label);
        check(optimal(nums).size() == expected.length, "optimal emitted a duplicate for " + label);
        check(pickNotPickSkip(nums).size() == expected.length, "pickNotPickSkip emitted a duplicate for " + label);
        check(iterative(nums).size() == expected.length, "iterative emitted a duplicate for " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 2}, "[]", "[1]", "[2]", "[1, 2]", "[2, 2]", "[1, 2, 2]");
        verify(new int[]{0}, "[]", "[0]");
        verify(new int[]{}, "[]");                                                      // only the empty subset
        verify(new int[]{2, 1, 2}, "[]", "[1]", "[2]", "[1, 2]", "[2, 2]", "[1, 2, 2]"); // unsorted input, same answer
        verify(new int[]{4, 4, 4, 1, 4}, "[]", "[1]", "[4]", "[1, 4]", "[4, 4]", "[1, 4, 4]", "[4, 4, 4]", "[1, 4, 4, 4]",
                "[4, 4, 4, 4]", "[1, 4, 4, 4, 4]");                                       // (1 + 1) * (4 + 1) = 10 subsets
        verify(new int[]{1, 1, 2, 2}, "[]", "[1]", "[1, 1]", "[2]", "[1, 2]", "[1, 1, 2]", "[2, 2]", "[1, 2, 2]", "[1, 1, 2, 2]");
        verify(new int[]{3, 3, 3}, "[]", "[3]", "[3, 3]", "[3, 3, 3]");                 // all equal: n + 1 subsets, not 2^n
        verify(new int[]{1, 2, 3}, "[]", "[1]", "[2]", "[3]", "[1, 2]", "[1, 3]", "[2, 3]", "[1, 2, 3]"); // no duplicates: full power set
        System.out.println("OK P868_SubsetsII");
    }
}
