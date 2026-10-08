import java.util.*;

/** TUF 1008 - Task Scheduler. Minimum number of time slots to run all tasks when equal tasks need n slots between them. */
public class P1008_TaskScheduler {

    /** Approach 1: simulate slot by slot, scanning all 26 letters each slot. O(answer * 26) time, O(1) space. */
    static int bruteForce(char[] tasks, int n) {
        int[] count = new int[26];
        for (char t : tasks) count[t - 'A']++;
        int[] nextFree = new int[26];            // earliest slot in which each letter may run again
        int remaining = tasks.length, time = 0;
        while (remaining > 0) {
            int pick = -1;
            for (int c = 0; c < 26; c++) {
                if (count[c] > 0 && nextFree[c] <= time && (pick == -1 || count[c] > count[pick])) pick = c;
            }
            if (pick != -1) {                    // otherwise this slot is idle
                count[pick]--;
                nextFree[pick] = time + n + 1;
                remaining--;
            }
            time++;
        }
        return time;
    }

    /** Approach 2: max-heap of runnable counts plus a FIFO queue of cooling tasks. O(T log 26) time, O(26) space. */
    static int better(char[] tasks, int n) {
        int[] count = new int[26];
        for (char t : tasks) count[t - 'A']++;
        PriorityQueue<Integer> ready = new PriorityQueue<>(Collections.reverseOrder());   // remaining counts
        for (int c : count) if (c > 0) ready.add(c);
        Deque<int[]> cooling = new ArrayDeque<>();   // {remainingCount, slotWhenReadyAgain}, ordered by slot
        int time = 0;
        while (!ready.isEmpty() || !cooling.isEmpty()) {
            if (ready.isEmpty()) time = Math.max(time, cooling.peekFirst()[1]);   // skip the idle stretch
            while (!cooling.isEmpty() && cooling.peekFirst()[1] <= time) ready.add(cooling.pollFirst()[0]);
            int left = ready.poll() - 1;
            if (left > 0) cooling.addLast(new int[]{left, time + n + 1});
            time++;
        }
        return time;
    }

    /** Approach 3: count frames built around the most frequent task. O(T) time, O(1) space. */
    static int optimal(char[] tasks, int n) {
        if (tasks.length == 0) return 0;
        int[] count = new int[26];
        for (char t : tasks) count[t - 'A']++;
        int maxFreq = 0;
        for (int c : count) maxFreq = Math.max(maxFreq, c);
        int maxCount = 0;                        // how many letters reach maxFreq
        for (int c : count) if (c == maxFreq) maxCount++;
        return Math.max(tasks.length, (maxFreq - 1) * (n + 1) + maxCount);
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(String tasks, int n, int expected) {
        char[] t = tasks.toCharArray();
        check(bruteForce(t, n) == expected, "bruteForce " + tasks + " n=" + n + " got " + bruteForce(t, n));
        check(better(t, n) == expected, "better " + tasks + " n=" + n + " got " + better(t, n));
        check(optimal(t, n) == expected, "optimal " + tasks + " n=" + n + " got " + optimal(t, n));
    }

    public static void main(String[] args) {
        verify("AAABBB", 2, 8);                  // A B _ A B _ A B
        verify("ACABDB", 1, 6);                  // no idle slot needed
        verify("AAABBB", 3, 10);                 // A B _ _ A B _ _ A B
        verify("AAAAAABCDEFG", 2, 16);           // A dominates: 5 full frames of 3 plus the last A
        verify("ABCDEABCDE", 4, 10);             // five letters tie for the maximum
        verify("AAABBBCCDDEE", 2, 12);           // plenty of fillers: the answer is just the task count
        verify("AAABBB", 0, 6);                  // n = 0: no cooldown at all
        verify("A", 5, 1);                       // edge: a single task never waits
        verify("", 3, 0);                        // edge: nothing to run
        // deterministic random cross-check of the three approaches
        Random rnd = new Random(1008);
        for (int iter = 0; iter < 300; iter++) {
            int len = 1 + rnd.nextInt(30), letters = 1 + rnd.nextInt(6), n = rnd.nextInt(6);
            char[] t = new char[len];
            for (int i = 0; i < len; i++) t[i] = (char) ('A' + rnd.nextInt(letters));
            int want = optimal(t, n);
            check(bruteForce(t, n) == want && better(t, n) == want, "random mismatch on " + new String(t) + " n=" + n);
        }
        System.out.println("OK P1008_TaskScheduler");
    }
}
