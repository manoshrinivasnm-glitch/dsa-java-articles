import java.util.*;

/** TUF 47 - Remove duplicates from sorted array. Keep one copy of each value in the front of nums, in order, and return how many there are. */
public class P47_RemoveDuplicatesFromSortedArray {

    /** Approach 1: put the values in an ordered set, then copy the set back to the front. O(n log n) time, O(n) space. */
    static int bruteForce(int[] nums) {
        Set<Integer> unique = new TreeSet<>();
        for (int x : nums) unique.add(x);
        int i = 0;
        for (int x : unique) nums[i++] = x;             // TreeSet iterates in ascending order
        return unique.size();
    }

    /** Approach 2: two pointers; write marks the last distinct value kept so far. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        if (nums.length == 0) return 0;
        int write = 0;                                  // nums[0..write] are the distinct values seen so far
        for (int read = 1; read < nums.length; read++) {
            if (nums[read] != nums[write]) {            // a new value: it can only differ from the last kept one
                write++;
                nums[write] = nums[read];
            }
        }
        return write + 1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expectedPrefix) {
        String in = input.length <= 12 ? Arrays.toString(input) : input.length + " values";
        int[] a = input.clone();
        int k1 = bruteForce(a);
        check(k1 == expectedPrefix.length && Arrays.equals(Arrays.copyOf(a, k1), expectedPrefix), "bruteForce " + in);
        int[] b = input.clone();
        int k2 = optimal(b);
        check(k2 == expectedPrefix.length && Arrays.equals(Arrays.copyOf(b, k2), expectedPrefix), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 2}, new int[]{1, 2});
        verify(new int[]{0, 0, 1, 1, 1, 2, 2, 3, 3, 4}, new int[]{0, 1, 2, 3, 4});
        verify(new int[]{1, 2, 3, 4}, new int[]{1, 2, 3, 4});                       // already distinct
        verify(new int[]{2, 2, 2, 2}, new int[]{2});                                 // all equal
        verify(new int[]{}, new int[]{});                                            // edge: empty array
        verify(new int[]{7}, new int[]{7});                                          // edge: single element
        verify(new int[]{-3, -3, -1, 0, 0, 5}, new int[]{-3, -1, 0, 5});             // negatives
        verify(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE}, new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE});
        int n = 200_000;
        int[] big = new int[n], bigExpected = new int[n / 4];
        for (int i = 0; i < n; i++) big[i] = i / 4;                                  // every value four times
        for (int i = 0; i < n / 4; i++) bigExpected[i] = i;
        verify(big, bigExpected);
        System.out.println("OK P47_RemoveDuplicatesFromSortedArray");
    }
}
