// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.core;

import java.util.HashMap;
import java.util.HashSet;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.NavigableMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.LinkedHashSet;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Set;

public abstract class CollectionFactory
{
    private static final Set<Class<?>> approximableCollectionTypes;
    private static final Set<Class<?>> approximableMapTypes;
    
    public static boolean isApproximableCollectionType(final Class<?> collectionType) {
        return collectionType != null && CollectionFactory.approximableCollectionTypes.contains(collectionType);
    }
    
    public static <E> Collection<E> createApproximateCollection(final Object collection, final int initialCapacity) {
        if (collection instanceof LinkedList) {
            return new LinkedList<E>();
        }
        if (collection instanceof List) {
            return new ArrayList<E>(initialCapacity);
        }
        if (collection instanceof SortedSet) {
            return new TreeSet<E>(((SortedSet)collection).comparator());
        }
        return new LinkedHashSet<E>(initialCapacity);
    }
    
    public static <E> Collection<E> createCollection(final Class<?> collectionType, final int initialCapacity) {
        if (collectionType.isInterface()) {
            if (List.class.equals(collectionType)) {
                return new ArrayList<E>(initialCapacity);
            }
            if (SortedSet.class.equals(collectionType) || NavigableSet.class.equals(collectionType)) {
                return new TreeSet<E>();
            }
            if (Set.class.equals(collectionType) || Collection.class.equals(collectionType)) {
                return new LinkedHashSet<E>(initialCapacity);
            }
            throw new IllegalArgumentException("Unsupported Collection interface: " + collectionType.getName());
        }
        else {
            if (!Collection.class.isAssignableFrom(collectionType)) {
                throw new IllegalArgumentException("Unsupported Collection type: " + collectionType.getName());
            }
            try {
                return (Collection<E>)collectionType.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Could not instantiate Collection type: " + collectionType.getName(), ex);
            }
        }
    }
    
    public static boolean isApproximableMapType(final Class<?> mapType) {
        return mapType != null && CollectionFactory.approximableMapTypes.contains(mapType);
    }
    
    public static <K, V> Map<K, V> createApproximateMap(final Object map, final int initialCapacity) {
        if (map instanceof SortedMap) {
            return new TreeMap<K, V>(((SortedMap)map).comparator());
        }
        return new LinkedHashMap<K, V>(initialCapacity);
    }
    
    public static <K, V> Map<K, V> createMap(final Class<?> mapType, final int initialCapacity) {
        if (mapType.isInterface()) {
            if (Map.class.equals(mapType)) {
                return new LinkedHashMap<K, V>(initialCapacity);
            }
            if (SortedMap.class.equals(mapType) || NavigableMap.class.equals(mapType)) {
                return new TreeMap<K, V>();
            }
            if (MultiValueMap.class.equals(mapType)) {
                return (Map<K, V>)new LinkedMultiValueMap();
            }
            throw new IllegalArgumentException("Unsupported Map interface: " + mapType.getName());
        }
        else {
            if (!Map.class.isAssignableFrom(mapType)) {
                throw new IllegalArgumentException("Unsupported Map type: " + mapType.getName());
            }
            try {
                return (Map<K, V>)mapType.newInstance();
            }
            catch (Exception ex) {
                throw new IllegalArgumentException("Could not instantiate Map type: " + mapType.getName(), ex);
            }
        }
    }
    
    static {
        approximableCollectionTypes = new HashSet<Class<?>>(10);
        approximableMapTypes = new HashSet<Class<?>>(6);
        CollectionFactory.approximableCollectionTypes.add(Collection.class);
        CollectionFactory.approximableCollectionTypes.add(List.class);
        CollectionFactory.approximableCollectionTypes.add(Set.class);
        CollectionFactory.approximableCollectionTypes.add(SortedSet.class);
        CollectionFactory.approximableCollectionTypes.add(NavigableSet.class);
        CollectionFactory.approximableMapTypes.add(Map.class);
        CollectionFactory.approximableMapTypes.add(SortedMap.class);
        CollectionFactory.approximableMapTypes.add(NavigableMap.class);
        CollectionFactory.approximableCollectionTypes.add(ArrayList.class);
        CollectionFactory.approximableCollectionTypes.add(LinkedList.class);
        CollectionFactory.approximableCollectionTypes.add(HashSet.class);
        CollectionFactory.approximableCollectionTypes.add(LinkedHashSet.class);
        CollectionFactory.approximableCollectionTypes.add(TreeSet.class);
        CollectionFactory.approximableMapTypes.add(HashMap.class);
        CollectionFactory.approximableMapTypes.add(LinkedHashMap.class);
        CollectionFactory.approximableMapTypes.add(TreeMap.class);
    }
}
