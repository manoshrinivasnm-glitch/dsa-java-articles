import java.util.*;

/** TUF 687 - Maximum of Minimums for Every Window Size. ans[k-1] = max over windows of size k of the window minimum. */
public class P687_MaximumOfMinimumsForEveryWindowSize {

    /** Approach 1: for every size, scan every window and compute its minimum from scratch. O(n^3) time, O(1) extra space. */
    static int[] bruteForce(int[] arr) {
        int n = arr.length;
        int[] ans = new int[n];
        for (int k = 1; k <= n; k++) {
            int best = Integer.MIN_VALUE;
            for (int start = 0; start + k <= n; start++) {
                int min = Integer.MAX_VALUE;
                for (int i = start; i < start + k; i++) min = Math.min(min, arr[i]);
                best = Math.max(best, min);
            }
            ans[k - 1] = best;
        }
        return ans;
    }

    /** Approach 2: fix the left end, extend the right end keeping a running minimum. O(n^2) time, O(1) extra space. */
    static int[] better(int[] arr) {
        int n = arr.length;
        int[] ans = new int[n];
        Arrays.fill(ans, Integer.MIN_VALUE);
        for (int start = 0; start < n; start++) {
            int min = Integer.MAX_VALUE;
            for (int end = start; end < n; end++) {
                min = Math.min(min, arr[end]);             // minimum of arr[start..end]
                int len = end - start + 1;
                ans[len - 1] = Math.max(ans[len - 1], min);
            }
        }
        return ans;
    }

    /** Approach 3: each arr[i] is the minimum of the window between its previous and next smaller elements. O(n) time, O(n) space. */
    static int[] optimal(int[] arr) {
        int n = arr.length;
        int[] left = new int[n], right = new int[n];      // index of previous / next strictly smaller element
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && arr[st.peek()] >= arr[i]) st.pop();
            left[i] = st.isEmpty() ? -1 : st.peek();
            st.push(i);
        }
        st.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && arr[st.peek()] >= arr[i]) st.pop();
            right[i] = st.isEmpty() ? n : st.peek();
            st.push(i);
        }
        int[] ans = new int[n];
        Arrays.fill(ans, Integer.MIN_VALUE);
        for (int i = 0; i < n; i++) {
            int len = right[i] - left[i] - 1;              // widest window in which arr[i] is the minimum
            ans[len - 1] = Math.max(ans[len - 1], arr[i]);
        }
        for (int k = n - 2; k >= 0; k--) {
            ans[k] = Math.max(ans[k], ans[k + 1]);         // a smaller window fits inside a bigger one
        }
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int[] expected) {
        check(Arrays.equals(bruteForce(arr), expected), "bruteForce " + Arrays.toString(arr) + " got " + Arrays.toString(bruteForce(arr)));
        check(Arrays.equals(better(arr), expected), "better " + Arrays.toString(arr) + " got " + Arrays.toString(better(arr)));
        check(Arrays.equals(optimal(arr), expected), "optimal " + Arrays.toString(arr) + " got " + Arrays.toString(optimal(arr)));
    }

    public static void main(String[] args) {
        verify(new int[]{10, 20, 30, 50, 10, 70, 30}, new int[]{70, 30, 20, 10, 10, 10, 10});
        verify(new int[]{10, 20, 30}, new int[]{30, 20, 10});
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{5, 4, 3, 2, 1});
        verify(new int[]{4, 4, 4}, new int[]{4, 4, 4});              // all equal
        verify(new int[]{-3, -1, -2}, new int[]{-1, -2, -3});        // negatives
        verify(new int[]{5}, new int[]{5});                          // edge: single element
        verify(new int[]{}, new int[]{});                            // edge: empty array
        Random rnd = new Random(687);
        for (int test = 0; test < 300; test++) {
            int[] a = new int[1 + rnd.nextInt(25)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(9) - 4;   // small range forces many duplicates
            verify(a, bruteForce(a));
        }
        System.out.println("OK P687_MaximumOfMinimumsForEveryWindowSize");
    }
}
