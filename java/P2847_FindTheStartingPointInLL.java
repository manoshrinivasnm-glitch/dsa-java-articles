import java.util.*;

/** TUF 2847 - Find the starting point in LL. Return the first node of the loop, or null when the list has no loop. */
public class P2847_FindTheStartingPointInLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Remember every node visited; the first node seen a second time is where the loop starts. O(n) time, O(n) space. */
    static ListNode bruteForce(ListNode head) {
        Set<ListNode> seen = new HashSet<>();
        for (ListNode t = head; t != null; t = t.next) {
            if (!seen.add(t)) return t;        // add returns false for a node object already in the set
        }
        return null;
    }

    /** Approach 2: optimal, Floyd's algorithm. Once slow and fast meet, a pointer from head and the slow pointer, both moving one step, meet at the loop's first node. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {                // a loop exists; now locate its entry
                ListNode entry = head;
                while (entry != slow) {        // both advance one step at a time
                    entry = entry.next;
                    slow = slow.next;
                }
                return entry;
            }
        }
        return null;                           // fast reached the end: no loop
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a list from arr and, when pos >= 0, links the last node back to the node at index pos. */
    static ListNode withLoop(int[] arr, int pos) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : arr) { tail.next = new ListNode(v); tail = tail.next; }
        if (pos >= 0 && arr.length > 0) {
            ListNode target = dummy.next;
            for (int i = 0; i < pos; i++) target = target.next;
            tail.next = target;
        }
        return dummy.next;
    }

    static ListNode nodeAt(ListNode head, int index) {
        ListNode t = head;
        for (int i = 0; i < index; i++) t = t.next;
        return t;
    }

    static String label(int[] arr, int pos) {
        String a = arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
        return a + " loop at " + pos;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int pos) {
        ListNode head = withLoop(arr, pos);
        ListNode expected = (pos >= 0 && arr.length > 0) ? nodeAt(head, pos) : null;
        String in = label(arr, pos);
        check(bruteForce(head) == expected, "bruteForce " + in);
        check(optimal(head) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, -1);        // no loop: answer is null
        verify(new int[]{1, 2, 3, 4, 5}, 0);         // loop starts at the head
        verify(new int[]{1, 2, 3, 4, 5}, 2);         // tail of 2 nodes, loop of 3 nodes
        verify(new int[]{1, 2, 3, 4, 5}, 4);         // last node points to itself
        verify(new int[]{}, -1);                     // edge: empty list
        verify(new int[]{1}, -1);                    // single node, no loop
        verify(new int[]{1}, 0);                     // single node pointing to itself
        verify(new int[]{1, 2}, 0);
        verify(new int[]{1, 2}, 1);
        verify(new int[]{3, 3, 3, 3, 3, 3}, 3);      // equal values: identity, not value, decides

        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big, -1);
        verify(big, 25_000);
        verify(big, 99_999);

        System.out.println("OK P2847_FindTheStartingPointInLL");
    }
}
