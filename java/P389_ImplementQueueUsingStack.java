import java.util.*;

/** TUF 389 - Implement Queue using Stack. FIFO push/pop/peek/isEmpty built only out of LIFO stack operations. */
public class P389_ImplementQueueUsingStack {

    /** Common contract so every implementation can be driven by the same test script. */
    interface IntQueue {
        void push(int x);
        int pop();
        int peek();
        boolean isEmpty();
        int size();
    }

    /** Approach 1: costly push. Empty the stack into a helper, push x at the bottom, pour everything back. push O(n), pop and peek O(1). */
    static class CostlyPushQueue implements IntQueue {
        private final Deque<Integer> stack = new ArrayDeque<>();    // top of stack is always the oldest element
        private final Deque<Integer> helper = new ArrayDeque<>();

        public void push(int x) {
            while (!stack.isEmpty()) helper.push(stack.pop());      // reverse into helper
            stack.push(x);                                          // x becomes the bottom
            while (!helper.isEmpty()) stack.push(helper.pop());     // old elements back on top, oldest ends on top
        }

        public int pop() {
            if (stack.isEmpty()) throw new NoSuchElementException("queue underflow");
            return stack.pop();
        }

        public int peek() {
            if (stack.isEmpty()) throw new NoSuchElementException("queue is empty");
            return stack.peek();
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public int size() {
            return stack.size();
        }
    }

    /** Approach 2: input and output stacks. Pushes go to input; pops come from output, refilled by reversing input only when it runs dry. Amortised O(1) everywhere. */
    static class AmortizedQueue implements IntQueue {
        private final Deque<Integer> input = new ArrayDeque<>();    // newest on top
        private final Deque<Integer> output = new ArrayDeque<>();   // oldest on top

        public void push(int x) {
            input.push(x);
        }

        private void refillOutputIfEmpty() {
            if (output.isEmpty()) {
                while (!input.isEmpty()) output.push(input.pop());  // one reversal flips newest-first into oldest-first
            }
        }

        public int pop() {
            refillOutputIfEmpty();
            if (output.isEmpty()) throw new NoSuchElementException("queue underflow");
            return output.pop();
        }

        public int peek() {
            refillOutputIfEmpty();
            if (output.isEmpty()) throw new NoSuchElementException("queue is empty");
            return output.peek();
        }

        public boolean isEmpty() {
            return input.isEmpty() && output.isEmpty();
        }

        public int size() {
            return input.size() + output.size();
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
        return List.<IntQueue>of(new CostlyPushQueue(), new AmortizedQueue());
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
        // 1. LeetCode 232 example: push 1, push 2, peek -> 1, pop -> 1, isEmpty -> false
        verify(new String[]{"push", "push", "peek", "pop", "isEmpty"},
               new int[]{1, 2, 0, 0, 0},
               List.of("-", "-", "1", "1", "false"));
        // 2. pushes after a pop: the amortised version must drain output before touching input
        verify(new String[]{"push", "push", "pop", "push", "push", "pop", "pop", "peek", "size", "pop", "isEmpty"},
               new int[]{1, 2, 0, 3, 4, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "1", "-", "-", "2", "3", "4", "1", "4", "true"));
        // 3. negatives and duplicates, emptied and refilled in between
        verify(new String[]{"push", "pop", "push", "push", "pop", "peek", "push", "pop", "pop", "size"},
               new int[]{-4, 0, 7, 7, 0, 0, -4, 0, 0, 0},
               List.of("-", "-4", "-", "-", "7", "7", "-", "7", "-4", "0"));
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
        System.out.println("OK P389_ImplementQueueUsingStack");
    }
}
