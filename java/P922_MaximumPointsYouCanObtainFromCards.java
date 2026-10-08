import java.util.*;

/** TUF 922 - Maximum Points You Can Obtain from Cards. Take exactly k cards from the two ends of the row; maximise their sum. */
public class P922_MaximumPointsYouCanObtainFromCards {

    /** Approach 1: try every split (left cards, k - left right cards) and sum each from scratch. O(k^2) time, O(1) space. */
    static int bruteForce(int[] cardPoints, int k) {
        int n = cardPoints.length, best = Integer.MIN_VALUE;
        for (int left = 0; left <= k; left++) {
            int sum = 0;
            for (int i = 0; i < left; i++) sum += cardPoints[i];
            for (int i = 0; i < k - left; i++) sum += cardPoints[n - 1 - i];
            best = Math.max(best, sum);
        }
        return best;
    }

    /** Approach 2: start with the first k cards, then trade the innermost left card for the next right card, k times. O(k) time, O(1) space. */
    static int optimal(int[] cardPoints, int k) {
        int n = cardPoints.length, leftSum = 0, rightSum = 0;
        for (int i = 0; i < k; i++) leftSum += cardPoints[i];
        int best = leftSum;
        for (int i = k - 1; i >= 0; i--) {
            leftSum -= cardPoints[i];                  // give back the innermost left card
            rightSum += cardPoints[n - k + i];         // take one more card from the right end
            best = Math.max(best, leftSum + rightSum);
        }
        return best;
    }

    /** Approach 3: the cards left behind form one window of n - k cards; minimise its sum. O(n) time, O(1) space. */
    static int minWindowComplement(int[] cardPoints, int k) {
        int n = cardPoints.length, w = n - k, total = 0;
        for (int x : cardPoints) total += x;
        int windowSum = 0;
        for (int i = 0; i < w; i++) windowSum += cardPoints[i];
        int minWindow = windowSum;
        for (int i = w; i < n; i++) {
            windowSum += cardPoints[i] - cardPoints[i - w];
            minWindow = Math.min(minWindow, windowSum);
        }
        return total - minWindow;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] cardPoints, int k, int expected) {
        String tag = Arrays.toString(cardPoints) + " k=" + k + " expected " + expected;
        check(bruteForce(cardPoints, k) == expected, "bruteForce " + tag);
        check(optimal(cardPoints, k) == expected, "optimal " + tag);
        check(minWindowComplement(cardPoints, k) == expected, "minWindowComplement " + tag);
    }

    public static void main(String[] args) {
        verify(new int[]{1, 2, 3, 4, 5, 6, 1}, 3, 12);
        verify(new int[]{2, 2, 2}, 2, 4);
        verify(new int[]{9, 7, 7, 9, 7, 7, 9}, 7, 55);        // k = n: take everything
        verify(new int[]{1, 1000, 1}, 1, 1);                  // the big card in the middle is unreachable
        verify(new int[]{1, 79, 80, 1, 1, 1, 200, 1}, 3, 202); // mixed split: 1 from the left, 2 from the right
        verify(new int[]{5}, 1, 5);                           // single card
        verify(new int[]{4, 3}, 0, 0);                        // k = 0: take nothing
        System.out.println("OK P922_MaximumPointsYouCanObtainFromCards");
    }
}
