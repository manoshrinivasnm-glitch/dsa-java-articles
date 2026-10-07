import java.util.*;

/** TUF 372 - GCD of Two Numbers. Return the largest integer that divides both a and b (a, b >= 0); gcd(0, b) = b and gcd(0, 0) = 0. */
public class P372_GCDOfTwoNumbers {

    /** Approach 1: try every candidate from min(a, b) down to 1; the first one that divides both is the answer. O(min(a, b)) time, O(1) space. */
    static int bruteForce(int a, int b) {
        if (a == 0) return b;
        if (b == 0) return a;
        for (int g = Math.min(a, b); g >= 1; g--) {
            if (a % g == 0 && b % g == 0) return g;
        }
        return 1;
    }

    /** Approach 2: Euclid by subtraction, gcd(a, b) = gcd(a - b, b) when a > b. O(max(a, b) / min(a, b)) steps per round, O(max(a, b)) worst case, O(1) space. */
    static int better(int a, int b) {
        if (a == 0) return b;
        if (b == 0) return a;
        while (a != b) {
            if (a > b) a -= b; else b -= a;
        }
        return a;
    }

    /** Approach 3: Euclid with modulo, gcd(a, b) = gcd(b, a % b). O(log(min(a, b))) time, O(1) space. */
    static int optimal(int a, int b) {
        while (b != 0) {
            int r = a % b;
            a = b;
            b = r;
        }
        return a;
    }

    /** The same algorithm written recursively; the recursion depth is O(log(min(a, b))). */
    static int optimalRecursive(int a, int b) {
        return b == 0 ? a : optimalRecursive(b, a % b);
    }

    /** lcm(a, b) = a / gcd(a, b) * b; dividing before multiplying keeps the intermediate value small. */
    static long lcm(int a, int b) {
        if (a == 0 || b == 0) return 0;
        return (long) (a / optimal(a, b)) * b;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int a, int b, int expected) {
        check(bruteForce(a, b) == expected, "bruteForce(" + a + ", " + b + ") expected " + expected);
        check(better(a, b) == expected, "better(" + a + ", " + b + ") expected " + expected);
        check(optimal(a, b) == expected, "optimal(" + a + ", " + b + ") expected " + expected);
        check(optimalRecursive(a, b) == expected, "optimalRecursive(" + a + ", " + b + ") expected " + expected);
    }

    public static void main(String[] args) {
        verify(20, 15, 5);
        verify(15, 20, 5);                       // order does not matter
        verify(7, 3, 1);                         // coprime
        verify(12, 12, 12);                      // equal numbers
        verify(100, 10, 10);                     // one divides the other
        verify(36, 60, 12);
        verify(0, 9, 9);                         // everything divides 0, so gcd(0, b) = b
        verify(9, 0, 9);
        verify(0, 0, 0);
        verify(1, 1_000_000_000, 1);
        verify(1_000_000_007, 1_000_000, 1);     // 10^9 + 7 is prime
        verify(2_147_483_646, 1_073_741_823, 1_073_741_823); // near the int limit, no overflow anywhere
        check(lcm(4, 6) == 12, "lcm(4, 6)");
        check(lcm(7, 3) == 21, "lcm(7, 3)");
        check(lcm(0, 5) == 0, "lcm(0, 5)");
        check(lcm(100_000, 99_999) == 9_999_900_000L, "lcm needs a long");
        System.out.println("OK P372_GCDOfTwoNumbers");
    }
}
