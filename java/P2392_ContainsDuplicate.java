import java.util.*;

/** TUF 2392 - Contains Duplicate. Return true if any value appears at least twice in nums. */
public class P2392_ContainsDuplicate {

    /** Approach 1: compare every pair. O(n^2) time, O(1) space. */
    static boolean bruteForce(int[] nums) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] == nums[j]) return true;
            }
        }
        return false;
    }

    /** Approach 2: sort a copy, equal values become neighbours. O(n log n) time, O(n) space for the copy. */
    static boolean better(int[] nums) {
        int[] a = nums.clone();                                  // do not reorder the caller's array
        Arrays.sort(a);
        for (int i = 1; i < a.length; i++) {
            if (a[i] == a[i - 1]) return true;
        }
        return false;
    }

    /** Approach 3: one pass with a HashSet. O(n) average time, O(n) space. */
    static boolean optimal(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int x : nums) {
            if (!seen.add(x)) return true;                       // add() returns false when x is already present
        }
        return false;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, boolean expected) {
        int[] before = nums.clone();
        check(bruteForce(nums) == expected, "bruteForce " + Arrays.toString(nums));
        check(better(nums) == expected, "better " + Arrays.toString(nums));
        check(optimal(nums) == expected, "optimal " + Arrays.toString(nums));
        check(Arrays.equals(before, nums), "input was modified");
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 1}, true);
        verify(new int[]{1, 2, 3, 4}, false);
        verify(new int[]{1, 1, 1, 3, 3, 4, 3, 2, 4, 2}, true);
        verify(new int[]{}, false);                              // empty array
        verify(new int[]{7}, false);                             // single element
        verify(new int[]{-1, Integer.MIN_VALUE, Integer.MAX_VALUE, -1}, true);   // negatives and extremes
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, false);
        int[] distinct = new int[3000];
        for (int i = 0; i < distinct.length; i++) distinct[i] = (i * 7919) % 3001;   // a permutation of 0..3000 minus one value
        verify(distinct, false);
        int[] lateDup = distinct.clone();
        lateDup[lateDup.length - 1] = lateDup[0];
        verify(lateDup, true);                                   // the only duplicate pair is first and last
        System.out.println("OK P2392_ContainsDuplicate");
    }
}
