import java.util.*;

/** TUF 625 - Add two numbers in Linked List. Digits are stored least significant first; return the sum in the same format. */
public class P625_AddTwoNumbersInLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: convert both lists to a long, add, convert back. Only valid while both numbers have at most 18 digits. O(n + m) time, O(1) extra space. */
    static ListNode bruteForce(ListNode l1, ListNode l2) {
        long sum = toLong(l1) + toLong(l2);
        if (sum == 0) return new ListNode(0);
        ListNode dummy = new ListNode(0), tail = dummy;
        while (sum > 0) {
            tail.next = new ListNode((int) (sum % 10));
            tail = tail.next;
            sum /= 10;
        }
        return dummy.next;
    }

    /** Reads a reversed-digit list into a long; refuses inputs that cannot fit. */
    static long toLong(ListNode head) {
        long value = 0, place = 1;
        int digits = 0;
        for (ListNode t = head; t != null; t = t.next) {
            if (++digits > 18) throw new IllegalArgumentException("more than 18 digits: does not fit in a long");
            value += t.val * place;
            place *= 10;
        }
        return value;
    }

    /** Approach 2: walk both lists together, one digit at a time, writing each digit of the result into a new list. O(max(n, m)) time, O(max(n, m)) space for the answer. */
    static ListNode better(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0), tail = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            if (l1 != null) { sum += l1.val; l1 = l1.next; }
            if (l2 != null) { sum += l2.val; l2 = l2.next; }
            tail.next = new ListNode(sum % 10);
            tail = tail.next;
            carry = sum / 10;
        }
        return dummy.next;
    }

    /** Approach 3: reuse the nodes of l1 for the answer and borrow l2's tail when l2 is longer. O(max(n, m)) time, O(1) extra space (at most one new node). */
    static ListNode optimal(ListNode l1, ListNode l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;
        ListNode head = l1, prev = null;
        int carry = 0;
        while (l1 != null && l2 != null) {                // digits present in both numbers
            int sum = l1.val + l2.val + carry;
            l1.val = sum % 10;
            carry = sum / 10;
            prev = l1;
            l1 = l1.next;
            l2 = l2.next;
        }
        if (l1 == null) {                                 // l2 is longer: continue on l2's remaining nodes
            prev.next = l2;
            l1 = l2;
        }
        while (l1 != null && carry != 0) {                // push the carry through the remaining digits
            int sum = l1.val + carry;
            l1.val = sum % 10;
            carry = sum / 10;
            prev = l1;
            l1 = l1.next;
        }
        if (carry != 0) prev.next = new ListNode(carry);  // ran out of digits with a carry left: one new node
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

    /** Runs every approach; the lists are rebuilt for each call because optimal() modifies its inputs. */
    static void verify(int[] a, int[] b, int[] expected, boolean includeBruteForce) {
        String in = Arrays.toString(a) + " + " + Arrays.toString(b);
        if (includeBruteForce) check(Arrays.equals(toArray(bruteForce(fromArray(a), fromArray(b))), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(better(fromArray(a), fromArray(b))), expected), "better " + in);
        check(Arrays.equals(toArray(optimal(fromArray(a), fromArray(b))), expected), "optimal " + in);
        check(Arrays.equals(toArray(optimal(fromArray(b), fromArray(a))), expected), "optimal (swapped) " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{2, 4, 3}, new int[]{5, 6, 4}, new int[]{7, 0, 8}, true);                // 342 + 465 = 807
        verify(new int[]{0}, new int[]{0}, new int[]{0}, true);                                  // 0 + 0 = 0
        verify(new int[]{9, 9, 9, 9, 9, 9, 9}, new int[]{9, 9, 9, 9}, new int[]{8, 9, 9, 9, 0, 0, 0, 1}, true);
        verify(new int[]{9, 9}, new int[]{1}, new int[]{0, 0, 1}, true);                         // carry runs off the end
        verify(new int[]{1}, new int[]{9, 9}, new int[]{0, 0, 1}, true);                         // same, with the longer list second
        verify(new int[]{5}, new int[]{5}, new int[]{0, 1}, true);                               // single digits with carry
        verify(new int[]{1, 8}, new int[]{0}, new int[]{1, 8}, true);                            // adding zero
        int[] nines = new int[25];
        Arrays.fill(nines, 9);
        int[] twiceNines = new int[26];                                                          // 2 * (10^25 - 1) = 199...98
        Arrays.fill(twiceNines, 9);
        twiceNines[0] = 8;
        twiceNines[25] = 1;
        verify(nines, nines, twiceNines, false);                                                 // too long for a long
        boolean threw = false;
        try { bruteForce(fromArray(nines), fromArray(nines)); } catch (IllegalArgumentException e) { threw = true; }
        check(threw, "bruteForce must refuse inputs longer than 18 digits");
        System.out.println("OK P625_AddTwoNumbersInLinkedList");
    }
}
