import java.util.*;

/** TUF 39 - Left Rotate Array by K Places. Rotate the array left by k positions in place; k may be 0 or larger than n. */
public class P39_LeftRotateArrayByKPlaces {

    /** Approach 1: rotate left by one, k times. O(n * k) time, O(1) extra space. */
    static void bruteForce(int[] nums, int k) {
        int n = nums.length;
        if (n == 0) return;
        k %= n;                                      // rotating by n is the identity
        for (int t = 0; t < k; t++) {
            int first = nums[0];
            for (int i = 1; i < n; i++) nums[i - 1] = nums[i];
            nums[n - 1] = first;
        }
    }

    /** Approach 2: stash the first k elements, shift the rest left by k, append the stash. O(n) time, O(k) extra space. */
    static void better(int[] nums, int k) {
        int n = nums.length;
        if (n == 0) return;
        k %= n;
        int[] temp = new int[k];
        for (int i = 0; i < k; i++) temp[i] = nums[i];
        for (int i = k; i < n; i++) nums[i - k] = nums[i];
        for (int i = 0; i < k; i++) nums[n - k + i] = temp[i];
    }

    /** Approach 3: reverse the first k, reverse the remaining n - k, then reverse the whole array. O(n) time, O(1) extra space. */
    static void optimal(int[] nums, int k) {
        int n = nums.length;
        if (n == 0) return;
        k %= n;
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
        reverse(nums, 0, n - 1);
    }

    static void reverse(int[] a, int lo, int hi) {
        while (lo < hi) {
            int t = a[lo];
            a[lo] = a[hi];
            a[hi] = t;
            lo++;
            hi--;
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int k, int[] expected) {
        String s = Arrays.toString(input) + " k=" + k;
        int[] a = input.clone();
        bruteForce(a, k);
        check(Arrays.equals(a, expected), "bruteForce " + s + " -> " + Arrays.toString(a));
        int[] b = input.clone();
        better(b, k);
        check(Arrays.equals(b, expected), "better " + s + " -> " + Arrays.toString(b));
        int[] c = input.clone();
        optimal(c, k);
        check(Arrays.equals(c, expected), "optimal " + s + " -> " + Arrays.toString(c));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5, 6, 7}, 3, new int[]{4, 5, 6, 7, 1, 2, 3});
        verify(new int[]{1, 2, 3, 4, 5, 6, 7}, 0, new int[]{1, 2, 3, 4, 5, 6, 7});   // k = 0
        verify(new int[]{1, 2, 3, 4, 5, 6, 7}, 7, new int[]{1, 2, 3, 4, 5, 6, 7});   // k = n is the identity
        verify(new int[]{1, 2, 3, 4, 5, 6, 7}, 10, new int[]{4, 5, 6, 7, 1, 2, 3});  // k > n reduces to 3
        verify(new int[]{1, 2, 3, 4, 5}, 4, new int[]{5, 1, 2, 3, 4});               // k = n - 1 equals a right rotation by one
        verify(new int[]{9}, 5, new int[]{9});                                      // single element
        verify(new int[]{}, 3, new int[]{});                                        // empty: k % n must not be evaluated
        verify(new int[]{-1, -2, -3, -4}, 2, new int[]{-3, -4, -1, -2});            // negatives
        verify(new int[]{1, 2, 3}, 1_000_000_001, new int[]{3, 1, 2});              // huge k: 1_000_000_001 % 3 == 2
        System.out.println("OK P39_LeftRotateArrayByKPlaces");
    }
}
