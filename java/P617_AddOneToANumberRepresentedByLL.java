import java.util.*;

/** TUF 617 - Add one to a number represented by LL. Digits are stored most significant first; return the head of the list for number + 1. */
public class P617_AddOneToANumberRepresentedByLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: reverse the list so the units digit comes first, add with carry, reverse back. O(n) time, O(1) space, three passes. */
    static ListNode bruteForce(ListNode head) {
        if (head == null) return new ListNode(1);          // an empty list stands for 0
        head = reverse(head);
        int carry = 1;                                     // the +1 we are adding
        ListNode cur = head, last = null;
        while (cur != null && carry > 0) {
            int sum = cur.val + carry;
            cur.val = sum % 10;
            carry = sum / 10;
            last = cur;
            cur = cur.next;
        }
        if (carry > 0) last.next = new ListNode(carry);    // every digit was 9: append the new most significant digit (the list is still reversed)
        return reverse(head);
    }

    /** Standard in-place reversal, used before and after the addition. */
    static ListNode reverse(ListNode head) {
        ListNode prev = null, cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = prev;
            prev = cur;
            cur = next;
        }
        return prev;
    }

    /** Approach 2: recursion. The deepest call handles the units digit and each call returns the carry to its caller. O(n) time, O(n) stack. */
    static ListNode better(ListNode head) {
        if (head == null) return new ListNode(1);
        int carry = addOneRec(head);
        if (carry == 0) return head;
        return new ListNode(carry, head);                  // the carry out of the first digit becomes a new leading node
    }

    /** Adds 1 to the number formed by node..end, in place, and returns the carry out of node. */
    static int addOneRec(ListNode node) {
        if (node == null) return 1;                        // base case: the "+1" arrives at the units digit
        int carry = addOneRec(node.next);
        int sum = node.val + carry;
        node.val = sum % 10;
        return sum / 10;
    }

    /** Approach 3: one forward pass. Remember the last digit that is not 9; increment it and zero everything after it. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head) {
        if (head == null) return new ListNode(1);
        ListNode lastNotNine = null;
        for (ListNode cur = head; cur != null; cur = cur.next) {
            if (cur.val != 9) lastNotNine = cur;
        }
        if (lastNotNine == null) {                         // every digit is 9: 99...9 + 1 = 100...0
            lastNotNine = new ListNode(0, head);           // a leading 0 that is about to become the new 1
            head = lastNotNine;
        }
        lastNotNine.val++;
        for (ListNode cur = lastNotNine.next; cur != null; cur = cur.next) cur.val = 0;
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

    static void verify(int[] digits, int[] expected) {
        String in = Arrays.toString(digits);
        check(Arrays.equals(toArray(bruteForce(fromArray(digits))), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(better(fromArray(digits))), expected), "better " + in);
        check(Arrays.equals(toArray(optimal(fromArray(digits))), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, new int[]{1, 2, 4});
        verify(new int[]{9, 9, 9}, new int[]{1, 0, 0, 0});          // every digit is 9: the list grows by one node
        verify(new int[]{1, 9, 9}, new int[]{2, 0, 0});             // the carry stops at the first non-9 from the right
        verify(new int[]{0}, new int[]{1});                         // single digit
        verify(new int[]{9}, new int[]{1, 0});                      // single 9
        verify(new int[]{2, 9, 0, 9}, new int[]{2, 9, 1, 0});       // 9s to the left of the last non-9 are untouched
        verify(new int[]{}, new int[]{1});                          // empty list stands for 0
        int[] nines = new int[20], expected = new int[21];          // far too many digits for a long
        Arrays.fill(nines, 9);
        expected[0] = 1;
        verify(nines, expected);
        System.out.println("OK P617_AddOneToANumberRepresentedByLL");
    }
}
