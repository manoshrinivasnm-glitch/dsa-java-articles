import java.util.*;

/** TUF 446 - Find Median in a Stream. Support addNum(int) and findMedian() over all numbers added so far. */
public class P446_FindMedianInAStream {

    interface MedianFinder {
        void addNum(int num);
        double findMedian();
    }

    /** Approach 1: brute force, keep everything unsorted and sort a copy on every query. add O(1), query O(n log n). */
    static class SortOnQuery implements MedianFinder {
        private final List<Integer> nums = new ArrayList<>();

        public void addNum(int num) {
            nums.add(num);
        }

        public double findMedian() {
            int n = nums.size();
            if (n == 0) throw new IllegalStateException("stream is empty");
            int[] a = new int[n];
            for (int i = 0; i < n; i++) a[i] = nums.get(i);
            Arrays.sort(a);
            if (n % 2 == 1) return a[n / 2];
            return ((long) a[n / 2 - 1] + a[n / 2]) / 2.0;      // long: the sum can overflow int
        }
    }

    /** Approach 2: better, keep the list sorted by inserting at the binary-searched position. add O(n), query O(1). */
    static class SortedInsert implements MedianFinder {
        private final List<Integer> sorted = new ArrayList<>();

        public void addNum(int num) {
            int lo = 0, hi = sorted.size();                     // find the first index holding a value > num
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (sorted.get(mid) <= num) lo = mid + 1; else hi = mid;
            }
            sorted.add(lo, num);                                // shifts the tail one step right: O(n)
        }

        public double findMedian() {
            int n = sorted.size();
            if (n == 0) throw new IllegalStateException("stream is empty");
            if (n % 2 == 1) return sorted.get(n / 2);
            return ((long) sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
        }
    }

    /** Approach 3: optimal, a max-heap for the smaller half and a min-heap for the larger half. add O(log n), query O(1). */
    static class TwoHeaps implements MedianFinder {
        private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder()); // smaller half, max on top
        private final PriorityQueue<Integer> high = new PriorityQueue<>();                          // larger half, min on top

        public void addNum(int num) {
            if (low.isEmpty() || num <= low.peek()) low.offer(num); else high.offer(num);
            if (low.size() > high.size() + 1) high.offer(low.poll());     // rebalance: low may hold one extra
            else if (high.size() > low.size()) low.offer(high.poll());
        }

        public double findMedian() {
            if (low.isEmpty()) throw new IllegalStateException("stream is empty");
            if (low.size() > high.size()) return low.peek();
            return ((long) low.peek() + high.peek()) / 2.0;
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<MedianFinder> fresh() {
        return List.of(new SortOnQuery(), new SortedInsert(), new TwoHeaps());
    }

    /** Adds the numbers one by one and compares the median after every add. */
    static void verify(int[] stream, double[] expected) {
        for (MedianFinder f : fresh()) {
            String name = f.getClass().getSimpleName();
            for (int i = 0; i < stream.length; i++) {
                f.addNum(stream[i]);
                double got = f.findMedian();
                check(got == expected[i], name + ": after " + (i + 1) + " adds got " + got + ", expected " + expected[i]);
            }
        }
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, new double[]{1.0, 1.5, 2.0});
        verify(new int[]{5, 15, 1, 3}, new double[]{5.0, 10.0, 5.0, 4.0});
        verify(new int[]{2, 2, 2, 2}, new double[]{2.0, 2.0, 2.0, 2.0});                     // duplicates
        verify(new int[]{-1, -2, -3, -4, -5}, new double[]{-1.0, -1.5, -2.0, -2.5, -3.0});    // descending negatives
        verify(new int[]{6, 10, 2, 6, 5, 0, 6, 3, 1, 0, 0},
               new double[]{6.0, 8.0, 6.0, 6.0, 6.0, 5.5, 6.0, 5.5, 5.0, 4.0, 3.0});
        verify(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}, new double[]{2147483647.0, 2147483647.0}); // int sum would overflow
        verify(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, new double[]{-2147483648.0, -0.5});

        for (MedianFinder f : fresh()) {                     // edge case: querying an empty stream
            boolean threw = false;
            try { f.findMedian(); } catch (IllegalStateException e) { threw = true; }
            check(threw, f.getClass().getSimpleName() + ": empty stream must throw");
        }

        // longer stream from a fixed seed: all three designs must agree on every prefix
        Random rng = new Random(446);
        List<MedianFinder> fs = fresh();
        for (int i = 0; i < 2000; i++) {
            int x = rng.nextInt(2001) - 1000;
            for (MedianFinder f : fs) f.addNum(x);
            double m = fs.get(0).findMedian();
            for (MedianFinder f : fs) check(f.findMedian() == m, "designs disagree after " + (i + 1) + " adds");
        }
        System.out.println("OK P446_FindMedianInAStream");
    }
}
