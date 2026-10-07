import java.util.*;

/**
 * TUF 415 - Pattern 22 (The Number Pattern).
 * A (2n-1) x (2n-1) grid of concentric square rings: the outer ring is n, the next n-1, ..., the centre is 1.
 * Numbers are separated by single spaces.
 * 4 4 4 4 4 4 4
 * 4 3 3 3 3 3 4
 * 4 3 2 2 2 3 4
 * 4 3 2 1 2 3 4
 * 4 3 2 2 2 3 4
 * 4 3 3 3 3 3 4
 * 4 4 4 4 4 4 4
 * Each method returns the whole pattern as one String, one row per line, each line ending in '\n'.
 */
public class P415_Pattern22 {

    /** Approach 1: paint the rings. Fill the whole grid with n, then overwrite a smaller square with n-1, and so on. O(n^3) time. */
    static String paintLayers(int n) {
        int m = 2 * n - 1;
        if (n <= 0) return "";
        int[][] g = new int[m][m];
        for (int layer = 0; layer < n; layer++) {           // square from (layer, layer) to (m-1-layer, m-1-layer)
            int value = n - layer;
            for (int i = layer; i < m - layer; i++) {
                for (int j = layer; j < m - layer; j++) g[i][j] = value;
            }
        }
        return render(g);
    }

    /** Approach 2: a cell's ring is its distance to the nearest border; value = n - min(top, left, bottom, right). O(n^2) time. */
    static String borderDistance(int n) {
        int m = 2 * n - 1;
        if (n <= 0) return "";
        int[][] g = new int[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                int top = i, left = j, bottom = m - 1 - i, right = m - 1 - j;
                g[i][j] = n - Math.min(Math.min(top, bottom), Math.min(left, right));
            }
        }
        return render(g);
    }

    /** Approach 3: the same ring seen from the centre: value = 1 + Chebyshev distance to the middle cell. O(n^2) time. */
    static String centreDistance(int n) {
        int m = 2 * n - 1;
        if (n <= 0) return "";
        int c = n - 1;                                      // centre index
        int[][] g = new int[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                g[i][j] = 1 + Math.max(Math.abs(i - c), Math.abs(j - c));
            }
        }
        return render(g);
    }

    /** Turn a grid into text: numbers separated by single spaces, one row per line. */
    static String render(int[][] g) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : g) {
            for (int j = 0; j < row.length; j++) {
                if (j > 0) sb.append(' ');
                sb.append(row[j]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int n, String expected) {
        check(paintLayers(n).equals(expected), "paintLayers(" + n + ") gave\n" + paintLayers(n));
        check(borderDistance(n).equals(expected), "borderDistance(" + n + ") gave\n" + borderDistance(n));
        check(centreDistance(n).equals(expected), "centreDistance(" + n + ") gave\n" + centreDistance(n));
    }

    public static void main(String[] args) {
        verify(0, "");                                   // edge: no rows at all
        verify(1, "1\n");                                // edge: a single cell
        verify(2, "2 2 2\n2 1 2\n2 2 2\n");
        verify(3, "3 3 3 3 3\n3 2 2 2 3\n3 2 1 2 3\n3 2 2 2 3\n3 3 3 3 3\n");
        verify(4, "4 4 4 4 4 4 4\n4 3 3 3 3 3 4\n4 3 2 2 2 3 4\n4 3 2 1 2 3 4\n4 3 2 2 2 3 4\n4 3 3 3 3 3 4\n4 4 4 4 4 4 4\n");

        // Structural checks: 2n-1 rows, symmetric under both flips, border all n, centre 1, ring k has 8k cells.
        for (int n = 1; n <= 12; n++) {
            String s = paintLayers(n);
            check(s.equals(borderDistance(n)), "borderDistance differs for n = " + n);
            check(s.equals(centreDistance(n)), "centreDistance differs for n = " + n);
            String[] rows = s.split("\n");
            int m = 2 * n - 1;
            check(rows.length == m, "row count for n = " + n);
            int[] count = new int[n + 1];
            for (int i = 0; i < m; i++) {
                String[] tok = rows[i].split(" ");
                check(tok.length == m, "column count for n = " + n);
                check(rows[i].equals(rows[m - 1 - i]), "vertical symmetry for n = " + n);
                for (int j = 0; j < m; j++) {
                    int v = Integer.parseInt(tok[j]);
                    check(v >= 1 && v <= n, "value range for n = " + n);
                    check(tok[j].equals(tok[m - 1 - j]), "horizontal symmetry for n = " + n);
                    if (i == 0 || j == 0 || i == m - 1 || j == m - 1) check(v == n, "border must be n");
                    count[v]++;
                }
            }
            check(count[1] == 1, "exactly one centre cell");
            for (int k = 2; k <= n; k++) check(count[k] == 8 * (k - 1), "ring " + k + " must have " + 8 * (k - 1) + " cells");
        }

        System.out.print(paintLayers(4));
        System.out.println("OK P415_Pattern22");
    }
}
