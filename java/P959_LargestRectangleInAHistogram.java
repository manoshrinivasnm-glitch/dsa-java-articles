import java.util.*;

/** TUF 959 - Largest rectangle in a histogram. Bars of width 1; return the area of the largest axis-aligned rectangle. */
public class P959_LargestRectangleInAHistogram {

    /** Approach 1: fix the left edge, extend right, track the minimum height. O(n^2) time, O(1) space. */
    static int bruteForce(int[] heights) {
        int n = heights.length, best = 0;
        for (int i = 0; i < n; i++) {
            int minH = Integer.MAX_VALUE;
            for (int j = i; j < n; j++) {
                minH = Math.min(minH, heights[j]);
                best = Math.max(best, minH * (j - i + 1));
            }
        }
        return best;
    }

    /** Approach 2: previous and next smaller element for every bar, in two stack passes. O(n) time, O(n) space. */
    static int better(int[] heights) {
        int n = heights.length;
        int[] left = new int[n], right = new int[n];   // nearest strictly smaller bar on each side
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && heights[st.peek()] >= heights[i]) st.pop();
            left[i] = st.isEmpty() ? -1 : st.peek();
            st.push(i);
        }
        st.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && heights[st.peek()] >= heights[i]) st.pop();
            right[i] = st.isEmpty() ? n : st.peek();
            st.push(i);
        }
        int best = 0;
        for (int i = 0; i < n; i++) best = Math.max(best, heights[i] * (right[i] - left[i] - 1));
        return best;
    }

    /** Approach 3: one pass; a bar's rectangle is measured at the moment it is popped. O(n) time, O(n) space. */
    static int optimal(int[] heights) {
        int n = heights.length, best = 0;
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];       // a height-0 sentinel flushes the stack at the end
            while (!st.isEmpty() && heights[st.peek()] >= h) {
                int height = heights[st.pop()];
                int leftBoundary = st.isEmpty() ? -1 : st.peek();
                best = Math.max(best, height * (i - leftBoundary - 1));
            }
            st.push(i);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] heights, int expected) {
        int b = bruteForce(heights), m = better(heights), o = optimal(heights);
        check(b == expected, "bruteForce " + Arrays.toString(heights) + " -> " + b);
        check(m == expected, "better " + Arrays.toString(heights) + " -> " + m);
        check(o == expected, "optimal " + Arrays.toString(heights) + " -> " + o);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 1, 5, 6, 2, 3}, 10);
        verify(new int[]{2, 4}, 4);
        verify(new int[]{6, 2, 5, 4, 5, 1, 6}, 12);
        verify(new int[]{1, 1, 1, 1}, 4);                // equal heights
        verify(new int[]{1, 2, 3, 4, 5}, 9);             // increasing
        verify(new int[]{5, 4, 3, 2, 1}, 9);             // decreasing
        verify(new int[]{0, 0}, 0);                      // zero heights
        verify(new int[]{5}, 5);                         // single bar
        verify(new int[]{}, 0);                          // empty
        System.out.println("OK P959_LargestRectangleInAHistogram");
    }
}
