import java.util.*;

/** TUF 2793 - Find the number that appears once while every other number appears exactly twice. */
public class P2793_FindTheNumberThatAppearsOnceAndOther {

    /** Approach 1: count every element's occurrences with a nested loop. O(n^2) time, O(1) space. */
    static int bruteForce(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {                // test each element in turn
            int count = 0;
            for (int j = 0; j < n; j++) {            // count how often nums[i] appears
                if (nums[j] == nums[i]) count++;
            }
            if (count == 1) return nums[i];          // no partner: this is the single number
        }
        return -1;                                   // unreachable for valid input
    }

    /** Approach 2: frequency map, then return the key whose count is 1. O(n) time, O(n) space. */
    static int better(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();   // value -> how many times it appears
        for (int x : nums) freq.merge(x, 1, Integer::sum);   // add 1 to x's count
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() == 1) return e.getKey();   // seen once: the answer
        }
        return -1;                                   // unreachable for valid input
    }

    /** Approach 2 (variant): sort so that pairs sit side by side; the first broken pair is the answer. O(n log n) time. */
    static int betterSorting(int[] nums) {
        int[] a = nums.clone();                      // sort a copy, not the caller's array
        Arrays.sort(a);
        for (int i = 0; i + 1 < a.length; i += 2) {  // check the pairs (0,1), (2,3), ...
            if (a[i] != a[i + 1]) return a[i];       // pair broken: a[i] has no twin
        }
        return a[a.length - 1];                      // every pair matched: the single one is last
    }

    /** Approach 3: XOR everything; equal pairs cancel to 0 and only the single number survives. O(n) time, O(1) space. */
    static int optimal(int[] nums) {
        int x = 0;
        for (int v : nums) x ^= v;                   // v ^ v = 0, so each pair cancels
        return x;                                    // only the single number is left
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] nums, int expected) {
        String in = Arrays.toString(nums.length > 20 ? Arrays.copyOf(nums, 20) : nums);
        check(bruteForce(nums) == expected, "bruteForce failed on " + in + " expected " + expected);
        check(better(nums) == expected, "better failed on " + in + " expected " + expected);
        check(betterSorting(nums) == expected, "betterSorting failed on " + in + " expected " + expected);
        check(optimal(nums) == expected, "optimal failed on " + in + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(new int[]{4, 1, 2, 1, 2}, 4);
        verify(new int[]{2, 2, 1}, 1);
        verify(new int[]{1}, 1);                                            // single element
        verify(new int[]{-5, 3, 3}, -5);                                    // negative answer
        verify(new int[]{7, 0, 7}, 0);                                      // the answer is zero
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE}, Integer.MIN_VALUE);
        verify(new int[]{3, 5, 3, 9, 5, 9, 11}, 11);                        // single is last
        List<Integer> list = new ArrayList<>();
        for (int v = 1; v <= 2000; v++) {
            list.add(v);
            list.add(v);
        }
        list.add(777_777);
        Collections.shuffle(list, new Random(7));
        int[] big = new int[list.size()];
        for (int i = 0; i < big.length; i++) big[i] = list.get(i);
        verify(big, 777_777);                                               // 4001 elements, fixed seed
        System.out.println("OK P2793_FindTheNumberThatAppearsOnceAndOther");
    }
}
