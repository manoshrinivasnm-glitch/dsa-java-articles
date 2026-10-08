import java.util.*;

/** TUF 618 - Check if LL is palindrome or not. Return true if the values read the same forwards and backwards. */
public class P618_CheckIfLLIsPalindromeOrNot {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** Approach 1: push every value on a stack, then compare the list with the stack's reverse order. O(n) time, O(n) space. */
    static boolean bruteForce(ListNode head) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (ListNode cur = head; cur != null; cur = cur.next) stack.push(cur.val);
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.val != stack.pop()) return false;   // pop() yields the values from the back
        }
        return true;
    }

    /** Approach 2: find the middle, reverse the second half, compare, then restore. O(n) time, O(1) space. */
    static boolean optimal(ListNode head) {
        if (head == null || head.next == null) return true;
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;                           // slow stops at the last node of the first half
            fast = fast.next.next;
        }
        ListNode secondHalf = reverse(slow.next);
        boolean isPalindrome = true;
        for (ListNode p = head, q = secondHalf; q != null; p = p.next, q = q.next) {
            if (p.val != q.val) {
                isPalindrome = false;
                break;
            }
        }
        slow.next = reverse(secondHalf);                // put the list back the way the caller gave it
        return isPalindrome;
    }

    static ListNode reverse(ListNode head) {
        ListNode prev = null, cur = head;
        while (cur != null) {
            ListNode nxt = cur.next;
            cur.next = prev;
            prev = cur;
            cur = nxt;
        }
        return prev;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] a) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : a) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        List<Integer> out = new ArrayList<>();
        for (ListNode cur = head; cur != null; cur = cur.next) out.add(cur.val);
        int[] a = new int[out.size()];
        for (int i = 0; i < a.length; i++) a[i] = out.get(i);
        return a;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, boolean expected) {
        String in = input.length <= 12 ? Arrays.toString(input) : input.length + " values";
        ListNode head = fromArray(input);
        check(bruteForce(head) == expected, "bruteForce " + in);
        check(optimal(head) == expected, "optimal " + in);
        check(Arrays.equals(toArray(head), input), "optimal must restore the list " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 2, 1}, true);            // even length
        verify(new int[]{1, 2, 3, 2, 1}, true);         // odd length, middle value is ignored
        verify(new int[]{1, 2}, false);
        verify(new int[]{1, 2, 3, 1}, false);           // ends match, inside does not
        verify(new int[]{}, true);                      // edge: empty list
        verify(new int[]{5}, true);                     // edge: single node
        verify(new int[]{4, 4}, true);
        verify(new int[]{-1000, 7, -1000}, true);       // values outside the Integer cache (-128..127)
        verify(new int[]{1000, 7, 1001}, false);
        int n = 100_001;
        int[] big = new int[n];
        for (int i = 0; i < n; i++) big[i] = Math.min(i, n - 1 - i);   // 0 1 2 ... 50000 ... 2 1 0
        verify(big, true);
        big[n - 2] = 3;                                 // break the symmetry near the end
        verify(big, false);
        System.out.println("OK P618_CheckIfLLIsPalindromeOrNot");
    }
}
