package F8;

import LabA.SingleLinkedListUppg3;

import java.util.Arrays;
import java.util.LinkedList;

public class HashTableBucket<K, V> {
    private static class Entry<K, V> {
        public K key;
        public V value;

        public Entry(K k, V v) {
            key = k;
            value = v;
        }

        @Override
        public String toString() {
            return "(" + key + ":" + value + ')';
        }
    }

    private HashTableBucketSingleLinkedList<Entry<K, V>>[] table;
    private int noElements;

    @SuppressWarnings("unchecked")
    public HashTableBucket(int initialSize) {
        table = new HashTableBucketSingleLinkedList[initialSize];
    }

    public V get(K key) {
        int index = key.hashCode() % table.length;
        if (index < 0) {
            index += table.length;
        }
        if (table[index] == null) {
            return null;
        }
        for (Entry<K, V> e : table[index]) {
            if (e.key.equals(key)) {
                return e.value;
            }
        }
        return null;
    }

    public V put(K key, V value) {
        if (noElements++ > 0.75 * table.length) {
            rehash(table.length * 2);
        }

        int index = key.hashCode() % table.length;
        if (index < 0) {
            index += table.length;
        }
        if (table[index] == null) {
            table[index] = new HashTableBucketSingleLinkedList<>();
        } else {
            V oldValue;
            for (Entry<K, V> e : table[index]) {
                if (e.key.equals(key)) {
                    oldValue = e.value;
                    e.value = value;
                    return oldValue;
                }
            }
        }
        table[index].addFirst(new Entry<>(key, value));
        return null;
    }

    public V remove(K key) {
        int index = key.hashCode() % table.length;
        if (index < 0) {
            index += table.length;
        }

        if (table[index] == null) {
            return null;
        }

        V oldValue = null;
        var iterator = table[index].iterator();
        while (iterator.hasNext()) {
            var next = iterator.next();
            if (next.key.equals(key)) {
                oldValue = next.value;
                iterator.remove();
                break;
            }
        }
        if (!table[index].iterator().hasNext()) {
            table[index] = null;
        }

        return oldValue;
    }

    private void rehash(int newSize) {
        noElements = 0;
        var oldTable = table;
        table = new HashTableBucketSingleLinkedList[newSize];
        for (var bucket : oldTable) {
            if (bucket != null) {
                for (var entry : bucket) {
                    put(entry.key, entry.value);
                }
            }
        }
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        for (var list : table) {
            if (list == null) {
                builder.append("-");
            } else {
                builder.append(list);
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}