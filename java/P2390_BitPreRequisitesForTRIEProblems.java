import java.util.*;

/** TUF 2390 - Bit PreRequisites for TRIE Problems. Fixed-width binary, reading and writing single bits, XOR,
 *  why the highest differing bit decides a comparison, and the binary trie that the maximum-XOR problems use. */
public class P2390_BitPreRequisitesForTRIEProblems {

    // ------------------------------------------------------------ numbers as bit strings

    /** All 32 bits of an int, most significant first. A binary trie stores exactly this string, one bit per level. */
    static String toBinary32(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            sb.append((n >> i) & 1);
        }
        return sb.toString();
    }

    /** How many bit positions are needed for every value in [0, maxValue]; that is the depth of the trie. */
    static int bitsNeeded(int maxValue) {
        return Math.max(1, 32 - Integer.numberOfLeadingZeros(maxValue));
    }

    // ------------------------------------------------------------ one bit at a time

    /** The four single-bit operations. All but getBit build the mask 1 << i, which has only bit i on. */
    static int getBit(int n, int i) {
        return (n >> i) & 1;                               // bring bit i down to position 0, mask the rest away
    }

    static int setBit(int n, int i) {
        return n | (1 << i);                               // OR with the mask turns bit i on
    }

    static int clearBit(int n, int i) {
        return n & ~(1 << i);                              // AND with the inverted mask turns bit i off
    }

    static int toggleBit(int n, int i) {
        return n ^ (1 << i);                               // XOR with the mask flips bit i and nothing else
    }

    /** Builds a number from bits given most significant first, the way a trie walk assembles an answer. */
    static int fromBits(int[] bitsHighToLow) {
        int value = 0;
        for (int b : bitsHighToLow) {
            value = (value << 1) | b;
        }
        return value;
    }

    // ------------------------------------------------------------ XOR

    /** Bit i of a ^ b is 1 exactly when a and b differ at bit i. */
    static int xorBitByBit(int a, int b) {
        int result = 0;
        for (int i = 31; i >= 0; i--) {
            if (getBit(a, i) != getBit(b, i)) result = setBit(result, i);
        }
        return result;
    }

    /** The partner bit that makes bit i of the XOR equal to 1 is the opposite of x's bit. */
    static int wantedBit(int x, int i) {
        return 1 - getBit(x, i);
    }

    // ------------------------------------------------------------ the highest bit wins

    /** Compares two non-negative ints by their first differing bit from the top. Agrees with Integer.compare. */
    static int compareByBits(int a, int b) {
        for (int i = 30; i >= 0; i--) {
            int x = getBit(a, i), y = getBit(b, i);
            if (x != y) return x - y;                      // 2^i outweighs all lower bits together (2^i - 1)
        }
        return 0;
    }

    // ------------------------------------------------------------ the binary trie

    /** Each node has a child for bit 0 and a child for bit 1. */
    static class BitNode {
        final BitNode[] child = new BitNode[2];
    }

    /** Stores x as the path of its bits from bit 30 down to bit 0 (inputs are non-negative). */
    static void insert(BitNode root, int x) {
        BitNode cur = root;
        for (int i = 30; i >= 0; i--) {
            int bit = getBit(x, i);
            if (cur.child[bit] == null) cur.child[bit] = new BitNode();
            cur = cur.child[bit];
        }
    }

    /** Greedy walk: take the opposite bit whenever that branch exists. Returns the largest x ^ y over stored y. */
    static int maxXor(BitNode root, int x) {
        BitNode cur = root;
        int result = 0;
        for (int i = 30; i >= 0; i--) {
            int want = wantedBit(x, i);
            if (cur.child[want] != null) {
                result = setBit(result, i);                // this bit of the XOR can be 1
                cur = cur.child[want];
            } else {
                cur = cur.child[1 - want];                 // forced: this bit of the XOR is 0
            }
        }
        return result;
    }

    /** Reference answer by trying every stored number. */
    static int maxXorBrute(int[] pool, int x) {
        int best = 0;
        for (int y : pool) best = Math.max(best, x ^ y);
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // fixed-width binary
        check(toBinary32(13).equals("00000000000000000000000000001101"), "13 in 32 bits");
        check(toBinary32(-1).equals("1".repeat(32)), "-1 is all ones");
        check(toBinary32(Integer.MIN_VALUE).equals("1" + "0".repeat(31)), "MIN_VALUE");
        for (int n : new int[]{0, 1, 5, 13, 1 << 20, Integer.MAX_VALUE, -7, Integer.MIN_VALUE}) {
            String expected = String.format("%32s", Integer.toBinaryString(n)).replace(' ', '0');
            check(toBinary32(n).equals(expected), "toBinary32 " + n);
        }
        check(bitsNeeded(0) == 1 && bitsNeeded(1) == 1 && bitsNeeded(5) == 3 && bitsNeeded(8) == 4, "bitsNeeded small");
        check(bitsNeeded(1_000_000_000) == 30 && bitsNeeded(Integer.MAX_VALUE) == 31, "bitsNeeded large");

        // single bits on 13 = 1101
        check(getBit(13, 0) == 1 && getBit(13, 1) == 0 && getBit(13, 2) == 1 && getBit(13, 3) == 1, "getBit 13");
        check(getBit(-1, 31) == 1 && getBit(Integer.MAX_VALUE, 31) == 0, "getBit sign bit");
        check(setBit(13, 1) == 15 && setBit(13, 0) == 13, "setBit");
        check(clearBit(13, 2) == 9 && clearBit(13, 1) == 13, "clearBit");
        check(toggleBit(13, 0) == 12 && toggleBit(13, 1) == 15, "toggleBit");
        check(setBit(0, 31) == Integer.MIN_VALUE, "1 << 31 is the sign bit");
        check((1 << 32) == 1 && (1L << 32) == 4294967296L, "int shift counts are taken mod 32");
        check(fromBits(new int[]{1, 1, 0, 1}) == 13 && fromBits(new int[]{}) == 0, "fromBits");

        // XOR
        int[] sample = {0, 1, 5, 9, 13, 255, 1 << 30, Integer.MAX_VALUE, -1, -13, Integer.MIN_VALUE};
        for (int a : sample) {
            check((a ^ a) == 0 && (a ^ 0) == a, "self-inverse and identity " + a);
            for (int b : sample) {
                check(xorBitByBit(a, b) == (a ^ b), "xor bit by bit " + a + " " + b);
                check((a ^ b) == (b ^ a), "commutative");
                check(((a ^ b) ^ b) == a, "xor with b twice cancels");
                for (int c : new int[]{3, 6, -2}) {
                    check(((a ^ b) ^ c) == (a ^ (b ^ c)), "associative");
                }
            }
        }
        check((5 ^ 3) == 6, "0101 ^ 0011 = 0110");
        check(wantedBit(5, 0) == 0 && wantedBit(5, 1) == 1, "wantedBit");

        // the highest differing bit decides the comparison
        int[] nonNeg = {0, 1, 2, 3, 7, 8, 12, 13, 100, 1 << 29, (1 << 30) - 1, 1 << 30, Integer.MAX_VALUE};
        for (int a : nonNeg) {
            for (int b : nonNeg) {
                check(Integer.signum(compareByBits(a, b)) == Integer.signum(Integer.compare(a, b)), "compareByBits " + a + " " + b);
            }
        }
        check(compareByBits(8, 7) > 0, "1000 beats 0111");

        // the binary trie answers max XOR exactly like brute force
        int[] pool = {3, 10, 5, 25, 2, 8};
        BitNode root = new BitNode();
        for (int y : pool) insert(root, y);
        check(maxXor(root, 5) == 28, "5 ^ 25 = 28");
        check(maxXor(root, 0) == 25, "0 ^ 25 = 25");
        Random rnd = new Random(2390);
        int[] big = new int[300];
        BitNode bigRoot = new BitNode();
        for (int i = 0; i < big.length; i++) {
            big[i] = rnd.nextInt(Integer.MAX_VALUE);
            insert(bigRoot, big[i]);
        }
        for (int t = 0; t < 300; t++) {
            int x = t % 2 == 0 ? rnd.nextInt(Integer.MAX_VALUE) : rnd.nextInt(64);
            check(maxXor(bigRoot, x) == maxXorBrute(big, x), "trie vs brute for " + x);
        }
        BitNode single = new BitNode();
        insert(single, 7);
        check(maxXor(single, 7) == 0, "edge: only partner is x itself");
        System.out.println("OK P2390_BitPreRequisitesForTRIEProblems");
    }
}
