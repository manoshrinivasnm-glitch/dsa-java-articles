import java.util.*;

/** TUF 2396 - Container with most water. Two lines i < j hold min(h[i], h[j]) * (j - i) water; maximise it. */
public class P2396_ContainerWithMostWater {

    /** Approach 1: try every pair of lines. O(n^2) time, O(1) space. */
    static long bruteForce(int[] height) {
        long best = 0;
        for (int i = 0; i < height.length; i++) {
            for (int j = i + 1; j < height.length; j++) {
                long area = (long) Math.min(height[i], height[j]) * (j - i);
                best = Math.max(best, area);
            }
        }
        return best;
    }

    /** Approach 2: two pointers from the ends, always moving the shorter line inward. O(n) time, O(1) space. */
    static long optimal(int[] height) {
        long best = 0;
        int i = 0, j = height.length - 1;
        while (i < j) {
            long area = (long) Math.min(height[i], height[j]) * (j - i);
            best = Math.max(best, area);
            if (height[i] < height[j]) i++;                  // the shorter line cannot do better with any closer partner
            else j--;
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] height, long expected) {
        check(bruteForce(height) == expected, "bruteForce " + Arrays.toString(height));
        check(optimal(height) == expected, "optimal " + Arrays.toString(height));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}, 49);             // lines 1 and 8: min(8, 7) * 7
        verify(new int[]{1, 1}, 1);
        verify(new int[]{4, 3, 2, 1, 4}, 16);                          // the two outer lines
        verify(new int[]{1, 2, 1}, 2);
        verify(new int[]{1, 2, 4, 3}, 4);                              // heights 2 and 3 at distance 2 beat the outer pair (1 * 3)
        verify(new int[]{0, 0}, 0);                                    // zero-height lines
        verify(new int[]{5}, 0);                                       // one line holds no water
        verify(new int[]{}, 0);                                        // no lines
        verify(new int[]{1_000_000_000, 1, 1_000_000_000}, 2_000_000_000L);   // would overflow if the product were int
        verify(new int[]{2, 3, 10, 5, 7, 8, 9}, 36);                   // best pair is inside: 10 and 9 at distance 4
        System.out.println("OK P2396_ContainerWithMostWater");
    }
}
