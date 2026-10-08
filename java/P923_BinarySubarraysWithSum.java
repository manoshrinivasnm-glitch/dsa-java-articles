import java.util.*;

/** TUF 923 - Binary Subarrays With Sum. Count subarrays of a 0/1 array whose sum equals goal. */
public class P923_BinarySubarraysWithSum {

    /** Approach 1: every start, extend while the running sum has not passed goal. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums, int goal) {
        int n = nums.length, count = 0;
        for (int i = 0; i < n; i++) {
            int sum = 0;
            for (int j = i; j < n; j++) {
                sum += nums[j];
                if (sum == goal) count++;
                else if (sum > goal) break;            // sums only grow: nothing further from i can match
            }
        }
        return count;
    }

    /** Approach 2: prefix sums plus a frequency map of earlier prefixes. O(n) time, O(n) space. */
    static int better(int[] nums, int goal) {
        Map<Integer, Integer> freq = new HashMap<>();
        freq.put(0, 1);                                // the empty prefix
        int prefix = 0, count = 0;
        for (int x : nums) {
            prefix += x;
            count += freq.getOrDefault(prefix - goal, 0);
            freq.merge(prefix, 1, Integer::sum);
        }
        return count;
    }

    /** Approach 3: exactly(goal) = atMost(goal) - atMost(goal - 1), each counted by a sliding window. O(n) time, O(1) space. */
    static int optimal(int[] nums, int goal) {
        return atMost(nums, goal) - atMost(nums, goal - 1);
    }

    static int atMost(int[] nums, int goal) {
        if (goal < 0) return 0;                        // no subarray of a 0/1 array has a negative sum
        int l = 0, sum = 0, count = 0;
        for (int r = 0; r < nums.length; r++) {
            sum += nums[r];
            while (sum > goal) {
                sum -= nums[l];
                l++;
            }
            count += r - l + 1;                        // every start in [l, r] gives a valid subarray ending at r
        }
        return count;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int goal, int expected) {
        String tag = Arrays.toString(nums) + " goal=" + goal + " expected " + expected;
        check(bruteForce(nums, goal) == expected, "bruteForce " + tag);
        check(better(nums, goal) == expected, "better " + tag);
        check(optimal(nums, goal) == expected, "optimal " + tag);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 0, 1, 0, 1}, 2, 4);
        verify(new int[]{0, 0, 0, 0, 0}, 0, 15);      // goal 0: every all-zero subarray, 5*6/2
        verify(new int[]{1, 1, 1}, 2, 2);
        verify(new int[]{0, 1, 0}, 1, 4);              // zeros on both sides multiply the choices
        verify(new int[]{1, 0, 1}, 5, 0);              // goal larger than the total
        verify(new int[]{}, 0, 0);                     // empty array has no non-empty subarray
        System.out.println("OK P923_BinarySubarraysWithSum");
    }
}
