import java.util.*;

/** TUF 146 - XOR of all integers in the range [L, R], with 0 <= L <= R. */
public class P146_XOROfNumbersInAGivenRange {

    /** Approach 1: XOR every number from L to R one by one. O(R - L) time, O(1) space. */
    static int bruteForce(int l, int r) {
        int xor = 0;
        for (long i = l; i <= r; i++) xor ^= (int) i;    // long counter: i++ would overflow when r == Integer.MAX_VALUE
        return xor;
    }

    /** Approach 2: decide each bit on its own by counting how many numbers in [L, R] have it set. 31 counts, each O(1). */
    static int better(int l, int r) {
        int result = 0;
        for (int bit = 0; bit < 31; bit++) {
            long ones = countSetBit(r, bit) - countSetBit(l - 1, bit);
            if ((ones & 1) == 1) result |= 1 << bit;      // an odd number of 1s XORs to 1
        }
        return result;
    }

    /** How many integers in [0, n] have bit `bit` set (0 when n < 0). */
    static long countSetBit(int n, int bit) {
        if (n < 0) return 0;
        long period = 1L << (bit + 1);                    // the bit repeats: `half` zeros, then `half` ones
        long half = 1L << bit;
        long full = (n + 1L) / period * half;             // complete periods contribute `half` ones each
        long rem = (n + 1L) % period;                     // the unfinished period at the end
        return full + Math.max(0, rem - half);
    }

    /** Approach 3: XOR of 0..n repeats every 4 numbers, so XOR(L..R) = XOR(0..R) ^ XOR(0..L-1). O(1) time. */
    static int optimal(int l, int r) {
        return xorUpTo(r) ^ xorUpTo(l - 1);
    }

    /** XOR of all integers in [0, n]; 0 when n < 0. */
    static int xorUpTo(int n) {
        if (n < 0) return 0;
        return switch (n % 4) {
            case 0 -> n;
            case 1 -> 1;
            case 2 -> n + 1;
            default -> 0;
        };
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int l, int r, int expected) {
        String label = "[" + l + ", " + r + "]";
        check(bruteForce(l, r) == expected, "bruteForce " + label);
        check(better(l, r) == expected, "better " + label);
        check(optimal(l, r) == expected, "optimal " + label);
    }

    public static void main(String[] args) {
        verify(1, 4, 4);                       // 1 ^ 2 ^ 3 ^ 4
        verify(3, 3, 3);                       // single number: L == R
        verify(0, 0, 0);
        verify(0, 7, 0);                       // a full block of 8 cancels completely
        verify(5, 10, 15);
        verify(4, 7, 0);                       // any block of 4 starting at a multiple of 4 is 0
        verify(1_000_000_000, 1_000_000_010, bruteForce(1_000_000_000, 1_000_000_010));
        verify(Integer.MAX_VALUE - 3, Integer.MAX_VALUE, 0);
        verify(123, 456_789, bruteForce(123, 456_789));

        int[] firstEight = {0, 1, 3, 0, 4, 1, 7, 0};   // XOR(0..n) for n = 0..7
        for (int n = 0; n < 8; n++) check(xorUpTo(n) == firstEight[n], "xorUpTo(" + n + ")");
        check(countSetBit(5, 0) == 3 && countSetBit(5, 1) == 2 && countSetBit(5, 2) == 2, "countSetBit on 0..5");
        check(countSetBit(-1, 0) == 0, "countSetBit below 0");
        System.out.println("OK P146_XOROfNumbersInAGivenRange");
    }
}
