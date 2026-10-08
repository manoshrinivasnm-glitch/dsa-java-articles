import java.util.*;

/** TUF 982 - Count and say. Return the n-th term (n >= 1) of the look-and-say sequence that starts with "1". */
public class P982_CountAndSay {

    /** Approach 1: recursion, building each term with String concatenation. Each append copies the whole string. */
    static String bruteForce(int n) {
        if (n == 1) return "1";
        String prev = bruteForce(n - 1);
        String result = "";
        int i = 0;
        while (i < prev.length()) {
            int j = i;
            while (j < prev.length() && prev.charAt(j) == prev.charAt(i)) j++;
            result = result + (j - i) + prev.charAt(i);   // count of the run, then its digit
            i = j;
        }
        return result;
    }

    /** Approach 2: iterate n - 1 times, run-length encoding into a StringBuilder. O(total length) time. */
    static String optimal(int n) {
        String cur = "1";
        for (int step = 2; step <= n; step++) {
            StringBuilder next = new StringBuilder();
            int count = 1;
            for (int i = 1; i <= cur.length(); i++) {
                if (i < cur.length() && cur.charAt(i) == cur.charAt(i - 1)) {
                    count++;                               // the run continues
                } else {
                    next.append(count).append(cur.charAt(i - 1));   // the run ended at i - 1
                    count = 1;
                }
            }
            cur = next.toString();
        }
        return cur;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(bruteForce(n).equals(expected), "bruteForce n=" + n);
        check(optimal(n).equals(expected), "optimal n=" + n);
    }

    public static void main(String[] args) {
        verify(1, "1");                                  // edge case: the seed itself
        verify(2, "11");
        verify(3, "21");
        verify(4, "1211");
        verify(5, "111221");
        verify(6, "312211");
        verify(7, "13112221");
        verify(8, "1113213211");

        String a = bruteForce(30), b = optimal(30);
        check(a.equals(b), "n=30 mismatch");
        check(b.length() == 4462, "length of term 30");
        check(b.chars().allMatch(c -> c >= '1' && c <= '3'), "only digits 1..3 ever appear");
        System.out.println("OK P982_CountAndSay");
    }
}
