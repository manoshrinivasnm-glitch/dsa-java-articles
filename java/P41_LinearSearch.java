import java.util.*;

/** TUF 41 - Linear Search. Return the index of the first occurrence of target in nums, or -1 if it is absent. */
public class P41_LinearSearch {

    /** Approach 1: scan left to right and stop at the first match. O(n) time, O(1) space. */
    static int iterative(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) return i;
        }
        return -1;
    }

    /** Approach 2: the same scan written recursively, one element per call. O(n) time, O(n) stack space. */
    static int recursive(int[] nums, int target) {
        return recursive(nums, target, 0);
    }

    static int recursive(int[] nums, int target, int i) {
        if (i == nums.length) return -1;             // ran off the end: not found
        if (nums[i] == target) return i;
        return recursive(nums, target, i + 1);
    }

    /** Approach 3: sentinel search; park target in the last slot so the loop needs no bounds check, then restore. O(n) time, O(1) space. */
    static int sentinel(int[] nums, int target) {
        int n = nums.length;
        if (n == 0) return -1;
        int last = nums[n - 1];
        nums[n - 1] = target;                        // the scan is now guaranteed to stop
        int i = 0;
        while (nums[i] != target) i++;
        nums[n - 1] = last;                          // undo the temporary write
        if (i < n - 1 || last == target) return i;   // a real match, either before the sentinel or at it
        return -1;
    }

    /** Variant: every index at which target occurs, in increasing order. O(n) time, O(k) space for k matches. */
    static List<Integer> allOccurrences(int[] nums, int target) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) result.add(i);
        }
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int target, int expected) {
        String s = Arrays.toString(nums) + " target " + target;
        int[] before = nums.clone();
        check(iterative(nums, target) == expected, "iterative " + s);
        check(recursive(nums, target) == expected, "recursive " + s);
        check(sentinel(nums, target) == expected, "sentinel " + s);
        check(Arrays.equals(nums, before), "sentinel must restore the array for " + s);
    }

    public static void main(String[] args) {
        verify(new int[]{6, 7, 8, 4, 1}, 4, 3);
        verify(new int[]{6, 7, 8, 4, 1}, 9, -1);                              // absent
        verify(new int[]{6, 7, 8, 4, 1}, 6, 0);                               // first element
        verify(new int[]{6, 7, 8, 4, 1}, 1, 4);                               // last element, where the sentinel sits
        verify(new int[]{}, 5, -1);                                           // empty array
        verify(new int[]{5}, 5, 0);                                           // single element, present
        verify(new int[]{5}, 3, -1);                                          // single element, absent
        verify(new int[]{2, 3, 2, 3}, 3, 1);                                  // duplicates: first occurrence wins
        verify(new int[]{-4, 0, -4}, -4, 0);                                  // negatives
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE, 1);
        check(allOccurrences(new int[]{2, 3, 2, 3}, 3).equals(List.of(1, 3)), "allOccurrences two matches");
        check(allOccurrences(new int[]{2, 3, 2, 3}, 7).isEmpty(), "allOccurrences absent");
        check(allOccurrences(new int[]{}, 1).isEmpty(), "allOccurrences empty");
        check(allOccurrences(new int[]{1, 1, 1}, 1).equals(List.of(0, 1, 2)), "allOccurrences every index");
        System.out.println("OK P41_LinearSearch");
    }
}
