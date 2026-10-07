import java.util.*;

/** TUF 900 - Reverse a Doubly Linked List. Return the head of the reversed list. */
public class P900_ReverseADoublyLinkedList {

    static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
        Node(int val, Node prev, Node next) { this.val = val; this.prev = prev; this.next = next; }
    }

    /** Approach 1: brute force. Push every value on a stack, then overwrite the values front to back. The nodes stay where they are. O(n) time, O(n) space. */
    static Node bruteForce(Node head) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (Node t = head; t != null; t = t.next) stack.push(t.val);
        for (Node t = head; t != null; t = t.next) t.val = stack.pop();    // pops come out in reverse order
        return head;
    }

    /** Approach 2: optimal. Swap prev and next inside every node; the last node visited is the new head. O(n) time, O(1) space. */
    static Node optimal(Node head) {
        Node current = head;
        Node newHead = head;
        while (current != null) {
            Node oldNext = current.next;      // remember where to go before the links are flipped
            current.next = current.prev;      // flip
            current.prev = oldNext;           // flip
            newHead = current;                // whichever node we flip last is the old tail = new head
            current = oldNext;
        }
        return newHead;
    }

    /** Approach 2, the classic TUF form: the same swap, written with a single `last` pointer. O(n) time, O(1) space. */
    static Node optimalClassic(Node head) {
        if (head == null || head.next == null) return head;
        Node last = null;
        Node current = head;
        while (current != null) {
            last = current.prev;
            current.prev = current.next;
            current.next = last;
            current = current.prev;           // prev now holds the old next
        }
        return last.prev;                     // last is the old tail's old prev; after the swap its prev is the old tail
    }

    // ---------------------------------------------------------------- helpers
    static Node fromArray(int[] arr) {
        Node head = null, tail = null;
        for (int v : arr) {
            Node node = new Node(v, tail, null);
            if (tail == null) head = node; else tail.next = node;
            tail = node;
        }
        return head;
    }

    static int[] forward(Node head) {
        List<Integer> out = new ArrayList<>();
        for (Node t = head; t != null; t = t.next) out.add(t.val);
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    static int[] backward(Node head) {
        Node tail = null;
        for (Node t = head; t != null; t = t.next) tail = t;
        List<Integer> out = new ArrayList<>();
        for (Node t = tail; t != null; t = t.prev) out.add(t.val);
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    static boolean isWellFormed(Node head) {
        if (head == null) return true;
        if (head.prev != null) return false;
        for (Node t = head; t != null; t = t.next) {
            if (t.next != null && t.next.prev != t) return false;
        }
        return true;
    }

    static int[] reversed(int[] arr) {
        int[] out = new int[arr.length];
        for (int i = 0; i < arr.length; i++) out[i] = arr[arr.length - 1 - i];
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void checkList(Node head, int[] expected, String msg) {
        check(Arrays.equals(forward(head), expected), msg + ": forward " + Arrays.toString(forward(head)));
        check(Arrays.equals(backward(head), reversed(expected)), msg + ": backward " + Arrays.toString(backward(head)));
        check(isWellFormed(head), msg + ": links inconsistent");
    }

    static void verify(int[] arr) {
        int[] expected = reversed(arr);
        String in = Arrays.toString(arr);
        checkList(bruteForce(fromArray(arr)), expected, "bruteForce " + in);
        checkList(optimal(fromArray(arr)), expected, "optimal " + in);
        checkList(optimalClassic(fromArray(arr)), expected, "optimalClassic " + in);
        checkList(optimal(optimal(fromArray(arr))), arr, "reversing twice restores " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5});
        verify(new int[]{});                        // edge: empty list
        verify(new int[]{1});                       // single node is its own reverse
        verify(new int[]{1, 2});                    // two nodes
        verify(new int[]{3, 3, 3});                 // equal values
        verify(new int[]{-4, 0, 9, -1, 7, 2});

        Node head = fromArray(new int[]{1, 2, 3});
        Node oldTail = head.next.next;
        Node newHead = optimal(head);
        check(newHead == oldTail && newHead.prev == null && head.next == null && head.prev == newHead.next, "optimal re-links the existing nodes");
        Node same = fromArray(new int[]{1, 2, 3});
        check(bruteForce(same) == same, "bruteForce keeps the same head node and only moves values");

        int[] big = new int[100_000];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big);

        System.out.println("OK P900_ReverseADoublyLinkedList");
    }
}
