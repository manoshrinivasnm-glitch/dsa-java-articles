import java.util.*;

/** TUF 1211 - Cpp Basics, done in Java: primitive types and their ranges, overflow, integer division,
 *  casts, char arithmetic, floating point comparison and boxed Integers. */
public class P1211_CppBasics {

    /** int arithmetic silently wraps around: MAX_VALUE + 1 becomes MIN_VALUE. */
    static int addInt(int a, int b) {
        return a + b;
    }

    /** Widen one operand to long before adding and the true sum survives. */
    static long addLong(int a, int b) {
        return (long) a + b;
    }

    /** Integer division truncates toward zero, and % takes the sign of the dividend (the left operand). */
    static int[] divMod(int a, int b) {
        return new int[]{a / b, a % b};
    }

    /** Floor division always rounds down, and floorMod is never negative for a positive divisor. */
    static int[] floorDivMod(int a, int b) {
        return new int[]{Math.floorDiv(a, b), Math.floorMod(a, b)};
    }

    /** (a + b) / 2 would be an int result; one double operand forces real division. */
    static double average(int a, int b) {
        return (a + b) / 2.0;
    }

    /** Casting a double to int drops the fraction; Math.round goes to the nearest whole number. */
    static long[] truncateAndRound(double x) {
        return new long[]{(int) x, Math.round(x)};
    }

    /** Characters are small integers: 'a' + 1 is 'b' once cast back to char. */
    static char shiftLowercase(char c, int k) {
        return (char) ('a' + (c - 'a' + k) % 26);
    }

    /** '7' - '0' is 7; the raw code of '7' is 55, so never use the char itself as a number. */
    static int digitValue(char c) {
        return c - '0';
    }

    /** 13! does not fit in an int, so this version returns a wrapped, wrong value from 13 onwards. */
    static int factorialInt(int n) {
        int f = 1;
        for (int i = 2; i <= n; i++) f *= i;
        return f;
    }

    /** The same loop in long is exact up to 20!. */
    static long factorialLong(int n) {
        long f = 1;
        for (int i = 2; i <= n; i++) f *= i;
        return f;
    }

