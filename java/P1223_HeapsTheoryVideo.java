import java.util.*;

/** TUF 1223 - Heaps (Theory). Array-backed binary heaps: index arithmetic, sift up, sift down, building, heap sort, PriorityQueue. */
public class P1223_HeapsTheoryVideo {

    /** Index of the parent of node i (only meaningful for i > 0) in a 0-indexed array heap. */
    static int parent(int i) {
        return (i - 1) / 2;                              // children 2p + 1 and 2p + 2 both map back to p
    }

    /** Index of the left child of node i. */
    static int left(int i) {
        return 2 * i + 1;                                // 0-indexed: the children of i are 2i + 1 and 2i + 2
    }

    /** Index of the right child of node i. */
    static int right(int i) {
        return 2 * i + 2;                                // the slot right after the left child
    }

    static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    /** Min-heap property for a[0..n-1]: no child is smaller than its parent. */
    static boolean isMinHeap(int[] a, int n) {
        for (int i = 1; i < n; i++) {                    // compare every non-root node with its parent
            if (a[parent(i)] > a[i]) return false;       // a child smaller than its parent breaks the rule
        }
        return true;
    }

    /** Moves heap[i] up while it is smaller than its parent. Returns the number of swaps. */
    static int siftUp(int[] heap, int i) {
        int swaps = 0;                                   // number of levels the element climbs
        while (i > 0 && heap[parent(i)] > heap[i]) {     // stop at the root or once the parent is not larger
            swap(heap, i, parent(i));                    // move the smaller value up one level
            i = parent(i);                               // follow the element to its new slot
            swaps++;
        }
        return swaps;
    }

    /** Moves heap[i] down while some child is smaller, looking only at heap[0..n-1]. Returns the number of swaps. */
    static int siftDown(int[] heap, int n, int i) {
        int swaps = 0;
        while (true) {                                    // each pass moves the element down one level
            int smallest = i, l = left(i), r = right(i);  // assume heap[i] is the smallest of the three
            if (l < n && heap[l] < heap[smallest]) smallest = l;   // left child exists and is smaller
            if (r < n && heap[r] < heap[smallest]) smallest = r;   // right child exists and is smaller still
            if (smallest == i) return swaps;              // no child is smaller: the heap is fixed
            swap(heap, i, smallest);                      // the smaller child moves up, heap[i] moves down
            i = smallest;                                 // continue from the new position
            swaps++;
        }
    }

    /** Top-down build: a[0..k-1] is already a heap, so "insert" a[k] by sifting it up. O(n log n). Returns total swaps. */
    static long buildByInsertion(int[] a) {
        long swaps = 0;
        // a[0..k-1] is already a heap: sift a[k] up to join it
        for (int k = 1; k < a.length; k++) swaps += siftUp(a, k);
        return swaps;
    }

    /** Bottom-up build (heapify): sift down every internal node, last one first. O(n). Returns total swaps. */
    static long buildBottomUp(int[] a) {
        long swaps = 0;
        // leaves (i >= n/2) are heaps already; fix internal nodes from the bottom up
        for (int i = a.length / 2 - 1; i >= 0; i--) swaps += siftDown(a, a.length, i);
        return swaps;
    }

    /** Repeated extract-min on a copy: take the root, move the last element to the root, sift it down. */
    static int[] drainInOrder(int[] heap) {
        int[] h = heap.clone();                          // work on a copy so the caller's heap is untouched
        int n = h.length;                                // n = size of the heap part of h
        int[] out = new int[n];
        for (int j = 0; j < out.length; j++) {           // one extract-min per step
            out[j] = h[0];                               // the root is the current minimum
            h[0] = h[--n];                               // last element fills the root; the heap shrinks
            siftDown(h, n, 0);                           // restore the heap property from the root down
        }
        return out;
    }

    /** Max-heap version of siftDown, used by heap sort. */
    static void siftDownMax(int[] a, int n, int i) {
        while (true) {
            int largest = i, l = left(i), r = right(i);   // assume a[i] is the largest of the three
            if (l < n && a[l] > a[largest]) largest = l;  // left child exists and is bigger
            if (r < n && a[r] > a[largest]) largest = r;  // right child exists and is bigger still
            if (largest == i) return;                     // no child is bigger: the heap is fixed
            swap(a, i, largest);                          // the bigger child moves up, a[i] moves down
            i = largest;                                  // continue from the new position
        }
    }

