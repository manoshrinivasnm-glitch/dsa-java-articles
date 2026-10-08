import java.util.*;

/** TUF 2838 - Kth largest element in a stream of running integers. After every add, report the k-th largest value so far (-1 while fewer than k values exist). */
public class P2838_KthLargestElementInAStreamOfRunningInteg {

    /** Common contract so every implementation can be driven by the same test script. */
    interface KthStream {
        int add(int val);
    }

    /** Approach 1: keep every value; copy and sort on each add. O(n log n) per add, O(n) space. */
    static class SortEveryTime implements KthStream {
        private final int k;
        private final List<Integer> all = new ArrayList<>();

        SortEveryTime(int k, int[] nums) {
            this.k = k;
            for (int x : nums) all.add(x);
        }

        public int add(int val) {
            all.add(val);
            if (all.size() < k) return -1;
            int[] a = new int[all.size()];
            for (int i = 0; i < a.length; i++) a[i] = all.get(i);
            Arrays.sort(a);
            return a[a.length - k];
        }
    }

    /** Approach 2: keep only the k largest in an ascending list, inserting by binary search. O(k) per add, O(k) space. */
    static class SortedTopK implements KthStream {
        private final int k;
        private final List<Integer> top = new ArrayList<>();      // ascending; top.get(0) is the k-th largest once full

        SortedTopK(int k, int[] nums) {
            this.k = k;
            for (int x : nums) add(x);
        }

        public int add(int val) {
            int lo = 0, hi = top.size();                          // find the first index whose value is > val
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (top.get(mid) <= val) lo = mid + 1; else hi = mid;
            }
            top.add(lo, val);
            if (top.size() > k) top.remove(0);                    // drop the smallest: it can never be k-th largest again
            return top.size() < k ? -1 : top.get(0);
        }
    }

    /** Approach 3: min-heap holding the k largest values; its root is the answer. O(log k) per add, O(k) space. */
    static class MinHeapK implements KthStream {
        private final int k;
        private final PriorityQueue<Integer> heap = new PriorityQueue<>();

        MinHeapK(int k, int[] nums) {
            this.k = k;
            for (int x : nums) add(x);
        }

        public int add(int val) {
            if (heap.size() < k) {
                heap.add(val);
            } else if (val > heap.peek()) {                       // val beats the current k-th largest
                heap.poll();
                heap.add(val);
            }
            return heap.size() < k ? -1 : heap.peek();
        }
    }

    /** Feeds the values one at a time and records every answer. */
    static int[] run(KthStream s, int[] adds) {
        int[] out = new int[adds.length];
        for (int i = 0; i < adds.length; i++) out[i] = s.add(adds[i]);
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<KthStream> fresh(int k, int[] nums) {
        return List.<KthStream>of(new SortEveryTime(k, nums), new SortedTopK(k, nums), new MinHeapK(k, nums));
    }

    static void verify(int k, int[] nums, int[] adds, int[] expected) {
        for (KthStream s : fresh(k, nums)) {
            int[] got = run(s, adds);
            check(Arrays.equals(got, expected), s.getClass().getSimpleName() + " got " + Arrays.toString(got)
                    + " expected " + Arrays.toString(expected));
        }
    }

    public static void main(String[] args) {
        verify(3, new int[]{4, 5, 8, 2}, new int[]{3, 5, 10, 9, 4}, new int[]{4, 5, 5, 8, 8});
        verify(4, new int[]{7, 7, 7, 7, 8, 3}, new int[]{2, 10, 9, 9}, new int[]{7, 7, 7, 8});   // duplicates count separately
        verify(1, new int[]{}, new int[]{-3, -2, -4, 0, 4}, new int[]{-3, -2, -2, 0, 4});         // k = 1 is the running maximum
        verify(2, new int[]{0}, new int[]{-1, 1, -2, -4, 3}, new int[]{-1, 0, 0, 0, 1});
        verify(3, new int[]{}, new int[]{5, 1, 2, 7, 6}, new int[]{-1, -1, 1, 2, 5});             // edge: fewer than k values at first
        verify(2, new int[]{Integer.MAX_VALUE}, new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE},
               new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE});                                 // extreme values
        // deterministic random cross-check
        Random rnd = new Random(2838);
        for (int iter = 0; iter < 200; iter++) {
            int k = 1 + rnd.nextInt(6);
            int[] nums = new int[rnd.nextInt(8)], adds = new int[1 + rnd.nextInt(30)];
            for (int i = 0; i < nums.length; i++) nums[i] = rnd.nextInt(41) - 20;
            for (int i = 0; i < adds.length; i++) adds[i] = rnd.nextInt(41) - 20;
            int[] want = run(new SortEveryTime(k, nums), adds);
            check(Arrays.equals(run(new SortedTopK(k, nums), adds), want), "random SortedTopK mismatch");
            check(Arrays.equals(run(new MinHeapK(k, nums), adds), want), "random MinHeapK mismatch");
        }
        System.out.println("OK P2838_KthLargestElementInAStreamOfRunningInteg");
    }
}
