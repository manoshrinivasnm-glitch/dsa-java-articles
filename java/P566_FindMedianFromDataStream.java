import java.util.*;

/** TUF 566 - Find Median from Data Stream. addNum(int) and findMedian() over all numbers added so far. */
public class P566_FindMedianFromDataStream {

    /** Common contract so every implementation can be driven by the same test script. */
    interface MedianFinder {
        void addNum(int num);   // add one number from the stream
        double findMedian();    // median of every number added so far
    }

    /** Approach 1: store everything; sort a copy on every query. add O(1), median O(n log n). */
    static class SortEachQuery implements MedianFinder {
        private final List<Integer> nums = new ArrayList<>();   // every number, in arrival order

        public void addNum(int num) {
            nums.add(num);   // O(1): all the work is left to findMedian
        }

        public double findMedian() {
            int n = nums.size();
            if (n == 0) throw new IllegalStateException("no numbers yet");   // median of nothing is undefined
            int[] a = new int[n];              // copy and sort on every query
            for (int i = 0; i < n; i++) a[i] = nums.get(i);
            Arrays.sort(a);
            if (n % 2 == 1) return a[n / 2];   // odd count: the single middle value
            // even count: average the two middles (long: no overflow)
            return ((long) a[n / 2 - 1] + a[n / 2]) / 2.0;
        }
    }

    /** Approach 2: keep the list sorted with binary-search insertion. add O(n), median O(1). */
    static class SortedInsert implements MedianFinder {
        // every number so far, kept in ascending order
        private final List<Integer> sorted = new ArrayList<>();

        public void addNum(int num) {
            int lo = 0, hi = sorted.size();   // first index whose value is > num
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                // discard the half that cannot hold it
                if (sorted.get(mid) <= num) lo = mid + 1; else hi = mid;
            }
            sorted.add(lo, num);              // insert there so the list stays sorted (shifts later items)
        }

        public double findMedian() {
            int n = sorted.size();
            if (n == 0) throw new IllegalStateException("no numbers yet");   // median of nothing is undefined
            if (n % 2 == 1) return sorted.get(n / 2);   // odd count: the single middle value
            // even count: average the two middles
            return ((long) sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
        }
    }

    /** Approach 3: max-heap for the lower half, min-heap for the upper half. add O(log n), median O(1). */
    static class TwoHeaps implements MedianFinder {
        // lower half of the numbers, as a max-heap: its top is the largest small number
        private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());
        // upper half of the numbers, as a min-heap: its top is the smallest big number
        private final PriorityQueue<Integer> high = new PriorityQueue<>();

        public void addNum(int num) {
            low.add(num);
            high.add(low.poll());                                 // largest of the lower half crosses over
            if (high.size() > low.size()) low.add(high.poll());   // keep low the same size or one bigger
        }

        public double findMedian() {
            // median of nothing is undefined
            if (low.isEmpty()) throw new IllegalStateException("no numbers yet");
            // odd count: the extra number in low is the middle
            if (low.size() > high.size()) return low.peek();
            return ((long) low.peek() + high.peek()) / 2.0;   // even count: average the two middle values
        }
    }

    /** Runs a script: "add" with its value, or "median" which records the current median. */
    static List<Double> simulate(MedianFinder mf, String[] ops, int[] vals) {
        List<Double> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            if (ops[i].equals("add")) mf.addNum(vals[i]);
            else out.add(mf.findMedian());
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<MedianFinder> fresh() {
        return List.<MedianFinder>of(new SortEachQuery(), new SortedInsert(), new TwoHeaps());
    }

    static void verify(String[] ops, int[] vals, List<Double> expected) {
        for (MedianFinder mf : fresh()) {
            List<Double> got = simulate(mf, ops, vals);
            check(got.equals(expected), mf.getClass().getSimpleName() + " got " + got + " expected " + expected);
        }
    }

    /** Adds every value and records the median after each one. */
    static void verifyRunning(int[] vals, double[] expected) {
        String[] ops = new String[2 * vals.length];
        int[] v = new int[2 * vals.length];
        List<Double> exp = new ArrayList<>();
        for (int i = 0; i < vals.length; i++) {
            ops[2 * i] = "add";
            v[2 * i] = vals[i];
            ops[2 * i + 1] = "median";
            exp.add(expected[i]);
        }
        verify(ops, v, exp);
    }

    public static void main(String[] args) {
        verify(new String[]{"add", "add", "median", "add", "median"}, new int[]{1, 2, 0, 3, 0}, List.of(1.5, 2.0));
        verifyRunning(new int[]{5, 15, 1, 3}, new double[]{5, 10, 5, 4});
        verifyRunning(new int[]{-1, -2, -3, -4, -5}, new double[]{-1, -1.5, -2, -2.5, -3});
        verifyRunning(new int[]{6, 10, 2, 6, 5, 0, 6, 3, 1, 0, 0},
                      new double[]{6, 8, 6, 6, 6, 5.5, 6, 5.5, 5, 4, 3});
        verifyRunning(new int[]{4, 4, 4}, new double[]{4, 4, 4});                            // duplicates
        verifyRunning(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE},
                      new double[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE});  // sum would overflow int
        verifyRunning(new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE}, new double[]{Integer.MIN_VALUE, -0.5});
        verifyRunning(new int[]{42}, new double[]{42});                                      // edge: one number
        for (MedianFinder mf : fresh()) {                                                    // edge: no numbers yet
            boolean threw = false;
            try {
                mf.findMedian();
            } catch (IllegalStateException e) {
                threw = true;
            }
            check(threw, mf.getClass().getSimpleName() + " must reject findMedian on an empty stream");
        }
        // deterministic random cross-check
        Random rnd = new Random(566);
        List<MedianFinder> impls = fresh();
        for (int step = 0; step < 2000; step++) {
            int val = rnd.nextInt(201) - 100;
            for (MedianFinder mf : impls) mf.addNum(val);
            double want = impls.get(0).findMedian();
            for (MedianFinder mf : impls) check(mf.findMedian() == want, "random mismatch at step " + step);
        }
        System.out.println("OK P566_FindMedianFromDataStream");
    }
}
