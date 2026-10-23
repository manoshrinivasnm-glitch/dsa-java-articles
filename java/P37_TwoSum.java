import java.util.*;

/** TUF 37 - Two Sum. Return indices i < j with nums[i] + nums[j] == target, or {-1, -1}. */
public class P37_TwoSum {

    /** Approach 1: try every pair. O(n^2) time, O(1) space. */
    static int[] bruteForce(int[] nums, int target) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {            // first index of the pair
            for (int j = i + 1; j < n; j++) {    // second index, always after i
                if ((long) nums[i] + nums[j] == target) return new int[]{i, j};   // long: no int overflow
            }
        }
        return new int[]{-1, -1};                // no pair adds up to target
    }

    /** Approach 2: sort (keeping original indices) and walk two pointers inward. O(n log n) time, O(n) space. */
    static int[] better(int[] nums, int target) {
        int n = nums.length;
        int[][] a = new int[n][2];               // {value, originalIndex}
        for (int i = 0; i < n; i++) a[i] = new int[]{nums[i], i};   // remember where each value came from
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));   // sort by value only
        int lo = 0, hi = n - 1;                  // smallest and largest remaining values
        while (lo < hi) {
            long sum = (long) a[lo][0] + a[hi][0];   // long: no int overflow
            if (sum == target) {                 // found: report the original indices
                // smaller original index first
                int i = Math.min(a[lo][1], a[hi][1]), j = Math.max(a[lo][1], a[hi][1]);
                return new int[]{i, j};
            }
            if (sum < target) lo++; else hi--;   // too small -> raise lo, too big -> lower hi
        }
        return new int[]{-1, -1};                // pointers met: no pair
    }

    /** Approach 3: one pass with a HashMap from value to index. O(n) time, O(n) space. */
    static int[] optimal(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();      // value -> its index, for elements before j
        for (int j = 0; j < nums.length; j++) {
            long need = (long) target - nums[j];           // long: target - nums[j] can overflow int
            if (need >= Integer.MIN_VALUE && need <= Integer.MAX_VALUE) {   // an int partner can exist
                Integer i = seen.get((int) need);          // has the partner appeared earlier?
                if (i != null) return new int[]{i, j};     // yes: i < j by construction
            }
            seen.put(nums[j], j);                          // after the lookup: j never pairs with itself
        }
        return new int[]{-1, -1};                          // no pair adds up to target
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int target, boolean expectFound) {
        for (int[] r : new int[][]{bruteForce(nums, target), better(nums, target), optimal(nums, target)}) {
            if (!expectFound) {
                check(r[0] == -1 && r[1] == -1, "expected no pair for " + Arrays.toString(nums) + " target " + target);
            } else {
                check(r[0] >= 0 && r[1] > r[0] && r[1] < nums.length, "bad indices " + Arrays.toString(r));
                check(nums[r[0]] + nums[r[1]] == target, "pair does not sum to target: " + Arrays.toString(r));
            }
        }
    }

    public static void main(String[] args) {
        verify(new int[]{2, 7, 11, 15}, 9, true);
        verify(new int[]{3, 2, 4}, 6, true);
        verify(new int[]{3, 3}, 6, true);                     // duplicates must use two different indices
        verify(new int[]{1, 2, 3}, 7, false);                 // no answer
        verify(new int[]{-3, 4, 3, 90}, 0, true);             // negatives
        verify(new int[]{2_000_000_000, 2_000_000_000}, -294967296, false); // naive int addition wraps to exactly this target
        verify(new int[]{5}, 5, false);                        // single element
        System.out.println("OK P37_TwoSum");
    }
}
