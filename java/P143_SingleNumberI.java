import java.util.*;

/** TUF 143 - Single Number I. Every element appears exactly twice except one; return that one. */
public class P143_SingleNumberI {

    /** Approach 1: for every element, count its occurrences with a second scan. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (nums[j] == nums[i]) count++;
            }
            if (count == 1) return nums[i];
        }
        return -1;
    }

    /** Approach 2: count occurrences in a HashMap, then return the key whose count is 1. O(n) time, O(n) space. */
    static int better(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) freq.merge(x, 1, Integer::sum);
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() == 1) return e.getKey();
        }
        return -1;
    }

    /** Approach 2 (variant): sort so that pairs sit side by side; the first even index whose partner differs is the answer. O(n log n) time. */
    static int betterSorting(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        int n = a.length;
        for (int i = 0; i < n; i += 2) {
            if (i == n - 1 || a[i] != a[i + 1]) return a[i];
        }
        return -1;
    }

    /** Approach 3: XOR everything; equal pairs cancel to 0 and only the single element survives. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        int xor = 0;
        for (int x : nums) xor ^= x;
        return xor;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String label = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce " + label);
        check(better(nums) == expected, "better " + label);
        check(betterSorting(nums) == expected, "betterSorting " + label);
        check(optimal(nums) == expected, "optimal " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{4, 1, 2, 1, 2}, 4);
        verify(new int[]{2, 2, 1}, 1);
        verify(new int[]{7}, 7);                                   // a lone element is its own answer
        verify(new int[]{-3, 5, 5, -3, 9}, 9);                     // negatives
        verify(new int[]{0, 1, 1}, 0);                             // the answer itself is 0
        verify(new int[]{Integer.MIN_VALUE, 3, 3}, Integer.MIN_VALUE);
        verify(new int[]{1, 1, 2, 2, 3, 3, 4, 4, 5}, 5);           // single element at the very end
        verify(new int[]{9, 1, 1, 2, 2, 3, 3, 4, 4}, 9);           // single element at the very front
        System.out.println("OK P143_SingleNumberI");
    }
}
