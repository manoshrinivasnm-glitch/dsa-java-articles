import java.util.*;

/** TUF 622 - Find the starting point in LL. Return the first node of the loop, or null if the list has no loop. */
public class P622_FindTheStartingPointInLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** Approach 1: the first node seen twice while walking is where the loop starts. O(n) time, O(n) space. */
    static ListNode hashing(ListNode head) {
        Set<ListNode> visited = new HashSet<>();
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (!visited.add(cur)) return cur;          // the walk re-enters the loop exactly at its start
        }
        return null;
    }

    /** Approach 2: Floyd's cycle detection, then walk from head and from the meeting point together. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {                         // inside the loop; now locate its entrance
                ListNode entry = head;
                while (entry != slow) {                 // head and meeting point are equally far from the start
                    entry = entry.next;
                    slow = slow.next;
                }
                return entry;
            }
        }
        return null;                                    // fast reached the end: no loop
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a list from values; nodes[i] receives the i-th node. If pos >= 0 the last node links to nodes[pos]. */
    static ListNode build(int[] values, int pos, ListNode[] nodes) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int i = 0; i < values.length; i++) {
            tail.next = new ListNode(values[i]);
            tail = tail.next;
            nodes[i] = tail;
        }
        if (pos >= 0) tail.next = nodes[pos];
        return dummy.next;
    }

    static int[] range(int count) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) a[i] = i;
        return a;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] values, int pos) {
        ListNode[] nodes = new ListNode[values.length];
        ListNode head = build(values, pos, nodes);
        ListNode expected = pos >= 0 ? nodes[pos] : null;
        String in = (values.length <= 10 ? Arrays.toString(values) : values.length + " nodes") + " pos=" + pos;
        check(hashing(head) == expected, "hashing " + in);
        check(optimal(head) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 2, 0, -4}, 1);              // loop starts at the node holding 2
        verify(new int[]{1, 2}, 0);                     // loop starts at the head
        verify(new int[]{1, 2, 3, 4, 5, 6}, 3);
        verify(new int[]{1, 2, 3}, -1);                 // no loop
        verify(new int[]{}, -1);                        // edge: empty list
        verify(new int[]{7}, -1);                       // edge: single node, no loop
        verify(new int[]{7}, 0);                        // edge: single node pointing to itself
        verify(new int[]{5, 5, 5, 5, 5}, 2);            // equal values: identity decides, not value
        verify(range(100_000), 99_999);                 // long tail, loop of length 1
        verify(range(100_000), 0);                      // no tail, loop of length n
        verify(range(100_000), 31_337);
        verify(range(100_000), -1);
        System.out.println("OK P622_FindTheStartingPointInLL");
    }
}
