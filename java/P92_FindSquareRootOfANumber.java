import java.util.*;

/** TUF 92 - Find square root of a number. Return floor(sqrt(n)) for a non-negative integer n. */
public class P92_FindSquareRootOfANumber {

    /** Approach 1: walk i = 1, 2, 3, ... while i*i <= n and remember the last such i. O(sqrt n) time, O(1) space. */
    static int bruteForce(int n) {
        int ans = 0;
        for (long i = 1; i * i <= n; i++) ans = (int) i;
        return ans;
    }

    /** Approach 2: binary search on the answer in [1, n]; the predicate mid*mid <= n is monotonic. O(log n) time, O(1) space. */
    static int optimal(int n) {
        int lo = 1, hi = n, ans = 0;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if ((long) mid * mid <= n) { ans = mid; lo = mid + 1; }
            else hi = mid - 1;
        }
        return ans;
    }

    /** Approach 3: floating-point sqrt followed by an integer fix-up so rounding can never make it wrong. O(1) time. */
    static int builtin(int n) {
        int r = (int) Math.sqrt(n);
        while ((long) r * r > n) r--;
        while ((long) (r + 1) * (r + 1) <= n) r++;
        return r;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int expected) {
        check(bruteForce(n) == expected, "bruteForce " + n);
        check(optimal(n) == expected, "optimal " + n);
        check(builtin(n) == expected, "builtin " + n);
    }

    public static void main(String[] args) {
        verify(0, 0);                        // edge: sqrt(0) = 0
        verify(1, 1);
        verify(2, 1);
        verify(8, 2);
        verify(28, 5);
        verify(36, 6);                       // perfect square
        verify(99, 9);
        verify(100, 10);
        verify(2_147_395_599, 46339);        // 46340^2 = 2147395600 is one too many
        verify(2_147_395_600, 46340);
        verify(Integer.MAX_VALUE, 46340);    // largest int input; mid*mid must be computed in long
        System.out.println("OK P92_FindSquareRootOfANumber");
    }
}
