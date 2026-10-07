import java.util.*;

/** TUF 903 - Roman to Integer. Convert a Roman numeral in the range 1..3999 to its integer value. */
public class P903_RomanToInteger {

    /** Approach 1: table of all 13 tokens (7 symbols + 6 subtractive pairs); at each position try the 2-letter token first. O(n) time, O(1) space. */
    static int bruteForce(String s) {
        Map<String, Integer> table = new HashMap<>();
        table.put("I", 1);    table.put("IV", 4);    table.put("V", 5);    table.put("IX", 9);
        table.put("X", 10);   table.put("XL", 40);   table.put("L", 50);   table.put("XC", 90);
        table.put("C", 100);  table.put("CD", 400);  table.put("D", 500);  table.put("CM", 900);
        table.put("M", 1000);
        int total = 0, i = 0, n = s.length();
        while (i < n) {
            if (i + 1 < n && table.containsKey(s.substring(i, i + 2))) {
                total += table.get(s.substring(i, i + 2));
                i += 2;
            } else {
                total += table.get(s.substring(i, i + 1));
                i += 1;
            }
        }
        return total;
    }

    /** Value of a single Roman symbol. */
    static int value(char c) {
        return switch (c) {
            case 'I' -> 1;
            case 'V' -> 5;
            case 'X' -> 10;
            case 'L' -> 50;
            case 'C' -> 100;
            case 'D' -> 500;
            case 'M' -> 1000;
            default -> throw new IllegalArgumentException("not a Roman symbol: " + c);
        };
    }

    /** Approach 2: left to right; a symbol smaller than its right neighbour is subtracted, every other symbol is added. O(n) time, O(1) space. */
    static int optimal(String s) {
        int total = 0, n = s.length();
        for (int i = 0; i < n; i++) {
            int cur = value(s.charAt(i));
            if (i + 1 < n && cur < value(s.charAt(i + 1))) total -= cur;
            else total += cur;
        }
        return total;
    }

    /** Approach 3: right to left; a symbol smaller than the one just processed (its right neighbour) is subtracted. O(n) time, O(1) space. */
    static int optimalRightToLeft(String s) {
        int total = 0, prev = 0;
        for (int i = s.length() - 1; i >= 0; i--) {
            int cur = value(s.charAt(i));
            if (cur < prev) total -= cur;
            else total += cur;
            prev = cur;
        }
        return total;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String s, int expected) {
        check(bruteForce(s) == expected, "bruteForce(" + s + ") != " + expected);
        check(optimal(s) == expected, "optimal(" + s + ") != " + expected);
        check(optimalRightToLeft(s) == expected, "optimalRightToLeft(" + s + ") != " + expected);
    }

    public static void main(String[] args) {
        verify("III", 3);
        verify("LVIII", 58);
        verify("MCMXCIV", 1994);
        verify("IV", 4);
        verify("IX", 9);
        verify("XL", 40);
        verify("XC", 90);
        verify("CD", 400);
        verify("CM", 900);
        verify("I", 1);                                    // smallest numeral
        verify("MMMCMXCIX", 3999);                         // largest numeral
        verify("CDXLIV", 444);                             // three subtractive pairs in a row
        verify("MMXXIV", 2024);
        verify("DCCCXC", 890);
        verify("MDCLXVI", 1666);                           // every symbol once, strictly decreasing
        System.out.println("OK P903_RomanToInteger");
    }
}
