import java.util.*;

/** TUF 23 - Majority Element II. All elements that appear strictly more than n/3 times (there can be at most two), returned in increasing order. */
public class P23_MajorityElementII {

    /** Approach 1: count each element by rescanning the whole array. O(n^2) time, O(1) extra space. */
    static List<Integer> bruteForce(int[] nums) {
        int n = nums.length;
        List<Integer> ans = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (ans.contains(nums[i])) continue;      // already reported
            int count = 0;
            for (int j = 0; j < n; j++) {
                if (nums[j] == nums[i]) count++;
            }
            if (count > n / 3) ans.add(nums[i]);
            if (ans.size() == 2) break;               // no third element can exceed n/3
        }
        Collections.sort(ans);
        return ans;
    }

    /** Approach 2: a hash map of frequencies; report an element the moment its count passes n/3. O(n) time, O(n) space. */
    static List<Integer> better(int[] nums) {
        int n = nums.length;
        Map<Integer, Integer> freq = new HashMap<>();
        List<Integer> ans = new ArrayList<>();
        for (int x : nums) {
            int c = freq.merge(x, 1, Integer::sum);
            if (c == n / 3 + 1) ans.add(x);           // the count crosses the threshold exactly once
        }
        Collections.sort(ans);
        return ans;
    }

    /** Approach 3: extended Boyer-Moore voting with two candidates, then one verification pass. O(n) time, O(1) space. */
    static List<Integer> optimal(int[] nums) {
        int n = nums.length;
        int cand1 = 0, cand2 = 0, count1 = 0, count2 = 0;
        for (int x : nums) {
            if (count1 > 0 && x == cand1) count1++;
            else if (count2 > 0 && x == cand2) count2++;
            else if (count1 == 0) {
                cand1 = x;
                count1 = 1;
            } else if (count2 == 0) {
                cand2 = x;
                count2 = 1;
            } else {                                  // x differs from both: cancel one vote of each
                count1--;
                count2--;
            }
        }
        count1 = 0;
        count2 = 0;
        for (int x : nums) {                          // voting only yields candidates; verify their real counts
            if (x == cand1) count1++;
            else if (x == cand2) count2++;
        }
        List<Integer> ans = new ArrayList<>();
        if (count1 > n / 3) ans.add(cand1);
        if (count2 > n / 3) ans.add(cand2);
        Collections.sort(ans);
        return ans;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, List<Integer> expected) {
        check(bruteForce(nums).equals(expected), "bruteForce " + Arrays.toString(nums) + " -> " + bruteForce(nums));
        check(better(nums).equals(expected), "better " + Arrays.toString(nums) + " -> " + better(nums));
        check(optimal(nums).equals(expected), "optimal " + Arrays.toString(nums) + " -> " + optimal(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{3, 2, 3}, List.of(3));
        verify(new int[]{1}, List.of(1));                                        // single element
        verify(new int[]{1, 2}, List.of(1, 2));                                  // n/3 == 0: both appear more than 0 times
        verify(new int[]{1, 1, 1, 3, 3, 2, 2, 2}, List.of(1, 2));                // two answers, candidates get replaced midway
        verify(new int[]{1, 2, 3, 4}, List.of());                                // no majority at all
        verify(new int[]{2, 2, 1, 1, 1, 2, 2}, List.of(1, 2));
        verify(new int[]{}, List.of());                                          // empty
        verify(new int[]{0, 0, 0}, List.of(0));                                  // the value 0 equals the default candidate
        verify(new int[]{-1, -1, -1, 2, 2}, List.of(-1, 2));                     // negatives
        verify(new int[]{1, 2, 3, 1, 2, 3, 1, 2, 3}, List.of());                 // three values at exactly n/3 each: none qualifies
        System.out.println("OK P23_MajorityElementII");
    }
}
