import java.util.*;

/** TUF 511 - Accounts merge. Merge accounts that share at least one email; output name followed by sorted emails. */
public class P511_AccountsMerge {

    /** Approach 1: repeatedly merge any two accounts whose email sets intersect. O(n^3 * k) time, O(n * k) space. */
    static List<List<String>> bruteForce(List<List<String>> accounts) {
        List<String> names = new ArrayList<>();
        List<Set<String>> sets = new ArrayList<>();
        for (List<String> acc : accounts) {
            names.add(acc.get(0));
            sets.add(new HashSet<>(acc.subList(1, acc.size())));
        }
        boolean merged = true;
        while (merged) {                                         // restart the scan after every merge
            merged = false;
            for (int i = 0; i < sets.size() && !merged; i++) {
                for (int j = i + 1; j < sets.size() && !merged; j++) {
                    if (!Collections.disjoint(sets.get(i), sets.get(j))) {
                        sets.get(i).addAll(sets.remove(j));
                        names.remove(j);
                        merged = true;
                    }
                }
            }
        }
        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i < sets.size(); i++) {
            List<String> emails = new ArrayList<>(sets.get(i));
            Collections.sort(emails);
            emails.add(0, names.get(i));
            result.add(emails);
        }
        return result;
    }

    /** Approach 2: graph on emails (star from each account's first email), DFS for components. O(N log N) time, O(N) space, N = total emails. */
    static List<List<String>> dfsEmails(List<List<String>> accounts) {
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, String> owner = new HashMap<>();
        for (List<String> acc : accounts) {
            for (int i = 1; i < acc.size(); i++) {
                String email = acc.get(i);
                graph.computeIfAbsent(email, k -> new ArrayList<>());
                owner.put(email, acc.get(0));
                if (i > 1) {                                     // link every email to the account's first email
                    graph.get(acc.get(1)).add(email);
                    graph.get(email).add(acc.get(1));
                }
            }
        }
        Set<String> visited = new HashSet<>();
        List<List<String>> result = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        for (String start : graph.keySet()) {
            if (!visited.add(start)) continue;
            List<String> group = new ArrayList<>();
            stack.push(start);
            while (!stack.isEmpty()) {
                String e = stack.pop();
                group.add(e);
                for (String next : graph.get(e)) {
                    if (visited.add(next)) stack.push(next);
                }
            }
            Collections.sort(group);
            group.add(0, owner.get(start));
            result.add(group);
        }
        return result;
    }

    /** Disjoint Set Union with union by size and path compression. */
    static final class DisjointSet {
        final int[] parent, size;

        DisjointSet(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);   // path compression
            return parent[x];
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false;                         // already in the same set
            if (size[ra] < size[rb]) {
                int t = ra;
                ra = rb;
                rb = t;
            }
            parent[rb] = ra;                                    // hang the smaller tree under the larger
            size[ra] += size[rb];
            return true;
        }
    }

    /** Approach 3: Disjoint Set over account indices; an email seen before unions the two accounts. O(N log N) time, O(N) space. */
    static List<List<String>> disjointSet(List<List<String>> accounts) {
        int n = accounts.size();
        DisjointSet ds = new DisjointSet(n);
        Map<String, Integer> firstOwner = new HashMap<>();
        for (int i = 0; i < n; i++) {
            List<String> acc = accounts.get(i);
            for (int j = 1; j < acc.size(); j++) {
                Integer prev = firstOwner.putIfAbsent(acc.get(j), i);
                if (prev != null) ds.union(prev, i);            // shared email: same person
            }
        }
        Map<Integer, List<String>> byRoot = new HashMap<>();
        for (Map.Entry<String, Integer> e : firstOwner.entrySet()) {
            byRoot.computeIfAbsent(ds.find(e.getValue()), k -> new ArrayList<>()).add(e.getKey());
        }
        List<List<String>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> e : byRoot.entrySet()) {
            List<String> emails = e.getValue();
            Collections.sort(emails);
            emails.add(0, accounts.get(e.getKey()).get(0));
            result.add(emails);
        }
        return result;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Accounts may come out in any order; emails inside an account must already be sorted. */
    static List<String> normalize(List<List<String>> accs) {
        List<String> out = new ArrayList<>();
        for (List<String> a : accs) out.add(String.join(",", a));
        Collections.sort(out);
        return out;
    }

    static List<List<String>> parse(String... rows) {
        List<List<String>> res = new ArrayList<>();
        for (String r : rows) res.add(new ArrayList<>(Arrays.asList(r.split(","))));
        return res;
    }

    static void verify(List<List<String>> input, List<List<String>> expected) {
        List<String> want = normalize(expected);
        check(normalize(bruteForce(input)).equals(want), "bruteForce wrong: " + normalize(bruteForce(input)));
        check(normalize(dfsEmails(input)).equals(want), "dfsEmails wrong: " + normalize(dfsEmails(input)));
        check(normalize(disjointSet(input)).equals(want), "disjointSet wrong: " + normalize(disjointSet(input)));
    }

    public static void main(String[] args) {
        verify(parse("John,johnsmith@mail.com,john_newyork@mail.com", "John,johnsmith@mail.com,john00@mail.com",
                        "Mary,mary@mail.com", "John,johnnybravo@mail.com"),
                parse("John,john00@mail.com,john_newyork@mail.com,johnsmith@mail.com", "Mary,mary@mail.com",
                        "John,johnnybravo@mail.com"));
        verify(parse("Gabe,Gabe0@m.co,Gabe3@m.co,Gabe1@m.co", "Kevin,Kevin3@m.co,Kevin5@m.co,Kevin0@m.co",
                        "Ethan,Ethan5@m.co,Ethan4@m.co,Ethan0@m.co"),
                parse("Gabe,Gabe0@m.co,Gabe1@m.co,Gabe3@m.co", "Kevin,Kevin0@m.co,Kevin3@m.co,Kevin5@m.co",
                        "Ethan,Ethan0@m.co,Ethan4@m.co,Ethan5@m.co"));
        verify(parse("John,j1@com,j2@com,j3@com", "John,j4@com", "Raj,r1@com,r2@com", "John,j1@com,j5@com",
                        "Raj,r2@com,r3@com", "Mary,m1@com"),
                parse("John,j1@com,j2@com,j3@com,j5@com", "John,j4@com", "Raj,r1@com,r2@com,r3@com", "Mary,m1@com"));
        // transitive merge where the bridging account comes last
        verify(parse("A,a@x,b@x", "A,c@x,d@x", "A,b@x,c@x"), parse("A,a@x,b@x,c@x,d@x"));
        // same name but no shared email: stay separate
        verify(parse("Sam,s1@x", "Sam,s2@x"), parse("Sam,s1@x", "Sam,s2@x"));
        // duplicate email inside one account appears once
        verify(parse("X,x@a,x@a"), parse("X,x@a"));
        // empty input
        verify(new ArrayList<>(), new ArrayList<>());
        System.out.println("OK P511_AccountsMerge");
    }
}
