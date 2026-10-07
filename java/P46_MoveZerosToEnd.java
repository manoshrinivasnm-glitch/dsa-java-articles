import java.util.*;

/** TUF 46 - Move Zeros to End. Move every zero to the end in place while keeping the order of the non-zero elements. */
public class P46_MoveZerosToEnd {

    /** Approach 1: copy the non-zeros to a list, write them back, then fill the tail with zeros. O(n) time, O(n) extra space. */
    static void bruteForce(int[] nums) {
        int n = nums.length;
        List<Integer> nonZero = new ArrayList<>();
        for (int x : nums) if (x != 0) nonZero.add(x);
        int i = 0;
        for (int x : nonZero) nums[i++] = x;
        while (i < n) nums[i++] = 0;
    }

    /** Approach 2: two pointers; j sits on the leftmost zero, i finds the next non-zero and swaps it in. O(n) time, O(1) extra space. */
    static void optimal(int[] nums) {
        int n = nums.length;
        int j = -1;                                  // index of the leftmost zero, -1 while none is found
        for (int i = 0; i < n; i++) {
            if (nums[i] == 0) { j = i; break; }
        }
        if (j == -1) return;                         // no zero anywhere: nothing to move
        for (int i = j + 1; i < n; i++) {
            if (nums[i] != 0) {
                int t = nums[i];                     // every slot between j and i is a zero
                nums[i] = nums[j];
                nums[j] = t;
                j++;
            }
        }
    }

    /** Approach 3: write pointer; compact the non-zeros to the front, then zero-fill the rest. O(n) time, O(1) extra space. */
    static void optimalWritePointer(int[] nums) {
        int w = 0;                                   // next slot that should receive a non-zero
        for (int x : nums) {
            if (x != 0) nums[w++] = x;               // w <= current index, so nothing unread is overwritten
        }
        while (w < nums.length) nums[w++] = 0;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        String s = Arrays.toString(input);
        int[] a = input.clone();
        bruteForce(a);
        check(Arrays.equals(a, expected), "bruteForce " + s + " -> " + Arrays.toString(a));
        int[] b = input.clone();
        optimal(b);
        check(Arrays.equals(b, expected), "optimal " + s + " -> " + Arrays.toString(b));
        int[] c = input.clone();
        optimalWritePointer(c);
        check(Arrays.equals(c, expected), "optimalWritePointer " + s + " -> " + Arrays.toString(c));
    }

    public static void main(String[] args) {
        verify(new int[]{0, 1, 0, 3, 12}, new int[]{1, 3, 12, 0, 0});
        verify(new int[]{1, 0, 2, 3, 0, 4, 0, 1}, new int[]{1, 2, 3, 4, 1, 0, 0, 0});
        verify(new int[]{0, 0, 0}, new int[]{0, 0, 0});                   // all zeros
        verify(new int[]{1, 2, 3}, new int[]{1, 2, 3});                   // no zeros
        verify(new int[]{}, new int[]{});                                 // empty
        verify(new int[]{0}, new int[]{0});                               // single zero
        verify(new int[]{0, 0, 1}, new int[]{1, 0, 0});                   // zeros first
        verify(new int[]{-1, 0, -2, 0, 5}, new int[]{-1, -2, 5, 0, 0});   // negatives are not zeros
        verify(new int[]{4, 0, 0, 0, 7}, new int[]{4, 7, 0, 0, 0});       // run of zeros in the middle
        verify(new int[]{1, 2, 0}, new int[]{1, 2, 0});                   // already in final form
        System.out.println("OK P46_MoveZerosToEnd");
    }
}
