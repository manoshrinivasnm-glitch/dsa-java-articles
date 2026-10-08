import java.util.*;

/** TUF 2393 - Counting Bits. Return ans[0..n] where ans[i] is the number of 1 bits in i. */
public class P2393_CountingBits {

    /** Approach 1: count the bits of every number on its own with Kernighan's loop. O(n log n) time, O(1) extra space. */
    static int[] bruteForce(int n) {
        int[] ans = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            int x = i, count = 0;
            while (x != 0) {
                x &= x - 1;     // drop the lowest 1
                count++;
            }
            ans[i] = count;
        }
        return ans;
    }

    /** Approach 2: DP on the last bit. i >> 1 is i without its last bit and is smaller, so its answer is known. O(n) time, O(1) extra space. */
    static int[] optimalShift(int n) {
        int[] ans = new int[n + 1];
        for (int i = 1; i <= n; i++) ans[i] = ans[i >> 1] + (i & 1);
        return ans;
    }

    /** Approach 3: DP on the lowest set bit. i & (i - 1) is i with its lowest 1 removed. O(n) time, O(1) extra space. */
    static int[] optimalLowestBit(int n) {
        int[] ans = new int[n + 1];
        for (int i = 1; i <= n; i++) ans[i] = ans[i & (i - 1)] + 1;
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[] expected) {
        check(Arrays.equals(bruteForce(n), expected), "bruteForce n=" + n);
        check(Arrays.equals(optimalShift(n), expected), "optimalShift n=" + n);
        check(Arrays.equals(optimalLowestBit(n), expected), "optimalLowestBit n=" + n);
    }

    public static void main(String[] args) {
        verify(2, new int[]{0, 1, 1});
        verify(5, new int[]{0, 1, 1, 2, 1, 2});
        verify(0, new int[]{0});                                    // edge: only the number 0
        verify(1, new int[]{0, 1});
        verify(8, new int[]{0, 1, 1, 2, 1, 2, 2, 3, 1});           // 8 drops back to a single 1
        verify(15, new int[]{0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4});
        int big = 100_000;                                          // the largest n in the constraints
        int[] expected = new int[big + 1];
        for (int i = 0; i <= big; i++) expected[i] = Integer.bitCount(i);
        verify(big, expected);
        System.out.println("OK P2393_CountingBits");
    }
}
