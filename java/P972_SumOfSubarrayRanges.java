import java.util.*;

/** TUF 972 - Sum of Subarray Ranges. Sum of (max - min) over every contiguous subarray. */
public class P972_SumOfSubarrayRanges {

    /** Approach 1: enumerate every subarray and scan it for max and min. O(n^3) time, O(1) space. */
    static long bruteForce(int[] nums) {
        int n = nums.length;
        long total = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                int mx = nums[i], mn = nums[i];
                for (int k = i; k <= j; k++) {
                    mx = Math.max(mx, nums[k]);
                    mn = Math.min(mn, nums[k]);
                }
                total += (long) mx - mn;
            }
        }
        return total;
    }

    /** Approach 2: fix the start, extend the end and update max/min incrementally. O(n^2) time, O(1) space. */
    static long better(int[] nums) {
        int n = nums.length;
        long total = 0;
        for (int i = 0; i < n; i++) {
            int mx = nums[i], mn = nums[i];
            for (int j = i; j < n; j++) {
                mx = Math.max(mx, nums[j]);
                mn = Math.min(mn, nums[j]);
                total += (long) mx - mn;              // long: max - min can reach 2 * 10^9
            }
        }
        return total;
    }

    /** Approach 3: sum of subarray maximums minus sum of subarray minimums, each with monotonic stacks. O(n) time, O(n) space. */
    static long optimal(int[] nums) {
        return sumOfMaximums(nums) - sumOfMinimums(nums);
    }

    static long sumOfMinimums(int[] a) {
        int n = a.length;
        long total = 0;
        int[] left = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && a[st.peek()] > a[i]) st.pop();      // stop at previous element <= a[i]
            left[i] = st.isEmpty() ? i + 1 : i - st.peek();
            st.push(i);
        }
        st.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && a[st.peek()] >= a[i]) st.pop();     // stop at next element < a[i]
            int right = st.isEmpty() ? n - i : st.peek() - i;
            total += (long) a[i] * left[i] * right;
            st.push(i);
        }
        return total;
    }

    static long sumOfMaximums(int[] a) {
        int n = a.length;
        long total = 0;
        int[] left = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && a[st.peek()] < a[i]) st.pop();      // stop at previous element >= a[i]
            left[i] = st.isEmpty() ? i + 1 : i - st.peek();
            st.push(i);
        }
        st.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && a[st.peek()] <= a[i]) st.pop();     // stop at next element > a[i]
            int right = st.isEmpty() ? n - i : st.peek() - i;
            total += (long) a[i] * left[i] * right;
            st.push(i);
        }
        return total;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, long expected) {
        long b = bruteForce(nums), m = better(nums), o = optimal(nums);
        check(b == expected, "bruteForce " + Arrays.toString(nums) + " -> " + b);
        check(m == expected, "better " + Arrays.toString(nums) + " -> " + m);
        check(o == expected, "optimal " + Arrays.toString(nums) + " -> " + o);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, 4);
        verify(new int[]{1, 3, 3}, 4);                                  // duplicates
        verify(new int[]{4, -2, -3, 4, 1}, 59);                         // negatives
        verify(new int[]{2, 2, 2}, 0);                                  // all equal
        verify(new int[]{3, 1, 2, 4}, 13);
        verify(new int[]{5}, 0);                                        // single element
        verify(new int[]{}, 0);                                         // empty
        verify(new int[]{1_000_000_000, -1_000_000_000}, 2_000_000_000L); // max - min overflows int
        // sum of minimums on its own (LeetCode 907 example)
        check(sumOfMinimums(new int[]{3, 1, 2, 4}) == 17, "sumOfMinimums");
        check(sumOfMaximums(new int[]{3, 1, 2, 4}) == 30, "sumOfMaximums");
        System.out.println("OK P972_SumOfSubarrayRanges");
    }
}
