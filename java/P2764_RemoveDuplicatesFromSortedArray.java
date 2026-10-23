import java.util.*;

/** TUF 2764 - Remove duplicates from Sorted array. Keep each value once at the front, in place, and return the new length k. */
public class P2764_RemoveDuplicatesFromSortedArray {

    /** Approach 1: collect the distinct values in a sorted set, then copy them back to the front. O(n log n) time, O(n) space. */
    static int bruteForce(int[] nums) {
        TreeSet<Integer> set = new TreeSet<>();      // holds each value once, in sorted order
        for (int x : nums) set.add(x);               // duplicates are dropped automatically
        int k = 0;                                   // next free slot at the front
        for (int x : set) nums[k++] = x;             // TreeSet iterates in ascending order
        return k;                                    // k = number of distinct values
    }

    /** Approach 2: two pointers; i is the end of the unique prefix, j scans for the next new value. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        int n = nums.length;
        if (n == 0) return 0;                        // empty array: nothing to keep
        int i = 0;                                   // nums[0..i] holds the distinct values found so far
        for (int j = 1; j < n; j++) {                // j scans every later element
            if (nums[j] != nums[i]) {                // a new value (sorted, so duplicates are adjacent)
                i++;
                nums[i] = nums[j];                   // append it to the distinct prefix
            }
        }
        return i + 1;                                // length of the distinct prefix
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expectedPrefix) {
        String s = Arrays.toString(input);
        int[] a = input.clone();
        int k1 = bruteForce(a);
        check(k1 == expectedPrefix.length, "bruteForce length for " + s + " got " + k1);
        check(Arrays.equals(Arrays.copyOf(a, k1), expectedPrefix), "bruteForce prefix for " + s + " got " + Arrays.toString(Arrays.copyOf(a, k1)));
        int[] b = input.clone();
        int k2 = optimal(b);
        check(k2 == expectedPrefix.length, "optimal length for " + s + " got " + k2);
        check(Arrays.equals(Arrays.copyOf(b, k2), expectedPrefix), "optimal prefix for " + s + " got " + Arrays.toString(Arrays.copyOf(b, k2)));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 2}, new int[]{1, 2});
        verify(new int[]{0, 0, 1, 1, 1, 2, 2, 3, 3, 4}, new int[]{0, 1, 2, 3, 4});
        verify(new int[]{}, new int[]{});                                        // empty
        verify(new int[]{7}, new int[]{7});                                      // single element
        verify(new int[]{5, 5, 5, 5}, new int[]{5});                             // everything is a duplicate
        verify(new int[]{1, 2, 3, 4}, new int[]{1, 2, 3, 4});                    // already distinct
        verify(new int[]{-3, -3, -1, 0, 0, 0, 2}, new int[]{-3, -1, 0, 2});      // negatives and zero
        verify(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE}, new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE});
        // every value 0..99 repeated three times
        int[] big = new int[300];
        int[] expectBig = new int[100];
        for (int i = 0; i < 300; i++) big[i] = i / 3;
        for (int i = 0; i < 100; i++) expectBig[i] = i;
        verify(big, expectBig);
        System.out.println("OK P2764_RemoveDuplicatesFromSortedArray");
    }
}
