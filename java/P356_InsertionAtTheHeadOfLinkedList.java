import java.util.*;

/** TUF 356 - Insertion at the head of Linked List. Return the new head after placing val in front of the list. */
public class P356_InsertionAtTheHeadOfLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Insert at the head: create the node, point it at the old head, and it is the new head. O(1) time, O(1) space. */
    static ListNode insertAtHead(ListNode head, int val) {
        ListNode node = new ListNode(val);
        node.next = head;                // also correct when head is null: the new node is then the only node
        return node;
    }

    /** The same operation as a single expression, using the two-argument constructor. O(1) time, O(1) space. */
    static ListNode insertAtHeadOneLiner(ListNode head, int val) {
        return new ListNode(val, head);
    }

    /** For contrast: insertion at the tail has to walk the whole list to find the last node. O(n) time, O(1) space. */
    static ListNode insertAtTail(ListNode head, int val) {
        ListNode node = new ListNode(val);
        if (head == null) return node;
        ListNode temp = head;
        while (temp.next != null) temp = temp.next;
        temp.next = node;
        return head;
    }

    /** Generalisation: insert before the k-th node (1-based). k == 1 is insertion at the head, k == length + 1 appends. O(k) time. */
    static ListNode insertAtPosition(ListNode head, int k, int val) {
        if (k < 1) return head;                                               // no such position: leave the list alone
        if (k == 1) return new ListNode(val, head);
        ListNode temp = head;
        for (int i = 1; i < k - 1 && temp != null; i++) temp = temp.next;    // stop on node k - 1
        if (temp == null) return head;                                        // fewer than k - 1 nodes: position does not exist
        temp.next = new ListNode(val, temp.next);
        return head;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] arr) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : arr) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        List<Integer> out = new ArrayList<>();
        for (ListNode t = head; t != null; t = t.next) out.add(t.val);
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int val) {
        int[] expectedHead = new int[arr.length + 1];
        expectedHead[0] = val;
        System.arraycopy(arr, 0, expectedHead, 1, arr.length);
        int[] expectedTail = Arrays.copyOf(arr, arr.length + 1);
        expectedTail[arr.length] = val;
        String in = Arrays.toString(arr) + " with " + val;
        check(Arrays.equals(toArray(insertAtHead(fromArray(arr), val)), expectedHead), "insertAtHead " + in);
        check(Arrays.equals(toArray(insertAtHeadOneLiner(fromArray(arr), val)), expectedHead), "insertAtHeadOneLiner " + in);
        check(Arrays.equals(toArray(insertAtPosition(fromArray(arr), 1, val)), expectedHead), "insertAtPosition(1) " + in);
        check(Arrays.equals(toArray(insertAtTail(fromArray(arr), val)), expectedTail), "insertAtTail " + in);
        check(Arrays.equals(toArray(insertAtPosition(fromArray(arr), arr.length + 1, val)), expectedTail), "insertAtPosition(n+1) " + in);
        check(Arrays.equals(toArray(insertAtPosition(fromArray(arr), arr.length + 2, val)), arr), "position past the end is rejected " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 4}, 1);
        verify(new int[]{}, 5);                       // edge: empty list, the new node is the whole list
        verify(new int[]{9}, 9);                      // single node, duplicate value
        verify(new int[]{-1, 0, 1}, -7);              // negatives
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 0);

        check(Arrays.equals(toArray(insertAtPosition(fromArray(new int[]{1, 2, 4}), 3, 3)), new int[]{1, 2, 3, 4}), "insert in the middle");
        check(Arrays.equals(toArray(insertAtPosition(fromArray(new int[]{1, 2}), 0, 7)), new int[]{1, 2}), "k = 0 is rejected");
        check(insertAtPosition(null, 2, 7) == null, "position 2 of an empty list does not exist");

        ListNode old = fromArray(new int[]{10, 20});
        ListNode fresh = insertAtHead(old, 5);
        check(fresh.next == old && old.val == 10 && fresh.val == 5, "the new head links to the old head node itself");

        ListNode h = null;
        for (int v = 1; v <= 5; v++) h = insertAtHead(h, v);
        check(Arrays.equals(toArray(h), new int[]{5, 4, 3, 2, 1}), "repeated head insertion reverses the insertion order");

        System.out.println("OK P356_InsertionAtTheHeadOfLinkedList");
    }
}
