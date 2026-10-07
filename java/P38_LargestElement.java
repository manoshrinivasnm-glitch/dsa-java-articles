import java.util.*;
import java.util.stream.*;

/** TUF 38 - Largest Element. Return the maximum value of a non-empty int array. */
public class P38_LargestElement {

    /** Approach 1: sort a copy and read the last element. O(n log n) time, O(n) space for the copy. */
    static int bruteForce(int[] nums) {
        if (nums.length == 0) throw new IllegalArgumentException("empty array has no largest element");
        int[] copy = nums.clone();                   // never sort the caller's array just to read one value
        Arrays.sort(copy);
        return copy[copy.length - 1];
    }

    /** Approach 2: one linear pass remembering the best value so far. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        if (nums.length == 0) throw new IllegalArgumentException("empty array has no largest element");
        int max = nums[0];                           // start from a real element, not from 0
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > max) max = nums[i];
        }
        return max;
    }

    /** Approach 3: the same scan written as a stream reduction. O(n) time, O(1) space. */
    static int optimalStream(int[] nums) {
        if (nums.length == 0) throw new IllegalArgumentException("empty array has no largest element");
        return IntStream.of(nums).max().getAsInt();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String s = Arrays.toString(nums);
        int[] before = nums.clone();
        check(bruteForce(nums) == expected, "bruteForce " + s);
        check(Arrays.equals(nums, before), "bruteForce must not reorder the input " + s);
        check(optimal(nums) == expected, "optimal " + s);
        check(optimalStream(nums) == expected, "optimalStream " + s);
    }

    static void verifyRejectsEmpty() {
        int thrown = 0;
        try { bruteForce(new int[]{}); } catch (IllegalArgumentException e) { thrown++; }
        try { optimal(new int[]{}); } catch (IllegalArgumentException e) { thrown++; }
        try { optimalStream(new int[]{}); } catch (IllegalArgumentException e) { thrown++; }
        check(thrown == 3, "every approach must reject an empty array");
    }

    public static void main(String[] args) {
        verify(new int[]{2, 5, 1, 3, 0}, 5);
        verify(new int[]{8, 10, 5, 7, 9}, 10);
        verify(new int[]{7}, 7);                                            // single element
        verify(new int[]{-5, -2, -9, -2}, -2);                              // all negative, maximum appears twice
        verify(new int[]{3, 3, 3}, 3);                                      // all equal
        verify(new int[]{1, 2, 3, 4, 5}, 5);                                // ascending, maximum last
        verify(new int[]{5, 4, 3, 2, 1}, 5);                                // descending, maximum first
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MAX_VALUE);
        verify(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE); // the maximum is the smallest int
        verifyRejectsEmpty();
        System.out.println("OK P38_LargestElement");
    }
}
