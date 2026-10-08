import java.util.*;

/** TUF 504 - Course Schedule I. prerequisites[i] = {a, b} means course b must be taken before course a. Can every course be finished? */
public class P504_CourseScheduleI {

    /** Graph with an edge b -> a for every pair {a, b}: "finishing b unlocks a". */
    static List<List<Integer>> buildGraph(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) adj.add(new ArrayList<>());
        for (int[] p : prerequisites) adj.get(p[1]).add(p[0]);
        return adj;
    }

    /** Approach 1: simulate semesters, each time taking every course whose prerequisites are all done. O(V * (V + E)) time, O(V) space. */
    static boolean bruteForce(int numCourses, int[][] prerequisites) {
        boolean[] taken = new boolean[numCourses];
        int takenCount = 0;
        while (takenCount < numCourses) {
            boolean[] blocked = new boolean[numCourses];
            for (int[] p : prerequisites) {
                if (!taken[p[1]]) blocked[p[0]] = true;       // course p[0] still waits for p[1]
            }
            int before = takenCount;
            for (int c = 0; c < numCourses; c++) {
                if (!taken[c] && !blocked[c]) {
                    taken[c] = true;
                    takenCount++;
                }
            }
            if (takenCount == before) return false;           // nothing could be taken this semester: stuck on a cycle
        }
        return true;
    }

    /** Approach 2: DFS with three colours; a course reached again while still on the path closes a cycle. O(V + E) time, O(V) space. */
    static boolean dfsThreeColour(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = buildGraph(numCourses, prerequisites);
        int[] state = new int[numCourses];                    // 0 = unvisited, 1 = on current path, 2 = finished
        for (int c = 0; c < numCourses; c++) {
            if (state[c] == 0 && hasCycle(c, adj, state)) return false;
        }
        return true;
    }

    static boolean hasCycle(int u, List<List<Integer>> adj, int[] state) {
        state[u] = 1;
        for (int v : adj.get(u)) {
            if (state[v] == 1) return true;
            if (state[v] == 0 && hasCycle(v, adj, state)) return true;
        }
        state[u] = 2;
        return false;
    }

    /** Approach 3: Kahn's algorithm; all courses finish exactly when the topological sort removes every node. O(V + E) time, O(V + E) space. */
    static boolean optimal(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = buildGraph(numCourses, prerequisites);
        int[] indegree = new int[numCourses];                 // number of unfinished prerequisites
        for (int[] p : prerequisites) indegree[p[0]]++;
        Deque<Integer> queue = new ArrayDeque<>();
        for (int c = 0; c < numCourses; c++) if (indegree[c] == 0) queue.add(c);
        int finished = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            finished++;
            for (int v : adj.get(u)) {
                if (--indegree[v] == 0) queue.add(v);
            }
        }
        return finished == numCourses;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] pre, boolean expected) {
        String name = "n=" + n + " pre=" + Arrays.deepToString(pre);
        check(bruteForce(n, pre) == expected, "bruteForce " + name);
        check(dfsThreeColour(n, pre) == expected, "dfsThreeColour " + name);
        check(optimal(n, pre) == expected, "optimal " + name);
    }

    public static void main(String[] args) {
        verify(2, new int[][]{{1, 0}}, true);                                     // take 0, then 1
        verify(2, new int[][]{{1, 0}, {0, 1}}, false);                            // each waits for the other
        verify(1, new int[][]{}, true);                                           // single course, no prerequisites
        verify(3, new int[][]{{0, 0}}, false);                                    // a course that requires itself
        verify(5, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}, {4, 3}}, true);     // diamond then a tail
        verify(4, new int[][]{{1, 0}, {2, 1}, {3, 2}, {1, 3}}, false);            // cycle 1 -> 2 -> 3 -> 1 behind course 0
        verify(2, new int[][]{{1, 0}, {1, 0}}, true);                             // duplicate prerequisite pair
        verify(6, new int[][]{{1, 0}, {3, 2}, {4, 3}, {2, 4}}, false);            // independent component holds the cycle
        int n = 3000;
        int[][] chain = new int[n - 1][];
        for (int i = 0; i + 1 < n; i++) chain[i] = new int[]{i + 1, i};
        verify(n, chain, true);                                                   // long chain of prerequisites
        System.out.println("OK P504_CourseScheduleI");
    }
}
