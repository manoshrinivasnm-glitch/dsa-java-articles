import java.util.*;

/** TUF 2402 - Number of 1 Bits. Count the 1s in the 32-bit pattern of n, read as unsigned (so -1 has 32). */
public class P2402_NumberOf1Bits {

    /** Approach 1: test each of the 32 bit positions. O(32) time, O(1) space. */
    static int bruteForce(int n) {
        int count = 0;
        for (int i = 0; i < 32; i++) {
            if (((n >>> i) & 1) == 1) count++;
        }
        return count;
    }

    /** Approach 2: Brian Kernighan's loop, n & (n - 1) clears the lowest set bit. O(k) time for k set bits, O(1) space. */
    static int optimal(int n) {
        int count = 0;
        while (n != 0) {
            n &= n - 1;     // drop the lowest 1
            count++;
        }
        return count;
    }

    /** Approach 3: count inside the word in parallel (SWAR), the method behind Integer.bitCount. O(1) time, O(1) space. */
    static int parallel(int n) {
        n = n - ((n >>> 1) & 0x55555555);                  // every 2-bit field holds its own count (0..2)
        n = (n & 0x33333333) + ((n >>> 2) & 0x33333333);   // every 4-bit field holds its count (0..4)
        n = (n + (n >>> 4)) & 0x0f0f0f0f;                  // every byte holds its count (0..8)
        n = n + (n >>> 8);                                 // low byte of each 16-bit half: count of that half
        n = n + (n >>> 16);                                // low byte of the word: count of all 32 bits
        return n & 0x3f;                                   // the answer is at most 32, which fits in 6 bits
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int expected) {
        String s = Integer.toUnsignedString(n) + " (" + Integer.toBinaryString(n) + ")";
        check(bruteForce(n) == expected, "bruteForce " + s);
        check(optimal(n) == expected, "optimal " + s);
        check(parallel(n) == expected, "parallel " + s);
    }

    public static void main(String[] args) {
        verify(11, 3);                        // 1011
        verify(128, 1);                       // 10000000
        verify(0, 0);                         // edge: no bits set
        verify(-3, 31);                       // unsigned 4294967293 = 1111...1101
        verify(-1, 32);                       // edge: all 32 bits set
        verify(Integer.MIN_VALUE, 1);         // only the top bit
        verify(Integer.MAX_VALUE, 31);
        verify(0x55555555, 16);
        Random rnd = new Random(2402);
        for (int k = 0; k < 5000; k++) {
            int n = rnd.nextInt();
            verify(n, Integer.bitCount(n));
        }
        System.out.println("OK P2402_NumberOf1Bits");
    }
}
