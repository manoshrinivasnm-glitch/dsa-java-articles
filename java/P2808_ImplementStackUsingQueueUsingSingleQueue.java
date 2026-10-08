import java.util.*;

/** TUF 2808 - Implement Stack using Queue (using single queue). Only queue operations: offer, remove, element, size. */
public class P2808_ImplementStackUsingQueueUsingSingleQueue {

    interface IntStack {
        void push(int x);
        int pop();
        int top();
        boolean isEmpty();
        int size();
    }

    /** Approach 1: two queues, costly push. push O(n), pop and top O(1), space O(n). */
    static class TwoQueues implements IntStack {
        private Queue<Integer> main = new ArrayDeque<>();
        private Queue<Integer> helper = new ArrayDeque<>();

        public void push(int x) {
            helper.offer(x);                               // the newest element enters an empty queue first
            while (!main.isEmpty()) helper.offer(main.remove());
            Queue<Integer> t = main;                       // swap roles: helper now holds stack order
            main = helper;
            helper = t;
        }

        public int pop() {
            return main.remove();                          // throws NoSuchElementException when empty
        }

        public int top() {
            return main.element();
        }

        public boolean isEmpty() {
            return main.isEmpty();
        }

        public int size() {
            return main.size();
        }
    }

    /** Approach 2: one queue, costly pop. push O(1), pop and top O(n), space O(n). */
    static class OneQueueCostlyPop implements IntStack {
        private final Queue<Integer> q = new ArrayDeque<>();

        public void push(int x) {
            q.offer(x);                                    // the newest element sits at the back
        }

        public int pop() {
            if (q.isEmpty()) throw new NoSuchElementException("stack is empty");
            int n = q.size();
            for (int i = 0; i < n - 1; i++) q.offer(q.remove());  // cycle the older n - 1 behind the newest
            return q.remove();                             // the newest is now at the front
        }

        public int top() {
            int x = pop();
            q.offer(x);                                    // back at the back: original order restored
            return x;
        }

        public boolean isEmpty() {
            return q.isEmpty();
        }

        public int size() {
            return q.size();
        }
    }

    /** Approach 3: one queue, costly push. push O(n), pop and top O(1), space O(n). */
    static class OneQueueCostlyPush implements IntStack {
        private final Queue<Integer> q = new ArrayDeque<>();

        public void push(int x) {
            q.offer(x);
            int n = q.size();
            for (int i = 0; i < n - 1; i++) q.offer(q.remove());  // cycle older elements behind x
        }

        public int pop() {
            return q.remove();                             // throws NoSuchElementException when empty
        }

        public int top() {
            return q.element();
        }

        public boolean isEmpty() {
            return q.isEmpty();
        }

        public int size() {
            return q.size();
        }
    }

    /** Runs a script of operations and records what each returns ("-" for push). */
    static List<String> simulate(IntStack st, String[] ops, int[] args) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case "push" -> {
                    st.push(args[i]);
                    out.add("-");
                }
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
        return List.<IntStack>of(new TwoQueues(), new OneQueueCostlyPop(), new OneQueueCostlyPush());
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
        // 1. LeetCode 225 example
        verify(new String[]{"push", "push", "top", "pop", "isEmpty"},
               new int[]{1, 2, 0, 0, 0},
               List.of("-", "-", "2", "2", "false"));
        // 2. interleaved pushes and pops
        verify(new String[]{"push", "push", "push", "pop", "push", "size", "pop", "pop", "top", "pop", "isEmpty"},
               new int[]{10, 20, 30, 0, 40, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "30", "-", "3", "40", "20", "10", "10", "true"));
        // 3. duplicates and negatives
        verify(new String[]{"push", "push", "push", "pop", "top", "size"},
               new int[]{-5, 7, -5, 0, 0, 0},
               List.of("-", "-", "-", "-5", "7", "2"));
        // 4. edge: fresh stack, and pop/top on an empty stack throw but leave it usable
        verify(new String[]{"isEmpty", "size"}, new int[]{0, 0}, List.of("true", "0"));
        for (IntStack st : freshStacks()) {
            check(throwsOnEmpty(st::pop), st.getClass().getSimpleName() + " pop on empty must throw");
            check(throwsOnEmpty(st::top), st.getClass().getSimpleName() + " top on empty must throw");
            st.push(9);
            check(st.top() == 9 && st.size() == 1, "usable after underflow");
        }
        // 5. seeded random workload against ArrayDeque used as a stack
        Random rnd = new Random(2808);
        for (IntStack st : freshStacks()) {
            Deque<Integer> oracle = new ArrayDeque<>();
            for (int step = 0; step < 3000; step++) {
                int op = rnd.nextInt(3);
                if (oracle.isEmpty() || op == 0) {
                    int v = rnd.nextInt(1000);
                    st.push(v);
                    oracle.push(v);
                } else if (op == 1) {
                    check(st.pop() == oracle.pop(), "random pop mismatch");
                } else {
                    check(st.top() == oracle.peek(), "random top mismatch");
                }
                check(st.size() == oracle.size(), "random size mismatch");
            }
        }
        System.out.println("OK P2808_ImplementStackUsingQueueUsingSingleQueue");
    }
}
