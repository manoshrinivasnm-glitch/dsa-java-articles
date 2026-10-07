import java.util.*;

/** TUF 31 - Next Permutation. Rearrange nums in place into the lexicographically next permutation; wrap to the smallest one if nums is already the largest. */
public class P31_NextPermutation {

    /** Approach 1: generate every distinct permutation, sort them, and take the one after the input. O(n! * n) time and space. */
    static void bruteForce(int[] nums) {
        List<int[]> all = new ArrayList<>();
        generate(nums.clone(), 0, all);
        all.sort(P31_NextPermutation::compareLex);
        for (int i = 0; i < all.size(); i++) {
            if (Arrays.equals(all.get(i), nums)) {
                int[] next = all.get((i + 1) % all.size());   // wrap around after the last permutation
                System.arraycopy(next, 0, nums, 0, nums.length);
                return;
            }
        }
    }

    /** Fill position idx with every distinct value found at idx..n-1 (swap in, recurse, swap back). */
    private static void generate(int[] a, int idx, List<int[]> out) {
        if (idx == a.length) {
            out.add(a.clone());
            return;
        }
        Set<Integer> used = new HashSet<>();            // a value placed at idx once must not be placed again
        for (int i = idx; i < a.length; i++) {
            if (!used.add(a[i])) continue;
            swap(a, idx, i);
            generate(a, idx + 1, out);
            swap(a, idx, i);
        }
    }

    private static int compareLex(int[] x, int[] y) {
        for (int i = 0; i < x.length; i++) {
            if (x[i] != y[i]) return Integer.compare(x[i], y[i]);
        }
        return 0;
    }

    /** Approach 2: find the pivot from the right, swap it with its successor, reverse the suffix. O(n) time, O(1) space. */
    static void optimal(int[] nums) {
        int n = nums.length;
        int pivot = -1;
        for (int i = n - 2; i >= 0; i--) {          // 1. rightmost i with nums[i] < nums[i + 1]
            if (nums[i] < nums[i + 1]) {
                pivot = i;
                break;
            }
        }
        if (pivot == -1) {                          // whole array is non-increasing: it is the last permutation
            reverse(nums, 0, n - 1);
            return;
        }
        for (int j = n - 1; j > pivot; j--) {       // 2. rightmost j with nums[j] > nums[pivot]: the smallest value larger than the pivot
            if (nums[j] > nums[pivot]) {
                swap(nums, pivot, j);
                break;
            }
        }
        reverse(nums, pivot + 1, n - 1);            // 3. suffix is still non-increasing; reversing makes it the smallest arrangement
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    private static void reverse(int[] a, int lo, int hi) {
        while (lo < hi) swap(a, lo++, hi--);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        int[] a = input.clone(), b = input.clone();
        bruteForce(a);
        optimal(b);
        check(Arrays.equals(a, expected), "bruteForce " + Arrays.toString(input) + " -> " + Arrays.toString(a));
        check(Arrays.equals(b, expected), "optimal " + Arrays.toString(input) + " -> " + Arrays.toString(b));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, new int[]{1, 3, 2});
        verify(new int[]{3, 2, 1}, new int[]{1, 2, 3});                         // last permutation wraps to the first
        verify(new int[]{1, 1, 5}, new int[]{1, 5, 1});                         // duplicates
        verify(new int[]{1}, new int[]{1});                                     // single element
        verify(new int[]{}, new int[]{});                                       // empty
        verify(new int[]{2, 3, 1}, new int[]{3, 1, 2});
        verify(new int[]{1, 3, 2}, new int[]{2, 1, 3});
        verify(new int[]{1, 5, 8, 4, 7, 6, 5, 3, 1}, new int[]{1, 5, 8, 5, 1, 3, 4, 6, 7});
        verify(new int[]{2, 2, 2}, new int[]{2, 2, 2});                         // only one distinct permutation
        verify(new int[]{-3, -1, -2}, new int[]{-2, -3, -1});                   // negatives
        System.out.println("OK P31_NextPermutation");
    }
}
