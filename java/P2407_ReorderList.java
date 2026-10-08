import java.util.*;
import java.util.function.*;

/** TUF 2407 - Reorder List. Relink L0 -> L1 -> ... -> Ln into L0 -> Ln -> L1 -> Ln-1 -> L2 -> ... in place, without changing node values. */
public class P2407_ReorderList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Repeatedly detach the current tail and splice it right after the current node. O(n^2) time, O(1) space. */
    static void bruteForce(ListNode head) {
        ListNode cur = head;
        while (cur != null && cur.next != null && cur.next.next != null) {
            ListNode prev = cur;                     // walk to the node before the tail
            while (prev.next.next != null) prev = prev.next;
            ListNode tail = prev.next;
            prev.next = null;                        // detach the tail
            tail.next = cur.next;                    // and splice it in right after cur
            cur.next = tail;
            cur = tail.next;                         // continue from the next untouched front node
        }
    }

    /** Approach 2: better. Store node references in an array and relink with two indices moving inward. O(n) time, O(n) space. */
    static void better(ListNode head) {
        if (head == null) return;
        List<ListNode> nodes = new ArrayList<>();
        for (ListNode t = head; t != null; t = t.next) nodes.add(t);
        int i = 0, j = nodes.size() - 1;
        while (i < j) {
            nodes.get(i).next = nodes.get(j);        // front -> back
            i++;
            if (i == j) break;
            nodes.get(j).next = nodes.get(i);        // back -> next front
            j--;
        }
        nodes.get(i).next = null;                    // the node where i and j meet is the new tail
    }

    /** Approach 3: optimal. Find the middle, reverse the second half, then weave the two halves together. O(n) time, O(1) space. */
    static void optimal(ListNode head) {
        if (head == null || head.next == null) return;
        ListNode slow = head, fast = head;           // slow ends on the last node of the first half
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        ListNode second = reverse(slow.next);        // second half, back to front
        slow.next = null;                            // cut the first half off
        ListNode first = head;
        while (second != null) {                     // second half is never longer than the first
            ListNode n1 = first.next, n2 = second.next;
            first.next = second;
            second.next = n1;
            first = n1;
            second = n2;
        }
    }

    static ListNode reverse(ListNode head) {
        ListNode prev = null;
        while (head != null) {
            ListNode next = head.next;
            head.next = prev;
            prev = head;
            head = next;
        }
        return prev;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode[] buildNodes(int[] arr) {
        ListNode[] nodes = new ListNode[arr.length];
        for (int i = arr.length - 1; i >= 0; i--) nodes[i] = new ListNode(arr[i], i + 1 < arr.length ? nodes[i + 1] : null);
        return nodes;
    }

    /** Original positions in reordered sequence: 0, n-1, 1, n-2, ... */
    static int[] expectedOrder(int n) {
        int[] order = new int[n];
        int lo = 0, hi = n - 1;
        for (int k = 0; k < n; k++) order[k] = (k % 2 == 0) ? lo++ : hi--;
        return order;
    }

    static String label(int[] arr) {
        return arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Checks that the reordered list consists of the original node objects in the order 0, n-1, 1, n-2, ... */
    static void verifyOne(String name, Consumer<ListNode> method, int[] arr) {
        ListNode[] nodes = buildNodes(arr);
        ListNode head = arr.length == 0 ? null : nodes[0];
        method.accept(head);
        int[] order = expectedOrder(arr.length);
        ListNode t = head;
        for (int k = 0; k < order.length; k++) {
            check(t == nodes[order[k]], name + " wrong node at position " + k + " for " + label(arr));
            t = t.next;
        }
        check(t == null, name + " list too long for " + label(arr));
    }

    static void verify(int[] arr) {
        verifyOne("bruteForce", P2407_ReorderList::bruteForce, arr);
        verifyOne("better", P2407_ReorderList::better, arr);
        verifyOne("optimal", P2407_ReorderList::optimal, arr);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4});                  // -> 1 4 2 3
        verify(new int[]{1, 2, 3, 4, 5});               // -> 1 5 2 4 3
        verify(new int[]{1});                           // edge: single node unchanged
        verify(new int[]{1, 2});                        // two nodes unchanged
        verify(new int[]{1, 2, 3});                     // -> 1 3 2
        verify(new int[]{});                            // edge: empty list
        verify(new int[]{9, 9, 9, 9, 9, 9});            // equal values: node identity is checked

        int[] big = new int[3001];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big);                                    // the O(n^2) brute force is still fast here
        verify(Arrays.copyOf(big, 3000));

        System.out.println("OK P2407_ReorderList");
    }
}
