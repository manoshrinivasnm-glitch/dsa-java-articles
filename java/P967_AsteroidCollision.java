import java.util.*;

/** TUF 967 - Asteroid Collision. Positive moves right, negative moves left; return the asteroids left after all collisions. */
public class P967_AsteroidCollision {

    /** Approach 1: repeatedly find the first adjacent (right-mover, left-mover) pair and resolve it. O(n^2) time, O(n) space. */
    static int[] bruteForce(int[] asteroids) {
        List<Integer> list = new ArrayList<>();               // asteroids still flying, in order
        for (int a : asteroids) list.add(a);
        boolean changed = true;
        while (changed) {                                     // repeat until a full pass finds no collision
            changed = false;
            for (int i = 0; i + 1 < list.size(); i++) {       // look for the first adjacent colliding pair
                int left = list.get(i), right = list.get(i + 1);
                if (left > 0 && right < 0) {                  // the only pattern that can collide
                    if (left > -right) list.remove(i + 1);    // the left-mover explodes
                    else if (left < -right) list.remove(i);   // the right-mover explodes
                    else { list.remove(i + 1); list.remove(i); }   // equal sizes: both explode
                    changed = true;
                    break;                                    // the list changed, so rescan from the start
                }
            }
        }
        int[] res = new int[list.size()];                     // copy the survivors into an array
        for (int i = 0; i < res.length; i++) res[i] = list.get(i);
        return res;
    }

    /** Approach 2: stack of survivors; a left-mover fights the right-movers on top of the stack. O(n) time, O(n) space. */
    static int[] optimal(int[] asteroids) {
        Deque<Integer> st = new ArrayDeque<>();               // survivors so far; the top is the rightmost
        for (int a : asteroids) {
            boolean alive = true;                             // false once a explodes
            while (alive && a < 0 && !st.isEmpty() && st.peek() > 0) {   // a moves left into a right-mover
                int top = st.peek();
                if (top < -a) st.pop();                       // top explodes, a keeps moving left
                else if (top == -a) { st.pop(); alive = false; }   // both explode
                else alive = false;                           // a explodes
            }
            if (alive) st.push(a);                            // a survived every fight it met
        }
        int[] res = new int[st.size()];
        // the top is the rightmost survivor, so fill the array from the back
        for (int i = res.length - 1; i >= 0; i--) res[i] = st.pop();
        return res;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] asteroids, int[] expected) {
        int[] copy = asteroids.clone();
        int[] b = bruteForce(asteroids);
        int[] o = optimal(asteroids);
        check(Arrays.equals(b, expected), "bruteForce " + Arrays.toString(asteroids) + " -> " + Arrays.toString(b));
        check(Arrays.equals(o, expected), "optimal " + Arrays.toString(asteroids) + " -> " + Arrays.toString(o));
        check(Arrays.equals(copy, asteroids), "input was modified");
    }

    public static void main(String[] args) {
        verify(new int[]{5, 10, -5}, new int[]{5, 10});
        verify(new int[]{8, -8}, new int[]{});                         // equal sizes: both explode
        verify(new int[]{10, 2, -5}, new int[]{10});                    // -5 destroys 2, then loses to 10
        verify(new int[]{-2, -1, 1, 2}, new int[]{-2, -1, 1, 2});       // moving apart, nothing collides
        verify(new int[]{1, -2, -2, -2}, new int[]{-2, -2, -2});
        verify(new int[]{3, 5, -6, 2, -1, 4}, new int[]{-6, 2, 4});
        verify(new int[]{}, new int[]{});                               // empty input
        verify(new int[]{-5}, new int[]{-5});                           // single asteroid
        System.out.println("OK P967_AsteroidCollision");
    }
}
