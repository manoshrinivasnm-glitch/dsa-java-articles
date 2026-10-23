import java.util.*;

/** TUF 568 - Maximum Sum Combination. The k largest sums a[i] + b[j] over all index pairs, in non-increasing order. */
public class P568_MaximumSumCombination {

    /** Approach 1: build all n*m sums, sort, take the k largest. O(nm log(nm)) time, O(nm) space. */
    static long[] bruteForce(int[] a, int[] b, int k) {
        long[] all = new long[a.length * b.length];      // one slot for every (a[i], b[j]) pair
        int idx = 0;
        for (int x : a) {
            for (int y : b) all[idx++] = (long) x + y;   // long so the sum cannot overflow
        }
        Arrays.sort(all);                                // ascending, so the largest sums sit at the end
        long[] res = new long[k];
        for (int t = 0; t < k; t++) res[t] = all[all.length - 1 - t];   // read the top k from the back
        return res;
    }

    /** Approach 2: stream all sums through a size-k min-heap. O(nm log k) time, O(k) space. */
    static long[] better(int[] a, int[] b, int k) {
        PriorityQueue<Long> pq = new PriorityQueue<>();   // the k largest sums seen so far
        for (int x : a) {
            for (int y : b) {                             // try every pair once
                long s = (long) x + y;
                if (pq.size() < k) {                      // heap not full yet: keep every sum
                    pq.add(s);
                } else if (s > pq.peek()) {               // beats the smallest of the current top k
                    pq.poll();                            // evict it to make room
                    pq.add(s);
                }
            }
        }
        long[] res = new long[pq.size()];
        for (int t = res.length - 1; t >= 0; t--) res[t] = pq.poll();   // smallest comes out first
        return res;
    }

    /** Approach 3: sort both, then expand the best pair's neighbours with a max-heap. O(n log n + m log m + k log k) time. */
    static long[] optimal(int[] a, int[] b, int k) {
        int n = a.length, m = b.length;
        int[] x = a.clone(), y = b.clone();   // sorted copies; the inputs stay untouched
        Arrays.sort(x);
        Arrays.sort(y);
        // max-heap of {sum, i, j}: largest sum first
        PriorityQueue<long[]> pq = new PriorityQueue<>((p, q) -> Long.compare(q[0], p[0]));
        Set<Long> seen = new HashSet<>();     // pair (i, j) encoded as i * m + j
        pq.add(new long[]{(long) x[n - 1] + y[m - 1], n - 1, m - 1});   // the two maxima give the largest sum
        seen.add((long) (n - 1) * m + (m - 1));
        long[] res = new long[k];
        for (int t = 0; t < k; t++) {         // each round pops the next largest sum
            long[] top = pq.poll();
            res[t] = top[0];
            int i = (int) top[1], j = (int) top[2];
            // the next candidates are the two neighbours with one index stepped down; skip pairs already queued
            if (i > 0 && seen.add((long) (i - 1) * m + j)) pq.add(new long[]{(long) x[i - 1] + y[j], i - 1, j});
            if (j > 0 && seen.add((long) i * m + (j - 1))) pq.add(new long[]{(long) x[i] + y[j - 1], i, j - 1});
        }
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int[] b, int k, long[] expected) {
        String tag = Arrays.toString(a) + " " + Arrays.toString(b) + " k=" + k;
        check(Arrays.equals(bruteForce(a, b, k), expected), "bruteForce " + tag + " got " + Arrays.toString(bruteForce(a, b, k)));
        check(Arrays.equals(better(a, b, k), expected), "better " + tag + " got " + Arrays.toString(better(a, b, k)));
        check(Arrays.equals(optimal(a, b, k), expected), "optimal " + tag + " got " + Arrays.toString(optimal(a, b, k)));
    }

    public static void main(String[] args) {
        verify(new int[]{3, 2}, new int[]{1, 4}, 2, new long[]{7, 6});
        verify(new int[]{1, 4, 2, 3}, new int[]{2, 5, 1, 6}, 4, new long[]{10, 9, 9, 8});    // equal sums from different pairs
        verify(new int[]{1, 1}, new int[]{1, 1}, 4, new long[]{2, 2, 2, 2});                // duplicates: every pair counts
        verify(new int[]{-1, -5}, new int[]{-2, 0}, 3, new long[]{-1, -3, -5});            // negatives
        verify(new int[]{4, 9, 1}, new int[]{3, 6}, 6, new long[]{15, 12, 10, 7, 7, 4});    // k = n*m, unequal lengths
        verify(new int[]{5}, new int[]{7}, 1, new long[]{12});                               // edge: single pair
        verify(new int[]{2_000_000_000, 1}, new int[]{2_000_000_000, 0}, 2,
               new long[]{4_000_000_000L, 2_000_000_001L});                                  // needs long
        // deterministic random cross-check
        Random rnd = new Random(568);
        for (int iter = 0; iter < 300; iter++) {
            int n = 1 + rnd.nextInt(8), m = 1 + rnd.nextInt(8), k = 1 + rnd.nextInt(n * m);
            int[] a = new int[n], b = new int[m];
            for (int i = 0; i < n; i++) a[i] = rnd.nextInt(21) - 10;
            for (int j = 0; j < m; j++) b[j] = rnd.nextInt(21) - 10;
            long[] want = bruteForce(a, b, k);
            check(Arrays.equals(better(a, b, k), want) && Arrays.equals(optimal(a, b, k), want), "random mismatch");
        }
        System.out.println("OK P568_MaximumSumCombination");
    }
}
