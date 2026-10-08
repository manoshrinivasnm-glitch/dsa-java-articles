import java.util.*;

/**
 * TUF 465 - Find the Duplicate Number. nums has n + 1 values, each in [1, n]; exactly one value repeats
 * (possibly more than twice). Return it without modifying nums.
 */
public class P465_FindTheDuplicateNumber {

    /** Approach 1: sort a copy; the duplicate ends up next to itself. O(n log n) time, O(n) space for the copy. */
    static int bruteForce(int[] nums) {
        int[] a = nums.clone();
        Arrays.sort(a);
        for (int i = 1; i < a.length; i++) {
            if (a[i] == a[i - 1]) return a[i];
        }
        return -1;
    }

    /** Approach 2: remember which values were seen. O(n) time, O(n) space. */
    static int better(int[] nums) {
        boolean[] seen = new boolean[nums.length];   // values are in [1, n] and nums.length = n + 1
        for (int v : nums) {
            if (seen[v]) return v;
            seen[v] = true;
        }
        return -1;
    }

    /** Approach 3: Floyd's cycle detection on the graph i -> nums[i]. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        int slow = 0, fast = 0;                      // index 0 is the head: no value points back to it
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);
        slow = 0;                                    // phase 2: both move one step until they meet
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }
        return slow;                                 // the cycle entrance is the duplicated value
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        int[] copy = nums.clone();
        check(bruteForce(nums) == expected, "bruteForce " + Arrays.toString(nums));
        check(better(nums) == expected, "better " + Arrays.toString(nums));
        check(optimal(nums) == expected, "optimal " + Arrays.toString(nums));
        check(Arrays.equals(nums, copy), "input must not be modified");
    }

    public static void main(String[] args) {
        verify(new int[]{1, 3, 4, 2, 2}, 2);
        verify(new int[]{3, 1, 3, 4, 2}, 3);
        verify(new int[]{1, 1}, 1);                              // edge: smallest input, n = 1
        verify(new int[]{3, 3, 3, 3, 3}, 3);                     // the duplicate appears many times
        verify(new int[]{2, 5, 9, 6, 9, 3, 8, 9, 7, 1}, 9);
        verify(new int[]{1, 4, 4, 2, 4}, 4);
        verify(new int[]{2, 2, 2}, 2);

        Random rnd = new Random(465);
        for (int trial = 0; trial < 300; trial++) {
            int n = 1 + rnd.nextInt(30);
            int dup = 1 + rnd.nextInt(n);
            List<Integer> vals = new ArrayList<>();
            for (int v = 1; v <= n; v++) vals.add(v);
            vals.add(dup);
            int extra = rnd.nextInt(n);              // replace some other values by the duplicate too
            Collections.shuffle(vals, rnd);
            for (int k = 0; k < extra && k < vals.size(); k++) vals.set(k, dup);
            Collections.shuffle(vals, rnd);
            int[] a = vals.stream().mapToInt(Integer::intValue).toArray();
            verify(a, dup);
        }
        System.out.println("OK P465_FindTheDuplicateNumber");
    }
}
