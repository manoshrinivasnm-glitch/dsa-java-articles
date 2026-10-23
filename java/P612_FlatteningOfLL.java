import java.util.*;

/** TUF 612 - Flattening of LL. Merge sorted vertical (bottom) lists hanging off a horizontal (next) list into one sorted bottom list. */
public class P612_FlatteningOfLL {

    static class Node {
        int data;                                   // the value stored in this node
        Node next, bottom;                          // next: head of the next column; bottom: next node down
        Node(int data) { this.data = data; }
    }

    /** Approach 1: collect every node, sort by value, relink through bottom. O(N log N) time, O(N) space. */
    static Node bruteForce(Node root) {
        List<Node> all = new ArrayList<>();         // every node of every column
        for (Node col = root; col != null; col = col.next) {   // walk the heads from left to right
            for (Node cur = col; cur != null; cur = cur.bottom) all.add(cur);   // then down that column
        }
        all.sort((x, y) -> Integer.compare(x.data, y.data));   // sort all nodes by value
        for (int i = 0; i < all.size(); i++) {      // relink the sorted nodes into one bottom chain
            Node node = all.get(i);
            node.next = null;                       // the result uses bottom pointers only
            // point down at the next larger node; the last gets null
            node.bottom = (i + 1 < all.size()) ? all.get(i + 1) : null;
        }
        return all.isEmpty() ? null : all.get(0);   // the smallest node is the new head
    }

    /** Approach 2: flatten everything to the right, then merge this column into it. O(N * k) time, O(k) stack. */
    static Node mergeFromRight(Node root) {
        if (root == null || root.next == null) return root;   // zero or one column is already flat
        Node rest = mergeFromRight(root.next);      // already one sorted bottom list
        root.next = null;                           // detach this column from the horizontal list
        return merge(root, rest);                   // merge this column into the flattened rest
    }

    /** Merges two sorted bottom lists, reusing their nodes. */
    static Node merge(Node a, Node b) {
        Node dummy = new Node(0), tail = dummy;     // dummy anchors the result, tail is its last node
        while (a != null && b != null) {            // both lists still have nodes to compare
            if (a.data <= b.data) {                 // take the smaller front node (equal values from a first)
                tail.bottom = a;
                a = a.bottom;
            } else {
                tail.bottom = b;
                b = b.bottom;
            }
            tail = tail.bottom;                     // the result grew by one node
        }
        tail.bottom = (a != null) ? a : b;          // one side is exhausted, append the other
        return dummy.bottom;                        // skip the dummy
    }

    /** Approach 3: k-way merge with a min-heap holding the current front of every column. O(N log k) time, O(k) space. */
    static Node minHeap(Node root) {
        // smallest front node on top
        PriorityQueue<Node> heap = new PriorityQueue<>((x, y) -> Integer.compare(x.data, y.data));
        for (Node col = root; col != null; col = col.next) heap.add(col);   // seed with each column's head
        Node dummy = new Node(0), tail = dummy;     // dummy anchors the result, tail is its last node
        while (!heap.isEmpty()) {                   // until every node has been placed
            Node smallest = heap.poll();            // the smallest value not yet placed
            if (smallest.bottom != null) heap.add(smallest.bottom);   // its successor joins the race
            smallest.next = null;                   // the result uses bottom pointers only
            tail.bottom = smallest;                 // append it to the result
            tail = smallest;
        }
        return dummy.bottom;                        // the last node polled has bottom == null already
    }

    // ---------------------------------------------------------------- helpers
    /** Builds the two-level structure: columns[i] is the i-th vertical list, columns are joined by next. */
    static Node build(int[][] columns) {
        Node head = null, prevTop = null;
        for (int[] col : columns) {
            Node top = null, tail = null;
            for (int v : col) {
                Node node = new Node(v);
                if (top == null) top = node; else tail.bottom = node;
                tail = node;
            }
            if (head == null) head = top; else prevTop.next = top;
            prevTop = top;
        }
        return head;
    }

    /** Reads the bottom chain and fails if any node still has a next pointer. */
    static int[] toArray(Node head) {
        List<Integer> out = new ArrayList<>();
        for (Node cur = head; cur != null; cur = cur.bottom) {
            check(cur.next == null, "next pointer left at node " + cur.data);
            out.add(cur.data);
        }
        int[] a = new int[out.size()];
        for (int i = 0; i < a.length; i++) a[i] = out.get(i);
        return a;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] columns, int[] expected) {
        String in = Arrays.deepToString(columns);
        if (in.length() > 80) in = columns.length + " columns";
        check(Arrays.equals(toArray(bruteForce(build(columns))), expected), "bruteForce " + in);
        check(Arrays.equals(toArray(mergeFromRight(build(columns))), expected), "mergeFromRight " + in);
        check(Arrays.equals(toArray(minHeap(build(columns))), expected), "minHeap " + in);
    }

    public static void main(String[] args) {
        verify(new int[][]{{5, 7, 8, 30}, {10, 20}, {19, 22, 50}, {28, 35, 40, 45}},
               new int[]{5, 7, 8, 10, 19, 20, 22, 28, 30, 35, 40, 45, 50});
        verify(new int[][]{{10, 20}, {20, 30}, {5}}, new int[]{5, 10, 20, 20, 30});   // duplicates across columns
        verify(new int[][]{{1, 2, 3, 4}}, new int[]{1, 2, 3, 4});                    // one column, already flat
        verify(new int[][]{{3}, {2}, {1}}, new int[]{1, 2, 3});                      // single-node columns
        verify(new int[][]{}, new int[]{});                                          // edge: empty structure
        verify(new int[][]{{-5, 0}, {-7, 9}}, new int[]{-7, -5, 0, 9});              // negatives
        int k = 60, perColumn = 500;                                                 // 30 000 nodes
        int[][] cols = new int[k][perColumn];
        int[] all = new int[k * perColumn];
        for (int c = 0; c < k; c++) {
            for (int r = 0; r < perColumn; r++) {
                cols[c][r] = r * k + (c * 37) % k;                                   // interleaved values, sorted per column
                all[c * perColumn + r] = cols[c][r];
            }
        }
        Arrays.sort(all);
        verify(cols, all);
        System.out.println("OK P612_FlatteningOfLL");
    }
}
