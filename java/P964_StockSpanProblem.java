import java.util.*;

/** TUF 964 - Stock span problem. span[i] = number of consecutive days ending at day i with price <= prices[i]. */
public class P964_StockSpanProblem {

    /** Approach 1: walk left from every day until a strictly higher price. O(n^2) time, O(1) extra space. */
    static int[] bruteForce(int[] prices) {
        int n = prices.length;
        int[] span = new int[n];
        for (int i = 0; i < n; i++) {
            int j = i;
            while (j >= 0 && prices[j] <= prices[i]) j--;
            span[i] = i - j;                          // j is the previous greater day, or -1
        }
        return span;
    }

    /** Approach 2: previous greater element with a monotonic stack of indices. O(n) time, O(n) space. */
    static int[] optimal(int[] prices) {
        int n = prices.length;
        int[] span = new int[n];
        Deque<Integer> st = new ArrayDeque<>();      // indices whose prices strictly decrease from bottom to top
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && prices[st.peek()] <= prices[i]) st.pop();
            span[i] = st.isEmpty() ? i + 1 : i - st.peek();
            st.push(i);
        }
        return span;
    }

    /** Approach 3: the same idea online (LeetCode 901); each stack entry remembers the span it absorbed. O(1) amortised per call. */
    static class StockSpanner {
        private final Deque<int[]> st = new ArrayDeque<>();   // {price, span}

        int next(int price) {
            int span = 1;
            while (!st.isEmpty() && st.peek()[0] <= price) span += st.pop()[1];
            st.push(new int[]{price, span});
            return span;
        }
    }

    static int[] online(int[] prices) {
        StockSpanner spanner = new StockSpanner();
        int[] span = new int[prices.length];
        for (int i = 0; i < prices.length; i++) span[i] = spanner.next(prices[i]);
        return span;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] prices, int[] expected) {
        int[] b = bruteForce(prices), o = optimal(prices), s = online(prices);
        String in = Arrays.toString(prices);
        check(Arrays.equals(b, expected), "bruteForce " + in + " -> " + Arrays.toString(b));
        check(Arrays.equals(o, expected), "optimal " + in + " -> " + Arrays.toString(o));
        check(Arrays.equals(s, expected), "online " + in + " -> " + Arrays.toString(s));
    }

    public static void main(String[] args) {
        verify(new int[]{100, 80, 60, 70, 60, 75, 85}, new int[]{1, 1, 1, 2, 1, 4, 6});
        verify(new int[]{10, 4, 5, 90, 120, 80}, new int[]{1, 1, 2, 4, 5, 1});
        verify(new int[]{5, 5, 5}, new int[]{1, 2, 3});                 // equal prices extend the span
        verify(new int[]{1, 2, 3, 4}, new int[]{1, 2, 3, 4});           // increasing
        verify(new int[]{4, 3, 2, 1}, new int[]{1, 1, 1, 1});           // decreasing
        verify(new int[]{7}, new int[]{1});                             // single day
        verify(new int[]{}, new int[]{});                               // empty
        System.out.println("OK P964_StockSpanProblem");
    }
}
