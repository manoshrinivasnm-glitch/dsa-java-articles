import java.util.*;

/** TUF 470 - Find the length of the Linked List. Count the nodes reachable from head. */
public class P470_FindTheLengthOfTheLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: walk the list with a counter. O(n) time, O(1) space. */
    static int lengthIterative(ListNode head) {
        int count = 0;
        ListNode temp = head;
        while (temp != null) {
            count++;
            temp = temp.next;
        }
        return count;
    }

    /** Approach 2: recursion. The length of a list is 1 plus the length of the rest; the empty list has length 0. O(n) time, O(n) stack. */
    static int lengthRecursive(ListNode head) {
        if (head == null) return 0;
        return 1 + lengthRecursive(head.next);
    }

    /** Approach 3: keep the size up to date on every change, so asking for it costs O(1). This is what java.util.LinkedList does. */
    static class SizedList {
        ListNode head;
        int size;

        void addFirst(int val) {
            head = new ListNode(val, head);
            size++;
        }

        void removeFirst() {
            if (head == null) return;
            head = head.next;
            size--;
        }

        int length() { return size; }
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] arr) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : arr) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr) {
        ListNode head = fromArray(arr);
        String in = Arrays.toString(arr);
        check(lengthIterative(head) == arr.length, "iterative " + in);
        check(lengthRecursive(head) == arr.length, "recursive " + in);
        SizedList list = new SizedList();
        for (int i = arr.length - 1; i >= 0; i--) list.addFirst(arr[i]);     // addFirst in reverse keeps the order
        check(list.length() == arr.length, "sized list " + in);
        check(lengthIterative(list.head) == list.length() && lengthRecursive(list.head) == list.length(), "sized list agrees with counting " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5});
        verify(new int[]{});                 // edge: empty list has length 0
        verify(new int[]{7});                // single node
        verify(new int[]{3, 3, 3});          // duplicates still count as separate nodes
        verify(new int[]{-5, 0, 5, 10, 15, 20});

        int[] big = new int[5_000];
        verify(big);                          // 5000 recursive frames fit comfortably in the default stack

        SizedList list = new SizedList();
        list.addFirst(1); list.addFirst(2); list.addFirst(3);
        list.removeFirst();
        check(list.length() == 2 && lengthIterative(list.head) == 2, "size tracks removals");
        list.removeFirst(); list.removeFirst(); list.removeFirst();
        check(list.length() == 0 && list.head == null, "removing from an empty list keeps size at 0");

        System.out.println("OK P470_FindTheLengthOfTheLinkedList");
    }
}