    /** Doubles are binary fractions; compare with a tolerance, never with ==. */
    static boolean nearlyEqual(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    /** Boxed Integers from -128 to 127 are cached, so == happens to work there and fails elsewhere; use equals. */
    static boolean[] boxedComparison(int v) {
        Integer a = v, b = v;
        return new boolean[]{a == b, a.equals(b)};
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        // ranges of the primitive types
        check(Byte.MIN_VALUE == -128 && Byte.MAX_VALUE == 127, "byte range");
        check(Short.MAX_VALUE == 32767, "short range");
        check(Integer.MAX_VALUE == 2147483647 && Integer.MIN_VALUE == -2147483648, "int range");
        check(Long.MAX_VALUE == 9223372036854775807L, "long range");
        check(Character.MAX_VALUE == 65535, "char is an unsigned 16-bit number");

        check(addInt(2, 3) == 5 && addLong(2, 3) == 5, "small sum");
        check(addInt(-5, 5) == 0 && addLong(-5, 5) == 0, "sum to zero");
        check(addInt(Integer.MAX_VALUE, 1) == Integer.MIN_VALUE, "int wraps around");
        check(addLong(Integer.MAX_VALUE, 1) == 2147483648L, "long keeps the true sum");
        check(addInt(Integer.MIN_VALUE, -1) == Integer.MAX_VALUE, "wrap in the other direction");
        check(addLong(Integer.MIN_VALUE, -1) == -2147483649L, "long keeps the true negative sum");

        check(Arrays.equals(divMod(7, 2), new int[]{3, 1}), "7 / 2");
        check(Arrays.equals(divMod(-7, 2), new int[]{-3, -1}), "-7 / 2 truncates toward zero");
        check(Arrays.equals(divMod(7, -2), new int[]{-3, 1}), "7 / -2");
        check(Arrays.equals(divMod(-7, -2), new int[]{3, -1}), "-7 / -2");
        check(Arrays.equals(floorDivMod(7, 2), new int[]{3, 1}), "floor 7 / 2");
        check(Arrays.equals(floorDivMod(-7, 2), new int[]{-4, 1}), "floor -7 / 2 rounds down");
        check(Arrays.equals(floorDivMod(7, -2), new int[]{-4, -1}), "floor 7 / -2");
        check(Arrays.equals(floorDivMod(-7, -2), new int[]{3, -1}), "floor -7 / -2");

        check(average(3, 4) == 3.5, "average needs real division");
        check(average(2, 2) == 2.0, "average of equal values");
        check(average(-3, 4) == 0.5, "average with a negative");
        check(average(0, 0) == 0.0, "average of zeros");

        check(Arrays.equals(truncateAndRound(3.99), new long[]{3, 4}), "3.99");
        check(Arrays.equals(truncateAndRound(-3.99), new long[]{-3, -4}), "-3.99 truncates toward zero");
        check(Arrays.equals(truncateAndRound(2.5), new long[]{2, 3}), "2.5 rounds up");
        check(Arrays.equals(truncateAndRound(-2.5), new long[]{-2, -2}), "-2.5 rounds toward positive infinity");
        check(Arrays.equals(truncateAndRound(0.0), new long[]{0, 0}), "zero");

        check(shiftLowercase('a', 1) == 'b', "a -> b");
        check(shiftLowercase('z', 1) == 'a', "z wraps to a");
        check(shiftLowercase('x', 3) == 'a', "x + 3 wraps");
        check(shiftLowercase('a', 0) == 'a', "shift by zero");
        check(shiftLowercase('c', 26) == 'c', "full cycle");

        check(digitValue('0') == 0 && digitValue('7') == 7 && digitValue('9') == 9, "digit values");
        check(digitValue('7') == Character.getNumericValue('7'), "same as getNumericValue");
        check((int) '7' == 55, "the raw code of '7' is 55, not 7");

        check(factorialInt(0) == 1 && factorialLong(0) == 1, "0!");
        check(factorialInt(5) == 120 && factorialLong(5) == 120, "5!");
        check(factorialInt(12) == 479001600 && factorialLong(12) == 479001600L, "12! is the last to fit in int");
        check(factorialInt(13) == 1932053504, "13! overflowed int and is wrong");
        check(factorialLong(13) == 6227020800L, "13! in long is right");
        check(factorialLong(20) == 2432902008176640000L, "20! is the last to fit in long");

        check(0.1 + 0.2 != 0.3, "binary doubles cannot represent 0.1 exactly");
        check(nearlyEqual(0.1 + 0.2, 0.3), "tolerance comparison");
        check(nearlyEqual(1.0, 1.0), "equal doubles");
        check(!nearlyEqual(1.0, 1.1), "different doubles");
        check(nearlyEqual(1e-12, 0.0), "tiny difference counts as equal");

        check(Arrays.equals(boxedComparison(127), new boolean[]{true, true}), "127 is cached");
        check(Arrays.equals(boxedComparison(128), new boolean[]{false, true}), "128 is not cached");
        check(Arrays.equals(boxedComparison(-128), new boolean[]{true, true}), "-128 is cached");
        check(Arrays.equals(boxedComparison(1000), new boolean[]{false, true}), "1000 is not cached");

        // explicit casts
        check((byte) 200 == -56, "narrowing keeps the low 8 bits");
        check((int) 3_000_000_000L == -1294967296, "narrowing long to int");
        check((int) 'A' == 65 && (char) 66 == 'B', "char and int convert both ways");
        check((long) Integer.MAX_VALUE + 1 == 2147483648L, "widening before the add");

        System.out.println("OK P1211_CppBasics");
    }
}
