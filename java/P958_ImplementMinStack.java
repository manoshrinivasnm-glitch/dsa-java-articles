import java.util.*;

/** TUF 958 - Implement Min Stack (LeetCode 155). push, pop, top and getMin, each in O(1). */
public class P958_ImplementMinStack {

    /** Common contract so every implementation can be driven by the same test script. */
    interface MinStack {
        void push(int x);  // add x on top
        void pop();        // remove the top element
        int top();         // read the top element
        int getMin();      // smallest value currently in the stack
    }

    /** Approach 1: every entry stores the value together with the minimum of everything at or below it. O(1) per operation, 2n ints. */
    static class PairMinStack implements MinStack {
        // each entry is {value, min of this entry and all below}
        private final Deque<int[]> stack = new ArrayDeque<>();

        public void push(int x) {
            // minimum of x and everything already below it
            int min = stack.isEmpty() ? x : Math.min(x, stack.peek()[1]);
            stack.push(new int[]{x, min});  // store the value with that running minimum
        }

        public void pop() {
            if (stack.isEmpty()) throw new NoSuchElementException("stack underflow");   // nothing to remove
            stack.pop();  // the entry below still knows its own minimum
        }

        public int top() {
            if (stack.isEmpty()) throw new NoSuchElementException("stack is empty");   // nothing to read
            return stack.peek()[0];  // the value half of the entry
        }

        public int getMin() {
            if (stack.isEmpty()) throw new NoSuchElementException("stack is empty");   // nothing to read
            return stack.peek()[1];  // the minimum half of the top entry
        }
    }

    /** Approach 2: a second stack that records a value only when it is a new minimum (<= the current one). Smaller when minimums are rare. */
    static class TwoStackMinStack implements MinStack {
        private final Deque<Integer> values = new ArrayDeque<>();  // every pushed value, in stack order
        private final Deque<Integer> mins = new ArrayDeque<>();    // non-increasing from bottom to top

        public void push(int x) {
            values.push(x);  // every value goes onto the main stack
            // <= so that duplicate minimums are counted
            if (mins.isEmpty() || x <= mins.peek()) mins.push(x);
        }

        public void pop() {
            if (values.isEmpty()) throw new NoSuchElementException("stack underflow");   // nothing to remove
            int x = values.pop();              // the value being removed
            if (x == mins.peek()) mins.pop();  // the popped value was the current minimum
        }

        public int top() {
            if (values.isEmpty()) throw new NoSuchElementException("stack is empty");   // nothing to read
            return values.peek();
        }

        public int getMin() {
            if (mins.isEmpty()) throw new NoSuchElementException("stack is empty");   // nothing to read
            return mins.peek();  // the top of mins is the current minimum
        }
    }

    /** Approach 3: one stack of longs plus the current minimum. A new minimum x is stored encoded as 2x - oldMin, which is below x and so marks itself. O(1) extra space. */
    static class EncodedMinStack implements MinStack {
        // plain values, or encoded markers where the minimum dropped
        private final Deque<Long> stack = new ArrayDeque<>();
        private long min;                                      // valid whenever the stack is non-empty

        public void push(int x) {
            if (stack.isEmpty()) {         // first element: it is the minimum
                stack.push((long) x);
                min = x;
            } else if (x >= min) {         // x does not beat the minimum
                stack.push((long) x);      // ordinary value, stored as is
            } else {                       // x is a new minimum
                stack.push(2L * x - min);  // encoded: strictly less than the new minimum x
                min = x;                   // the old minimum can be recovered from the encoded entry
            }
        }

        public void pop() {
            if (stack.isEmpty()) throw new NoSuchElementException("stack underflow");   // nothing to remove
            long t = stack.pop();  // the entry being removed
            // encoded entry: undo the encoding to recover the previous minimum
            if (t < min) min = 2 * min - t;
        }

        public int top() {
            if (stack.isEmpty()) throw new NoSuchElementException("stack is empty");   // nothing to read
            long t = stack.peek();               // stored entry, possibly encoded
            return (int) (t < min ? min : t);    // an encoded entry means the real value is the current minimum
        }

