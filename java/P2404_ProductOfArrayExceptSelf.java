import java.util.*;

/** TUF 2404 - Product of Array Except Self. answer[i] = product of all nums[j] with j != i, without division. */
public class P2404_ProductOfArrayExceptSelf {

    /** Approach 1: for each i multiply everything else. O(n^2) time, O(1) extra space. */
    static int[] bruteForce(int[] nums) {
        int n = nums.length;
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            int p = 1;
            for (int j = 0; j < n; j++) {
                if (j != i) p *= nums[j];
            }
            ans[i] = p;
        }
        return ans;
    }

    /** Approach 2: prefix and suffix product arrays. O(n) time, O(n) extra space. */
    static int[] better(int[] nums) {
        int n = nums.length;
        int[] prefix = new int[n], suffix = new int[n];      // prefix[i] = nums[0..i-1], suffix[i] = nums[i+1..n-1]
        for (int i = 0; i < n; i++) prefix[i] = i == 0 ? 1 : prefix[i - 1] * nums[i - 1];
        for (int i = n - 1; i >= 0; i--) suffix[i] = i == n - 1 ? 1 : suffix[i + 1] * nums[i + 1];
        int[] ans = new int[n];
        for (int i = 0; i < n; i++) ans[i] = prefix[i] * suffix[i];
        return ans;
    }

    /** Approach 3: prefix products in the output array, suffix as one running variable. O(n) time, O(1) extra space. */
    static int[] optimal(int[] nums) {
        int n = nums.length;
        int[] ans = new int[n];
        int left = 1;
        for (int i = 0; i < n; i++) {                        // ans[i] = product of everything left of i
            ans[i] = left;
            left *= nums[i];
        }
        int right = 1;
        for (int i = n - 1; i >= 0; i--) {                   // multiply in the product of everything right of i
            ans[i] *= right;
            right *= nums[i];
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int[] expected) {
        check(Arrays.equals(bruteForce(nums), expected), "bruteForce " + Arrays.toString(nums));
        check(Arrays.equals(better(nums), expected), "better " + Arrays.toString(nums));
        check(Arrays.equals(optimal(nums), expected), "optimal " + Arrays.toString(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4}, new int[]{24, 12, 8, 6});
        verify(new int[]{-1, 1, 0, -3, 3}, new int[]{0, 0, 9, 0, 0});        // one zero
        verify(new int[]{0, 4, 0}, new int[]{0, 0, 0});                      // two zeros: everything is 0
        verify(new int[]{2, 3}, new int[]{3, 2});                            // smallest valid input
        verify(new int[]{5}, new int[]{1});                                  // single element: empty product
        verify(new int[]{}, new int[]{});                                    // empty input
        verify(new int[]{-2, -3, 4}, new int[]{-12, -8, 6});                 // negatives
        verify(new int[]{1, 1, 1, 1, 1}, new int[]{1, 1, 1, 1, 1});
        System.out.println("OK P2404_ProductOfArrayExceptSelf");
    }
}
