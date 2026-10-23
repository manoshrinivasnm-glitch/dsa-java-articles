import java.util.*;

/** TUF 971 - Sum of Subarray Minimums (LeetCode 907). Sum of min over all contiguous subarrays, modulo 1e9+7. */
public class P971_SumOfSubarrayMinimums {

    static final int MOD = 1_000_000_007;

    /** Approach 1: fix the start, extend the end while tracking the running minimum. O(n^2) time, O(1) space. */
    static int bruteForce(int[] arr) {
        long sum = 0;                                // sum of minimums, kept modulo MOD
        for (int i = 0; i < arr.length; i++) {       // every start index i
            int min = arr[i];                        // minimum of arr[i..j], updated as j grows
            for (int j = i; j < arr.length; j++) {   // every end index j
                min = Math.min(min, arr[j]);
                sum = (sum + min) % MOD;             // add the minimum of arr[i..j]
            }
        }
        return (int) sum;
    }

    /** Approach 2: contribution technique; arr[i] is the minimum of left[i] * right[i] subarrays. O(n) time, O(n) space. */
    static int optimal(int[] arr) {
        int n = arr.length;
        int[] left = new int[n];                     // choices of start: i - (previous strictly smaller index)
        int[] right = new int[n];                    // choices of end: (next smaller-or-equal index) - i
        Deque<Integer> st = new ArrayDeque<>();      // indices whose values increase upwards
        for (int i = 0; i < n; i++) {                // left to right: find the previous smaller element
            while (!st.isEmpty() && arr[st.peek()] >= arr[i]) st.pop();   // pop values not strictly smaller
            left[i] = st.isEmpty() ? i + 1 : i - st.peek();   // distance to the previous smaller, or to -1
            st.push(i);
        }
        st.clear();                                  // reuse the stack for the second pass
        for (int i = n - 1; i >= 0; i--) {           // right to left: find the next smaller-or-equal
            // pop only strictly greater: an equal value stops here, so ties are counted once
            while (!st.isEmpty() && arr[st.peek()] > arr[i]) st.pop();
            right[i] = st.isEmpty() ? n - i : st.peek() - i;   // distance to next smaller-or-equal, or n
            st.push(i);
        }
        long sum = 0;
        for (int i = 0; i < n; i++) {                // add each element's contribution
            sum = (sum + (long) arr[i] * left[i] % MOD * right[i]) % MOD;   // value * number of subarrays
        }
        return (int) sum;
    }

    /** Approach 3: one pass; ending[i] = sum of minimums of subarrays ending at i, built from the previous smaller index. O(n) time, O(n) space. */
    static int endingAtDp(int[] arr) {
        int n = arr.length;
        long[] ending = new long[n];                 // ending[i] = sum of mins of subarrays ending at i
        Deque<Integer> st = new ArrayDeque<>();      // indices whose values increase upwards
        long sum = 0;
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && arr[st.peek()] >= arr[i]) st.pop();   // pop values not strictly smaller
            int prev = st.isEmpty() ? -1 : st.peek();   // previous smaller index, or -1 if none
            // starts after prev have min arr[i]; earlier starts reuse ending[prev]
            ending[i] = ((prev >= 0 ? ending[prev] : 0) + (long) arr[i] * (i - prev)) % MOD;
            sum = (sum + ending[i]) % MOD;           // add every subarray that ends at i
            st.push(i);
        }
        return (int) sum;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int expected) {
        check(bruteForce(arr) == expected, "bruteForce failed on " + Arrays.toString(arr));
        check(optimal(arr) == expected, "optimal failed on " + Arrays.toString(arr));
        check(endingAtDp(arr) == expected, "endingAtDp failed on " + Arrays.toString(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{3, 1, 2, 4}, 17);
        verify(new int[]{11, 81, 94, 43, 3}, 444);
        verify(new int[]{1, 2, 3}, 10);
        verify(new int[]{3, 2, 1}, 10);
        verify(new int[]{2, 2, 2}, 12);                                     // duplicates must be counted once
        verify(new int[]{1}, 1);                                            // single element
        verify(new int[]{}, 0);                                             // empty
        int[] big = new int[3000];                                          // raw total 135045000000 needs the modulus
        Arrays.fill(big, 30000);
        verify(big, 44999055);

        Random rnd = new Random(971);                                       // cross-check on random arrays with many ties
        for (int t = 0; t < 300; t++) {
            int[] a = new int[rnd.nextInt(20)];
            for (int i = 0; i < a.length; i++) a[i] = 1 + rnd.nextInt(5);
            int b = bruteForce(a);
            check(optimal(a) == b && endingAtDp(a) == b, "random mismatch on " + Arrays.toString(a));
        }
        System.out.println("OK P971_SumOfSubarrayMinimums");
    }
}
