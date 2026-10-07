import java.util.*;

/** TUF 28 - 4 Sum. Return every distinct quadruplet of values (each sorted) that sums to target. */
public class P28_4Sum {

    /** Approach 1: try every quadruple and de-duplicate with a set. O(n^4) time, O(k) space for k quadruplets. */
    static List<List<Integer>> bruteForce(int[] nums, int target) {
        int n = nums.length;
        Set<List<Integer>> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    for (int l = k + 1; l < n; l++) {
                        if ((long) nums[i] + nums[j] + nums[k] + nums[l] == target) {
                            int[] q = {nums[i], nums[j], nums[k], nums[l]};
                            Arrays.sort(q);
                            set.add(List.of(q[0], q[1], q[2], q[3]));
                        }
                    }
                }
            }
        }
        return sorted(set);
    }

    /** Approach 2: fix i and j, then one pass over k with a hash set of the values strictly between j and k. O(n^3) time, O(n) space. */
    static List<List<Integer>> better(int[] nums, int target) {
        int n = nums.length;
        Set<List<Integer>> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Set<Long> seen = new HashSet<>();            // values nums[j+1 .. k-1]
                for (int k = j + 1; k < n; k++) {
                    long need = (long) target - nums[i] - nums[j] - nums[k];
                    if (seen.contains(need)) {
                        int[] q = {nums[i], nums[j], nums[k], (int) need};
                        Arrays.sort(q);
                        set.add(List.of(q[0], q[1], q[2], q[3]));
                    }
                    seen.add((long) nums[k]);
                }
            }
        }
        return sorted(set);
    }

    /** Approach 3: sort, fix i and j (skipping duplicates), two pointers for the last two. O(n^3) time, O(1) extra space. */
    static List<List<Integer>> optimal(int[] nums, int target) {
        int[] a = nums.clone();
        Arrays.sort(a);
        int n = a.length;
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < n - 3; i++) {
            if (i > 0 && a[i] == a[i - 1]) continue;          // same first value already handled
            for (int j = i + 1; j < n - 2; j++) {
                if (j > i + 1 && a[j] == a[j - 1]) continue;  // same second value already handled for this i
                int lo = j + 1, hi = n - 1;
                while (lo < hi) {
                    long sum = (long) a[i] + a[j] + a[lo] + a[hi];
                    if (sum < target) lo++;
                    else if (sum > target) hi--;
                    else {
                        res.add(List.of(a[i], a[j], a[lo], a[hi]));
                        lo++;
                        hi--;
                        while (lo < hi && a[lo] == a[lo - 1]) lo++;   // skip equal third values
                        while (lo < hi && a[hi] == a[hi + 1]) hi--;   // skip equal fourth values
                    }
                }
            }
        }
        return res;
    }

    /** Puts a collection of quadruplets into lexicographic order so results from different approaches can be compared. */
    static List<List<Integer>> sorted(Collection<List<Integer>> quads) {
        List<List<Integer>> list = new ArrayList<>(quads);
        list.sort(P28_4Sum::compareLists);
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

    static void verify(int[] nums, int target, List<List<Integer>> expected) {
        String in = Arrays.toString(nums) + " target " + target;
        check(bruteForce(nums, target).equals(expected), "bruteForce " + in + " -> " + bruteForce(nums, target));
        check(better(nums, target).equals(expected), "better " + in + " -> " + better(nums, target));
        check(sorted(optimal(nums, target)).equals(expected), "optimal " + in + " -> " + optimal(nums, target));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 0, -1, 0, -2, 2}, 0,
               List.of(List.of(-2, -1, 1, 2), List.of(-2, 0, 0, 2), List.of(-1, 0, 0, 1)));
        verify(new int[]{2, 2, 2, 2, 2}, 8, List.of(List.of(2, 2, 2, 2)));               // all equal
        verify(new int[]{4, 3, 3, 4, 4, 2, 1, 2, 1, 1}, 9,
               List.of(List.of(1, 1, 3, 4), List.of(1, 2, 2, 4), List.of(1, 2, 3, 3)));
        verify(new int[]{1, -2, -5, -4, -3, 3, 3, 5}, -11, List.of(List.of(-5, -4, -3, 1)));
        verify(new int[]{-3, -1, 0, 2, 4, 5}, 2, List.of(List.of(-3, -1, 2, 4)));
        verify(new int[]{}, 0, List.of());                                                // empty input
        verify(new int[]{1, 2, 3}, 6, List.of());                                         // fewer than four elements
        verify(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000, 1_000_000_000}, -294967296,
               List.of());                                                                // naive int sum wraps to exactly this target
        System.out.println("OK P28_4Sum");
    }
}
