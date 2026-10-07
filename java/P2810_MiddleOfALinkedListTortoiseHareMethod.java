import java.util.*;

/** TUF 2810 - Middle of a LinkedList [TortoiseHare Method]. Return the middle node; for an even length, the second of the two middles. */
public class P2810_MiddleOfALinkedListTortoiseHareMethod {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: two passes. Count the nodes, then walk n / 2 steps from the head. O(n) time, O(1) space. */
    static ListNode bruteForce(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        ListNode temp = head;
        for (int i = 0; i < n / 2; i++) temp = temp.next;   // 0-based index n / 2 is the (second) middle
        return temp;
    }

    /** Approach 2: tortoise and hare. slow moves one step while fast moves two; when fast runs off the end, slow is on the middle. O(n) time, O(1) space, one pass. */
    static ListNode optimal(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    /** Variant: the first of the two middles for an even length (the same node as optimal for an odd length). Needed when the list is to be split in half. */
    static ListNode optimalFirstMiddle(ListNode head) {
        if (head == null) return null;
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        return slow;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] arr) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : arr) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    static ListNode nodeAt(ListNode head, int index) {
        ListNode t = head;
        for (int i = 0; i < index && t != null; i++) t = t.next;
        return t;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Both approaches must return the very same node object (not just an equal value) as the position-based oracle. */
    static void verify(int[] arr) {
        ListNode head = fromArray(arr);
        int n = arr.length;
        ListNode expectedSecond = n == 0 ? null : nodeAt(head, n / 2);
        ListNode expectedFirst = n == 0 ? null : nodeAt(head, (n - 1) / 2);
        String in = Arrays.toString(arr);
        check(bruteForce(head) == expectedSecond, "bruteForce " + in);
        check(optimal(head) == expectedSecond, "optimal " + in);
        check(optimalFirstMiddle(head) == expectedFirst, "optimalFirstMiddle " + in);
        if (n > 0) check(optimal(head).val == arr[n / 2], "optimal value " + in);
        if (n % 2 == 1) check(optimalFirstMiddle(head) == optimal(head), "odd length: both middles coincide " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5});           // odd length: the middle is 3
        verify(new int[]{1, 2, 3, 4, 5, 6});        // even length: second middle 4, first middle 3
        verify(new int[]{});                         // edge: empty list has no middle
        verify(new int[]{1});                        // single node is its own middle
        verify(new int[]{1, 2});                     // two nodes: second middle is 2, first middle is 1
        verify(new int[]{7, 7, 7, 7});               // equal values: node identity is checked, not the value
        verify(new int[]{-3, 0, 4, -8, 10, 2, 6});

        ListNode head = fromArray(new int[]{1, 2, 3, 4, 5, 6});
        check(optimal(head).val == 4 && bruteForce(head).val == 4, "second middle of six nodes is 4");
        check(optimalFirstMiddle(head).val == 3, "first middle of six nodes is 3");

        int[] big = new int[200_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big);
        check(optimal(fromArray(big)).val == 100_000, "middle of 200000 nodes");

        System.out.println("OK P2810_MiddleOfALinkedListTortoiseHareMethod");
    }
}
