import java.util.*;

/** TUF 569 - Merge K sorted Lists. Merge k sorted singly linked lists into one sorted list. */
public class P569_MergeKSortedLists {

    static class ListNode {
        int val;                                         // the value stored in this node
        ListNode next;                                   // the following node, or null at the end

        ListNode(int val) {
            this.val = val;
        }
    }

    /** Approach 1: collect every value, sort, and build a new list. O(N log N) time, O(N) space. */
    static ListNode bruteForce(ListNode[] lists) {
        List<Integer> values = new ArrayList<>();        // every value from every list
        for (ListNode head : lists) {                    // each list in turn
            // walk this list and collect its values
            for (ListNode cur = head; cur != null; cur = cur.next) values.add(cur.val);
        }
        Collections.sort(values);                        // one global sort
        ListNode dummy = new ListNode(0), tail = dummy;  // dummy head; tail = last node of the result
        for (int v : values) {
            tail.next = new ListNode(v);                 // append a fresh node with the next value
            tail = tail.next;
        }
        return dummy.next;                               // skip the dummy
    }

    /** Merges two sorted lists by relinking their nodes. O(len(a) + len(b)) time, O(1) space. */
    static ListNode mergeTwo(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), tail = dummy;  // dummy head; tail = last node of the merged list
        while (a != null && b != null) {                 // both lists still have nodes
            if (a.val <= b.val) {                        // take the smaller front; <= keeps it stable
                tail.next = a;                           // link a's front node
                a = a.next;
            } else {
                tail.next = b;                           // link b's front node
                b = b.next;
            }
            tail = tail.next;                            // tail moves to the node just linked
        }
        tail.next = (a != null) ? a : b;                 // append whatever is left of the other list
        return dummy.next;                               // skip the dummy
    }

    /** Approach 2: fold the lists into the answer one at a time. O(N k) time, O(1) extra space. */
    static ListNode better(ListNode[] lists) {
        ListNode result = null;                          // merged list so far (empty at first)
        // merge each list into the running result
        for (ListNode head : lists) result = mergeTwo(result, head);
        return result;
    }

    /** Approach 3: min-heap holding the current front node of every list. O(N log k) time, O(k) space. */
    static ListNode optimalHeap(ListNode[] lists) {
        // min-heap ordered by node value
        PriorityQueue<ListNode> heap = new PriorityQueue<>((x, y) -> Integer.compare(x.val, y.val));
        for (ListNode head : lists) {                      // seed the heap with every list's head
            if (head != null) heap.offer(head);            // empty lists contribute nothing
        }
        ListNode dummy = new ListNode(0), tail = dummy;    // dummy head; tail = last node of the result
        while (!heap.isEmpty()) {                          // until every node has been placed
            ListNode node = heap.poll();                   // smallest front node among all lists
            tail.next = node;                              // append it to the result
            tail = node;
            if (node.next != null) heap.offer(node.next);  // its successor becomes that list's new front
        }
        return dummy.next;                                 // skip the dummy
    }

    /** Approach 4: merge the lists in pairs, halving their number every round. O(N log k) time, O(k) space for the copy of the array. */
    static ListNode optimalDivideAndConquer(ListNode[] lists) {
        if (lists.length == 0) return null;              // no lists: empty result
        ListNode[] work = lists.clone();                 // copy, so the caller's array is not overwritten
        // step = gap between the lists paired this round
        for (int step = 1; step < work.length; step *= 2) {
            for (int i = 0; i + step < work.length; i += 2 * step) {   // pair work[i] with work[i + step]
                work[i] = mergeTwo(work[i], work[i + step]);   // the merged list stays in slot i
            }
        }
        return work[0];                                  // slot 0 holds the full merge
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static ListNode build(int[] values) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : values) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    static ListNode[] buildAll(int[][] lists) {
        ListNode[] heads = new ListNode[lists.length];
        for (int i = 0; i < lists.length; i++) heads[i] = build(lists[i]);
        return heads;
    }

    static List<Integer> toList(ListNode head) {
        List<Integer> out = new ArrayList<>();
        for (ListNode cur = head; cur != null; cur = cur.next) out.add(cur.val);
        return out;
    }

    /** Each approach gets its own fresh lists, because three of them relink the input nodes. */
    static void verify(int[][] lists, List<Integer> expected) {
        List<Integer> r1 = toList(bruteForce(buildAll(lists)));
        List<Integer> r2 = toList(better(buildAll(lists)));
        List<Integer> r3 = toList(optimalHeap(buildAll(lists)));
        List<Integer> r4 = toList(optimalDivideAndConquer(buildAll(lists)));
        check(r1.equals(expected), "bruteForce got " + r1 + " expected " + expected);
        check(r2.equals(expected), "better got " + r2 + " expected " + expected);
        check(r3.equals(expected), "optimalHeap got " + r3 + " expected " + expected);
        check(r4.equals(expected), "optimalDivideAndConquer got " + r4 + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(new int[][]{{1, 4, 5}, {1, 3, 4}, {2, 6}}, List.of(1, 1, 2, 3, 4, 4, 5, 6));
        verify(new int[][]{}, List.of());                                         // edge: no lists at all
        verify(new int[][]{{}}, List.of());                                       // edge: one empty list
        verify(new int[][]{{}, {-1, 5, 11}, {}, {6, 10}}, List.of(-1, 5, 6, 10, 11));
        verify(new int[][]{{2}, {1}, {0}}, List.of(0, 1, 2));
        verify(new int[][]{{5, 5}, {5}, {5, 5, 5}}, List.of(5, 5, 5, 5, 5, 5));  // duplicates across lists
        verify(new int[][]{{1, 2, 3}}, List.of(1, 2, 3));                         // a single list comes back unchanged
        verify(new int[][]{{Integer.MIN_VALUE, 0}, {Integer.MAX_VALUE}, {-1}}, List.of(Integer.MIN_VALUE, -1, 0, Integer.MAX_VALUE));

        // large: 100 lists, list i holds i, i + 100, i + 200, ... so the merge is 0..99999
        int k = 100, per = 1000;
        int[][] many = new int[k][per];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < per; j++) many[i][j] = i + j * k;
        }
        List<Integer> expected = new ArrayList<>();
        for (int v = 0; v < k * per; v++) expected.add(v);
        verify(many, expected);

        // seeded random cross-check against sorting all values
        Random rnd = new Random(23);
        for (int t = 0; t < 500; t++) {
            int[][] lists = new int[rnd.nextInt(7)][];
            List<Integer> all = new ArrayList<>();
            for (int i = 0; i < lists.length; i++) {
                lists[i] = new int[rnd.nextInt(6)];
                for (int j = 0; j < lists[i].length; j++) {
                    lists[i][j] = rnd.nextInt(15) - 5;
                    all.add(lists[i][j]);
                }
                Arrays.sort(lists[i]);
            }
            Collections.sort(all);
            verify(lists, all);
        }
        System.out.println("OK P569_MergeKSortedLists");
    }
}
