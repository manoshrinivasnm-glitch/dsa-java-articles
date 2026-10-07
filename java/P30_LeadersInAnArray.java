import java.util.*;

/** TUF 30 - Leaders in an Array. An element is a leader if it is strictly greater than every element to its right; the last element is always a leader. */
public class P30_LeadersInAnArray {

    /** Approach 1: for each element, scan everything to its right. O(n^2) time, O(1) extra space. */
    static List<Integer> bruteForce(int[] nums) {
        int n = nums.length;
        List<Integer> leaders = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            boolean leader = true;
            for (int j = i + 1; j < n; j++) {
                if (nums[j] >= nums[i]) {            // something to the right is at least as big
                    leader = false;
                    break;
                }
            }
            if (leader) leaders.add(nums[i]);
        }
        return leaders;
    }

    /** Approach 2: scan from the right while tracking the maximum seen so far. O(n) time, O(1) extra space. */
    static List<Integer> optimal(int[] nums) {
        int n = nums.length;
        List<Integer> leaders = new ArrayList<>();
        if (n == 0) return leaders;
        int maxRight = nums[n - 1];
        leaders.add(maxRight);                       // the last element has nothing to its right
        for (int i = n - 2; i >= 0; i--) {
            if (nums[i] > maxRight) {
                leaders.add(nums[i]);
                maxRight = nums[i];
            }
        }
        Collections.reverse(leaders);                // collected right to left; restore left-to-right order
        return leaders;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, List<Integer> expected) {
        check(bruteForce(nums).equals(expected), "bruteForce " + Arrays.toString(nums) + " -> " + bruteForce(nums));
        check(optimal(nums).equals(expected), "optimal " + Arrays.toString(nums) + " -> " + optimal(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{10, 22, 12, 3, 0, 6}, List.of(22, 12, 6));
        verify(new int[]{16, 17, 4, 3, 5, 2}, List.of(17, 5, 2));
        verify(new int[]{1, 2, 3, 4, 5}, List.of(5));                            // increasing: only the last
        verify(new int[]{5, 4, 3, 2, 1}, List.of(5, 4, 3, 2, 1));                // decreasing: everything
        verify(new int[]{7}, List.of(7));                                        // single element
        verify(new int[]{}, List.of());                                          // empty
        verify(new int[]{3, 3, 3}, List.of(3));                                  // equal values: strictly greater is required
        verify(new int[]{-1, -5, -2, -9}, List.of(-1, -2, -9));                  // negatives
        verify(new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE}, List.of(Integer.MIN_VALUE)); // a MIN_VALUE sentinel would miss this
        System.out.println("OK P30_LeadersInAnArray");
    }
}
