import java.util.*;

/** TUF 2867 - For loops. Counting loops, enhanced for, two loop variables, nested loops, break, continue
 *  and labeled break. */
public class P2867_ForLoops {

    /** The counting loop: initialise once, test before every pass, update after every pass. */
    static long sumTo(int n) {
        long sum = 0;
        for (int i = 1; i <= n; i++) sum += i;
        return sum;
    }

    /** Count down with a decrementing step and build the output in a StringBuilder. */
    static String countdown(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = n; i >= 1; i--) {
            sb.append(i);
            if (i > 1) sb.append(' ');
        }
        return sb.toString();
    }

    /** Enhanced for: visit each value when the index itself is not needed. */
    static int maxElement(int[] a) {
        int best = Integer.MIN_VALUE;
        for (int x : a) if (x > best) best = x;
        return best;
    }

    /** Two loop variables moving toward each other, declared and updated together in the header. */
    static boolean isPalindrome(int[] a) {
        for (int i = 0, j = a.length - 1; i < j; i++, j--) {
            if (a[i] != a[j]) return false;
        }
        return true;
    }

    /** break: stop at the first match instead of scanning the rest. */
    static int firstNegativeIndex(int[] a) {
        int found = -1;
        for (int i = 0; i < a.length; i++) {
            if (a[i] < 0) {
                found = i;
                break;
            }
        }
        return found;
    }

    /** continue: skip the rest of this pass and move on to the next value. */
    static long sumOfOdds(int[] a) {
        long sum = 0;
        for (int x : a) {
            if (x % 2 == 0) continue;
            sum += x;
        }
        return sum;
    }

    /** Nested loops over pairs i < j; the inner loop starts at i + 1 so each pair is seen exactly once. */
    static int countPairsWithSum(int[] a, int target) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[i] + a[j] == target) count++;
            }
        }
        return count;
    }

    /** Labeled break leaves both loops at once; a plain break would only leave the inner one. */
    static int[] findInGrid(int[][] grid, int target) {
        int[] pos = {-1, -1};
        outer:
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (grid[r][c] == target) {
                    pos = new int[]{r, c};
                    break outer;
                }
            }
        }
        return pos;
    }

    /** Multiplication table as text: the outer loop picks the row, the inner loop fills it. */
    static String multiplicationTable(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (j > 1) sb.append(' ');
                sb.append(i * j);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        check(sumTo(0) == 0, "no iterations");
        check(sumTo(1) == 1, "one iteration");
        check(sumTo(10) == 55, "1..10");
        check(sumTo(100000) == 5000050000L, "needs long");
        check(sumTo(-5) == 0, "negative n runs zero times");

        check(countdown(0).equals(""), "countdown empty");
        check(countdown(1).equals("1"), "countdown one");
        check(countdown(3).equals("3 2 1"), "countdown three");
        check(countdown(10).startsWith("10 9 8") && countdown(10).endsWith("2 1"), "countdown ten");

        check(maxElement(new int[]{4}) == 4, "single");
        check(maxElement(new int[]{1, 9, 3}) == 9, "middle max");
        check(maxElement(new int[]{-7, -2, -9}) == -2, "all negative");
        check(maxElement(new int[]{5, 5, 5}) == 5, "ties");
        check(maxElement(new int[0]) == Integer.MIN_VALUE, "empty returns the sentinel");

        check(isPalindrome(new int[0]), "empty is a palindrome");
        check(isPalindrome(new int[]{1}), "single");
        check(isPalindrome(new int[]{1, 2, 1}) && isPalindrome(new int[]{1, 2, 2, 1}), "odd and even palindromes");
        check(!isPalindrome(new int[]{1, 2, 3}), "not a palindrome");

        check(firstNegativeIndex(new int[]{3, -1, -5}) == 1, "first negative at 1");
        check(firstNegativeIndex(new int[]{-9}) == 0, "first element");
        check(firstNegativeIndex(new int[]{1, 2, 3}) == -1, "none");
        check(firstNegativeIndex(new int[0]) == -1, "empty");

        check(sumOfOdds(new int[0]) == 0, "odds of empty");
        check(sumOfOdds(new int[]{2, 4, 6}) == 0, "no odds");
        check(sumOfOdds(new int[]{1, 2, 3, 4, 5}) == 9, "1 + 3 + 5");
        check(sumOfOdds(new int[]{-3, -2, 7}) == 4, "negative odd is counted");

        check(countPairsWithSum(new int[]{1, 2, 3, 4}, 5) == 2, "(1,4) and (2,3)");
        check(countPairsWithSum(new int[]{3, 3, 3}, 6) == 3, "three equal pairs");
        check(countPairsWithSum(new int[]{1, 2}, 10) == 0, "no pairs");
        check(countPairsWithSum(new int[]{5}, 10) == 0, "single element never pairs with itself");

        int[][] grid = { {1, 2, 3}, {4, 5, 6}, {7, 8, 9} };
        check(Arrays.equals(findInGrid(grid, 1), new int[]{0, 0}), "top left");
        check(Arrays.equals(findInGrid(grid, 6), new int[]{1, 2}), "middle row");
        check(Arrays.equals(findInGrid(grid, 9), new int[]{2, 2}), "bottom right");
        check(Arrays.equals(findInGrid(grid, 10), new int[]{-1, -1}), "absent");
        check(Arrays.equals(findInGrid(new int[0][0], 1), new int[]{-1, -1}), "empty grid");

        check(multiplicationTable(0).equals(""), "table of 0");
        check(multiplicationTable(1).equals("1\n"), "table of 1");
        check(multiplicationTable(2).equals("1 2\n2 4\n"), "table of 2");
        check(multiplicationTable(3).equals("1 2 3\n2 4 6\n3 6 9\n"), "table of 3");

        System.out.println("OK P2867_ForLoops");
    }
}
