import java.util.*;

/** TUF 610 - Remove duplicates from sorted DLL. Keep one node per distinct value and return the head. */
public class P610_RemoveDuplicatesFromSortedDLL {

    static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
    }

    /** Approach 1: a hash set of the values already kept; unlink any node whose value was seen before. O(n) time, O(n) space; works even if the list were not sorted. */
    static Node bruteForce(Node head) {
        Set<Integer> seen = new HashSet<>();
        Node cur = head;
        while (cur != null) {
            Node next = cur.next;
            if (!seen.add(cur.val)) {                     // add returns false when the value is already present
                cur.prev.next = cur.next;                 // a duplicate is never the head, so prev is non-null
                if (cur.next != null) cur.next.prev = cur.prev;
                cur.prev = null;
                cur.next = null;
            }
            cur = next;
        }
        return head;
    }

    /** Approach 2: in a sorted list equal values are adjacent, so from each kept node jump over the whole run of equal values. O(n) time, O(1) space. */
    static Node optimal(Node head) {
        Node cur = head;
        while (cur != null && cur.next != null) {
            Node runEnd = cur.next;
            while (runEnd != null && runEnd.val == cur.val) runEnd = runEnd.next;   // first node with a different value, or null
            cur.next = runEnd;                            // splice out the whole run at once
            if (runEnd != null) runEnd.prev = cur;
            cur = runEnd;
        }
        return head;
    }

    /** Approach 3: the same idea written as "delete the next node while it equals the current one". O(n) time, O(1) space. */
    static Node optimalOneAtATime(Node head) {
        for (Node cur = head; cur != null; cur = cur.next) {
            while (cur.next != null && cur.next.val == cur.val) {
                Node dup = cur.next;
                cur.next = dup.next;
                if (dup.next != null) dup.next.prev = cur;
                dup.prev = null;
                dup.next = null;
            }
        }
        return head;
    }

    // ---------------------------------------------------------------- helpers
    static Node fromArray(int[] arr) {
        Node head = null, tail = null;
        for (int v : arr) {
            Node node = new Node(v);
            if (head == null) head = node;
            else { tail.next = node; node.prev = tail; }
            tail = node;
        }
        return head;
    }

    static int[] toArray(Node head) {
        List<Integer> out = new ArrayList<>();
        for (Node t = head; t != null; t = t.next) out.add(t.val);
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    /** True when the head has no predecessor and every next pointer is mirrored by the matching prev pointer. */
    static boolean wellFormed(Node head) {
        if (head == null) return true;
        if (head.prev != null) return false;
        for (Node t = head; t != null; t = t.next) {
            if (t.next != null && t.next.prev != t) return false;
        }
        return true;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] sortedValues, int[] expected) {
        String in = Arrays.toString(sortedValues);
        Node r1 = bruteForce(fromArray(sortedValues));
        Node r2 = optimal(fromArray(sortedValues));
        Node r3 = optimalOneAtATime(fromArray(sortedValues));
        check(Arrays.equals(toArray(r1), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(r2), expected), "optimal " + in);
        check(Arrays.equals(toArray(r3), expected), "optimalOneAtATime " + in);
        check(wellFormed(r1) && wellFormed(r2) && wellFormed(r3), "prev pointers broken for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 1, 1, 2, 3, 3, 4}, new int[]{1, 2, 3, 4});
        verify(new int[]{1, 2, 3}, new int[]{1, 2, 3});                    // already distinct
        verify(new int[]{5, 5, 5, 5}, new int[]{5});                       // one value only
        verify(new int[]{}, new int[]{});                                  // empty list
        verify(new int[]{7}, new int[]{7});                                // single node
        verify(new int[]{1, 1, 2, 2, 3, 3}, new int[]{1, 2, 3});           // duplicates at the tail too
        verify(new int[]{-3, -3, 0, 0, 0, 2}, new int[]{-3, 0, 2});        // negatives and a longer run
        System.out.println("OK P610_RemoveDuplicatesFromSortedDLL");
    }
}
