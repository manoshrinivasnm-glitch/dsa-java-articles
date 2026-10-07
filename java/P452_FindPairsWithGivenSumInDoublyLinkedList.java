import java.util.*;

/** TUF 452 - Find Pairs with Given Sum in Doubly Linked List. The DLL is sorted ascending with distinct values; return every pair of values that sums to target, smaller value first, ordered by the smaller value. */
public class P452_FindPairsWithGivenSumInDoublyLinkedList {

    static class Node {
        int val;
        Node prev, next;
        Node(int val) { this.val = val; }
    }

    /** Approach 1: check every pair of nodes. O(n^2) time, O(1) extra space. */
    static List<int[]> bruteForce(Node head, int target) {
        List<int[]> pairs = new ArrayList<>();
        for (Node a = head; a != null; a = a.next) {
            for (Node b = a.next; b != null; b = b.next) {
                if ((long) a.val + b.val == target) pairs.add(new int[]{a.val, b.val});
            }
        }
        return pairs;
    }

    /** Approach 2: a hash set of the values seen so far; a pair is complete when the complement of the current value is already in it. O(n) time, O(n) space; does not need the list to be sorted. */
    static List<int[]> better(Node head, int target) {
        List<int[]> pairs = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        for (Node t = head; t != null; t = t.next) {
            long need = (long) target - t.val;                    // long: target - value can overflow int
            if (need >= Integer.MIN_VALUE && need <= Integer.MAX_VALUE && seen.contains((int) need)) {
                int other = (int) need;
                pairs.add(new int[]{Math.min(other, t.val), Math.max(other, t.val)});
            }
            seen.add(t.val);
        }
        pairs.sort((x, y) -> Integer.compare(x[0], y[0]));      // the sweep discovers pairs by their larger element; order them by the smaller one
        return pairs;
    }

    /** Approach 3: two pointers, left at the head and right at the tail, walking inward through prev. O(n) time, O(1) space. */
    static List<int[]> optimal(Node head, int target) {
        List<int[]> pairs = new ArrayList<>();
        if (head == null) return pairs;
        Node left = head, right = head;
        while (right.next != null) right = right.next;            // find the tail
        while (left != right && right.next != left) {             // stop when the pointers meet or cross
            long sum = (long) left.val + right.val;
            if (sum == target) {
                pairs.add(new int[]{left.val, right.val});
                left = left.next;
                right = right.prev;
            } else if (sum < target) {
                left = left.next;                                 // too small: the only way up is a bigger left value
            } else {
                right = right.prev;                               // too big: the only way down is a smaller right value
            }
        }
        return pairs;
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

    static String show(List<int[]> pairs) {
        StringBuilder sb = new StringBuilder();
        for (int[] p : pairs) sb.append('(').append(p[0]).append(',').append(p[1]).append(')');
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] sortedValues, int target, int[][] expected) {
        Node head = fromArray(sortedValues);                      // no approach modifies the list, so one copy serves all three
        String want = show(Arrays.asList(expected));
        String in = Arrays.toString(sortedValues) + " target " + target;
        check(show(bruteForce(head, target)).equals(want), "bruteForce " + in);
        check(show(better(head, target)).equals(want), "better " + in);
        check(show(optimal(head, target)).equals(want), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 4, 5, 6, 8, 9}, 7, new int[][]{{1, 6}, {2, 5}});
        verify(new int[]{1, 2, 3, 4, 5}, 5, new int[][]{{1, 4}, {2, 3}});
        verify(new int[]{1, 2, 3}, 10, new int[][]{});                                  // nothing sums to the target
        verify(new int[]{}, 5, new int[][]{});                                          // empty list
        verify(new int[]{3}, 6, new int[][]{});                                         // a single node cannot pair with itself
        verify(new int[]{1, 5, 9}, 10, new int[][]{{1, 9}});                            // the middle 5 must not be paired with itself
        verify(new int[]{-5, -2, 0, 3, 7}, -2, new int[][]{{-5, 3}, {-2, 0}});          // negatives
        verify(new int[]{1, 2, 3, 4, 5, 6}, 7, new int[][]{{1, 6}, {2, 5}, {3, 4}});    // every element is in some pair
        verify(new int[]{1_000_000_000, 2_000_000_000}, -1_294_967_296, new int[][]{}); // int addition wraps to exactly this target
        System.out.println("OK P452_FindPairsWithGivenSumInDoublyLinkedList");
    }
}
