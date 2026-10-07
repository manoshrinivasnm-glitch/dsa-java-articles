import java.util.*;

/** TUF 2835 - Longest Consecutive Sequence. Length of the longest run of consecutive integers present in the array (any order, duplicates allowed). */
public class P2835_LongestConsecutiveSequenceInAnArray {

    /** Approach 1: from every element keep looking for value + 1 with a linear search. O(n^2) time in the common case, O(1) space. */
    static int bruteForce(int[] nums) {
        int longest = 0;
        for (int x : nums) {
            int len = 1;
            long next = (long) x + 1;                // long: x + 1 overflows int for Integer.MAX_VALUE
            while (contains(nums, next)) {
                len++;
                next++;
            }
            longest = Math.max(longest, len);
        }
        return longest;
    }

    private static boolean contains(int[] nums, long value) {
        for (int v : nums) if (v == value) return true;
        return false;
    }

    /** Approach 2: sort a copy, then count runs while skipping duplicates. O(n log n) time, O(n) for the copy (O(1) if sorting in place is allowed). */
    static int better(int[] nums) {
        if (nums.length == 0) return 0;
        int[] a = nums.clone();
        Arrays.sort(a);
        int longest = 1, len = 1;
        for (int i = 1; i < a.length; i++) {
            if (a[i] == a[i - 1]) continue;          // duplicate: neither extends nor breaks the run
            if ((long) a[i] - a[i - 1] == 1) len++;  // long: the difference can overflow int
            else len = 1;
            longest = Math.max(longest, len);
        }
        return longest;
    }

    /** Approach 3: hash set; count a run only from its smallest element (the one whose predecessor is absent). O(n) time, O(n) space. */
    static int optimal(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) set.add(x);
        int longest = 0;
        for (int x : set) {
            if (x != Integer.MIN_VALUE && set.contains(x - 1)) continue;   // not the start of a run
            int cur = x, len = 1;
            while (cur != Integer.MAX_VALUE && set.contains(cur + 1)) {
                cur++;
                len++;
            }
            longest = Math.max(longest, len);
        }
        return longest;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        check(bruteForce(nums) == expected, "bruteForce " + Arrays.toString(nums) + " -> " + bruteForce(nums));
        check(better(nums) == expected, "better " + Arrays.toString(nums) + " -> " + better(nums));
        check(optimal(nums) == expected, "optimal " + Arrays.toString(nums) + " -> " + optimal(nums));
    }

    public static void main(String[] args) {
        verify(new int[]{100, 4, 200, 1, 3, 2}, 4);                          // 1, 2, 3, 4
        verify(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}, 9);                 // 0..8, with a duplicate 0
        verify(new int[]{}, 0);                                              // empty
        verify(new int[]{1, 1, 1}, 1);                                       // duplicates only
        verify(new int[]{5}, 1);                                             // single element
        verify(new int[]{-2, -1, 0, 1, -5}, 4);                              // negatives crossing zero
        verify(new int[]{10, 5, 12, 3, 55, 30, 4, 11, 2}, 4);               // 2, 3, 4, 5 (10, 11, 12 is shorter)
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}, 1);          // MAX + 1 must not wrap to MIN
        verify(new int[]{Integer.MAX_VALUE - 1, Integer.MAX_VALUE}, 2);      // run ending at the top of the int range
        System.out.println("OK P2835_LongestConsecutiveSequenceInAnArray");
    }
}
