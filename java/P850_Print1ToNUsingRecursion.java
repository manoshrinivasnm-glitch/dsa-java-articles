import java.util.*;

/** TUF 850 - Print 1 to N using Recursion. Produce 1, 2, ..., N in order without writing a loop. */
public class P850_Print1ToNUsingRecursion {

    /** Approach 1: the plain loop, kept as the reference answer. O(n) time, O(1) extra space. */
    static List<Integer> iterative(int n) {
        List<Integer> out = new ArrayList<>();
        for (int i = 1; i <= n; i++) out.add(i);
        return out;
    }

    /** Approach 2: parameterised recursion. Carry the current value i, emit it, then recurse with i + 1. O(n) time, O(n) stack. */
    static List<Integer> recursiveForward(int n) {
        List<Integer> out = new ArrayList<>();
        forward(1, n, out);
        return out;
    }

    private static void forward(int i, int n, List<Integer> out) {
        if (i > n) return;          // base case: nothing left to print
        out.add(i);                 // do the work ...
        forward(i + 1, n, out);     // ... then recurse
    }

    /** Approach 3: backtracking. Recurse on n - 1 first, then emit n while the calls unwind. O(n) time, O(n) stack. */
    static List<Integer> recursiveBacktrack(int n) {
        List<Integer> out = new ArrayList<>();
        backtrack(n, out);
        return out;
    }

    private static void backtrack(int n, List<Integer> out) {
        if (n < 1) return;          // base case
        backtrack(n - 1, out);      // recurse first ...
        out.add(n);                 // ... print on the way back
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, List<Integer> expected) {
        check(iterative(n).equals(expected), "iterative failed for n = " + n);
        check(recursiveForward(n).equals(expected), "recursiveForward failed for n = " + n);
        check(recursiveBacktrack(n).equals(expected), "recursiveBacktrack failed for n = " + n);
    }

    public static void main(String[] args) {
        verify(0, List.of());                              // edge: nothing to print
        verify(-3, List.of());                             // edge: negative n behaves like 0
        verify(1, List.of(1));
        verify(5, List.of(1, 2, 3, 4, 5));
        verify(10, List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<Integer> big = new ArrayList<>();
        for (int i = 1; i <= 2000; i++) big.add(i);
        verify(2000, big);                                 // deeper recursion still fits the default stack
        System.out.println(recursiveBacktrack(5));         // [1, 2, 3, 4, 5]
        System.out.println("OK P850_Print1ToNUsingRecursion");
    }
}
