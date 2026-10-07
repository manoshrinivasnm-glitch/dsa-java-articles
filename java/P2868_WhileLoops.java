import java.util.*;

/** TUF 2868 - While loops. Loops whose trip count is unknown in advance: digit extraction, Euclid's gcd,
 *  base conversion, do-while, while (true) with an inner exit, and two indices moving at different speeds. */
public class P2868_WhileLoops {

    /** Peel off the last digit each pass; the loop ends when nothing is left. */
    static long reverseNumber(long n) {
        long rev = 0;
        while (n != 0) {
            rev = rev * 10 + n % 10;
            n /= 10;
        }
        return rev;
    }

    /** do-while runs the body at least once, which is exactly what 0 (one digit) needs. */
    static int countDigits(int n) {
        long m = Math.abs((long) n);
        int count = 0;
        do {
            count++;
            m /= 10;
        } while (m > 0);
        return count;
    }

    /** Euclid's algorithm: replace (a, b) by (b, a mod b) until b is 0. */
    static int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    /** Repeated halving gives the bits from least to most significant, so each new bit goes in front. */
    static String toBinary(int n) {
        if (n == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            sb.insert(0, n % 2);
            n /= 2;
        }
        return sb.toString();
    }

    /** Nobody knows how many steps this takes in advance; a while loop is the only natural fit. */
    static int collatzSteps(long n) {
        int steps = 0;
        while (n != 1) {
            n = (n % 2 == 0) ? n / 2 : 3 * n + 1;
            steps++;
        }
        return steps;
    }

    /** Largest r with r * r <= n; the product is computed in long so it cannot overflow near 2^31. */
    static int floorSqrt(int n) {
        int r = 0;
        while ((long) (r + 1) * (r + 1) <= n) r++;
        return r;
    }

    /** while (true) with the exit in the middle, for loops whose stop test sits after some work. */
    static int firstPowerOfTwoAbove(int n) {
        int p = 1;
        while (true) {
            if (p > n) return p;
            p *= 2;
        }
    }

    /** Two indices advancing at different speeds: take the smaller head until one list runs out. */
    static int[] mergeSorted(int[] a, int[] b) {
        int[] out = new int[a.length + b.length];
        int i = 0, j = 0, k = 0;
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) out[k++] = a[i++];
            else out[k++] = b[j++];
        }
        while (i < a.length) out[k++] = a[i++];
        while (j < b.length) out[k++] = b[j++];
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        check(reverseNumber(123) == 321, "123");
        check(reverseNumber(120) == 21, "trailing zero disappears");
        check(reverseNumber(0) == 0, "zero");
        check(reverseNumber(-45) == -54, "negative numbers work because % keeps the sign");
        check(reverseNumber(1534236469L) == 9646324351L, "the result does not fit in int, long does");

        check(countDigits(0) == 1, "0 has one digit");
        check(countDigits(7) == 1 && countDigits(10) == 2, "small numbers");
        check(countDigits(-12345) == 5, "the sign is not a digit");
        check(countDigits(Integer.MIN_VALUE) == 10, "abs of MIN_VALUE needs long");

        check(gcd(12, 18) == 6, "12 18");
        check(gcd(7, 5) == 1, "coprime");
        check(gcd(0, 9) == 9 && gcd(9, 0) == 9, "zero on either side");
        check(gcd(20, 100) == 20, "one divides the other");

        check(toBinary(0).equals("0"), "binary of 0");
        check(toBinary(1).equals("1"), "binary of 1");
        check(toBinary(10).equals("1010"), "binary of 10");
        check(toBinary(255).equals("11111111"), "binary of 255");
        check(toBinary(1024).equals(Integer.toBinaryString(1024)), "matches the library");

        check(collatzSteps(1) == 0, "already 1");
        check(collatzSteps(2) == 1, "one halving");
        check(collatzSteps(6) == 8, "6 takes 8 steps");
        check(collatzSteps(27) == 111, "27 takes 111 steps");

        check(floorSqrt(0) == 0 && floorSqrt(1) == 1, "0 and 1");
        check(floorSqrt(15) == 3 && floorSqrt(16) == 4, "around a perfect square");
        check(floorSqrt(99) == 9, "99");
        check(floorSqrt(Integer.MAX_VALUE) == 46340, "largest int: an int product would overflow here");

        check(firstPowerOfTwoAbove(0) == 1, "above 0");
        check(firstPowerOfTwoAbove(1) == 2, "above 1");
        check(firstPowerOfTwoAbove(5) == 8, "above 5");
        check(firstPowerOfTwoAbove(8) == 16, "strictly above 8");
        check(firstPowerOfTwoAbove(1000) == 1024, "above 1000");

        check(Arrays.equals(mergeSorted(new int[]{1, 3, 5}, new int[]{2, 4, 6}), new int[]{1, 2, 3, 4, 5, 6}), "interleaved");
        check(Arrays.equals(mergeSorted(new int[0], new int[]{1, 2}), new int[]{1, 2}), "empty left");
        check(Arrays.equals(mergeSorted(new int[]{1, 2}, new int[0]), new int[]{1, 2}), "empty right");
        check(Arrays.equals(mergeSorted(new int[]{1, 1}, new int[]{1}), new int[]{1, 1, 1}), "duplicates");
        check(Arrays.equals(mergeSorted(new int[]{5, 6}, new int[]{1, 2}), new int[]{1, 2, 5, 6}), "one side is exhausted first");

        System.out.println("OK P2868_WhileLoops");
    }
}
