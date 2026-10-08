import java.util.*;

/** TUF 392 - Implement Stack using Queue. LIFO push/pop/top/isEmpty built only out of FIFO queue operations. */
public class P392_ImplementStackUsingQueue {

    /** Common contract so every implementation can be driven by the same test script. */
    interface IntStack {
        void push(int x);
        int pop();
        int top();
        boolean isEmpty();
        int size();
    }

    /** Approach 1: two queues, cheap push. pop moves the first n-1 elements to the spare queue, takes the last one, swaps. push O(1), pop and top O(n). */
    static class TwoQueuesCostlyPop implements IntStack {
        private Queue<Integer> q1 = new ArrayDeque<>();     // holds the stack, oldest at the front
        private Queue<Integer> q2 = new ArrayDeque<>();     // always empty between operations

        public void push(int x) {
            q1.offer(x);
        }

        public int pop() {
            if (q1.isEmpty()) throw new NoSuchElementException("stack underflow");
            while (q1.size() > 1) q2.offer(q1.poll());      // everything except the most recent element
            int x = q1.poll();                              // the most recent element is the stack top
            Queue<Integer> t = q1;                          // swap roles; q2 is empty again
            q1 = q2;
            q2 = t;
            return x;
        }

        public int top() {
            int x = pop();
            q1.offer(x);                                    // re-append: it is the newest element again
            return x;
        }

        public boolean isEmpty() {
            return q1.isEmpty();
        }

        public int size() {
            return q1.size();
        }
    }

    /** Approach 2: two queues, costly push. x goes into the empty spare queue, the old contents follow it, then the queues swap. push O(n), pop and top O(1). */
    static class TwoQueuesCostlyPush implements IntStack {
        private Queue<Integer> q1 = new ArrayDeque<>();     // holds the stack, newest at the front
        private Queue<Integer> q2 = new ArrayDeque<>();     // always empty between operations

        public void push(int x) {
            q2.offer(x);                                    // x first, so it ends up at the front
            while (!q1.isEmpty()) q2.offer(q1.poll());      // older elements behind it, order kept
            Queue<Integer> t = q1;
            q1 = q2;
            q2 = t;
        }

        public int pop() {
            if (q1.isEmpty()) throw new NoSuchElementException("stack underflow");
            return q1.poll();
        }

        public int top() {
            if (q1.isEmpty()) throw new NoSuchElementException("stack is empty");
            return q1.peek();
        }

        public boolean isEmpty() {
            return q1.isEmpty();
        }

        public int size() {
            return q1.size();
        }
    }

    /** Approach 3: a single queue. After enqueueing x, rotate the other n-1 elements behind it so x sits at the front. push O(n), rest O(1). */
    static class OneQueue implements IntStack {
        private final Queue<Integer> q = new ArrayDeque<>();   // newest at the front

        public void push(int x) {
            q.offer(x);
            for (int i = 1; i < q.size(); i++) q.offer(q.poll());   // n-1 rotations move x to the front
        }

        public int pop() {
            if (q.isEmpty()) throw new NoSuchElementException("stack underflow");
            return q.poll();
        }

        public int top() {
            if (q.isEmpty()) throw new NoSuchElementException("stack is empty");
            return q.peek();
        }

        public boolean isEmpty() {
            return q.isEmpty();
        }

        public int size() {
            return q.size();
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
        return List.<IntStack>of(new TwoQueuesCostlyPop(), new TwoQueuesCostlyPush(), new OneQueue());
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
        // 1. LeetCode 225 example: push 1, push 2, top -> 2, pop -> 2, isEmpty -> false
        verify(new String[]{"push", "push", "top", "pop", "isEmpty"},
               new int[]{1, 2, 0, 0, 0},
               List.of("-", "-", "2", "2", "false"));
        // 2. LIFO order with top read twice in a row (top must not disturb the stack)
        verify(new String[]{"push", "push", "push", "top", "top", "pop", "pop", "size", "pop", "isEmpty"},
               new int[]{1, 2, 3, 0, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "3", "3", "3", "2", "1", "1", "true"));
        // 3. interleaved pushes and pops, negatives and duplicates
        verify(new String[]{"push", "pop", "push", "push", "pop", "push", "top", "size", "pop", "pop"},
               new int[]{-5, 0, 7, 7, 0, -5, 0, 0, 0, 0},
               List.of("-", "-5", "-", "-", "7", "-", "-5", "2", "-5", "7"));
        // 4. edge: a fresh stack
        verify(new String[]{"isEmpty", "size"}, new int[]{0, 0}, List.of("true", "0"));
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
        System.out.println("OK P392_ImplementStackUsingQueue");
    }
}
