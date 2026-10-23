import java.util.*;

/** TUF 982 - Count and say. Return the n-th term (n >= 1) of the look-and-say sequence that starts with "1". */
public class P982_CountAndSay {

    /** Approach 1: recursion, building each term with String concatenation. Each append copies the whole string. */
    static String bruteForce(int n) {
        if (n == 1) return "1";                          // base case: the first term
        String prev = bruteForce(n - 1);                 // the term we have to describe
        String result = "";
        int i = 0;                                       // start of the current run of equal digits
        while (i < prev.length()) {
            int j = i;
            while (j < prev.length() && prev.charAt(j) == prev.charAt(i)) j++;   // j stops just past the run
            result = result + (j - i) + prev.charAt(i);   // count of the run, then its digit
            i = j;                                       // the next run starts where this one ended
        }
        return result;
    }

    /** Approach 2: iterate n - 1 times, run-length encoding into a StringBuilder. O(total length) time. */
    static String optimal(int n) {
        String cur = "1";                                  // term 1
        for (int step = 2; step <= n; step++) {            // build terms 2, 3, ..., n in order
            StringBuilder next = new StringBuilder();      // the description of cur
            int count = 1;                                 // length of the run ending at i - 1
            // i == length is a sentinel step that flushes the last run
            for (int i = 1; i <= cur.length(); i++) {
                if (i < cur.length() && cur.charAt(i) == cur.charAt(i - 1)) {
                    count++;                               // the run continues
                } else {
                    next.append(count).append(cur.charAt(i - 1));   // the run ended at i - 1
                    count = 1;                             // a new run starts at i
                }
            }
            cur = next.toString();                         // move on to the next term
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
