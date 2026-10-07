import java.util.*;

/** TUF 1218 - STL. The C++ Standard Template Library mapped onto Java: every container, the algorithms used in contests, and the ones Java lacks written by hand. */
public class P1218_STL {

    /** std::pair<int,int> -> a record. Immutable, compares by value, prints as Pair[first=1, second=2]. */
    record Pair(int first, int second) {}

    // ------------------------------------------------------------ containers

    /** std::vector -> ArrayList: push_back/add, pop_back/remove(size - 1), operator[]/get and set, insert, erase. */
    static List<Integer> vectorDemo() {
        List<Integer> v = new ArrayList<>();         // vector<int> v;
        v.add(1); v.add(2); v.add(3);                // v.push_back(1); ...        -> [1, 2, 3]
        v.remove(v.size() - 1);                      // v.pop_back();              -> [1, 2]
        v.add(0, 0);                                 // v.insert(v.begin(), 0);    -> [0, 1, 2]
        v.set(1, 10);                                // v[1] = 10;                 -> [0, 10, 2]
        v.remove(0);                                 // v.erase(v.begin());        -> [10, 2]
        v.addAll(List.of(7, 8));                     // v.insert(v.end(), {7, 8}); -> [10, 2, 7, 8]
        return v;
    }

    /** std::deque / std::list -> ArrayDeque / LinkedList: O(1) push and pop at both ends. Returns front-to-back order. */
    static List<Integer> dequeDemo() {
        Deque<Integer> dq = new ArrayDeque<>();      // deque<int> dq;
        dq.addLast(1);                               // dq.push_back(1);   -> [1]
        dq.addFirst(2);                              // dq.push_front(2);  -> [2, 1]
        dq.addLast(3);                               // dq.push_back(3);   -> [2, 1, 3]
        dq.addFirst(4);                              // dq.push_front(4);  -> [4, 2, 1, 3]
        dq.removeLast();                             // dq.pop_back();     -> [4, 2, 1]
        dq.removeFirst();                            // dq.pop_front();    -> [2, 1]
        return new ArrayList<>(dq);
    }

    /** std::stack and std::queue -> ArrayDeque used LIFO and FIFO. Returns the stack pops followed by the queue polls. */
    static List<Integer> stackAndQueueDemo() {
        Deque<Integer> st = new ArrayDeque<>();      // stack<int> st;
        Deque<Integer> q = new ArrayDeque<>();       // queue<int> q;
        for (int x = 1; x <= 3; x++) {
            st.push(x);                              // st.push(x);
            q.offer(x);                              // q.push(x);
        }
        List<Integer> out = new ArrayList<>();
        while (!st.isEmpty()) out.add(st.pop());     // st.top(); st.pop();   -> 3, 2, 1
        while (!q.isEmpty()) out.add(q.poll());      // q.front(); q.pop();   -> 1, 2, 3
        return out;
    }

