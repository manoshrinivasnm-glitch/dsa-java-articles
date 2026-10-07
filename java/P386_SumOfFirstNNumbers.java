import java.util.*;

/** TUF 386 - Sum of First N Numbers. Return 1 + 2 + ... + N (0 when N < 1). */
public class P386_SumOfFirstNNumbers {

    /** Approach 1: add the numbers one by one in a loop. O(n) time, O(1) space. */
    static long iterative(int n) {
        long sum = 0;
        for (int i = 1; i <= n; i++) sum += i;
        return sum;
    }

    /** Approach 2: parameterised recursion. Carry the running total down the calls; the base case returns it. O(n) time, O(n) stack. */
    static long parameterised(int n) {
        return sumDown(n, 0);
    }

    private static long sumDown(int i, long runningSum) {
        if (i < 1) return runningSum;           // base case: nothing left to add
        return sumDown(i - 1, runningSum + i);  // add i now, let the smaller problem finish the job
    }

    /** Approach 3: functional recursion. Trust that the call for n - 1 returns the right answer and add n to it. O(n) time, O(n) stack. */
    static long functional(int n) {
        if (n < 1) return 0;                    // base case: the empty sum is 0
        return n + functional(n - 1);
    }

    /** Approach 4: closed form n(n+1)/2, computed in long so the product cannot overflow. O(1) time and space. */
    static long formula(int n) {
        if (n < 1) return 0;
        return (long) n * (n + 1L) / 2;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, long expected) {
        check(iterative(n) == expected, "iterative failed for n = " + n);
        check(parameterised(n) == expected, "parameterised failed for n = " + n);
        check(functional(n) == expected, "functional failed for n = " + n);
        check(formula(n) == expected, "formula failed for n = " + n);
    }

    public static void main(String[] args) {
        verify(0, 0);                       // edge: empty sum
        verify(-7, 0);                      // edge: negative n behaves like 0
        verify(1, 1);
        verify(3, 6);
        verify(5, 15);
        verify(10, 55);
        verify(100, 5050);
        verify(5000, 12_502_500L);          // recursion 5000 deep is still fine
        // Larger n: the recursive versions would overflow the call stack, so only loop and formula are compared.
        check(iterative(1_000_000) == 500_000_500_000L, "iterative 1e6");
        check(formula(1_000_000) == 500_000_500_000L, "formula 1e6");
        check(formula(Integer.MAX_VALUE) == 2_305_843_008_139_952_128L, "formula must not overflow at Integer.MAX_VALUE");
        System.out.println("OK P386_SumOfFirstNNumbers");
    }
}
