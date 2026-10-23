import java.util.*;

/** TUF 930 - Max Consecutive Ones III. Longest run of 1s in a binary array if at most k zeros may be flipped. */
public class P930_MaxConsecutiveOnesIII {

    /** Approach 1: every start, extend until the window holds more than k zeros. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums, int k) {
        int n = nums.length, best = 0;                 // best = longest valid window so far
        for (int i = 0; i < n; i++) {                  // try every start index i
            int zeros = 0;                             // zeros in nums[i..j] = flips this window needs
            for (int j = i; j < n; j++) {
                if (nums[j] == 0) zeros++;             // each zero costs one flip
                if (zeros > k) break;                  // any longer window from i also has too many zeros
                best = Math.max(best, j - i + 1);      // nums[i..j] needs at most k flips
            }
        }
        return best;
    }

    /** Approach 2: sliding window that shrinks with a while loop until it is valid again. O(2n) time, O(1) space. */
    static int better(int[] nums, int k) {
        int l = 0, zeros = 0, best = 0;                // window is nums[l..r]; zeros = flips it needs
        for (int r = 0; r < nums.length; r++) {        // grow the window by one on the right
            if (nums[r] == 0) zeros++;                 // a new zero entered the window
            while (zeros > k) {                        // too many zeros: shrink from the left until valid
                if (nums[l] == 0) zeros--;             // a zero leaves the window
                l++;
            }
            best = Math.max(best, r - l + 1);          // the window is valid here
        }
        return best;
    }

    /** Approach 3: window that never shrinks; when invalid it slides right by one. O(n) time, O(1) space. */
    static int optimal(int[] nums, int k) {
        int l = 0, zeros = 0, best = 0;                // window is nums[l..r]; zeros = flips it needs
        for (int r = 0; r < nums.length; r++) {        // grow the window by one on the right
            if (nums[r] == 0) zeros++;                 // a new zero entered the window
            if (zeros > k) {                           // slide instead of shrink: length stays at the best so far
                if (nums[l] == 0) zeros--;             // a zero leaves the window
                l++;
            }
            if (zeros <= k) best = Math.max(best, r - l + 1);   // only a valid window may set a new best
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int k, int expected) {
        String tag = Arrays.toString(nums) + " k=" + k + " expected " + expected;
        check(bruteForce(nums, k) == expected, "bruteForce " + tag);
        check(better(nums, k) == expected, "better " + tag);
        check(optimal(nums, k) == expected, "optimal " + tag);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0}, 2, 6);
        verify(new int[]{0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1}, 3, 10);
        verify(new int[]{1, 0, 1, 1}, 0, 2);          // k = 0: plain longest run of ones
        verify(new int[]{0, 0, 0}, 0, 0);             // nothing can be flipped and there are no ones
        verify(new int[]{0, 0}, 5, 2);                // k larger than the number of zeros
        verify(new int[]{}, 1, 0);                    // empty array
        System.out.println("OK P930_MaxConsecutiveOnesIII");
    }
}
