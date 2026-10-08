import java.util.*;

/** TUF 957 - Celebrity Problem. m[i][j] == 1 means i knows j; find the person everyone knows who knows nobody, or -1. */
public class P957_CelebrityProblem {

    /** Approach 1: count, for every person, how many people they know and how many know them. O(n^2) time, O(n) space. */
    static int bruteForce(int[][] m) {
        int n = m.length;
        int[] knowMe = new int[n], iKnow = new int[n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (i != j && m[i][j] == 1) { iKnow[i]++; knowMe[j]++; }
        for (int i = 0; i < n; i++)
            if (knowMe[i] == n - 1 && iKnow[i] == 0) return i;
        return -1;
    }

    /** Approach 2: stack elimination; one question rules out one of two people. O(n) time, O(n) space. */
    static int better(int[][] m) {
        int n = m.length;
        if (n == 0) return -1;
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) st.push(i);
        while (st.size() > 1) {
            int a = st.pop(), b = st.pop();
            if (m[a][b] == 1) st.push(b);             // a knows b, so a is not the celebrity
            else st.push(a);                          // a does not know b, so b is not the celebrity
        }
        int c = st.pop();
        return isCelebrity(m, c) ? c : -1;
    }

    static boolean isCelebrity(int[][] m, int c) {
        for (int j = 0; j < m.length; j++) {
            if (j == c) continue;
            if (m[c][j] == 1 || m[j][c] == 0) return false;
        }
        return true;
    }

    /** Approach 3: two pointers from both ends do the same elimination without a stack. O(n) time, O(1) space. */
    static int optimal(int[][] m) {
        int n = m.length;
        if (n == 0) return -1;
        int top = 0, bottom = n - 1;
        while (top < bottom) {
            if (m[top][bottom] == 1) top++;           // top knows someone: not the celebrity
            else bottom--;                            // nobody unknown to top can be the celebrity
        }
        return isCelebrity(m, top) ? top : -1;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[][] m, int expected) {
        int b = bruteForce(m), s = better(m), o = optimal(m);
        String in = Arrays.deepToString(m);
        check(b == expected, "bruteForce " + in + " -> " + b);
        check(s == expected, "better " + in + " -> " + s);
        check(o == expected, "optimal " + in + " -> " + o);
    }

    public static void main(String[] args) {
        verify(new int[][]{{0, 1, 0}, {0, 0, 0}, {0, 1, 0}}, 1);
        verify(new int[][]{{0, 1}, {1, 0}}, -1);                       // they know each other
        verify(new int[][]{{0, 1, 1, 0}, {0, 0, 0, 0}, {0, 1, 0, 0}, {1, 1, 0, 0}}, 1);
        verify(new int[][]{{0, 0, 0}, {1, 0, 0}, {1, 1, 0}}, 0);       // celebrity at index 0
        verify(new int[][]{{0, 0}, {0, 0}}, -1);                       // nobody is known by anyone
        verify(new int[][]{{1, 1, 0}, {0, 1, 0}, {0, 1, 1}}, 1);       // diagonal set to 1 is ignored
        verify(new int[][]{{0, 1, 0}, {0, 0, 0}, {0, 0, 0}}, -1);      // 1 is not known by 2
        verify(new int[][]{{0}}, 0);                                   // a single person is trivially the celebrity
        verify(new int[0][0], -1);                                     // nobody at all
        System.out.println("OK P957_CelebrityProblem");
    }
}
