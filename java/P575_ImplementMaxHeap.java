import java.util.*;

/** TUF 575 - Implement Max Heap: insert, getMax, extractMax, changeKey, isEmpty, heapSize. */
public class P575_ImplementMaxHeap {

    interface MaxHeapOps {
        void insert(int key);
        int getMax();
        int extractMax();
        void changeKey(int index, int newVal);
        boolean isEmpty();
        int heapSize();
    }

    /** Approach 1: unsorted dynamic array. insert and changeKey O(1), getMax and extractMax O(n). */
    static class UnsortedArrayHeap implements MaxHeapOps {
        private int[] data = new int[8];
        private int size = 0;

        public void insert(int key) {
            if (size == data.length) data = Arrays.copyOf(data, size * 2);
            data[size++] = key;
        }

        private int maxIndex() {
            if (size == 0) throw new NoSuchElementException("heap is empty");
            int best = 0;
            for (int i = 1; i < size; i++) {
                if (data[i] > data[best]) best = i;
            }
            return best;
        }

        public int getMax() {
            return data[maxIndex()];
        }

        public int extractMax() {
            int i = maxIndex();
            int max = data[i];
            data[i] = data[--size];                        // order is irrelevant: fill the hole with the last element
            return max;
        }

        public void changeKey(int index, int newVal) {
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index);
            data[index] = newVal;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int heapSize() {
            return size;
        }
    }

    /** Approach 2: binary max-heap in an array. insert, extractMax, changeKey O(log n); getMax O(1). */
    static class BinaryMaxHeap implements MaxHeapOps {
        private int[] heap;
        private int size;

        BinaryMaxHeap() {
            heap = new int[8];
        }

        /** Builds a heap from arbitrary values in O(n) by sifting down every internal node, last one first. */
        BinaryMaxHeap(int[] values) {
            heap = Arrays.copyOf(values, Math.max(8, values.length));
            size = values.length;
            for (int i = size / 2 - 1; i >= 0; i--) siftDown(i);
        }

        public void insert(int key) {
            if (size == heap.length) heap = Arrays.copyOf(heap, size * 2);
            heap[size] = key;
            siftUp(size++);
        }

        public int getMax() {
            if (size == 0) throw new NoSuchElementException("heap is empty");
            return heap[0];
        }

        public int extractMax() {
            if (size == 0) throw new NoSuchElementException("heap is empty");
            int max = heap[0];
            heap[0] = heap[--size];                        // move the last leaf to the root
            siftDown(0);                                   // and let it sink to its place
            return max;
        }

        public void changeKey(int index, int newVal) {
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index);
            int old = heap[index];
            heap[index] = newVal;
            if (newVal > old) siftUp(index);               // bigger: may beat its parent
            else siftDown(index);                          // smaller: may lose to a child
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int heapSize() {
            return size;
        }

        private void siftUp(int i) {
            while (i > 0) {
                int p = (i - 1) / 2;
                if (heap[p] >= heap[i]) break;
                swap(p, i);
                i = p;
            }
        }

        private void siftDown(int i) {
            while (true) {
                int largest = i, l = 2 * i + 1, r = 2 * i + 2;
                if (l < size && heap[l] > heap[largest]) largest = l;
                if (r < size && heap[r] > heap[largest]) largest = r;
                if (largest == i) return;
                swap(i, largest);
                i = largest;
            }
        }

        private void swap(int i, int j) {
            int t = heap[i];
            heap[i] = heap[j];
            heap[j] = t;
        }

        int[] snapshot() {
            return Arrays.copyOf(heap, size);
        }
    }

    /** Runs a script of operations and records what each returns ("-" for insert). */
    static List<String> simulate(MaxHeapOps h, String[] ops, int[] args) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case "insert" -> {
                    h.insert(args[i]);
                    out.add("-");
                }
                case "getMax" -> out.add(String.valueOf(h.getMax()));
                case "extractMax" -> out.add(String.valueOf(h.extractMax()));
                case "isEmpty" -> out.add(String.valueOf(h.isEmpty()));
                case "heapSize" -> out.add(String.valueOf(h.heapSize()));
                default -> throw new IllegalArgumentException("unknown op " + ops[i]);
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<MaxHeapOps> freshHeaps() {
        return List.<MaxHeapOps>of(new UnsortedArrayHeap(), new BinaryMaxHeap());
    }

    static void verify(String[] ops, int[] args, List<String> expected) {
        for (MaxHeapOps h : freshHeaps()) {
            List<String> got = simulate(h, ops, args);
            check(got.equals(expected), h.getClass().getSimpleName() + " got " + got + " expected " + expected);
        }
    }

