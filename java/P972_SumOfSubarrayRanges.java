import java.util.*;

/** TUF 972 - Sum of Subarray Ranges. Sum of (max - min) over every contiguous subarray. */
public class P972_SumOfSubarrayRanges {

    /** Approach 1: enumerate every subarray and scan it for max and min. O(n^3) time, O(1) space. */
    static long bruteForce(int[] nums) {
        int n = nums.length;
        long total = 0;                               // sum of max - min over all subarrays
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {             // every subarray nums[i..j]
                int mx = nums[i], mn = nums[i];       // max and min of nums[i..j]
                for (int k = i; k <= j; k++) {        // scan the subarray from scratch
                    mx = Math.max(mx, nums[k]);
                    mn = Math.min(mn, nums[k]);
                }
                total += (long) mx - mn;              // add this subarray's range
            }
        }
        return total;
    }

    /** Approach 2: fix the start, extend the end and update max/min incrementally. O(n^2) time, O(1) space. */
    static long better(int[] nums) {
        int n = nums.length;
        long total = 0;
        for (int i = 0; i < n; i++) {
            int mx = nums[i], mn = nums[i];           // max and min of nums[i..j], grown with j
            for (int j = i; j < n; j++) {             // extend the end one step at a time
                mx = Math.max(mx, nums[j]);           // only nums[j] is new, so update in O(1)
                mn = Math.min(mn, nums[j]);
                total += (long) mx - mn;              // long: max - min can reach 2 * 10^9
            }
        }
        return total;
    }

    /** Approach 3: sum of subarray maximums minus sum of subarray minimums, each with monotonic stacks. O(n) time, O(n) space. */
    static long optimal(int[] nums) {
        return sumOfMaximums(nums) - sumOfMinimums(nums);   // each range is max - min, so the sums split
    }

    static long sumOfMinimums(int[] a) {
        int n = a.length;
        long total = 0;                               // sum of the minimum of every subarray
        int[] left = new int[n];
        Deque<Integer> st = new ArrayDeque<>();       // indices; values never decrease upwards
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && a[st.peek()] > a[i]) st.pop();      // stop at previous element <= a[i]
            left[i] = st.isEmpty() ? i + 1 : i - st.peek();   // how many starts make a[i] the minimum
            st.push(i);
        }
        st.clear();                                   // reuse the stack for the right-to-left pass
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && a[st.peek()] >= a[i]) st.pop();     // stop at next element < a[i]
            int right = st.isEmpty() ? n - i : st.peek() - i;   // how many ends make a[i] the minimum
            total += (long) a[i] * left[i] * right;   // a[i] is the minimum of left[i] * right subarrays
            st.push(i);
        }
        return total;
    }

    static long sumOfMaximums(int[] a) {
        int n = a.length;
        long total = 0;                               // sum of the maximum of every subarray
        int[] left = new int[n];
        Deque<Integer> st = new ArrayDeque<>();       // indices; values never increase upwards
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && a[st.peek()] < a[i]) st.pop();      // stop at previous element >= a[i]
            left[i] = st.isEmpty() ? i + 1 : i - st.peek();   // how many starts make a[i] the maximum
            st.push(i);
        }
        st.clear();                                   // reuse the stack for the right-to-left pass
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && a[st.peek()] <= a[i]) st.pop();     // stop at next element > a[i]
            int right = st.isEmpty() ? n - i : st.peek() - i;   // how many ends make a[i] the maximum
            total += (long) a[i] * left[i] * right;   // a[i] is the maximum of left[i] * right subarrays
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
