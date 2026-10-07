import java.util.*;
import java.util.stream.*;

/** TUF 1217 - Java Collections. A tour of the Java Collections Framework: List, Deque, Set, Map, PriorityQueue, Comparator, Iterator, streams and the classic pitfalls. */
public class P1217_JavaCollections {

    /** A small value type for the sorting demo. Records get equals, hashCode, toString and accessors for free. */
    record Student(String name, int marks) {}

    // ------------------------------------------------------------ List

    /** ArrayList basics: add, insert, set, remove by index versus remove by value, duplicates allowed. */
    static List<Integer> listOperations() {
        List<Integer> list = new ArrayList<>();
        list.add(10);                                // [10]
        list.add(20);                                // [10, 20]
        list.add(30);                                // [10, 20, 30]
        list.add(1, 15);                             // insert at index 1      -> [10, 15, 20, 30]
        list.set(0, 5);                              // overwrite index 0      -> [5, 15, 20, 30]
        list.remove(2);                              // remove by INDEX        -> [5, 15, 30]
        list.remove(Integer.valueOf(15));            // remove by VALUE        -> [5, 30]
        list.add(30);                                // duplicates are allowed -> [5, 30, 30]
        return list;
    }

    /** ArrayDeque as a stack (LIFO) and as a queue (FIFO). Returns the pop order followed by the poll order. */
    static List<Integer> dequeAsStackAndQueue() {
        Deque<Integer> stack = new ArrayDeque<>();
        Deque<Integer> queue = new ArrayDeque<>();
        for (int x = 1; x <= 3; x++) {
            stack.push(x);                           // push adds at the head
            queue.offer(x);                          // offer adds at the tail
        }
        List<Integer> order = new ArrayList<>();
        while (!stack.isEmpty()) order.add(stack.pop());    // 3, 2, 1
        while (!queue.isEmpty()) order.add(queue.poll());   // 1, 2, 3
        return order;
    }

