import java.util.*;

/** TUF 755 - Minimum Cost to Connect Sticks. Joining sticks x and y costs x + y; join all into one at minimum total cost. */
public class P755_MinimumCostToConnectSticks {

    /** Approach 1: repeatedly scan for the two shortest sticks. O(n^2) time, O(n) space. */
    static long bruteForce(int[] sticks) {
        List<Long> pile = new ArrayList<>();   // sticks still to be joined (long: sums can grow large)
        for (int s : sticks) pile.add((long) s);
        long cost = 0;                         // total paid so far
        while (pile.size() > 1) {              // keep joining until one stick is left
            long a = removeMin(pile), b = removeMin(pile);   // greedily take the two shortest sticks
            cost += a + b;                     // joining costs the sum of their lengths
            pile.add(a + b);                   // the joined stick goes back in the pile
        }
        return cost;
    }

    static long removeMin(List<Long> pile) {
        int best = 0;               // index of the shortest stick seen so far
        for (int i = 1; i < pile.size(); i++) {
            if (pile.get(i) < pile.get(best)) best = i;
        }
        return pile.remove(best);   // remove(int index) returns the removed Long
    }

    /** Approach 2: min-heap; always join the two shortest. O(n log n) time, O(n) space. */
    static long minHeap(int[] sticks) {
        PriorityQueue<Long> pq = new PriorityQueue<>();   // min-heap: shortest stick always on top
        for (int s : sticks) pq.add((long) s);
        long cost = 0;                                    // total paid so far
        while (pq.size() > 1) {                           // keep joining until one stick is left
            long a = pq.poll(), b = pq.poll();            // the two shortest sticks
            cost += a + b;                                // joining costs the sum of their lengths
            pq.add(a + b);                                // the joined stick may be picked again later
        }
        return cost;
    }

    /** Approach 3: sort once, then merge from two queues (sorted sticks, sums in creation order). O(n log n) sort + O(n) merging. */
    static long twoQueues(int[] sticks) {
        int n = sticks.length;
        int[] a = sticks.clone();                // sorted copy of the original sticks
        Arrays.sort(a);
        long[] merged = new long[n];             // joined sticks; created in non-decreasing order
        int i = 0, head = 0, tail = 0;           // a[i..] unused originals, merged[head..tail) unused joins
        long cost = 0;
        for (int step = 1; step < n; step++) {   // n sticks need exactly n - 1 joins
            long sum = 0;                        // length of the stick made in this step
            for (int pick = 0; pick < 2; pick++) {   // take the two shortest available sticks
                // the shortest is the smaller front of the two sorted queues
                if (head == tail || (i < n && a[i] <= merged[head])) sum += a[i++];
                else sum += merged[head++];
            }
            cost += sum;                         // pay for this join
            merged[tail++] = sum;                // sums never decrease, so this queue stays sorted
        }
        return cost;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] sticks, long expected) {
        String tag = Arrays.toString(sticks);
        check(bruteForce(sticks) == expected, "bruteForce " + tag + " got " + bruteForce(sticks));
        check(minHeap(sticks) == expected, "minHeap " + tag + " got " + minHeap(sticks));
        check(twoQueues(sticks) == expected, "twoQueues " + tag + " got " + twoQueues(sticks));
    }

    public static void main(String[] args) {
        verify(new int[]{2, 4, 3}, 14);                        // 2+3=5, 5+4=9
        verify(new int[]{1, 8, 3, 5}, 30);                     // 1+3=4, 4+5=9, 9+8=17
        verify(new int[]{4, 3, 2, 6}, 29);                     // 2+3=5, 4+5=9, 6+9=15
        verify(new int[]{1, 1, 1, 1}, 8);                      // 2 + 2 + 4
        verify(new int[]{3, 3, 3, 3, 3}, 36);                  // 6, 6, 9, 15
        verify(new int[]{5}, 0);                               // edge: already one stick
        verify(new int[]{}, 0);                                // edge: nothing to join
        verify(new int[]{1_000_000_000, 1_000_000_000, 1_000_000_000, 1_000_000_000}, 8_000_000_000L);   // needs long
        // deterministic random cross-check
        Random rnd = new Random(755);
        for (int iter = 0; iter < 300; iter++) {
            int len = rnd.nextInt(25);
            int[] s = new int[len];
            for (int i = 0; i < len; i++) s[i] = 1 + rnd.nextInt(50);
            long want = bruteForce(s);
            check(minHeap(s) == want && twoQueues(s) == want, "random mismatch " + Arrays.toString(s));
        }
        System.out.println("OK P755_MinimumCostToConnectSticks");
    }
}