    /** Heap sort: build a max-heap, then repeatedly swap the maximum to the end of the unsorted prefix. O(n log n), O(1) extra. */
    static void heapSort(int[] a) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) siftDownMax(a, n, i);   // turn the whole array into a max-heap
        for (int end = n - 1; end > 0; end--) {          // a[0..end] is the heap, a[end+1..] is sorted
            swap(a, 0, end);                             // the maximum goes to its final slot
            siftDownMax(a, end, 0);                      // repair the shrunken heap a[0..end-1]
        }
    }

    /** java.util.PriorityQueue is a binary min-heap: poll() always removes the smallest element. */
    static List<Integer> pollAllMin(int[] values) {
        PriorityQueue<Integer> pq = new PriorityQueue<>();  // natural order: a min-heap
        for (int v : values) pq.offer(v);                   // each offer is O(log n)
        List<Integer> out = new ArrayList<>();
        while (!pq.isEmpty()) out.add(pq.poll());           // smallest first -> ascending order
        return out;
    }

    /** A max-heap is the same class with the comparator reversed. */
    static List<Integer> pollAllMax(int[] values) {
        // reversed comparator: the largest value has top priority
        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
        for (int v : values) pq.offer(v);
        List<Integer> out = new ArrayList<>();
        while (!pq.isEmpty()) out.add(pq.poll());        // largest first -> descending order
        return out;
    }

    /** Heap of pairs {priority, id}: smallest priority first, ties broken by the smaller id. Returns ids in poll order. */
    static List<Integer> idsByPriority(int[][] tasks) {
        // order by priority, then by the smaller id to break ties
        PriorityQueue<int[]> pq = new PriorityQueue<>((x, y) -> x[0] != y[0] ? Integer.compare(x[0], y[0]) : Integer.compare(x[1], y[1]));
        for (int[] t : tasks) pq.offer(t);
        List<Integer> out = new ArrayList<>();
        while (!pq.isEmpty()) out.add(pq.poll()[1]);     // keep only the id of each polled pair
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static int[] sortedCopy(int[] a) {
        int[] s = a.clone();
        Arrays.sort(s);
        return s;
    }

    public static void main(String[] args) {
        // 1. index arithmetic
        check(parent(1) == 0 && parent(2) == 0 && parent(5) == 2 && parent(6) == 2, "parent");
        check(left(0) == 1 && right(0) == 2 && left(2) == 5 && right(2) == 6, "children");
        for (int i = 0; i < 100; i++) check(parent(left(i)) == i && parent(right(i)) == i, "parent of child is the node");

        // 2. sift up / sift down on the worked example
        int[] ins = {5, 3, 8, 1, 9, 2};
        check(buildByInsertion(ins) == 4, "insertion build swaps");
        check(Arrays.equals(ins, new int[]{1, 3, 2, 5, 9, 8}), "insertion build result " + Arrays.toString(ins));
        int[] bu = {5, 3, 8, 1, 9, 2};
        check(buildBottomUp(bu) == 4, "bottom-up build swaps");
        check(Arrays.equals(bu, new int[]{1, 3, 2, 5, 9, 8}), "bottom-up build result " + Arrays.toString(bu));
        int[] up = {1, 3, 2, 5, 9, 8, 0};                        // a heap with a new 0 appended at index 6
        check(siftUp(up, 6) == 2 && Arrays.equals(up, new int[]{0, 3, 1, 5, 9, 8, 2}), "sift up of appended 0");
        int[] down = {8, 3, 2, 5, 9};                             // root replaced by 8 after an extract
        check(siftDown(down, 5, 0) == 1 && Arrays.equals(down, new int[]{2, 3, 8, 5, 9}), "sift down of new root");
        check(Arrays.equals(drainInOrder(new int[]{1, 3, 2, 5, 9, 8}), new int[]{1, 2, 3, 5, 8, 9}), "drain in order");

        // 3. both builds always produce a valid heap with the same elements; draining yields sorted order
        int[][] inputs = {{}, {7}, {2, 1}, {4, 4, 4, 4}, {-3, 10, -7, 0, 5, 5, -1}, {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1}};
        for (int[] in : inputs) {
            int[] a = in.clone(), b = in.clone(), c = in.clone();
            buildByInsertion(a);
            buildBottomUp(b);
            check(isMinHeap(a, a.length) && isMinHeap(b, b.length), "build must give a heap for " + Arrays.toString(in));
            check(Arrays.equals(sortedCopy(a), sortedCopy(in)) && Arrays.equals(sortedCopy(b), sortedCopy(in)), "same multiset");
            check(Arrays.equals(drainInOrder(a), sortedCopy(in)), "drain must be sorted for " + Arrays.toString(in));
            heapSort(c);
            check(Arrays.equals(c, sortedCopy(in)), "heap sort for " + Arrays.toString(in));
        }

        // 4. edge: worst case cost of the two builds on a strictly decreasing array of 2^16 - 1 elements
        int n = (1 << 16) - 1;
        int[] desc1 = new int[n], desc2 = new int[n];
        for (int i = 0; i < n; i++) desc1[i] = desc2[i] = n - 1 - i;
        long insertionSwaps = buildByInsertion(desc1);
        long bottomUpSwaps = buildBottomUp(desc2);
        check(insertionSwaps == 917_506, "insertion build swaps " + insertionSwaps);      // sum of depths
        check(bottomUpSwaps == 65_519, "bottom-up build swaps " + bottomUpSwaps);         // sum of heights = n - 16
        check(isMinHeap(desc1, n) && isMinHeap(desc2, n) && desc1[0] == 0 && desc2[0] == 0, "large builds valid");

        // 5. heap sort against Arrays.sort on a seeded random array
        Random rnd = new Random(2024);
        int[] big = new int[50_000];
        for (int i = 0; i < big.length; i++) big[i] = rnd.nextInt();
        int[] expect = sortedCopy(big);
        heapSort(big);
        check(Arrays.equals(big, expect), "heap sort random");

        // 6. PriorityQueue as min-heap, max-heap and heap of pairs
        int[] vals = {5, -1, Integer.MAX_VALUE, 3, Integer.MIN_VALUE, 3};
        check(pollAllMin(vals).equals(List.of(Integer.MIN_VALUE, -1, 3, 3, 5, Integer.MAX_VALUE)), "pq min order");
        check(pollAllMax(vals).equals(List.of(Integer.MAX_VALUE, 5, 3, 3, -1, Integer.MIN_VALUE)), "pq max order");
        check(pollAllMin(new int[]{}).isEmpty(), "pq empty");
        int[][] tasks = {{3, 10}, {1, 7}, {3, 2}, {2, 5}, {1, 9}};
        check(idsByPriority(tasks).equals(List.of(7, 9, 5, 2, 10)), "pq pairs " + idsByPriority(tasks));

        System.out.println("OK P1223_HeapsTheoryVideo");
    }
}
