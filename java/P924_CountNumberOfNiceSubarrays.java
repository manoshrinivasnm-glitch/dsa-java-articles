import java.util.*;

/** TUF 924 - Count number of Nice subarrays. Count subarrays that contain exactly k odd numbers. */
public class P924_CountNumberOfNiceSubarrays {

    /** Approach 1: every start, extend while the odd count has not passed k. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums, int k) {
        int n = nums.length, count = 0;
        for (int i = 0; i < n; i++) {
            int odd = 0;
            for (int j = i; j < n; j++) {
                odd += nums[j] & 1;                    // & 1 is 1 for odd values, negatives included
                if (odd == k) count++;
                else if (odd > k) break;
            }
        }
        return count;
    }

    /** Approach 2: prefix counts of odd numbers, with an array of how often each prefix count occurred. O(n) time, O(n) space. */
    static int better(int[] nums, int k) {
        int[] freq = new int[nums.length + 1];         // prefix odd counts lie in 0..n
        freq[0] = 1;
        int odd = 0, count = 0;
        for (int x : nums) {
            odd += x & 1;
            if (odd >= k) count += freq[odd - k];
            freq[odd]++;
        }
        return count;
    }

    /** Approach 3: exactly(k) = atMost(k) - atMost(k - 1) with a sliding window on parities. O(n) time, O(1) space. */
    static int optimal(int[] nums, int k) {
        return atMost(nums, k) - atMost(nums, k - 1);
    }

    static int atMost(int[] nums, int k) {
        if (k < 0) return 0;
        int l = 0, odd = 0, count = 0;
        for (int r = 0; r < nums.length; r++) {
            odd += nums[r] & 1;
            while (odd > k) {
                odd -= nums[l] & 1;
                l++;
            }
            count += r - l + 1;                        // subarrays ending at r with at most k odd numbers
        }
        return count;
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
        verify(new int[]{1, 1, 2, 1, 1}, 3, 2);
        verify(new int[]{2, 4, 6}, 1, 0);                              // no odd numbers at all
        verify(new int[]{2, 2, 2, 1, 2, 2, 1, 2, 2, 2}, 2, 16);        // 4 choices on the left times 4 on the right
        verify(new int[]{1}, 1, 1);
        verify(new int[]{1, 2, 3}, 0, 1);                              // k = 0: only [2]
        verify(new int[]{-1, 2, -3}, 2, 1);                            // negative odd numbers
        verify(new int[]{}, 1, 0);                                     // empty array
        System.out.println("OK P924_CountNumberOfNiceSubarrays");
    }
}
