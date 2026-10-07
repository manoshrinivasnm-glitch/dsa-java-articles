import java.util.*;

/** TUF 619 - Delete the middle node in LL. Remove the node at 0-based index floor(n / 2) and return the head. */
public class P619_DeleteTheMiddleNodeInLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force, two passes. Count the nodes, walk to index n/2 - 1 and unlink the node after it. O(n) time, O(1) space. */
    static ListNode bruteForce(ListNode head) {
        if (head == null || head.next == null) return null;   // 0 or 1 node: deleting the middle leaves nothing
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        ListNode t = head;
        for (int i = 0; i < n / 2 - 1; i++) t = t.next;       // stop on the node just before the middle
        t.next = t.next.next;
        return head;
    }

    /** Approach 2: optimal, one pass. Start fast two nodes ahead so that slow stops on the node just before the middle. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head) {
        if (head == null || head.next == null) return null;
        ListNode slow = head, fast = head.next.next;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        slow.next = slow.next.next;          // slow.next is the middle node
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

    /** Reference answer on the array: drop index n / 2; one or zero elements give an empty result. */
    static int[] expectedAfterDelete(int[] arr) {
        if (arr.length <= 1) return new int[0];
        int skip = arr.length / 2;
        int[] out = new int[arr.length - 1];
        int k = 0;
        for (int i = 0; i < arr.length; i++) if (i != skip) out[k++] = arr[i];
        return out;
    }

    static String label(int[] arr) {
        return arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr) {
        int[] expected = expectedAfterDelete(arr);
        String in = label(arr);
        check(Arrays.equals(toArray(bruteForce(fromArray(arr))), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(optimal(fromArray(arr))), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 3, 4, 7, 1, 2, 6});       // odd length: index 3 (value 7) goes
        verify(new int[]{1, 2, 3, 4});                // even length: index 2 (value 3) goes
        verify(new int[]{2, 1});                      // two nodes: the second one goes
        verify(new int[]{1});                         // single node -> empty list
        verify(new int[]{});                          // edge: empty list
        verify(new int[]{1, 2, 3});                   // -> 1 3
        verify(new int[]{1, 2, 3, 4, 5, 6});          // -> 1 2 3 5 6
        verify(new int[]{9, 9, 9, 9, 9});             // equal values

        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big);
        int[] bigOdd = new int[100_001];
        for (int i = 0; i < bigOdd.length; i++) bigOdd[i] = i;
        verify(bigOdd);

        System.out.println("OK P619_DeleteTheMiddleNodeInLL");
    }
}
