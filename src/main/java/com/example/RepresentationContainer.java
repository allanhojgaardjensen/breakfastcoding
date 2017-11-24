package com.example;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A container for immutable representations of a given type.
 *
 * @param <K> the type of the key
 * @param <V> the type of representation
 */
public class RepresentationContainer<K, V> {

    private Map<K, V> representations = new ConcurrentHashMap<>();
    private int chCode = 111;

    /**
     * retrieves the value object V on the basis of the key K
     *
     * @param key the identifying key for a given representation
     * @return the representation object
     */
    public V get(K key) {
        return representations.get(key);
    }

    /**
     * adds a value object V under the key K
     *
     * @param key the identifying key for a given representation
     * @param representation the representation object
     */
    public void add(K key, V representation) {
        if (representations.get(key) == null) {
            chCode++;
        } else if (!representation.equals(representations.get(key))) {
            chCode++;
        }
        representations.put(key, representation);
    }

    public void remove(K key) {
        representations.remove(key);
        chCode++;
    }

    public Collection<V> values() {
        return Collections.unmodifiableCollection(representations.values());
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(representations.entrySet());
    }

    public void alterchCode() {
        chCode++;
    }

    public String getChCode() {
        return Integer.toHexString(chCode);
    }

    public int size() {
        return representations.size();
    }

    public boolean isEmpty() {
        return representations.isEmpty();
    }
}
