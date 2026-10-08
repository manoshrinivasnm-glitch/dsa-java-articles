import java.util.*;

/** TUF 620 - Detect a loop in LL. Return true if following next pointers from head ever revisits a node. */
public class P620_DetectALoopInLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** Approach 1: remember every visited node in a hash set; a repeat means a loop. O(n) time, O(n) space. */
    static boolean hashing(ListNode head) {
        Set<ListNode> visited = new HashSet<>();
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (!visited.add(cur)) return true;         // add() is false when this node object was seen before
        }
        return false;                                   // reached null, so the list ends
    }

    /** Approach 2: Floyd's tortoise and hare. O(n) time, O(1) space. */
    static boolean optimal(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;                           // 1 step
            fast = fast.next.next;                      // 2 steps
            if (slow == fast) return true;              // the hare caught the tortoise inside the loop
        }
        return false;                                   // the hare found the end of the list
    }

    // ---------------------------------------------------------------- helpers
    /** Builds a list from values; if pos >= 0 the last node points back to the node at index pos. */
    static ListNode build(int[] values, int pos) {
        ListNode dummy = new ListNode(0), tail = dummy;
        ListNode loopTarget = null;
        for (int i = 0; i < values.length; i++) {
            tail.next = new ListNode(values[i]);
            tail = tail.next;
            if (i == pos) loopTarget = tail;
        }
        tail.next = loopTarget;                         // stays null when pos == -1
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

    static void verify(int[] values, int pos, boolean expected) {
        ListNode head = build(values, pos);
        String in = (values.length <= 10 ? Arrays.toString(values) : values.length + " nodes") + " pos=" + pos;
        check(hashing(head) == expected, "hashing " + in);
        check(optimal(head) == expected, "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{3, 2, 0, -4}, 1, true);        // tail links back to the second node
        verify(new int[]{1, 2}, 0, true);               // tail links back to the head
        verify(new int[]{1, 2, 3, 4, 5}, -1, false);    // ordinary list
        verify(new int[]{}, -1, false);                 // edge: empty list
        verify(new int[]{7}, -1, false);                // edge: single node, no loop
        verify(new int[]{7}, 0, true);                  // edge: single node pointing to itself
        verify(new int[]{1, 1, 1, 1}, -1, false);       // equal values do not make a loop
        verify(range(100_000), -1, false);              // long list without a loop
        verify(range(100_000), 99_999, true);           // long list, last node points to itself
        verify(range(100_001), 0, true);                // long list, one big loop
        System.out.println("OK P620_DetectALoopInLL");
    }
}
