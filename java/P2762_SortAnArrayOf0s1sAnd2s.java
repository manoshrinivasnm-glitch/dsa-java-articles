import java.util.*;

/** TUF 2762 - Sort an array containing only 0s, 1s and 2s, in place. */
public class P2762_SortAnArrayOf0s1sAnd2s {

    /** Approach 1: use a general-purpose sort. O(n log n) time, O(log n) stack for the sort. */
    static void bruteForce(int[] nums) {
        Arrays.sort(nums);                           // general sort; ignores that only 0, 1, 2 occur
    }

    /** Approach 2: count the 0s, 1s and 2s, then overwrite the array. Two passes, O(n) time, O(1) space. */
    static void better(int[] nums) {
        int c0 = 0, c1 = 0, c2 = 0;                  // how many 0s, 1s and 2s
        for (int x : nums) {                         // pass 1: count each value
            if (x == 0) c0++;
            else if (x == 1) c1++;
            else c2++;
        }
        int i = 0;                                   // pass 2: rewrite the array in order
        while (c0-- > 0) nums[i++] = 0;              // all the 0s first
        while (c1-- > 0) nums[i++] = 1;              // then the 1s
        while (c2-- > 0) nums[i++] = 2;              // then the 2s
    }

    /** Approach 3: Dutch National Flag, one pass with three pointers. O(n) time, O(1) space. */
    static void optimal(int[] nums) {
        int low = 0, mid = 0, high = nums.length - 1;   // [0, low) 0s, [low, mid) 1s, (high, end] 2s
        while (mid <= high) {                        // nums[mid..high] is still unclassified
            if (nums[mid] == 0) {                    // a 0: swap it to the end of the 0s zone
                int t = nums[low]; nums[low] = nums[mid]; nums[mid] = t;
                low++;                               // the 0s zone grows by one
                mid++;                               // the value swapped in is a 1, already checked
            } else if (nums[mid] == 1) {             // a 1 is already in place
                mid++;
            } else {                                 // a 2: swap it to the front of the 2s zone
                int t = nums[mid]; nums[mid] = nums[high]; nums[high] = t;
                high--;                              // mid stays: the value swapped in is unchecked
            }
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int[] expected) {
        String in = Arrays.toString(input.length > 20 ? Arrays.copyOf(input, 20) : input);
        int[] a = input.clone();
        bruteForce(a);
        check(Arrays.equals(a, expected), "bruteForce failed on " + in);
        int[] b = input.clone();
        better(b);
        check(Arrays.equals(b, expected), "better failed on " + in);
        int[] c = input.clone();
        optimal(c);
        check(Arrays.equals(c, expected), "optimal failed on " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 0, 2, 1, 1, 0}, new int[]{0, 0, 1, 1, 2, 2});
        verify(new int[]{2, 0, 1}, new int[]{0, 1, 2});
        verify(new int[]{0}, new int[]{0});                                 // single element
        verify(new int[]{}, new int[]{});                                   // empty
        verify(new int[]{1, 1, 1}, new int[]{1, 1, 1});                     // only ones: mid walks to the end
        verify(new int[]{2, 2, 2, 0, 0}, new int[]{0, 0, 2, 2, 2});         // no ones at all
        verify(new int[]{2, 1, 0}, new int[]{0, 1, 2});                     // reverse order
        verify(new int[]{0, 0, 1, 1, 2, 2}, new int[]{0, 0, 1, 1, 2, 2});   // already sorted
        verify(new int[]{2, 2, 1, 1, 0, 0}, new int[]{0, 0, 1, 1, 2, 2});
        Random rnd = new Random(42);
        int[] big = new int[5000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(3);
        int[] expected = big.clone();
        Arrays.sort(expected);
        verify(big, expected);                                              // 5000 values, fixed seed
        System.out.println("OK P2762_SortAnArrayOf0s1sAnd2s");
    }
}
