import java.util.*;

/** TUF 626 - Remove Nth node from the back of the LL. Delete the n-th node counted from the end (n = 1 is the tail) and return the head. */
public class P626_RemoveNthNodeFromTheBackOfTheLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force, two passes. Count the nodes, walk to the node before index len - n and unlink. O(len) time, O(1) space. */
    static ListNode bruteForce(ListNode head, int n) {
        int len = 0;
        for (ListNode t = head; t != null; t = t.next) len++;
        if (n <= 0 || n > len) return head;          // out of range: leave the list alone
        if (n == len) return head.next;              // the target is the head itself
        ListNode prev = head;
        for (int i = 0; i < len - n - 1; i++) prev = prev.next;   // prev is now just before the target
        prev.next = prev.next.next;
        return head;
    }

    /** Approach 2: better, one pass with an array of node references. Index len - n - 1 is the predecessor. O(len) time, O(len) space. */
    static ListNode better(ListNode head, int n) {
        List<ListNode> nodes = new ArrayList<>();
        for (ListNode t = head; t != null; t = t.next) nodes.add(t);
        int len = nodes.size();
        if (n <= 0 || n > len) return head;
        int target = len - n;                        // 0-based index of the node to delete
        if (target == 0) return head.next;
        nodes.get(target - 1).next = nodes.get(target).next;
        return head;
    }

    /** Approach 3: optimal, one pass with two pointers n apart, both starting at a dummy node before the head. O(len) time, O(1) space. */
    static ListNode optimal(ListNode head, int n) {
        if (n <= 0) return head;
        ListNode dummy = new ListNode(0, head);
        ListNode fast = dummy, slow = dummy;
        for (int i = 0; i < n; i++) {
            fast = fast.next;
            if (fast == null) return head;           // n is larger than the length
        }
        while (fast.next != null) {                  // stop when fast stands on the last node
            fast = fast.next;
            slow = slow.next;
        }
        slow.next = slow.next.next;                  // slow is just before the target
        return dummy.next;                           // the head may have been the target
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
        check(Arrays.equals(toArray(better(fromArray(arr), n)), expected), "better " + in);
        check(Arrays.equals(toArray(optimal(fromArray(arr), n)), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 2);            // -> 1 2 3 5
        verify(new int[]{1, 2, 3, 4, 5}, 5);            // remove the head -> 2 3 4 5
        verify(new int[]{1, 2, 3, 4, 5}, 1);            // remove the tail -> 1 2 3 4
        verify(new int[]{1}, 1);                        // edge: single node becomes an empty list
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

        System.out.println("OK P626_RemoveNthNodeFromTheBackOfTheLL");
    }
}
