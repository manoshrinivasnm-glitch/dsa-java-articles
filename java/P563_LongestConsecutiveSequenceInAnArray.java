import java.util.*;

/** TUF 563 - Longest Consecutive Sequence in an Array. Length of the longest run of values x, x+1, ..., x+L-1 present in the array (any order). */
public class P563_LongestConsecutiveSequenceInAnArray {

    /** Approach 1: from every value, walk upwards using linear searches. O(n^3) worst-case time, O(1) space. */
    static int bruteForce(int[] nums) {
        int best = 0;
        for (int x : nums) {
            int cur = x, len = 1;
            while (cur != Integer.MAX_VALUE && linearSearch(nums, cur + 1)) {
                cur++;
                len++;
            }
            best = Math.max(best, len);
        }
        return best;
    }

    static boolean linearSearch(int[] nums, int target) {
        for (int v : nums) if (v == target) return true;
        return false;
    }

    /** Approach 2: sort a copy, then count runs of neighbours that differ by exactly 1. O(n log n) time, O(n) space. */
    static int better(int[] nums) {
        if (nums.length == 0) return 0;
        int[] a = nums.clone();                          // do not reorder the caller's array
        Arrays.sort(a);
        int best = 1, len = 1;
        for (int i = 1; i < a.length; i++) {
            if (a[i] == a[i - 1]) continue;              // duplicate: neither extends nor breaks the run
            if (a[i] - a[i - 1] == 1) len++;
            else len = 1;
            best = Math.max(best, len);
        }
        return best;
    }

    /** Approach 3: hash set, and only count upwards from values that start a run. O(n) average time, O(n) space. */
    static int optimal(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) set.add(x);
        int best = 0;
        for (int x : set) {                              // iterate the set, so duplicates are visited once
            if (x != Integer.MIN_VALUE && set.contains(x - 1)) continue;   // x - 1 exists: x is not a start
            int cur = x, len = 1;
            while (cur != Integer.MAX_VALUE && set.contains(cur + 1)) {
                cur++;
                len++;
            }
            best = Math.max(best, len);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected, boolean includeBrute) {
        int[] before = nums.clone();
        int[] got = includeBrute
                ? new int[]{bruteForce(nums), better(nums), optimal(nums)}
                : new int[]{better(nums), optimal(nums)};
        for (int g : got) check(g == expected, "expected " + expected + " got " + Arrays.toString(got));
        check(Arrays.equals(before, nums), "input array was modified");
    }

    public static void main(String[] args) {
        verify(new int[]{100, 4, 200, 1, 3, 2}, 4, true);
        verify(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}, 9, true);
        verify(new int[]{1, 2, 0, 1}, 3, true);                       // duplicates do not break or extend a run
        verify(new int[]{}, 0, true);                                 // empty array
        verify(new int[]{5}, 1, true);                                // single element
        verify(new int[]{7, 7, 7}, 1, true);                          // all equal
        verify(new int[]{-3, 10, -1, 1, -2, 0}, 5, true);             // negatives: -3..1
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}, 1, true);   // MAX + 1 wraps to MIN: not consecutive
        verify(new int[]{Integer.MIN_VALUE + 1, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE - 1}, 2, true);
        int[] perm = new int[2003];                                   // a permutation of 0..2002
        for (int i = 0; i < perm.length; i++) perm[i] = (int) ((long) i * 7 % 2003);
        verify(perm, 2003, false);
        Random rnd = new Random(42);                                  // seeded cross-check against brute force
        for (int t = 0; t < 300; t++) {
            int[] a = new int[rnd.nextInt(12)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(15) - 5;
            verify(a, bruteForce(a), true);
        }
        System.out.println("OK P563_LongestConsecutiveSequenceInAnArray");
    }
}
