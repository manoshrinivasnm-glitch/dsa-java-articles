import java.util.*;

/** TUF 2410 - Set/Unset the rightmost unset bit.
 *  setRightmost*: turn on the lowest 0 bit of n (if n is all ones, like 7 = 111, leave it alone).
 *  unsetRightmost*: turn off the lowest 1 bit of n (0 stays 0). n >= 0 throughout. */
public class P2410_SetUnsetTheRightmostUnsetBit {

    /** Number of binary digits in n (1 for n = 0). */
    static int bitLength(int n) {
        int len = 0;
        do {
            len++;
            n >>>= 1;
        } while (n != 0);
        return len;
    }

    /** Approach 1: scan positions from 0 upward inside n's binary representation and turn on the first 0 found. O(log n). */
    static int setRightmostUnsetBruteForce(int n) {
        int len = bitLength(n);
        for (int i = 0; i < len; i++) {
            if (((n >> i) & 1) == 0) return n | (1 << i);
        }
        return n;                                   // every digit is already 1
    }

    /** Approach 2: n + 1 flips the trailing ones to zero and the first zero to one; OR with n restores the ones. O(1). */
    static int setRightmostUnsetOptimal(int n) {
        if (n != 0 && (n & (n + 1)) == 0) return n; // n is 2^k - 1, all ones, nothing to set
        return n | (n + 1);
    }

    /** Approach 1: scan positions from 0 upward and turn off the first 1 found. O(log n). */
    static int unsetRightmostSetBruteForce(int n) {
        for (int i = 0; i < 31; i++) {
            if (((n >> i) & 1) == 1) return n & ~(1 << i);
        }
        return n;                                   // n == 0, nothing to clear
    }

    /** Approach 2: n - 1 flips the lowest one and the zeros below it; AND with n keeps everything above. O(1). */
    static int unsetRightmostSetOptimal(int n) {
        return n & (n - 1);
    }

    /** Bonus: the lowest set bit on its own, since -n = ~n + 1 agrees with n exactly there. */
    static int isolateRightmostSet(int n) {
        return n & -n;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifySet(int n, int expected) {
        check(setRightmostUnsetBruteForce(n) == expected, "setRightmostUnsetBruteForce(" + n + ")");
        check(setRightmostUnsetOptimal(n) == expected, "setRightmostUnsetOptimal(" + n + ")");
    }

    static void verifyUnset(int n, int expected) {
        check(unsetRightmostSetBruteForce(n) == expected, "unsetRightmostSetBruteForce(" + n + ")");
        check(unsetRightmostSetOptimal(n) == expected, "unsetRightmostSetOptimal(" + n + ")");
    }

    public static void main(String[] args) {
        verifySet(6, 7);                           // 110 -> 111
        verifySet(10, 11);                         // 1010 -> 1011
        verifySet(11, 15);                         // 1011 -> 1111, the zero is above two trailing ones
        verifySet(12, 13);                         // 1100 -> 1101
        verifySet(8, 9);                           // 1000 -> 1001
        verifySet(0, 1);                           // edge: "0" has one digit and it is unset
        verifySet(1, 1);                           // "1" is all ones
        verifySet(7, 7);                           // 111 is all ones
        verifySet(15, 15);                         // 1111 is all ones
        verifySet(1023, 1023);
        verifySet(1024, 1025);
        verifySet(Integer.MAX_VALUE, Integer.MAX_VALUE); // 31 ones, no room inside the representation
        verifySet(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
        for (int n = 0; n <= 5000; n++) {
            int expected = setRightmostUnsetBruteForce(n);
            check(setRightmostUnsetOptimal(n) == expected, "set sweep " + n);
            check(expected == n || Integer.bitCount(expected) == Integer.bitCount(n) + 1, "exactly one bit turned on " + n);
        }

        verifyUnset(12, 8);                        // 1100 -> 1000
        verifyUnset(7, 6);                         // 111 -> 110
        verifyUnset(8, 0);                         // 1000 -> 0
        verifyUnset(10, 8);                        // 1010 -> 1000
        verifyUnset(6, 4);
        verifyUnset(1, 0);
        verifyUnset(0, 0);                         // edge: nothing to clear
        verifyUnset(Integer.MAX_VALUE, Integer.MAX_VALUE - 1);
        verifyUnset(1 << 30, 0);
        for (int n = 0; n <= 5000; n++) {
            int expected = unsetRightmostSetBruteForce(n);
            check(unsetRightmostSetOptimal(n) == expected, "unset sweep " + n);
            check(n - expected == isolateRightmostSet(n), "removed bit equals isolated bit " + n);
        }
        check(isolateRightmostSet(12) == 4 && isolateRightmostSet(7) == 1 && isolateRightmostSet(0) == 0, "isolate spot checks");
        System.out.println("OK P2410_SetUnsetTheRightmostUnsetBit");
    }
}
