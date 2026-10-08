import java.util.*;

/** TUF 391 - Implement stack using Linkedlist. push, pop, top, isEmpty and size on a singly linked list. */
public class P391_ImplementStackUsingLinkedlist {

    /** Common contract so every implementation can be driven by the same test script. */
    interface IntStack {
        void push(int x);
        int pop();
        int top();
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

    /** Approach 1: the tail is the top. Every push, pop and top walks the whole list: O(n) each. */
    static class TailLinkedStack implements IntStack {
        private Node head;                       // bottom of the stack
        private int count = 0;

        public void push(int x) {
            Node node = new Node(x);
            if (head == null) {
                head = node;
            } else {
                Node cur = head;
                while (cur.next != null) cur = cur.next;   // walk to the tail
                cur.next = node;
            }
            count++;
        }

        public int pop() {
            if (head == null) throw new NoSuchElementException("stack underflow");
            count--;
            if (head.next == null) {             // single node: the tail is the head itself
                int x = head.val;
                head = null;
                return x;
            }
            Node cur = head;
            while (cur.next.next != null) cur = cur.next;   // stop at the second-last node
            int x = cur.next.val;
            cur.next = null;                     // unlink the old tail
            return x;
        }

        public int top() {
            if (head == null) throw new NoSuchElementException("stack is empty");
            Node cur = head;
            while (cur.next != null) cur = cur.next;
            return cur.val;
        }

        public boolean isEmpty() {
            return head == null;
        }

        public int size() {
            return count;
        }
    }

    /** Approach 2: the head is the top. push, pop and top touch only the head: O(1) each. */
    static class HeadLinkedStack implements IntStack {
        private Node head;                       // top of the stack
        private int count = 0;

        public void push(int x) {
            Node node = new Node(x);
            node.next = head;                    // new node points at the old top
            head = node;
            count++;
        }

        public int pop() {
            if (head == null) throw new NoSuchElementException("stack underflow");
            int x = head.val;
            head = head.next;                    // the old top is now unreachable and gets collected
            count--;
            return x;
        }

        public int top() {
            if (head == null) throw new NoSuchElementException("stack is empty");
            return head.val;
        }

        public boolean isEmpty() {
            return head == null;
        }

        public int size() {
            return count;
        }
    }

    /** Runs a script against a stack and records what each operation returns ("-" for push). */
    static List<String> simulate(IntStack st, String[] ops, int[] args) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case "push" -> { st.push(args[i]); out.add("-"); }
                case "pop" -> out.add(String.valueOf(st.pop()));
                case "top" -> out.add(String.valueOf(st.top()));
                case "isEmpty" -> out.add(String.valueOf(st.isEmpty()));
                case "size" -> out.add(String.valueOf(st.size()));
                default -> throw new IllegalArgumentException("unknown op " + ops[i]);
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<IntStack> freshStacks() {
        return List.<IntStack>of(new TailLinkedStack(), new HeadLinkedStack());
    }

    static void verify(String[] ops, int[] args, List<String> expected) {
        for (IntStack st : freshStacks()) {
            List<String> got = simulate(st, ops, args);
            check(got.equals(expected), st.getClass().getSimpleName() + " got " + got + " expected " + expected);
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
        // 1. plain LIFO order
        verify(new String[]{"push", "push", "push", "top", "pop", "pop", "size", "isEmpty", "pop", "isEmpty"},
               new int[]{1, 2, 3, 0, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "3", "3", "2", "1", "false", "1", "true"));
        // 2. interleaved pushes and pops, negatives and duplicates, emptied and refilled in between
        verify(new String[]{"push", "pop", "push", "push", "pop", "push", "top", "size", "pop", "pop"},
               new int[]{-5, 0, 7, 7, 0, -5, 0, 0, 0, 0},
               List.of("-", "-5", "-", "-", "7", "-", "-5", "2", "-5", "7"));
        // 3. edge: a fresh stack
        verify(new String[]{"isEmpty", "size"}, new int[]{0, 0}, List.of("true", "0"));
        // 4. edge: a single element pushed and popped repeatedly (exercises the one-node branch of pop)
        verify(new String[]{"push", "pop", "push", "top", "pop", "isEmpty", "size"},
               new int[]{4, 0, 8, 0, 0, 0, 0},
               List.of("-", "4", "-", "8", "8", "true", "0"));
        // 5. edge: pop and top on an empty stack throw, and the stack stays usable afterwards
        for (IntStack st : freshStacks()) {
            check(throwsOnEmpty(st::pop), "pop on empty must throw");
            check(throwsOnEmpty(st::top), "top on empty must throw");
            st.push(9);
            check(st.top() == 9 && st.size() == 1, "stack usable after underflow");
        }
        // 6. deterministic random workload against java.util.ArrayDeque used as a stack
        Random rnd = new Random(42);
        for (IntStack st : freshStacks()) {
            Deque<Integer> oracle = new ArrayDeque<>();
            for (int i = 0; i < 3000; i++) {
                if (oracle.isEmpty() || rnd.nextInt(3) > 0) {
                    int v = rnd.nextInt(2001) - 1000;
                    st.push(v);
                    oracle.push(v);
                } else {
                    check(st.pop() == oracle.pop(), "random pop mismatch");
                }
                check(st.size() == oracle.size() && st.isEmpty() == oracle.isEmpty(), "random size mismatch");
                if (!oracle.isEmpty()) check(st.top() == oracle.peek(), "random top mismatch");
            }
        }
        System.out.println("OK P391_ImplementStackUsingLinkedlist");
    }
}
