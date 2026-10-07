import java.util.*;

/** TUF 2869 - What are arrays, strings? Fixed-size arrays, 2D arrays, reference semantics,
 *  and immutable Strings with their char-level view. */
public class P2869_WhatAreArraysStrings {

    /** Create an array of n slots (all 0 by default) and fill it by index. */
    static int[] squares(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i * i;
        return a;
    }

    /** Visit every element with the enhanced for loop; use long because n ints can overflow an int sum. */
    static long sum(int[] a) {
        long total = 0;
        for (int x : a) total += x;
        return total;
    }

    /** Arrays are mutable: swapping from both ends changes the caller's array in place. */
    static void reverseInPlace(int[] a) {
        int i = 0, j = a.length - 1;
        while (i < j) {
            int t = a[i];
            a[i] = a[j];
            a[j] = t;
            i++;
            j--;
        }
    }

    /** Assignment copies the reference, not the elements; Arrays.copyOf makes an independent copy. */
    static boolean[] aliasVersusCopy(int[] a) {
        int[] alias = a;
        int[] copy = Arrays.copyOf(a, a.length);
        alias[0] = 99;
        return new boolean[]{a[0] == 99, copy[0] == 99};
    }

    /** A 2D array is an array of rows; grid[r][c] is column c of row r. */
    static int[][] identity(int n) {
        int[][] grid = new int[n][n];
        for (int i = 0; i < n; i++) grid[i][i] = 1;
        return grid;
    }

    /** Row sums of a 2D array; rows may have different lengths, so use row.length, not a fixed width. */
    static int[] rowSums(int[][] grid) {
        int[] sums = new int[grid.length];
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) sums[r] += grid[r][c];
        }
        return sums;
    }

    /** Walk a String one char at a time with charAt. */
    static int countVowels(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = Character.toLowerCase(s.charAt(i));
            if ("aeiou".indexOf(c) >= 0) count++;
        }
        return count;
    }

    /** Strings cannot be edited, so copy to a char[], reverse that, and build a new String. */
    static String reverse(String s) {
        char[] c = s.toCharArray();
        int i = 0, j = c.length - 1;
        while (i < j) {
            char t = c[i];
            c[i] = c[j];
            c[j] = t;
            i++;
            j--;
        }
        return new String(c);
    }

    /** Strings are immutable: concat returns a new String and the original is untouched. */
    static String[] concatDoesNotMutate(String s) {
        String t = s.concat("!");
        return new String[]{s, t};
    }

    /** Letters as array indices: 'c' - 'a' is 2, so a 26-slot array counts each lowercase letter. */
    static int[] letterFrequency(String s) {
        int[] freq = new int[26];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z') freq[c - 'a']++;
        }
        return freq;
    }

    /** Building a String in a loop: StringBuilder is linear, repeated + on a String is quadratic. */
    static String joinReversedWords(String sentence) {
        String[] words = sentence.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(words[i]);
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        check(Arrays.equals(squares(0), new int[0]), "n = 0 gives an empty array");
        check(Arrays.equals(squares(1), new int[]{0}), "n = 1");
        check(Arrays.equals(squares(4), new int[]{0, 1, 4, 9}), "n = 4");
        check(squares(5).length == 5 && squares(5)[4] == 16, "length and last element");

        check(sum(new int[0]) == 0, "empty sum");
        check(sum(new int[]{5}) == 5, "single");
        check(sum(new int[]{1, -2, 3}) == 2, "mixed signs");
        check(sum(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}) == 4294967294L, "needs long");

        int[] r1 = {1, 2, 3};
        reverseInPlace(r1);
        check(Arrays.equals(r1, new int[]{3, 2, 1}), "odd length reversed in place");
        int[] r2 = {1, 2, 3, 4};
        reverseInPlace(r2);
        check(Arrays.equals(r2, new int[]{4, 3, 2, 1}), "even length");
        int[] r3 = {7};
        reverseInPlace(r3);
        check(Arrays.equals(r3, new int[]{7}), "single element");
        int[] r4 = {};
        reverseInPlace(r4);
        check(r4.length == 0, "empty array survives");

        for (int[] a : new int[][]{ {1}, {1, 2}, {-5, 0, 5}, {0, 0, 0, 0} }) {
            check(Arrays.equals(aliasVersusCopy(a), new boolean[]{true, false}), "alias changes, copy does not: " + Arrays.toString(a));
            check(a[0] == 99, "the caller's array was modified through the alias");
        }

        check(identity(0).length == 0, "0 x 0 grid");
        check(Arrays.deepEquals(identity(1), new int[][]{ {1} }), "1 x 1");
        check(Arrays.deepEquals(identity(2), new int[][]{ {1, 0}, {0, 1} }), "2 x 2");
        check(Arrays.deepEquals(identity(3), new int[][]{ {1, 0, 0}, {0, 1, 0}, {0, 0, 1} }), "3 x 3");

        check(Arrays.equals(rowSums(new int[0][0]), new int[0]), "no rows");
        check(Arrays.equals(rowSums(new int[][]{ {1, 2, 3} }), new int[]{6}), "one row");
        check(Arrays.equals(rowSums(identity(3)), new int[]{1, 1, 1}), "identity rows");
        check(Arrays.equals(rowSums(new int[][]{ {1}, {2, 3}, {} }), new int[]{1, 5, 0}), "jagged rows");

        check(countVowels("") == 0, "empty string");
        check(countVowels("rhythm") == 0, "no vowels");
        check(countVowels("Education") == 5, "mixed case vowels");
        check(countVowels("AEIOUaeiou") == 10, "all vowels");

        check(reverse("").equals(""), "reverse empty");
        check(reverse("a").equals("a"), "reverse single");
        check(reverse("abc").equals("cba"), "reverse odd");
        check(reverse("ab cd").equals("dc ba"), "reverse with a space");
        check(reverse(reverse("racecar")).equals("racecar"), "reverse twice is the identity");

        for (String s : new String[]{"", "hi", "a b", "!"}) {
            String[] r = concatDoesNotMutate(s);
            check(r[0].equals(s) && r[1].equals(s + "!"), "concat left the original unchanged: " + s);
        }

        check(Arrays.equals(letterFrequency(""), new int[26]), "empty frequency");
        int[] f = letterFrequency("banana");
        check(f['b' - 'a'] == 1 && f['a' - 'a'] == 3 && f['n' - 'a'] == 2, "banana counts");
        check(letterFrequency("zzz")[25] == 3, "z lands in the last slot");
        check(Arrays.equals(letterFrequency("ABC 123"), new int[26]), "uppercase and digits are ignored");

        check(joinReversedWords("").equals(""), "empty sentence");
        check(joinReversedWords("hello").equals("hello"), "single word");
        check(joinReversedWords("hello world").equals("world hello"), "two words");
        check(joinReversedWords("  the  quick brown fox ").equals("fox brown quick the"), "extra spaces collapse");

        // equals compares content, == compares references
        String literal = "java";
        String built = new String("java");
        check(literal.equals(built), "same characters");
        check(literal != built, "different objects");

        System.out.println("OK P2869_WhatAreArraysStrings");
    }
}
