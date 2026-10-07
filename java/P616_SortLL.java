import java.util.*;

/** TUF 616 - Sort LL. Sort a singly linked list in non-decreasing order and return its head. */
public class P616_SortLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Copy the values into an array, sort the array, write the values back over the nodes. O(n log n) time, O(n) space. */
    static ListNode bruteForce(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        int[] values = new int[n];
        int i = 0;
        for (ListNode t = head; t != null; t = t.next) values[i++] = t.val;
        Arrays.sort(values);
        i = 0;
        for (ListNode t = head; t != null; t = t.next) t.val = values[i++];
        return head;
    }

    /** Approach 2: optimal, top-down merge sort. Split at the middle, sort both halves recursively, merge by re-linking nodes. O(n log n) time, O(log n) stack. */
    static ListNode optimal(ListNode head) {
        if (head == null || head.next == null) return head;   // 0 or 1 node is already sorted
        ListNode mid = lastOfFirstHalf(head);
        ListNode rightHead = mid.next;
        mid.next = null;                                       // cut the list into two independent halves
        ListNode left = optimal(head);
        ListNode right = optimal(rightHead);
        return merge(left, right);
    }

    /** Returns the last node of the first half (for two nodes, the first one), so the list can be cut right after it. */
    static ListNode lastOfFirstHalf(ListNode head) {
        ListNode slow = head, fast = head.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    /** Merges two sorted lists into one sorted list by re-linking nodes. Ties take from a first, which keeps the sort stable. */
    static ListNode merge(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), tail = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) { tail.next = a; a = a.next; }
            else { tail.next = b; b = b.next; }
            tail = tail.next;
        }
        tail.next = (a != null) ? a : b;                       // whatever is left is already sorted
        return dummy.next;
    }

    /** Approach 3: optimal without recursion, bottom-up merge sort. Merge runs of size 1, 2, 4, ... in place. O(n log n) time, O(1) extra space. */
    static ListNode optimalBottomUp(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        ListNode dummy = new ListNode(0, head);
        for (int size = 1; size < n; size *= 2) {
            ListNode prev = dummy, cur = dummy.next;
            while (cur != null) {
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
        if (head == null) return null;
        for (int i = 1; i < size && head.next != null; i++) head = head.next;
        ListNode rest = head.next;
        head.next = null;
        return rest;
    }

    /** Merges sorted runs a and b, links the result after prev, and returns the last node of the merged run. */
    static ListNode mergeAfter(ListNode prev, ListNode a, ListNode b) {
        ListNode tail = prev;
        while (a != null && b != null) {
            if (a.val <= b.val) { tail.next = a; a = a.next; }
            else { tail.next = b; b = b.next; }
            tail = tail.next;
        }
        tail.next = (a != null) ? a : b;
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
