import java.util.*;

/** TUF 628 - Segregate odd and even nodes in Linked List. Nodes at odd positions (1st, 3rd, ...) come first, then nodes at even positions, each group keeping its order. */
public class P628_SegregateOddAndEvenNodesInLinkedList {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Collect the values at odd positions, then at even positions, and write them back over the nodes. O(n) time, O(n) space. */
    static ListNode bruteForce(ListNode head) {
        List<Integer> values = new ArrayList<>();
        ListNode t = head;
        while (t != null) {                          // positions 1, 3, 5, ...
            values.add(t.val);
            t = (t.next == null) ? null : t.next.next;
        }
        t = (head == null) ? null : head.next;
        while (t != null) {                          // positions 2, 4, 6, ...
            values.add(t.val);
            t = (t.next == null) ? null : t.next.next;
        }
        int i = 0;
        for (ListNode cur = head; cur != null; cur = cur.next) cur.val = values.get(i++);
        return head;
    }

    /** Approach 2: optimal. Grow an odd chain and an even chain by re-linking the existing nodes, then attach the even chain after the odd one. O(n) time, O(1) space. */
    static ListNode optimal(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode odd = head, even = head.next, evenHead = head.next;
        while (even != null && even.next != null) {
            odd.next = even.next;        // the next odd node sits right after the current even one
            odd = odd.next;
            even.next = odd.next;        // the next even node sits right after the new odd one
            even = even.next;
        }
        odd.next = evenHead;             // odd chain is complete; hang the even chain after it
        return head;
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

    /** Reference answer computed on the array: indices 0, 2, 4, ... then 1, 3, 5, ... */
    static int[] expectedOrder(int[] arr) {
        int[] out = new int[arr.length];
        int k = 0;
        for (int i = 0; i < arr.length; i += 2) out[k++] = arr[i];
        for (int i = 1; i < arr.length; i += 2) out[k++] = arr[i];
        return out;
    }

    static List<ListNode> nodes(ListNode head) {
        List<ListNode> out = new ArrayList<>();
        for (ListNode t = head; t != null; t = t.next) out.add(t);
        return out;
    }

    static String label(int[] arr) {
        return arr.length <= 12 ? Arrays.toString(arr) : "array of " + arr.length + " values";
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] arr) {
        int[] expected = expectedOrder(arr);
        String in = label(arr);
        check(Arrays.equals(toArray(bruteForce(fromArray(arr))), expected), "bruteForce " + in);

        ListNode head = fromArray(arr);
        List<ListNode> before = nodes(head);
        ListNode result = optimal(head);
        check(Arrays.equals(toArray(result), expected), "optimal " + in);
        List<ListNode> after = nodes(result);
        check(after.size() == before.size() && new HashSet<>(after).containsAll(before), "optimal must reuse exactly the original nodes for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5});               // -> 1 3 5 2 4
        verify(new int[]{2, 1, 3, 5, 6, 4, 7});         // -> 2 3 6 7 1 5 4
        verify(new int[]{});                            // edge: empty list
        verify(new int[]{1});                           // single node
        verify(new int[]{1, 2});                        // already segregated
        verify(new int[]{1, 2, 3, 4});                  // even length -> 1 3 2 4
        verify(new int[]{5, 5, 5, 5, 5});               // equal values
        verify(new int[]{-1, 2, -3, 4, -5, 6});         // negatives

        int[] big = new int[100_001];
        for (int i = 0; i < big.length; i++) big[i] = i;
        verify(big);

        System.out.println("OK P628_SegregateOddAndEvenNodesInLinkedList");
    }
}
