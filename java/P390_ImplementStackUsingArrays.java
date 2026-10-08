import java.util.*;

/** TUF 390 - Implement Stack using Arrays. push, pop, top, isEmpty and size backed by a plain int[]. */
public class P390_ImplementStackUsingArrays {

    /** Common contract so every implementation can be driven by the same test script. */
    interface IntStack {
        void push(int x);
        int pop();
        int top();
        boolean isEmpty();
        int size();
    }

    /** Approach 1: fixed-capacity array. Every operation is O(1); push on a full stack throws. */
    static class FixedArrayStack implements IntStack {
        private final int[] data;
        private int topIndex = -1;               // index of the current top, -1 when empty

        FixedArrayStack(int capacity) {
            data = new int[capacity];
        }

        public void push(int x) {
            if (topIndex == data.length - 1) throw new IllegalStateException("stack overflow");
            data[++topIndex] = x;
        }

        public int pop() {
            if (topIndex == -1) throw new NoSuchElementException("stack underflow");
            return data[topIndex--];
        }

        public int top() {
            if (topIndex == -1) throw new NoSuchElementException("stack is empty");
            return data[topIndex];
        }

        public boolean isEmpty() {
            return topIndex == -1;
        }

        public int size() {
            return topIndex + 1;
        }
    }

    /** Approach 2: growable array that doubles when full and halves when a quarter full. push is amortised O(1). */
    static class DynamicArrayStack implements IntStack {
        private int[] data = new int[2];
        private int topIndex = -1;

        public void push(int x) {
            if (topIndex == data.length - 1) data = Arrays.copyOf(data, data.length * 2);
            data[++topIndex] = x;
        }

        public int pop() {
            if (topIndex == -1) throw new NoSuchElementException("stack underflow");
            int x = data[topIndex--];
            if (data.length > 2 && topIndex + 1 <= data.length / 4) data = Arrays.copyOf(data, data.length / 2);
            return x;
        }

        public int top() {
            if (topIndex == -1) throw new NoSuchElementException("stack is empty");
            return data[topIndex];
        }

        public boolean isEmpty() {
            return topIndex == -1;
        }

        public int size() {
            return topIndex + 1;
        }

        int capacity() {
            return data.length;
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

    static List<IntStack> freshStacks(int capacity) {
        return List.<IntStack>of(new FixedArrayStack(capacity), new DynamicArrayStack());
    }

    static void verify(String[] ops, int[] args, List<String> expected) {
        for (IntStack st : freshStacks(ops.length)) {
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
        // 2. interleaved pushes and pops, negatives and duplicates
        verify(new String[]{"push", "pop", "push", "push", "pop", "push", "top", "size", "pop", "pop"},
               new int[]{-5, 0, 7, 7, 0, -5, 0, 0, 0, 0},
               List.of("-", "-5", "-", "-", "7", "-", "-5", "2", "-5", "7"));
        // 3. edge: a fresh stack
        verify(new String[]{"isEmpty", "size"}, new int[]{0, 0}, List.of("true", "0"));
        // 4. edge: pop and top on an empty stack throw, and the stack stays usable afterwards
        for (IntStack st : freshStacks(1)) {
            check(throwsOnEmpty(st::pop), "pop on empty must throw");
            check(throwsOnEmpty(st::top), "top on empty must throw");
            st.push(9);
            check(st.top() == 9 && st.size() == 1, "stack usable after underflow");
        }
        // 5. a fixed stack refuses a push beyond capacity; the dynamic one grows and later shrinks
        FixedArrayStack fixed = new FixedArrayStack(2);
        fixed.push(1);
        fixed.push(2);
        boolean overflow = false;
        try {
            fixed.push(3);
        } catch (IllegalStateException e) {
            overflow = true;
        }
        check(overflow && fixed.size() == 2 && fixed.top() == 2, "fixed stack must reject a third push");
        DynamicArrayStack dyn = new DynamicArrayStack();
        for (int i = 0; i < 1000; i++) dyn.push(i);
        check(dyn.size() == 1000 && dyn.capacity() == 1024, "capacity after 1000 pushes should be 1024");
        for (int i = 999; i >= 100; i--) check(dyn.pop() == i, "dynamic pop order");
        check(dyn.size() == 100 && dyn.capacity() == 256, "capacity after shrinking to 100 elements should be 256");
        // 6. deterministic random workload against java.util.ArrayDeque
        Random rnd = new Random(42);
        for (IntStack st : freshStacks(5000)) {
            Deque<Integer> oracle = new ArrayDeque<>();
            for (int i = 0; i < 5000; i++) {
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
        System.out.println("OK P390_ImplementStackUsingArrays");
    }
}
