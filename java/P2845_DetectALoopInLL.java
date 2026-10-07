import java.util.*;

/** TUF 2845 - Detect a loop in LL. Report whether following next pointers from head ever revisits a node. */
public class P2845_DetectALoopInLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Remember every node visited; the first repeat proves a loop. O(n) time, O(n) space. */
    static boolean bruteForce(ListNode head) {
        Set<ListNode> seen = new HashSet<>();
        for (ListNode t = head; t != null; t = t.next) {
            if (!seen.add(t)) return true;     // add returns false when the node object was already in the set
        }
        return false;
    }

    /** Approach 2: optimal, Floyd's tortoise and hare. A slow and a fast pointer meet if and only if there is a loop. O(n) time, O(1) space. */
    static boolean optimal(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;                  // one step
            fast = fast.next.next;             // two steps
            if (slow == fast) return true;     // same node object, not same value
        }
        return false;                          // fast fell off the end, so there is no loop
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
        boolean expected = pos >= 0 && arr.length > 0;
        ListNode head = withLoop(arr, pos);
        String in = label(arr, pos);
        check(bruteForce(head) == expected, "bruteForce " + in);
        check(optimal(head) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, -1);        // plain list, no loop
        verify(new int[]{1, 2, 3, 4, 5}, 0);         // tail points back to head: the whole list is the loop
        verify(new int[]{1, 2, 3, 4, 5}, 2);         // loop of length 3 after a tail of 2
        verify(new int[]{1, 2, 3, 4, 5}, 4);         // last node points to itself
        verify(new int[]{}, -1);                     // edge: empty list
        verify(new int[]{1}, -1);                    // single node, no loop
        verify(new int[]{1}, 0);                     // single node pointing to itself
        verify(new int[]{1, 2}, -1);
        verify(new int[]{1, 2}, 0);
        verify(new int[]{1, 2}, 1);
        verify(new int[]{4, 4, 4, 4}, -1);           // equal values are not a loop

        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big, -1);
        verify(big, 50_000);
        verify(big, 99_999);

        System.out.println("OK P2845_DetectALoopInLL");
    }
}
