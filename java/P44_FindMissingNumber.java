import java.util.*;

/** TUF 44 - Find missing number. nums holds n distinct values from 0..n; return the single value that is absent. */
public class P44_FindMissingNumber {

    /** Approach 1: for every candidate 0..n, linearly search the array for it. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length;
        for (int candidate = 0; candidate <= n; candidate++) {
            boolean present = false;
            for (int x : nums) {
                if (x == candidate) { present = true; break; }
            }
            if (!present) return candidate;
        }
        return -1;                                   // unreachable for valid input
    }

    /** Approach 2: mark every value seen in a boolean array, then report the first unmarked index. O(n) time, O(n) space. */
    static int better(int[] nums) {
        int n = nums.length;
        boolean[] seen = new boolean[n + 1];
        for (int x : nums) seen[x] = true;
        for (int v = 0; v <= n; v++) {
            if (!seen[v]) return v;
        }
        return -1;                                   // unreachable for valid input
    }

    /** Approach 3: the sum 0 + 1 + ... + n minus the sum of the array. O(n) time, O(1) space; long keeps the sums exact. */
    static int optimalSum(int[] nums) {
        int n = nums.length;
        long expected = (long) n * (n + 1) / 2;
        long actual = 0;
        for (int x : nums) actual += x;
        return (int) (expected - actual);
    }

    /** Approach 4: XOR every index 0..n with every value; equal pairs cancel and only the missing value survives. O(n) time, O(1) space. */
    static int optimalXor(int[] nums) {
        int xor = 0;
        for (int i = 0; i < nums.length; i++) xor ^= i ^ nums[i];
        return xor ^ nums.length;                    // the index n has no array slot, fold it in separately
    }

    /** A2Z wording: nums has N - 1 distinct values from 1..N; return the missing one. Same XOR trick, shifted by one. */
    static int missingFromOneToN(int[] nums, int N) {
        int xor = 0;
        for (int v = 1; v <= N; v++) xor ^= v;
        for (int x : nums) xor ^= x;
        return xor;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String s = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce " + s);
        check(better(nums) == expected, "better " + s);
        check(optimalSum(nums) == expected, "optimalSum " + s);
        check(optimalXor(nums) == expected, "optimalXor " + s);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 0, 1}, 2);
        verify(new int[]{0, 1}, 2);                                   // the missing value is n itself
        verify(new int[]{9, 6, 4, 2, 3, 5, 7, 0, 1}, 8);
        verify(new int[]{0}, 1);                                      // n = 1, missing 1
        verify(new int[]{1}, 0);                                      // n = 1, missing 0
        verify(new int[]{}, 0);                                       // n = 0: the only value in 0..0 is missing
        verify(new int[]{1, 2, 3, 4, 5}, 0);                          // 0 missing from an otherwise complete run
        // large n: 0 + 1 + ... + n is about 2.45 * 10^9, past Integer.MAX_VALUE; an int-only sum version fails here
        int n = 70_000;
        int[] big = new int[n];
        int missing = 54_321;
        for (int i = 0, v = 0; v <= n; v++) {
            if (v != missing) big[i++] = v;
        }
        check(better(big) == missing, "better large");
        check(optimalSum(big) == missing, "optimalSum large");
        check(optimalXor(big) == missing, "optimalXor large");
        // A2Z wording: values 1..N, N - 1 of them present
        check(missingFromOneToN(new int[]{1, 2, 4, 5}, 5) == 3, "missingFromOneToN middle");
        check(missingFromOneToN(new int[]{2, 3}, 3) == 1, "missingFromOneToN first");
        check(missingFromOneToN(new int[]{1, 2}, 3) == 3, "missingFromOneToN last");
        check(missingFromOneToN(new int[]{}, 1) == 1, "missingFromOneToN single");
        System.out.println("OK P44_FindMissingNumber");
    }
}
