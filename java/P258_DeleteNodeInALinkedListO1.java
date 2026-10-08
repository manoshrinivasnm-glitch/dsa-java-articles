import java.util.*;

/** TUF 258 - Delete Node in a Linked List O(1). You get only a reference to a node that is not the tail (no head); remove its value from the list. */
public class P258_DeleteNodeInALinkedListO1 {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Shift every later value one position to the left, then drop the last node. O(k) time for k nodes after it, O(1) space. */
    static void bruteForce(ListNode node) {
        ListNode cur = node;
        while (cur.next.next != null) {              // stop on the second-to-last node
            cur.val = cur.next.val;
            cur = cur.next;
        }
        cur.val = cur.next.val;                      // final shift
        cur.next = null;                             // the old tail is now a duplicate: drop it
    }

    /** Approach 2: optimal. Copy the next node's value into this node, then bypass the next node. O(1) time, O(1) space. */
    static void optimal(ListNode node) {
        ListNode victim = node.next;
        node.val = victim.val;                       // this node takes over the next node's value
        node.next = victim.next;                     // and the next node disappears from the chain
        victim.next = null;                          // optional: detach the removed node completely
    }

    // ---------------------------------------------------------------- helpers
    static ListNode[] buildNodes(int[] arr) {
        ListNode[] nodes = new ListNode[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) nodes[i] = new ListNode(arr[i], i + 1 < arr.length ? nodes[i + 1] : null);
        return nodes;
    }

    static int[] toArray(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        int[] out = new int[n];
        int i = 0;
        for (ListNode t = head; t != null; t = t.next) out[i++] = t.val;
        return out;
    }

    static String label(int[] arr, int k) {
        String a = arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
        return a + " delete index " + k;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Delete the node at index k (0-based, never the tail) and compare the list seen from the head. */
    static void verify(int[] arr, int k) {
        int[] expected = new int[arr.length - 1];
        for (int i = 0, j = 0; i < arr.length; i++) if (i != k) expected[j++] = arr[i];
        String in = label(arr, k);

        ListNode[] a = buildNodes(arr);
        bruteForce(a[k]);
        check(Arrays.equals(toArray(a[0]), expected), "bruteForce " + in);

        ListNode[] b = buildNodes(arr);
        optimal(b[k]);
        check(Arrays.equals(toArray(b[0]), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{4, 5, 1, 9}, 1);               // delete 5 -> 4 1 9
        verify(new int[]{4, 5, 1, 9}, 2);               // delete 1 (second-to-last) -> 4 5 9
        verify(new int[]{4, 5, 1, 9}, 0);               // delete the head node -> 5 1 9
        verify(new int[]{1, 2}, 0);                     // edge: smallest legal list -> 2
        verify(new int[]{-3, 0, 7, -8, 2}, 3);          // negatives -> -3 0 7 2

        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big, 0);                                 // brute force shifts every value
        verify(big, 99_998);
        verify(big, 50_000);

        System.out.println("OK P258_DeleteNodeInALinkedListO1");
    }
}
