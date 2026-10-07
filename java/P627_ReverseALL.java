import java.util.*;

/** TUF 627 - Reverse a LL. Return the head of the reversed singly linked list. */
public class P627_ReverseALL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Push every value on a stack, then pop them back into the same nodes front to back. O(n) time, O(n) space. */
    static ListNode bruteForce(ListNode head) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (ListNode t = head; t != null; t = t.next) stack.push(t.val);
        for (ListNode t = head; t != null; t = t.next) t.val = stack.pop();   // pops come out last-in first-out
        return head;
    }

    /** Approach 2: optimal, iterative. Walk the list once and point every node at its predecessor. O(n) time, O(1) space. */
    static ListNode optimalIterative(ListNode head) {
        ListNode prev = null, current = head;
        while (current != null) {
            ListNode next = current.next;   // remember the rest before cutting the link
            current.next = prev;            // flip the pointer backwards
            prev = current;                 // advance both pointers
            current = next;
        }
        return prev;                        // the old tail is the new head
    }

    /** Approach 3: optimal, recursive. Reverse everything after the head, then hang the head on the end of that. O(n) time, O(n) stack. */
    static ListNode optimalRecursive(ListNode head) {
        if (head == null || head.next == null) return head;   // empty list or single node: already reversed
        ListNode newHead = optimalRecursive(head.next);       // after this call, head.next is the tail of the reversed rest
        head.next.next = head;                                // the old second node now points back at head
        head.next = null;                                     // head becomes the new tail
        return newHead;
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

    static int[] reversed(int[] arr) {
        int[] out = new int[arr.length];
        for (int i = 0; i < arr.length; i++) out[i] = arr[arr.length - 1 - i];
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
        int[] expected = reversed(arr);
        String in = label(arr);
        check(Arrays.equals(toArray(bruteForce(fromArray(arr))), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(optimalIterative(fromArray(arr))), expected), "optimalIterative " + in);
        check(Arrays.equals(toArray(optimalRecursive(fromArray(arr))), expected), "optimalRecursive " + in);
        check(Arrays.equals(toArray(optimalIterative(optimalIterative(fromArray(arr)))), arr), "reversing twice restores " + in);
        check(Arrays.equals(toArray(optimalRecursive(optimalRecursive(fromArray(arr)))), arr), "recursive twice restores " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5});
        verify(new int[]{});                         // edge: empty list
        verify(new int[]{1});                        // single node is its own reverse
        verify(new int[]{1, 2});                     // two nodes
        verify(new int[]{7, 7, 7});                  // equal values
        verify(new int[]{-3, 0, 9, -1, 4, 2});       // negatives

        ListNode head = fromArray(new int[]{1, 2, 3});
        ListNode oldTail = head.next.next;
        ListNode newHead = optimalIterative(head);
        check(newHead == oldTail && head.next == null && newHead.next.next == head, "iterative re-links the existing nodes");

        ListNode head2 = fromArray(new int[]{1, 2, 3});
        ListNode oldTail2 = head2.next.next;
        ListNode newHead2 = optimalRecursive(head2);
        check(newHead2 == oldTail2 && head2.next == null && newHead2.next.next == head2, "recursive re-links the existing nodes");

        ListNode same = fromArray(new int[]{1, 2, 3});
        check(bruteForce(same) == same, "bruteForce keeps the same head node and only moves values");

        int[] medium = new int[5_000];
        for (int i = 0; i < medium.length; i++) medium[i] = i;
        verify(medium);                              // 5000 recursive frames fit comfortably in the default stack

        int[] big = new int[200_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        int[] bigExpected = reversed(big);
        check(Arrays.equals(toArray(optimalIterative(fromArray(big))), bigExpected), "iterative on 200k nodes");
        check(Arrays.equals(toArray(bruteForce(fromArray(big))), bigExpected), "bruteForce on 200k nodes");

        System.out.println("OK P627_ReverseALL");
    }
}
