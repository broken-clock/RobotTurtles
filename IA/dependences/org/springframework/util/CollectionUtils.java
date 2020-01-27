// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.util.Set;
import java.util.LinkedList;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Collection;

public abstract class CollectionUtils
{
    public static boolean isEmpty(final Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    public static List arrayToList(final Object source) {
        return Arrays.asList(ObjectUtils.toObjectArray(source));
    }
    
    public static <E> void mergeArrayIntoCollection(final Object array, final Collection<E> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        final Object[] objectArray;
        final Object[] arr = objectArray = ObjectUtils.toObjectArray(array);
        for (final Object elem : objectArray) {
            collection.add((E)elem);
        }
    }
    
    public static <K, V> void mergePropertiesIntoMap(final Properties props, final Map<K, V> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        if (props != null) {
            final Enumeration<?> en = props.propertyNames();
            while (en.hasMoreElements()) {
                final String key = (String)en.nextElement();
                Object value = props.getProperty(key);
                if (value == null) {
                    value = props.get(key);
                }
                map.put((K)key, (V)value);
            }
        }
    }
    
    public static boolean contains(final Iterator<?> iterator, final Object element) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                final Object candidate = iterator.next();
                if (ObjectUtils.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean contains(final Enumeration<?> enumeration, final Object element) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                final Object candidate = enumeration.nextElement();
                if (ObjectUtils.nullSafeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean containsInstance(final Collection<?> collection, final Object element) {
        if (collection != null) {
            for (final Object candidate : collection) {
                if (candidate == element) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean containsAny(final Collection<?> source, final Collection<?> candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return false;
        }
        for (final Object candidate : candidates) {
            if (source.contains(candidate)) {
                return true;
            }
        }
        return false;
    }
    
    public static <E> E findFirstMatch(final Collection<?> source, final Collection<E> candidates) {
        if (isEmpty(source) || isEmpty(candidates)) {
            return null;
        }
        for (final Object candidate : candidates) {
            if (source.contains(candidate)) {
                return (E)candidate;
            }
        }
        return null;
    }
    
    public static <T> T findValueOfType(final Collection<?> collection, final Class<T> type) {
        if (isEmpty(collection)) {
            return null;
        }
        T value = null;
        for (final Object element : collection) {
            if (type == null || type.isInstance(element)) {
                if (value != null) {
                    return null;
                }
                value = (T)element;
            }
        }
        return value;
    }
    
    public static Object findValueOfType(final Collection<?> collection, final Class<?>[] types) {
        if (isEmpty(collection) || ObjectUtils.isEmpty(types)) {
            return null;
        }
        for (final Class<?> type : types) {
            final Object value = findValueOfType(collection, type);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
    
    public static boolean hasUniqueObject(final Collection<?> collection) {
        if (isEmpty(collection)) {
            return false;
        }
        boolean hasCandidate = false;
        Object candidate = null;
        for (final Object elem : collection) {
            if (!hasCandidate) {
                hasCandidate = true;
                candidate = elem;
            }
            else {
                if (candidate != elem) {
                    return false;
                }
                continue;
            }
        }
        return true;
    }
    
    public static Class<?> findCommonElementType(final Collection<?> collection) {
        if (isEmpty(collection)) {
            return null;
        }
        Class<?> candidate = null;
        for (final Object val : collection) {
            if (val != null) {
                if (candidate == null) {
                    candidate = val.getClass();
                }
                else {
                    if (candidate != val.getClass()) {
                        return null;
                    }
                    continue;
                }
            }
        }
        return candidate;
    }
    
    public static <A, E extends A> A[] toArray(final Enumeration<E> enumeration, final A[] array) {
        final ArrayList<A> elements = new ArrayList<A>();
        while (enumeration.hasMoreElements()) {
            elements.add(enumeration.nextElement());
        }
        return elements.toArray(array);
    }
    
    public static <E> Iterator<E> toIterator(final Enumeration<E> enumeration) {
        return new EnumerationIterator<E>(enumeration);
    }
    
    public static <K, V> MultiValueMap<K, V> toMultiValueMap(final Map<K, List<V>> map) {
        return new MultiValueMapAdapter<K, V>(map);
    }
    
    public static <K, V> MultiValueMap<K, V> unmodifiableMultiValueMap(final MultiValueMap<? extends K, ? extends V> map) {
        Assert.notNull(map, "'map' must not be null");
        final Map<K, List<V>> result = new LinkedHashMap<K, List<V>>(map.size());
        for (final Map.Entry<? extends K, ? extends List<? extends V>> entry : map.entrySet()) {
            final List<V> values = Collections.unmodifiableList((List<? extends V>)entry.getValue());
            result.put((K)entry.getKey(), values);
        }
        final Map<K, List<V>> unmodifiableMap = Collections.unmodifiableMap((Map<? extends K, ? extends List<V>>)result);
        return toMultiValueMap(unmodifiableMap);
    }
    
    private static class EnumerationIterator<E> implements Iterator<E>
    {
        private Enumeration<E> enumeration;
        
        public EnumerationIterator(final Enumeration<E> enumeration) {
            this.enumeration = enumeration;
        }
        
        @Override
        public boolean hasNext() {
            return this.enumeration.hasMoreElements();
        }
        
        @Override
        public E next() {
            return this.enumeration.nextElement();
        }
        
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Not supported");
        }
    }
    
    private static class MultiValueMapAdapter<K, V> implements MultiValueMap<K, V>, Serializable
    {
        private final Map<K, List<V>> map;
        
        public MultiValueMapAdapter(final Map<K, List<V>> map) {
            Assert.notNull(map, "'map' must not be null");
            this.map = map;
        }
        
        @Override
        public void add(final K key, final V value) {
            List<V> values = this.map.get(key);
            if (values == null) {
                values = new LinkedList<V>();
                this.map.put(key, values);
            }
            values.add(value);
        }
        
        @Override
        public V getFirst(final K key) {
            final List<V> values = this.map.get(key);
            return (values != null) ? values.get(0) : null;
        }
        
        @Override
        public void set(final K key, final V value) {
            final List<V> values = new LinkedList<V>();
            values.add(value);
            this.map.put(key, values);
        }
        
        @Override
        public void setAll(final Map<K, V> values) {
            for (final Map.Entry<K, V> entry : values.entrySet()) {
                this.set(entry.getKey(), entry.getValue());
            }
        }
        
        @Override
        public Map<K, V> toSingleValueMap() {
            final LinkedHashMap<K, V> singleValueMap = new LinkedHashMap<K, V>(this.map.size());
            for (final Map.Entry<K, List<V>> entry : this.map.entrySet()) {
                singleValueMap.put(entry.getKey(), entry.getValue().get(0));
            }
            return singleValueMap;
        }
        
        @Override
        public int size() {
            return this.map.size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.map.containsKey(key);
        }
        
        @Override
        public boolean containsValue(final Object value) {
            return this.map.containsValue(value);
        }
        
        @Override
        public List<V> get(final Object key) {
            return this.map.get(key);
        }
        
        @Override
        public List<V> put(final K key, final List<V> value) {
            return this.map.put(key, value);
        }
        
        @Override
        public List<V> remove(final Object key) {
            return this.map.remove(key);
        }
        
        @Override
        public void putAll(final Map<? extends K, ? extends List<V>> m) {
            this.map.putAll(m);
        }
        
        @Override
        public void clear() {
            this.map.clear();
        }
        
        @Override
        public Set<K> keySet() {
            return this.map.keySet();
        }
        
        @Override
        public Collection<List<V>> values() {
            return this.map.values();
        }
        
        @Override
        public Set<Map.Entry<K, List<V>>> entrySet() {
            return this.map.entrySet();
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || this.map.equals(other);
        }
        
        @Override
        public int hashCode() {
            return this.map.hashCode();
        }
        
        @Override
        public String toString() {
            return this.map.toString();
        }
    }
}
