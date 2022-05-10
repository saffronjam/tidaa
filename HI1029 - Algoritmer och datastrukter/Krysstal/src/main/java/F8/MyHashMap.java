package F8;

import java.util.Arrays;

public class MyHashMap<K, V> {
    private static class Entry<K, V> {
        public K key;
        public V value;

        public Entry(K k, V v) {
            key = k;
            value = v;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "key=" + key +
                    ", value=" + value +
                    '}';
        }
    }

    private Entry<K, V>[] table;
    private int noElements;

    public MyHashMap() {
        table = new Entry[10];
    }

    public V get(K key) {
        if (key == null) {
            throw new NullPointerException("Key must be non-null");
        }

        int index = getHashFromKey(key);
        while (table[index] != null && (isDummy(table[index].key) || !table[index].key.equals(key))) {
            index++;
            index %= table.length;
        }
        if (table[index] == null) {
            return null;
        }
        return table[index].value;
    }

    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("Key and Value must be non-null");
        }

        int index = getHashFromKey(key);

        // Only advance noElements (used for rehash) if we are not overwriting
        if (get(key) != null) {
            table[index] = new Entry<>(key, value);
            return value;
        }

        if (noElements++ > 0.75 * table.length) {
            rehash(table.length * 2);
        }

        while (table[index] != null && (isDummy(table[index].key) || !table[index].key.equals(key))) {
            index++;
            index %= table.length;
        }

        table[index] = new Entry<>(key, value);
        return value;
    }

    private void rehash(int newSize) {
        noElements = 0;
        var oldTable = table;
        table = new Entry[newSize];
        for (var entry : oldTable) {
            if (entry != null && !isDummy(entry.key)) {
                put(entry.key, entry.value);
            }
        }
    }

    public V remove(K key) {
        if (key == null) {
            throw new NullPointerException("Key must be non-null");
        }

        int index = getHashFromKey(key);
        if (table[index] == null) {
            return null;
        }

        while (table[index] != null && (isDummy(table[index].key) || !table[index].key.equals(key))) {
            index++;
            index %= table.length;
        }
        if (table[index] == null) {
            return null;
        }

        var oldValue = table[index].value;
        table[index].key = null;
        table[index].value = null;
        return oldValue;
    }

    private int getHashFromKey(K key) {
        int index = key.hashCode() % table.length;
        if (index < 0) {
            index += table.length;
        }
        return index;
    }

    private boolean isDummy(K key) {
        return key == null;
    }

    @Override
    public String toString() {
        return "MyHashMap{" +
                "table=" + Arrays.toString(table) +
                ", noElements=" + noElements +
                '}';
    }
}
