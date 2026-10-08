import java.util.*;

/** TUF 621 - Find the intersection point of Y LL. Return the first node shared by two lists, or null. */
public class P621_FindTheIntersectionPointOfYLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** Approach 1: for every node of A, scan all of B looking for the same object. O(m * n) time, O(1) space. */
    static ListNode bruteForce(ListNode headA, ListNode headB) {
        for (ListNode a = headA; a != null; a = a.next) {
            for (ListNode b = headB; b != null; b = b.next) {
                if (a == b) return a;                   // same object, not merely the same value
            }
        }
        return null;
    }

    /** Approach 2: store every node of A in a hash set, then walk B. O(m + n) time, O(m) space. */
    static ListNode hashing(ListNode headA, ListNode headB) {
        Set<ListNode> nodesOfA = new HashSet<>();      // ListNode has no equals(), so the set compares identity
        for (ListNode a = headA; a != null; a = a.next) nodesOfA.add(a);
        for (ListNode b = headB; b != null; b = b.next) {
            if (nodesOfA.contains(b)) return b;
        }
        return null;
    }

    /** Approach 3: skip the length difference in the longer list, then walk both in lock-step. O(m + n) time, O(1) space. */
    static ListNode lengthDifference(ListNode headA, ListNode headB) {
        int lenA = length(headA), lenB = length(headB);
        ListNode a = headA, b = headB;
        while (lenA > lenB) { a = a.next; lenA--; }     // at most one of these two loops runs
        while (lenB > lenA) { b = b.next; lenB--; }
        while (a != b) {                                // equal distance to the end from here on
            a = a.next;
            b = b.next;
        }
        return a;                                       // the shared node, or null if both ran off the end
    }

    static int length(ListNode head) {
        int len = 0;
        for (ListNode cur = head; cur != null; cur = cur.next) len++;
        return len;
    }

    /** Approach 4: two pointers that restart on the other list's head. O(m + n) time, O(1) space. */
    static ListNode optimal(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) {
            a = (a == null) ? headB : a.next;           // after m + n steps both have walked the same distance
            b = (b == null) ? headA : b.next;
        }
        return a;
    }

    // ---------------------------------------------------------------- helpers
    /** Returns a list made of the values in prefix followed by the existing nodes starting at tail. */
    static ListNode prepend(int[] prefix, ListNode tail) {
        ListNode head = tail;
        for (int i = prefix.length - 1; i >= 0; i--) {
            ListNode node = new ListNode(prefix[i]);
            node.next = head;
            head = node;
        }
        return head;
    }

    static int[] range(int from, int count) {
        int[] a = new int[count];
        for (int i = 0; i < count; i++) a[i] = from + i;
        return a;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] onlyA, int[] onlyB, int[] shared) {
        ListNode common = prepend(shared, null);       // null when shared is empty: the lists never meet
        ListNode headA = prepend(onlyA, common), headB = prepend(onlyB, common);
        String in = "A-only=" + onlyA.length + " B-only=" + onlyB.length + " shared=" + shared.length;
        check(bruteForce(headA, headB) == common, "bruteForce " + in);
        check(hashing(headA, headB) == common, "hashing " + in);
        check(lengthDifference(headA, headB) == common, "lengthDifference " + in);
        check(optimal(headA, headB) == common, "optimal " + in);
        check(bruteForce(headB, headA) == common, "bruteForce swapped " + in);
        check(hashing(headB, headA) == common, "hashing swapped " + in);
        check(lengthDifference(headB, headA) == common, "lengthDifference swapped " + in);
        check(optimal(headB, headA) == common, "optimal swapped " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{4, 1}, new int[]{5, 6, 1}, new int[]{8, 4, 5});    // meet at the node holding 8
        verify(new int[]{1, 9, 1}, new int[]{3}, new int[]{2, 4});          // meet at the node holding 2
        verify(new int[]{2, 6, 4}, new int[]{1, 5}, new int[]{});           // no shared node
        verify(new int[]{}, new int[]{7, 8}, new int[]{3, 4});              // intersection is head of A
        verify(new int[]{}, new int[]{}, new int[]{1, 2, 3});               // both heads are the same node
        verify(new int[]{}, new int[]{}, new int[]{});                      // edge: both lists empty
        verify(new int[]{}, new int[]{1}, new int[]{});                     // edge: one list empty
        verify(new int[]{1, 2, 3}, new int[]{1, 2, 3}, new int[]{});        // equal values, different nodes
        verify(new int[]{9}, new int[]{9}, new int[]{9});                   // equal values, one shared node
        verify(range(0, 2000), range(10_000, 700), range(50_000, 1500));    // long, very uneven lists
        System.out.println("OK P621_FindTheIntersectionPointOfYLL");
    }
}
