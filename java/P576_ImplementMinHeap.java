import java.util.*;

/** TUF 576 - Implement Min Heap. insert, getMin, extractMin, changeKey, isEmpty and heapSize on an array. */
public class P576_ImplementMinHeap {

    /** Operations every implementation supports, so one test script can drive both. */
    interface MinHeapOps {
        void insert(int key);
        int getMin();
        int extractMin();
        void changeKey(int index, int newVal);
        boolean isEmpty();
        int heapSize();
    }

    /** Approach 1: unsorted growable array. insert and changeKey O(1), getMin and extractMin O(n). */
    static class UnsortedArrayHeap implements MinHeapOps {
        private int[] data = new int[4];
        private int size = 0;

        public void insert(int key) {
            if (size == data.length) data = Arrays.copyOf(data, size * 2);
            data[size++] = key;
        }

        private int minIndex() {
            if (size == 0) throw new NoSuchElementException("heap is empty");
            int best = 0;
            for (int i = 1; i < size; i++) {
                if (data[i] < data[best]) best = i;
            }
            return best;
        }

        public int getMin() {
            return data[minIndex()];
        }

        public int extractMin() {
            int i = minIndex();
            int min = data[i];
            data[i] = data[--size];          // fill the hole with the last element
            return min;
        }

        public void changeKey(int index, int newVal) {
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index);
            data[index] = newVal;            // there is no order to restore
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public int heapSize() {
            return size;
        }
    }

    /** Approach 2: binary min-heap in an array. insert, extractMin and changeKey O(log n); getMin O(1). */
    static class BinaryMinHeap implements MinHeapOps {
        private int[] heap = new int[4];
        private int size = 0;

        public void insert(int key) {
            if (size == heap.length) heap = Arrays.copyOf(heap, size * 2);
            heap[size] = key;
            size++;
            siftUp(size - 1);
        }

        public int getMin() {
            if (size == 0) throw new NoSuchElementException("heap is empty");
            return heap[0];
        }

        public int extractMin() {
            if (size == 0) throw new NoSuchElementException("heap is empty");
            int min = heap[0];
            size--;
            heap[0] = heap[size];            // last leaf becomes the root
            siftDown(0);
            return min;
        }

        public void changeKey(int index, int newVal) {
            if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index);
            int old = heap[index];
            heap[index] = newVal;
            if (newVal < old) siftUp(index);
            else siftDown(index);
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
                if (heap[p] <= heap[i]) break;
                swap(i, p);
                i = p;
            }
        }

        private void siftDown(int i) {
            while (true) {
                int smallest = i, l = 2 * i + 1, r = 2 * i + 2;
                if (l < size && heap[l] < heap[smallest]) smallest = l;
                if (r < size && heap[r] < heap[smallest]) smallest = r;
                if (smallest == i) return;
                swap(i, smallest);
                i = smallest;
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
    static List<String> simulate(MinHeapOps h, String[] ops, int[] args) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case "insert" -> { h.insert(args[i]); out.add("-"); }
                case "getMin" -> out.add(String.valueOf(h.getMin()));
                case "extractMin" -> out.add(String.valueOf(h.extractMin()));
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

    static List<MinHeapOps> freshHeaps() {
        return List.<MinHeapOps>of(new UnsortedArrayHeap(), new BinaryMinHeap());
    }

    static void verify(String[] ops, int[] args, List<String> expected) {
        for (MinHeapOps h : freshHeaps()) {
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

    static boolean isMinHeap(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[(i - 1) / 2] > a[i]) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        // 1. basic order
        verify(new String[]{"insert", "insert", "insert", "getMin", "insert", "heapSize", "extractMin", "extractMin", "getMin", "isEmpty", "extractMin", "extractMin", "isEmpty"},
               new int[]{5, 3, 8, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "3", "-", "4", "1", "3", "5", "false", "5", "8", "true"));
        // 2. duplicates and negatives
        verify(new String[]{"insert", "insert", "insert", "extractMin", "getMin", "insert", "extractMin", "extractMin", "heapSize", "extractMin", "isEmpty"},
               new int[]{-2, 7, -2, 0, 0, -9, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "-2", "-2", "-", "-9", "-2", "1", "7", "true"));
        // 3. edge: a fresh heap
        verify(new String[]{"isEmpty", "heapSize"}, new int[]{0, 0}, List.of("true", "0"));
        // 4. edge: getMin and extractMin on an empty heap throw, and the heap stays usable
        for (MinHeapOps h : freshHeaps()) {
            check(throwsOnEmpty(h::getMin), "getMin on empty must throw");
            check(throwsOnEmpty(h::extractMin), "extractMin on empty must throw");
            h.insert(4);
            check(h.getMin() == 4 && h.heapSize() == 1, "usable after underflow");
        }
        // 5. changeKey: inserting 4, 7, 9, 10 in increasing order leaves both arrays as [4, 7, 9, 10]
        for (MinHeapOps h : freshHeaps()) {
            for (int v : new int[]{4, 7, 9, 10}) h.insert(v);
            h.changeKey(3, 1);                                     // 10 -> 1
            check(h.getMin() == 1, h.getClass().getSimpleName() + " decrease-key");
            List<Integer> order = new ArrayList<>();
            while (!h.isEmpty()) order.add(h.extractMin());
            check(order.equals(List.of(1, 4, 7, 9)), "order after decrease-key " + order);
        }
        BinaryMinHeap bh = new BinaryMinHeap();
        for (int v : new int[]{4, 7, 9, 10}) bh.insert(v);
        bh.changeKey(3, 1);
        check(Arrays.equals(bh.snapshot(), new int[]{1, 4, 9, 7}), "decrease-key sifts up " + Arrays.toString(bh.snapshot()));
        bh.changeKey(0, 20);
        check(Arrays.equals(bh.snapshot(), new int[]{4, 7, 9, 20}), "increase-key sifts down " + Arrays.toString(bh.snapshot()));
        // 6. seeded random workload against java.util.PriorityQueue
        Random rnd = new Random(42);
        for (MinHeapOps h : freshHeaps()) {
            PriorityQueue<Integer> oracle = new PriorityQueue<>();
            for (int step = 0; step < 4000; step++) {
                int op = rnd.nextInt(3);
                if (oracle.isEmpty() || op == 0 || op == 1) {
                    int v = rnd.nextInt(201) - 100;
                    h.insert(v);
                    oracle.offer(v);
                } else {
                    check(h.extractMin() == oracle.poll(), "random extractMin mismatch");
                }
                check(h.heapSize() == oracle.size(), "random size mismatch");
                if (!oracle.isEmpty()) check(h.getMin() == oracle.peek(), "random getMin mismatch");
            }
        }
        // 7. random changeKey keeps the binary heap valid and keeps the right multiset
        BinaryMinHeap rh = new BinaryMinHeap();
        List<Integer> mirror = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            int v = rnd.nextInt(1000);
            rh.insert(v);
            mirror.add(v);
        }
        for (int step = 0; step < 300; step++) {
            int idx = rnd.nextInt(rh.heapSize());
            int newVal = rnd.nextInt(1000);
            mirror.remove(Integer.valueOf(rh.snapshot()[idx]));
            mirror.add(newVal);
            rh.changeKey(idx, newVal);
            check(isMinHeap(rh.snapshot()), "heap property after changeKey");
        }
        Collections.sort(mirror);
        List<Integer> drained = new ArrayList<>();
        while (!rh.isEmpty()) drained.add(rh.extractMin());
        check(drained.equals(mirror), "multiset after random changeKey");
        System.out.println("OK P576_ImplementMinHeap");
    }
}
