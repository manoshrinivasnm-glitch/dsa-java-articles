import java.util.*;

/** TUF 963 - Sliding Window Maximum. Maximum of every window of size k as it slides from left to right. */
public class P963_SlidingWindowMaximum {

    /** Approach 1: scan every window. O(n * k) time, O(1) extra space. */
    static int[] bruteForce(int[] nums, int k) {
        int n = nums.length;
        if (n == 0 || k <= 0 || k > n) return new int[0];
        int[] res = new int[n - k + 1];
        for (int i = 0; i + k <= n; i++) {
            int mx = Integer.MIN_VALUE;
            for (int j = i; j < i + k; j++) mx = Math.max(mx, nums[j]);
            res[i] = mx;
        }
        return res;
    }

    /** Approach 2: max-heap of {value, index} with lazy removal of indices that left the window. O(n log n) time, O(n) space. */
    static int[] better(int[] nums, int k) {
        int n = nums.length;
        if (n == 0 || k <= 0 || k > n) return new int[0];
        int[] res = new int[n - k + 1];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(b[0], a[0]));   // largest value first
        for (int i = 0; i < n; i++) {
            pq.offer(new int[]{nums[i], i});
            if (i >= k - 1) {
                while (pq.peek()[1] <= i - k) pq.poll();      // discard maxima that are no longer in the window
                res[i - k + 1] = pq.peek()[0];
            }
        }
        return res;
    }

    /** Approach 3: monotonic deque of indices whose values decrease from front to back. O(n) time, O(k) space. */
    static int[] optimal(int[] nums, int k) {
        int n = nums.length;
        if (n == 0 || k <= 0 || k > n) return new int[0];
        int[] res = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (!dq.isEmpty() && dq.peekFirst() <= i - k) dq.pollFirst();     // front slid out of the window
            while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[i]) dq.pollLast();   // can never be a maximum again
            dq.offerLast(i);
            if (i >= k - 1) res[i - k + 1] = nums[dq.peekFirst()];
        }
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int[] expected) {
        int[] b = bruteForce(nums, k), m = better(nums, k), o = optimal(nums, k);
        String in = Arrays.toString(nums) + " k=" + k;
        check(Arrays.equals(b, expected), "bruteForce " + in + " -> " + Arrays.toString(b));
        check(Arrays.equals(m, expected), "better " + in + " -> " + Arrays.toString(m));
        check(Arrays.equals(o, expected), "optimal " + in + " -> " + Arrays.toString(o));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3, new int[]{3, 3, 5, 5, 6, 7});
        verify(new int[]{1}, 1, new int[]{1});                          // single element
        verify(new int[]{9, 8, 7, 6}, 2, new int[]{9, 8, 7});            // decreasing
        verify(new int[]{1, 2, 3, 4}, 4, new int[]{4});                  // k == n
        verify(new int[]{4, 4, 4}, 2, new int[]{4, 4});                  // duplicates
        verify(new int[]{1, 3, 1, 2, 0, 5}, 3, new int[]{3, 3, 2, 5});
        verify(new int[]{-7, -8, 7, 5, 7, 1, 6, 0}, 4, new int[]{7, 7, 7, 7, 7});
        verify(new int[]{5, -1, 2}, 1, new int[]{5, -1, 2});             // k == 1 returns the array
        verify(new int[]{}, 1, new int[]{});                             // empty input
        System.out.println("OK P963_SlidingWindowMaximum");
    }
}
