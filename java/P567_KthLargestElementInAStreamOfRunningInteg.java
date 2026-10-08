import java.util.*;

/** TUF 567 - Kth largest element in a stream of running integers. add(val) returns the k-th largest so far, or -1 if fewer than k. */
public class P567_KthLargestElementInAStreamOfRunningInteg {

    interface KthLargest {
        int add(int val);
    }

    /** Approach 1: brute force, keep every number and sort on every add. O(n log n) per add, O(n) space. */
    static class KeepAllSort implements KthLargest {
        private final int k;
        private final List<Integer> all = new ArrayList<>();

        KeepAllSort(int k, int[] nums) {
            this.k = k;
            for (int x : nums) all.add(x);
        }

        public int add(int val) {
            all.add(val);
            if (all.size() < k) return -1;
            all.sort(Collections.reverseOrder());               // largest first
            return all.get(k - 1);
        }
    }

    /** Approach 2: better, keep only the k largest in an ascending list; the answer is its first element. O(k) per add, O(k) space. */
    static class SortedTopK implements KthLargest {
        private final int k;
        private final List<Integer> top = new ArrayList<>();    // ascending, at most k values

        SortedTopK(int k, int[] nums) {
            this.k = k;
            for (int x : nums) insert(x);
        }

        private void insert(int val) {
            if (top.size() == k && val <= top.get(0)) return;   // cannot enter the top k
            int lo = 0, hi = top.size();                        // first index holding a value > val
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (top.get(mid) <= val) lo = mid + 1; else hi = mid;
            }
            top.add(lo, val);
            if (top.size() > k) top.remove(0);                  // drop the old k-th largest
        }

        public int add(int val) {
            insert(val);
            return top.size() < k ? -1 : top.get(0);
        }
    }

    /** Approach 3: optimal, a min-heap holding the k largest; its root is the answer. O(log k) per add, O(k) space. */
    static class MinHeapOfK implements KthLargest {
        private final int k;
        private final PriorityQueue<Integer> heap = new PriorityQueue<>();   // min-heap

        MinHeapOfK(int k, int[] nums) {
            this.k = k;
            for (int x : nums) offer(x);
        }

        private void offer(int val) {
            if (heap.size() < k) heap.offer(val);
            else if (val > heap.peek()) {                       // val beats the current k-th largest
                heap.poll();
                heap.offer(val);
            }
        }

        public int add(int val) {
            offer(val);
            return heap.size() < k ? -1 : heap.peek();
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<KthLargest> fresh(int k, int[] nums) {
        return List.of(new KeepAllSort(k, nums), new SortedTopK(k, nums), new MinHeapOfK(k, nums));
    }

    static void verify(int k, int[] nums, int[] adds, int[] expected) {
        for (KthLargest s : fresh(k, nums)) {
            String name = s.getClass().getSimpleName();
            for (int i = 0; i < adds.length; i++) {
                int got = s.add(adds[i]);
                check(got == expected[i], name + ": add #" + (i + 1) + " returned " + got + ", expected " + expected[i]);
            }
        }
    }

    public static void main(String[] args) {
        verify(3, new int[]{4, 5, 8, 2}, new int[]{3, 5, 10, 9, 4}, new int[]{4, 5, 5, 8, 8});      // LeetCode example
        verify(4, new int[]{}, new int[]{1, 2, 3, 4, 5, 6}, new int[]{-1, -1, -1, 1, 2, 3});         // fewer than k at first
        verify(1, new int[]{}, new int[]{-3, -2, -4, 0, 4}, new int[]{-3, -2, -2, 0, 4});             // k = 1 is the running max
        verify(2, new int[]{5, 5}, new int[]{5, 1, 6}, new int[]{5, 5, 5});                            // duplicates count separately
        verify(3, new int[]{1}, new int[]{2, 3, 0}, new int[]{-1, 1, 1});
        verify(2, new int[]{Integer.MIN_VALUE}, new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE},
               new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE});                                         // extreme values

        // longer seeded stream: all three designs must agree on every answer
        Random rng = new Random(567);
        List<KthLargest> ss = fresh(25, new int[]{});
        for (int i = 0; i < 1500; i++) {
            int x = rng.nextInt(1000);
            int a = ss.get(0).add(x), b = ss.get(1).add(x), c = ss.get(2).add(x);
            check(a == b && b == c, "designs disagree at add " + (i + 1));
        }
        System.out.println("OK P567_KthLargestElementInAStreamOfRunningInteg");
    }
}
