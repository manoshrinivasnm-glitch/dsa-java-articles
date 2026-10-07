import java.util.*;

/** TUF 361 - Insert node before head in Doubly Linked List. Return the new head after placing val in front. */
public class P361_InsertNodeBeforeHeadInDoublyLinkedList {

    static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
        Node(int val, Node prev, Node next) { this.val = val; this.prev = prev; this.next = next; }
    }

    /** Insert before the head: the new node's next is the old head, the old head's prev is the new node. O(1) time, O(1) space. */
    static Node insertBeforeHead(Node head, int val) {
        Node node = new Node(val);
        node.next = head;
        if (head != null) head.prev = node;      // the old head now has a predecessor; skip when the list was empty
        return node;
    }

    /** Insert before any node we hold a reference to. Four links change. Returns the (possibly new) head. O(1) time. */
    static Node insertBeforeNode(Node head, Node target, int val) {
        Node before = target.prev;
        Node node = new Node(val, before, target);          // prev = before, next = target
        target.prev = node;
        if (before != null) before.next = node;             // splice in between
        else head = node;                                   // target was the head, so node is the new head
        return head;
    }

    /** Generalisation: insert before the k-th node (1-based). k == 1 is insertion before the head. O(k) time. */
    static Node insertBeforePosition(Node head, int k, int val) {
        if (k < 1) return head;                                               // no such position
        if (k == 1) return insertBeforeHead(head, val);
        Node target = head;
        for (int i = 1; i < k && target != null; i++) target = target.next;  // walk to the k-th node
        if (target == null) return head;                                      // fewer than k nodes
        return insertBeforeNode(head, target, val);
    }

    /** For contrast: insertion at the tail has to find the tail first. O(n) time, O(1) space. */
    static Node insertAtTail(Node head, int val) {
        Node node = new Node(val);
        if (head == null) return node;
        Node tail = head;
        while (tail.next != null) tail = tail.next;
        tail.next = node;
        node.prev = tail;
        return head;
    }

    // ---------------------------------------------------------------- helpers
    static Node fromArray(int[] arr) {
        Node head = null, tail = null;
        for (int v : arr) {
            Node node = new Node(v, tail, null);
            if (tail == null) head = node; else tail.next = node;
            tail = node;
        }
        return head;
    }

    static int[] forward(Node head) {
        List<Integer> out = new ArrayList<>();
        for (Node t = head; t != null; t = t.next) out.add(t.val);
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    static int[] backward(Node head) {
        Node tail = null;
        for (Node t = head; t != null; t = t.next) tail = t;
        List<Integer> out = new ArrayList<>();
        for (Node t = tail; t != null; t = t.prev) out.add(t.val);
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    static boolean isWellFormed(Node head) {
        if (head == null) return true;
        if (head.prev != null) return false;
        for (Node t = head; t != null; t = t.next) {
            if (t.next != null && t.next.prev != t) return false;
        }
        return true;
    }

    static int[] reversed(int[] arr) {
        int[] out = new int[arr.length];
        for (int i = 0; i < arr.length; i++) out[i] = arr[arr.length - 1 - i];
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Every variant must produce the same forward sequence, the mirrored backward sequence, and consistent links. */
    static void checkList(Node head, int[] expected, String msg) {
        check(Arrays.equals(forward(head), expected), msg + ": forward " + Arrays.toString(forward(head)));
        check(Arrays.equals(backward(head), reversed(expected)), msg + ": backward " + Arrays.toString(backward(head)));
        check(isWellFormed(head), msg + ": links inconsistent");
    }

    static void verify(int[] arr, int val) {
        int[] expectedHead = new int[arr.length + 1];
        expectedHead[0] = val;
        System.arraycopy(arr, 0, expectedHead, 1, arr.length);
        int[] expectedTail = Arrays.copyOf(arr, arr.length + 1);
        expectedTail[arr.length] = val;
        String in = Arrays.toString(arr) + " with " + val;
        checkList(insertBeforeHead(fromArray(arr), val), expectedHead, "insertBeforeHead " + in);
        checkList(insertBeforePosition(fromArray(arr), 1, val), expectedHead, "insertBeforePosition(1) " + in);
        if (arr.length > 0) {
            Node head = fromArray(arr);
            checkList(insertBeforeNode(head, head, val), expectedHead, "insertBeforeNode(head) " + in);
        }
        checkList(insertAtTail(fromArray(arr), val), expectedTail, "insertAtTail " + in);
        checkList(insertBeforePosition(fromArray(arr), arr.length + 1, val), arr, "position n+1 does not exist " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 3, 4}, 1);
        verify(new int[]{}, 5);                        // edge: empty list, nothing to re-point
        verify(new int[]{9}, 9);                       // single node, duplicate value
        verify(new int[]{-1, 0, 1}, -7);               // negatives
        verify(new int[]{1, 2, 3, 4, 5, 6}, 0);

        checkList(insertBeforePosition(fromArray(new int[]{1, 2, 4}), 3, 3), new int[]{1, 2, 3, 4}, "insert before the 3rd node");
        checkList(insertBeforePosition(fromArray(new int[]{1, 2}), 0, 7), new int[]{1, 2}, "k = 0 is rejected");
        check(insertBeforePosition(null, 2, 7) == null, "position 2 of an empty list does not exist");

        Node old = fromArray(new int[]{10, 20});
        Node fresh = insertBeforeHead(old, 5);
        check(fresh.next == old && old.prev == fresh && fresh.prev == null, "both links between new and old head are set");

        Node h = null;
        for (int v = 1; v <= 4; v++) h = insertBeforeHead(h, v);
        checkList(h, new int[]{4, 3, 2, 1}, "repeated insertion before the head reverses the order");

        System.out.println("OK P361_InsertNodeBeforeHeadInDoublyLinkedList");
    }
}
