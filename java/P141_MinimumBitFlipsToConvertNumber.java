import java.util.*;

/** TUF 141 - Minimum Bit Flips to Convert Number (LeetCode 2220). Fewest single-bit flips turning start into goal. */
public class P141_MinimumBitFlipsToConvertNumber {

    /** Approach 1: walk all 32 positions and count where the two bits disagree. O(32) time. */
    static int bruteForce(int start, int goal) {
        int flips = 0;
        for (int i = 0; i < 32; i++) {
            if (((start >> i) & 1) != ((goal >> i) & 1)) flips++;
        }
        return flips;
    }

    /** Approach 2: XOR marks every differing position with a 1; count the ones by shifting them out. O(number of bits). */
    static int better(int start, int goal) {
        int diff = start ^ goal;
        int flips = 0;
        while (diff != 0) {
            flips += diff & 1;
            diff >>>= 1;
        }
        return flips;
    }

    /** Approach 3: XOR, then Brian Kernighan drops one set bit per round. O(number of flips) time. */
    static int optimal(int start, int goal) {
        int diff = start ^ goal;
        int flips = 0;
        while (diff != 0) {
            diff &= diff - 1;
            flips++;
        }
        return flips;
    }

    /** Approach 3, one-liner: the library popcount compiles to a single CPU instruction on most machines. */
    static int optimalBuiltin(int start, int goal) {
        return Integer.bitCount(start ^ goal);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int start, int goal, int expected) {
        check(bruteForce(start, goal) == expected, "bruteForce(" + start + ", " + goal + ")");
        check(better(start, goal) == expected, "better(" + start + ", " + goal + ")");
        check(optimal(start, goal) == expected, "optimal(" + start + ", " + goal + ")");
        check(optimalBuiltin(start, goal) == expected, "optimalBuiltin(" + start + ", " + goal + ")");
    }

    public static void main(String[] args) {
        verify(10, 7, 3);                          // 1010 -> 0111
        verify(3, 4, 3);                           // 011 -> 100
        verify(0, 0, 0);                           // edge: nothing to do
        verify(5, 5, 0);                           // equal numbers
        verify(0, 1 << 30, 1);
        verify(1, 0, 1);
        verify(1_000_000_000, 0, 13);
        verify(Integer.MAX_VALUE, 0, 31);
        verify(-1, 0, 32);                         // every bit differs
        verify(Integer.MIN_VALUE, Integer.MAX_VALUE, 32);
        verify(-1, Integer.MAX_VALUE, 1);          // only the sign bit differs
        for (int start : new int[]{0, 1, 13, 1024, 123456789, -7, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            for (int goal : new int[]{0, 2, 13, 4095, 987654321, -1, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
                verify(start, goal, Integer.bitCount(start ^ goal));
                check(optimal(start, goal) == optimal(goal, start), "symmetric " + start + " " + goal);
            }
        }
        System.out.println("OK P141_MinimumBitFlipsToConvertNumber");
    }
}
