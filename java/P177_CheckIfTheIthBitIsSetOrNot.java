import java.util.*;

/** TUF 177 - Check if the i-th bit is Set or Not. Bit 0 is the least significant bit. */
public class P177_CheckIfTheIthBitIsSetOrNot {

    /** Approach 1: peel off binary digits by repeated division until digit i is at the front. O(i) time. */
    static boolean bruteForce(int n, int i) {
        long v = n & 0xFFFFFFFFL;              // unsigned view, so a negative n behaves like its 32-bit pattern
        for (int k = 0; k < i; k++) v /= 2;
        return v % 2 == 1;
    }

    /** Approach 2: build a mask with a single 1 at position i and AND it with n. O(1) time. */
    static boolean leftShiftMask(int n, int i) {
        return (n & (1 << i)) != 0;
    }

    /** Approach 3: bring bit i down to position 0 and look at the lowest bit. O(1) time. */
    static boolean rightShift(int n, int i) {
        return ((n >> i) & 1) == 1;
    }

    /** Companion operations built from the same mask. */
    static int setBit(int n, int i) {
        return n | (1 << i);
    }

    static int clearBit(int n, int i) {
        return n & ~(1 << i);
    }

    static int toggleBit(int n, int i) {
        return n ^ (1 << i);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int i, boolean expected) {
        check(bruteForce(n, i) == expected, "bruteForce(" + n + ", " + i + ")");
        check(leftShiftMask(n, i) == expected, "leftShiftMask(" + n + ", " + i + ")");
        check(rightShift(n, i) == expected, "rightShift(" + n + ", " + i + ")");
    }

    public static void main(String[] args) {
        verify(13, 0, true);                      // 13 = 1101
        verify(13, 1, false);
        verify(13, 2, true);
        verify(13, 3, true);
        verify(13, 4, false);                     // beyond the highest set bit
        verify(0, 0, false);                      // zero has no set bits
        verify(1, 0, true);
        verify(8, 3, true);
        verify(8, 2, false);
        verify(Integer.MAX_VALUE, 30, true);
        verify(Integer.MAX_VALUE, 31, false);     // the sign bit of the largest positive int is 0
        verify(-1, 31, true);                     // every bit of -1 is set
        verify(Integer.MIN_VALUE, 31, true);      // only the sign bit is set
        verify(Integer.MIN_VALUE, 0, false);
        for (int n : new int[]{0, 1, 5, 13, 1024, 123456789, -1, -2, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            String s = Integer.toBinaryString(n);          // 32 chars for negatives, no leading zeros otherwise
            for (int i = 0; i < 32; i++) {
                boolean expected = i < s.length() && s.charAt(s.length() - 1 - i) == '1';
                verify(n, i, expected);
            }
        }
        check(setBit(13, 1) == 15 && setBit(13, 0) == 13, "setBit on 1101");
        check(clearBit(13, 2) == 9 && clearBit(13, 1) == 13, "clearBit on 1101");
        check(toggleBit(13, 1) == 15 && toggleBit(13, 0) == 12, "toggleBit on 1101");
        check(setBit(0, 31) == Integer.MIN_VALUE && clearBit(-1, 31) == Integer.MAX_VALUE, "sign bit via mask");
        System.out.println("OK P177_CheckIfTheIthBitIsSetOrNot");
    }
}
