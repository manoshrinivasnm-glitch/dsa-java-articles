import java.util.*;

/** TUF 965 - Trapping Rainwater (LeetCode 42). Total water trapped between bars of the given heights. */
public class P965_TrappingRainwater {

    /** Approach 1: for every bar, scan both sides for the tallest wall. O(n^2) time, O(1) space. */
    static long bruteForce(int[] height) {
        int n = height.length;
        long water = 0;                              // total trapped water
        for (int i = 0; i < n; i++) {                // water above each bar is counted separately
            int leftMax = 0, rightMax = 0;           // tallest bar on each side, bar i included
            for (int j = 0; j <= i; j++) leftMax = Math.max(leftMax, height[j]);   // tallest bar in 0..i
            for (int j = i; j < n; j++) rightMax = Math.max(rightMax, height[j]);   // tallest bar in i..n-1
            water += Math.min(leftMax, rightMax) - height[i];   // lower wall sets the level, minus the bar
        }
        return water;
    }

    /** Approach 2: precompute prefix maxima and suffix maxima. O(n) time, O(n) space. */
    static long prefixSuffix(int[] height) {
        int n = height.length;
        if (n == 0) return 0;                        // no bars, no water
        int[] leftMax = new int[n], rightMax = new int[n];   // tallest bar on each side of i
        leftMax[0] = height[0];
        // leftMax[i] = tallest bar in 0..i
        for (int i = 1; i < n; i++) leftMax[i] = Math.max(leftMax[i - 1], height[i]);
        rightMax[n - 1] = height[n - 1];
        // rightMax[i] = tallest bar in i..n-1
        for (int i = n - 2; i >= 0; i--) rightMax[i] = Math.max(rightMax[i + 1], height[i]);
        long water = 0;
        // each bar holds water up to the lower of its two walls
        for (int i = 0; i < n; i++) water += Math.min(leftMax[i], rightMax[i]) - height[i];
        return water;
    }

    /** Approach 3: monotonic stack; when a taller bar arrives, fill the dip in front of it layer by layer. O(n) time, O(n) space. */
    static long monotonicStack(int[] height) {
        long water = 0;                              // total trapped water
        Deque<Integer> st = new ArrayDeque<>();      // indices; heights never increase from bottom to top
        for (int i = 0; i < height.length; i++) {
            // bar i is taller: it is the right wall of the dip on top
            while (!st.isEmpty() && height[i] > height[st.peek()]) {
                int bottom = st.pop();               // the floor of the dip being filled
                if (st.isEmpty()) break;             // no wall on the left: this layer spills out
                int left = st.peek();                // the left wall of the dip
                long width = i - left - 1;           // gap strictly between the two walls
                // the lower wall sets the water level above this floor
                long depth = Math.min(height[left], height[i]) - height[bottom];
                water += width * depth;              // add this horizontal layer of water
            }
            st.push(i);                              // i waits as a possible left wall
        }
        return water;
    }

    /** Approach 4: two pointers; the side with the lower wall already knows its answer. O(n) time, O(1) space. */
    static long optimal(int[] height) {
        int l = 0, r = height.length - 1;            // two pointers moving towards each other
        int leftMax = 0, rightMax = 0;               // tallest bar seen from each side so far
        long water = 0;
        while (l < r) {
            if (height[l] <= height[r]) {            // the right side has a wall at least this tall
                if (height[l] >= leftMax) leftMax = height[l];   // a new tallest bar holds no water
                else water += leftMax - height[l];   // trapped up to the left maximum
                l++;
            } else {                                 // the left side has a wall taller than this
                if (height[r] >= rightMax) rightMax = height[r];   // a new tallest bar holds no water
                else water += rightMax - height[r];  // trapped up to the right maximum
                r--;
            }
        }
        return water;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] height, long expected) {
        check(bruteForce(height) == expected, "bruteForce failed on " + Arrays.toString(height));
        check(prefixSuffix(height) == expected, "prefixSuffix failed on " + Arrays.toString(height));
        check(monotonicStack(height) == expected, "monotonicStack failed on " + Arrays.toString(height));
        check(optimal(height) == expected, "optimal failed on " + Arrays.toString(height));
    }

    public static void main(String[] args) {
        verify(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}, 6);
        verify(new int[]{4, 2, 0, 3, 2, 5}, 9);
        verify(new int[]{3, 0, 3}, 3);
        verify(new int[]{5, 4, 1, 2}, 1);
        verify(new int[]{2, 1, 1, 2}, 2);                                   // flat-bottomed basin
        verify(new int[]{1, 2, 3, 4}, 0);                                   // rising: everything spills left
        verify(new int[]{4, 3, 2, 1}, 0);                                   // falling: everything spills right
        verify(new int[]{1}, 0);                                            // single bar
        verify(new int[]{}, 0);                                             // no bars
        verify(new int[]{2_000_000_000, 0, 0, 2_000_000_000}, 4_000_000_000L);   // total exceeds int

        Random rnd = new Random(965);                                       // cross-check on random terrains
        for (int t = 0; t < 300; t++) {
            int[] h = new int[rnd.nextInt(25)];
            for (int i = 0; i < h.length; i++) h[i] = rnd.nextInt(8);
            long b = bruteForce(h);
            check(prefixSuffix(h) == b && monotonicStack(h) == b && optimal(h) == b, "random mismatch on " + Arrays.toString(h));
        }
        System.out.println("OK P965_TrappingRainwater");
    }
}
