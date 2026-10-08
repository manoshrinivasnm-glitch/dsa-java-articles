import java.util.*;

/** TUF 556 - Hand of Straights. Can the cards be split into groups of groupSize consecutive values? */
public class P556_HandOfStraights {

    /** Approach 1: sort, then build each group by scanning forward for the next value. O(n^2) time, O(n) space. */
    static boolean bruteForce(int[] hand, int groupSize) {
        int n = hand.length;
        if (n % groupSize != 0) return false;
        int[] a = hand.clone();
        Arrays.sort(a);
        boolean[] used = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (used[i]) continue;
            used[i] = true;                      // smallest unused card: it must start a group
            int need = a[i] + 1, j = i + 1;
            for (int taken = 1; taken < groupSize; taken++) {
                while (j < n && (used[j] || a[j] < need)) j++;
                if (j == n || a[j] != need) return false;
                used[j] = true;
                need++;
                j++;
            }
        }
        return true;
    }

    /** Approach 2: count map plus a min-heap of distinct values; always start a group at the heap top. O(n log n) time, O(n) space. */
    static boolean minHeap(int[] hand, int groupSize) {
        if (hand.length % groupSize != 0) return false;
        Map<Integer, Integer> count = new HashMap<>();
        for (int c : hand) count.merge(c, 1, Integer::sum);
        PriorityQueue<Integer> pq = new PriorityQueue<>(count.keySet());
        while (!pq.isEmpty()) {
            int first = pq.peek();
            for (int v = first; v < first + groupSize; v++) {
                int c = count.getOrDefault(v, 0);
                if (c == 0) return false;
                count.put(v, c - 1);
                if (c == 1) {                    // v is used up: it must be the smallest value left
                    if (v != pq.peek()) return false;
                    pq.poll();
                }
            }
        }
        return true;
    }

    /** Approach 3: count map; walk back to the start of each run and open all its groups at once. O(n) expected time, O(n) space. */
    static boolean optimal(int[] hand, int groupSize) {
        if (hand.length % groupSize != 0) return false;
        Map<Integer, Integer> count = new HashMap<>();
        for (int c : hand) count.merge(c, 1, Integer::sum);
        for (int card : hand) {
            int start = card;
            while (count.getOrDefault(start - 1, 0) > 0) start--;   // nothing below start is left
            for (; start <= card; start++) {
                int groups = count.getOrDefault(start, 0);         // every remaining card here opens a group
                if (groups == 0) continue;
                for (int v = start; v < start + groupSize; v++) {
                    int have = count.getOrDefault(v, 0);
                    if (have < groups) return false;
                    count.put(v, have - groups);
                }
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] hand, int groupSize, boolean expected) {
        String tag = Arrays.toString(hand) + " k=" + groupSize;
        check(bruteForce(hand, groupSize) == expected, "bruteForce " + tag);
        check(minHeap(hand, groupSize) == expected, "minHeap " + tag);
        check(optimal(hand, groupSize) == expected, "optimal " + tag);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 6, 2, 3, 4, 7, 8}, 3, true);   // [1,2,3] [2,3,4] [6,7,8]
        verify(new int[]{1, 2, 3, 4, 5}, 4, false);              // 5 cards cannot form groups of 4
        verify(new int[]{1, 1, 2, 2, 3, 3}, 3, true);            // two copies of [1,2,3]
        verify(new int[]{1, 2, 2, 3, 3, 4}, 3, true);            // overlapping runs [1,2,3] [2,3,4]
        verify(new int[]{1, 1, 2, 3}, 2, false);                 // second 1 has no partner 2
        verify(new int[]{1, 2, 4, 5}, 2, true);                  // a gap between groups is fine
        verify(new int[]{8, 10, 12}, 3, false);                  // values not consecutive
        verify(new int[]{5, 1, 3, 2, 4, 6}, 6, true);            // one group of the whole hand
        verify(new int[]{0, 1, 999999999, 1000000000}, 2, true);  // large values, no overflow
        verify(new int[]{0, 0, 1, 1, 2, 2, 999999999, 1000000000}, 2, false);
        verify(new int[]{7}, 1, true);                           // edge: groupSize 1 always works
        verify(new int[]{}, 3, true);                            // edge: empty hand, zero groups
        // deterministic random cross-check
        Random rnd = new Random(556);
        for (int iter = 0; iter < 400; iter++) {
            int len = rnd.nextInt(13), k = 1 + rnd.nextInt(4), range = 1 + rnd.nextInt(7);
            int[] hand = new int[len];
            for (int i = 0; i < len; i++) hand[i] = rnd.nextInt(range);
            boolean want = bruteForce(hand, k);
            check(minHeap(hand, k) == want && optimal(hand, k) == want, "random mismatch " + Arrays.toString(hand) + " k=" + k);
        }
        System.out.println("OK P556_HandOfStraights");
    }
}
