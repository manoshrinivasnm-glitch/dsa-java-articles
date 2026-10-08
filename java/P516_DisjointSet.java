import java.util.*;

/** TUF 516 - Disjoint Set. Process "union u v" and "are u and v connected?" operations on elements 0..n-1. */
public class P516_DisjointSet {

    static final int UNION = 0, QUERY = 1;

    interface UnionFind {
        int find(int x);
        void union(int a, int b);
    }

    /** Plain parent pointers: find walks to the root, union hangs one root under the other. O(n) per operation worst case. */
    static class NaiveDSU implements UnionFind {
        final int[] parent;

        NaiveDSU(int n) {
            parent = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        public int find(int x) {
            while (parent[x] != x) x = parent[x];
            return x;
        }

        public void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra != rb) parent[ra] = rb;
        }
    }

    /** Union by rank + path compression. Amortised O(alpha(n)) per operation. */
    static class DSUByRank implements UnionFind {
        final int[] parent, rank;

        DSUByRank(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        public int find(int x) {
            if (parent[x] == x) return x;
            return parent[x] = find(parent[x]);          // path compression: point straight at the root
        }

        public void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;
            if (rank[ra] < rank[rb]) {
                parent[ra] = rb;
            } else if (rank[ra] > rank[rb]) {
                parent[rb] = ra;
            } else {
                parent[rb] = ra;                         // equal ranks: the new root grows by one
                rank[ra]++;
            }
        }
    }

    /** Union by size + path compression. Same bound, and size[find(x)] is the size of x's set. */
    static class DSUBySize implements UnionFind {
        final int[] parent, size;

        DSUBySize(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        public int find(int x) {
            if (parent[x] == x) return x;
            return parent[x] = find(parent[x]);
        }

        public void union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return;
            if (size[ra] < size[rb]) {                   // hang the smaller tree under the larger one
                parent[ra] = rb;
                size[rb] += size[ra];
            } else {
                parent[rb] = ra;
                size[ra] += size[rb];
            }
        }
    }

    /** Shared driver: apply operations {UNION, u, v} / {QUERY, u, v} and collect query answers. */
    static List<Boolean> run(UnionFind dsu, int[][] ops) {
        List<Boolean> answers = new ArrayList<>();
        for (int[] op : ops) {
            if (op[0] == UNION) dsu.union(op[1], op[2]);
            else answers.add(dsu.find(op[1]) == dsu.find(op[2]));
        }
        return answers;
    }

    /** Approach 1: naive parent array. */
    static List<Boolean> naive(int n, int[][] ops) {
        return run(new NaiveDSU(n), ops);
    }

    /** Approach 2: union by rank with path compression. */
    static List<Boolean> byRank(int n, int[][] ops) {
        return run(new DSUByRank(n), ops);
    }

    /** Approach 3: union by size with path compression. */
    static List<Boolean> bySize(int n, int[][] ops) {
        return run(new DSUBySize(n), ops);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, int[][] ops, List<Boolean> expected) {
        List<Boolean> a = naive(n, ops), b = byRank(n, ops), c = bySize(n, ops);
        check(a.equals(expected) && b.equals(expected) && c.equals(expected),
                "expected " + expected + " got " + a + " " + b + " " + c);
    }

    public static void main(String[] args) {
        int[][] ops = {{UNION, 1, 2}, {UNION, 2, 3}, {UNION, 4, 5}, {UNION, 6, 7}, {UNION, 5, 6},
                       {QUERY, 3, 7}, {UNION, 3, 7}, {QUERY, 3, 7}, {QUERY, 1, 4}};
        verify(8, ops, List.of(false, true, true));
        verify(1, new int[][]{{QUERY, 0, 0}, {UNION, 0, 0}, {QUERY, 0, 0}}, List.of(true, true));   // single element, self-union
        verify(4, new int[][]{{QUERY, 0, 1}, {QUERY, 2, 3}}, List.of(false, false));                  // no unions at all
        verify(5, new int[][]{{UNION, 0, 1}, {UNION, 1, 0}, {UNION, 3, 4}, {QUERY, 1, 0}, {QUERY, 0, 4}, {UNION, 1, 4}, {QUERY, 0, 3}},
               List.of(true, false, true));                                                             // repeated union is harmless

        // Long chain 0-1-2-...: the naive tree degenerates into a path, the optimised ones stay shallow.
        int n = 2000;
        List<int[]> chain = new ArrayList<>();
        List<Boolean> expected = new ArrayList<>();
        for (int i = 0; i + 1 < n; i++) chain.add(new int[]{UNION, i, i + 1});
        for (int i = 0; i < n; i++) {
            chain.add(new int[]{QUERY, 0, i});
            expected.add(true);
        }
        verify(n, chain.toArray(new int[0][]), expected);

        DSUByRank r = new DSUByRank(1 << 10);            // rank never exceeds log2(n)
        for (int step = 1; step < (1 << 10); step <<= 1)
            for (int i = 0; i + step < (1 << 10); i += 2 * step) r.union(i, i + step);
        check(r.rank[r.find(0)] == 10, "perfectly balanced merges reach rank log2(1024) = 10");
        DSUBySize z = new DSUBySize(6);
        z.union(0, 1); z.union(1, 2); z.union(4, 5);
        check(z.size[z.find(2)] == 3 && z.size[z.find(4)] == 2 && z.size[z.find(3)] == 1, "component sizes");

        Random rnd = new Random(516);                    // seeded cross-check on random operation sequences
        for (int t = 0; t < 200; t++) {
            int m = 1 + rnd.nextInt(12);
            int[][] rops = new int[30][];
            for (int i = 0; i < rops.length; i++) rops[i] = new int[]{rnd.nextInt(2), rnd.nextInt(m), rnd.nextInt(m)};
            verify(m, rops, naive(m, rops));
        }
        System.out.println("OK P516_DisjointSet");
    }
}
