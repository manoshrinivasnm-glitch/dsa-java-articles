import java.util.*;

/** TUF 425 - Input Output. Reading tokens and lines, and formatting output, in Java.
 *  Every parser here works on a String so the tests need no stdin; a Scanner over standard input behaves identically. */
public class P425_InputOutput {

    /** Read every whitespace-separated integer in the input and add them up. */
    static long sumOfTokens(String input) {
        Scanner sc = new Scanner(input);              // reads tokens from the text; System.in works the same
        long sum = 0;                                 // long: many ints can overflow an int total
        while (sc.hasNextInt()) sum += sc.nextInt();  // keep reading until no integer is left
        return sum;
    }

    /** Classic judge format: the first token is n, then n integers follow (line breaks do not matter). */
    static int[] readArray(String input) {
        Scanner sc = new Scanner(input);
        int n = sc.nextInt();                             // first token says how many numbers follow
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = sc.nextInt();  // read n values; line breaks are just whitespace
        return a;
    }

    /** The same job without Scanner: split one line on runs of whitespace and parse each piece. */
    static int[] readArraySplit(String line) {
        String t = line.trim();              // drop outer spaces so split yields no empty first piece
        if (t.isEmpty()) return new int[0];  // blank line -> no numbers
        String[] parts = t.split("\\s+");    // split on every run of spaces or tabs
        int[] a = new int[parts.length];
        // turn each piece of text into an int
        for (int i = 0; i < parts.length; i++) a[i] = Integer.parseInt(parts[i]);
        return a;
    }

    /** Mixing nextInt and nextLine: the line break left behind by nextInt must be consumed first. */
    static String readAgeThenName(String input) {
        Scanner sc = new Scanner(input);
        int age = sc.nextInt();       // reads the number but leaves its line break behind
        sc.nextLine();                // discard the rest of the first line
        String name = sc.nextLine();  // the whole second line, spaces included
        return name + " is " + age;
    }

    /** Several test cases in one input: t, then for each case n followed by n numbers. One answer per case. */
    static long[] sumPerTestCase(String input) {
        Scanner sc = new Scanner(input);
        int t = sc.nextInt();                                 // number of test cases
        long[] answers = new long[t];                         // one answer per test case
        for (int tc = 0; tc < t; tc++) {                      // handle each test case in turn
            int n = sc.nextInt();                             // how many numbers this case has
            long sum = 0;                                     // restart the total for every case
            for (int i = 0; i < n; i++) sum += sc.nextInt();  // read this case's numbers
            answers[tc] = sum;
        }
        return answers;
    }

    /** Collect output in a StringBuilder and print it once; this is how to print 10^5 numbers quickly. */
    static String joinWithSpaces(int[] a) {
        StringBuilder sb = new StringBuilder();  // build the whole output in memory first
        for (int i = 0; i < a.length; i++) {
            if (i > 0) sb.append(' ');           // space before all but the first value
            sb.append(a[i]);
        }
        return sb.toString();                    // one string, printed with a single call
    }

    /** Fixed-width columns and fixed decimals with String.format (Locale.US keeps the decimal point a '.'). */
    static String formatRow(String name, int qty, double price) {
        // name left-aligned in 6 chars, qty right-aligned in 4, price in 8 with 2 decimals
        return String.format(Locale.US, "%-6s|%4d|%8.2f", name, qty, price);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        check(sumOfTokens("1 2 3") == 6, "sum 1 2 3");
        check(sumOfTokens("10\n-4\t 7") == 13, "sum across spaces, tabs and line breaks");
        check(sumOfTokens("") == 0, "empty input sums to 0");
        check(sumOfTokens("2000000000 2000000000") == 4_000_000_000L, "sum needs long");

        check(Arrays.equals(readArray("3\n5 6 7"), new int[]{5, 6, 7}), "readArray basic");
        check(Arrays.equals(readArray("1 42"), new int[]{42}), "readArray single");
        check(Arrays.equals(readArray("0"), new int[0]), "readArray empty");
        check(Arrays.equals(readArray("4\n-1 -2\n-3 -4"), new int[]{-1, -2, -3, -4}), "readArray negatives across lines");

        check(Arrays.equals(readArraySplit("5 6 7"), new int[]{5, 6, 7}), "split basic");
        check(Arrays.equals(readArraySplit("  8   9 "), new int[]{8, 9}), "split trims and collapses spaces");
        check(Arrays.equals(readArraySplit(""), new int[0]), "split empty");
        check(Arrays.equals(readArraySplit("-7"), new int[]{-7}), "split single negative");
        check(Arrays.equals(readArraySplit("5 6 7"), readArray("3 5 6 7")), "both parsers agree");

        check(readAgeThenName("25\nAlice Smith").equals("Alice Smith is 25"), "age then full name");
        check(readAgeThenName("7\nBob").equals("Bob is 7"), "age then short name");
        check(readAgeThenName("3\n  Al").equals("  Al is 3"), "nextLine keeps leading spaces");
        check(readAgeThenName("0\nZed Q").equals("Zed Q is 0"), "age zero");

        check(Arrays.equals(sumPerTestCase("2\n3 1 2 3\n2 10 20"), new long[]{6, 30}), "two cases");
        check(Arrays.equals(sumPerTestCase("1\n0"), new long[]{0}), "one empty case");
        check(Arrays.equals(sumPerTestCase("0"), new long[0]), "zero cases");
        check(Arrays.equals(sumPerTestCase("3\n1 5\n1 -5\n2 2000000000 2000000000"), new long[]{5, -5, 4_000_000_000L}), "mixed cases");

        check(joinWithSpaces(new int[]{1, 2, 3}).equals("1 2 3"), "join basic");
        check(joinWithSpaces(new int[]{42}).equals("42"), "join single");
        check(joinWithSpaces(new int[0]).equals(""), "join empty");
        check(joinWithSpaces(new int[]{-1, 0, 1}).equals("-1 0 1"), "join negatives");

        check(formatRow("pen", 3, 1.5).equals("pen   |   3|    1.50"), "format basic");
        check(formatRow("notebook", 12, 249.999).equals("notebook|  12|  250.00"), "format rounds and never truncates text");
        check(formatRow("", 0, 0).equals("      |   0|    0.00"), "format empty name");
        check(formatRow("x", -5, 2.0).equals("x     |  -5|    2.00"), "format negative quantity");

        System.out.println(formatRow("pen", 3, 1.5));
        System.out.println(joinWithSpaces(readArray("3\n5 6 7")));
        System.out.println("OK P425_InputOutput");
    }
}
