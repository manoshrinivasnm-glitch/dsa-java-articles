import java.util.*;

/** TUF 43 - Second Largest Element. Return the second largest distinct value, or -1 if it does not exist. */
public class P43_SecondLargestElement {

    /** Approach 1: sort a copy, then walk down from the end to the first value below the maximum. O(n log n) time, O(n) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length;
        if (n < 2) return -1;
        int[] a = nums.clone();
        Arrays.sort(a);
        int largest = a[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            if (a[i] != largest) return a[i];        // first value strictly below the maximum
        }
        return -1;                                   // every element equals the maximum
    }

    /** Approach 2: two passes, first the maximum, then the largest value strictly below it. O(n) time, O(1) space. */
    static int better(int[] nums) {
        if (nums.length < 2) return -1;
        int largest = nums[0];
        for (int x : nums) if (x > largest) largest = x;
        boolean found = false;
        int second = Integer.MIN_VALUE;
        for (int x : nums) {
            if (x != largest && (!found || x > second)) {
                second = x;
                found = true;
            }
        }
        return found ? second : -1;
    }

    /** Approach 3: one pass carrying the largest and second largest seen so far. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        if (nums.length < 2) return -1;
        long largest = Long.MIN_VALUE, second = Long.MIN_VALUE;   // long: no int value can collide with the sentinel
        for (int x : nums) {
            if (x > largest) {
                second = largest;                    // the old maximum is now the runner-up
                largest = x;
            } else if (x < largest && x > second) {
                second = x;                          // between the two: new runner-up
            }
        }
        return second == Long.MIN_VALUE ? -1 : (int) second;
    }

    /** Mirror image: the second smallest distinct value, or -1 if it does not exist. O(n) time, O(1) space. */
    static int secondSmallest(int[] nums) {
        if (nums.length < 2) return -1;
        long smallest = Long.MAX_VALUE, second = Long.MAX_VALUE;
        for (int x : nums) {
            if (x < smallest) {
                second = smallest;
                smallest = x;
            } else if (x > smallest && x < second) {
                second = x;
            }
        }
        return second == Long.MAX_VALUE ? -1 : (int) second;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expectedSecondLargest, int expectedSecondSmallest) {
        String s = Arrays.toString(nums);
        int[] before = nums.clone();
        check(bruteForce(nums) == expectedSecondLargest, "bruteForce " + s);
        check(Arrays.equals(nums, before), "bruteForce must not reorder the input " + s);
        check(better(nums) == expectedSecondLargest, "better " + s);
        check(optimal(nums) == expectedSecondLargest, "optimal " + s);
        check(secondSmallest(nums) == expectedSecondSmallest, "secondSmallest " + s);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 4, 7, 7, 5}, 5, 2);
        verify(new int[]{1}, -1, -1);                                 // single element
        verify(new int[]{}, -1, -1);                                  // empty
        verify(new int[]{7, 7, 7}, -1, -1);                           // all equal: no second distinct value
        verify(new int[]{5, 5, 4}, 4, 5);                             // duplicate maximum, runner-up is strictly smaller
        verify(new int[]{-3, -1, -2}, -2, -2);                        // negatives
        verify(new int[]{2, 1}, 1, 2);                                // two elements, descending
        verify(new int[]{1, 2}, 1, 2);                                // two elements, ascending
        verify(new int[]{10, 9, 8, 7}, 9, 8);                         // strictly descending
        verify(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MIN_VALUE, Integer.MAX_VALUE); // sentinel collision
        System.out.println("OK P43_SecondLargestElement");
    }
}
