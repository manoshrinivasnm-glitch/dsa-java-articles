import java.util.*;

/** TUF 387 - Implement Queue using Arrays. push (enqueue), pop (dequeue), peek, isEmpty and size on an int[]. */
public class P387_ImplementQueueUsingArrays {

    /** Common contract so every implementation can be driven by the same test script. */
    interface IntQueue {
        void push(int x);
        int pop();
        int peek();
        boolean isEmpty();
        int size();
    }

    /** Approach 1: the front is always index 0, so pop shifts every element one slot left. push O(1), pop O(n). */
    static class ShiftingArrayQueue implements IntQueue {
        private final int[] data;
        private int count = 0;                   // elements live in data[0 .. count-1], oldest at 0

        ShiftingArrayQueue(int capacity) {
            data = new int[capacity];
        }

        public void push(int x) {
            if (count == data.length) throw new IllegalStateException("queue overflow");
            data[count++] = x;
        }

        public int pop() {
            if (count == 0) throw new NoSuchElementException("queue underflow");
            int front = data[0];
            for (int i = 1; i < count; i++) data[i - 1] = data[i];   // O(n) shift
            count--;
            return front;
        }

        public int peek() {
            if (count == 0) throw new NoSuchElementException("queue is empty");
            return data[0];
        }

        public boolean isEmpty() {
            return count == 0;
        }

        public int size() {
            return count;
        }
    }

    /** Approach 2: circular buffer. front moves forward on pop, rear is derived as (front + count) mod capacity. All O(1). */
    static class CircularArrayQueue implements IntQueue {
        private final int[] data;
        private int front = 0;                   // index of the oldest element
        private int count = 0;                   // how many slots are in use

        CircularArrayQueue(int capacity) {
            data = new int[capacity];
        }

        public void push(int x) {
            if (count == data.length) throw new IllegalStateException("queue overflow");
            data[(front + count) % data.length] = x;   // wraps around to reuse freed slots
            count++;
        }

        public int pop() {
            if (count == 0) throw new NoSuchElementException("queue underflow");
            int x = data[front];
            front = (front + 1) % data.length;
            count--;
            return x;
        }

        public int peek() {
            if (count == 0) throw new NoSuchElementException("queue is empty");
            return data[front];
        }

        public boolean isEmpty() {
            return count == 0;
        }

        public int size() {
            return count;
        }
    }

    /** Approach 3: circular buffer that doubles when full, copying elements out in FIFO order. push amortised O(1). */
    static class DynamicCircularQueue implements IntQueue {
        private int[] data = new int[2];
        private int front = 0;
        private int count = 0;

        public void push(int x) {
            if (count == data.length) grow();
            data[(front + count) % data.length] = x;
            count++;
        }

        private void grow() {
            int[] bigger = new int[data.length * 2];
            for (int i = 0; i < count; i++) bigger[i] = data[(front + i) % data.length];   // unwrap: oldest lands at 0
            data = bigger;
            front = 0;
        }

        public int pop() {
            if (count == 0) throw new NoSuchElementException("queue underflow");
            int x = data[front];
            front = (front + 1) % data.length;
            count--;
            return x;
        }

        public int peek() {
            if (count == 0) throw new NoSuchElementException("queue is empty");
            return data[front];
        }

        public boolean isEmpty() {
            return count == 0;
        }

        public int size() {
            return count;
        }

        int capacity() {
            return data.length;
        }
    }

    /** Runs a script against a queue and records what each operation returns ("-" for push). */
    static List<String> simulate(IntQueue q, String[] ops, int[] args) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case "push" -> { q.push(args[i]); out.add("-"); }
                case "pop" -> out.add(String.valueOf(q.pop()));
                case "peek" -> out.add(String.valueOf(q.peek()));
                case "isEmpty" -> out.add(String.valueOf(q.isEmpty()));
                case "size" -> out.add(String.valueOf(q.size()));
                default -> throw new IllegalArgumentException("unknown op " + ops[i]);
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<IntQueue> freshQueues(int capacity) {
        return List.<IntQueue>of(new ShiftingArrayQueue(capacity), new CircularArrayQueue(capacity), new DynamicCircularQueue());
    }

