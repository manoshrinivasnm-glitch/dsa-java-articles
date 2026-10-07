import java.util.*;

/** TUF 2401 - Understand recursion by print something N times. The output is built into a String so main can assert on it. */
public class P2401_UnderstandRecursionByPrintSomethingNTime {

    /** Baseline: a plain loop. O(n) time, O(1) extra space. */
    static String iterative(String text, int n) {
        StringBuilder out = new StringBuilder();
        for (int i = 1; i <= n; i++) out.append(text).append('\n');
        return out.toString();
    }

    /** Approach 1: parameterised recursion, a counter goes up from 1 to n. O(n) time, O(n) stack. */
    static String recursiveCountUp(String text, int n) {
        StringBuilder out = new StringBuilder();
        countUp(text, 1, n, out);
        return out.toString();
    }

    static void countUp(String text, int i, int n, StringBuilder out) {
        if (i > n) return;                           // base case: nothing left to print
        out.append(text).append('\n');               // the work of this call
        countUp(text, i + 1, n, out);                // the same problem, one step smaller
    }

    /** Approach 2: the counter goes down to 0, so no extra parameter is needed. O(n) time, O(n) stack. */
    static String recursiveCountDown(String text, int n) {
        StringBuilder out = new StringBuilder();
        countDown(text, n, out);
        return out.toString();
    }

    static void countDown(String text, int n, StringBuilder out) {
        if (n <= 0) return;
        out.append(text).append('\n');
        countDown(text, n - 1, out);
    }

    /** Approach 3: each call returns its result instead of writing into a shared buffer. O(n^2) characters copied, O(n) stack. */
    static String recursiveReturn(String text, int n) {
        if (n <= 0) return "";
        return text + "\n" + recursiveReturn(text, n - 1);
    }

    /** Records the exact order in which calls are entered and left, to make the call stack visible. */
    static List<String> traceCalls(int n) {
        List<String> log = new ArrayList<>();
        trace(1, n, log);
        return log;
    }

    static void trace(int i, int n, List<String> log) {
        if (i > n) {
            log.add("base case reached");
            return;
        }
        log.add("enter f(" + i + ")");
        trace(i + 1, n, log);
        log.add("leave f(" + i + ")");
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String text, int n, String expected) {
        check(iterative(text, n).equals(expected), "iterative(" + text + ", " + n + ")");
        check(recursiveCountUp(text, n).equals(expected), "recursiveCountUp(" + text + ", " + n + ")");
        check(recursiveCountDown(text, n).equals(expected), "recursiveCountDown(" + text + ", " + n + ")");
        check(recursiveReturn(text, n).equals(expected), "recursiveReturn(" + text + ", " + n + ")");
    }

    public static void main(String[] args) {
        verify("Hello", 3, "Hello\nHello\nHello\n");
        verify("Striver", 1, "Striver\n");
        verify("x", 0, "");                                    // nothing to print
        verify("x", -2, "");                                   // a negative count must not recurse forever
        verify("ab", 5, "ab\nab\nab\nab\nab\n");
        verify("", 4, "\n\n\n\n");                             // empty text still produces n lines
        verify("deep", 2000, iterative("deep", 2000));          // 2000 frames is well inside the default stack
        check(traceCalls(0).equals(List.of("base case reached")), "trace(0)");
        check(traceCalls(2).equals(List.of("enter f(1)", "enter f(2)", "base case reached", "leave f(2)", "leave f(1)")), "trace(2)");
        check(traceCalls(3).size() == 7, "trace(3) has 2n + 1 events");
        System.out.print(recursiveCountUp("Hello", 3));
        System.out.println("OK P2401_UnderstandRecursionByPrintSomethingNTime");
    }
}
