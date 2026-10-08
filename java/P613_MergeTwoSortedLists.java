import java.util.*;

/** TUF 613 - Merge two Sorted Lists. Merge two lists sorted in non-decreasing order into one sorted list and return its head. */
public class P613_MergeTwoSortedLists {

    static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    /** Approach 1: brute force. Copy every value into a list, sort it and build a fresh linked list. O((m+n) log(m+n)) time, O(m+n) space. */
    static ListNode bruteForce(ListNode a, ListNode b) {
        List<Integer> vals = new ArrayList<>();
        for (ListNode t = a; t != null; t = t.next) vals.add(t.val);
        for (ListNode t = b; t != null; t = t.next) vals.add(t.val);
        Collections.sort(vals);
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : vals) {
            tail.next = new ListNode(v);
            tail = tail.next;
        }
        return dummy.next;
    }

    /** Approach 2: recursion. The smaller head comes first, and its next is the merge of everything that is left. O(m+n) time, O(m+n) stack space. */
    static ListNode recursive(ListNode a, ListNode b) {
        if (a == null) return b;
        if (b == null) return a;
        if (a.val <= b.val) {
            a.next = recursive(a.next, b);
            return a;
        }
        b.next = recursive(a, b.next);
        return b;
    }

    /** Approach 3: optimal, iterative splice behind a dummy node. Relinks the existing nodes. O(m+n) time, O(1) extra space. */
    static ListNode optimal(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), tail = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) {                    // <= keeps equal values from a first (stable)
                tail.next = a;
                a = a.next;
            } else {
                tail.next = b;
                b = b.next;
            }
            tail = tail.next;
        }
        tail.next = (a != null) ? a : b;             // one list is used up: attach the other as it is
        return dummy.next;
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
        int[] res = new int[out.size()];
        for (int i = 0; i < res.length; i++) res[i] = out.get(i);
        return res;
    }

    static Set<ListNode> identities(ListNode... heads) {
        Set<ListNode> set = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ListNode h : heads) for (ListNode t = h; t != null; t = t.next) set.add(t);
        return set;
    }

    static String label(int[] a, int[] b) {
        if (a.length + b.length > 16) return "lists of " + a.length + " and " + b.length + " values";
        return Arrays.toString(a) + " + " + Arrays.toString(b);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] a, int[] b) {
        int[] expected = new int[a.length + b.length];
        System.arraycopy(a, 0, expected, 0, a.length);
        System.arraycopy(b, 0, expected, a.length, b.length);
        Arrays.sort(expected);
        String in = label(a, b);

        check(Arrays.equals(toArray(bruteForce(fromArray(a), fromArray(b))), expected), "bruteForce " + in);

        ListNode ra = fromArray(a), rb = fromArray(b);
        Set<ListNode> before = identities(ra, rb);
        ListNode r = recursive(ra, rb);
        check(Arrays.equals(toArray(r), expected), "recursive " + in);
        check(identities(r).equals(before), "recursive must reuse the input nodes " + in);

        ListNode oa = fromArray(a), ob = fromArray(b);
        before = identities(oa, ob);
        ListNode o = optimal(oa, ob);
        check(Arrays.equals(toArray(o), expected), "optimal " + in);
        check(identities(o).equals(before), "optimal must reuse the input nodes " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 4}, new int[]{1, 3, 4});           // -> 1 1 2 3 4 4
        verify(new int[]{}, new int[]{});                         // edge: both empty
        verify(new int[]{}, new int[]{0});                        // edge: one empty
        verify(new int[]{5}, new int[]{});                        // other side empty
        verify(new int[]{1, 2, 3}, new int[]{4, 5, 6});           // a entirely before b
        verify(new int[]{4, 5, 6}, new int[]{1, 2, 3});           // b entirely before a
        verify(new int[]{-10, -3, 0, 7}, new int[]{-5, -3, 8});   // negatives and duplicates
        verify(new int[]{2, 2, 2}, new int[]{2, 2});              // all equal
        verify(new int[]{Integer.MIN_VALUE, 0}, new int[]{Integer.MAX_VALUE}); // extreme values

        int[] evens = new int[3000], odds = new int[3000];
        for (int i = 0; i < 3000; i++) { evens[i] = 2 * i; odds[i] = 2 * i + 1; }
        verify(evens, odds);                                      // perfectly interleaved, 6000 nodes

        System.out.println("OK P613_MergeTwoSortedLists");
    }
}
