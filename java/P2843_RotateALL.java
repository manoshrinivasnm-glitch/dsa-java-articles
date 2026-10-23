import java.util.*;

/** TUF 2843 - Rotate a LL. Rotate the list to the right by k places: the last k nodes move to the front, keeping their order. */
public class P2843_RotateALL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: rotate by one place k times; every single rotation walks to the tail. O(n * k) time, O(1) space. */
    static ListNode bruteForce(ListNode head, int k) {
        if (head == null || head.next == null) return head;   // 0 or 1 node: rotating changes nothing
        for (int i = 0; i < k; i++) {                     // one single-step rotation per iteration
            ListNode secondLast = head;
            while (secondLast.next.next != null) secondLast = secondLast.next;   // walk to the node before the tail
            ListNode last = secondLast.next;
            secondLast.next = null;                       // detach the tail node
            last.next = head;                             // and put it in front
            head = last;                                  // the old tail is the new head
        }
        return head;
    }

    /** Approach 2: copy the values into a list and write them back shifted by k mod n positions. O(n) time, O(n) space. */
    static ListNode better(ListNode head, int k) {
        if (head == null) return null;                    // nothing to rotate
        List<Integer> vals = new ArrayList<>();
        for (ListNode t = head; t != null; t = t.next) vals.add(t.val);   // copy the values out in order
        int n = vals.size(), shift = k % n;               // n rotations restore the list: only k mod n counts
        ListNode t = head;
        for (int i = 0; i < n; i++) {                     // rewrite each node front to back
            // position i of the result holds position i - shift of the original
            t.val = vals.get((i - shift + n) % n);
            t = t.next;
        }
        return head;
    }

    /** Approach 3: count the nodes, join the tail to the head to form a ring, then cut the ring after node n - (k mod n). O(n) time, O(1) space. */
    static ListNode optimal(ListNode head, int k) {
        if (head == null || head.next == null) return head;   // 0 or 1 node: rotating changes nothing
        int n = 1;                                        // counts head already
        ListNode tail = head;
        while (tail.next != null) { tail = tail.next; n++; }   // find the tail and the length together
        int shift = k % n;                                // every n rotations bring the list back
        if (shift == 0) return head;                      // whole turns change nothing
        tail.next = head;                                 // close the ring
        ListNode newTail = head;
        // node number n - shift (1-based) becomes the new tail
        for (int i = 1; i < n - shift; i++) newTail = newTail.next;
        ListNode newHead = newTail.next;                  // the node after the new tail starts the rotated list
        newTail.next = null;                              // open the ring again
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

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Runs every approach on a fresh copy of the list; the brute force is skipped when k is too large for it. */
    static void verify(int[] arr, int k, int[] expected, boolean includeBruteForce) {
        String in = Arrays.toString(arr) + " k=" + k;
        if (includeBruteForce) check(Arrays.equals(toArray(bruteForce(fromArray(arr), k)), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(better(fromArray(arr), k)), expected), "better " + in);
        check(Arrays.equals(toArray(optimal(fromArray(arr), k)), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 2, new int[]{4, 5, 1, 2, 3}, true);
        verify(new int[]{0, 1, 2}, 4, new int[]{2, 0, 1}, true);                 // k > n: only k mod n matters
        verify(new int[]{1, 2, 3}, 0, new int[]{1, 2, 3}, true);                 // k = 0
        verify(new int[]{1, 2, 3}, 3, new int[]{1, 2, 3}, true);                 // k = n is a full turn
        verify(new int[]{1, 2, 3}, 6, new int[]{1, 2, 3}, true);                 // two full turns
        verify(new int[]{1}, 7, new int[]{1}, true);                             // single node
        verify(new int[]{}, 3, new int[]{}, true);                               // empty list
        verify(new int[]{1, 2}, 1, new int[]{2, 1}, true);                       // two nodes swap
        verify(new int[]{1, 2, 3, 4, 5}, 2_000_000_003, new int[]{3, 4, 5, 1, 2}, false);   // huge k: 2000000003 mod 5 = 3
        System.out.println("OK P2843_RotateALL");
    }
}
