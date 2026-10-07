import java.util.*;

/** TUF 27 - 3 Sum. Return every distinct triplet of values (each triplet sorted) that sums to zero. */
public class P27_3Sum {

    /** Approach 1: try every triple and de-duplicate with a set of sorted triplets. O(n^3) time, O(k) space for k triplets. */
    static List<List<Integer>> bruteForce(int[] nums) {
        int n = nums.length;
        Set<List<Integer>> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    if ((long) nums[i] + nums[j] + nums[k] == 0) {
                        int[] t = {nums[i], nums[j], nums[k]};
                        Arrays.sort(t);
                        set.add(List.of(t[0], t[1], t[2]));
                    }
                }
            }
        }
        return sorted(set);
    }

    /** Approach 2: fix i, then one pass over j with a hash set of the values strictly between i and j. O(n^2) time, O(n) space. */
    static List<List<Integer>> better(int[] nums) {
        int n = nums.length;
        Set<List<Integer>> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            Set<Integer> seen = new HashSet<>();           // values nums[i+1 .. j-1]
            for (int j = i + 1; j < n; j++) {
                long need = -(long) nums[i] - nums[j];     // third value that would complete the triplet
                if (need >= Integer.MIN_VALUE && need <= Integer.MAX_VALUE && seen.contains((int) need)) {
                    int[] t = {nums[i], nums[j], (int) need};
                    Arrays.sort(t);
                    set.add(List.of(t[0], t[1], t[2]));
                }
                seen.add(nums[j]);
            }
        }
        return sorted(set);
    }

    /** Approach 3: sort, fix i, two pointers on the rest, skip duplicates as we go. O(n^2) time, O(1) extra space. */
    static List<List<Integer>> optimal(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        int n = a.length;
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < n - 2; i++) {
            if (i > 0 && a[i] == a[i - 1]) continue;      // same first value was already handled
            if (a[i] > 0) break;                           // three positives can never sum to zero
            int lo = i + 1, hi = n - 1;
            while (lo < hi) {
                long sum = (long) a[i] + a[lo] + a[hi];
                if (sum < 0) lo++;
                else if (sum > 0) hi--;
                else {
                    res.add(List.of(a[i], a[lo], a[hi]));
                    lo++;
                    hi--;
                    while (lo < hi && a[lo] == a[lo - 1]) lo++;   // skip equal second values
                    while (lo < hi && a[hi] == a[hi + 1]) hi--;   // skip equal third values
                }
            }
        }
        return res;
    }

    /** Puts a collection of triplets into lexicographic order so results from different approaches can be compared. */
    static List<List<Integer>> sorted(Collection<List<Integer>> triplets) {
        List<List<Integer>> list = new ArrayList<>(triplets);
        list.sort(P27_3Sum::compareLists);
        return list;
    }

    static int compareLists(List<Integer> x, List<Integer> y) {
        for (int i = 0; i < Math.min(x.size(), y.size()); i++) {
            int c = Integer.compare(x.get(i), y.get(i));
            if (c != 0) return c;
        }
        return Integer.compare(x.size(), y.size());
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, List<List<Integer>> expected) {
        check(bruteForce(nums).equals(expected), "bruteForce " + Arrays.toString(nums) + " -> " + bruteForce(nums));
        check(better(nums).equals(expected), "better " + Arrays.toString(nums) + " -> " + better(nums));
        check(sorted(optimal(nums)).equals(expected), "optimal " + Arrays.toString(nums) + " -> " + optimal(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{-1, 0, 1, 2, -1, -4}, List.of(List.of(-1, -1, 2), List.of(-1, 0, 1)));
        verify(new int[]{0, 1, 1}, List.of());
        verify(new int[]{0, 0, 0}, List.of(List.of(0, 0, 0)));
        verify(new int[]{0, 0, 0, 0}, List.of(List.of(0, 0, 0)));                 // many duplicates, one triplet
        verify(new int[]{}, List.of());                                             // empty input
        verify(new int[]{1, 2}, List.of());                                         // fewer than three elements
        verify(new int[]{-2, 0, 1, 1, 2}, List.of(List.of(-2, 0, 2), List.of(-2, 1, 1)));
        verify(new int[]{3, -2, 1, 0}, List.of());                                  // no triplet at all
        verify(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000, -2_000_000_000},
               List.of(List.of(-2_000_000_000, 1_000_000_000, 1_000_000_000)));   // sums leave the int range
        System.out.println("OK P27_3Sum");
    }
}
