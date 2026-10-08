import java.util.*;

/** TUF 615 - Rotate a LL. Rotate the list to the right by k places. */
public class P615_RotateALL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** Approach 1: k times, detach the last node and put it in front. O(n * k) time, O(1) space. */
    static ListNode bruteForce(ListNode head, int k) {
        if (head == null || head.next == null) return head;
        for (int step = 0; step < k; step++) {
            ListNode beforeLast = head;
            while (beforeLast.next.next != null) beforeLast = beforeLast.next;
            ListNode last = beforeLast.next;
            beforeLast.next = null;                     // last node leaves the end ...
            last.next = head;                           // ... and becomes the new head
            head = last;
        }
        return head;
    }

    /** Approach 2: count the length, reduce k mod n, cut once and splice the tail before the head. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head, int k) {
        if (head == null || head.next == null) return head;
        int n = 1;
        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
            n++;
        }
        k %= n;                                         // rotating by n gives the same list back
        if (k == 0) return head;
        ListNode newTail = head;
        for (int i = 1; i < n - k; i++) newTail = newTail.next;   // the (n - k)-th node
        ListNode newHead = newTail.next;
        newTail.next = null;
        tail.next = head;                               // old tail now leads into the old head
        return newHead;
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

    static void verify(int[] input, int k, int[] expected, boolean runBruteForce) {
        String in = Arrays.toString(input) + " k=" + k;
        if (runBruteForce) check(Arrays.equals(toArray(bruteForce(fromArray(input), k)), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(optimal(fromArray(input), k)), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 2, new int[]{4, 5, 1, 2, 3}, true);
        verify(new int[]{0, 1, 2}, 4, new int[]{2, 0, 1}, true);                     // k > n
        verify(new int[]{1, 2, 3}, 3, new int[]{1, 2, 3}, true);                     // k == n
        verify(new int[]{1, 2, 3}, 0, new int[]{1, 2, 3}, true);                     // k == 0
        verify(new int[]{}, 5, new int[]{}, true);                                   // edge: empty list
        verify(new int[]{7}, 99, new int[]{7}, true);                                // edge: single node
        verify(new int[]{1, 2}, 1, new int[]{2, 1}, true);
        verify(new int[]{1, 2, 3}, 1_000_000, new int[]{3, 1, 2}, true);             // 1 000 000 % 3 == 1
        verify(new int[]{1, 2, 3, 4}, Integer.MAX_VALUE, new int[]{2, 3, 4, 1}, false); // brute force would take 2^31 rounds
        System.out.println("OK P615_RotateALL");
    }
}
