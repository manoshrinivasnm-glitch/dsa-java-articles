import java.util.*;

/** TUF 2848 - Add two numbers as LinkedList. Each list stores a non-negative number with its digits in reverse order; return their sum in the same form. */
public class P2848_AddTwoNumbersAsLinkedList {

    static class ListNode {
        int val;                                  // the value stored in this node
        ListNode next;                            // the following node, or null at the tail
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Copy the digits into lists, add them column by column into an array, then build the answer list. O(m+n) time, O(m+n) extra space. */
    static ListNode bruteForce(ListNode l1, ListNode l2) {
        // digits of each number, least significant first
        List<Integer> a = new ArrayList<>(), b = new ArrayList<>();
        for (ListNode t = l1; t != null; t = t.next) a.add(t.val);   // copy the digits of l1
        for (ListNode t = l2; t != null; t = t.next) b.add(t.val);   // copy the digits of l2
        int len = Math.max(a.size(), b.size());   // the longer number decides how many columns to add
        int[] sum = new int[len + 1];             // one extra slot for a final carry
        int carry = 0;                            // carry into the next column
        for (int i = 0; i < len; i++) {           // add column i, like on paper
            // a missing digit counts as 0
            int d = carry + (i < a.size() ? a.get(i) : 0) + (i < b.size() ? b.get(i) : 0);
            sum[i] = d % 10;                      // keep the units digit in this column
            carry = d / 10;                       // the tens digit moves on
        }
        sum[len] = carry;                         // the last carry becomes the top digit
        int used = carry > 0 ? len + 1 : len;     // drop the extra slot if there was no final carry
        ListNode dummy = new ListNode(0), tail = dummy;   // dummy anchors the answer, tail is its last node
        for (int i = 0; i < used; i++) {          // build the answer list digit by digit
            tail.next = new ListNode(sum[i]);
            tail = tail.next;
        }
        return dummy.next;
    }

    /** Approach 2: optimal, one simultaneous walk. Add digit + digit + carry, append the units digit, carry the tens. O(max(m, n)) time, O(1) extra space besides the output. */
    static ListNode optimal(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), tail = dummy;   // dummy anchors the answer, tail is its last node
        int carry = 0;                            // carry into the next column
        while (l1 != null || l2 != null || carry != 0) {   // a leftover carry still needs a node
            int sum = carry;                      // start from the carry of the previous column
            if (l1 != null) { sum += l1.val; l1 = l1.next; }   // add l1's digit and step past it
            if (l2 != null) { sum += l2.val; l2 = l2.next; }   // add l2's digit and step past it
            tail.next = new ListNode(sum % 10);   // the units digit becomes the next answer node
            tail = tail.next;
            carry = sum / 10;                     // the tens digit is carried into the next column
        }
        return dummy.next;                        // skip the dummy
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

    static String label(int[] a, int[] b) {
        if (a.length + b.length > 20) return "lists of " + a.length + " and " + b.length + " digits";
        return Arrays.toString(a) + " + " + Arrays.toString(b);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int[] b, int[] expected) {
        String in = label(a, b);
        ListNode l1 = fromArray(a), l2 = fromArray(b);
        check(Arrays.equals(toArray(bruteForce(l1, l2)), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(optimal(l1, l2)), expected), "optimal " + in);
        check(Arrays.equals(toArray(l1), a) && Arrays.equals(toArray(l2), b), "inputs must not change " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 4, 3}, new int[]{5, 6, 4}, new int[]{7, 0, 8});              // 342 + 465 = 807
        verify(new int[]{0}, new int[]{0}, new int[]{0});                                 // edge: 0 + 0
        verify(new int[]{9, 9, 9, 9, 9, 9, 9}, new int[]{9, 9, 9, 9},
               new int[]{8, 9, 9, 9, 0, 0, 0, 1});                                        // 9999999 + 9999 = 10009998
        verify(new int[]{5}, new int[]{5}, new int[]{0, 1});                              // final carry creates a node
        verify(new int[]{1, 8}, new int[]{0}, new int[]{1, 8});                           // 81 + 0
        verify(new int[]{0}, new int[]{3, 2, 1}, new int[]{3, 2, 1});                     // shorter list first
        verify(new int[]{}, new int[]{}, new int[]{});                                    // edge: two empty lists

        int[] nines = new int[100];                                                       // 10^100 - 1, far beyond long
        Arrays.fill(nines, 9);
        int[] power = new int[101];                                                       // 10^100
        power[100] = 1;
        verify(nines, new int[]{1}, power);

        int[] ones = new int[60_000], twos = new int[60_000], threes = new int[60_000];
        Arrays.fill(ones, 1);
        Arrays.fill(twos, 2);
        Arrays.fill(threes, 3);
        verify(ones, twos, threes);                                                       // long inputs, no carries

        System.out.println("OK P2848_AddTwoNumbersAsLinkedList");
    }
}
