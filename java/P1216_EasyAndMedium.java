import java.util.*;

/** TUF 1216 - Easy and Medium (pattern printing). Every pattern is built as a String whose lines end in '\n',
 *  so the tests can compare exact output; main prints a couple of them. */
public class P1216_EasyAndMedium {

    /** Pattern 1: an n x n block of stars. Both loops run n times. */
    static String square(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 2: right-angled triangle, row i has i stars. */
    static String rightTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 3: row i lists 1 2 ... i. */
    static String numberTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (j > 1) sb.append(' ');
                sb.append(j);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 4: row i repeats the number i exactly i times. */
    static String repeatedNumberTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                if (j > 1) sb.append(' ');
                sb.append(i);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 5: inverted triangle, row i has n - i + 1 stars. */
    static String invertedTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n - i + 1; j++) sb.append('*');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 6: inverted number triangle, row i lists 1 ... n - i + 1. */
    static String invertedNumberTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n - i + 1; j++) {
                if (j > 1) sb.append(' ');
                sb.append(j);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Pattern 7: centred pyramid, row i has n - i leading spaces and 2i - 1 stars. */
    static String pyramid(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(" ".repeat(n - i)).append("*".repeat(2 * i - 1)).append('\n');
        }
        return sb.toString();
    }

    /** Pattern 8: inverted pyramid, row i has i - 1 leading spaces and 2(n - i) + 1 stars. */
    static String invertedPyramid(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(" ".repeat(i - 1)).append("*".repeat(2 * (n - i) + 1)).append('\n');
        }
        return sb.toString();
    }

    /** Pattern 9: diamond = pyramid stacked on inverted pyramid, 2n rows in total. */
    static String diamond(int n) {
        return pyramid(n) + invertedPyramid(n);
    }

    /** Pattern 10: half diamond, star counts go 1, 2, ..., n, n - 1, ..., 1 over 2n - 1 rows. */
    static String halfDiamond(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 2 * n - 1; i++) {
            int stars = i <= n ? i : 2 * n - i;
            sb.append("*".repeat(stars)).append('\n');
        }
        return sb.toString();
    }

    /** Pattern 11: binary triangle, row i starts with 1 when i is odd and 0 when even, then alternates. */
    static String binaryTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            int bit = i % 2;
            for (int j = 1; j <= i; j++) {
                if (j > 1) sb.append(' ');
                sb.append(bit);
                bit = 1 - bit;
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
        check(square(0).isEmpty(), "square 0");
        check(square(1).equals("*\n"), "square 1");
        check(square(3).equals("***\n***\n***\n"), "square 3");
        check(lines(square(5)) == 5 && square(5).length() == 5 * 6, "square 5: five rows of five stars plus newlines");

        check(rightTriangle(0).isEmpty(), "triangle 0");
        check(rightTriangle(1).equals("*\n"), "triangle 1");
        check(rightTriangle(3).equals("*\n**\n***\n"), "triangle 3");
        check(lines(rightTriangle(5)) == 5 && rightTriangle(5).endsWith("*****\n"), "triangle 5");

        check(numberTriangle(0).isEmpty(), "numbers 0");
        check(numberTriangle(1).equals("1\n"), "numbers 1");
        check(numberTriangle(3).equals("1\n1 2\n1 2 3\n"), "numbers 3");
        check(lines(numberTriangle(5)) == 5 && numberTriangle(5).endsWith("1 2 3 4 5\n"), "numbers 5");

        check(repeatedNumberTriangle(0).isEmpty(), "repeated 0");
        check(repeatedNumberTriangle(1).equals("1\n"), "repeated 1");
        check(repeatedNumberTriangle(3).equals("1\n2 2\n3 3 3\n"), "repeated 3");
        check(lines(repeatedNumberTriangle(5)) == 5 && repeatedNumberTriangle(5).endsWith("5 5 5 5 5\n"), "repeated 5");

        check(invertedTriangle(0).isEmpty(), "inverted 0");
        check(invertedTriangle(1).equals("*\n"), "inverted 1");
        check(invertedTriangle(3).equals("***\n**\n*\n"), "inverted 3");
        check(lines(invertedTriangle(5)) == 5 && invertedTriangle(5).startsWith("*****\n"), "inverted 5");

        check(invertedNumberTriangle(0).isEmpty(), "inverted numbers 0");
        check(invertedNumberTriangle(1).equals("1\n"), "inverted numbers 1");
        check(invertedNumberTriangle(3).equals("1 2 3\n1 2\n1\n"), "inverted numbers 3");
        check(lines(invertedNumberTriangle(5)) == 5 && invertedNumberTriangle(5).startsWith("1 2 3 4 5\n"), "inverted numbers 5");

        check(pyramid(0).isEmpty(), "pyramid 0");
        check(pyramid(1).equals("*\n"), "pyramid 1");
        check(pyramid(3).equals("  *\n ***\n*****\n"), "pyramid 3");
        check(lines(pyramid(5)) == 5 && pyramid(5).startsWith("    *\n") && pyramid(5).endsWith("*********\n"), "pyramid 5");

        check(invertedPyramid(0).isEmpty(), "inverted pyramid 0");
        check(invertedPyramid(1).equals("*\n"), "inverted pyramid 1");
        check(invertedPyramid(3).equals("*****\n ***\n  *\n"), "inverted pyramid 3");
        check(lines(invertedPyramid(5)) == 5 && invertedPyramid(5).endsWith("    *\n"), "inverted pyramid 5");

        check(diamond(0).isEmpty(), "diamond 0");
        check(diamond(1).equals("*\n*\n"), "diamond 1");
        check(diamond(3).equals("  *\n ***\n*****\n*****\n ***\n  *\n"), "diamond 3");
        check(lines(diamond(5)) == 10, "diamond 5 has 2n rows");

        check(halfDiamond(0).isEmpty(), "half diamond 0");
        check(halfDiamond(1).equals("*\n"), "half diamond 1");
        check(halfDiamond(3).equals("*\n**\n***\n**\n*\n"), "half diamond 3");
        check(lines(halfDiamond(5)) == 9, "half diamond 5 has 2n - 1 rows");

        check(binaryTriangle(0).isEmpty(), "binary 0");
        check(binaryTriangle(1).equals("1\n"), "binary 1");
        check(binaryTriangle(3).equals("1\n0 1\n1 0 1\n"), "binary 3");
        check(binaryTriangle(4).equals("1\n0 1\n1 0 1\n0 1 0 1\n"), "binary 4");
        check(lines(binaryTriangle(5)) == 5, "binary 5");

        System.out.print(pyramid(4));
        System.out.print(binaryTriangle(4));
        System.out.println("OK P1216_EasyAndMedium");
    }
}
