import java.util.*;

/** TUF 523 - Minimum multiplications to reach end. One step: start = (start * arr[i]) % 100000. Fewest steps to reach end, or -1. */
public class P523_MinimumMultiplicationsToReachEnd {

    static final int MOD = 100_000;

    /** Approach 1: Dijkstra on the 100000 residues, every multiplication is an edge of weight 1. O(MOD * m * log(MOD * m)) time. */
    static int dijkstra(int[] arr, int start, int end) {
        int[] dist = new int[MOD];                       // dist[x] = fewest steps known to reach value x
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;                                 // the start value needs 0 steps
        // min-heap: the value with the fewest steps comes out first
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, start});                   // {steps, value}
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();                       // value with the fewest steps so far
            int steps = cur[0], value = cur[1];
            if (value == end) return steps;              // first time end leaves the heap, its distance is final
            if (steps > dist[value]) continue;           // stale entry
            for (int m : arr) {                          // each multiplier is one possible step
                int next = (int) ((long) value * m % MOD);   // long: value * m can reach about 10^10
                if (steps + 1 < dist[next]) {            // found a shorter way to next
                    dist[next] = steps + 1;
                    pq.offer(new int[]{steps + 1, next});   // queue next with its new step count
                }
            }
        }
        return -1;                                       // end can never be produced
    }

    /** Approach 2: plain BFS, because unit weights make a FIFO queue already ordered by steps. O(MOD * m) time, O(MOD) space. */
    static int bfs(int[] arr, int start, int end) {
        if (start == end) return 0;                      // already there: no steps needed
        int[] dist = new int[MOD];                       // dist[x] = steps to reach value x
        Arrays.fill(dist, -1);                           // -1 = not reached yet
        dist[start] = 0;                                 // the start value needs 0 steps
        Deque<Integer> queue = new ArrayDeque<>();       // values whose neighbours are still to explore
        queue.offer(start);
        while (!queue.isEmpty()) {
            int value = queue.poll();                    // values come out in order of steps
            for (int m : arr) {                          // each multiplier is one possible step
                int next = (int) ((long) value * m % MOD);   // value after one more step
                if (dist[next] == -1) {                  // first visit to next
                    dist[next] = dist[value] + 1;        // one step more than value
                    if (next == end) return dist[next];  // the first discovery is the shortest
                    queue.offer(next);                   // explore from next later
                }
            }
        }
        return -1;                                       // end can never be produced
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int start, int end, int expected) {
        int a = dijkstra(arr, start, end), b = bfs(arr, start, end);
        check(a == expected && b == expected,
                Arrays.toString(arr) + " " + start + " -> " + end + ": expected " + expected + " got " + a + " " + b);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 5, 7}, 3, 30, 2);            // 3 * 2 = 6, 6 * 5 = 30
        verify(new int[]{3, 4, 65}, 7, 66175, 4);        // 7 -> 21 -> 63 -> 4095 -> 266175 % 100000
        verify(new int[]{2, 5, 7}, 42, 42, 0);           // already there
        verify(new int[]{2}, 1, 3, -1);                  // powers of 2 are never odd again
        verify(new int[]{1}, 5, 7, -1);                  // multiplying by 1 never moves
        verify(new int[]{99999}, 99999, 1, 1);           // 99999 * 99999 overflows int; mod 100000 it is 1
        verify(new int[]{10, 100000}, 7, 0, 1);          // multiplier 100000 jumps straight to 0
        verify(new int[]{10}, 7, 0, 5);                  // 70, 700, 7000, 70000, 700000 % 100000 = 0
        System.out.println("OK P523_MinimumMultiplicationsToReachEnd");
    }
}
