import java.util.*;

/** TUF 366 - Check if the Number is Armstrong. n is an Armstrong number when the sum of its digits, each raised to the number of digits, equals n. */
public class P366_CheckIfTheNumberIsArmstrong {

    /** Approach 1: walk the characters of the decimal string and raise each digit with Math.pow. O(d) time, O(d) space. */
    static boolean bruteForce(int n) {
        if (n < 0) return false;
        String s = Integer.toString(n);
        int k = s.length();
        long sum = 0;
        for (char c : s.toCharArray()) {
            sum += (long) Math.pow(c - '0', k);
        }
        return sum == n;
    }

    /** Approach 2: pure integer arithmetic, digits by % 10 and an integer power loop, with an early exit. O(d^2) time, O(1) space. */
    static boolean optimal(int n) {
        if (n < 0) return false;
        int k = countDigits(n);
        long sum = 0;
        int x = n;
        while (x > 0) {
            sum += power(x % 10, k);
            if (sum > n) return false;           // the sum only grows, so n can no longer be reached
            x /= 10;
        }
        return sum == n;
    }

    static int countDigits(int n) {
        int count = 0;
        do {
            count++;
            n /= 10;
        } while (n > 0);
        return count;
    }

    static long power(int base, int exp) {
        long result = 1;
        for (int i = 0; i < exp; i++) result *= base;
        return result;
    }

    /** Every Armstrong number in [0, limit], in increasing order. */
    static List<Integer> allUpTo(int limit) {
        List<Integer> out = new ArrayList<>();
        for (int n = 0; n <= limit; n++) {
            if (optimal(n)) out.add(n);
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, boolean expected) {
        check(bruteForce(n) == expected, "bruteForce(" + n + ") expected " + expected);
        check(optimal(n) == expected, "optimal(" + n + ") expected " + expected);
    }

    public static void main(String[] args) {
        verify(153, true);                       // 1^3 + 5^3 + 3^3
        verify(370, true);
        verify(371, true);
        verify(407, true);
        verify(1634, true);                      // 1^4 + 6^4 + 3^4 + 4^4
        verify(9474, true);
        verify(9475, false);
        verify(0, true);                         // 0^1 = 0
        verify(5, true);                         // every single digit is Armstrong
        verify(10, false);                       // 1^2 + 0^2 = 1
        verify(100, false);
        verify(-153, false);                     // negatives are excluded by definition
        verify(548_834, true);
        verify(548_835, false);
        verify(1_741_725, true);
        verify(9_926_315, true);
        verify(24_678_051, true);
        verify(912_985_153, true);               // the largest Armstrong number that fits in int
        verify(Integer.MAX_VALUE, false);        // digit powers sum to 1702364300, no int overflow thanks to long
        for (int n = 0; n <= 100_000; n++) {
            check(bruteForce(n) == optimal(n), "approaches disagree at " + n);
        }
        List<Integer> expected = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 153, 370, 371, 407, 1634, 8208, 9474, 54748, 92727, 93084);
        check(allUpTo(100_000).equals(expected), "allUpTo(100000) = " + allUpTo(100_000));
        System.out.println("OK P366_CheckIfTheNumberIsArmstrong");
    }
}
