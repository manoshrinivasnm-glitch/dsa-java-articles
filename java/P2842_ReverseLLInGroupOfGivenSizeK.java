import java.util.*;

/** TUF 2842 - Reverse LL in group of given size K. Reverse every consecutive block of k nodes; a final block shorter than k is left as it is. */
public class P2842_ReverseLLInGroupOfGivenSizeK {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: copy the values into a list, reverse each full block of k entries, write the values back. O(n) time, O(n) space; node identities are not preserved. */
    static ListNode bruteForce(ListNode head, int k) {
        List<Integer> vals = new ArrayList<>();
        for (ListNode t = head; t != null; t = t.next) vals.add(t.val);   // copy the values out in order
        int n = vals.size();
        for (int start = 0; start + k <= n; start += k) {                 // only full blocks of k are reversed
            for (int i = start, j = start + k - 1; i < j; i++, j--) {     // swap from both ends of the block inward
                int tmp = vals.get(i);
                vals.set(i, vals.get(j));
                vals.set(j, tmp);
            }
        }
        ListNode t = head;
        for (int v : vals) { t.val = v; t = t.next; }                     // write the values back over the nodes
        return head;
    }

    /** Approach 2: recursion. If a full block exists, reverse it and point its tail at the recursively processed remainder. O(n) time, O(n / k) stack. */
    static ListNode better(ListNode head, int k) {
        ListNode node = head;
        int count = 0;
        while (node != null && count < k) { node = node.next; count++; }   // check that k nodes are left
        if (count < k) return head;                            // fewer than k nodes remain: leave them unchanged
        ListNode restReversed = better(node, k);              // node is the first node of the next block
        ListNode prev = restReversed, cur = head;              // starting prev at the rest links the block to it
        // reverse this block; its old first node ends up pointing at the rest
        for (int i = 0; i < k; i++) {
            ListNode next = cur.next;                          // remember the rest before cutting the link
            cur.next = prev;                                   // flip the pointer backwards
            prev = cur;                                        // advance both pointers
            cur = next;
        }
        return prev;                                           // the old k-th node is the new first node of the block
    }

    /** Approach 3: iterative. For each block find its k-th node, cut the block off, reverse it, and splice it back between the previous block and the rest. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head, int k) {
        ListNode dummy = new ListNode(0, head);                          // stays in front of the head as it changes
        ListNode prevBlockTail = dummy;                                  // last node of the already-processed part
        while (true) {                                                   // one block per iteration
            ListNode kth = prevBlockTail;
            for (int i = 0; i < k && kth != null; i++) kth = kth.next;   // k-th node of the current block
            if (kth == null) break;                                      // fewer than k nodes remain
            ListNode blockStart = prevBlockTail.next, nextBlockStart = kth.next;
            kth.next = null;                                             // isolate the block
            prevBlockTail.next = reverse(blockStart);                    // kth is now the first node of the block
            blockStart.next = nextBlockStart;                            // the old first node is now the block's tail
            prevBlockTail = blockStart;                                  // the next block hangs after this one
        }
        return dummy.next;                                               // the real head after all swaps
    }

    /** Standard in-place reversal of a whole list. */
    static ListNode reverse(ListNode head) {
        ListNode prev = null, cur = head;   // prev heads the reversed part, cur is the next node to flip
        while (cur != null) {
            ListNode next = cur.next;       // remember the rest before cutting the link
            cur.next = prev;                // flip the pointer backwards
            prev = cur;                     // advance both pointers
            cur = next;
        }
        return prev;                        // the old tail is the new head
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

    static void verify(int[] arr, int k, int[] expected) {
        String in = Arrays.toString(arr) + " k=" + k;
        check(Arrays.equals(toArray(bruteForce(fromArray(arr), k)), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(better(fromArray(arr), k)), expected), "better " + in);
        check(Arrays.equals(toArray(optimal(fromArray(arr), k)), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 2, new int[]{2, 1, 4, 3, 5});          // last block of size 1 stays
        verify(new int[]{1, 2, 3, 4, 5}, 3, new int[]{3, 2, 1, 4, 5});          // last block of size 2 stays
        verify(new int[]{1, 2, 3, 4, 5}, 1, new int[]{1, 2, 3, 4, 5});          // k = 1 changes nothing
        verify(new int[]{1, 2, 3, 4, 5}, 5, new int[]{5, 4, 3, 2, 1});          // k = n reverses the whole list
        verify(new int[]{1, 2, 3, 4, 5}, 6, new int[]{1, 2, 3, 4, 5});          // k > n: no full block, nothing changes
        verify(new int[]{1, 2, 3, 4, 5, 6}, 3, new int[]{3, 2, 1, 6, 5, 4});    // n divisible by k
        verify(new int[]{}, 2, new int[]{});                                    // empty list
        verify(new int[]{1}, 1, new int[]{1});                                  // single node
        verify(new int[]{1, 2}, 2, new int[]{2, 1});                            // exactly one block
        System.out.println("OK P2842_ReverseLLInGroupOfGivenSizeK");
    }
}
