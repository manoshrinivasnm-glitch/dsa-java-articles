import java.util.*;

/** TUF 926 - Fruit Into Baskets. Longest subarray that contains at most two distinct values. */
public class P926_FruitIntoBaskets {

    /** Approach 1: every start, extend while the window has at most two fruit types. O(n^2) time, O(1) extra space (set of size 3). */
    static int bruteForce(int[] fruits) {
        int n = fruits.length, best = 0;
        for (int i = 0; i < n; i++) {
            Set<Integer> types = new HashSet<>();
            for (int j = i; j < n; j++) {
                types.add(fruits[j]);
                if (types.size() > 2) break;           // a third type: no longer window from i can work
                best = Math.max(best, j - i + 1);
            }
        }
        return best;
    }

    /** Approach 2: sliding window with a count map, shrinking until at most two types remain. O(2n) time, O(1) space. */
    static int better(int[] fruits) {
        Map<Integer, Integer> count = new HashMap<>();
        int l = 0, best = 0;
        for (int r = 0; r < fruits.length; r++) {
            count.merge(fruits[r], 1, Integer::sum);
            while (count.size() > 2) {
                int f = fruits[l];
                if (count.merge(f, -1, Integer::sum) == 0) count.remove(f);
                l++;
            }
            best = Math.max(best, r - l + 1);
        }
        return best;
    }

    /** Approach 3: window that never shrinks; when it holds three types it slides right by one. O(n) time, O(1) space. */
    static int optimal(int[] fruits) {
        Map<Integer, Integer> count = new HashMap<>();
        int l = 0, best = 0;
        for (int r = 0; r < fruits.length; r++) {
            count.merge(fruits[r], 1, Integer::sum);
            if (count.size() > 2) {                    // slide: drop exactly one fruit from the left
                int f = fruits[l];
                if (count.merge(f, -1, Integer::sum) == 0) count.remove(f);
                l++;
            }
            if (count.size() <= 2) best = Math.max(best, r - l + 1);
        }
        return best;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] fruits, int expected) {
        String tag = Arrays.toString(fruits) + " expected " + expected;
        check(bruteForce(fruits) == expected, "bruteForce " + tag);
        check(better(fruits) == expected, "better " + tag);
        check(optimal(fruits) == expected, "optimal " + tag);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 1}, 3);
        verify(new int[]{0, 1, 2, 2}, 3);
        verify(new int[]{1, 2, 3, 2, 2}, 4);
        verify(new int[]{3, 3, 3, 1, 2, 1, 1, 2, 3, 3, 4}, 5);
        verify(new int[]{5}, 1);                       // a single tree
        verify(new int[]{}, 0);                        // no trees at all
        System.out.println("OK P926_FruitIntoBaskets");
    }
}
