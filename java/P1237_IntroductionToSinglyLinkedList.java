import java.util.*;

/** TUF 1237 - Introduction to Singly LinkedList. The node, building a list from an array, traversal, and the basic queries. */
public class P1237_IntroductionToSinglyLinkedList {

    /** A node of a singly linked list: one value and one reference to the next node (null at the end). */
    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Build a list from an array the textbook way: a "mover" stays on the last node and each new node is attached behind it. O(n) time, O(n) space. */
    static ListNode arrayToLinkedList(int[] arr) {
        if (arr.length == 0) return null;                  // an empty list is just a null head
        ListNode head = new ListNode(arr[0]);
        ListNode mover = head;                             // always points at the current last node
        for (int i = 1; i < arr.length; i++) {
            ListNode temp = new ListNode(arr[i]);
            mover.next = temp;                             // link the old last node to the new one
            mover = temp;                                  // the new node is now the last one
        }
        return head;
    }

    /** The same construction with a dummy node, which removes the special case for the first element. O(n) time, O(n) space. */
    static ListNode arrayToLinkedListWithDummy(int[] arr) {
        ListNode dummy = new ListNode(0);                  // placeholder that sits in front of the real head
        ListNode tail = dummy;
        for (int v : arr) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;                                 // skip the placeholder
    }

    /** Traverse the list and render it as "1 -> 2 -> 3 -> null". O(n) time, O(n) for the output string. */
    static String traverse(ListNode head) {
        StringBuilder sb = new StringBuilder();
        ListNode temp = head;
        while (temp != null) {
            sb.append(temp.val).append(" -> ");
            temp = temp.next;
        }
        sb.append("null");
        return sb.toString();
    }

    /** Copy the values back into an array, the inverse of arrayToLinkedList. O(n) time, O(n) space. */
    static int[] toArray(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        int[] out = new int[n];
        int i = 0;
        for (ListNode t = head; t != null; t = t.next) out[i++] = t.val;
        return out;
    }

    /** Count the nodes. O(n) time, O(1) space. */
    static int length(ListNode head) {
        int count = 0;
        for (ListNode t = head; t != null; t = t.next) count++;
        return count;
    }

    /** Access by 0-based position, or -1 when the position does not exist. O(index) time: a list has no random access. */
    static int get(ListNode head, int index) {
        if (index < 0) return -1;
        ListNode t = head;
        for (int i = 0; i < index && t != null; i++) t = t.next;
        return t == null ? -1 : t.val;
    }

    /** Node variables are references: a change made through one alias is visible through every other alias of the same node. */
    static boolean aliasingDemo() {
        ListNode head = new ListNode(1, new ListNode(2));
        ListNode alias = head;                             // a second name for the same node, not a copy
        alias.val = 99;
        alias.next = null;                                 // cuts the list down to one node
        return head.val == 99 && head.next == null && length(head) == 1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verifyBuild(int[] arr, String rendered) {
        ListNode a = arrayToLinkedList(arr);
        ListNode b = arrayToLinkedListWithDummy(arr);
        check(traverse(a).equals(rendered), "mover build of " + Arrays.toString(arr) + " gave " + traverse(a));
        check(traverse(b).equals(rendered), "dummy build of " + Arrays.toString(arr) + " gave " + traverse(b));
        check(Arrays.equals(toArray(a), arr), "round trip (mover) of " + Arrays.toString(arr));
        check(Arrays.equals(toArray(b), arr), "round trip (dummy) of " + Arrays.toString(arr));
        check(length(a) == arr.length && length(b) == arr.length, "length of " + Arrays.toString(arr));
        for (int i = 0; i < arr.length; i++) {
            check(get(a, i) == arr[i] && get(b, i) == arr[i], "get(" + i + ") on " + Arrays.toString(arr));
        }
        check(get(a, arr.length) == -1 && get(a, -1) == -1, "out-of-range get on " + Arrays.toString(arr));
    }

    public static void main(String[] args) {
        verifyBuild(new int[]{1, 2, 3, 4, 5}, "1 -> 2 -> 3 -> 4 -> 5 -> null");
        verifyBuild(new int[]{7}, "7 -> null");                               // single node
        verifyBuild(new int[]{}, "null");                                     // edge: empty list
        verifyBuild(new int[]{4, 4, 4}, "4 -> 4 -> 4 -> null");               // equal values are distinct nodes
        verifyBuild(new int[]{10, -3, 0, 8}, "10 -> -3 -> 0 -> 8 -> null");   // negatives and zero
        check(aliasingDemo(), "node variables are references, not copies");

        ListNode head = arrayToLinkedList(new int[]{1, 2, 3});
        check(head.val == 1 && head.next.val == 2 && head.next.next.val == 3 && head.next.next.next == null, "manual pointer walk");
        check(arrayToLinkedList(new int[]{5}).next == null, "a one-node list ends immediately");

        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        check(length(arrayToLinkedList(big)) == 100_000 && get(arrayToLinkedListWithDummy(big), 99_999) == 99_999, "large list");

        System.out.println(traverse(head));
        System.out.println("OK P1237_IntroductionToSinglyLinkedList");
    }
}
