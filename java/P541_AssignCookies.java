import java.util.*;

/** TUF 541 - Assign Cookies (LeetCode 455). Each child i needs a cookie of size >= greed[i]; one cookie per child. Maximise content children. */
public class P541_AssignCookies {

    /** Approach 1: exhaustive search over every assignment of cookies to children. Exponential time, O(n + m) space. */
    static int bruteForce(int[] greed, int[] cookies) {
        return tryAll(0, greed, cookies, new boolean[cookies.length]);
    }

    /** Most children among greed[i..] that can still be made content with the unused cookies. */
    static int tryAll(int i, int[] greed, int[] cookies, boolean[] used) {
        if (i == greed.length) return 0;
        int best = tryAll(i + 1, greed, cookies, used);              // child i gets nothing
        for (int j = 0; j < cookies.length; j++) {
            if (!used[j] && cookies[j] >= greed[i]) {                  // child i gets cookie j
                used[j] = true;
                best = Math.max(best, 1 + tryAll(i + 1, greed, cookies, used));
                used[j] = false;
            }
        }
        return best;
    }

    /** Approach 2: least greedy child first, smallest sufficient cookie from a TreeMap multiset. O((n + m) log m) time. */
    static int better(int[] greed, int[] cookies) {
        TreeMap<Integer, Integer> left = new TreeMap<>();              // cookie size -> how many are unused
        for (int c : cookies) left.merge(c, 1, Integer::sum);
        int[] g = greed.clone();
        Arrays.sort(g);
        int content = 0;
        for (int need : g) {
            Integer size = left.ceilingKey(need);                      // smallest unused cookie with size >= need
            if (size == null) break;                                   // nothing fits; greedier children fare no better
            if (left.merge(size, -1, Integer::sum) == 0) left.remove(size);
            content++;
        }
        return content;
    }

    /** Approach 3: sort both arrays and sweep them with two pointers. O(n log n + m log m) time. */
    static int optimal(int[] greed, int[] cookies) {
        int[] g = greed.clone(), s = cookies.clone();
        Arrays.sort(g);
        Arrays.sort(s);
        int child = 0, cookie = 0;
        while (child < g.length && cookie < s.length) {
            if (s[cookie] >= g[child]) child++;                        // this cookie satisfies the least greedy child left
            cookie++;                                                  // used or too small for everyone left: discard it
        }
        return child;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] greed, int[] cookies, int expected) {
        String in = Arrays.toString(greed) + " / " + Arrays.toString(cookies);
        check(bruteForce(greed, cookies) == expected, "bruteForce failed for " + in);
        check(better(greed, cookies) == expected, "better failed for " + in);
        check(optimal(greed, cookies) == expected, "optimal failed for " + in);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3}, new int[]{1, 1}, 1);              // both cookies are size 1
        verify(new int[]{1, 2}, new int[]{1, 2, 3}, 2);
        verify(new int[]{10, 9, 8, 7}, new int[]{5, 6, 7, 8}, 2);    // 7 -> 7 and 8 -> 8
        verify(new int[]{1, 2, 3}, new int[]{3}, 1);                 // edge: one cookie, many children
        verify(new int[]{}, new int[]{1, 2}, 0);                     // edge: no children
        verify(new int[]{1, 2}, new int[]{}, 0);                     // edge: no cookies
        verify(new int[]{2, 2, 2}, new int[]{2, 2}, 2);              // duplicates
        verify(new int[]{1, 3}, new int[]{3, 1}, 2);                 // order in the input does not matter
        verify(new int[]{Integer.MAX_VALUE}, new int[]{Integer.MAX_VALUE}, 1);   // largest values, comparisons only

        // Cross-check the greedy methods against the exhaustive search on seeded random inputs.
        Random rnd = new Random(541);
        for (int t = 0; t < 300; t++) {
            int n = rnd.nextInt(6), m = rnd.nextInt(6);
            int[] g = new int[n], s = new int[m];
            for (int i = 0; i < n; i++) g[i] = 1 + rnd.nextInt(8);
            for (int j = 0; j < m; j++) s[j] = 1 + rnd.nextInt(8);
            verify(g, s, bruteForce(g, s));
        }
        System.out.println("OK P541_AssignCookies");
    }
}