    /** Removing while iterating: the Iterator's own remove is safe; list.remove inside a for-each throws ConcurrentModificationException. */
    static List<Integer> removeEvens(List<Integer> input) {
        List<Integer> list = new ArrayList<>(input);
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) it.remove();
        }
        return list;
    }

    /** The modern one-liner for the same job. */
    static List<Integer> removeEvensWithRemoveIf(List<Integer> input) {
        List<Integer> list = new ArrayList<>(input);
        list.removeIf(v -> v % 2 == 0);
        return list;
    }

    // ------------------------------------------------------------ Set

    /** HashSet removes duplicates, LinkedHashSet keeps insertion order, TreeSet keeps sorted order and answers floor/ceiling/headSet queries. */
    static List<String> setOperations(int[] values, int query) {
        Set<Integer> hash = new HashSet<>();
        Set<Integer> linked = new LinkedHashSet<>();
        TreeSet<Integer> tree = new TreeSet<>();
        for (int v : values) {
            hash.add(v);
            linked.add(v);
            tree.add(v);
        }
        List<String> out = new ArrayList<>();
        out.add("distinct=" + hash.size());
        out.add("insertion=" + linked);
        out.add("sorted=" + tree);
        out.add("floor(" + query + ")=" + tree.floor(query));       // largest element <= query, or null
        out.add("ceiling(" + query + ")=" + tree.ceiling(query));   // smallest element >= query, or null
        out.add("headSet(" + query + ")=" + tree.headSet(query));   // everything strictly below query
        return out;
    }

    // ------------------------------------------------------------ Map

    /** Count word frequencies with merge, then list words from most to least frequent, ties broken alphabetically. */
    static List<String> wordsByFrequency(String[] words) {
        Map<String, Integer> freq = new HashMap<>();
        for (String w : words) freq.merge(w, 1, Integer::sum);     // freq[w]++ in one call
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(freq.entrySet());
        entries.sort((a, b) -> {
            if (!a.getValue().equals(b.getValue())) return b.getValue() - a.getValue();   // higher count first
            return a.getKey().compareTo(b.getKey());                                    // then alphabetical
        });
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, Integer> e : entries) out.add(e.getKey() + "=" + e.getValue());
        return out;
    }

    /** TreeMap keeps keys sorted and offers firstKey/lastKey/floorKey/ceilingKey; HashMap is the O(1) default when order does not matter. */
    static List<String> treeMapOperations(int[] keys, int query) {
        TreeMap<Integer, String> map = new TreeMap<>();
        for (int k : keys) map.put(k, "v" + k);                     // put overwrites an existing key
        List<String> out = new ArrayList<>();
        out.add("size=" + map.size());
        out.add("first=" + (map.isEmpty() ? null : map.firstKey()));
        out.add("last=" + (map.isEmpty() ? null : map.lastKey()));
        out.add("floorKey(" + query + ")=" + map.floorKey(query));
        out.add("ceilingKey(" + query + ")=" + map.ceilingKey(query));
        out.add("get(" + query + ")=" + map.getOrDefault(query, "absent"));
        return out;
    }

    // ------------------------------------------------------------ PriorityQueue

    /** The k largest values using a min-heap of size k, returned in descending order. O(n log k) time, O(k) space. */
    static List<Integer> kLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int v : nums) {
            minHeap.offer(v);
            if (minHeap.size() > k) minHeap.poll();  // evict the smallest so only the k largest survive
        }
        List<Integer> out = new ArrayList<>(minHeap);
        out.sort(Collections.reverseOrder());
        return out;
    }

    // ------------------------------------------------------------ Comparator

    /** Sort by marks descending, then by name ascending, with a chained Comparator. */
    static List<String> rankStudents(List<Student> students) {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingInt(Student::marks).reversed().thenComparing(Student::name));
        List<String> out = new ArrayList<>();
        for (Student s : sorted) out.add(s.name() + ":" + s.marks());
        return out;
    }

    // ------------------------------------------------------------ Streams

    /** Streams: sum, distinct sorted values and a partition count, all in a declarative pipeline. */
    static String streamSummary(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        List<Integer> distinctSorted = Arrays.stream(nums).distinct().sorted().boxed().toList();
        Map<Boolean, Long> evenOdd = Arrays.stream(nums).boxed()
                .collect(Collectors.partitioningBy(v -> v % 2 == 0, Collectors.counting()));
        return "sum=" + sum + " distinct=" + distinctSorted + " even=" + evenOdd.get(true) + " odd=" + evenOdd.get(false);
    }

    // ------------------------------------------------------------ Pitfalls

    /** The facts every Java interviewee gets asked about, recorded as strings so they can be asserted. */
    static List<String> pitfalls() {
        List<String> notes = new ArrayList<>();
        List<Integer> nums = new ArrayList<>(List.of(1, 2, 3));
        nums.remove(1);                                      // index 1 -> [1, 3]
        notes.add("remove(int) -> " + nums);
        nums.remove(Integer.valueOf(1));                     // value 1 -> [3]
        notes.add("remove(Object) -> " + nums);

        List<Integer> fixed = Arrays.asList(1, 2, 3);        // a view over the array: set works, add does not
        fixed.set(0, 9);
        try {
            fixed.add(4);
            notes.add("asList add -> ok");
        } catch (UnsupportedOperationException e) {
            notes.add("asList add -> UnsupportedOperationException");
        }

        List<Integer> immutable = List.of(1, 2, 3);          // fully immutable
        try {
            immutable.set(0, 9);
            notes.add("List.of set -> ok");
        } catch (UnsupportedOperationException e) {
            notes.add("List.of set -> UnsupportedOperationException");
        }

        Integer a = 127, b = 127, c = 1000, d = 1000;
        notes.add("127 == 127 -> " + (a == b));              // true only because small values are cached
        notes.add("1000.equals(1000) -> " + c.equals(d));    // always compare boxed values with equals

        try {
            for (Integer v : nums) {
                if (v == 3) nums.add(4);                     // structural change during a for-each
            }
            notes.add("modify in for-each -> ok");
        } catch (ConcurrentModificationException e) {
            notes.add("modify in for-each -> ConcurrentModificationException");
        }
        return notes;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        check(listOperations().equals(List.of(5, 30, 30)), "listOperations");
        check(dequeAsStackAndQueue().equals(List.of(3, 2, 1, 1, 2, 3)), "dequeAsStackAndQueue");

        List<List<Integer>> evenInputs = List.of(List.of(1, 2, 3, 4, 5, 6), List.of(2, 4, 6), List.of(1, 3), List.<Integer>of());
        List<List<Integer>> evenExpected = List.of(List.of(1, 3, 5), List.<Integer>of(), List.of(1, 3), List.<Integer>of());
        for (int i = 0; i < evenInputs.size(); i++) {
            check(removeEvens(evenInputs.get(i)).equals(evenExpected.get(i)), "removeEvens " + evenInputs.get(i));
            check(removeEvensWithRemoveIf(evenInputs.get(i)).equals(evenExpected.get(i)), "removeIf " + evenInputs.get(i));
        }

        check(setOperations(new int[]{5, 1, 9, 5, 3, 9}, 6).equals(List.of("distinct=4", "insertion=[5, 1, 9, 3]", "sorted=[1, 3, 5, 9]",
                "floor(6)=5", "ceiling(6)=9", "headSet(6)=[1, 3, 5]")), "setOperations");
        check(setOperations(new int[]{}, 0).equals(List.of("distinct=0", "insertion=[]", "sorted=[]",
                "floor(0)=null", "ceiling(0)=null", "headSet(0)=[]")), "setOperations empty");
        check(setOperations(new int[]{4, 4, 4}, 4).equals(List.of("distinct=1", "insertion=[4]", "sorted=[4]",
                "floor(4)=4", "ceiling(4)=4", "headSet(4)=[]")), "setOperations all equal");
        check(setOperations(new int[]{10, 20}, 5).get(3).equals("floor(5)=null"), "floor below the minimum");

        check(wordsByFrequency(new String[]{"b", "a", "b", "c", "a", "b"}).equals(List.of("b=3", "a=2", "c=1")), "wordsByFrequency");
        check(wordsByFrequency(new String[]{"z", "y"}).equals(List.of("y=1", "z=1")), "ties alphabetical");
        check(wordsByFrequency(new String[]{}).isEmpty(), "wordsByFrequency empty");
        check(wordsByFrequency(new String[]{"a", "a", "a"}).equals(List.of("a=3")), "single word");

        check(treeMapOperations(new int[]{30, 10, 20}, 25).equals(List.of("size=3", "first=10", "last=30", "floorKey(25)=20", "ceilingKey(25)=30", "get(25)=absent")), "treeMap");
        check(treeMapOperations(new int[]{30, 10, 20}, 20).equals(List.of("size=3", "first=10", "last=30", "floorKey(20)=20", "ceilingKey(20)=20", "get(20)=v20")), "treeMap hit");
        check(treeMapOperations(new int[]{7, 7}, 1).equals(List.of("size=1", "first=7", "last=7", "floorKey(1)=null", "ceilingKey(1)=7", "get(1)=absent")), "treeMap duplicate key");
        check(treeMapOperations(new int[]{}, 1).equals(List.of("size=0", "first=null", "last=null", "floorKey(1)=null", "ceilingKey(1)=null", "get(1)=absent")), "treeMap empty");

        check(kLargest(new int[]{3, 1, 5, 12, 2, 11}, 3).equals(List.of(12, 11, 5)), "kLargest");
        check(kLargest(new int[]{3, 1, 5}, 5).equals(List.of(5, 3, 1)), "kLargest with k > n");
        check(kLargest(new int[]{3, 1, 5}, 0).isEmpty(), "kLargest with k = 0");
        check(kLargest(new int[]{}, 2).isEmpty(), "kLargest on empty");
        check(kLargest(new int[]{4, 4, 4, 1}, 2).equals(List.of(4, 4)), "kLargest duplicates");

        check(rankStudents(List.of(new Student("Bob", 80), new Student("Alice", 95), new Student("Carl", 80))).equals(List.of("Alice:95", "Bob:80", "Carl:80")), "rankStudents");
        check(rankStudents(List.of(new Student("Zed", 1))).equals(List.of("Zed:1")), "rankStudents single");
        check(rankStudents(List.of()).isEmpty(), "rankStudents empty");
        check(rankStudents(List.of(new Student("B", 50), new Student("A", 50))).equals(List.of("A:50", "B:50")), "rankStudents tie");

        check(streamSummary(new int[]{4, 1, 4, 3, 2}).equals("sum=14 distinct=[1, 2, 3, 4] even=3 odd=2"), "streamSummary");
        check(streamSummary(new int[]{}).equals("sum=0 distinct=[] even=0 odd=0"), "streamSummary empty");
        check(streamSummary(new int[]{7}).equals("sum=7 distinct=[7] even=0 odd=1"), "streamSummary single");
        check(streamSummary(new int[]{-2, -2}).equals("sum=-4 distinct=[-2] even=2 odd=0"), "streamSummary negatives");

        check(pitfalls().equals(List.of("remove(int) -> [1, 3]", "remove(Object) -> [3]",
                "asList add -> UnsupportedOperationException", "List.of set -> UnsupportedOperationException",
                "127 == 127 -> true", "1000.equals(1000) -> true",
                "modify in for-each -> ConcurrentModificationException")), "pitfalls: " + pitfalls());
        System.out.println("OK P1217_JavaCollections");
    }
}
