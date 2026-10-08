import java.util.*;

/** TUF 2406 - Reverse Bits. Reverse the 32-bit pattern of n (bit 0 becomes bit 31), treating n as unsigned. */
public class P2406_ReverseBits {

    /** Approach 1: write n as a 32-character binary string, reverse the string, parse it back as unsigned. O(32) time, O(32) space. */
    static int bruteForce(int n) {
        String bits = String.format("%32s", Integer.toBinaryString(n)).replace(' ', '0');
        String reversed = new StringBuilder(bits).reverse().toString();
        return Integer.parseUnsignedInt(reversed, 2);
    }

    /** Approach 2: pop the lowest bit of n and push it onto the result, 32 times. O(32) time, O(1) space. */
    static int bitByBit(int n) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (n & 1);   // push n's lowest bit onto the right end of result
            n >>>= 1;                           // unsigned shift, so the sign bit is never copied in
        }
        return result;
    }

    /** Approach 3: divide and conquer. Swap the 16-bit halves, then bytes, nibbles, pairs and single bits. O(1) time, O(1) space. */
    static int divideAndConquer(int n) {
        n = (n >>> 16) | (n << 16);                                // swap the two 16-bit halves
        n = ((n & 0xff00ff00) >>> 8) | ((n & 0x00ff00ff) << 8);    // swap the bytes inside each half
        n = ((n & 0xf0f0f0f0) >>> 4) | ((n & 0x0f0f0f0f) << 4);    // swap the nibbles inside each byte
        n = ((n & 0xcccccccc) >>> 2) | ((n & 0x33333333) << 2);    // swap the bit pairs inside each nibble
        n = ((n & 0xaaaaaaaa) >>> 1) | ((n & 0x55555555) << 1);    // swap the bits inside each pair
        return n;
    }

    /** Approach 4 (called many times): reverse each of the 4 bytes with a 256-entry table built once. O(1) per call. */
    static final int[] REVERSED_BYTE = buildByteTable();

    static int[] buildByteTable() {
        int[] table = new int[256];
        for (int b = 0; b < 256; b++) table[b] = bitByBit(b) >>> 24;   // the 8 bits of b land in the top byte
        return table;
    }

    static int byteTable(int n) {
        return (REVERSED_BYTE[n & 0xff] << 24)
                | (REVERSED_BYTE[(n >>> 8) & 0xff] << 16)
                | (REVERSED_BYTE[(n >>> 16) & 0xff] << 8)
                | REVERSED_BYTE[n >>> 24];
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int expected) {
        String s = Integer.toUnsignedString(n);
        check(bruteForce(n) == expected, "bruteForce " + s);
        check(bitByBit(n) == expected, "bitByBit " + s);
        check(divideAndConquer(n) == expected, "divideAndConquer " + s);
        check(byteTable(n) == expected, "byteTable " + s);
    }

    public static void main(String[] args) {
        verify(43261596, 964176192);              // 00000010100101000001111010011100 -> 00111001011110000010100101000000
        verify(-3, -1073741825);                  // unsigned 4294967293 -> 3221225471
        verify(0, 0);                             // edge: all zeros stay all zeros
        verify(1, Integer.MIN_VALUE);             // bit 0 moves to bit 31
        verify(Integer.MIN_VALUE, 1);             // and back
        verify(-1, -1);                           // edge: all ones
        verify(0x0000ffff, 0xffff0000);
        Random rnd = new Random(2406);
        for (int k = 0; k < 5000; k++) {
            int n = rnd.nextInt();
            verify(n, Integer.reverse(n));
            check(bitByBit(bitByBit(n)) == n, "reversing twice gives n back");
        }
        System.out.println("OK P2406_ReverseBits");
    }
}
