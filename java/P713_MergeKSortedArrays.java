import java.util.*;

/** TUF 713 - Merge K Sorted Arrays (and the Blind 75 linked-list version, Merge k Sorted Lists). */
public class P713_MergeKSortedArrays {

    static class ListNode {
        int val;                                         // the value stored in this node
        ListNode next;                                   // the following node, or null at the end

        ListNode(int val) {
            this.val = val;
        }
    }

    /** Approach 1: copy everything into one array and sort it. O(N log N) time, O(N) space (N = total elements). */
    static int[] bruteForce(int[][] arrays) {
        int total = 0;                                   // size of the merged result
        for (int[] a : arrays) total += a.length;
        int[] out = new int[total];
        int t = 0;                                       // next free slot in out
        for (int[] a : arrays) {                         // copy every array, one after another
            for (int v : a) out[t++] = v;
        }
        Arrays.sort(out);                                // one sort puts everything in order
        return out;
    }

    /** Standard two-way merge of sorted arrays. O(len(a) + len(b)). */
    static int[] mergeTwo(int[] a, int[] b) {
        int[] out = new int[a.length + b.length];        // room for both inputs
        int i = 0, j = 0, t = 0;                         // read heads of a and b, write head of out
        // both have elements left: take the smaller head
        while (i < a.length && j < b.length) out[t++] = a[i] <= b[j] ? a[i++] : b[j++];
        while (i < a.length) out[t++] = a[i++];          // leftovers of a
        while (j < b.length) out[t++] = b[j++];          // leftovers of b
        return out;
    }

    /** Approach 2: divide and conquer, merging halves pairwise like merge sort. O(N log k) time, O(N) extra space. */
    static int[] better(int[][] arrays) {
        if (arrays.length == 0) return new int[0];       // no arrays: empty result
        return mergeRange(arrays, 0, arrays.length - 1);   // merge the whole range of arrays
    }

    static int[] mergeRange(int[][] arrays, int lo, int hi) {
        if (lo == hi) return arrays[lo].clone();         // one array: already sorted, return a copy
        int mid = (lo + hi) >>> 1;                       // split the arrays into two halves
        // merge each half recursively, then merge the two results
        return mergeTwo(mergeRange(arrays, lo, mid), mergeRange(arrays, mid + 1, hi));
    }

    /** Approach 3: min-heap holding the current front of every array. O(N log k) time, O(k) extra space. */
    static int[] optimal(int[][] arrays) {
        int total = 0;                                   // total elements, to size the output
        // heap entries are {value, arrayIndex, elementIndex}
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> Integer.compare(x[0], y[0]));
        for (int r = 0; r < arrays.length; r++) {        // seed the heap with every array's first element
            total += arrays[r].length;
            if (arrays[r].length > 0) pq.offer(new int[]{arrays[r][0], r, 0});   // skip empty arrays
        }
        int[] out = new int[total];
        int t = 0;                                       // next free slot in out
        while (!pq.isEmpty()) {                          // each round takes the smallest remaining front
            int[] top = pq.poll();                       // smallest front value among all arrays
            out[t++] = top[0];                           // it goes next in the output
            int r = top[1], next = top[2] + 1;           // where it came from, and the index after it
            // that array has more: push its next element
            if (next < arrays[r].length) pq.offer(new int[]{arrays[r][next], r, next});
        }
        return out;
    }

    /** Blind 75 variant (LeetCode 23): merge k sorted linked lists with a min-heap of nodes. O(N log k) time, O(k) space. */
    static ListNode mergeKLists(ListNode[] lists) {
        // min-heap of list heads, smallest value first
        PriorityQueue<ListNode> pq = new PriorityQueue<>((x, y) -> Integer.compare(x.val, y.val));
        for (ListNode head : lists) {                    // start with the head of every list
            if (head != null) pq.offer(head);            // skip empty lists
        }
        ListNode dummy = new ListNode(0), tail = dummy;  // dummy sits before the result; tail is its last node
        while (!pq.isEmpty()) {                          // repeatedly take the smallest head
            ListNode node = pq.poll();                   // smallest node among all heads
            tail.next = node;                            // append it to the result
            tail = node;
            if (node.next != null) pq.offer(node.next);  // its successor becomes that list's new head
        }
        return dummy.next;                               // the real head, after the dummy
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static ListNode toList(int[] a) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : a) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        List<Integer> vals = new ArrayList<>();
        for (ListNode c = head; c != null; c = c.next) vals.add(c.val);
        return vals.stream().mapToInt(Integer::intValue).toArray();
    }

    static void verify(int[][] arrays, int[] expected) {
        String in = Arrays.deepToString(arrays);
        check(Arrays.equals(bruteForce(arrays), expected), "bruteForce " + in);
        check(Arrays.equals(better(arrays), expected), "better " + in);
        check(Arrays.equals(optimal(arrays), expected), "optimal " + in);
        ListNode[] lists = new ListNode[arrays.length];
        for (int i = 0; i < arrays.length; i++) lists[i] = toList(arrays[i]);
        check(Arrays.equals(toArray(mergeKLists(lists)), expected), "mergeKLists " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        verify(new int[][]{{1, 3, 5, 7}, {2, 4, 6, 8}, {0, 9, 10, 11}},
               new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
        verify(new int[][]{{1, 4, 5}, {1, 3, 4}, {2, 6}}, new int[]{1, 1, 2, 3, 4, 4, 5, 6}); // LeetCode 23 example
        verify(new int[][]{}, new int[]{});                              // edge: k = 0
        verify(new int[][]{{}, {}}, new int[]{});                        // edge: only empty arrays
        verify(new int[][]{{}, {-1}, {}}, new int[]{-1});                // edge: empties around one element
        verify(new int[][]{{5, 5, 5}}, new int[]{5, 5, 5});              // edge: k = 1, duplicates
        verify(new int[][]{{-10, -3, 0}, {-7, -3, 20}}, new int[]{-10, -7, -3, -3, 0, 20});
        // seeded random inputs, checked against a plain sort
        Random rnd = new Random(713);
        for (int test = 0; test < 200; test++) {
            int k = rnd.nextInt(8);
            int[][] arrays = new int[k][];
            for (int r = 0; r < k; r++) {
                arrays[r] = new int[rnd.nextInt(6)];
                for (int i = 0; i < arrays[r].length; i++) arrays[r][i] = rnd.nextInt(41) - 20;
                Arrays.sort(arrays[r]);
            }
            int[] expected = Arrays.stream(arrays).flatMapToInt(Arrays::stream).sorted().toArray();
            verify(arrays, expected);
        }
        System.out.println("OK P713_MergeKSortedArrays");
    }
}
