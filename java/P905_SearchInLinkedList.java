import java.util.*;

/** TUF 905 - Search in Linked List. Report whether key occurs in the list (and, as variants, where and how often). */
public class P905_SearchInLinkedList {

    static class ListNode {
        int val;                                   // the value stored in this node
        ListNode next;                             // next node in the list, null at the end
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: linear scan with a loop, stop at the first match. O(n) time, O(1) space. */
    static boolean searchIterative(ListNode head, int key) {
        ListNode temp = head;                      // walking pointer, so head itself never moves
        while (temp != null) {                     // stop once we step past the last node
            if (temp.val == key) return true;      // found it: stop early
            temp = temp.next;                      // move to the next node
        }
        return false;                              // reached the end without a match
    }

    /** Approach 2: recursion. The key is in the list if it is at the head or somewhere in the rest. O(n) time, O(n) stack. */
    static boolean searchRecursive(ListNode head, int key) {
        if (head == null) return false;            // base case: empty list, so the key is absent
        if (head.val == key) return true;          // base case: the key is at the head
        return searchRecursive(head.next, key);    // otherwise look in the rest of the list
    }

    /** Variant: the 0-based position of the first match, or -1 when the key is absent. O(n) time, O(1) space. */
    static int indexOf(ListNode head, int key) {
        int i = 0;                                 // position of the node t
        for (ListNode t = head; t != null; t = t.next, i++) {   // i always holds t's position
            if (t.val == key) return i;            // first match: return its position
        }
        return -1;                                 // the key never appeared
    }

    /** Variant: count every occurrence instead of stopping at the first one. O(n) time, O(1) space. */
    static int countOccurrences(ListNode head, int key) {
        int count = 0;                             // matches seen so far
        for (ListNode t = head; t != null; t = t.next) {   // visit every node, no early exit
            if (t.val == key) count++;             // one more match
        }
        return count;
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

    static void verify(int[] arr, int key, int expectedIndex, int expectedCount) {
        ListNode head = fromArray(arr);
        boolean expectedFound = expectedIndex >= 0;
        String in = Arrays.toString(arr) + " key " + key;
        check(searchIterative(head, key) == expectedFound, "iterative " + in);
        check(searchRecursive(head, key) == expectedFound, "recursive " + in);
        check(indexOf(head, key) == expectedIndex, "indexOf " + in);
        check(countOccurrences(head, key) == expectedCount, "countOccurrences " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 4, 3, 1);
        verify(new int[]{1, 2, 3, 4, 5}, 1, 0, 1);              // key at the head
        verify(new int[]{1, 2, 3, 4, 5}, 5, 4, 1);              // key at the tail
        verify(new int[]{1, 2, 3, 4, 5}, 6, -1, 0);             // absent
        verify(new int[]{}, 1, -1, 0);                          // edge: empty list
        verify(new int[]{7}, 7, 0, 1);                          // single node, present
        verify(new int[]{7}, 8, -1, 0);                         // single node, absent
        verify(new int[]{2, 9, 2, 2, 5}, 2, 0, 3);              // duplicates: first index, full count
        verify(new int[]{-3, 0, -3}, -3, 0, 2);                 // negatives
        verify(new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}, Integer.MIN_VALUE, 1, 1);

        int[] big = new int[5_000];
        for (int i = 0; i < big.length; i++) big[i] = i * 2;
        verify(big, 9_998, 4_999, 1);                           // last node of a long list, 5000 recursive frames
        verify(big, 9_999, -1, 0);                              // odd value never present

        System.out.println("OK P905_SearchInLinkedList");
    }
}
