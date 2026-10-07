import java.util.*;

/** TUF 342 - Reverse an array. Every method returns a reversed copy and leaves the input untouched. */
public class P342_ReverseAnArray {

    /** Approach 1: copy into a second array back to front. O(n) time, O(n) extra space. */
    static int[] bruteForce(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];
        for (int i = 0; i < n; i++) res[n - 1 - i] = arr[i];
        return res;
    }

    /** Approach 2: swap from both ends while two pointers move inward. O(n) time, O(1) extra space. */
    static int[] iterativeTwoPointers(int[] arr) {
        int[] a = arr.clone();
        int l = 0, r = a.length - 1;
        while (l < r) {
            int t = a[l]; a[l] = a[r]; a[r] = t;
            l++; r--;
        }
        return a;
    }

    /** Approach 3: the same two pointers, but the loop becomes a recursive call. O(n) time, O(n) stack. */
    static int[] recursiveTwoPointers(int[] arr) {
        int[] a = arr.clone();
        reverse(a, 0, a.length - 1);
        return a;
    }

    private static void reverse(int[] a, int l, int r) {
        if (l >= r) return;                     // base case: pointers met or crossed
        int t = a[l]; a[l] = a[r]; a[r] = t;    // swap the outer pair
        reverse(a, l + 1, r - 1);               // reverse the inner part
    }

    /** Approach 4: one index i paired with its mirror n - 1 - i; stop at the middle. O(n) time, O(n) stack. */
    static int[] recursiveOnePointer(int[] arr) {
        int[] a = arr.clone();
        reverseFrom(a, 0);
        return a;
    }

    private static void reverseFrom(int[] a, int i) {
        int n = a.length;
        if (i >= n / 2) return;                 // base case: first half done, so the whole array is reversed
        int t = a[i]; a[i] = a[n - 1 - i]; a[n - 1 - i] = t;
        reverseFrom(a, i + 1);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int[] expected) {
        int[] original = arr.clone();
        check(Arrays.equals(bruteForce(arr), expected), "bruteForce failed for " + Arrays.toString(arr));
        check(Arrays.equals(iterativeTwoPointers(arr), expected), "iterativeTwoPointers failed for " + Arrays.toString(arr));
        check(Arrays.equals(recursiveTwoPointers(arr), expected), "recursiveTwoPointers failed for " + Arrays.toString(arr));
        check(Arrays.equals(recursiveOnePointer(arr), expected), "recursiveOnePointer failed for " + Arrays.toString(arr));
        check(Arrays.equals(arr, original), "input must not be modified: " + Arrays.toString(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{}, new int[]{});                                   // edge: empty
        verify(new int[]{7}, new int[]{7});                                 // edge: single element
        verify(new int[]{1, 2}, new int[]{2, 1});
        verify(new int[]{1, 2, 3, 4}, new int[]{4, 3, 2, 1});               // even length
        verify(new int[]{1, 2, 3, 4, 5}, new int[]{5, 4, 3, 2, 1});         // odd length: middle stays
        verify(new int[]{-1, 2, 2, -1, 0}, new int[]{0, -1, 2, 2, -1});     // duplicates and negatives
        verify(new int[]{3, 3, 3}, new int[]{3, 3, 3});                     // palindrome stays the same
        System.out.println("OK P342_ReverseAnArray");
    }
}
