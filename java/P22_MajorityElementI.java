import java.util.*;

/** TUF 22 - Majority Element I. Return the element that appears more than n/2 times, or -1 if there is none. */
public class P22_MajorityElementI {

    /** Approach 1: count every element with a nested loop. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (nums[j] == nums[i]) count++;
            }
            if (count > n / 2) return nums[i];
        }
        return -1;
    }

    /** Approach 2: frequency map; stop as soon as one count passes n/2. O(n) time, O(n) space. */
    static int better(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) {
            int c = freq.merge(x, 1, Integer::sum);
            if (c > nums.length / 2) return x;
        }
        return -1;
    }

    /** Approach 3: Boyer-Moore voting to find the only possible candidate, then one pass to confirm it. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        int candidate = 0, votes = 0;
        for (int x : nums) {
            if (votes == 0) {
                candidate = x;
                votes = 1;
            } else if (x == candidate) {
                votes++;
            } else {
                votes--;
            }
        }
        int count = 0;
        for (int x : nums) if (x == candidate) count++;
        return count > nums.length / 2 ? candidate : -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String in = Arrays.toString(nums.length > 20 ? Arrays.copyOf(nums, 20) : nums);
        check(bruteForce(nums) == expected, "bruteForce failed on " + in + " expected " + expected);
        check(better(nums) == expected, "better failed on " + in + " expected " + expected);
        check(optimal(nums) == expected, "optimal failed on " + in + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 2, 3}, 3);
        verify(new int[]{2, 2, 1, 1, 1, 2, 2}, 2);
        verify(new int[]{1}, 1);                                            // single element is a majority
        verify(new int[]{1, 2, 3}, -1);                                     // no majority
        verify(new int[]{1, 1, 2, 2}, -1);                                  // exactly n/2 is NOT a majority
        verify(new int[]{}, -1);                                            // empty
        verify(new int[]{-7, -7, 4}, -7);                                   // negative majority
        verify(new int[]{5, 1, 5, 2, 5, 3, 5}, 5);                          // candidate resets several times
        verify(new int[]{1, 2, 1, 2, 1, 2, 1}, 1);                          // votes keep returning to zero
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1501; i++) list.add(9);
        for (int i = 0; i < 1500; i++) list.add(i % 9);
        Collections.shuffle(list, new Random(42));
        int[] big = new int[list.size()];
        for (int i = 0; i < big.length; i++) big[i] = list.get(i);
        verify(big, 9);                                                     // 1501 of 3001 is one more than half
        System.out.println("OK P22_MajorityElementI");
    }
}
