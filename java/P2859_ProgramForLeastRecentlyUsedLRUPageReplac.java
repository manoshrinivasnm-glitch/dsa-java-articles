import java.util.*;

/** TUF 2859 - LRU Page Replacement. Count page faults for a reference string with `capacity` frames (capacity >= 1). */
public class P2859_ProgramForLeastRecentlyUsedLRUPageReplac {

    /** Approach 1: keep the frames in a list ordered from least to most recently used. O(n * C) time, O(C) space. */
    static int bruteForce(int[] pages, int capacity) {
        List<Integer> frames = new ArrayList<>();                 // index 0 = least recently used
        int faults = 0;
        for (int page : pages) {
            int pos = frames.indexOf(page);
            if (pos >= 0) {
                frames.remove(pos);                               // hit: re-added at the most recent end below
            } else {
                faults++;
                if (frames.size() == capacity) frames.remove(0);  // memory full: evict the least recently used page
            }
            frames.add(page);
        }
        return faults;
    }

    /** Doubly linked list node holding one resident page. */
    static class Node {
        int page;
        Node prev, next;

        Node(int page) {
            this.page = page;
        }
    }

    /** Approach 2: hash map from page to its node in a doubly linked list (head side = most recent). O(n) time, O(C) space. */
    static int optimal(int[] pages, int capacity) {
        Map<Integer, Node> where = new HashMap<>();
        Node head = new Node(-1), tail = new Node(-1);            // sentinels: head.next is most recent, tail.prev least recent
        head.next = tail;
        tail.prev = head;
        int faults = 0;
        for (int page : pages) {
            Node node = where.get(page);
            if (node != null) {
                unlink(node);                                     // hit: re-inserted at the front below
            } else {
                faults++;
                if (where.size() == capacity) {                   // memory full: evict the node just before tail
                    Node lru = tail.prev;
                    unlink(lru);
                    where.remove(lru.page);
                }
                node = new Node(page);
                where.put(page, node);
            }
            node.next = head.next;                                // insert right after head
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }
        return faults;
    }

    static void unlink(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /** Approach 3: let a LinkedHashMap in access order do the recency bookkeeping. O(n) time, O(C) space. */
    static int optimalLinkedHashMap(int[] pages, int capacity) {
        LinkedHashMap<Integer, Boolean> frames = new LinkedHashMap<>(16, 0.75f, true);   // true = iterate in access order
        int faults = 0;
        for (int page : pages) {
            if (frames.containsKey(page)) {
                frames.get(page);                                 // get() counts as an access and moves the page to the end
            } else {
                faults++;
                if (frames.size() == capacity) {
                    int lru = frames.keySet().iterator().next();  // first key in access order = least recently used
                    frames.remove(lru);
                }
                frames.put(page, Boolean.TRUE);
            }
        }
        return faults;
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    static void verify(int[] pages, int capacity, int expected) {
        String in = Arrays.toString(pages) + " C=" + capacity;
        check(bruteForce(pages, capacity) == expected, "bruteForce " + in + " -> " + bruteForce(pages, capacity));
        check(optimal(pages, capacity) == expected, "optimal " + in + " -> " + optimal(pages, capacity));
        check(optimalLinkedHashMap(pages, capacity) == expected,
              "optimalLinkedHashMap " + in + " -> " + optimalLinkedHashMap(pages, capacity));
    }

    public static void main(String[] args) {
        verify(new int[]{5, 0, 1, 3, 2, 4, 1, 0, 5}, 4, 8);
        verify(new int[]{7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2}, 4, 6);
        verify(new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5}, 3, 10);
        verify(new int[]{1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5}, 4, 8);      // more frames never hurt LRU
        verify(new int[]{1, 1, 2, 2, 1}, 1, 3);                            // one frame: fault on every change of page
        verify(new int[]{1, 2, 1, 3, 2}, 10, 3);                           // never full: faults = distinct pages
        verify(new int[]{}, 3, 0);                                         // empty reference string
        System.out.println("OK P2859_ProgramForLeastRecentlyUsedLRUPageReplac");
    }
}