        public int getMin() {
            if (stack.isEmpty()) throw new NoSuchElementException("stack is empty");   // nothing to read
            return (int) min;
        }
    }

    /** Runs a script against a min stack and records what each operation returns ("-" for push and pop). */
    static List<String> simulate(MinStack st, String[] ops, int[] args) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < ops.length; i++) {
            switch (ops[i]) {
                case "push" -> { st.push(args[i]); out.add("-"); }
                case "pop" -> { st.pop(); out.add("-"); }
                case "top" -> out.add(String.valueOf(st.top()));
                case "getMin" -> out.add(String.valueOf(st.getMin()));
                default -> throw new IllegalArgumentException("unknown op " + ops[i]);
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static List<MinStack> freshStacks() {
        return List.<MinStack>of(new PairMinStack(), new TwoStackMinStack(), new EncodedMinStack());
    }

    static void verify(String[] ops, int[] args, List<String> expected) {
        for (MinStack st : freshStacks()) {
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
        final String MAX = String.valueOf(Integer.MAX_VALUE), MIN = String.valueOf(Integer.MIN_VALUE);
        // 1. LeetCode 155 example
        verify(new String[]{"push", "push", "push", "getMin", "pop", "top", "getMin"},
               new int[]{-2, 0, -3, 0, 0, 0, 0},
               List.of("-", "-", "-", "-3", "-", "0", "-2"));
        // 2. duplicate minimums: popping one copy must not lose the minimum
        verify(new String[]{"push", "push", "push", "getMin", "pop", "getMin", "pop", "getMin", "top"},
               new int[]{2, 1, 1, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "1", "-", "1", "-", "2", "2"));
        // 3. strictly increasing pushes: the minimum never changes
        verify(new String[]{"push", "push", "push", "getMin", "top", "pop", "pop", "getMin", "top"},
               new int[]{1, 2, 3, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "1", "3", "-", "-", "1", "1"));
        // 4. strictly decreasing pushes: every push is a new minimum, every pop restores the previous one
        verify(new String[]{"push", "push", "push", "getMin", "pop", "getMin", "top", "pop", "getMin"},
               new int[]{5, 3, 1, 0, 0, 0, 0, 0, 0},
               List.of("-", "-", "-", "1", "-", "3", "3", "-", "5"));
        // 5. edge: extreme values, where 2*x - min overflows int in the encoded approach
        verify(new String[]{"push", "push", "top", "getMin", "pop", "top", "getMin", "push", "getMin", "pop", "pop", "push", "getMin"},
               new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 0, 0, 0, 0, Integer.MIN_VALUE, 0, 0, 0, Integer.MIN_VALUE, 0},
               List.of("-", "-", MIN, MIN, "-", MAX, MAX, "-", MIN, "-", "-", "-", MIN));
        // 6. edge: an empty stack throws on top, getMin and pop
        for (MinStack st : freshStacks()) {
            check(throwsOnEmpty(st::top), "top on empty must throw");
            check(throwsOnEmpty(st::getMin), "getMin on empty must throw");
            check(throwsOnEmpty(st::pop), "pop on empty must throw");
            st.push(4);
            check(st.top() == 4 && st.getMin() == 4, "usable after underflow");
        }
        // 7. deterministic random workload against a plain stack whose minimum is recomputed by scanning
        Random rnd = new Random(2024);
        int[] extremes = {Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE + 1, 0};
        for (MinStack st : freshStacks()) {
            Deque<Integer> oracle = new ArrayDeque<>();
            for (int i = 0; i < 3000; i++) {
                if (oracle.isEmpty() || rnd.nextInt(3) > 0) {
                    int v = rnd.nextInt(10) == 0 ? extremes[rnd.nextInt(extremes.length)] : rnd.nextInt(2001) - 1000;
                    st.push(v);
                    oracle.push(v);
                } else {
                    st.pop();
                    oracle.pop();
                }
                if (!oracle.isEmpty()) {
                    check(st.top() == oracle.peek(), "random top mismatch");
                    check(st.getMin() == Collections.min(oracle), "random getMin mismatch");
                }
            }
        }
        System.out.println("OK P958_ImplementMinStack");
    }
}
