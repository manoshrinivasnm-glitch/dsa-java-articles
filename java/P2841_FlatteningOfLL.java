import java.util.*;

/** TUF 2841 - Flattening of LL. Each top-level node heads a sorted "bottom" list; return one sorted list linked only through bottom pointers. */
public class P2841_FlatteningOfLL {

    static class Node {
        int val;
        Node next, bottom;
        Node(int val) { this.val = val; }
    }

    /** Approach 1: collect every value, sort, and build a fresh bottom-linked list. O(N log N) time, O(N) space, where N is the total number of nodes. */
    static Node bruteForce(Node head) {
        List<Integer> vals = new ArrayList<>();                            // every value from every column
        for (Node col = head; col != null; col = col.next) {               // walk the top row, one column at a time
            for (Node t = col; t != null; t = t.bottom) vals.add(t.val);   // walk down this column
        }
        Collections.sort(vals);
        Node dummy = new Node(0), tail = dummy;                            // tail: last node of the new list
        for (int v : vals) { tail.bottom = new Node(v); tail = tail.bottom; }   // chain new nodes through bottom
        return dummy.bottom;                                               // first real node of the flat list
    }

    /** Approach 2: recursion. Flatten everything to the right of head, then merge head's own column into that result. O(N * k) time in the worst case for k columns, O(k) stack, no new nodes. */
    static Node better(Node head) {
        if (head == null || head.next == null) return head;        // a single column is already a sorted bottom list
        Node rest = better(head.next);                             // flatten columns 2..k first
        head.next = null;                                          // this column no longer points sideways
        return mergeBottom(head, rest);                            // merge this column into the flattened rest
    }

    /** Merge two sorted bottom-linked lists into one, reusing the nodes. */
    static Node mergeBottom(Node a, Node b) {
        Node dummy = new Node(0), tail = dummy;                    // tail: last node of the merged list
        while (a != null && b != null) {                           // both lists still have nodes
            if (a.val <= b.val) { tail.bottom = a; a = a.bottom; } // take the smaller front node
            else { tail.bottom = b; b = b.bottom; }
            tail = tail.bottom;                                    // the taken node is the new tail
            tail.next = null;                                      // keep the result strictly one-dimensional
        }
        tail.bottom = (a != null) ? a : b;                         // whatever is left is already sorted
        return dummy.bottom;
    }

    /** Approach 3: k-way merge with a min-heap that holds the current front node of each column. O(N log k) time, O(k) space. */
    static Node optimal(Node head) {
        // min-heap by value; it holds the front node of every column that still has nodes
        PriorityQueue<Node> heap = new PriorityQueue<>((x, y) -> Integer.compare(x.val, y.val));
        for (Node col = head; col != null; col = col.next) heap.offer(col);   // seed with the top of each column
        Node dummy = new Node(0), tail = dummy;                    // tail: last node of the flat list
        while (!heap.isEmpty()) {
            Node smallest = heap.poll();                           // the smallest value not yet placed
            if (smallest.bottom != null) heap.offer(smallest.bottom);   // the next node of the same column takes its place
            smallest.next = null;                                  // flattened nodes link only through bottom
            tail.bottom = smallest;                                // append it to the result
            tail = smallest;
        }
        tail.bottom = null;                                        // end the flat list cleanly
        return dummy.bottom;
    }

    // ---------------------------------------------------------------- helpers
    /** Builds the 2-D structure: each inner array is one sorted column, columns are chained through next. */
    static Node build(int[][] columns) {
        Node head = null, prevCol = null;
        for (int[] column : columns) {
            Node colHead = null, tail = null;
            for (int v : column) {
                Node node = new Node(v);
                if (colHead == null) colHead = node;
                else tail.bottom = node;
                tail = node;
            }
            if (colHead == null) continue;                 // skip empty columns
            if (head == null) head = colHead;
            else prevCol.next = colHead;
            prevCol = colHead;
        }
        return head;
    }

    /** Reads the flattened list through bottom pointers and asserts that no next pointer survives. */
    static int[] toArray(Node head) {
        List<Integer> out = new ArrayList<>();
        for (Node t = head; t != null; t = t.bottom) {
            check(t.next == null, "flattened list must not use next pointers");
            out.add(t.val);
        }
        return out.stream().mapToInt(Integer::intValue).toArray();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] columns, int[] expected) {
        String in = Arrays.deepToString(columns);
        check(Arrays.equals(toArray(bruteForce(build(columns))), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(better(build(columns))), expected), "better " + in);
        check(Arrays.equals(toArray(optimal(build(columns))), expected), "optimal " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{5, 7, 8, 30}, {10, 20}, {19, 22, 50}, {28, 35, 40, 45}},
               new int[]{5, 7, 8, 10, 19, 20, 22, 28, 30, 35, 40, 45, 50});
        verify(new int[][]{{5}, {10}, {19}, {28}}, new int[]{5, 10, 19, 28});           // every column has one node
        verify(new int[][]{{1, 2, 3}}, new int[]{1, 2, 3});                             // a single column
        verify(new int[][]{}, new int[]{});                                             // empty structure
        verify(new int[][]{{3, 3}, {1, 3}, {3}}, new int[]{1, 3, 3, 3, 3});             // duplicates across columns
        verify(new int[][]{{9, 10}, {1, 2}, {5}}, new int[]{1, 2, 5, 9, 10});           // column heads not sorted among themselves
        verify(new int[][]{{4}, {1, 2, 3, 5, 6}}, new int[]{1, 2, 3, 4, 5, 6});         // one long column after a short one
        System.out.println("OK P2841_FlatteningOfLL");
    }
}
