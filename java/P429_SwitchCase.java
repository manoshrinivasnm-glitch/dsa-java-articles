import java.util.*;

/** TUF 429 - Switch Case. Classic switch with fall-through, arrow-form switch, switch expressions with yield,
 *  switching on Strings and chars, and Java 21 pattern matching. */
public class P429_SwitchCase {

    /** Classic switch: stacked case labels share one body, and return (or break) stops the fall-through. */
    static int daysInMonth(int month, int year) {
        boolean leap = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return leap ? 29 : 28;
            default:
                return -1;
        }
    }

    /** Deliberate fall-through: without break, execution continues into the next case body. */
    static String unlockedLevels(int level) {
        StringBuilder sb = new StringBuilder();
        switch (level) {
            case 3: sb.append("hard ");
            case 2: sb.append("medium ");
            case 1: sb.append("easy");
                break;
            default: sb.append("none");
        }
        return sb.toString();
    }

    /** Arrow form: no fall-through, several labels per case, and the whole switch is an expression. */
    static String dayType(String day) {
        return switch (day) {
            case "SAT", "SUN" -> "weekend";
            case "MON", "TUE", "WED", "THU", "FRI" -> "weekday";
            default -> "unknown";
        };
    }

    /** Switch expression on a char; every arm produces exactly one value. */
    static int romanValue(char c) {
        return switch (c) {
            case 'I' -> 1;
            case 'V' -> 5;
            case 'X' -> 10;
            case 'L' -> 50;
            case 'C' -> 100;
            case 'D' -> 500;
            case 'M' -> 1000;
            default -> 0;
        };
    }

    /** A block arm needs yield to hand back its value; an arm may also throw instead of yielding. */
    static int calculate(int a, char op, int b) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) yield 0;
                yield a / b;
            }
            case '%' -> b == 0 ? 0 : a % b;
            default -> throw new IllegalArgumentException("unknown operator " + op);
        };
    }

    /** Java 21 pattern matching: switch on the runtime type, with a guard and an explicit null case. */
    static String describe(Object o) {
        return switch (o) {
            case null -> "null";
            case Integer i when i < 0 -> "negative int " + i;
            case Integer i -> "int " + i;
            case String s -> "string of length " + s.length();
            default -> "something else";
        };
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    public static void main(String[] args) {
        check(daysInMonth(1, 2023) == 31 && daysInMonth(12, 2023) == 31, "31-day months");
        check(daysInMonth(4, 2023) == 30 && daysInMonth(11, 2023) == 30, "30-day months");
        check(daysInMonth(2, 2024) == 29 && daysInMonth(2, 2000) == 29, "leap February");
        check(daysInMonth(2, 2023) == 28 && daysInMonth(2, 1900) == 28, "ordinary February");
        check(daysInMonth(13, 2023) == -1 && daysInMonth(0, 2023) == -1, "invalid month");

        check(unlockedLevels(3).equals("hard medium easy"), "level 3 falls through everything");
        check(unlockedLevels(2).equals("medium easy"), "level 2");
        check(unlockedLevels(1).equals("easy"), "level 1 stops at the break");
        check(unlockedLevels(0).equals("none") && unlockedLevels(9).equals("none"), "default");

        check(dayType("SAT").equals("weekend") && dayType("SUN").equals("weekend"), "weekend");
        check(dayType("MON").equals("weekday") && dayType("FRI").equals("weekday"), "weekday");
        check(dayType("sat").equals("unknown"), "switch on String is case-sensitive");
        check(dayType("").equals("unknown"), "empty string");

        check(romanValue('I') == 1 && romanValue('V') == 5, "I V");
        check(romanValue('X') == 10 && romanValue('L') == 50, "X L");
        check(romanValue('C') == 100 && romanValue('D') == 500 && romanValue('M') == 1000, "C D M");
        check(romanValue('i') == 0 && romanValue('?') == 0, "unknown char");

        check(calculate(6, '+', 3) == 9 && calculate(6, '-', 3) == 3, "plus and minus");
        check(calculate(6, '*', 3) == 18 && calculate(7, '/', 2) == 3, "times and integer division");
        check(calculate(6, '/', 0) == 0 && calculate(6, '%', 0) == 0, "division by zero is guarded");
        check(calculate(7, '%', 3) == 1, "modulo");
        boolean threw = false;
        try {
            calculate(2, '^', 5);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check(threw, "unknown operator throws");

        check(describe(5).equals("int 5"), "positive Integer");
        check(describe(-3).equals("negative int -3"), "guarded Integer arm");
        check(describe("hey").equals("string of length 3"), "String arm");
        check(describe(null).equals("null"), "null arm");
        check(describe(2.5).equals("something else"), "default arm");

        System.out.println("OK P429_SwitchCase");
    }
}
