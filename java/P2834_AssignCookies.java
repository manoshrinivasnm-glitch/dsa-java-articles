import java.util.*;

/** TUF 2834 - Assign Cookies. A child is content with one cookie of size >= its greed; maximise content children. */
public class P2834_AssignCookies {

    /** Approach 1: least greedy child first, linear scan for the smallest unused cookie that fits. O(n log n + n*m) time, O(n + m) space. */
    static int bruteForce(int[] greed, int[] cookies) {
        int[] g = greed.clone();
        Arrays.sort(g);
        boolean[] used = new boolean[cookies.length];
        int content = 0;
        for (int need : g) {
            int pick = -1;
            for (int j = 0; j < cookies.length; j++) {
                if (!used[j] && cookies[j] >= need && (pick == -1 || cookies[j] < cookies[pick])) pick = j;
            }
            if (pick == -1) break;            // nothing fits this child, so nothing fits a greedier one
            used[pick] = true;
            content++;
        }
        return content;
    }

    /** Approach 2: sort both arrays and sweep them with two pointers. O(n log n + m log m) time, O(n + m) space for the copies. */
    static int optimal(int[] greed, int[] cookies) {
        int[] g = greed.clone(), s = cookies.clone();
        Arrays.sort(g);
        Arrays.sort(s);
        int child = 0, cookie = 0;
        while (child < g.length && cookie < s.length) {
            if (s[cookie] >= g[child]) child++;   // smallest remaining cookie satisfies the least greedy waiting child
            cookie++;                             // the cookie is either given away or too small for everyone left
        }
        return child;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] greed, int[] cookies, int expected) {
        String in = Arrays.toString(greed) + " / " + Arrays.toString(cookies);
        int b = bruteForce(greed, cookies);
        int o = optimal(greed, cookies);
        check(b == expected, "bruteForce gave " + b + ", expected " + expected + " for " + in);
        check(o == expected, "optimal gave " + o + ", expected " + expected + " for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, new int[]{1, 1}, 1);
        verify(new int[]{1, 2}, new int[]{1, 2, 3}, 2);
        verify(new int[]{10, 9, 8, 7}, new int[]{5, 6, 7, 8}, 2);       // unsorted input
        verify(new int[]{2, 2, 3}, new int[]{1, 2, 2, 2, 5}, 3);        // duplicates
        verify(new int[]{5, 5, 5}, new int[]{4, 4, 4}, 0);              // every cookie too small
        verify(new int[]{1, 2, 3}, new int[]{3}, 1);                    // one big cookie, only one child can have it
        verify(new int[]{}, new int[]{1, 2}, 0);                        // no children
        verify(new int[]{1}, new int[]{}, 0);                           // no cookies
        int[] g = {3, 1, 2};
        optimal(g, new int[]{1, 2, 3});
        check(g[0] == 3 && g[1] == 1 && g[2] == 2, "input array must not be reordered");
        System.out.println("OK P2834_AssignCookies");
    }
}
