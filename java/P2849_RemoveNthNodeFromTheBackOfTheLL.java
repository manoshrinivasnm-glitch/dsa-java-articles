import java.util.*;

/** TUF 2849 - Remove Nth node from the back of the LL. Delete the n-th node counted from the end (n = 1 is the tail) and return the head. */
public class P2849_RemoveNthNodeFromTheBackOfTheLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force, two passes. Count the nodes; the target is index len - n from the front, so walk to the node before it and unlink. O(len) time, O(1) space. */
    static ListNode bruteForce(ListNode head, int n) {
        int len = 0;
        for (ListNode t = head; t != null; t = t.next) len++;
        if (n <= 0 || n > len) return head;          // nothing sensible to remove
        if (n == len) return head.next;              // the target is the head itself
        ListNode t = head;
        for (int i = 0; i < len - n - 1; i++) t = t.next;   // t is now the node just before the target
        t.next = t.next.next;
        return head;
    }

    /** Approach 2: optimal, one pass. Move fast n steps ahead, then move slow and fast together until fast is on the last node; slow then sits just before the target. O(len) time, O(1) space. */
    static ListNode optimal(ListNode head, int n) {
        if (n <= 0) return head;
        ListNode fast = head;
        for (int i = 0; i < n; i++) {
            if (fast == null) return head;           // n is larger than the length: nothing to remove
            fast = fast.next;
        }
        if (fast == null) return head.next;          // the gap spans the whole list, so the head is the target
        ListNode slow = head;
        while (fast.next != null) {                  // stop when fast stands on the last node
            slow = slow.next;
            fast = fast.next;
        }
        slow.next = slow.next.next;                  // unlink the target
        return head;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] arr) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : arr) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        int[] out = new int[n];
        int i = 0;
        for (ListNode t = head; t != null; t = t.next) out[i++] = t.val;
        return out;
    }

    /** Reference answer on the array: drop index len - n when 1 <= n <= len, otherwise unchanged. */
    static int[] expectedAfterRemoval(int[] arr, int n) {
        if (n <= 0 || n > arr.length) return arr.clone();
        int skip = arr.length - n;
        int[] out = new int[arr.length - 1];
        int k = 0;
        for (int i = 0; i < arr.length; i++) if (i != skip) out[k++] = arr[i];
        return out;
    }

    static String label(int[] arr, int n) {
        String a = arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
        return a + " n=" + n;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, int n) {
        int[] expected = expectedAfterRemoval(arr, n);
        String in = label(arr, n);
        check(Arrays.equals(toArray(bruteForce(fromArray(arr), n)), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(optimal(fromArray(arr), n)), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 2);            // -> 1 2 3 5
        verify(new int[]{1, 2, 3, 4, 5}, 5);            // remove the head -> 2 3 4 5
        verify(new int[]{1, 2, 3, 4, 5}, 1);            // remove the tail -> 1 2 3 4
        verify(new int[]{1}, 1);                        // edge: single node becomes empty list
        verify(new int[]{1, 2}, 1);                     // -> 1
        verify(new int[]{1, 2}, 2);                     // -> 2
        verify(new int[]{1, 2, 3}, 4);                  // n too large: unchanged
        verify(new int[]{1, 2, 3}, 0);                  // n = 0: unchanged
        verify(new int[]{}, 1);                         // empty list stays empty
        verify(new int[]{7, 7, 7, 7}, 3);               // equal values: position decides

        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big, 1);
        verify(big, 100_000);
        verify(big, 50_000);

        System.out.println("OK P2849_RemoveNthNodeFromTheBackOfTheLL");
    }
}
