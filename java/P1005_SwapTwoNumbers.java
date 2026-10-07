import java.util.*;

/** TUF 1005 - Swap Two Numbers. Each method takes (a, b), swaps the two locals and returns {a, b};
 *  the array is only there because Java passes ints by value, so a caller could never see a swap of its own variables. */
public class P1005_SwapTwoNumbers {

    /** Approach 1: the plain way, a third variable holds one value while the other moves. O(1). */
    static int[] usingTemp(int a, int b) {
        int temp = a;
        a = b;
        b = temp;
        return new int[]{a, b};
    }

    /** Approach 2: store the sum, then peel each original off it. Wraps around on overflow but still lands correctly in Java. */
    static int[] usingArithmetic(int a, int b) {
        a = a + b;        // a holds a0 + b0
        b = a - b;        // (a0 + b0) - b0 = a0
        a = a - b;        // (a0 + b0) - a0 = b0
        return new int[]{a, b};
    }

    /** Approach 3: x ^ x == 0 and x ^ 0 == x, so three XORs move each value across. Never overflows. */
    static int[] usingXor(int a, int b) {
        a = a ^ b;        // a holds a0 ^ b0
        b = a ^ b;        // (a0 ^ b0) ^ b0 = a0
        a = a ^ b;        // (a0 ^ b0) ^ a0 = b0
        return new int[]{a, b};
    }

    /** XOR swap on two array slots; the i == j guard matters, see the unguarded version below. */
    static void swapInArray(int[] arr, int i, int j) {
        if (i == j) return;
        arr[i] ^= arr[j];
        arr[j] ^= arr[i];
        arr[i] ^= arr[j];
    }

    /** The trap: when both references name the same slot, the first XOR zeroes it and the value is gone. */
    static void swapInArrayUnguarded(int[] arr, int i, int j) {
        arr[i] ^= arr[j];
        arr[j] ^= arr[i];
        arr[i] ^= arr[j];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int a, int b) {
        int[] expected = {b, a};
        check(Arrays.equals(usingTemp(a, b), expected), "usingTemp(" + a + ", " + b + ")");
        check(Arrays.equals(usingArithmetic(a, b), expected), "usingArithmetic(" + a + ", " + b + ")");
        check(Arrays.equals(usingXor(a, b), expected), "usingXor(" + a + ", " + b + ")");
    }

    public static void main(String[] args) {
        verify(3, 5);
        verify(5, 3);
        verify(7, 7);                                          // equal values must survive
        verify(0, 0);                                          // edge: both zero
        verify(0, -1);
        verify(-20, 15);                                       // mixed signs
        verify(Integer.MAX_VALUE, Integer.MIN_VALUE);          // extremes
        verify(2_000_000_000, 2_000_000_000);                  // a + b overflows int, the arithmetic swap still works
        verify(Integer.MAX_VALUE, 1);
        verify(Integer.MIN_VALUE, -1);

        int[] arr = {1, 2, 3};
        swapInArray(arr, 0, 2);
        check(Arrays.equals(arr, new int[]{3, 2, 1}), "swap ends");
        swapInArray(arr, 1, 1);
        check(Arrays.equals(arr, new int[]{3, 2, 1}), "guarded swap of a slot with itself is a no-op");
        swapInArrayUnguarded(arr, 0, 2);
        check(Arrays.equals(arr, new int[]{1, 2, 3}), "unguarded swap of different slots is fine");
        swapInArrayUnguarded(arr, 1, 1);
        check(arr[1] == 0, "unguarded XOR swap of a slot with itself wipes it to 0");
        System.out.println("OK P1005_SwapTwoNumbers");
    }
}
