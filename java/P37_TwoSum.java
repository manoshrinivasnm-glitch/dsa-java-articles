import java.util.*;

/** TUF 37 - Two Sum. Return indices i < j with nums[i] + nums[j] == target, or {-1, -1}. */
public class P37_TwoSum {

    /** Approach 1: try every pair. O(n^2) time, O(1) space. */
    static int[] bruteForce(int[] nums, int target) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] + nums[j] == target) return new int[]{i, j};
            }
        }
        return new int[]{-1, -1};
    }

    /** Approach 2: sort (keeping original indices) and walk two pointers inward. O(n log n) time, O(n) space. */
    static int[] better(int[] nums, int target) {
        int n = nums.length;
        int[][] a = new int[n][2];               // {value, originalIndex}
        for (int i = 0; i < n; i++) a[i] = new int[]{nums[i], i};
        Arrays.sort(a, (x, y) -> Integer.compare(x[0], y[0]));
        int lo = 0, hi = n - 1;
        while (lo < hi) {
            long sum = (long) a[lo][0] + a[hi][0];
            if (sum == target) {
                int i = Math.min(a[lo][1], a[hi][1]), j = Math.max(a[lo][1], a[hi][1]);
                return new int[]{i, j};
            }
            if (sum < target) lo++; else hi--;
        }
        return new int[]{-1, -1};
    }

    /** Approach 3: one pass with a HashMap from value to index. O(n) time, O(n) space. */
    static int[] optimal(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int j = 0; j < nums.length; j++) {
            int need = target - nums[j];
            Integer i = seen.get(need);
            if (i != null) return new int[]{i, j};
            seen.put(nums[j], j);
        }
        return new int[]{-1, -1};
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
        verify(new int[]{2_000_000_000, 2_000_000_000}, -294967296, true); // int overflow in naive sum is a real trap
        verify(new int[]{5}, 5, false);                        // single element
        System.out.println("OK P37_TwoSum");
    }
}
