import java.util.*;

/** TUF 629 - Sort a Linked List of 0's 1's and 2's. Every node holds 0, 1 or 2; rearrange so all 0s come first, then 1s, then 2s. */
public class P629_SortALinkedListOf0s1sAnd2s {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force, counting. Count how many 0s, 1s and 2s there are, then overwrite the values front to back. Two passes. O(n) time, O(1) space. */
    static ListNode bruteForce(ListNode head) {
        int zeros = 0, ones = 0, twos = 0;
        for (ListNode t = head; t != null; t = t.next) {
            if (t.val == 0) zeros++;
            else if (t.val == 1) ones++;
            else twos++;
        }
        for (ListNode t = head; t != null; t = t.next) {
            if (zeros > 0) { t.val = 0; zeros--; }
            else if (ones > 0) { t.val = 1; ones--; }
            else t.val = 2;
        }
        return head;
    }

    /** Approach 2: optimal, re-linking. Thread the nodes onto three chains (0s, 1s, 2s) in one pass, then join the chains. O(n) time, O(1) space, no values are changed. */
    static ListNode optimal(ListNode head) {
        ListNode zeroDummy = new ListNode(-1), oneDummy = new ListNode(-1), twoDummy = new ListNode(-1);
        ListNode zero = zeroDummy, one = oneDummy, two = twoDummy;
        for (ListNode t = head; t != null; t = t.next) {
            if (t.val == 0) { zero.next = t; zero = t; }
            else if (t.val == 1) { one.next = t; one = t; }
            else { two.next = t; two = t; }
        }
        zero.next = (oneDummy.next != null) ? oneDummy.next : twoDummy.next;   // skip the 1-chain if it is empty
        one.next = twoDummy.next;
        two.next = null;                                                        // the last 2 (or the last node overall) ends the list
        return zeroDummy.next;
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
        int[] expected = arr.clone();
        Arrays.sort(expected);
        String in = label(arr);
        check(Arrays.equals(toArray(bruteForce(fromArray(arr))), expected), "bruteForce " + in);

        ListNode head = fromArray(arr);
        List<ListNode> before = nodes(head);
        ListNode sorted = optimal(head);
        check(Arrays.equals(toArray(sorted), expected), "optimal " + in);
        List<ListNode> after = nodes(sorted);
        check(after.size() == before.size() && new HashSet<>(after).containsAll(before), "optimal must reuse exactly the original nodes for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 0, 2, 1, 0, 2, 1});          // -> 0 0 1 1 1 2 2
        verify(new int[]{2, 2, 0});                      // -> 0 2 2 (no 1s: the 0-chain must link straight to the 2-chain)
        verify(new int[]{});                             // edge: empty list
        verify(new int[]{1});                            // single node
        verify(new int[]{0, 0, 0});                      // only 0s: the 2-chain is empty
        verify(new int[]{1, 1});                         // only 1s
        verify(new int[]{2, 1, 0});                      // reverse order
        verify(new int[]{0, 1, 2});                      // already sorted
        verify(new int[]{2, 2, 2, 0, 0, 0});             // two groups only
        verify(new int[]{1, 2, 1, 2});                   // no 0s: the 0-chain is empty

        Random rnd = new Random(7);
        int[] big = new int[200_000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt(3);
        verify(big);

        System.out.println("OK P629_SortALinkedListOf0s1sAnd2s");
    }
}
