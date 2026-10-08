import java.util.*;

/** TUF 614 - Reverse LL in group of given size K. Reverse every full block of k nodes; a shorter last block stays as it is. */
public class P614_ReverseLLInGroupOfGivenSizeK {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /** Approach 1: copy the values out, reverse each full block of the copy, write the values back. O(n) time, O(n) space. */
    static ListNode bruteForce(ListNode head, int k) {
        List<Integer> vals = new ArrayList<>();
        for (ListNode cur = head; cur != null; cur = cur.next) vals.add(cur.val);
        for (int start = 0; start + k <= vals.size(); start += k) {
            Collections.reverse(vals.subList(start, start + k));   // the partial last block is skipped
        }
        ListNode cur = head;
        for (int v : vals) {
            cur.val = v;
            cur = cur.next;
        }
        return head;
    }

    /** Approach 2: reverse the first k nodes so that they point at the already-solved rest. O(n) time, O(n / k) stack. */
    static ListNode recursive(ListNode head, int k) {
        ListNode cur = head;
        for (int i = 0; i < k; i++) {
            if (cur == null) return head;               // fewer than k nodes left: leave them alone
            cur = cur.next;
        }
        ListNode prev = recursive(cur, k);              // cur is the first node of the next group
        ListNode node = head;
        while (node != cur) {                           // reverse this group; its old head ends up pointing at prev
            ListNode nxt = node.next;
            node.next = prev;
            prev = node;
            node = nxt;
        }
        return prev;                                    // the old k-th node is the new head of this group
    }

    /** Approach 3: iterative in-place reversal with a dummy node. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head, int k) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode groupPrev = dummy;                     // last node of the part that is already done
        while (true) {
            ListNode kth = kthNode(groupPrev, k);       // last node of the current group
            if (kth == null) break;                     // fewer than k nodes remain
            ListNode groupNext = kth.next;
            ListNode prev = groupNext, cur = groupPrev.next;
            while (cur != groupNext) {                  // reversed group's tail will point at groupNext
                ListNode nxt = cur.next;
                cur.next = prev;
                prev = cur;
                cur = nxt;
            }
            ListNode oldFirst = groupPrev.next;         // now the last node of the reversed group
            groupPrev.next = kth;
            groupPrev = oldFirst;
        }
        return dummy.next;
    }

    /** Returns the node k steps after start, or null if the list ends first. */
    static ListNode kthNode(ListNode start, int k) {
        while (start != null && k > 0) {
            start = start.next;
            k--;
        }
        return start;
    }

    // ---------------------------------------------------------------- helpers
    static ListNode fromArray(int[] a) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : a) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    static int[] toArray(ListNode head) {
        List<Integer> out = new ArrayList<>();
        for (ListNode cur = head; cur != null; cur = cur.next) out.add(cur.val);
        int[] a = new int[out.size()];
        for (int i = 0; i < a.length; i++) a[i] = out.get(i);
        return a;
    }

    static Set<ListNode> nodeSet(ListNode head) {
        Set<ListNode> s = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ListNode cur = head; cur != null; cur = cur.next) s.add(cur);
        return s;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] input, int k, int[] expected) {
        String in = Arrays.toString(input) + " k=" + k;
        check(Arrays.equals(toArray(bruteForce(fromArray(input), k)), expected), "bruteForce " + in);
        ListNode h2 = fromArray(input);
        Set<ListNode> before2 = nodeSet(h2);
        ListNode r2 = recursive(h2, k);
        check(Arrays.equals(toArray(r2), expected), "recursive " + in);
        check(nodeSet(r2).equals(before2), "recursive must relink the original nodes " + in);
        ListNode h3 = fromArray(input);
        Set<ListNode> before3 = nodeSet(h3);
        ListNode r3 = optimal(h3, k);
        check(Arrays.equals(toArray(r3), expected), "optimal " + in);
        check(nodeSet(r3).equals(before3), "optimal must relink the original nodes " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5}, 2, new int[]{2, 1, 4, 3, 5});
        verify(new int[]{1, 2, 3, 4, 5}, 3, new int[]{3, 2, 1, 4, 5});
        verify(new int[]{1, 2, 3, 4, 5, 6, 7, 8}, 4, new int[]{4, 3, 2, 1, 8, 7, 6, 5});   // no leftover block
        verify(new int[]{1, 2, 3, 4, 5}, 5, new int[]{5, 4, 3, 2, 1});                       // k == n
        verify(new int[]{1, 2, 3}, 4, new int[]{1, 2, 3});                                   // k > n: nothing to reverse
        verify(new int[]{1, 2, 3}, 1, new int[]{1, 2, 3});                                   // k == 1
        verify(new int[]{}, 2, new int[]{});                                                 // edge: empty list
        verify(new int[]{9}, 1, new int[]{9});                                               // edge: single node
        int n = 3000;
        int[] big = new int[n], bigExpected = new int[n];
        for (int i = 0; i < n; i++) big[i] = i;
        int k = 7;
        for (int i = 0; i < n; i++) {
            int start = (i / k) * k;
            bigExpected[i] = (start + k <= n) ? start + k - 1 - (i - start) : i;            // 3000 = 428 * 7 + 4
        }
        verify(big, k, bigExpected);
        System.out.println("OK P614_ReverseLLInGroupOfGivenSizeK");
    }
}
