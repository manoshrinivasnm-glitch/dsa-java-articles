import java.util.*;

/** TUF 2846 - Find the intersection point of Y LL. Two singly linked lists share their tail from some node on; return that node, or null if they never meet. */
public class P2846_FindTheIntersectionPointOfYLL {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. For every node of A, scan all of B looking for the same node object. O(m * n) time, O(1) space. */
    static ListNode bruteForce(ListNode headA, ListNode headB) {
        for (ListNode a = headA; a != null; a = a.next) {
            for (ListNode b = headB; b != null; b = b.next) {
                if (a == b) return a;              // identity, not value
            }
        }
        return null;
    }

    /** Approach 2: better. Put every node of A in a hash set; the first node of B that is in the set is the intersection. O(m + n) time, O(m) space. */
    static ListNode better(ListNode headA, ListNode headB) {
        Set<ListNode> nodesOfA = new HashSet<>();
        for (ListNode a = headA; a != null; a = a.next) nodesOfA.add(a);
        for (ListNode b = headB; b != null; b = b.next) {
            if (nodesOfA.contains(b)) return b;
        }
        return null;
    }

    /** Approach 3: optimal via lengths. Advance the longer list by the length difference, then walk both together until they coincide. O(m + n) time, O(1) space. */
    static ListNode optimalLengthDifference(ListNode headA, ListNode headB) {
        int lenA = length(headA), lenB = length(headB);
        ListNode a = headA, b = headB;
        for (int i = 0; i < lenA - lenB; i++) a = a.next;    // only one of these two loops actually runs
        for (int i = 0; i < lenB - lenA; i++) b = b.next;
        while (a != b) {                                     // both now have the same number of nodes left
            a = a.next;
            b = b.next;
        }
        return a;                                            // the meeting node, or null when both ran off the end
    }

    static int length(ListNode head) {
        int n = 0;
        for (ListNode t = head; t != null; t = t.next) n++;
        return n;
    }

    /** Approach 4: optimal, two pointers that switch lists. Each pointer walks A then B (or B then A), so both cover m + n steps and arrive aligned. O(m + n) time, O(1) space. */
    static ListNode optimalTwoPointer(ListNode headA, ListNode headB) {
        ListNode a = headA, b = headB;
        while (a != b) {
            a = (a == null) ? headB : a.next;    // when a list ends, jump to the head of the other one
            b = (b == null) ? headA : b.next;
        }
        return a;                                // the intersection, or null when both are null together
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] arr) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : arr) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    /** Builds a fresh chain holding prefix and links its last node to tail (which may be null). */
    static ListNode prepend(int[] prefix, ListNode tail) {
        ListNode dummy = new ListNode(0), t = dummy;
        for (int v : prefix) { t.next = new ListNode(v); t = t.next; }
        t.next = tail;
        return dummy.next;
    }

    static int[] fill(int n, int start) {
        int[] out = new int[n];
        for (int i = 0; i < n; i++) out[i] = start + i;
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] prefixA, int[] prefixB, int[] common) {
        ListNode shared = fromArray(common);                 // null when common is empty: no intersection
        ListNode headA = prepend(prefixA, shared);
        ListNode headB = prepend(prefixB, shared);
        String in = "A=" + prefixA.length + " B=" + prefixB.length + " common=" + common.length;
        check(bruteForce(headA, headB) == shared, "bruteForce " + in);
        check(better(headA, headB) == shared, "better " + in);
        check(optimalLengthDifference(headA, headB) == shared, "optimalLengthDifference " + in);
        check(optimalTwoPointer(headA, headB) == shared, "optimalTwoPointer " + in);
        check(bruteForce(headB, headA) == shared, "bruteForce swapped " + in);
        check(better(headB, headA) == shared, "better swapped " + in);
        check(optimalLengthDifference(headB, headA) == shared, "optimalLengthDifference swapped " + in);
        check(optimalTwoPointer(headB, headA) == shared, "optimalTwoPointer swapped " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{4, 1}, new int[]{5, 6, 1}, new int[]{8, 4, 5});       // classic example, meet at 8
        verify(new int[]{1, 9, 1}, new int[]{3}, new int[]{2, 4});             // meet at 2
        verify(new int[]{2, 6, 4}, new int[]{1, 5}, new int[]{});              // two separate lists: no intersection
        verify(new int[]{}, new int[]{1, 2}, new int[]{3, 4});                 // intersection is the head of A
        verify(new int[]{}, new int[]{}, new int[]{1, 2, 3});                  // same list twice: intersection is the shared head
        verify(new int[]{}, new int[]{}, new int[]{});                         // edge: both empty
        verify(new int[]{}, new int[]{1}, new int[]{});                        // A empty, B not
        verify(new int[]{7}, new int[]{7}, new int[]{7});                      // equal values everywhere, only one shared node
        verify(new int[]{1, 2, 3, 4, 5, 6}, new int[]{9}, new int[]{10});     // very uneven lengths

        verify(fill(3_000, 0), fill(1_000, 100_000), fill(2_000, 200_000));   // longer lists, brute force is 5000 x 3000 steps
        verify(fill(5, 0), fill(5, 0), fill(0, 0));                            // same values, disjoint nodes: no intersection

        System.out.println("OK P2846_FindTheIntersectionPointOfYLL");
    }
}
