import java.util.*;

/** TUF 145 - Single Number III. Every element appears exactly twice except two; return those two in increasing order. */
public class P145_SingleNumberIII {

    /** Approach 1: for each element, count its occurrences with a second scan. O(n^2) time, O(1) space. */
    static int[] bruteForce(int[] nums) {
        int n = nums.length;
        int[] result = new int[2];
        int found = 0;
        for (int i = 0; i < n && found < 2; i++) {
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (nums[j] == nums[i]) count++;
            }
            if (count == 1) result[found++] = nums[i];
        }
        Arrays.sort(result);
        return result;
    }

    /** Approach 2: count occurrences in a HashMap and collect the two keys seen once. O(n) time, O(n) space. */
    static int[] better(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) freq.merge(x, 1, Integer::sum);
        int[] result = new int[2];
        int found = 0;
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() == 1) result[found++] = e.getKey();
        }
        Arrays.sort(result);
        return result;
    }

    /** Approach 3: XOR all -> a ^ b; split the array by the lowest set bit of that value; XOR each bucket. O(n) time, O(1) space. */
    static int[] optimal(int[] nums) {
        int xorAll = 0;
        for (int x : nums) xorAll ^= x;             // pairs cancel, so this is a ^ b
        int lowestBit = xorAll & -xorAll;           // a and b differ at this bit, so it separates them
        int bucket0 = 0, bucket1 = 0;
        for (int x : nums) {
            if ((x & lowestBit) == 0) bucket0 ^= x; else bucket1 ^= x;
        }
        int[] result = {bucket0, bucket1};
        Arrays.sort(result);
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int[] expected) {
        String label = Arrays.toString(nums);
        check(Arrays.equals(bruteForce(nums), expected), "bruteForce " + label);
        check(Arrays.equals(better(nums), expected), "better " + label);
        check(Arrays.equals(optimal(nums), expected), "optimal " + label);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 1, 3, 2, 5}, new int[]{3, 5});
        verify(new int[]{-1, 0}, new int[]{-1, 0});                              // smallest valid input
        verify(new int[]{0, 1}, new int[]{0, 1});                                // one answer is 0
        verify(new int[]{2, 2, 4, 4, 7, 9}, new int[]{7, 9});
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE, 3, 3}, new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE});
        verify(new int[]{1, 1, -2, -8}, new int[]{-8, -2});                      // negatives: lowest set bit of 6 is 2
        verify(new int[]{8, 24, 100, 100}, new int[]{8, 24});                    // the separating bit is 16, not bit 0
        verify(new int[]{5, 6, 6, 5, 11, 12, 13, 13, 12, 10}, new int[]{10, 11});
        System.out.println("OK P145_SingleNumberIII");
    }
}
