import java.util.*;

/** TUF 547 - Job Sequencing Problem. Unit-time jobs with deadlines and profits; return {jobs done, maximum profit}. */
public class P547_JobSequencingProblem {

    /** Approach 1: try every subset of jobs and keep the most profitable one that can be scheduled. O(2^n * n log n) time, O(n) space. */
    static int[] bruteForce(int[] deadline, int[] profit) {
        int n = deadline.length;
        int bestCount = 0, bestProfit = 0;                         // best schedule found so far
        for (int mask = 0; mask < (1 << n); mask++) {              // each bitmask is one subset of jobs
            List<Integer> chosen = new ArrayList<>();              // deadlines of the jobs in this subset
            int total = 0;                                         // profit of this subset
            for (int i = 0; i < n; i++) {
                if (((mask >> i) & 1) == 1) {                      // job i is in the subset
                    chosen.add(deadline[i]);
                    total += profit[i];
                }
            }
            Collections.sort(chosen);                              // earliest deadline first
            boolean feasible = true;
            for (int k = 0; k < chosen.size(); k++) {
                if (chosen.get(k) < k + 1) {                       // the (k+1)-th job finishes at time k+1
                    feasible = false;                              // that job would miss its deadline
                    break;
                }
            }
            // keep the subset if it can be scheduled and earns more (ties: more jobs)
            if (feasible && (total > bestProfit || (total == bestProfit && chosen.size() > bestCount))) {
                bestProfit = total;
                bestCount = chosen.size();
            }
        }
        return new int[]{bestCount, bestProfit};                   // {jobs done, maximum profit}
    }

    /** Approach 2: highest profit first, each job takes the latest free slot on or before its deadline (linear scan). O(n log n + n * D) time, O(n + D) space. */
    static int[] better(int[] deadline, int[] profit) {
        int n = deadline.length;
        Integer[] order = new Integer[n];                                      // job indices, to be sorted by profit
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(profit[b], profit[a]));   // descending profit
        int maxDeadline = 0;                                                   // the last time slot any job can use
        for (int d : deadline) maxDeadline = Math.max(maxDeadline, d);
        boolean[] used = new boolean[maxDeadline + 1];                         // used[t]: the slot ending at time t is taken
        int count = 0, total = 0;
        for (int i : order) {                                                  // most profitable job first
            for (int t = deadline[i]; t >= 1; t--) {                           // try the latest slot first
                if (!used[t]) {                                                // free slot found: book it for job i
                    used[t] = true;
                    count++;
                    total += profit[i];
                    break;
                }
            }
        }
        return new int[]{count, total};
    }

    /** Approach 3: same greedy, but a disjoint-set structure jumps straight to the latest free slot. O(n log n) time, O(n) space. */
    static int[] optimal(int[] deadline, int[] profit) {
        int n = deadline.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        Arrays.sort(order, (a, b) -> Integer.compare(profit[b], profit[a]));   // descending profit
        int[] parent = new int[n + 1];                                         // find(t) = latest free slot <= t, 0 = none
        for (int t = 0; t <= n; t++) parent[t] = t;                            // every slot starts free
        int count = 0, total = 0;
        for (int i : order) {                                                  // most profitable job first
            int slot = find(parent, Math.min(deadline[i], n));                 // n jobs never need more than n slots
            if (slot > 0) {                                                    // a free slot exists: schedule job i
                parent[slot] = slot - 1;                                       // slot taken: point it at the one before
                count++;
                total += profit[i];
            }
        }
        return new int[]{count, total};
    }

    /** Root of t with path compression, written iteratively so long chains cannot overflow the stack. */
    static int find(int[] parent, int t) {
        int root = t;
        while (parent[root] != root) root = parent[root];        // climb to the latest free slot
        while (parent[t] != root) {                              // path compression: repoint the chain at root
            int next = parent[t];
            parent[t] = root;
            t = next;
        }
        return root;                                             // latest free slot <= t (0 means none)
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] deadline, int[] profit, int expCount, int expProfit) {
        String in = Arrays.toString(deadline) + " " + Arrays.toString(profit);
        int[][] results = {bruteForce(deadline, profit), better(deadline, profit), optimal(deadline, profit)};
        String[] names = {"bruteForce", "better", "optimal"};
        for (int k = 0; k < results.length; k++) {
            check(results[k][0] == expCount && results[k][1] == expProfit,
                  names[k] + " " + in + " -> " + Arrays.toString(results[k]));
        }
    }

    public static void main(String[] args) {
        verify(new int[]{4, 1, 1, 1}, new int[]{20, 10, 40, 30}, 2, 60);
        verify(new int[]{2, 1, 2, 1, 1}, new int[]{100, 19, 27, 25, 15}, 2, 127);
        verify(new int[]{2, 2, 1, 3, 3}, new int[]{10, 20, 30, 40, 50}, 3, 120);
        verify(new int[]{3, 3, 3}, new int[]{1, 2, 3}, 3, 6);                  // every job fits
        verify(new int[]{100, 100}, new int[]{5, 6}, 2, 11);                  // deadlines far larger than n
        verify(new int[]{1}, new int[]{5}, 1, 5);                              // single job
        verify(new int[]{}, new int[]{}, 0, 0);                                // no jobs
        System.out.println("OK P547_JobSequencingProblem");
    }
}
