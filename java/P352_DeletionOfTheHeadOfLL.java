import java.util.*;

/** TUF 352 - Deletion of the head of LL. Return the new head after removing the first node. */
public class P352_DeletionOfTheHeadOfLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Delete the head: the second node becomes the head. O(1) time, O(1) space. */
    static ListNode deleteHead(ListNode head) {
        if (head == null) return null;             // nothing to delete
        ListNode newHead = head.next;
        head.next = null;                          // detach the removed node so it no longer points into the list
        return newHead;
    }

    /** For contrast: deleting the tail needs the second-last node, so the whole list is walked. O(n) time, O(1) space. */
    static ListNode deleteTail(ListNode head) {
        if (head == null || head.next == null) return null;     // empty or single node: the list becomes empty
        ListNode temp = head;
        while (temp.next.next != null) temp = temp.next;        // stop on the second-last node
        temp.next = null;
        return head;
    }

    /** Generalisation: delete the k-th node (1-based). k == 1 is deletion of the head; an out-of-range k leaves the list unchanged. O(k) time. */
    static ListNode deleteAtPosition(ListNode head, int k) {
        if (head == null || k < 1) return head;
        if (k == 1) return deleteHead(head);
        ListNode prev = head;
        for (int i = 1; i < k - 1 && prev != null; i++) prev = prev.next;    // stop on node k - 1
        if (prev == null || prev.next == null) return head;                  // there is no k-th node
        ListNode removed = prev.next;
        prev.next = removed.next;                                             // bypass the k-th node
        removed.next = null;
        return head;
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

    static void verify(int[] arr) {
        int[] withoutHead = arr.length == 0 ? arr : Arrays.copyOfRange(arr, 1, arr.length);
        int[] withoutTail = arr.length == 0 ? arr : Arrays.copyOf(arr, arr.length - 1);
        String in = Arrays.toString(arr);
        check(Arrays.equals(toArray(deleteHead(fromArray(arr))), withoutHead), "deleteHead " + in);
        check(Arrays.equals(toArray(deleteAtPosition(fromArray(arr), 1)), withoutHead), "deleteAtPosition(1) " + in);
        check(Arrays.equals(toArray(deleteTail(fromArray(arr))), withoutTail), "deleteTail " + in);
        check(Arrays.equals(toArray(deleteAtPosition(fromArray(arr), arr.length)), withoutTail), "deleteAtPosition(n) " + in);
        check(Arrays.equals(toArray(deleteAtPosition(fromArray(arr), arr.length + 1)), arr), "deleteAtPosition(n+1) leaves " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5});
        verify(new int[]{});                           // edge: deleting from an empty list is a no-op
        verify(new int[]{42});                         // single node: the list becomes empty
        verify(new int[]{5, 5});                       // two equal values
        verify(new int[]{-1, 0, 7, -9});

        check(Arrays.equals(toArray(deleteAtPosition(fromArray(new int[]{1, 2, 3, 4}), 3)), new int[]{1, 2, 4}), "delete in the middle");
        check(Arrays.equals(toArray(deleteAtPosition(fromArray(new int[]{1, 2}), 0)), new int[]{1, 2}), "k = 0 is rejected");

        ListNode first = fromArray(new int[]{1, 2, 3});
        ListNode rest = deleteHead(first);
        check(first.val == 1 && first.next == null && rest.val == 2 && rest.next.val == 3, "removed node is detached, rest is intact");

        ListNode h = fromArray(new int[]{1, 2, 3});
        for (int i = 0; i < 5; i++) h = deleteHead(h);
        check(h == null, "deleting more times than there are nodes ends at the empty list");

        System.out.println("OK P352_DeletionOfTheHeadOfLL");
    }
}
