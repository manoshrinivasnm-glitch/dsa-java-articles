import java.util.*;

/** TUF 36 - Sort an array of 0's 1's and 2's, in place, without calling a library sort in the optimal version. */
public class P36_SortAnArrayOf0s1sAnd2s {

    /** Approach 1: any comparison sort. O(n log n) time. */
    static void bruteForce(int[] nums) {
        Arrays.sort(nums);
    }

    /** Approach 2: count each value, then overwrite the array. Two passes, O(n) time, O(1) space. */
    static void better(int[] nums) {
        int[] cnt = new int[3];
        for (int v : nums) cnt[v]++;
        int k = 0;
        for (int v = 0; v < 3; v++) {
            for (int c = 0; c < cnt[v]; c++) nums[k++] = v;
        }
    }

    /** Approach 3: Dutch National Flag, one pass with three pointers. O(n) time, O(1) space. */
    static void optimal(int[] nums) {
        int low = 0, mid = 0, high = nums.length - 1;
        // [0, low) are 0s, [low, mid) are 1s, [mid, high] unknown, (high, n) are 2s
        while (mid <= high) {
            if (nums[mid] == 0) {
                swap(nums, low++, mid++);
            } else if (nums[mid] == 1) {
                mid++;
            } else {
                swap(nums, mid, high--);       // do not advance mid: the swapped-in value is unexamined
            }
        }
    }

    static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone(), b = input.clone(), c = input.clone();
        bruteForce(a);
        better(b);
        optimal(c);
        check(Arrays.equals(a, expected), "bruteForce " + Arrays.toString(input));
        check(Arrays.equals(b, expected), "better " + Arrays.toString(input));
        check(Arrays.equals(c, expected), "optimal " + Arrays.toString(input));
    }

    public static void main(String[] args) {
        verify(new int[]{2, 0, 2, 1, 1, 0}, new int[]{0, 0, 1, 1, 2, 2});
        verify(new int[]{2, 0, 1}, new int[]{0, 1, 2});
        verify(new int[]{0}, new int[]{0});                                 // single element
        verify(new int[]{}, new int[]{});                                   // edge: empty array
        verify(new int[]{2, 2, 2}, new int[]{2, 2, 2});                     // one value only
        verify(new int[]{1, 0}, new int[]{0, 1});
        verify(new int[]{2, 1, 0, 2, 1, 0, 0}, new int[]{0, 0, 0, 1, 1, 2, 2});

        Random rnd = new Random(36);
        for (int trial = 0; trial < 300; trial++) {
            int[] a = new int[rnd.nextInt(20)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(3);
            int[] expected = a.clone();
            Arrays.sort(expected);
            verify(a, expected);
        }
        System.out.println("OK P36_SortAnArrayOf0s1sAnd2s");
    }
}
