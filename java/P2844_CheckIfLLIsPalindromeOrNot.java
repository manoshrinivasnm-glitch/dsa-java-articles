import java.util.*;

/** TUF 2844 - Check if LL is palindrome or not. True when the sequence of values reads the same forwards and backwards. */
public class P2844_CheckIfLLIsPalindromeOrNot {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Push every value on a stack, then walk the list again comparing each value with the popped one. O(n) time, O(n) space. */
    static boolean bruteForce(ListNode head) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (ListNode t = head; t != null; t = t.next) stack.push(t.val);
        for (ListNode t = head; t != null; t = t.next) {
            if (t.val != stack.pop()) return false;   // the pop yields the values back to front
        }
        return true;
    }

    /** Approach 2: optimal. Find the middle, reverse the second half in place, compare the halves, then restore the list. O(n) time, O(1) space. */
    static boolean optimal(ListNode head) {
        if (head == null || head.next == null) return true;
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {   // slow stops on the last node of the first half
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode secondHalf = reverse(slow.next);              // second half, now reversed
        boolean palindrome = true;
        ListNode a = head, b = secondHalf;
        while (b != null) {                                     // the second half is never longer than the first
            if (a.val != b.val) { palindrome = false; break; }
            a = a.next;
            b = b.next;
        }
        slow.next = reverse(secondHalf);                        // put the list back the way it was
        return palindrome;
    }

    /** Standard in-place reversal; returns the new head. */
    static ListNode reverse(ListNode head) {
        ListNode prev = null, current = head;
        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        return prev;
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

    static String label(int[] arr) {
        return arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr, boolean expected) {
        String in = label(arr);
        ListNode head = fromArray(arr);
        check(bruteForce(head) == expected, "bruteForce " + in);
        check(optimal(head) == expected, "optimal " + in);
        check(Arrays.equals(toArray(head), arr), "optimal must leave the list unchanged for " + in);
        check(optimal(head) == expected, "optimal, second call on the restored list " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 2, 1}, true);               // even length
        verify(new int[]{1, 2, 3, 2, 1}, true);            // odd length, middle ignored
        verify(new int[]{1, 2}, false);
        verify(new int[]{1, 1}, true);
        verify(new int[]{1}, true);                        // single node
        verify(new int[]{}, true);                         // edge: empty list
        verify(new int[]{1, 2, 3, 3, 2, 1}, true);
        verify(new int[]{1, 2, 3, 1}, false);              // mismatch in the inner pair
        verify(new int[]{1, 2, 1, 2}, false);              // not a palindrome although values repeat
        verify(new int[]{-5, 0, -5}, true);                // negatives
        verify(new int[]{2, 1, 1, 2, 1}, false);           // early mismatch must still restore the list

        int[] big = new int[100_000];
        for (int i = 0; i < 50_000; i++) { big[i] = i; big[big.length - 1 - i] = i; }
        verify(big, true);
        big[big.length - 1] = -1;
        verify(big, false);

        System.out.println("OK P2844_CheckIfLLIsPalindromeOrNot");
    }
}
