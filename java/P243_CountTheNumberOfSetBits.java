import java.util.*;

/** TUF 243 - Count the Number of Set Bits.
 *  Part A: how many 1 bits does a single int have (negatives are treated as their 32-bit pattern)?
 *  Part B: how many 1 bits are there in total across the binary representations of 1, 2, ..., n? */
public class P243_CountTheNumberOfSetBits {

    // ------------------------------------------------------------ Part A: one number

    /** Approach 1: look at the lowest bit, shift it out, repeat until nothing is left. O(number of bits) time. */
    static int bruteForce(int n) {
        int count = 0;
        while (n != 0) {
            count += n & 1;
            n >>>= 1;                       // unsigned shift so a negative n terminates after 32 rounds
        }
        return count;
    }

    /** Approach 2: Brian Kernighan. n & (n - 1) removes the lowest set bit, so the loop runs once per set bit. */
    static int optimal(int n) {
        int count = 0;
        while (n != 0) {
            n &= n - 1;
            count++;
        }
        return count;
    }

    /** TABLE[b] = number of set bits in the byte b, built from the recurrence bits(b) = bits(b >> 1) + (b & 1). */
    static final int[] TABLE = buildTable();

    static int[] buildTable() {
        int[] t = new int[256];
        for (int b = 1; b < 256; b++) t[b] = t[b >> 1] + (b & 1);
        return t;
    }

    /** Approach 3: split the int into four bytes and add up four table lookups. O(1) time, 256 ints of space. */
    static int lookupTable(int n) {
        return TABLE[n & 0xFF] + TABLE[(n >>> 8) & 0xFF] + TABLE[(n >>> 16) & 0xFF] + TABLE[n >>> 24];
    }

    // ------------------------------------------------------------ Part B: all numbers 1..n

    /** Approach 1: add up the per-number counts. O(n log n) time. */
    static long totalBruteForce(int n) {
        long total = 0;
        for (int i = 1; i <= n; i++) total += optimal(i);
        return total;
    }

    /** Approach 2: bit i is 0 for 2^i numbers, then 1 for 2^i numbers, repeating; count the 1-runs inside 0..n. O(log n). */
    static long totalOptimal(int n) {
        long total = 0;
        for (int i = 0; i < 31; i++) {
            long half = 1L << i, period = half << 1;
            long full = (n + 1L) / period * half;          // complete periods, each contributes 2^i ones
            long partial = (n + 1L) % period - half;        // the unfinished period: ones only after its zero-run
            total += full + Math.max(0L, partial);
        }
        return total;
    }

    /** Approach 3: split at the highest power of two 2^x <= n and recurse on the remainder. O(log n) time. */
    static long totalRecursive(int n) {
        if (n <= 0) return 0;
        int x = 0;
        while ((1L << (x + 1)) <= n) x++;                   // 2^x <= n < 2^(x+1)
        long belowTop = x == 0 ? 0 : (long) x * (1L << (x - 1)); // set bits among 0 .. 2^x - 1
        long topBits = n - (1L << x) + 1;                   // bit x is set in every number 2^x .. n
        return belowTop + topBits + totalRecursive(n - (1 << x));
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int expected) {
        check(bruteForce(n) == expected, "bruteForce(" + n + ")");
        check(optimal(n) == expected, "optimal(" + n + ")");
        check(lookupTable(n) == expected, "lookupTable(" + n + ")");
    }

    static void verifyTotal(int n, long expected, boolean includeBrute) {
        if (includeBrute) check(totalBruteForce(n) == expected, "totalBruteForce(" + n + ")");
        check(totalOptimal(n) == expected, "totalOptimal(" + n + ")");
        check(totalRecursive(n) == expected, "totalRecursive(" + n + ")");
    }

    public static void main(String[] args) {
        verify(0, 0);                              // edge: no set bits
        verify(1, 1);
        verify(7, 3);                              // 111
        verify(8, 1);                              // 1000
        verify(13, 3);                             // 1101
        verify(255, 8);
        verify(1023, 10);
        verify(Integer.MAX_VALUE, 31);
        verify(Integer.MIN_VALUE, 1);              // only the sign bit
        verify(-1, 32);                            // every bit set
        verify(0xAAAAAAAA, 16);                    // 1010...1010, a negative int literal
        for (int n : new int[]{0, 5, 1000, 123456789, -3, -1024, Integer.MAX_VALUE, Integer.MIN_VALUE}) {
            verify(n, Integer.bitCount(n));
        }

        verifyTotal(0, 0, true);                   // edge: empty range
        verifyTotal(1, 1, true);
        verifyTotal(2, 2, true);
        verifyTotal(3, 4, true);
        verifyTotal(4, 5, true);
        verifyTotal(6, 9, true);                   // 1,1,2,1,2,2
        verifyTotal(7, 12, true);
        verifyTotal(8, 13, true);
        verifyTotal(15, 32, true);
        verifyTotal(16, 33, true);
        verifyTotal(17, 35, true);
        verifyTotal(1000, 4938, true);
        verifyTotal(1023, 5120, true);
        verifyTotal(1024, 5121, true);
        verifyTotal(65535, 524288, true);
        verifyTotal(1_000_000, 9_884_999L, true);
        verifyTotal(1_000_000_000, 14_846_928_141L, false);   // needs a long; brute force would take too long
        verifyTotal(Integer.MAX_VALUE, 33_285_996_544L, false);
        for (int n = 0; n <= 3000; n++) {
            long expected = totalBruteForce(n);
            check(totalOptimal(n) == expected && totalRecursive(n) == expected, "sweep " + n);
        }
        System.out.println("OK P243_CountTheNumberOfSetBits");
    }
}
