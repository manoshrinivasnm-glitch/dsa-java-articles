import java.util.*;

/** TUF 623 - Length of loop in LL. Return the number of nodes on the loop, or 0 when the list has no loop. */
public class P623_LengthOfLoopInLL {

    static class ListNode {
        int val;                                         // the value stored in this node
        ListNode next;                                   // the following node; a loop points back to an earlier one
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Store the step number at which each node was first seen; at the first repeat, the difference of step numbers is the loop length. O(n) time, O(n) space. */
    static int bruteForce(ListNode head) {
        Map<ListNode, Integer> firstSeen = new HashMap<>();   // node -> step at which we first reached it
        int step = 0;                                         // how many moves we have made from head
        for (ListNode t = head; t != null; t = t.next, step++) {
            Integer earlier = firstSeen.get(t);               // null if this is the first visit
            if (earlier != null) return step - earlier;   // nodes visited between the two visits form the loop
            firstSeen.put(t, step);
        }
        return 0;                                             // reached null: no loop
    }

    /** Approach 2: optimal. Detect the loop with slow and fast pointers, then walk once around the loop from the meeting point and count. O(n) time, O(1) space. */
    static int optimal(ListNode head) {
        ListNode slow = head, fast = head;               // both start at the head
        while (fast != null && fast.next != null) {      // fast can still take two steps
            slow = slow.next;                            // one step
            fast = fast.next.next;                       // two steps
            if (slow == fast) return loopLength(slow);   // the meeting node is somewhere on the loop
        }
        return 0;                                        // fast fell off the end: no loop
    }

    /** Counts the nodes on the loop that contains onLoop: step forward until we are back where we started. */
    static int loopLength(ListNode onLoop) {
        int length = 1;                                                       // counts onLoop itself
        for (ListNode t = onLoop.next; t != onLoop; t = t.next) length++;     // one lap around the loop
        return length;
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
        int expected = (pos >= 0 && arr.length > 0) ? arr.length - pos : 0;
        String in = label(arr, pos);
        check(bruteForce(head) == expected, "bruteForce " + in + " expected " + expected);
        check(optimal(head) == expected, "optimal " + in + " expected " + expected);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, -1);        // no loop: 0
        verify(new int[]{1, 2, 3, 4, 5}, 0);         // whole list is the loop: 5
        verify(new int[]{1, 2, 3, 4, 5}, 2);         // loop 3 -> 4 -> 5 -> 3: 3
        verify(new int[]{1, 2, 3, 4, 5}, 4);         // self-loop on the last node: 1
        verify(new int[]{}, -1);                     // edge: empty list
        verify(new int[]{1}, -1);                    // single node, no loop
        verify(new int[]{1}, 0);                     // single node pointing to itself: 1
        verify(new int[]{1, 2}, 0);                  // 2
        verify(new int[]{1, 2}, 1);                  // 1
        verify(new int[]{9, 9, 9, 9, 9, 9, 9}, 3);   // equal values: 4

        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big, -1);
        verify(big, 25_000);                         // loop of 75000 nodes
        verify(big, 99_999);

        System.out.println("OK P623_LengthOfLoopInLL");
    }
}
