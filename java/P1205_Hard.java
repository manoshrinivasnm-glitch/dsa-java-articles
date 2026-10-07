import java.util.*;

/** TUF 1205 - Hard (pattern printing). Patterns that need several segments per row, a counter that runs across
 *  rows, letters treated as numbers, mirrored rows, or a hollow interior. Lines end in '\n' so tests can compare
 *  exact output. */
public class P1205_Hard {

    /** Pattern 12: number crown. Row i is 1..i, then 2(n - i) spaces, then i..1. */
    static String numberCrown(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) sb.append(j);
            sb.append(" ".repeat(2 * (n - i)));
            for (int j = i; j >= 1; j--) sb.append(j);
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 13: one counter keeps running across rows: 1 / 2 3 / 4 5 6 ... */
    static String increasingNumberTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        int next = 1;
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (j > 1) sb.append(' ');
                sb.append(next++);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 14: letters instead of numbers; (char) ('A' + k) is the k-th letter. Row i shows A up to letter i. */
    static String increasingLetterTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) sb.append((char) ('A' + j));
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 15: the same triangle upside down. Row i shows A up to letter n - i + 1. */
    static String reverseLetterTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < n - i + 1; j++) sb.append((char) ('A' + j));
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 16: alpha ramp. Row i repeats the i-th letter i times. */
    static String alphaRamp(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            char letter = (char) ('A' + i - 1);
            for (int j = 1; j <= i; j++) sb.append(letter);
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 17: alpha hill. Row i is centred: n - i spaces, then A up to letter i and back down to A. */
    static String alphaHill(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(" ".repeat(n - i));
            for (int j = 0; j < 2 * i - 1; j++) {
                int k = j < i ? j : 2 * i - 2 - j;
                sb.append((char) ('A' + k));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 18: alpha triangle. Row i shows the last i letters of A..letter n, in alphabetical order. */
    static String alphaTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = n - i; j < n; j++) sb.append((char) ('A' + j));
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 19: symmetric void. Top half: n - i + 1 stars, 2(i - 1) spaces, n - i + 1 stars; the bottom mirrors it. */
    static String symmetricVoid(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            int stars = n - i + 1;
            sb.append("*".repeat(stars)).append(" ".repeat(2 * (i - 1))).append("*".repeat(stars)).append('\n');
        }
        for (int i = 1; i <= n; i++) {
            sb.append("*".repeat(i)).append(" ".repeat(2 * (n - i))).append("*".repeat(i)).append('\n');
        }
        return sb.toString();
    }

    /** Pattern 20: symmetric butterfly. Row i (rising to n, then falling) has i stars, 2(n - i) spaces, i stars. */
    static String butterfly(int n) {
        StringBuilder sb = new StringBuilder();
        for (int row = 1; row <= 2 * n - 1; row++) {
            int i = row <= n ? row : 2 * n - row;
            sb.append("*".repeat(i)).append(" ".repeat(2 * (n - i))).append("*".repeat(i)).append('\n');
        }
        return sb.toString();
    }

    /** Pattern 21: hollow rectangle. Border rows are full; inner rows have a star at each end only. */
    static String hollowRectangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            if (i == 1 || i == n) {
                sb.append("*".repeat(n));
            } else {
                sb.append('*').append(" ".repeat(n - 2)).append('*');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 22: concentric squares. In a (2n - 1) grid, cell (r, c) holds n minus its distance to the nearest edge. */
    static String concentricSquares(int n) {
        StringBuilder sb = new StringBuilder();
        int size = 2 * n - 1;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int edge = Math.min(Math.min(r, c), Math.min(size - 1 - r, size - 1 - c));
                if (c > 0) sb.append(' ');
                sb.append(n - edge);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static int lines(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '\n') count++;
        return count;
    }

    public static void main(String[] args) {
        check(numberCrown(0).isEmpty(), "crown 0");
        check(numberCrown(1).equals("11\n"), "crown 1");
        check(numberCrown(3).equals("1    1\n12  21\n123321\n"), "crown 3");
        check(lines(numberCrown(5)) == 5 && numberCrown(5).endsWith("1234554321\n"), "crown 5");

        check(increasingNumberTriangle(0).isEmpty(), "increasing 0");
        check(increasingNumberTriangle(1).equals("1\n"), "increasing 1");
        check(increasingNumberTriangle(3).equals("1\n2 3\n4 5 6\n"), "increasing 3");
        check(lines(increasingNumberTriangle(5)) == 5 && increasingNumberTriangle(5).endsWith("11 12 13 14 15\n"), "increasing 5 ends at n(n + 1) / 2");

        check(increasingLetterTriangle(0).isEmpty(), "letters 0");
        check(increasingLetterTriangle(1).equals("A\n"), "letters 1");
        check(increasingLetterTriangle(3).equals("A\nAB\nABC\n"), "letters 3");
        check(lines(increasingLetterTriangle(5)) == 5 && increasingLetterTriangle(5).endsWith("ABCDE\n"), "letters 5");

        check(reverseLetterTriangle(0).isEmpty(), "reverse letters 0");
        check(reverseLetterTriangle(1).equals("A\n"), "reverse letters 1");
        check(reverseLetterTriangle(3).equals("ABC\nAB\nA\n"), "reverse letters 3");
        check(lines(reverseLetterTriangle(5)) == 5 && reverseLetterTriangle(5).startsWith("ABCDE\n"), "reverse letters 5");

        check(alphaRamp(0).isEmpty(), "ramp 0");
        check(alphaRamp(1).equals("A\n"), "ramp 1");
        check(alphaRamp(3).equals("A\nBB\nCCC\n"), "ramp 3");
        check(lines(alphaRamp(5)) == 5 && alphaRamp(5).endsWith("EEEEE\n"), "ramp 5");

        check(alphaHill(0).isEmpty(), "hill 0");
        check(alphaHill(1).equals("A\n"), "hill 1");
        check(alphaHill(3).equals("  A\n ABA\nABCBA\n"), "hill 3");
        check(lines(alphaHill(5)) == 5 && alphaHill(5).endsWith("ABCDEDCBA\n"), "hill 5");

        check(alphaTriangle(0).isEmpty(), "alpha triangle 0");
        check(alphaTriangle(1).equals("A\n"), "alpha triangle 1");
        check(alphaTriangle(3).equals("C\nBC\nABC\n"), "alpha triangle 3");
        check(lines(alphaTriangle(5)) == 5 && alphaTriangle(5).startsWith("E\nDE\n"), "alpha triangle 5");

        check(symmetricVoid(0).isEmpty(), "void 0");
        check(symmetricVoid(1).equals("**\n**\n"), "void 1");
        check(symmetricVoid(2).equals("****\n*  *\n*  *\n****\n"), "void 2");
        check(symmetricVoid(3).equals("******\n**  **\n*    *\n*    *\n**  **\n******\n"), "void 3");
        check(lines(symmetricVoid(5)) == 10, "void 5 has 2n rows");

        check(butterfly(0).isEmpty(), "butterfly 0");
        check(butterfly(1).equals("**\n"), "butterfly 1");
        check(butterfly(2).equals("*  *\n****\n*  *\n"), "butterfly 2");
        check(butterfly(3).equals("*    *\n**  **\n******\n**  **\n*    *\n"), "butterfly 3");
        check(lines(butterfly(5)) == 9, "butterfly 5 has 2n - 1 rows");

        check(hollowRectangle(0).isEmpty(), "hollow 0");
        check(hollowRectangle(1).equals("*\n"), "hollow 1");
        check(hollowRectangle(2).equals("**\n**\n"), "hollow 2");
        check(hollowRectangle(4).equals("****\n*  *\n*  *\n****\n"), "hollow 4");
        check(lines(hollowRectangle(6)) == 6, "hollow 6");

        check(concentricSquares(0).isEmpty(), "concentric 0");
        check(concentricSquares(1).equals("1\n"), "concentric 1");
        check(concentricSquares(2).equals("2 2 2\n2 1 2\n2 2 2\n"), "concentric 2");
        check(concentricSquares(3).equals("3 3 3 3 3\n3 2 2 2 3\n3 2 1 2 3\n3 2 2 2 3\n3 3 3 3 3\n"), "concentric 3");
        check(lines(concentricSquares(5)) == 9, "concentric 5 has 2n - 1 rows");

        System.out.print(butterfly(3));
        System.out.print(concentricSquares(3));
        System.out.println("OK P1205_Hard");
    }
}
