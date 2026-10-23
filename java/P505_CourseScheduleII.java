import java.util.*;

/** TUF 505 - Course Schedule II. prerequisites[i] = {a, b} means b before a. Return any valid order of all courses, or an empty array if none exists. */
public class P505_CourseScheduleII {

    /** Graph with an edge b -> a for every pair {a, b}. */
    static List<List<Integer>> buildGraph(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();          // adj.get(b) = courses that need b first
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] p : prerequisites) adj.get(p[1]).add(p[0]);   // pair [a, b] means b -> a: take b before a
        return adj;
    }

    /** Approach 1: semester simulation, writing courses down in the order they are taken. O(V * (V + E)) time, O(V) space. */
    static int[] bruteForce(int numCourses, int[][] prerequisites) {
        boolean[] taken = new boolean[numCourses];            // courses already placed in the order
        int[] order = new int[numCourses];
        int count = 0;                                        // number of courses placed so far
        while (count < numCourses) {                          // one round = one semester of free courses
            boolean[] blocked = new boolean[numCourses];      // courses that still wait on something
            for (int[] p : prerequisites) {
                if (!taken[p[1]]) blocked[p[0]] = true;       // a's prerequisite b is not done yet
            }
            int before = count;                               // progress check for this round
            for (int c = 0; c < numCourses; c++) {
                if (!taken[c] && !blocked[c]) {               // free course: append it to the order
                    taken[c] = true;
                    order[count++] = c;
                }
            }
            if (count == before) return new int[0];           // stuck: the rest are on or behind a cycle
        }
        return order;
    }

    /** Approach 2: DFS; a course is written (from the back) only after every course it unlocks. O(V + E) time, O(V) space. */
    static int[] dfsPostorder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = buildGraph(numCourses, prerequisites);
        int[] state = new int[numCourses];                    // 0 = new, 1 = on current path, 2 = done
        int[] order = new int[numCourses];
        int[] slot = {numCourses - 1};                        // next free position, filled right to left
        for (int c = 0; c < numCourses; c++) {
            // a cycle anywhere means no order exists
            if (state[c] == 0 && !place(c, adj, state, order, slot)) return new int[0];
        }
        return order;
    }

    /** Returns false if a cycle is found below u. */
    static boolean place(int u, List<List<Integer>> adj, int[] state, int[] order, int[] slot) {
        state[u] = 1;                                         // u joins the current path
        for (int v : adj.get(u)) {
            if (state[v] == 1) return false;                  // back to a course on the path: cycle
            // unexplored chain below u: a cycle there propagates
            if (state[v] == 0 && !place(v, adj, state, order, slot)) return false;
        }
        state[u] = 2;                                         // u is done
        order[slot[0]--] = u;                                 // u goes before everything placed after it
        return true;
    }

    /** Approach 3: Kahn's algorithm; the dequeue order is a valid schedule. O(V + E) time, O(V + E) space. */
    static int[] optimal(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = buildGraph(numCourses, prerequisites);
        int[] indegree = new int[numCourses];                 // indegree[a] = untaken prerequisites of a
        for (int[] p : prerequisites) indegree[p[0]]++;       // count prerequisites for every course
        Deque<Integer> queue = new ArrayDeque<>();            // courses ready to take now
        // start with courses that need nothing
        for (int c = 0; c < numCourses; c++) if (indegree[c] == 0) queue.add(c);
        int[] order = new int[numCourses];
        int idx = 0;                                          // next free position in the order
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order[idx++] = u;                                 // take u next
            for (int v : adj.get(u)) {
                if (--indegree[v] == 0) queue.add(v);         // last prerequisite done: v becomes ready
            }
        }
        return idx == numCourses ? order : new int[0];        // some courses never freed: cycle, no order
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** True if order is a permutation of 0..n-1 in which every prerequisite comes before the course that needs it. */
    static boolean isValidOrder(int n, int[][] pre, int[] order) {
        if (order.length != n) return false;
        int[] pos = new int[n];
        Arrays.fill(pos, -1);
        for (int i = 0; i < n; i++) {
            if (order[i] < 0 || order[i] >= n || pos[order[i]] != -1) return false;
            pos[order[i]] = i;
        }
        for (int[] p : pre) if (pos[p[1]] > pos[p[0]]) return false;
        return true;
    }

    static void verify(int n, int[][] pre, boolean possible, int[] uniqueOrder) {
        String name = "n=" + n + " pre=" + Arrays.deepToString(pre);
        for (int[] r : new int[][]{bruteForce(n, pre), dfsPostorder(n, pre), optimal(n, pre)}) {
            if (!possible) {
                check(r.length == 0, "expected empty array for " + name + " got " + Arrays.toString(r));
            } else {
                check(isValidOrder(n, pre, r), "invalid order " + Arrays.toString(r) + " for " + name);
                if (uniqueOrder != null) check(Arrays.equals(r, uniqueOrder), "expected " + Arrays.toString(uniqueOrder) + " for " + name);
            }
        }
    }

    public static void main(String[] args) {
        verify(2, new int[][]{{1, 0}}, true, new int[]{0, 1});
        verify(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}, true, null);           // [0,1,2,3] or [0,2,1,3]
        verify(1, new int[][]{}, true, new int[]{0});                                  // single course
        verify(3, new int[][]{}, true, null);                                          // any permutation
        verify(2, new int[][]{{0, 1}, {1, 0}}, false, null);                           // 2-cycle
        verify(3, new int[][]{{1, 0}, {1, 2}, {0, 1}}, false, null);                   // cycle 0 <-> 1
        verify(2, new int[][]{{0, 0}}, false, null);                                   // self prerequisite
        verify(4, new int[][]{{3, 2}, {2, 1}, {1, 0}}, true, new int[]{0, 1, 2, 3});   // chain has exactly one order
        verify(3, new int[][]{{2, 0}, {2, 0}, {1, 2}}, true, new int[]{0, 2, 1});      // duplicate pair
        System.out.println("OK P505_CourseScheduleII");
    }
}
