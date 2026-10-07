import java.util.*;

/** TUF 852 - Print N to 1 using Recursion. Produce N, N-1, ..., 1 in order without writing a loop. */
public class P852_PrintNTo1UsingRecursion {

    /** Approach 1: the plain loop, kept as the reference answer. O(n) time, O(1) extra space. */
    static List<Integer> iterative(int n) {
        List<Integer> out = new ArrayList<>();
        for (int i = n; i >= 1; i--) out.add(i);
        return out;
    }

    /** Approach 2: parameterised recursion. Start at i = n, emit i, then recurse with i - 1. O(n) time, O(n) stack. */
    static List<Integer> recursiveForward(int n) {
        List<Integer> out = new ArrayList<>();
        forward(n, out);
        return out;
    }

    private static void forward(int i, List<Integer> out) {
        if (i < 1) return;          // base case: we have gone below 1
        out.add(i);                 // do the work ...
        forward(i - 1, out);        // ... then recurse towards the base case
    }

    /** Approach 3: backtracking. Start at i = 1, recurse on i + 1 first, then emit i while unwinding. O(n) time, O(n) stack. */
    static List<Integer> recursiveBacktrack(int n) {
        List<Integer> out = new ArrayList<>();
        backtrack(1, n, out);
        return out;
    }

    private static void backtrack(int i, int n, List<Integer> out) {
        if (i > n) return;          // base case
        backtrack(i + 1, n, out);   // recurse first ...
        out.add(i);                 // ... print on the way back, so n comes out first
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
        verify(-2, List.of());                             // edge: negative n behaves like 0
        verify(1, List.of(1));
        verify(5, List.of(5, 4, 3, 2, 1));
        verify(10, List.of(10, 9, 8, 7, 6, 5, 4, 3, 2, 1));
        List<Integer> big = new ArrayList<>();
        for (int i = 2000; i >= 1; i--) big.add(i);
        verify(2000, big);                                 // deeper recursion still fits the default stack
        System.out.println(recursiveBacktrack(5));         // [5, 4, 3, 2, 1]
        System.out.println("OK P852_PrintNTo1UsingRecursion");
    }
}