    static boolean throwsOnEmpty(Runnable action) {
        try {
            action.run();
            return false;
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    static boolean isMaxHeap(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[(i - 1) / 2] < a[i]) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        // 1. basic order
        verify(new String[]{"insert", "insert", "insert", "getMax", "insert", "heapSize", "extractMax", "extractMax", "getMax", "isEmpty", "extractMax", "extractMax", "isEmpty"},
               new int[]{5, 3, 8, 0, 10, 0, 0, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "8", "-", "4", "10", "8", "5", "false", "5", "3", "true"));
        // 2. duplicates and negatives
        verify(new String[]{"insert", "insert", "insert", "extractMax", "getMax", "insert", "extractMax", "extractMax", "heapSize", "extractMax", "isEmpty"},
               new int[]{-2, -7, -2, 0, 0, 9, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "-2", "-2", "-", "9", "-2", "1", "-7", "true"));
        // 3. edge: a fresh heap
        verify(new String[]{"isEmpty", "heapSize"}, new int[]{0, 0}, List.of("true", "0"));
        // 4. edge: getMax and extractMax on an empty heap throw, and the heap stays usable
        for (MaxHeapOps h : freshHeaps()) {
            check(throwsOnEmpty(h::getMax), "getMax on empty must throw");
            check(throwsOnEmpty(h::extractMax), "extractMax on empty must throw");
            h.insert(4);
            check(h.getMax() == 4 && h.heapSize() == 1, "usable after underflow");
        }
        // 5. changeKey: inserting 10, 9, 7, 4 in decreasing order leaves both arrays as [10, 9, 7, 4]
        for (MaxHeapOps h : freshHeaps()) {
            for (int v : new int[]{10, 9, 7, 4}) h.insert(v);
            h.changeKey(3, 15);                                    // 4 -> 15
            check(h.getMax() == 15, h.getClass().getSimpleName() + " increase-key");
            List<Integer> order = new ArrayList<>();
            while (!h.isEmpty()) order.add(h.extractMax());
            check(order.equals(List.of(15, 10, 9, 7)), "order after increase-key " + order);
        }
        BinaryMaxHeap bh = new BinaryMaxHeap();
        for (int v : new int[]{10, 9, 7, 4}) bh.insert(v);
        bh.changeKey(3, 15);
        check(Arrays.equals(bh.snapshot(), new int[]{15, 10, 7, 9}), "increase-key sifts up " + Arrays.toString(bh.snapshot()));
        bh.changeKey(0, 1);
        check(Arrays.equals(bh.snapshot(), new int[]{10, 9, 7, 1}), "decrease-key sifts down " + Arrays.toString(bh.snapshot()));
        // 6. building from an array in O(n)
        BinaryMaxHeap built = new BinaryMaxHeap(new int[]{3, 1, 6, 5, 2, 4});
        check(Arrays.equals(built.snapshot(), new int[]{6, 5, 4, 1, 2, 3}), "build heap " + Arrays.toString(built.snapshot()));
        check(new BinaryMaxHeap(new int[]{}).isEmpty(), "build from empty array");
        // 7. seeded random workload against java.util.PriorityQueue (max order)
        Random rnd = new Random(575);
        for (MaxHeapOps h : freshHeaps()) {
            PriorityQueue<Integer> oracle = new PriorityQueue<>(Comparator.reverseOrder());
            for (int step = 0; step < 4000; step++) {
                int op = rnd.nextInt(3);
                if (oracle.isEmpty() || op < 2) {
                    int v = rnd.nextInt(201) - 100;
                    h.insert(v);
                    oracle.offer(v);
                } else {
                    check(h.extractMax() == oracle.poll(), "random extractMax mismatch");
                }
                check(h.heapSize() == oracle.size(), "random size mismatch");
                if (!oracle.isEmpty()) check(h.getMax() == oracle.peek(), "random getMax mismatch");
            }
        }
        // 8. random changeKey and random build keep the binary heap valid and keep the right multiset
        int[] start = new int[300];
        List<Integer> mirror = new ArrayList<>();
        for (int i = 0; i < start.length; i++) {
            start[i] = rnd.nextInt(1000);
            mirror.add(start[i]);
        }
        BinaryMaxHeap rh = new BinaryMaxHeap(start);
        check(isMaxHeap(rh.snapshot()), "heap property after build");
        for (int step = 0; step < 300; step++) {
            int idx = rnd.nextInt(rh.heapSize());
            int newVal = rnd.nextInt(1000);
            mirror.remove(Integer.valueOf(rh.snapshot()[idx]));
            mirror.add(newVal);
            rh.changeKey(idx, newVal);
            check(isMaxHeap(rh.snapshot()), "heap property after changeKey");
        }
        mirror.sort(Comparator.reverseOrder());
        List<Integer> drained = new ArrayList<>();
        while (!rh.isEmpty()) drained.add(rh.extractMax());
        check(drained.equals(mirror), "multiset after random changeKey");
        System.out.println("OK P575_ImplementMaxHeap");
    }
}
