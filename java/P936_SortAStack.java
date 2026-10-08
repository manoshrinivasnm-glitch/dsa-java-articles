import java.util.*;
import java.util.function.*;

/** TUF 936 - Sort a Stack. Rearrange the stack in place so the largest element is on top. */
public class P936_SortAStack {

    /** Approach 1: pour the stack into a list, sort it, push it back smallest first. O(n log n) time, O(n) extra space. */
    static void bruteForce(Deque<Integer> st) {
        List<Integer> items = new ArrayList<>();
        while (!st.isEmpty()) items.add(st.pop());
        Collections.sort(items);
        for (int v : items) st.push(v);                    // smallest pushed first ends at the bottom
    }

    /** Approach 2: recursion. Remove the top, sort the rest, insert the top back in order. O(n^2) time, O(n) call stack. */
    static void sortRecursive(Deque<Integer> st) {
        if (st.isEmpty()) return;
        int top = st.pop();
        sortRecursive(st);                                 // the remaining stack is now sorted
        insertSorted(st, top);
    }

    /** Inserts x into a stack that is sorted with its largest element on top, keeping it sorted. */
    static void insertSorted(Deque<Integer> st, int x) {
        if (st.isEmpty() || st.peek() <= x) {
            st.push(x);
            return;
        }
        int bigger = st.pop();                             // x belongs below this element
        insertSorted(st, x);
        st.push(bigger);
    }

    /** Approach 3: iterative, with one temporary stack kept smallest-on-top. O(n^2) time, O(n) extra space. */
    static void sortWithTempStack(Deque<Integer> st) {
        Deque<Integer> tmp = new ArrayDeque<>();           // invariant: sorted, smallest element on top
        while (!st.isEmpty()) {
            int x = st.pop();
            while (!tmp.isEmpty() && tmp.peek() < x) st.push(tmp.pop());  // park smaller ones back on st
            tmp.push(x);
        }
        while (!tmp.isEmpty()) st.push(tmp.pop());         // smallest goes in first, so largest ends on top
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** Builds a stack by pushing values left to right (the last value ends on top). */
    static Deque<Integer> stackOf(int[] bottomToTop) {
        Deque<Integer> st = new ArrayDeque<>();
        for (int v : bottomToTop) st.push(v);
        return st;
    }

    /** Pops everything, returning the values from top to bottom. */
    static List<Integer> drainTopToBottom(Deque<Integer> st) {
        List<Integer> out = new ArrayList<>();
        while (!st.isEmpty()) out.add(st.pop());
        return out;
    }

    static void verify(int[] bottomToTop, List<Integer> expectedTopToBottom) {
        List<Consumer<Deque<Integer>>> methods = List.of(P936_SortAStack::bruteForce,
                P936_SortAStack::sortRecursive, P936_SortAStack::sortWithTempStack);
        String[] names = {"bruteForce", "sortRecursive", "sortWithTempStack"};
        for (int i = 0; i < methods.size(); i++) {
            Deque<Integer> st = stackOf(bottomToTop);
            methods.get(i).accept(st);
            List<Integer> got = drainTopToBottom(st);
            check(got.equals(expectedTopToBottom), names[i] + " on " + Arrays.toString(bottomToTop) + " got " + got);
        }
    }

    public static void main(String[] args) {
        verify(new int[]{11, 2, 32, 3, 41}, List.of(41, 32, 11, 3, 2));
        verify(new int[]{3, 2, 1}, List.of(3, 2, 1));                 // smallest on top becomes largest on top
        verify(new int[]{1, 2, 3}, List.of(3, 2, 1));                 // already sorted
        verify(new int[]{5, -1, 5, 0, -3}, List.of(5, 5, 0, -1, -3)); // duplicates and negatives
        verify(new int[]{}, List.of());                               // edge: empty stack
        verify(new int[]{7}, List.of(7));                             // edge: single element
        Random rnd = new Random(936);
        for (int test = 0; test < 100; test++) {
            int[] a = new int[rnd.nextInt(60)];
            for (int i = 0; i < a.length; i++) a[i] = rnd.nextInt(51) - 25;
            List<Integer> expected = new ArrayList<>();
            for (int v : a) expected.add(v);
            expected.sort(Comparator.reverseOrder());
            verify(a, expected);
        }
        System.out.println("OK P936_SortAStack");
    }
}
