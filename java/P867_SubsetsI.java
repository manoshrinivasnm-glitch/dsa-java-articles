import java.util.*;

/** TUF 867 - Subsets I (Subset Sums). Return the sums of all 2^n subsets of nums, in non-decreasing order. */
public class P867_SubsetsI {

    /** Approach 1: build every subset explicitly from a bitmask, then add it up. O(2^n * n) time, O(2^n) for the output. */
    static List<Integer> bruteForce(int[] nums) {
        int n = nums.length;
        List<Integer> sums = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) sum += nums[i];
            }
            sums.add(sum);
        }
        Collections.sort(sums);
        return sums;
    }

    /** Approach 2: pick / not-pick recursion that carries the running sum, so each leaf costs O(1). O(2^n) time, O(n) depth. */
    static List<Integer> optimal(int[] nums) {
        List<Integer> sums = new ArrayList<>();
        sumRec(nums, 0, 0, sums);
        Collections.sort(sums);
        return sums;
    }

    static void sumRec(int[] nums, int i, int sum, List<Integer> sums) {
        if (i == nums.length) {
            sums.add(sum);
            return;
        }
        sumRec(nums, i + 1, sum + nums[i], sums);   // pick nums[i]
        sumRec(nums, i + 1, sum, sums);             // leave nums[i] out
    }

    /** Approach 3: iterative doubling; the sums for the first i + 1 elements are the previous sums with and without nums[i]. O(2^n) time. */
    static List<Integer> iterative(int[] nums) {
        List<Integer> sums = new ArrayList<>();
        sums.add(0);                                 // the empty subset
        for (int x : nums) {
            int size = sums.size();
            for (int j = 0; j < size; j++) sums.add(sums.get(j) + x);
        }
        Collections.sort(sums);
        return sums;
    }

    /** Bonus: the same pick / not-pick recursion collecting the subsets themselves instead of their sums. O(2^n * n). */
    static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> out = new ArrayList<>();
        subsetRec(nums, 0, new ArrayList<>(), out);
        return out;
    }

    static void subsetRec(int[] nums, int i, List<Integer> cur, List<List<Integer>> out) {
        if (i == nums.length) {
            out.add(new ArrayList<>(cur));
            return;
        }
        cur.add(nums[i]);                             // pick nums[i]
        subsetRec(nums, i + 1, cur, out);
        cur.remove(cur.size() - 1);                   // undo, then leave nums[i] out
        subsetRec(nums, i + 1, cur, out);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, Integer... expected) {
        List<Integer> exp = Arrays.asList(expected);
        String label = Arrays.toString(nums);
        check(bruteForce(nums).equals(exp), "bruteForce " + label);
        check(optimal(nums).equals(exp), "optimal " + label);
        check(iterative(nums).equals(exp), "iterative " + label);
        List<List<Integer>> subs = subsets(nums);
        check(subs.size() == exp.size(), "subsets count " + label);
        check(new HashSet<>(subs).size() == subs.size() || hasDuplicateValues(nums), "subsets repeated for " + label);
        List<Integer> fromSubsets = new ArrayList<>();
        for (List<Integer> sub : subs) {
            int sum = 0;
            for (int x : sub) sum += x;
            fromSubsets.add(sum);
        }
        Collections.sort(fromSubsets);
        check(fromSubsets.equals(exp), "subsets sums " + label);
    }

    static boolean hasDuplicateValues(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int x : nums) if (!seen.add(x)) return true;
        return false;
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3}, 0, 2, 3, 5);
        verify(new int[]{5, 2, 1}, 0, 1, 2, 3, 5, 6, 7, 8);
        verify(new int[]{3, 1, 2}, 0, 1, 2, 3, 3, 4, 5, 6);      // 3 appears twice: {3} and {1, 2}
        verify(new int[]{}, 0);                                    // only the empty subset
        verify(new int[]{7}, 0, 7);                                // single element
        verify(new int[]{1, 1}, 0, 1, 1, 2);                       // equal elements give equal sums, all are kept
        verify(new int[]{-1, 2}, -1, 0, 1, 2);                     // negatives
        int[] powers = new int[15];
        for (int i = 0; i < 15; i++) powers[i] = 1 << i;
        Integer[] all = new Integer[1 << 15];
        for (int v = 0; v < (1 << 15); v++) all[v] = v;            // distinct powers of two: every sum 0..2^15-1 exactly once
        verify(powers, all);
        System.out.println("OK P867_SubsetsI");
    }
}
