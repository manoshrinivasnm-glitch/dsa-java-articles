import java.util.*;

/** TUF 2858 - Sort a stack using recursion. Rearrange a stack so its largest element is on top, using only push, pop, peek, isEmpty and recursion. */
public class P2858_SortAStackUsingRecursion {

    /** Approach 1: pour the stack into a list, sort, push back. O(n log n) time, O(n) extra space; ignores the stack-operations-only rule. */
    static void sortViaList(Deque<Integer> stack) {
        List<Integer> items = new ArrayList<>();
        while (!stack.isEmpty()) items.add(stack.pop());
        Collections.sort(items);                           // ascending
        for (int v : items) stack.push(v);                 // smallest pushed first, so the largest ends on top
    }

    /** Approach 2: iterative insertion sort with one helper stack. O(n^2) time, O(n) extra space. */
    static void sortWithHelperStack(Deque<Integer> stack) {
        Deque<Integer> helper = new ArrayDeque<>();        // invariant: sorted, smallest on top
        while (!stack.isEmpty()) {
            int v = stack.pop();
            while (!helper.isEmpty() && helper.peek() < v) stack.push(helper.pop());   // smaller ones wait on the input
            helper.push(v);
        }
        while (!helper.isEmpty()) stack.push(helper.pop()); // pour back: smallest first, largest on top
    }

    /** Approach 3: recursion only. Hold the top in the current frame, sort the rest, then insert the held value at its place. O(n^2) time, O(n) stack. */
    static void sortRecursive(Deque<Integer> stack) {
        if (stack.isEmpty()) return;                       // base case: nothing to sort
        int top = stack.pop();
        sortRecursive(stack);                              // the remaining n - 1 elements, sorted
        insertSorted(stack, top);
    }

    /** stack is sorted with its largest on top; insert v so that it stays sorted. */
    static void insertSorted(Deque<Integer> stack, int v) {
        if (stack.isEmpty() || stack.peek() <= v) {        // base case: v belongs on top
            stack.push(v);
            return;
        }
        int top = stack.pop();                             // top > v: lift it off
        insertSorted(stack, v);                            // place v below it
        stack.push(top);                                   // and put the larger one back
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Builds a stack by pushing the values in the given order, so the last value is on top. */
    static Deque<Integer> stackOf(int[] bottomToTop) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int v : bottomToTop) stack.push(v);
        return stack;
    }

    /** Pops everything, returning the elements from top to bottom (the stack is left empty). */
    static List<Integer> drain(Deque<Integer> stack) {
        List<Integer> out = new ArrayList<>();
        while (!stack.isEmpty()) out.add(stack.pop());
        return out;
    }

    /** expectedTopToBottom is the input sorted in decreasing order. */
    static void verify(int[] bottomToTop, List<Integer> expectedTopToBottom) {
        Deque<Integer> a = stackOf(bottomToTop);
        sortViaList(a);
        check(drain(a).equals(expectedTopToBottom), "sortViaList " + Arrays.toString(bottomToTop));
        Deque<Integer> b = stackOf(bottomToTop);
        sortWithHelperStack(b);
        check(drain(b).equals(expectedTopToBottom), "sortWithHelperStack " + Arrays.toString(bottomToTop));
        Deque<Integer> c = stackOf(bottomToTop);
        sortRecursive(c);
        check(drain(c).equals(expectedTopToBottom), "sortRecursive " + Arrays.toString(bottomToTop));
    }

    public static void main(String[] args) {
        verify(new int[]{3, 1, 2}, List.of(3, 2, 1));
        verify(new int[]{11, 2, 32, 3, 41}, List.of(41, 32, 11, 3, 2));
        verify(new int[]{}, List.of());                                 // empty stack
        verify(new int[]{7}, List.of(7));                               // single element
        verify(new int[]{2, 2, 1, 2}, List.of(2, 2, 2, 1));             // duplicates
        verify(new int[]{-5, 0, -1, 3}, List.of(3, 0, -1, -5));         // negatives
        verify(new int[]{1, 2, 3, 4}, List.of(4, 3, 2, 1));             // already sorted, largest on top
        verify(new int[]{4, 3, 2, 1}, List.of(4, 3, 2, 1));             // sorted the wrong way round
        int[] big = new int[500];
        Random rng = new Random(42);
        for (int i = 0; i < big.length; i++) big[i] = rng.nextInt(1000) - 500;
        Integer[] sorted = new Integer[big.length];
        for (int i = 0; i < big.length; i++) sorted[i] = big[i];
        Arrays.sort(sorted, Collections.reverseOrder());
        verify(big, Arrays.asList(sorted));
        System.out.println("OK P2858_SortAStackUsingRecursion");
    }
}