    static void verify(int capacity, String[] ops, int[] args, List<String> expected) {
        for (IntQueue q : freshQueues(capacity)) {
            List<String> got = simulate(q, ops, args);
            check(got.equals(expected), q.getClass().getSimpleName() + " got " + got + " expected " + expected);
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

    public static void main(String[] args) {
        // 1. plain FIFO order
        verify(3, new String[]{"push", "push", "push", "peek", "pop", "pop", "size", "isEmpty", "pop", "isEmpty"},
               new int[]{1, 2, 3, 0, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "1", "1", "2", "1", "false", "3", "true"));
        // 2. capacity 2 forces the circular buffer to wrap around
        verify(2, new String[]{"push", "push", "pop", "push", "peek", "size", "pop", "pop", "isEmpty"},
               new int[]{1, 2, 0, 3, 0, 0, 0, 0, 0},
               List.of("-", "-", "1", "-", "2", "2", "2", "3", "true"));
        // 3. negatives and duplicates, with the queue emptied and refilled in between
        verify(2, new String[]{"push", "pop", "push", "push", "pop", "peek", "push", "pop", "pop", "size"},
               new int[]{-4, 0, 7, 7, 0, 0, -4, 0, 0, 0},
               List.of("-", "-4", "-", "-", "7", "7", "-", "7", "-4", "0"));
        // 4. edge: a fresh queue
        verify(1, new String[]{"isEmpty", "size"}, new int[]{0, 0}, List.of("true", "0"));
        // 5. edge: pop and peek on an empty queue throw, and the queue stays usable afterwards
        for (IntQueue q : freshQueues(1)) {
            check(throwsOnEmpty(q::pop), "pop on empty must throw");
            check(throwsOnEmpty(q::peek), "peek on empty must throw");
            q.push(9);
            check(q.peek() == 9 && q.size() == 1, "queue usable after underflow");
        }
        // 6. fixed queues refuse a push beyond capacity; the dynamic one grows and keeps FIFO order across the wrap
        for (IntQueue q : List.<IntQueue>of(new ShiftingArrayQueue(2), new CircularArrayQueue(2))) {
            q.push(1);
            q.push(2);
            boolean overflow = false;
            try {
                q.push(3);
            } catch (IllegalStateException e) {
                overflow = true;
            }
            check(overflow && q.size() == 2 && q.peek() == 1, "fixed queue must reject a third push");
        }
        DynamicCircularQueue dyn = new DynamicCircularQueue();
        dyn.push(10);
        dyn.push(20);
        check(dyn.pop() == 10, "warm-up pop");
        for (int i = 0; i < 1000; i++) dyn.push(i);        // front is at index 1, so the data wraps before each grow
        check(dyn.size() == 1001 && dyn.capacity() == 1024, "capacity after 1001 elements should be 1024");
        check(dyn.pop() == 20, "oldest element survives the regrowth");
        for (int i = 0; i < 1000; i++) check(dyn.pop() == i, "FIFO order preserved across growth");
        check(dyn.isEmpty(), "dynamic queue drained");
        // 7. deterministic random workload against java.util.ArrayDeque
        Random rnd = new Random(7);
        for (IntQueue q : freshQueues(5000)) {
            Deque<Integer> oracle = new ArrayDeque<>();
            for (int i = 0; i < 5000; i++) {
                if (oracle.isEmpty() || rnd.nextInt(3) > 0) {
                    int v = rnd.nextInt(2001) - 1000;
                    q.push(v);
                    oracle.addLast(v);
                } else {
                    check(q.pop() == oracle.pollFirst(), "random pop mismatch");
                }
                check(q.size() == oracle.size() && q.isEmpty() == oracle.isEmpty(), "random size mismatch");
                if (!oracle.isEmpty()) check(q.peek() == oracle.peekFirst(), "random peek mismatch");
            }
        }
        System.out.println("OK P387_ImplementQueueUsingArrays");
    }
}
