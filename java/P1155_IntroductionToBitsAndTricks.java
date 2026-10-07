import java.util.*;

/** TUF 1155 - Introduction to Bits and Tricks. Binary representation, two's complement, the bitwise operators
 *  and the handful of one-line tricks that every later bit-manipulation problem is built from. */
public class P1155_IntroductionToBitsAndTricks {

    // ------------------------------------------------------------ conversion

    /** Decimal to binary by repeated division by 2; remainders come out least significant first. n must be >= 0. */
    static String toBinary(int n) {
        if (n == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.append(n % 2);
            n /= 2;
        }
        return sb.reverse().toString();
    }

    /** Binary string to decimal: every digit doubles what we have so far and adds itself. */
    static int fromBinary(String s) {
        int value = 0;
        for (int i = 0; i < s.length(); i++) {
            value = value * 2 + (s.charAt(i) - '0');
        }
        return value;
    }

    /** All 32 bits of an int, most significant first. This is how negatives are really stored (two's complement). */
    static String toBinary32(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            sb.append(((n >> i) & 1) == 1 ? '1' : '0');
        }
        return sb.toString();
    }

    // ------------------------------------------------------------ complements

    /** One's complement flips every bit. */
    static int onesComplement(int n) {
        return ~n;
    }

    /** Two's complement is one's complement plus one; it is exactly how -n is stored, so this equals -n. */
    static int twosComplement(int n) {
        return ~n + 1;
    }

    // ------------------------------------------------------------ shifts

    /** Left shift by k multiplies by 2^k, as long as nothing falls off the top. */
    static int multiplyByPowerOfTwo(int n, int k) {
        return n << k;
    }

    /** Arithmetic right shift divides by 2^k rounding toward negative infinity (unlike / which rounds toward zero). */
    static int divideByPowerOfTwo(int n, int k) {
        return n >> k;
    }

    // ------------------------------------------------------------ the tricks

    /** The lowest bit is the only odd power of two, so it alone decides parity. Works for negatives too. */
    static boolean isOdd(int n) {
        return (n & 1) == 1;
    }

    /** A power of two has exactly one set bit; n - 1 turns that bit off and everything below it on. */
    static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /** Bring bit i down to position 0 and read it. */
    static boolean isBitSet(int n, int i) {
        return ((n >> i) & 1) == 1;
    }

    /** OR with a mask that has only bit i on. */
    static int setBit(int n, int i) {
        return n | (1 << i);
    }

    /** AND with a mask that has every bit on except bit i. */
    static int clearBit(int n, int i) {
        return n & ~(1 << i);
    }

    /** XOR with a mask flips exactly the bits that are on in the mask. */
    static int toggleBit(int n, int i) {
        return n ^ (1 << i);
    }

    /** n - 1 flips the lowest set bit and all zeros below it, so AND-ing with n removes just that bit. */
    static int removeLowestSetBit(int n) {
        return n & (n - 1);
    }

    /** -n is ~n + 1, which agrees with n only at the lowest set bit, so the AND isolates it. */
    static int lowestSetBit(int n) {
        return n & -n;
    }

    /** Brian Kernighan: each round removes one set bit, so the loop runs once per set bit. */
    static int countSetBits(int n) {
        int count = 0;
        while (n != 0) {
            n &= n - 1;
            count++;
        }
        return count;
    }

    /** Swap without a temporary: after three XORs each variable holds the other's original value. */
    static int[] swapWithXor(int a, int b) {
        a ^= b;
        b ^= a;
        a ^= b;
        return new int[]{a, b};
    }

    /** x ^ x == 0 and x ^ 0 == x, so XOR-ing everything cancels pairs and leaves the element that appears an odd number of times. */
    static int oddOneOut(int[] a) {
        int x = 0;
        for (int v : a) x ^= v;
        return x;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // conversion
        check(toBinary(0).equals("0"), "toBinary(0)");
        check(toBinary(1).equals("1"), "toBinary(1)");
        check(toBinary(13).equals("1101"), "toBinary(13)");
        check(toBinary(Integer.MAX_VALUE).equals("1".repeat(31)), "toBinary(MAX) is 31 ones");
        for (int n : new int[]{0, 1, 13, 255, 1024, 123456789, Integer.MAX_VALUE}) {
            check(toBinary(n).equals(Integer.toBinaryString(n)), "toBinary matches library for " + n);
            check(fromBinary(toBinary(n)) == n, "round trip " + n);
        }
        check(fromBinary("1101") == 13, "fromBinary 1101");
        check(fromBinary("0") == 0, "fromBinary 0");
        check(fromBinary("100000000") == 256, "fromBinary 256");
        check(fromBinary("1".repeat(31)) == Integer.MAX_VALUE, "fromBinary MAX");
        check(toBinary32(5).equals("0".repeat(29) + "101"), "toBinary32(5)");
        check(toBinary32(-1).equals("1".repeat(32)), "toBinary32(-1)");
        check(toBinary32(-5).equals("1".repeat(29) + "011"), "toBinary32(-5)");
        check(toBinary32(Integer.MIN_VALUE).equals("1" + "0".repeat(31)), "toBinary32(MIN)");
        for (int n : new int[]{0, 7, -7, 1000, -1000, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            String lib = String.format("%32s", Integer.toBinaryString(n)).replace(' ', '0');
            check(toBinary32(n).equals(lib), "toBinary32 matches library for " + n);
        }

        // complements
        check(onesComplement(0) == -1, "~0 == -1");
        check(onesComplement(5) == -6, "~5 == -6");
        check(onesComplement(-1) == 0, "~-1 == 0");
        check(onesComplement(Integer.MAX_VALUE) == Integer.MIN_VALUE, "~MAX == MIN");
        for (int n : new int[]{0, 1, 5, -5, 123456, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            check(twosComplement(n) == -n, "two's complement of " + n + " is -n");
        }

        // operators on 12 = 1100 and 10 = 1010
        check((12 & 10) == 8, "1100 & 1010 = 1000");
        check((12 | 10) == 14, "1100 | 1010 = 1110");
        check((12 ^ 10) == 6, "1100 ^ 1010 = 0110");
        check((~12) == -13, "~12 = -13");

        // shifts
        check(multiplyByPowerOfTwo(5, 3) == 40, "5 << 3");
        check(multiplyByPowerOfTwo(-3, 2) == -12, "-3 << 2");
        check(multiplyByPowerOfTwo(7, 0) == 7, "shift by 0");
        check(multiplyByPowerOfTwo(1, 31) == Integer.MIN_VALUE, "1 << 31 lands on the sign bit");
        check(divideByPowerOfTwo(20, 2) == 5, "20 >> 2");
        check(divideByPowerOfTwo(7, 1) == 3, "7 >> 1");
        check(divideByPowerOfTwo(-7, 1) == -4 && -7 / 2 == -3, ">> floors, / truncates");
        check(divideByPowerOfTwo(-1, 31) == -1, "-1 >> 31 stays -1");
        check((-1 >>> 28) == 15, "unsigned shift fills with zeros");
        check((-8 >>> 1) == Integer.MAX_VALUE - 3, "-8 >>> 1");

        // tricks, cross-checked against the library on a mix of values
        for (int n : new int[]{0, 1, 2, 7, 8, 12, 100, -3, -4, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            check(isOdd(n) == (n % 2 != 0), "isOdd " + n);
            check(isPowerOfTwo(n) == (n > 0 && Integer.bitCount(n) == 1), "isPowerOfTwo " + n);
            check(countSetBits(n) == Integer.bitCount(n), "countSetBits " + n);
            check(lowestSetBit(n) == Integer.lowestOneBit(n), "lowestSetBit " + n);
            check(removeLowestSetBit(n) == n - Integer.lowestOneBit(n), "removeLowestSetBit " + n);
            for (int i = 0; i < 32; i++) {
                boolean bit = isBitSet(n, i);
                check(bit == ((n & (1 << i)) != 0), "isBitSet " + n + " " + i);
                check(isBitSet(setBit(n, i), i) && (setBit(n, i) ^ n) == (bit ? 0 : 1 << i), "setBit " + n + " " + i);
                check(!isBitSet(clearBit(n, i), i) && (clearBit(n, i) ^ n) == (bit ? 1 << i : 0), "clearBit " + n + " " + i);
                check(isBitSet(toggleBit(n, i), i) != bit && (toggleBit(n, i) ^ n) == (1 << i), "toggleBit " + n + " " + i);
            }
        }
        check(isOdd(-3) && !isOdd(-4) && isOdd(1) && !isOdd(0), "parity spot checks");
        check(isPowerOfTwo(1) && isPowerOfTwo(1024) && !isPowerOfTwo(0) && !isPowerOfTwo(6) && !isPowerOfTwo(Integer.MIN_VALUE), "power of two spot checks");
        check(countSetBits(0) == 0 && countSetBits(7) == 3 && countSetBits(8) == 1 && countSetBits(-1) == 32, "set bit spot checks");
        check(lowestSetBit(12) == 4 && removeLowestSetBit(12) == 8, "12 = 1100: lowest set bit is 100, removing it leaves 1000");
        check(setBit(13, 1) == 15 && clearBit(13, 2) == 9 && toggleBit(13, 0) == 12, "set/clear/toggle on 1101");

        // XOR
        check(Arrays.equals(swapWithXor(3, 5), new int[]{5, 3}), "swap 3 5");
        check(Arrays.equals(swapWithXor(7, 7), new int[]{7, 7}), "swap equal values");
        check(Arrays.equals(swapWithXor(0, -1), new int[]{-1, 0}), "swap 0 -1");
        check(Arrays.equals(swapWithXor(Integer.MIN_VALUE, Integer.MAX_VALUE), new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}), "swap extremes");
        check(oddOneOut(new int[]{2, 3, 2}) == 3, "odd one out 3");
        check(oddOneOut(new int[]{7}) == 7, "single element");
        check(oddOneOut(new int[]{1, 1, 2, 2, 5}) == 5, "odd one out 5");
        check(oddOneOut(new int[]{4, 4}) == 0 && oddOneOut(new int[]{}) == 0, "all paired or empty gives 0");
        System.out.println("OK P1155_IntroductionToBitsAndTricks");
    }
}
