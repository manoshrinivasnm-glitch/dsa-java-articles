import java.util.*;

/** TUF 2850 - Reverse a LinkedList [Iterative]. Return the head of the reversed list. */
public class P2850_ReverseALinkedListIterative {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Push the values on a stack and write them back front to back; the nodes stay where they are. O(n) time, O(n) space. */
    static ListNode bruteForce(ListNode head) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (ListNode t = head; t != null; t = t.next) stack.push(t.val);
        for (ListNode t = head; t != null; t = t.next) t.val = stack.pop();    // pops come out in reverse order
        return head;
    }

    /** Approach 2: optimal, iterative. Walk the list once and point every node at its predecessor. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head) {
        ListNode prev = null, current = head;
        while (current != null) {
            ListNode next = current.next;   // save the rest of the list before cutting the link
            current.next = prev;            // flip this node's pointer
            prev = current;                 // advance both pointers
            current = next;
        }
        return prev;                        // prev ends on the old tail, which is the new head
    }

    /** For comparison: the recursive version. Reverse the rest, then hang the head behind it. O(n) time, O(n) stack. */
    static ListNode recursive(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode newHead = recursive(head.next);
        head.next.next = head;              // the old second node now points back at head
        head.next = null;                   // head becomes the tail
        return newHead;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] arr) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : arr) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        List<Integer> out = new ArrayList<>();
        for (ListNode t = head; t != null; t = t.next) out.add(t.val);
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    static int[] reversed(int[] arr) {
        int[] out = new int[arr.length];
        for (int i = 0; i < arr.length; i++) out[i] = arr[arr.length - 1 - i];
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr) {
        int[] expected = reversed(arr);
        String in = Arrays.toString(arr);
        check(Arrays.equals(toArray(bruteForce(fromArray(arr))), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(optimal(fromArray(arr))), expected), "optimal " + in);
        check(Arrays.equals(toArray(recursive(fromArray(arr))), expected), "recursive " + in);
        check(Arrays.equals(toArray(optimal(optimal(fromArray(arr)))), arr), "reversing twice restores " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5});
        verify(new int[]{});                        // edge: empty list
        verify(new int[]{1});                       // single node is its own reverse
        verify(new int[]{1, 2});                    // two nodes
        verify(new int[]{4, 4, 4});                 // equal values
        verify(new int[]{-1, 0, 7, -9, 3, 3});

        ListNode head = fromArray(new int[]{1, 2, 3});
        ListNode oldTail = head.next.next;
        ListNode newHead = optimal(head);
        check(newHead == oldTail && head.next == null && newHead.next.next == head, "optimal re-links the existing nodes");
        ListNode same = fromArray(new int[]{1, 2, 3});
        check(bruteForce(same) == same && same.val == 3, "bruteForce keeps the head node and only moves values");

        int[] mid = new int[5_000];
        for (int i = 0; i < mid.length; i++) mid[i] = i;
        verify(mid);                                 // 5000 recursive frames fit in the default stack

        int[] big = new int[200_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        check(Arrays.equals(toArray(optimal(fromArray(big))), reversed(big)), "optimal on 200000 nodes");
        check(Arrays.equals(toArray(bruteForce(fromArray(big))), reversed(big)), "bruteForce on 200000 nodes");

        System.out.println("OK P2850_ReverseALinkedListIterative");
    }
}
