import java.util.*;

/** TUF 609 - Delete all occurrences of a key in DLL. Remove every node whose value equals key and return the new head. */
public class P609_DeleteAllOccurrencesOfAKeyInDLL {

    static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
    }

    /** Approach 1: copy the surviving values into a list and build a brand-new DLL from them. O(n) time, O(n) space. */
    static Node bruteForce(Node head, int key) {
        List<Integer> kept = new ArrayList<>();
        for (Node t = head; t != null; t = t.next) {
            if (t.val != key) kept.add(t.val);
        }
        Node newHead = null, tail = null;
        for (int v : kept) {
            Node node = new Node(v);
            if (newHead == null) newHead = node;
            else { tail.next = node; node.prev = tail; }
            tail = node;
        }
        return newHead;
    }

    /** Approach 2: one pass, unlinking each matching node in place through its prev and next pointers. O(n) time, O(1) space. */
    static Node optimal(Node head, int key) {
        Node cur = head;
        while (cur != null) {
            Node next = cur.next;                         // save it before any link is cut
            if (cur.val == key) {
                if (cur.prev != null) cur.prev.next = cur.next;
                else head = cur.next;                     // deleting the current head
                if (cur.next != null) cur.next.prev = cur.prev;
                cur.prev = null;
                cur.next = null;
            }
            cur = next;
        }
        return head;
    }

    /** Approach 3: the same unlinking with a sentinel node in front of the head, so the head needs no special case. O(n) time, O(1) space. */
    static Node optimalWithSentinel(Node head, int key) {
        Node sentinel = new Node(0);
        sentinel.next = head;
        if (head != null) head.prev = sentinel;
        Node cur = head;
        while (cur != null) {
            Node next = cur.next;
            if (cur.val == key) {
                cur.prev.next = cur.next;                 // cur.prev is never null thanks to the sentinel
                if (cur.next != null) cur.next.prev = cur.prev;
                cur.prev = null;
                cur.next = null;
            }
            cur = next;
        }
        Node newHead = sentinel.next;
        if (newHead != null) newHead.prev = null;         // detach the sentinel again
        return newHead;
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

    static void verify(int[] arr, int key, int[] expected) {
        String in = Arrays.toString(arr) + " key " + key;
        Node r1 = bruteForce(fromArray(arr), key);
        Node r2 = optimal(fromArray(arr), key);
        Node r3 = optimalWithSentinel(fromArray(arr), key);
        check(Arrays.equals(toArray(r1), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(r2), expected), "optimal " + in);
        check(Arrays.equals(toArray(r3), expected), "optimalWithSentinel " + in);
        check(wellFormed(r1) && wellFormed(r2) && wellFormed(r3), "prev pointers broken for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{10, 4, 10, 10, 6, 10}, 10, new int[]{4, 6});      // key at head, tail and in a run
        verify(new int[]{1, 2, 3}, 4, new int[]{1, 2, 3});                 // key absent: nothing changes
        verify(new int[]{5, 5, 5}, 5, new int[]{});                        // every node goes: the list becomes empty
        verify(new int[]{}, 1, new int[]{});                               // empty list
        verify(new int[]{7}, 7, new int[]{});                              // single node that matches
        verify(new int[]{7}, 8, new int[]{7});                             // single node that does not match
        verify(new int[]{1, 2, 2, 3, 2}, 2, new int[]{1, 3});              // consecutive duplicates in the middle
        verify(new int[]{2, 1}, 2, new int[]{1});                          // only the head is deleted
        verify(new int[]{1, 2}, 2, new int[]{1});                          // only the tail is deleted
        System.out.println("OK P609_DeleteAllOccurrencesOfAKeyInDLL");
    }
}
