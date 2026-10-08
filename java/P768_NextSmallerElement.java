import java.util.*;

/** TUF 768 - Next Smaller Element. For every index, the first element to its right that is strictly smaller, or -1. */
public class P768_NextSmallerElement {

    /** Approach 1: for every index, scan right until a smaller element appears. O(n^2) time, O(1) extra space. */
    static int[] bruteForce(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) {
            res[i] = -1;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[i]) {
                    res[i] = arr[j];
                    break;
                }
            }
        }
        return res;
    }

    /** Approach 2: scan right to left keeping a stack whose deeper entries are strictly smaller. O(n) time, O(n) space. */
    static int[] optimal(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && st.peek() >= arr[i]) st.pop();   // arr[i] is nearer and no larger: they can never win again
            res[i] = st.isEmpty() ? -1 : st.peek();
            st.push(arr[i]);
        }
        return res;
    }

    /** Variant (nearest smaller element on the LEFT, as on InterviewBit): same stack, scanned left to right. O(n) time, O(n) space. */
    static int[] previousSmaller(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && st.peek() >= arr[i]) st.pop();
            res[i] = st.isEmpty() ? -1 : st.peek();
            st.push(arr[i]);
        }
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
        verify(new int[]{4, 8, 5, 2, 25}, new int[]{2, 5, 2, -1, -1});
        verify(new int[]{13, 7, 6, 12}, new int[]{7, 6, -1, -1});
        verify(new int[]{3, 2, 1}, new int[]{2, 1, -1});
        verify(new int[]{1, 2, 3}, new int[]{-1, -1, -1});                  // strictly increasing
        verify(new int[]{2, 2, 2}, new int[]{-1, -1, -1});                  // equal is not smaller
        verify(new int[]{3, -2, 0, -7}, new int[]{-2, -7, -7, -1});         // negatives
        verify(new int[]{5}, new int[]{-1});                                // single element
        verify(new int[]{}, new int[]{});                                   // empty

        Random rnd = new Random(768);                                       // cross-check on small random arrays
        for (int t = 0; t < 300; t++) {
            int[] a = new int[rnd.nextInt(15)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(10);
            check(Arrays.equals(bruteForce(a), optimal(a)), "random mismatch on " + Arrays.toString(a));
        }

        check(Arrays.equals(previousSmaller(new int[]{4, 5, 2, 10, 8}), new int[]{-1, 4, -1, 2, 2}), "previousSmaller 1");
        check(Arrays.equals(previousSmaller(new int[]{1, 3, 0, 2, 5}), new int[]{-1, 1, -1, 0, 2}), "previousSmaller 2");
        check(Arrays.equals(previousSmaller(new int[]{3, 2, 1}), new int[]{-1, -1, -1}), "previousSmaller 3");
        check(Arrays.equals(previousSmaller(new int[]{2, 2, 2}), new int[]{-1, -1, -1}), "previousSmaller 4");
        check(Arrays.equals(previousSmaller(new int[]{}), new int[]{}), "previousSmaller empty");
        System.out.println("OK P768_NextSmallerElement");
    }
}
