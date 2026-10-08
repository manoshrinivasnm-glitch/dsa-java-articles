import java.util.*;

/** TUF 2411 - Sum of Two Integers. Return a + b without using the + or - operators. */
public class P2411_SumOfTwoIntegers {

    /** Approach 1: simulate a 32-bit ripple-carry adder, one bit position at a time. O(32) time, O(1) space. */
    static int rippleCarry(int a, int b) {
        int result = 0, carry = 0;
        for (int mask = 1; mask != 0; mask <<= 1) {   // 32 rounds: after bit 31 the 1 falls off the top
            int x = (a & mask) != 0 ? 1 : 0;
            int y = (b & mask) != 0 ? 1 : 0;
            if ((x ^ y ^ carry) == 1) result |= mask;  // sum bit of a full adder
            carry = (x & y) | (carry & (x ^ y));       // carry into the next position
        }
        return result;
    }

    /** Approach 2: XOR adds without carries, (a & b) << 1 is the carries; repeat until no carry is left. O(32) time, O(1) space. */
    static int optimal(int a, int b) {
        while (b != 0) {
            int carry = (a & b) << 1;   // where both bits are 1, a carry moves one position left
            a = a ^ b;                  // add every position, ignoring carries
            b = carry;                  // the carries still have to be added
        }
        return a;
    }

    /** Approach 3: the same identity written recursively, a + b == (a ^ b) + ((a & b) << 1). O(32) time, O(32) stack. */
    static int recursive(int a, int b) {
        if (b == 0) return a;
        return recursive(a ^ b, (a & b) << 1);
    }

    /** Follow-up: a - b == a + (-b), and in two's complement -b == ~b + 1. */
    static int subtract(int a, int b) {
        return optimal(a, optimal(~b, 1));
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int a, int b, int expected) {
        String s = a + " + " + b;
        check(rippleCarry(a, b) == expected, "rippleCarry " + s);
        check(optimal(a, b) == expected, "optimal " + s);
        check(recursive(a, b) == expected, "recursive " + s);
    }

    public static void main(String[] args) {
        verify(1, 2, 3);
        verify(2, 3, 5);
        verify(-1, 1, 0);                                         // carry ripples through all 32 bits
        verify(-5, -7, -12);                                      // both negative
        verify(0, 0, 0);                                          // edge: nothing to add
        verify(-1000, 1000, 0);
        verify(Integer.MAX_VALUE, 1, Integer.MIN_VALUE);          // wraps exactly like Java's +
        verify(Integer.MIN_VALUE, Integer.MIN_VALUE, 0);
        for (int a = -64; a <= 64; a++) {
            for (int b = -64; b <= 64; b++) verify(a, b, a + b); // the tests may use +, the methods may not
        }
        Random rnd = new Random(2411);
        for (int k = 0; k < 2000; k++) {
            int a = rnd.nextInt(), b = rnd.nextInt();
            verify(a, b, a + b);
            check(subtract(a, b) == a - b, "subtract " + a + " - " + b);
        }
        check(subtract(5, 7) == -2 && subtract(0, Integer.MIN_VALUE) == Integer.MIN_VALUE, "subtract edge");
        System.out.println("OK P2411_SumOfTwoIntegers");
    }
}
