import java.util.*;

/** TUF 448 - Find Middle of Linked List. Return the middle node; when the length is even, return the second of the two middle nodes. */
public class P448_FindMiddleOfLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force, two passes. Count the nodes, then walk len / 2 steps from the head. O(n) time, O(1) space. */
    static ListNode bruteForce(ListNode head) {
        int len = 0;
        for (ListNode t = head; t != null; t = t.next) len++;
        ListNode mid = head;
        for (int i = 0; i < len / 2; i++) mid = mid.next;   // index len / 2 is the (second) middle
        return mid;
    }

    /** Approach 2: optimal, tortoise and hare. slow moves one step, fast moves two; when fast runs out, slow is at the middle. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {   // fast can still take two steps
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode[] buildNodes(int[] arr) {
        ListNode[] nodes = new ListNode[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) nodes[i] = new ListNode(arr[i], i + 1 < arr.length ? nodes[i + 1] : null);
        return nodes;
    }

    static String label(int[] arr) {
        return arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** expectedIndex is the 0-based position of the middle node, or -1 for an empty list. */
    static void verify(int[] arr, int expectedIndex) {
        ListNode[] nodes = buildNodes(arr);
        ListNode head = arr.length == 0 ? null : nodes[0];
        ListNode expected = expectedIndex < 0 ? null : nodes[expectedIndex];
        check(bruteForce(head) == expected, "bruteForce " + label(arr));
        check(optimal(head) == expected, "optimal " + label(arr));
        if (expected != null) check(optimal(head).val == arr[expectedIndex], "value " + label(arr));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 2);            // odd length: the 3
        verify(new int[]{1, 2, 3, 4, 5, 6}, 3);         // even length: the second middle, the 4
        verify(new int[]{7}, 0);                        // edge: single node is its own middle
        verify(new int[]{1, 2}, 1);                     // two nodes: the second one
        verify(new int[]{}, -1);                        // edge: empty list has no middle
        verify(new int[]{5, 5, 5, 5}, 2);               // equal values: identity, not value, decides

        int[] big = new int[100_001];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big, 50_000);
        verify(Arrays.copyOf(big, 100_000), 50_000);

        System.out.println("OK P448_FindMiddleOfLinkedList");
    }
}
