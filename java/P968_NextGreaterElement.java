import java.util.*;

/** TUF 968 - Next Greater Element. For every index, the first element to its right that is strictly greater, or -1. */
public class P968_NextGreaterElement {

    /** Approach 1: for every index, scan right until a greater element appears. O(n^2) time, O(1) extra space. */
    static int[] bruteForce(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            res[i] = -1;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] > arr[i]) {
                    res[i] = arr[j];
                    break;
                }
            }
        }
        return res;
    }

    /** Approach 2: scan right to left keeping a monotonic stack of candidates. O(n) time, O(n) space. */
    static int[] optimal(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        Deque<Integer> st = new ArrayDeque<>();      // values; top is the nearest, deeper ones are strictly larger
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && st.peek() <= arr[i]) st.pop();   // hidden behind arr[i] for everyone further left
            res[i] = st.isEmpty() ? -1 : st.peek();
            st.push(arr[i]);
        }
        return res;
    }

    /** LeetCode 496 variant: nums1 is a subset of nums2 (distinct values); answer each nums1 value by its position in nums2. O(n + m) time. */
    static int[] nextGreaterOfSubset(int[] nums1, int[] nums2) {
        int[] nge = optimal(nums2);
        Map<Integer, Integer> answerOf = new HashMap<>();
        for (int i = 0; i < nums2.length; i++) answerOf.put(nums2[i], nge[i]);
        int[] res = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) res[i] = answerOf.get(nums1[i]);
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int[] expected) {
        check(Arrays.equals(bruteForce(arr), expected), "bruteForce failed on " + Arrays.toString(arr));
        check(Arrays.equals(optimal(arr), expected), "optimal failed on " + Arrays.toString(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{4, 5, 2, 10, 8}, new int[]{5, 10, 10, -1, -1});
        verify(new int[]{1, 3, 2, 4}, new int[]{3, 4, 4, -1});
        verify(new int[]{6, 8, 0, 1, 3}, new int[]{8, -1, 1, 3, -1});
        verify(new int[]{5, 4, 3, 2, 1}, new int[]{-1, -1, -1, -1, -1});   // strictly decreasing
        verify(new int[]{3, 3, 3}, new int[]{-1, -1, -1});                  // equal is not greater
        verify(new int[]{2, 7, 3, 5, 4, 6, 8}, new int[]{7, 8, 5, 6, 6, 8, -1});
        verify(new int[]{-5, -2, -9}, new int[]{-2, -1, -1});              // negatives
        verify(new int[]{7}, new int[]{-1});                                // single element
        verify(new int[]{}, new int[]{});                                   // empty

        Random rnd = new Random(968);                                       // cross-check on small random arrays
        for (int t = 0; t < 300; t++) {
            int[] a = new int[rnd.nextInt(15)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10);
            check(Arrays.equals(bruteForce(a), optimal(a)), "random mismatch on " + Arrays.toString(a));
        }

        check(Arrays.equals(nextGreaterOfSubset(new int[]{4, 1, 2}, new int[]{1, 3, 4, 2}), new int[]{-1, 3, -1}), "LC496 example 1");
        check(Arrays.equals(nextGreaterOfSubset(new int[]{2, 4}, new int[]{1, 2, 3, 4}), new int[]{3, -1}), "LC496 example 2");
        check(Arrays.equals(nextGreaterOfSubset(new int[]{}, new int[]{1, 2}), new int[]{}), "LC496 empty query");
        check(Arrays.equals(nextGreaterOfSubset(new int[]{5, 9}, new int[]{9, 5}), new int[]{-1, -1}), "LC496 no answers");
        System.out.println("OK P968_NextGreaterElement");
    }
}
