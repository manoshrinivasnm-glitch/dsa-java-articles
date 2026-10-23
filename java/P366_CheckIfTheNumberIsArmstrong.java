import java.util.*;

/** TUF 366 - Check if the Number is Armstrong. n is an Armstrong number when the sum of its digits, each raised to the number of digits, equals n. */
public class P366_CheckIfTheNumberIsArmstrong {

    /** Approach 1: walk the characters of the decimal string and raise each digit with Math.pow. O(d) time, O(d) space. */
    static boolean bruteForce(int n) {
        if (n < 0) return false;                 // negative numbers are never Armstrong numbers
        String s = Integer.toString(n);
        int k = s.length();                      // k digits -> raise each digit to the power k
        long sum = 0;                            // sum of digit^k, long to avoid overflow
        for (char c : s.toCharArray()) {         // each digit character
            sum += (long) Math.pow(c - '0', k);  // c - '0' is the digit's value
        }
        return sum == n;                         // Armstrong if the sum gives back n
    }

    /** Approach 2: pure integer arithmetic, digits by % 10 and an integer power loop, with an early exit. O(d^2) time, O(1) space. */
    static boolean optimal(int n) {
        if (n < 0) return false;        // negative numbers are never Armstrong numbers
        int k = countDigits(n);         // k digits -> raise each digit to the power k
        long sum = 0;                   // sum of digit^k, long to avoid overflow
        int x = n;                      // work on a copy, n is needed for the final check
        while (x > 0) {                 // peel digits off the right end
            sum += power(x % 10, k);    // add (last digit)^k
            if (sum > n) return false;  // the sum only grows, so n can no longer be reached
            x /= 10;                    // drop that digit
        }
        return sum == n;                // Armstrong if the sum gives back n
    }

    static int countDigits(int n) {
        int count = 0;
        do {          // do-while, so 0 counts as one digit
            count++;
            n /= 10;  // drop the last digit
        } while (n > 0);
        return count;
    }

    static long power(int base, int exp) {
        long result = 1;
        for (int i = 0; i < exp; i++) result *= base;  // exact integer power, unlike Math.pow
        return result;
    }

    /** Every Armstrong number in [0, limit], in increasing order. */
    static List<Integer> allUpTo(int limit) {
        List<Integer> out = new ArrayList<>();
        for (int n = 0; n <= limit; n++) {  // test every number from 0 to limit
            if (optimal(n)) out.add(n);     // keep the Armstrong numbers
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
