import java.util.*;

/** TUF 901 - Reverse a Stack. Reverse the order of the elements of a stack, ideally using recursion and no auxiliary data structure. */
public class P901_ReverseAStack {

    /** Approach 1: pop everything into a list, then push it back in the same order. O(n) time, O(n) extra space. */
    static void reverseViaList(Deque<Integer> stack) {
        List<Integer> popped = new ArrayList<>();
        while (!stack.isEmpty()) popped.add(stack.pop());   // popped = old top, ..., old bottom
        for (int v : popped) stack.push(v);                  // the old top is pushed first and ends at the bottom
    }

    /** Approach 2: three pours through two helper stacks, using stack operations only. O(n) time, O(n) extra space. */
    static void reverseWithTwoStacks(Deque<Integer> stack) {
        Deque<Integer> first = new ArrayDeque<>();
        Deque<Integer> second = new ArrayDeque<>();
        while (!stack.isEmpty()) first.push(stack.pop());    // first holds the reversed order
        while (!first.isEmpty()) second.push(first.pop());   // second holds the original order again
        while (!second.isEmpty()) stack.push(second.pop());  // back into the original stack, reversed
    }

    /** Approach 3: recursion only. Pop the top, reverse the rest, then push the popped value underneath everything. O(n^2) time, O(n) stack. */
    static void reverseRecursive(Deque<Integer> stack) {
        if (stack.isEmpty()) return;                         // base case: the empty stack is its own reverse
        int top = stack.pop();
        reverseRecursive(stack);                             // the other n - 1 elements, reversed
        insertAtBottom(stack, top);                          // the old top becomes the new bottom
    }

    /** Pushes v at the bottom of the stack while leaving the order of the other elements untouched. */
    static void insertAtBottom(Deque<Integer> stack, int v) {
        if (stack.isEmpty()) {                               // base case: the bottom is right here
            stack.push(v);
            return;
        }
        int top = stack.pop();                               // lift the elements above the bottom, one frame each
        insertAtBottom(stack, v);
        stack.push(top);                                     // and put them back in the same order
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

    /** After reversing, reading the stack from top to bottom gives the original bottom-to-top order. */
    static void verify(int[] bottomToTop, List<Integer> expectedTopToBottom) {
        Deque<Integer> a = stackOf(bottomToTop);
        reverseViaList(a);
        check(drain(a).equals(expectedTopToBottom), "reverseViaList " + Arrays.toString(bottomToTop));
        Deque<Integer> b = stackOf(bottomToTop);
        reverseWithTwoStacks(b);
        check(drain(b).equals(expectedTopToBottom), "reverseWithTwoStacks " + Arrays.toString(bottomToTop));
        Deque<Integer> c = stackOf(bottomToTop);
        reverseRecursive(c);
        check(drain(c).equals(expectedTopToBottom), "reverseRecursive " + Arrays.toString(bottomToTop));
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, List.of(1, 2, 3));                      // top was 3; after reversing the top is 1
        verify(new int[]{3, 2, 1, 4, 5}, List.of(3, 2, 1, 4, 5));
        verify(new int[]{}, List.of());                                     // empty stack
        verify(new int[]{9}, List.of(9));                                   // single element
        verify(new int[]{5, 5, 5}, List.of(5, 5, 5));                       // all equal
        verify(new int[]{-1, 0, -2, 7}, List.of(-1, 0, -2, 7));             // negatives
        int[] big = new int[1000];
        for (int i = 0; i < big.length; i++) big[i] = i * 7 % 101;
        List<Integer> expected = new ArrayList<>();
        for (int v : big) expected.add(v);
        verify(big, expected);                                              // 1000 elements: about 2000 frames deep
        Deque<Integer> twice = stackOf(new int[]{4, 8, 15, 16, 23, 42});
        reverseRecursive(twice);
        reverseRecursive(twice);                                            // reversing twice restores the original
        check(drain(twice).equals(List.of(42, 23, 16, 15, 8, 4)), "reverse twice");
        System.out.println("OK P901_ReverseAStack");
    }
}
