import java.util.*;

/** TUF 542 - Jump Game I. From index i you may jump up to nums[i] steps forward; can you reach the last index from index 0? */
public class P542_JumpGameI {

    /** Approach 1: recursively try every jump length from every index. Exponential time, O(n) stack. */
    static boolean bruteForce(int[] nums) {
        return canReach(nums, 0);
    }

    static boolean canReach(int[] nums, int i) {
        if (i >= nums.length - 1) return true;
        for (int step = 1; step <= nums[i]; step++) {
            if (canReach(nums, i + step)) return true;
        }
        return false;
    }

    /** Approach 2: DP from the right; good[i] is true when index i can reach the last index. O(n^2) time, O(n) space. */
    static boolean better(int[] nums) {
        int n = nums.length;
        boolean[] good = new boolean[n];
        good[n - 1] = true;
        for (int i = n - 2; i >= 0; i--) {
            int far = Math.min(n - 1, i + nums[i]);
            for (int j = i + 1; j <= far; j++) {
                if (good[j]) {
                    good[i] = true;
                    break;
                }
            }
        }
        return good[0];
    }

    /** Approach 3: greedy, keep the farthest index reachable so far. O(n) time, O(1) space. */
    static boolean optimal(int[] nums) {
        int maxReach = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) return false;              // index i is unreachable, so is everything after it
            maxReach = Math.max(maxReach, i + nums[i]);
            if (maxReach >= nums.length - 1) return true;
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, boolean expected) {
        String in = Arrays.toString(nums);
        check(bruteForce(nums) == expected, "bruteForce wrong for " + in);
        check(better(nums) == expected, "better wrong for " + in);
        check(optimal(nums) == expected, "optimal wrong for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 1, 1, 4}, true);
        verify(new int[]{3, 2, 1, 0, 4}, false);              // every path lands on the 0 at index 3
        verify(new int[]{2, 0, 0}, true);                     // jump straight over the zero
        verify(new int[]{1, 0, 1, 0}, false);
        verify(new int[]{1, 1, 1, 1, 1}, true);
        verify(new int[]{4, 0, 0, 0, 0, 0}, false);           // reach is one short
        verify(new int[]{5, 9, 3, 2, 1, 0, 2, 3, 3, 1, 0, 0}, true);
        verify(new int[]{0, 1}, false);                       // stuck at the start
        verify(new int[]{0}, true);                           // already at the last index
        System.out.println("OK P542_JumpGameI");
    }
}
