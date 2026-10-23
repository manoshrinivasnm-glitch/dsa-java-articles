import java.util.*;

/** TUF 898 - Replace Elements by Their Rank. The smallest value gets rank 1, equal values share a rank, ranks have no gaps. */
public class P898_ReplaceElementsByTheirRank {

    /** Approach 1: for each element, count the distinct values smaller than it. O(n^2) time, O(n) space. */
    static int[] bruteForce(int[] arr) {
        int n = arr.length;
        int[] rank = new int[n];                         // rank[i] = answer for arr[i]
        for (int i = 0; i < n; i++) {                    // rank each element on its own
            Set<Integer> smaller = new HashSet<>();      // distinct values below arr[i]
            for (int x : arr) {
                if (x < arr[i]) smaller.add(x);          // a set ignores repeats, so equal values count once
            }
            rank[i] = smaller.size() + 1;                // rank = 1 + number of distinct smaller values
        }
        return rank;
    }

    /** Approach 2: sort a copy, hand out ranks to distinct values in order, then look each element up. O(n log n) time, O(n) space. */
    static int[] sortAndMap(int[] arr) {
        int[] sorted = arr.clone();                      // sorted copy; arr itself is untouched
        Arrays.sort(sorted);
        Map<Integer, Integer> rankOf = new HashMap<>();  // value -> its rank
        for (int x : sorted) {                           // values in increasing order
            // first time we meet a value: give it the next rank
            if (!rankOf.containsKey(x)) rankOf.put(x, rankOf.size() + 1);
        }
        int[] rank = new int[arr.length];
        for (int i = 0; i < arr.length; i++) rank[i] = rankOf.get(arr[i]);   // look up each element's rank
        return rank;
    }

    /** Approach 3: min-heap of {value, index}; pop in increasing order and move to the next rank whenever the value changes. O(n log n) time, O(n) space. */
    static int[] minHeap(int[] arr) {
        // {value, index} pairs, smallest value first
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> Integer.compare(x[0], y[0]));
        // remember each value's original index
        for (int i = 0; i < arr.length; i++) pq.offer(new int[]{arr[i], i});
        int[] rank = new int[arr.length];
        int current = 0;                                 // rank given to the current value
        long prev = Long.MIN_VALUE;                      // equal to no int, so the first pop opens rank 1
        while (!pq.isEmpty()) {                          // values come out in increasing order
            int[] top = pq.poll();
            if (top[0] != prev) {                        // a new value: move to the next rank
                current++;
                prev = top[0];
            }
            rank[top[1]] = current;                      // write the rank at the original position
        }
        return rank;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int[] expected) {
        int[] copy = arr.clone();
        int[] r1 = bruteForce(arr), r2 = sortAndMap(arr), r3 = minHeap(arr);
        check(Arrays.equals(r1, expected), "bruteForce " + Arrays.toString(arr) + " -> " + Arrays.toString(r1));
        check(Arrays.equals(r2, expected), "sortAndMap " + Arrays.toString(arr) + " -> " + Arrays.toString(r2));
        check(Arrays.equals(r3, expected), "minHeap " + Arrays.toString(arr) + " -> " + Arrays.toString(r3));
        check(Arrays.equals(arr, copy), "input must not be modified");
    }

    public static void main(String[] args) {
        verify(new int[]{20, 15, 26, 2, 98, 6}, new int[]{4, 3, 5, 1, 6, 2});
        verify(new int[]{100, 100, 100}, new int[]{1, 1, 1});                          // all equal
        verify(new int[]{37, 12, 28, 9, 100, 56, 80, 5, 12}, new int[]{5, 3, 4, 2, 8, 6, 7, 1, 3});
        verify(new int[]{}, new int[]{});                                               // edge: empty
        verify(new int[]{42}, new int[]{1});                                            // edge: single element
        verify(new int[]{-5, 0, -5, 7}, new int[]{1, 2, 1, 3});                        // negatives and a tie
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}, new int[]{2, 1});

        // seeded random cross-check: all three must agree; ranks must be 1..distinct with no gaps
        Random rnd = new Random(31);
        for (int t = 0; t < 1000; t++) {
            int[] a = new int[rnd.nextInt(15)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(9) - 4;
            int[] expected = sortAndMap(a);
            verify(a, expected);
            Set<Integer> distinct = new HashSet<>();
            for (int x : a) distinct.add(x);
            Set<Integer> ranks = new HashSet<>();
            for (int r : expected) ranks.add(r);
            for (int r = 1; r <= distinct.size(); r++) check(ranks.contains(r), "rank " + r + " missing");
            check(ranks.size() == distinct.size(), "rank count");
        }

        // large: the two O(n log n) methods agree on a permutation (rank = value + 1)
        int n = 100_000;
        int[] perm = new int[n], want = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = (int) ((long) i * 7919 % n);
            want[i] = perm[i] + 1;
        }
        check(Arrays.equals(sortAndMap(perm), want) && Arrays.equals(minHeap(perm), want), "large permutation");
        System.out.println("OK P898_ReplaceElementsByTheirRank");
    }
}
