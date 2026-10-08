import java.util.*;

/** TUF 388 - Implement queue using Linkedlist. push (enqueue), pop (dequeue), peek, isEmpty and size on a singly linked list. */
public class P388_ImplementQueueUsingLinkedlist {

    /** Common contract so every implementation can be driven by the same test script. */
    interface IntQueue {
        void push(int x);
        int pop();
        int peek();
        boolean isEmpty();
        int size();
    }

    /** One cell of the singly linked list. */
    static class Node {
        int val;
        Node next;

        Node(int val) {
            this.val = val;
        }
    }

    /** Approach 1: only a head pointer. push walks to the end of the list: O(n); pop and peek work at the head: O(1). */
    static class HeadOnlyQueue implements IntQueue {
        private Node head;                       // front of the queue
        private int count = 0;

        public void push(int x) {
            Node node = new Node(x);
            if (head == null) {
                head = node;
            } else {
                Node cur = head;
                while (cur.next != null) cur = cur.next;   // find the rear every time
                cur.next = node;
            }
            count++;
        }

        public int pop() {
            if (head == null) throw new NoSuchElementException("queue underflow");
            int x = head.val;
            head = head.next;
            count--;
            return x;
        }

        public int peek() {
            if (head == null) throw new NoSuchElementException("queue is empty");
            return head.val;
        }

        public boolean isEmpty() {
            return head == null;
        }

        public int size() {
            return count;
        }
    }

    /** Approach 2: head and tail pointers. push appends after tail, pop removes head: both O(1). */
    static class HeadTailQueue implements IntQueue {
        private Node head;                       // front: where pop happens
        private Node tail;                       // rear: where push happens
        private int count = 0;

        public void push(int x) {
            Node node = new Node(x);
            if (tail == null) {                  // empty queue: the new node is both front and rear
                head = node;
            } else {
                tail.next = node;
            }
            tail = node;
            count++;
        }

        public int pop() {
            if (head == null) throw new NoSuchElementException("queue underflow");
            int x = head.val;
            head = head.next;
            if (head == null) tail = null;       // removed the last node: the rear must not dangle
            count--;
            return x;
        }

        public int peek() {
            if (head == null) throw new NoSuchElementException("queue is empty");
            return head.val;
        }

        public boolean isEmpty() {
            return head == null;
        }

        public int size() {
            return count;
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

    static List<IntQueue> freshQueues() {
        return List.<IntQueue>of(new HeadOnlyQueue(), new HeadTailQueue());
    }

    static void verify(String[] ops, int[] args, List<String> expected) {
        for (IntQueue q : freshQueues()) {
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
        verify(new String[]{"push", "push", "push", "peek", "pop", "pop", "size", "isEmpty", "pop", "isEmpty"},
               new int[]{1, 2, 3, 0, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "1", "1", "2", "1", "false", "3", "true"));
        // 2. the dangerous sequence: empty the queue completely, then push again (tail must have been reset)
        verify(new String[]{"push", "pop", "push", "push", "peek", "pop", "pop", "isEmpty", "push", "size", "pop"},
               new int[]{1, 0, 2, 3, 0, 0, 0, 0, 4, 0, 0},
               List.of("-", "1", "-", "-", "2", "2", "3", "true", "-", "1", "4"));
        // 3. negatives and duplicates
        verify(new String[]{"push", "push", "push", "pop", "peek", "push", "pop", "pop", "pop", "size"},
               new int[]{-4, 7, 7, 0, 0, -4, 0, 0, 0, 0},
               List.of("-", "-", "-", "-4", "7", "-", "7", "7", "-4", "0"));
        // 4. edge: a fresh queue
        verify(new String[]{"isEmpty", "size"}, new int[]{0, 0}, List.of("true", "0"));
        // 5. edge: pop and peek on an empty queue throw, and the queue stays usable afterwards
        for (IntQueue q : freshQueues()) {
            check(throwsOnEmpty(q::pop), "pop on empty must throw");
            check(throwsOnEmpty(q::peek), "peek on empty must throw");
            q.push(9);
            check(q.peek() == 9 && q.size() == 1, "queue usable after underflow");
        }
        // 6. deterministic random workload against java.util.ArrayDeque used as a queue
        Random rnd = new Random(7);
        for (IntQueue q : freshQueues()) {
            Deque<Integer> oracle = new ArrayDeque<>();
            for (int i = 0; i < 3000; i++) {
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
        System.out.println("OK P388_ImplementQueueUsingLinkedlist");
    }
}