    /** std::priority_queue -> PriorityQueue. C++ defaults to a MAX-heap, Java to a MIN-heap, so pass reverseOrder() to get the C++ behaviour. */
    static List<Integer> priorityQueueDemo(int[] values) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder()); // priority_queue<int>
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();                           // priority_queue<int, vector<int>, greater<int>>
        for (int v : values) {
            maxHeap.offer(v);
            minHeap.offer(v);
        }
        List<Integer> out = new ArrayList<>();
        while (!maxHeap.isEmpty()) out.add(maxHeap.poll());   // largest first
        while (!minHeap.isEmpty()) out.add(minHeap.poll());   // smallest first
        return out;
    }

    /** std::set / unordered_set / multiset -> TreeSet / HashSet / TreeMap of counts. lower_bound and upper_bound become ceiling and higher. */
    static List<String> setDemo(int[] values) {
        TreeSet<Integer> set = new TreeSet<>();                      // set<int>: sorted, unique
        Set<Integer> uset = new HashSet<>();                         // unordered_set<int>: hashed, unique
        TreeMap<Integer, Integer> multiset = new TreeMap<>();        // multiset<int>: sorted, duplicates kept as counts
        for (int v : values) {
            set.add(v);
            uset.add(v);
            multiset.merge(v, 1, Integer::sum);
        }
        List<String> out = new ArrayList<>();
        out.add("set=" + set);
        out.add("unordered_set.size=" + uset.size());
        out.add("multiset=" + multiset);
        out.add("set.lower_bound(4)=" + set.ceiling(4));             // first element >= 4, null when none
        out.add("set.upper_bound(4)=" + set.higher(4));              // first element > 4, null when none
        out.add("multiset.count(4)=" + multiset.getOrDefault(4, 0));
        return out;
    }

    /** std::map / unordered_map -> TreeMap / HashMap. The counting idiom m[w]++ becomes merge(w, 1, Integer::sum). */
    static List<String> mapDemo(String[] words) {
        TreeMap<String, Integer> map = new TreeMap<>();              // map<string, int>: keys kept sorted
        Map<String, Integer> umap = new HashMap<>();                 // unordered_map<string, int>: hashed
        for (String w : words) {
            map.merge(w, 1, Integer::sum);                           // map[w]++;
            umap.merge(w, 1, Integer::sum);
        }
        List<String> out = new ArrayList<>();
        out.add("map=" + map);
        out.add("umap.size=" + umap.size());
        out.add("count(zzz)=" + map.getOrDefault("zzz", 0));         // reads without inserting, unlike C++ operator[]
        out.add("firstKey=" + (map.isEmpty() ? "none" : map.firstKey()));
        return out;
    }

    /** vector<pair<int,int>> sorted the way C++ sorts pairs: by first, then by second. */
    static List<Pair> sortPairs(int[][] raw) {
        List<Pair> pairs = new ArrayList<>();
        for (int[] p : raw) pairs.add(new Pair(p[0], p[1]));
        pairs.sort(Comparator.comparingInt(Pair::first).thenComparingInt(Pair::second));
        return pairs;
    }

    // ------------------------------------------------------------ algorithms

    /** std::sort, reverse, min_element, max_element, accumulate, count, find -> Collections and List helpers. */
    static List<String> algorithmDemo(int[] values) {
        List<Integer> list = new ArrayList<>();
        for (int v : values) list.add(v);
        Collections.sort(list);                                      // sort(v.begin(), v.end());
        String ascending = list.toString();
        list.sort(Collections.reverseOrder());                       // sort(v.rbegin(), v.rend());
        String descending = list.toString();
        Collections.reverse(list);                                   // reverse(v.begin(), v.end());
        long sum = 0;                                                // accumulate(v.begin(), v.end(), 0LL);
        for (int v : list) sum += v;
        List<String> out = new ArrayList<>();
        out.add("asc=" + ascending);
        out.add("desc=" + descending);
        out.add("reversedBack=" + list);
        out.add("min=" + Collections.min(list) + " max=" + Collections.max(list));   // *min_element, *max_element
        out.add("sum=" + sum);
        out.add("count(2)=" + Collections.frequency(list, 2));       // count(v.begin(), v.end(), 2);
        out.add("find(5)=" + list.indexOf(5));                       // find(...) - v.begin(), or -1 when absent
        return out;
    }

    /** std::lower_bound: index of the first element >= x in a sorted array, a.length when there is none. O(log n). */
    static int lowerBound(int[] a, int x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < x) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    /** std::upper_bound: index of the first element > x in a sorted array, a.length when there is none. O(log n). */
    static int upperBound(int[] a, int x) {
        int lo = 0, hi = a.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] <= x) lo = mid + 1; else hi = mid;
        }
        return lo;
    }

    /** std::binary_search -> Arrays.binarySearch, which returns an index when found and a negative insertion point otherwise. */
    static boolean binarySearch(int[] a, int x) {
        return Arrays.binarySearch(a, x) >= 0;
    }

    /** std::next_permutation: rearrange a into the lexicographically next permutation in place. Returns false, leaving a sorted ascending, when a was already the last one. O(n). */
    static boolean nextPermutation(int[] a) {
        int n = a.length;
        int i = n - 2;
        while (i >= 0 && a[i] >= a[i + 1]) i--;      // 1. a[i + 1..] is the longest non-increasing suffix
        if (i < 0) {
            reverse(a, 0, n - 1);                    //    the whole array is descending: wrap around to the first permutation
            return false;
        }
        int j = n - 1;
        while (a[j] <= a[i]) j--;                    // 2. rightmost element larger than a[i]
        swap(a, i, j);                               // 3. put the next larger value at position i
        reverse(a, i + 1, n - 1);                    // 4. make the suffix as small as possible
        return true;
    }

    static void swap(int[] a, int i, int j) {        // std::swap(a[i], a[j])
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    static void reverse(int[] a, int lo, int hi) {   // std::reverse(a + lo, a + hi + 1)
        while (lo < hi) swap(a, lo++, hi--);
    }

    /** __builtin_popcount, __builtin_ctz, __builtin_clz -> Integer.bitCount, numberOfTrailingZeros, numberOfLeadingZeros. */
    static int[] builtins(int x) {
        return new int[]{Integer.bitCount(x), Integer.numberOfTrailingZeros(x), Integer.numberOfLeadingZeros(x)};
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyNextPermutation(int[] start, int[] expected, boolean expectedFlag) {
        int[] a = start.clone();
        boolean flag = nextPermutation(a);
        check(flag == expectedFlag && Arrays.equals(a, expected), "nextPermutation" + Arrays.toString(start) + " = " + Arrays.toString(a));
    }

    public static void main(String[] args) {
        check(vectorDemo().equals(List.of(10, 2, 7, 8)), "vectorDemo");
        check(dequeDemo().equals(List.of(2, 1)), "dequeDemo");
        check(stackAndQueueDemo().equals(List.of(3, 2, 1, 1, 2, 3)), "stackAndQueueDemo");

        check(priorityQueueDemo(new int[]{3, 9, 5}).equals(List.of(9, 5, 3, 3, 5, 9)), "priorityQueueDemo");
        check(priorityQueueDemo(new int[]{4, 4, 1}).equals(List.of(4, 4, 1, 1, 4, 4)), "priorityQueueDemo duplicates");
        check(priorityQueueDemo(new int[]{7}).equals(List.of(7, 7)), "priorityQueueDemo single");
        check(priorityQueueDemo(new int[]{}).isEmpty(), "priorityQueueDemo empty");

        check(setDemo(new int[]{4, 1, 4, 7, 2}).equals(List.of("set=[1, 2, 4, 7]", "unordered_set.size=4", "multiset={1=1, 2=1, 4=2, 7=1}",
                "set.lower_bound(4)=4", "set.upper_bound(4)=7", "multiset.count(4)=2")), "setDemo");
        check(setDemo(new int[]{}).equals(List.of("set=[]", "unordered_set.size=0", "multiset={}",
                "set.lower_bound(4)=null", "set.upper_bound(4)=null", "multiset.count(4)=0")), "setDemo empty");
        check(setDemo(new int[]{9, 8}).get(3).equals("set.lower_bound(4)=8"), "lower_bound when 4 is absent");
        check(setDemo(new int[]{1, 2}).get(4).equals("set.upper_bound(4)=null"), "upper_bound past the end");

        check(mapDemo(new String[]{"b", "a", "b", "c"}).equals(List.of("map={a=1, b=2, c=1}", "umap.size=3", "count(zzz)=0", "firstKey=a")), "mapDemo");
        check(mapDemo(new String[]{}).equals(List.of("map={}", "umap.size=0", "count(zzz)=0", "firstKey=none")), "mapDemo empty");
        check(mapDemo(new String[]{"zzz", "zzz"}).get(2).equals("count(zzz)=2"), "mapDemo count present key");
        check(mapDemo(new String[]{"k"}).get(0).equals("map={k=1}"), "mapDemo single");

        check(sortPairs(new int[][]{{2, 1}, {1, 5}, {1, 2}, {2, 0}}).equals(List.of(new Pair(1, 2), new Pair(1, 5), new Pair(2, 0), new Pair(2, 1))), "sortPairs");
        check(sortPairs(new int[][]{{3, 3}, {3, 3}}).equals(List.of(new Pair(3, 3), new Pair(3, 3))), "sortPairs equal");
        check(sortPairs(new int[][]{{-1, 9}, {-5, 0}}).equals(List.of(new Pair(-5, 0), new Pair(-1, 9))), "sortPairs negatives");
        check(sortPairs(new int[][]{}).isEmpty(), "sortPairs empty");

        check(algorithmDemo(new int[]{3, 1, 2, 2, 5}).equals(List.of("asc=[1, 2, 2, 3, 5]", "desc=[5, 3, 2, 2, 1]", "reversedBack=[1, 2, 2, 3, 5]",
                "min=1 max=5", "sum=13", "count(2)=2", "find(5)=4")), "algorithmDemo");
        check(algorithmDemo(new int[]{7}).equals(List.of("asc=[7]", "desc=[7]", "reversedBack=[7]", "min=7 max=7", "sum=7", "count(2)=0", "find(5)=-1")), "algorithmDemo single");
        check(algorithmDemo(new int[]{2, 2}).get(5).equals("count(2)=2"), "algorithmDemo all equal");
        check(algorithmDemo(new int[]{2_000_000_000, 2_000_000_000}).get(4).equals("sum=4000000000"), "accumulate into a long");

        int[] sorted = {1, 2, 4, 4, 5};
        check(lowerBound(sorted, 4) == 2 && upperBound(sorted, 4) == 4, "bounds of a duplicated value");
        check(lowerBound(sorted, 3) == 2 && upperBound(sorted, 3) == 2, "bounds of an absent value");
        check(lowerBound(sorted, 0) == 0 && upperBound(sorted, 0) == 0, "bounds below everything");
        check(lowerBound(sorted, 6) == 5 && upperBound(sorted, 6) == 5, "bounds above everything");
        check(lowerBound(new int[]{}, 1) == 0 && upperBound(new int[]{}, 1) == 0, "bounds on an empty array");
        check(binarySearch(sorted, 4) && !binarySearch(sorted, 3) && !binarySearch(new int[]{}, 1), "binarySearch");

        verifyNextPermutation(new int[]{1, 2, 3}, new int[]{1, 3, 2}, true);
        verifyNextPermutation(new int[]{1, 3, 2}, new int[]{2, 1, 3}, true);
        verifyNextPermutation(new int[]{3, 2, 1}, new int[]{1, 2, 3}, false);  // last permutation wraps to the first
        verifyNextPermutation(new int[]{1, 1, 5}, new int[]{1, 5, 1}, true);   // duplicates
        verifyNextPermutation(new int[]{1}, new int[]{1}, false);
        verifyNextPermutation(new int[]{}, new int[]{}, false);
        int[] perm = {1, 2, 3, 4};
        int rounds = 1;
        while (nextPermutation(perm)) rounds++;
        check(rounds == 24, "4! permutations visited, got " + rounds);

        check(Arrays.equals(builtins(12), new int[]{2, 2, 28}), "builtins(12)");        // 1100
        check(Arrays.equals(builtins(1), new int[]{1, 0, 31}), "builtins(1)");
        check(Arrays.equals(builtins(0), new int[]{0, 32, 32}), "builtins(0)");
        check(Arrays.equals(builtins(-1), new int[]{32, 0, 0}), "builtins(-1)");
        System.out.println("OK P1218_STL");
    }
}
