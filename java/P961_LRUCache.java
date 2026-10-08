import java.util.*;

/** TUF 961 - LRU Cache. get and put in O(1); when full, evict the least recently used key. */
public class P961_LRUCache {

    /** Common contract so every implementation can be driven by the same test script. */
    interface Cache {
        int get(int key);
        void put(int key, int value);
    }

    /** Approach 1: a list ordered from least to most recently used; every operation scans it. O(capacity) per operation. */
    static class BruteLRU implements Cache {
        private final int capacity;
        private final List<int[]> entries = new ArrayList<>();   // {key, value}, least recently used first

        BruteLRU(int capacity) { this.capacity = capacity; }

        public int get(int key) {
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i)[0] == key) {
                    int[] e = entries.remove(i);
                    entries.add(e);                              // now the most recently used
                    return e[1];
                }
            }
            return -1;
        }

        public void put(int key, int value) {
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i)[0] == key) {
                    entries.remove(i);
                    break;
                }
            }
            if (entries.size() == capacity) entries.remove(0);  // evict the least recently used
            entries.add(new int[]{key, value});
        }
    }

    /** Approach 2: HashMap from key to node plus a doubly linked list in recency order. O(1) per operation. */
    static class LRUCache implements Cache {
        private static class Node {
            int key, value;
            Node prev, next;
            Node(int key, int value) { this.key = key; this.value = value; }
        }

        private final int capacity;
        private final Map<Integer, Node> map = new HashMap<>();
        private final Node head = new Node(0, 0), tail = new Node(0, 0);   // sentinels; head.next is the most recent

        LRUCache(int capacity) {
            this.capacity = capacity;
            head.next = tail;
            tail.prev = head;
        }

        public int get(int key) {
            Node node = map.get(key);
            if (node == null) return -1;
            unlink(node);
            insertFront(node);
            return node.value;
        }

        public void put(int key, int value) {
            Node node = map.get(key);
            if (node != null) {
                node.value = value;
                unlink(node);
                insertFront(node);
                return;
            }
            if (map.size() == capacity) {
                Node lru = tail.prev;
                unlink(lru);
                map.remove(lru.key);
            }
            node = new Node(key, value);
            map.put(key, node);
            insertFront(node);
        }

        private void unlink(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void insertFront(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }
    }

    /** Approach 3: library shortcut, LinkedHashMap in access order with removeEldestEntry. O(1) per operation. */
    static class LinkedHashMapLRU implements Cache {
        private final LinkedHashMap<Integer, Integer> map;

        LinkedHashMapLRU(int capacity) {
            map = new LinkedHashMap<>(16, 0.75f, true) {          // true = order by access, not insertion
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                    return size() > capacity;
                }
            };
        }

        public int get(int key) {
            return map.getOrDefault(key, -1);
        }

        public void put(int key, int value) {
            map.put(key, value);
        }
    }

    // ---------------------------------------------------------------- tests
    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    /** ops: {key} means get(key), {key, value} means put(key, value). Returns the results of the gets in order. */
    static int[] run(Cache cache, int[][] ops) {
        List<Integer> out = new ArrayList<>();
        for (int[] op : ops) {
            if (op.length == 1) out.add(cache.get(op[0]));
            else cache.put(op[0], op[1]);
        }
        int[] res = new int[out.size()];
        for (int i = 0; i < res.length; i++) res[i] = out.get(i);
        return res;
    }

    static void verify(int capacity, int[][] ops, int[] expected) {
        Cache[] caches = {new BruteLRU(capacity), new LRUCache(capacity), new LinkedHashMapLRU(capacity)};
        for (Cache c : caches) {
            int[] got = run(c, ops);
            check(Arrays.equals(got, expected), c.getClass().getSimpleName() + " cap=" + capacity + " -> " + Arrays.toString(got));
        }
    }

    public static void main(String[] args) {
        // LeetCode example: put(1,1) put(2,2) get(1) put(3,3) get(2) put(4,4) get(1) get(3) get(4)
        verify(2, new int[][]{{1, 1}, {2, 2}, {1}, {3, 3}, {2}, {4, 4}, {1}, {3}, {4}}, new int[]{1, -1, -1, 3, 4});
        verify(1, new int[][]{{2, 1}, {2}, {3, 2}, {2}, {3}}, new int[]{1, -1, 2});           // capacity 1
        verify(2, new int[][]{{2, 1}, {2, 2}, {2}, {1, 1}, {4, 1}, {2}}, new int[]{2, -1});   // update keeps one copy
        verify(2, new int[][]{{2, 1}, {1, 1}, {2, 3}, {4, 1}, {1}, {2}}, new int[]{-1, 3});   // put on existing key refreshes recency
        verify(2, new int[][]{{1, 1}, {2, 2}, {1}, {3, 3}, {2}, {3}, {1}}, new int[]{1, -1, 3, 1}); // get refreshes recency
        verify(3, new int[][]{{5}}, new int[]{-1});                                          // get on an empty cache
        System.out.println("OK P961_LRUCache");
    }
}
