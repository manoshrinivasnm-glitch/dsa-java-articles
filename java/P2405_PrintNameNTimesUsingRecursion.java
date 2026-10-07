import java.util.*;

/** TUF 2405 - Print name N times using recursion. The output is built into a String (one name per line) so main can assert on it. */
public class P2405_PrintNameNTimesUsingRecursion {

    /** Baseline loop, for comparison. O(n) time, O(1) extra space. */
    static String iterative(String name, int n) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < n; i++) out.append(name).append('\n');
        return out.toString();
    }

    /** Approach 1: the standard recursive form, f(i, n) prints once and calls f(i + 1, n). O(n) time, O(n) stack. */
    static String recursive(String name, int n) {
        StringBuilder out = new StringBuilder();
        printName(name, 1, n, out);
        return out.toString();
    }

    static void printName(String name, int i, int n, StringBuilder out) {
        if (i > n) return;                           // base case
        out.append(name).append('\n');               // print once
        printName(name, i + 1, n, out);              // let the next call handle the remaining n - i lines
    }

    /** Approach 2: no shared buffer; each call returns its own line followed by whatever the rest returns. O(n^2) characters copied, O(n) stack. */
    static String recursiveReturn(String name, int n) {
        if (n <= 0) return "";
        return name + "\n" + recursiveReturn(name, n - 1);
    }

    /** Approach 3: divide and conquer. n copies = two blocks of n / 2 copies, plus one more line when n is odd. O(n) characters, O(log n) stack. */
    static String recursiveHalving(String name, int n) {
        if (n <= 0) return "";
        if (n == 1) return name + "\n";
        String half = recursiveHalving(name, n / 2);
        return (n % 2 == 0) ? half + half : half + half + name + "\n";
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String name, int n, String expected) {
        check(iterative(name, n).equals(expected), "iterative(" + name + ", " + n + ")");
        check(recursive(name, n).equals(expected), "recursive(" + name + ", " + n + ")");
        check(recursiveReturn(name, n).equals(expected), "recursiveReturn(" + name + ", " + n + ")");
        check(recursiveHalving(name, n).equals(expected), "recursiveHalving(" + name + ", " + n + ")");
    }

    public static void main(String[] args) {
        verify("Striver", 3, "Striver\nStriver\nStriver\n");
        verify("Raj", 1, "Raj\n");
        verify("Alice", 0, "");                                // zero lines
        verify("Bob", -4, "");                                 // negative count: nothing, and no infinite recursion
        verify("Mano", 7, "Mano\nMano\nMano\nMano\nMano\nMano\nMano\n");   // odd n exercises the "+ one more" branch
        verify("x", 8, "x\nx\nx\nx\nx\nx\nx\nx\n");             // power of two: only the even branch
        verify("deep", 1500, iterative("deep", 1500));          // linear recursion depth that every JVM handles
        // 200000 frames would overflow the default stack for the linear versions, but halving only needs about 18.
        String big = recursiveHalving("name", 200_000);
        check(big.equals(iterative("name", 200_000)) && big.length() == 5 * 200_000, "halving on a large n");
        System.out.print(recursive("Striver", 3));
        System.out.println("OK P2405_PrintNameNTimesUsingRecursion");
    }
}
