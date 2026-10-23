import java.util.*;

/** TUF 2866 - Functions (Pass by Reference and Value). Java passes every argument by value: primitives are
 *  copied, and so are references, which is why a callee can mutate an object but never rebind the caller's variable. */
public class P2866_FunctionsPassByReferenceAndValue {

    /** A tiny mutable object used to show the difference between mutating and reassigning a parameter. */
    static class Counter {
        int count;
    }

    /** The parameter is a copy of the caller's int; changing it changes nothing outside. */
    static void tryToChangeInt(int x) {
        x = 100;  // changes only the local copy; the caller's int is untouched
    }

    /** The clean way to "change" a primitive: return the new value and let the caller store it. */
    static int increment(int x) {
        return x + 1;  // the caller stores the result itself
    }

    /** The reference is copied, but it points at the caller's array, so element writes are visible. */
    static void setFirst(int[] arr, int value) {
        if (arr.length > 0) arr[0] = value;  // writes into the caller's own array
    }

    /** Reassigning the parameter only rebinds the local copy of the reference; the caller keeps the old array. */
    static void tryToReplaceArray(int[] arr) {
        arr = new int[]{42, 42, 42};  // rebinds only the local copy of the reference
    }

    /** Swapping two ints through parameters cannot work in Java: only the copies are swapped. */
    static void trySwap(int a, int b) {
        int t = a;  // only the local copies get swapped
        a = b;
        b = t;
    }

    /** Swapping inside an array works because caller and callee share the same array object. */
    static void swapInArray(int[] a, int i, int j) {
        int t = a[i];  // both sides share this array, so the swap sticks
        a[i] = a[j];
        a[j] = t;
    }

    /** StringBuilder is mutable, so appending through the copied reference changes the caller's object. */
    static void appendWorld(StringBuilder sb) {
        sb.append(" world");  // changes the one object the caller also holds
    }

    /** String is immutable: + creates a new String and only the local parameter points at it. */
    static void tryToChangeString(String s) {
        s = s + " world";  // builds a new String; only the local s points at it
    }

    /** Mutating a field through the reference is visible to the caller. */
    static void bump(Counter c) {
        c.count++;  // changes the caller's object through the shared reference
    }

    /** Pointing the parameter at a new object is not visible to the caller. */
    static void tryToReplaceCounter(Counter c) {
        c = new Counter();  // c now points at a new object, not the caller's
        c.count = 999;      // changes only that new object
    }

    /** Returning more than one value: wrap them in an array (or a small record or class). */
    static int[] minMax(int[] a) {
        int min = a[0], max = a[0];  // start both from the first element
        for (int x : a) {
            if (x < min) min = x;    // a new smallest
            if (x > max) max = x;    // a new largest
        }
        return new int[]{min, max};  // two answers packed into one array
    }

    /** Overloading: same name, different parameter lists; the compiler picks by argument types. */
    static int area(int side) {
        return side * side;  // chosen for a call like area(5)
    }

    static int area(int width, int height) {
        return width * height;  // chosen for a call like area(3, 4)
    }

    /** Varargs: any number of ints arrive as an int[] inside the method. */
    static long sumAll(int... nums) {
        long sum = 0;
        for (int x : nums) sum += x;  // nums is a plain int[] inside the method
        return sum;
    }

    /** Recursion: a function calling itself on a smaller input, with a base case that stops it. */
    static int digitSum(int n) {
        if (n < 10) return n;              // base case: a single digit is its own sum
        return n % 10 + digitSum(n / 10);  // last digit plus the digit sum of the rest
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        for (int v : new int[]{0, 5, -7, Integer.MAX_VALUE}) {
            int a = v;
            tryToChangeInt(a);
            check(a == v, "int parameter is a copy: " + v);
        }
        check(increment(0) == 1 && increment(-1) == 0 && increment(41) == 42, "increment returns the new value");
        check(increment(Integer.MAX_VALUE) == Integer.MIN_VALUE, "increment wraps at the top");

        int[][] arrays = { {1}, {1, 2}, {0, 0, 0}, {-1, -2, -3, -4} };
        for (int[] arr : arrays) {
            int len = arr.length;
            setFirst(arr, 7);
            check(arr[0] == 7, "element write is visible to the caller");
            tryToReplaceArray(arr);
            check(arr[0] == 7 && arr.length == len, "the caller's array was not replaced");
        }
        setFirst(new int[0], 7);                 // empty array: nothing to set, nothing to crash

        int x = 1, y = 2;
        trySwap(x, y);
        check(x == 1 && y == 2, "swap through int parameters does nothing");
        int[] pair = {1, 2};
        swapInArray(pair, 0, 1);
        check(pair[0] == 2 && pair[1] == 1, "swap inside an array works");
        int[] trio = {5, 6, 7};
        swapInArray(trio, 0, 2);
        check(Arrays.equals(trio, new int[]{7, 6, 5}), "swap the ends");
        swapInArray(trio, 1, 1);
        check(Arrays.equals(trio, new int[]{7, 6, 5}), "swap with itself is a no-op");
        int[] neg = {-1, 1};
        swapInArray(neg, 1, 0);
        check(Arrays.equals(neg, new int[]{1, -1}), "indices in either order");

        for (String start : new String[]{"hello", "", "a", "hello world"}) {
            StringBuilder sb = new StringBuilder(start);
            appendWorld(sb);
            check(sb.toString().equals(start + " world"), "StringBuilder was mutated: " + start);
            String s = start;
            tryToChangeString(s);
            check(s.equals(start), "String stayed the same: " + start);
        }

        for (int startCount : new int[]{0, 1, -1, 1000}) {
            Counter c = new Counter();
            c.count = startCount;
            bump(c);
            check(c.count == startCount + 1, "field mutation is visible");
            tryToReplaceCounter(c);
            check(c.count == startCount + 1, "reassignment inside the callee is invisible");
        }

        check(Arrays.equals(minMax(new int[]{3}), new int[]{3, 3}), "single element");
        check(Arrays.equals(minMax(new int[]{3, 1, 2}), new int[]{1, 3}), "basic");
        check(Arrays.equals(minMax(new int[]{-5, -9, -1}), new int[]{-9, -1}), "negatives");
        check(Arrays.equals(minMax(new int[]{4, 4, 4}), new int[]{4, 4}), "all equal");

        check(area(3) == 9 && area(0) == 0, "square area");
        check(area(3, 4) == 12 && area(0, 9) == 0, "rectangle area");
        check(area(1) == 1 && area(1, 1) == 1, "unit shapes");
        check(area(-2) == 4, "a negative side still squares");

        check(sumAll() == 0, "no arguments");
        check(sumAll(5) == 5, "one argument");
        check(sumAll(1, 2, 3) == 6, "three arguments");
        check(sumAll(Integer.MAX_VALUE, Integer.MAX_VALUE) == 4294967294L, "long accumulator");
        check(sumAll(new int[]{4, 5}) == 9, "an int[] can be passed directly");

        check(digitSum(0) == 0, "digit sum of 0");
        check(digitSum(7) == 7, "single digit");
        check(digitSum(1234) == 10, "1 + 2 + 3 + 4");
        check(digitSum(999999999) == 81, "largest 9-digit number");

        System.out.println("OK P2866_FunctionsPassByReferenceAndValue");
    }
}
