import java.util.*;

/** TUF 1234 - Introduction to Doubly LL. The two-link node, building a DLL from an array, traversal in both directions, and O(1) local edits. */
public class P1234_IntroductionToDoublyLL {

    /** A node of a doubly linked list: a value plus references to both neighbours (null at either end). */
    static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
        Node(int val, Node prev, Node next) { this.val = val; this.prev = prev; this.next = next; }
    }

    /** Build a DLL from an array: attach each new node after the mover and point its prev back at the mover. O(n) time, O(n) space. */
    static Node arrayToDLL(int[] arr) {
        if (arr.length == 0) return null;
        Node head = new Node(arr[0]);
        Node mover = head;                                 // always the current last node
        for (int i = 1; i < arr.length; i++) {
            Node temp = new Node(arr[i]);
            mover.next = temp;                             // forward link
            temp.prev = mover;                             // backward link, the one a singly list does not have
            mover = temp;
        }
        return head;
    }

    /** Forward traversal: follow next from the head. O(n) time. */
    static int[] toArrayForward(Node head) {
        int n = 0;
        for (Node t = head; t != null; t = t.next) n++;
        int[] out = new int[n];
        int i = 0;
        for (Node t = head; t != null; t = t.next) out[i++] = t.val;
        return out;
    }

    /** Backward traversal: walk to the tail, then follow prev back to the head. O(n) time. */
    static int[] toArrayBackward(Node head) {
        int n = 0;
        Node tail = null;
        for (Node t = head; t != null; t = t.next) { n++; tail = t; }
        int[] out = new int[n];
        int i = 0;
        for (Node t = tail; t != null; t = t.prev) out[i++] = t.val;
        return out;
    }

    /** Render as "null <- 1 <-> 2 <-> 3 -> null", or "null" for the empty list. */
    static String render(Node head) {
        if (head == null) return "null";
        StringBuilder sb = new StringBuilder("null <- ");
        for (Node t = head; t != null; t = t.next) {
            sb.append(t.val);
            sb.append(t.next != null ? " <-> " : " -> null");
        }
        return sb.toString();
    }

    /** A DLL is well formed when head.prev is null and every forward link is mirrored by a backward link. O(n) time. */
    static boolean isWellFormed(Node head) {
        if (head == null) return true;
        if (head.prev != null) return false;
        for (Node t = head; t != null; t = t.next) {
            if (t.next != null && t.next.prev != t) return false;
        }
        return true;
    }

    /** Insert a new node right after `node`. Four links change instead of one, but it is still O(1). */
    static void insertAfter(Node node, int val) {
        Node fresh = new Node(val, node, node.next);       // prev = node, next = node's old successor
        if (node.next != null) node.next.prev = fresh;     // the old successor now looks back at fresh
        node.next = fresh;
    }

    /** Delete a node given only a reference to it. The prev link makes this O(1); a singly list would need a scan. Returns the (possibly new) head. */
    static Node deleteNode(Node head, Node target) {
        if (target == null) return head;
        if (target.prev != null) target.prev.next = target.next;    // bypass target going forward
        else head = target.next;                                    // target was the head
        if (target.next != null) target.next.prev = target.prev;    // bypass target going backward
        target.prev = null;
        target.next = null;
        return head;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static int[] reversed(int[] arr) {
        int[] out = new int[arr.length];
        for (int i = 0; i < arr.length; i++) out[i] = arr[arr.length - 1 - i];
        return out;
    }

    static void verifyBuild(int[] arr, String rendered) {
        Node head = arrayToDLL(arr);
        String in = Arrays.toString(arr);
        check(isWellFormed(head), "well formed " + in);
        check(Arrays.equals(toArrayForward(head), arr), "forward " + in);
        check(Arrays.equals(toArrayBackward(head), reversed(arr)), "backward " + in);
        check(render(head).equals(rendered), "render " + in + " gave " + render(head));
    }

    public static void main(String[] args) {
        verifyBuild(new int[]{1, 2, 3, 4}, "null <- 1 <-> 2 <-> 3 <-> 4 -> null");
        verifyBuild(new int[]{}, "null");                                   // edge: empty list
        verifyBuild(new int[]{7}, "null <- 7 -> null");                     // single node: prev and next both null
        verifyBuild(new int[]{5, 5, 5}, "null <- 5 <-> 5 <-> 5 -> null");
        verifyBuild(new int[]{-2, 0, 9}, "null <- -2 <-> 0 <-> 9 -> null");

        Node head = arrayToDLL(new int[]{1, 2, 3});
        check(head.prev == null && head.next.prev == head && head.next.next.prev == head.next && head.next.next.next == null, "manual link walk");

        insertAfter(head.next, 25);                                         // 1 <-> 2 <-> 25 <-> 3
        check(Arrays.equals(toArrayForward(head), new int[]{1, 2, 25, 3}) && isWellFormed(head), "insertAfter in the middle");
        insertAfter(head.next.next.next, 4);                                // after the tail
        check(Arrays.equals(toArrayForward(head), new int[]{1, 2, 25, 3, 4}) && isWellFormed(head), "insertAfter at the tail");
        check(Arrays.equals(toArrayBackward(head), new int[]{4, 3, 25, 2, 1}), "backward after inserts");

        head = deleteNode(head, head.next.next);                            // remove 25
        check(Arrays.equals(toArrayForward(head), new int[]{1, 2, 3, 4}) && isWellFormed(head), "deleteNode in the middle");
        head = deleteNode(head, head);                                      // remove the head
        check(Arrays.equals(toArrayForward(head), new int[]{2, 3, 4}) && isWellFormed(head) && head.prev == null, "deleteNode at the head");
        head = deleteNode(head, head.next.next);                            // remove the tail
        check(Arrays.equals(toArrayForward(head), new int[]{2, 3}) && isWellFormed(head), "deleteNode at the tail");
        head = deleteNode(head, head);
        head = deleteNode(head, head);
        check(head == null && deleteNode(head, null) == null, "deleting every node gives the empty list");

        Node broken = new Node(1);
        broken.next = new Node(2);                                          // forgot broken.next.prev = broken
        check(!isWellFormed(broken), "a missing prev link is detected");
        broken.next.prev = broken;
        check(isWellFormed(broken), "fixed");

        System.out.println(render(arrayToDLL(new int[]{1, 2, 3, 4})));
        System.out.println("OK P1234_IntroductionToDoublyLL");
    }
}
