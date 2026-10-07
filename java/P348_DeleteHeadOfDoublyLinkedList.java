import java.util.*;

/** TUF 348 - Delete head of Doubly Linked List. Return the new head after removing the first node. */
public class P348_DeleteHeadOfDoublyLinkedList {

    static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
        Node(int val, Node prev, Node next) { this.val = val; this.prev = prev; this.next = next; }
    }

    /** Delete the head: the second node becomes the head and its prev is cleared. O(1) time, O(1) space. */
    static Node deleteHead(Node head) {
        if (head == null || head.next == null) return null;    // empty or single node: the list becomes empty
        Node newHead = head.next;
        newHead.prev = null;                                    // the new head has nothing before it
        head.next = null;                                       // detach the old head completely
        return newHead;
    }

    /** Delete any node we hold a reference to. prev makes this O(1) with no scan. Returns the (possibly new) head. */
    static Node deleteNode(Node head, Node target) {
        Node before = target.prev, after = target.next;
        if (before != null) before.next = after;    // bypass target going forward
        else head = after;                          // target was the head
        if (after != null) after.prev = before;     // bypass target going backward
        target.prev = null;
        target.next = null;
        return head;
    }

    /** Generalisation: delete the k-th node (1-based). k == 1 deletes the head; an out-of-range k leaves the list unchanged. O(k) time. */
    static Node deleteAtPosition(Node head, int k) {
        if (head == null || k < 1) return head;
        Node target = head;
        for (int i = 1; i < k && target != null; i++) target = target.next;  // walk to the k-th node
        if (target == null) return head;                                      // fewer than k nodes
        return deleteNode(head, target);
    }

    /** For contrast: deleting the tail still costs O(n) to reach it, but prev gives the second-last node for free. */
    static Node deleteTail(Node head) {
        if (head == null || head.next == null) return null;
        Node tail = head;
        while (tail.next != null) tail = tail.next;
        tail.prev.next = null;                                   // the second-last node becomes the tail
        tail.prev = null;
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

    static void checkList(Node head, int[] expected, String msg) {
        check(Arrays.equals(forward(head), expected), msg + ": forward " + Arrays.toString(forward(head)));
        check(Arrays.equals(backward(head), reversed(expected)), msg + ": backward " + Arrays.toString(backward(head)));
        check(isWellFormed(head), msg + ": links inconsistent");
    }

    static void verify(int[] arr) {
        int[] withoutHead = arr.length == 0 ? arr : Arrays.copyOfRange(arr, 1, arr.length);
        int[] withoutTail = arr.length == 0 ? arr : Arrays.copyOf(arr, arr.length - 1);
        String in = Arrays.toString(arr);
        checkList(deleteHead(fromArray(arr)), withoutHead, "deleteHead " + in);
        checkList(deleteAtPosition(fromArray(arr), 1), withoutHead, "deleteAtPosition(1) " + in);
        if (arr.length > 0) {
            Node head = fromArray(arr);
            checkList(deleteNode(head, head), withoutHead, "deleteNode(head) " + in);
        }
        checkList(deleteTail(fromArray(arr)), withoutTail, "deleteTail " + in);
        checkList(deleteAtPosition(fromArray(arr), arr.length), withoutTail, "deleteAtPosition(n) " + in);
        checkList(deleteAtPosition(fromArray(arr), arr.length + 1), arr, "deleteAtPosition(n+1) leaves " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5});
        verify(new int[]{});                            // edge: deleting from an empty list is a no-op
        verify(new int[]{42});                          // single node: the list becomes empty
        verify(new int[]{5, 5});                        // two equal values
        verify(new int[]{-1, 0, 7, -9});

        checkList(deleteAtPosition(fromArray(new int[]{1, 2, 3, 4}), 3), new int[]{1, 2, 4}, "delete in the middle");
        checkList(deleteAtPosition(fromArray(new int[]{1, 2}), 0), new int[]{1, 2}, "k = 0 is rejected");

        Node first = fromArray(new int[]{1, 2, 3});
        Node rest = deleteHead(first);
        check(first.next == null && first.prev == null && rest.prev == null && rest.val == 2, "old head detached, new head has no prev");

        Node h = fromArray(new int[]{1, 2, 3});
        for (int i = 0; i < 5; i++) h = deleteHead(h);
        check(h == null, "deleting more times than there are nodes ends at the empty list");

        System.out.println("OK P348_DeleteHeadOfDoublyLinkedList");
    }
}
