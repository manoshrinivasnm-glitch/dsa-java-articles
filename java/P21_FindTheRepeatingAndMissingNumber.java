import java.util.*;

/** TUF 21 - Find the repeating and missing number. arr has n values from 1..n, one value twice and one absent; return {repeating, missing}. */
public class P21_FindTheRepeatingAndMissingNumber {

    /** Approach 1: for every value 1..n count how often it occurs. O(n^2) time, O(1) space. */
    static int[] bruteForce(int[] arr) {
        int n = arr.length, repeating = -1, missing = -1;
        for (int v = 1; v <= n; v++) {
            int count = 0;
            for (int x : arr) if (x == v) count++;
            if (count == 2) repeating = v;
            else if (count == 0) missing = v;
            if (repeating != -1 && missing != -1) break;
        }
        return new int[]{repeating, missing};
    }

    /** Approach 2: frequency array indexed by value. O(n) time, O(n) space. */
    static int[] better(int[] arr) {
        int n = arr.length;
        int[] freq = new int[n + 1];
        for (int x : arr) freq[x]++;
        int repeating = -1, missing = -1;
        for (int v = 1; v <= n; v++) {
            if (freq[v] == 2) repeating = v;
            else if (freq[v] == 0) missing = v;
        }
        return new int[]{repeating, missing};
    }

    /** Approach 3: two equations from the sum and the sum of squares. O(n) time, O(1) space. */
    static int[] optimalMath(int[] arr) {
        long n = arr.length;
        long sn = n * (n + 1) / 2;                       // 1 + 2 + ... + n
        long s2n = n * (n + 1) * (2 * n + 1) / 6;        // 1^2 + 2^2 + ... + n^2
        long s = 0, s2 = 0;
        for (int v : arr) {
            s += v;
            s2 += (long) v * v;
        }
        long diff = s - sn;                              // x - y   (x repeating, y missing)
        long sumXY = (s2 - s2n) / diff;                  // x + y   since x^2 - y^2 = (x - y)(x + y)
        long x = (diff + sumXY) / 2;
        long y = x - diff;
        return new int[]{(int) x, (int) y};
    }

    /** Approach 4: xor everything to get x ^ y, split by one differing bit, xor each bucket separately. O(n) time, O(1) space. */
    static int[] optimalXor(int[] arr) {
        int n = arr.length, xr = 0;
        for (int i = 0; i < n; i++) xr ^= arr[i] ^ (i + 1);   // every value pairs off except x and y: xr = x ^ y
        int bit = xr & -xr;                                   // lowest bit where x and y differ
        int zero = 0, one = 0;
        for (int i = 0; i < n; i++) {
            if ((arr[i] & bit) != 0) one ^= arr[i]; else zero ^= arr[i];
            if (((i + 1) & bit) != 0) one ^= (i + 1); else zero ^= (i + 1);
        }
        int count = 0;                                        // decide which bucket holds the repeating number
        for (int v : arr) if (v == one) count++;
        return count == 2 ? new int[]{one, zero} : new int[]{zero, one};
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int[] expected, boolean runBrute) {
        String in = arr.length <= 10 ? Arrays.toString(arr) : "array of length " + arr.length;
        if (runBrute) check(Arrays.equals(bruteForce(arr), expected), "bruteForce " + in + " -> " + Arrays.toString(bruteForce(arr)));
        check(Arrays.equals(better(arr), expected), "better " + in + " -> " + Arrays.toString(better(arr)));
        check(Arrays.equals(optimalMath(arr), expected), "optimalMath " + in + " -> " + Arrays.toString(optimalMath(arr)));
        check(Arrays.equals(optimalXor(arr), expected), "optimalXor " + in + " -> " + Arrays.toString(optimalXor(arr)));
    }

    public static void main(String[] args) {
        verify(new int[]{3, 1, 2, 5, 3}, new int[]{3, 4}, true);
        verify(new int[]{1, 1}, new int[]{1, 2}, true);                 // smallest input, missing is the largest value
        verify(new int[]{2, 2}, new int[]{2, 1}, true);                 // smallest input, missing is 1
        verify(new int[]{1, 2, 3, 4, 4}, new int[]{4, 5}, true);        // duplicates adjacent at the end
        verify(new int[]{4, 3, 6, 2, 1, 1}, new int[]{1, 5}, true);
        verify(new int[]{5, 2, 3, 4, 5}, new int[]{5, 1}, true);        // repeating is n, missing is 1
        int n = 100_000, rep = 77_777, miss = 12_345;                  // sums of squares exceed the int range
        int[] big = new int[n];
        for (int i = 0; i < n; i++) big[i] = i + 1;
        big[miss - 1] = rep;
        verify(big, new int[]{rep, miss}, false);
        System.out.println("OK P21_FindTheRepeatingAndMissingNumber");
    }
}
