import java.util.*;

/** TUF 3 - Expression Add Operators. Insert +, - or * between the digits of num (or nothing, to form longer operands) so the expression equals target; return every such expression. */
public class P3_ExpressionAddOperators {

    /** Approach 1: generate all 4^(n-1) ways to join consecutive digits with "", "+", "-" or "*", then evaluate each string with precedence. */
    static List<String> bruteForce(String num, int target) {
        List<String> result = new ArrayList<>();
        if (num.isEmpty()) return result;
        buildAll(num, 1, new StringBuilder().append(num.charAt(0)), target, result);
        return result;
    }

    private static void buildAll(String num, int i, StringBuilder expr, int target, List<String> result) {
        if (i == num.length()) {
            if (hasNoLeadingZero(expr) && evaluate(expr) == target) result.add(expr.toString());
            return;
        }
        char digit = num.charAt(i);
        int len = expr.length();
        for (String op : new String[]{"", "+", "-", "*"}) {
            expr.append(op).append(digit);
            buildAll(num, i + 1, expr, target, result);
            expr.setLength(len);                         // undo
        }
    }

    /** An operand longer than one digit must not start with 0. */
    private static boolean hasNoLeadingZero(CharSequence expr) {
        int i = 0, n = expr.length();
        while (i < n) {
            int j = i;
            while (j < n && Character.isDigit(expr.charAt(j))) j++;
            if (j - i > 1 && expr.charAt(i) == '0') return false;
            i = j + 1;                                   // skip the operator
        }
        return true;
    }

    /** Evaluates digits joined by +, - and * with the usual precedence: a running total of finished terms plus the current product term. */
    private static long evaluate(CharSequence expr) {
        long total = 0, term = 0, cur = 0;
        char pending = '+';
        for (int i = 0; i <= expr.length(); i++) {
            char ch = i < expr.length() ? expr.charAt(i) : '+';   // a final '+' flushes the last operand
            if (Character.isDigit(ch)) {
                cur = cur * 10 + (ch - '0');
                continue;
            }
            if (pending == '*') {
                term *= cur;
            } else {
                total += term;
                term = pending == '+' ? cur : -cur;
            }
            pending = ch;
            cur = 0;
        }
        return total + term;
    }

    /** Approach 2: backtracking that evaluates as it goes, carrying the running value and the last multiplicative term so '*' can be applied in O(1). */
    static List<String> optimal(String num, int target) {
        List<String> result = new ArrayList<>();
        if (num.isEmpty()) return result;
        char[] expr = new char[2 * num.length()];       // at most n digits and n - 1 operators
        search(num, 0, expr, 0, 0L, 0L, target, result);
        return result;
    }

    private static void search(String num, int pos, char[] expr, int len, long value, long last, int target, List<String> result) {
        if (pos == num.length()) {
            if (value == target) result.add(new String(expr, 0, len));
            return;
        }
        long operand = 0;
        for (int end = pos; end < num.length(); end++) {
            if (end > pos && num.charAt(pos) == '0') break;        // "0" is fine, "05" is not
            operand = operand * 10 + (num.charAt(end) - '0');
            int digits = end - pos + 1;
            if (pos == 0) {                                        // first operand: no operator in front of it
                num.getChars(pos, end + 1, expr, len);
                search(num, end + 1, expr, len + digits, operand, operand, target, result);
            } else {
                num.getChars(pos, end + 1, expr, len + 1);         // digits go after the operator slot
                expr[len] = '+';
                search(num, end + 1, expr, len + 1 + digits, value + operand, operand, target, result);
                expr[len] = '-';
                search(num, end + 1, expr, len + 1 + digits, value - operand, -operand, target, result);
                expr[len] = '*';
                search(num, end + 1, expr, len + 1 + digits, value - last + last * operand, last * operand, target, result);
            }
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String num, int target, List<String> expected) {
        List<String> a = new ArrayList<>(bruteForce(num, target));
        List<String> b = new ArrayList<>(optimal(num, target));
        List<String> e = new ArrayList<>(expected);
        Collections.sort(a);
        Collections.sort(b);
        Collections.sort(e);
        check(a.equals(e), "bruteForce gave " + a + " for " + num + " -> " + target);
        check(b.equals(e), "optimal gave " + b + " for " + num + " -> " + target);
    }

    public static void main(String[] args) {
        verify("123", 6, List.of("1+2+3", "1*2*3"));
        verify("232", 8, List.of("2*3+2", "2+3*2"));
        verify("105", 5, List.of("1*0+5", "10-5"));                 // "1*05" is rejected: leading zero
        verify("00", 0, List.of("0+0", "0-0", "0*0"));              // "00" itself is not a valid operand
        verify("1", 1, List.of("1"));                               // single digit, no operator
        verify("1", 2, List.of());
        verify("1234", 10, List.of("1*2*3+4", "1+2+3+4"));
        verify("999", 81, List.of());                               // 9*9 would need a third 9 to vanish
        verify("1010", 10, List.of("1*0+10", "10*1+0", "10*1-0", "10+1*0", "10-1*0"));
        verify("3456237490", 9191, List.of());                      // 10 digits: 4^9 expressions for the brute force
        verify("2147483648", -2147483648, List.of());               // the whole string overflows int but not long
        check(evaluate("2*3+4") == 10 && evaluate("1-2*3") == -5 && evaluate("105") == 105, "evaluate is wrong");
        System.out.println("OK P3_ExpressionAddOperators");
    }
}
