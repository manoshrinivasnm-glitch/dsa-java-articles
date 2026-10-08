import java.util.*;

/** TUF 969 - Next Greater Element - 2. Circular array: the search may wrap around past the end. */
public class P969_NextGreaterElement2 {

    /** Approach 1: for every index, walk forward around the circle at most n - 1 steps. O(n^2) time, O(1) extra space. */
    static int[] bruteForce(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            res[i] = -1;
            for (int step = 1; step < n; step++) {
                int j = (i + step) % n;
                if (nums[j] > nums[i]) {
                    res[i] = nums[j];
                    break;
                }
            }
        }
        return res;
    }

    /** Approach 2: monotonic stack over the array laid out twice, scanning virtual indices 2n-1 down to 0. O(n) time, O(n) space. */
    static int[] optimal(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        Deque<Integer> st = new ArrayDeque<>();      // values; deeper entries are strictly larger
        for (int i = 2 * n - 1; i >= 0; i--) {
            int cur = nums[i % n];
            while (!st.isEmpty() && st.peek() <= cur) st.pop();
            if (i < n) res[i] = st.isEmpty() ? -1 : st.peek();   // only the second (real) pass records answers
            st.push(cur);
        }
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int[] expected) {
        check(Arrays.equals(bruteForce(nums), expected), "bruteForce failed on " + Arrays.toString(nums));
        check(Arrays.equals(optimal(nums), expected), "optimal failed on " + Arrays.toString(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 1}, new int[]{2, -1, 2});
        verify(new int[]{1, 2, 3, 4, 3}, new int[]{2, 3, 4, -1, 4});
        verify(new int[]{2, 10, 12, 1, 11}, new int[]{10, 12, -1, 11, 12});
        verify(new int[]{5, 4, 3, 2, 1}, new int[]{-1, 5, 5, 5, 5});       // everything wraps to the front
        verify(new int[]{3, 8, 4, 1, 2}, new int[]{8, -1, 8, 2, 3});
        verify(new int[]{3, 3, 3}, new int[]{-1, -1, -1});                  // equal is not greater
        verify(new int[]{7}, new int[]{-1});                                // single element
        verify(new int[]{}, new int[]{});                                   // empty

        Random rnd = new Random(969);                                       // cross-check on small random arrays
        for (int t = 0; t < 300; t++) {
            int[] a = new int[rnd.nextInt(15)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10) - 3;
            check(Arrays.equals(bruteForce(a), optimal(a)), "random mismatch on " + Arrays.toString(a));
        }
        System.out.println("OK P969_NextGreaterElement2");
    }
}
