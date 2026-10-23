import java.util.*;

/** TUF 616 - Sort LL. Sort a singly linked list in non-decreasing order and return its head. */
public class P616_SortLL {

    static class ListNode {
        int val;                                         // the value stored in this node
        ListNode next;                                   // the following node, or null at the tail
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Copy the values into an array, sort the array, write the values back over the nodes. O(n log n) time, O(n) space. */
    static ListNode bruteForce(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;                   // count nodes to size the array
        int[] values = new int[n];
        int i = 0;
        for (ListNode t = head; t != null; t = t.next) values[i++] = t.val;   // copy the values out
        Arrays.sort(values);                                                  // sort them in the array
        i = 0;
        for (ListNode t = head; t != null; t = t.next) t.val = values[i++];   // write them back in order
        return head;
    }

    /** Approach 2: optimal, top-down merge sort. Split at the middle, sort both halves recursively, merge by re-linking nodes. O(n log n) time, O(log n) stack. */
    static ListNode optimal(ListNode head) {
        if (head == null || head.next == null) return head;   // 0 or 1 node is already sorted
        ListNode mid = lastOfFirstHalf(head);                  // end of the left half
        ListNode rightHead = mid.next;                         // start of the right half
        mid.next = null;                                       // cut the list into two independent halves
        ListNode left = optimal(head);                         // sort each half on its own
        ListNode right = optimal(rightHead);
        return merge(left, right);                             // combine the two sorted halves
    }

    /** Returns the last node of the first half (for two nodes, the first one), so the list can be cut right after it. */
    static ListNode lastOfFirstHalf(ListNode head) {
        ListNode slow = head, fast = head.next;        // fast starts one ahead so slow stops on the left middle
        while (fast != null && fast.next != null) {    // fast can still take two steps
            slow = slow.next;                          // one step
            fast = fast.next.next;                     // two steps
        }
        return slow;
    }

    /** Merges two sorted lists into one sorted list by re-linking nodes. Ties take from a first, which keeps the sort stable. */
    static ListNode merge(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), tail = dummy;        // dummy anchors the result; tail is its last node
        while (a != null && b != null) {                       // both lists still have nodes
            if (a.val <= b.val) { tail.next = a; a = a.next; } // take the smaller head (a on ties)
            else { tail.next = b; b = b.next; }
            tail = tail.next;                                  // the taken node is the new tail
        }
        tail.next = (a != null) ? a : b;                       // whatever is left is already sorted
        return dummy.next;
    }

    /** Approach 3: optimal without recursion, bottom-up merge sort. Merge runs of size 1, 2, 4, ... in place. O(n log n) time, O(1) extra space. */
    static ListNode optimalBottomUp(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;   // length decides how many rounds we need
        ListNode dummy = new ListNode(0, head);                // stays in front of the head as it changes
        for (int size = 1; size < n; size *= 2) {              // run length doubles each round: 1, 2, 4, ...
            ListNode prev = dummy, cur = dummy.next;           // prev: last merged node, cur: first unmerged
            while (cur != null) {                              // pair up runs until the list is used up
                ListNode left = cur;
                ListNode right = split(left, size);            // left now holds at most size nodes
                cur = split(right, size);                      // right now holds at most size nodes; cur is the rest
                prev = mergeAfter(prev, left, right);          // attach the merged run after prev
            }
        }
        return dummy.next;
    }

    /** Cuts the list after at most size nodes and returns the head of the remainder (possibly null). */
    static ListNode split(ListNode head, int size) {
        if (head == null) return null;                                          // nothing to cut
        for (int i = 1; i < size && head.next != null; i++) head = head.next;   // move to the size-th node
        ListNode rest = head.next;                                              // everything after the cut
        head.next = null;                                                       // end this run here
        return rest;
    }

    /** Merges sorted runs a and b, links the result after prev, and returns the last node of the merged run. */
    static ListNode mergeAfter(ListNode prev, ListNode a, ListNode b) {
        ListNode tail = prev;                                  // merged nodes are appended after prev
        while (a != null && b != null) {                       // both runs still have nodes
            if (a.val <= b.val) { tail.next = a; a = a.next; } // take the smaller head (a on ties)
            else { tail.next = b; b = b.next; }
            tail = tail.next;                                  // the taken node is the new tail
        }
        tail.next = (a != null) ? a : b;                       // attach whatever is left of either run
        while (tail.next != null) tail = tail.next;            // walk to the end of the merged run
        return tail;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] arr) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : arr) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        int[] out = new int[n];
        int i = 0;
        for (ListNode t = head; t != null; t = t.next) out[i++] = t.val;
        return out;
    }

    static List<ListNode> nodes(ListNode head) {
        List<ListNode> out = new ArrayList<>();
        for (ListNode t = head; t != null; t = t.next) out.add(t);
        return out;
    }

    static String label(int[] arr) {
        return arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr) {
        int[] expected = arr.clone();
        Arrays.sort(expected);
        String in = label(arr);
        check(Arrays.equals(toArray(bruteForce(fromArray(arr))), expected), "bruteForce " + in);

        ListNode head = fromArray(arr);
        List<ListNode> before = nodes(head);
        ListNode sorted = optimal(head);
        check(Arrays.equals(toArray(sorted), expected), "optimal " + in);
        List<ListNode> after = nodes(sorted);
        check(after.size() == before.size() && new HashSet<>(after).containsAll(before), "optimal must reuse exactly the original nodes for " + in);

        ListNode head2 = fromArray(arr);
        List<ListNode> before2 = nodes(head2);
        ListNode sorted2 = optimalBottomUp(head2);
        check(Arrays.equals(toArray(sorted2), expected), "optimalBottomUp " + in);
        List<ListNode> after2 = nodes(sorted2);
        check(after2.size() == before2.size() && new HashSet<>(after2).containsAll(before2), "optimalBottomUp must reuse exactly the original nodes for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{4, 2, 1, 3});
        verify(new int[]{-1, 5, 3, 4, 0});
        verify(new int[]{});                                   // edge: empty list
        verify(new int[]{1});                                  // single node
        verify(new int[]{2, 1});                               // two nodes out of order
        verify(new int[]{1, 2, 3, 4, 5});                      // already sorted
        verify(new int[]{5, 4, 3, 2, 1});                      // reverse sorted
        verify(new int[]{3, 3, 1, 1, 2, 2, 3});                // duplicates
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0});   // extremes, no overflow in comparisons

        Random rnd = new Random(42);
        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(2_000_001) - 1_000_000;
        verify(big);
        int[] bigOdd = new int[65_537];                        // not a power of two: uneven runs in the bottom-up pass
        for (int i = 0; i < bigOdd.length; i++) bigOdd[i] = rnd.nextInt(1000);
        verify(bigOdd);

        System.out.println("OK P616_SortLL");
    }
}
