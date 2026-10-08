import java.util.*;

/** TUF 2805 - Subset Sums. Return the sums of all 2^n subsets of arr in non-decreasing order. */
public class P2805_SubsetSums {

    /** Approach 1: every bitmask from 0 to 2^n - 1 is one subset; add up its chosen elements. O(n * 2^n) time. */
    static List<Integer> bitmask(int[] arr) {
        int n = arr.length;
        List<Integer> sums = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {
            int sum = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) sum += arr[i];   // bit i set: arr[i] is in this subset
            }
            sums.add(sum);
        }
        Collections.sort(sums);
        return sums;
    }

    /** Approach 2: pick / not-pick recursion carrying the running sum, then sort. O(2^n) to generate plus O(n * 2^n) to sort. */
    static List<Integer> recursion(int[] arr) {
        List<Integer> sums = new ArrayList<>();
        collect(arr, 0, 0, sums);
        Collections.sort(sums);
        return sums;
    }

    static void collect(int[] arr, int index, int sumSoFar, List<Integer> sums) {
        if (index == arr.length) {                      // every element has been decided: one subset done
            sums.add(sumSoFar);
            return;
        }
        collect(arr, index + 1, sumSoFar + arr[index], sums);   // pick arr[index]
        collect(arr, index + 1, sumSoFar, sums);                // skip arr[index]
    }

    /** Approach 3: keep the sums sorted while building them; merge S with S + x for each x. O(2^n) time, no final sort. */
    static List<Integer> sortedMerge(int[] arr) {
        int[] sums = new int[]{0};                      // sums of all subsets of an empty prefix
        for (int x : arr) {
            int m = sums.length;
            int[] next = new int[2 * m];
            int i = 0, j = 0, w = 0;                    // i walks "without x", j walks "with x"
            while (i < m && j < m) {
                if (sums[i] <= sums[j] + x) next[w++] = sums[i++];
                else next[w++] = sums[j++] + x;
            }
            while (i < m) next[w++] = sums[i++];
            while (j < m) next[w++] = sums[j++] + x;
            sums = next;
        }
        List<Integer> out = new ArrayList<>(sums.length);
        for (int s : sums) out.add(s);
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, List<Integer> expected) {
        String in = arr.length <= 10 ? Arrays.toString(arr) : arr.length + " values";
        check(bitmask(arr).equals(expected), "bitmask " + in);
        check(recursion(arr).equals(expected), "recursion " + in);
        check(sortedMerge(arr).equals(expected), "sortedMerge " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3}, List.of(0, 2, 3, 5));
        verify(new int[]{5, 2, 1}, List.of(0, 1, 2, 3, 5, 6, 7, 8));
        verify(new int[]{1, 1}, List.of(0, 1, 1, 2));                    // equal elements give repeated sums
        verify(new int[]{}, List.of(0));                                 // edge: only the empty subset
        verify(new int[]{4}, List.of(0, 4));                             // edge: single element
        verify(new int[]{0, 0}, List.of(0, 0, 0, 0));
        verify(new int[]{-1, 2}, List.of(-1, 0, 1, 2));                  // negatives also work

        int[] big = new int[15];                                         // 32 768 subsets
        for (int i = 0; i < big.length; i++) big[i] = (i * 7919) % 10_000 + 1;
        List<Integer> reference = bitmask(big);
        check(reference.size() == 1 << 15, "size 2^15");
        int total = 0;
        for (int x : big) total += x;
        check(reference.get(0) == 0 && reference.get(reference.size() - 1) == total, "smallest is 0, largest is the full sum");
        verify(big, reference);
        System.out.println("OK P2805_SubsetSums");
    }
}
