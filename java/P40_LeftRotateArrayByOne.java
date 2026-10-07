import java.util.*;

/** TUF 40 - Left Rotate Array by One. Shift every element one position left; the first element wraps to the end. */
public class P40_LeftRotateArrayByOne {

    /** Approach 1: build the rotated array in a fresh array, then copy it back. O(n) time, O(n) extra space. */
    static void bruteForce(int[] nums) {
        int n = nums.length;
        if (n == 0) return;
        int[] rotated = new int[n];
        for (int i = 1; i < n; i++) rotated[i - 1] = nums[i];
        rotated[n - 1] = nums[0];
        for (int i = 0; i < n; i++) nums[i] = rotated[i];
    }

    /** Approach 2: remember the first element, shift the rest left by one, put it at the end. O(n) time, O(1) extra space. */
    static void optimal(int[] nums) {
        int n = nums.length;
        if (n == 0) return;
        int first = nums[0];
        for (int i = 1; i < n; i++) nums[i - 1] = nums[i];   // left to right, so nothing is overwritten before it is read
        nums[n - 1] = first;
    }

    /** Approach 3: the same shift with System.arraycopy, which copies overlapping ranges correctly. O(n) time, O(1) extra space. */
    static void optimalArraycopy(int[] nums) {
        int n = nums.length;
        if (n == 0) return;
        int first = nums[0];
        System.arraycopy(nums, 1, nums, 0, n - 1);
        nums[n - 1] = first;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        String s = Arrays.toString(input);
        int[] a = input.clone();
        bruteForce(a);
        check(Arrays.equals(a, expected), "bruteForce " + s + " -> " + Arrays.toString(a));
        int[] b = input.clone();
        optimal(b);
        check(Arrays.equals(b, expected), "optimal " + s + " -> " + Arrays.toString(b));
        int[] c = input.clone();
        optimalArraycopy(c);
        check(Arrays.equals(c, expected), "optimalArraycopy " + s + " -> " + Arrays.toString(c));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{2, 3, 4, 5, 1});
        verify(new int[]{1, 2}, new int[]{2, 1});                   // two elements simply swap
        verify(new int[]{7}, new int[]{7});                         // single element is unchanged
        verify(new int[]{}, new int[]{});                           // empty array
        verify(new int[]{3, 3, 3}, new int[]{3, 3, 3});             // all equal
        verify(new int[]{-1, 0, 1}, new int[]{0, 1, -1});           // negatives
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE});
        // rotating n times must bring the array back to where it started
        int[] cycle = {4, 8, 15, 16, 23, 42};
        int[] copy = cycle.clone();
        for (int t = 0; t < cycle.length; t++) optimal(copy);
        check(Arrays.equals(copy, cycle), "n rotations by one must be the identity");
        System.out.println("OK P40_LeftRotateArrayByOne");
    }
}
