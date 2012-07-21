package com.aristsoft.swing.jpivot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DualMap<K, V> {

    private List<K> keys1 = new ArrayList<K>();
    private List<K> keys2 = new ArrayList<K>();
    private HashMap<K, HashMap<K, V>> data = new HashMap<K, HashMap<K, V>>();

    public DualMap() {
    }

    public V get(K key1, K key2) {
        HashMap<K, V> k2 = data.get(key1);
        if (k2 == null)
            return null;
        return k2.get(key2);
    }

    public V get(int key1Index, int key2Index) {
        K key1 = keys1.get(key1Index);
        if (key1 == null)
            return null;
        HashMap<K, V> k2 = data.get(key1);
        if (k2 == null)
            return null;
        K key2 = keys2.get(key2Index);
        if (key2 == null)
            return null;
        return k2.get(key2);
    }

    public void put(K key1, K key2, V value) {
        doPut(key1, key2, value);
    }

    void doPut(K key1, K key2, V value) {
        HashMap<K, V> k2 = data.get(key1);
        if (k2 == null) {
            if (!keys1.contains(key1))
                keys1.add(key1);
            k2 = new HashMap<K, V>();
            data.put(key1, k2);
        }

        if (!keys2.contains(key2))
            keys2.add(key2);
        k2.put(key2, value);
    }

    public void clear() {
        keys1.clear();
        keys2.clear();
        data.clear();
    }

    public List<K> getKeys1() {
        return keys1;
    }

    public List<K> getKeys2() {
        return keys2;
    }

    public int size1() {
        return keys1.size();
    }

    public int size2() {
        return keys2.size();
    }

    public boolean isEmpty1() {
        return keys1.isEmpty();
    }

    public boolean isEmpty2() {
        return keys2.isEmpty();
    }

    public boolean isEmpty() {
        return isEmpty1() && isEmpty2();
    }

    public List<V> values() {
        List<V> result = new ArrayList<V>();
        for (HashMap<K, V> m : data.values()) {
            result.addAll(m.values());
        }
        return result;
    }
}
