// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import java.lang.ref.ReferenceQueue;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.AbstractMap;

public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>
{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private static final ReferenceType DEFAULT_REFERENCE_TYPE;
    private static final int MAXIMUM_CONCURRENCY_LEVEL = 65536;
    private static final int MAXIMUM_SEGMENT_SIZE = 1073741824;
    private final Segment[] segments;
    private final float loadFactor;
    private final ReferenceType referenceType;
    private final int shift;
    private Set<Map.Entry<K, V>> entrySet;
    
    public ConcurrentReferenceHashMap() {
        this(16, 0.75f, 16, ConcurrentReferenceHashMap.DEFAULT_REFERENCE_TYPE);
    }
    
    public ConcurrentReferenceHashMap(final int initialCapacity) {
        this(initialCapacity, 0.75f, 16, ConcurrentReferenceHashMap.DEFAULT_REFERENCE_TYPE);
    }
    
    public ConcurrentReferenceHashMap(final int initialCapacity, final float loadFactor) {
        this(initialCapacity, loadFactor, 16, ConcurrentReferenceHashMap.DEFAULT_REFERENCE_TYPE);
    }
    
    public ConcurrentReferenceHashMap(final int initialCapacity, final int concurrencyLevel) {
        this(initialCapacity, 0.75f, concurrencyLevel, ConcurrentReferenceHashMap.DEFAULT_REFERENCE_TYPE);
    }
    
    public ConcurrentReferenceHashMap(final int initialCapacity, final ReferenceType referenceType) {
        this(initialCapacity, 0.75f, 16, referenceType);
    }
    
    public ConcurrentReferenceHashMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel) {
        this(initialCapacity, loadFactor, concurrencyLevel, ConcurrentReferenceHashMap.DEFAULT_REFERENCE_TYPE);
    }
    
    public ConcurrentReferenceHashMap(final int initialCapacity, final float loadFactor, final int concurrencyLevel, final ReferenceType referenceType) {
        Assert.isTrue(initialCapacity >= 0, "Initial capacity must not be negative");
        Assert.isTrue(loadFactor > 0.0f, "Load factor must be positive");
        Assert.isTrue(concurrencyLevel > 0, "Concurrency level must be positive");
        Assert.notNull(referenceType, "Reference type must not be null");
        this.loadFactor = loadFactor;
        this.shift = calculateShift(concurrencyLevel, 65536);
        final int size = 1 << this.shift;
        this.referenceType = referenceType;
        final int roundedUpSegmentCapacity = (int)((initialCapacity + size - 1L) / size);
        this.segments = (Segment[])Array.newInstance(Segment.class, size);
        for (int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment(roundedUpSegmentCapacity);
        }
    }
    
    protected final float getLoadFactor() {
        return this.loadFactor;
    }
    
    protected final int getSegmentsSize() {
        return this.segments.length;
    }
    
    protected final Segment getSegment(final int index) {
        return this.segments[index];
    }
    
    protected ReferenceManager createReferenceManager() {
        return new ReferenceManager();
    }
    
    protected int getHash(final Object o) {
        int hash = (o == null) ? 0 : o.hashCode();
        hash += (hash << 15 ^ 0xFFFFCD7D);
        hash ^= hash >>> 10;
        hash += hash << 3;
        hash ^= hash >>> 6;
        hash += (hash << 2) + (hash << 14);
        hash ^= hash >>> 16;
        return hash;
    }
    
    @Override
    public V get(final Object key) {
        final Reference<K, V> reference = this.getReference(key, Restructure.WHEN_NECESSARY);
        final Entry<K, V> entry = (reference == null) ? null : reference.get();
        return (entry != null) ? entry.getValue() : null;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final Reference<K, V> reference = this.getReference(key, Restructure.WHEN_NECESSARY);
        final Entry<K, V> entry = (reference == null) ? null : reference.get();
        return entry != null && ObjectUtils.nullSafeEquals(entry.getKey(), key);
    }
    
    protected final Reference<K, V> getReference(final Object key, final Restructure restructure) {
        final int hash = this.getHash(key);
        return this.getSegmentForHash(hash).getReference(key, hash, restructure);
    }
    
    @Override
    public V put(final K key, final V value) {
        return this.put(key, value, true);
    }
    
    @Override
    public V putIfAbsent(final K key, final V value) {
        return this.put(key, value, false);
    }
    
    private V put(final K key, final V value, final boolean overwriteExisting) {
        return this.doTask(key, (Task<V>)new Task<V>(new TaskOption[] { TaskOption.RESTRUCTURE_BEFORE, TaskOption.RESIZE }) {
            @Override
            protected V execute(final Reference<K, V> reference, final Entry<K, V> entry, final Entries entries) {
                if (entry != null) {
                    final V previousValue = entry.getValue();
                    if (overwriteExisting) {
                        entry.setValue(value);
                    }
                    return previousValue;
                }
                entries.add(value);
                return null;
            }
        });
    }
    
    @Override
    public V remove(final Object key) {
        return this.doTask(key, (Task<V>)new Task<V>(new TaskOption[] { TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY }) {
            @Override
            protected V execute(final Reference<K, V> reference, final Entry<K, V> entry) {
                if (entry != null) {
                    reference.release();
                    return (V)((Entry<Object, Object>)entry).value;
                }
                return null;
            }
        });
    }
    
    @Override
    public boolean remove(final Object key, final Object value) {
        return this.doTask(key, (Task<Boolean>)new Task<Boolean>(new TaskOption[] { TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY }) {
            @Override
            protected Boolean execute(final Reference<K, V> reference, final Entry<K, V> entry) {
                if (entry != null && ObjectUtils.nullSafeEquals(entry.getValue(), value)) {
                    reference.release();
                    return true;
                }
                return false;
            }
        });
    }
    
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return this.doTask(key, (Task<Boolean>)new Task<Boolean>(new TaskOption[] { TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY }) {
            @Override
            protected Boolean execute(final Reference<K, V> reference, final Entry<K, V> entry) {
                if (entry != null && ObjectUtils.nullSafeEquals(entry.getValue(), oldValue)) {
                    entry.setValue(newValue);
                    return true;
                }
                return false;
            }
        });
    }
    
    @Override
    public V replace(final K key, final V value) {
        return this.doTask(key, (Task<V>)new Task<V>(new TaskOption[] { TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY }) {
            @Override
            protected V execute(final Reference<K, V> reference, final Entry<K, V> entry) {
                if (entry != null) {
                    final V previousValue = entry.getValue();
                    entry.setValue(value);
                    return previousValue;
                }
                return null;
            }
        });
    }
    
    @Override
    public void clear() {
        for (final Segment segment : this.segments) {
            segment.clear();
        }
    }
    
    public void purgeUnreferencedEntries() {
        for (final Segment segment : this.segments) {
            segment.restructureIfNecessary(false);
        }
    }
    
    @Override
    public int size() {
        int size = 0;
        for (final Segment segment : this.segments) {
            size += segment.getCount();
        }
        return size;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }
    
    private <T> T doTask(final Object key, final Task<T> task) {
        final int hash = this.getHash(key);
        return this.getSegmentForHash(hash).doTask(hash, key, task);
    }
    
    private Segment getSegmentForHash(final int hash) {
        return this.segments[hash >>> 32 - this.shift & this.segments.length - 1];
    }
    
    protected static int calculateShift(final int minimumValue, final int maximumValue) {
        int shift = 0;
        for (int value = 1; value < minimumValue && value < minimumValue; value <<= 1, ++shift) {}
        return shift;
    }
    
    static {
        DEFAULT_REFERENCE_TYPE = ReferenceType.SOFT;
    }
    
    public enum ReferenceType
    {
        SOFT, 
        WEAK;
    }
    
    protected final class Segment extends ReentrantLock
    {
        private final ReferenceManager referenceManager;
        private final int initialSize;
        private volatile Reference<K, V>[] references;
        private volatile int count;
        private int resizeThreshold;
        
        public Segment(final int initialCapacity) {
            this.count = 0;
            this.referenceManager = ConcurrentReferenceHashMap.this.createReferenceManager();
            this.initialSize = 1 << ConcurrentReferenceHashMap.calculateShift(initialCapacity, 1073741824);
            this.setReferences(this.createReferenceArray(this.initialSize));
        }
        
        public Reference<K, V> getReference(final Object key, final int hash, final Restructure restructure) {
            if (restructure == Restructure.WHEN_NECESSARY) {
                this.restructureIfNecessary(false);
            }
            if (this.count == 0) {
                return null;
            }
            final Reference<K, V>[] references = this.references;
            final int index = this.getIndex(hash, references);
            final Reference<K, V> head = references[index];
            return this.findInChain(head, key, hash);
        }
        
        public <T> T doTask(final int hash, final Object key, final Task<T> task) {
            final boolean resize = task.hasOption(TaskOption.RESIZE);
            if (task.hasOption(TaskOption.RESTRUCTURE_BEFORE)) {
                this.restructureIfNecessary(resize);
            }
            if (task.hasOption(TaskOption.SKIP_IF_EMPTY) && this.count == 0) {
                return task.execute(null, null, null);
            }
            this.lock();
            try {
                final int index = this.getIndex(hash, this.references);
                final Reference<K, V> head = this.references[index];
                final Reference<K, V> reference = this.findInChain(head, key, hash);
                final Entry<K, V> entry = (reference == null) ? null : reference.get();
                final Entries entries = new Entries() {
                    @Override
                    public void add(final V value) {
                        final Entry<K, V> newEntry = new Entry<K, V>((K)key, value);
                        final Reference<K, V> newReference = Segment.this.referenceManager.createReference(newEntry, hash, head);
                        Segment.this.references[index] = newReference;
                        Segment.this.count++;
                    }
                };
                return task.execute(reference, entry, entries);
            }
            finally {
                this.unlock();
                if (task.hasOption(TaskOption.RESTRUCTURE_AFTER)) {
                    this.restructureIfNecessary(resize);
                }
            }
        }
        
        public void clear() {
            if (this.count == 0) {
                return;
            }
            this.lock();
            try {
                this.setReferences(this.createReferenceArray(this.initialSize));
                this.count = 0;
            }
            finally {
                this.unlock();
            }
        }
        
        protected final void restructureIfNecessary(final boolean allowResize) {
            boolean needsResize = this.count > 0 && this.count >= this.resizeThreshold;
            Reference<K, V> reference = this.referenceManager.pollForPurge();
            if (reference != null || (needsResize && allowResize)) {
                this.lock();
                try {
                    int countAfterRestructure = this.count;
                    Set<Reference<K, V>> toPurge = Collections.emptySet();
                    if (reference != null) {
                        toPurge = new HashSet<Reference<K, V>>();
                        while (reference != null) {
                            toPurge.add(reference);
                            reference = this.referenceManager.pollForPurge();
                        }
                    }
                    countAfterRestructure -= toPurge.size();
                    needsResize = (countAfterRestructure > 0 && countAfterRestructure >= this.resizeThreshold);
                    boolean resizing = false;
                    int restructureSize = this.references.length;
                    if (allowResize && needsResize && restructureSize < 1073741824) {
                        restructureSize <<= 1;
                        resizing = true;
                    }
                    final Reference<K, V>[] restructured = resizing ? this.createReferenceArray(restructureSize) : this.references;
                    for (int i = 0; i < this.references.length; ++i) {
                        reference = this.references[i];
                        if (!resizing) {
                            restructured[i] = null;
                        }
                        while (reference != null) {
                            if (!toPurge.contains(reference) && reference.get() != null) {
                                final int index = this.getIndex(reference.getHash(), restructured);
                                restructured[index] = this.referenceManager.createReference(reference.get(), reference.getHash(), restructured[index]);
                            }
                            reference = reference.getNext();
                        }
                    }
                    if (resizing) {
                        this.setReferences(restructured);
                    }
                    this.count = Math.max(countAfterRestructure, 0);
                }
                finally {
                    this.unlock();
                }
            }
        }
        
        private Reference<K, V> findInChain(Reference<K, V> reference, final Object key, final int hash) {
            while (reference != null) {
                if (reference.getHash() == hash) {
                    final Entry<K, V> entry = reference.get();
                    if (entry != null) {
                        final K entryKey = entry.getKey();
                        if (entryKey == key || entryKey.equals(key)) {
                            return reference;
                        }
                    }
                }
                reference = reference.getNext();
            }
            return null;
        }
        
        private Reference<K, V>[] createReferenceArray(final int size) {
            return (Reference<K, V>[])Array.newInstance(Reference.class, size);
        }
        
        private int getIndex(final int hash, final Reference<K, V>[] references) {
            return hash & references.length - 1;
        }
        
        private void setReferences(final Reference<K, V>[] references) {
            this.references = references;
            this.resizeThreshold = (int)(references.length * ConcurrentReferenceHashMap.this.getLoadFactor());
        }
        
        public final int getSize() {
            return this.references.length;
        }
        
        public final int getCount() {
            return this.count;
        }
    }
    
    protected static final class Entry<K, V> implements Map.Entry<K, V>
    {
        private final K key;
        private volatile V value;
        
        public Entry(final K key, final V value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public V setValue(final V value) {
            final V previous = this.value;
            this.value = value;
            return previous;
        }
        
        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
        
        @Override
        public final boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o != null && o instanceof Map.Entry) {
                final Map.Entry other = (Map.Entry)o;
                return ObjectUtils.nullSafeEquals(this.getKey(), other.getKey()) && ObjectUtils.nullSafeEquals(this.getValue(), other.getValue());
            }
            return false;
        }
        
        @Override
        public final int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.key) ^ ObjectUtils.nullSafeHashCode(this.value);
        }
    }
    
    private abstract class Task<T>
    {
        private final EnumSet<TaskOption> options;
        
        public Task(final TaskOption... options) {
            this.options = ((options.length == 0) ? EnumSet.noneOf(TaskOption.class) : EnumSet.of(options[0], options));
        }
        
        public boolean hasOption(final TaskOption option) {
            return this.options.contains(option);
        }
        
        protected T execute(final Reference<K, V> reference, final Entry<K, V> entry, final Entries entries) {
            return this.execute(reference, entry);
        }
        
        protected T execute(final Reference<K, V> reference, final Entry<K, V> entry) {
            return null;
        }
    }
    
    private enum TaskOption
    {
        RESTRUCTURE_BEFORE, 
        RESTRUCTURE_AFTER, 
        SKIP_IF_EMPTY, 
        RESIZE;
    }
    
    private abstract class Entries
    {
        public abstract void add(final V p0);
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (o != null && o instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                final Reference<K, V> reference = ConcurrentReferenceHashMap.this.getReference(entry.getKey(), Restructure.NEVER);
                final Entry<K, V> other = (reference == null) ? null : reference.get();
                if (other != null) {
                    return ObjectUtils.nullSafeEquals(entry.getValue(), other.getValue());
                }
            }
            return false;
        }
        
        @Override
        public boolean remove(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                return ConcurrentReferenceHashMap.this.remove(entry.getKey(), entry.getValue());
            }
            return false;
        }
        
        @Override
        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }
        
        @Override
        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }
    
    private class EntryIterator implements Iterator<Map.Entry<K, V>>
    {
        private int segmentIndex;
        private int referenceIndex;
        private Reference<K, V>[] references;
        private Reference<K, V> reference;
        private Entry<K, V> next;
        private Entry<K, V> last;
        
        public EntryIterator() {
            this.moveToNextSegment();
        }
        
        @Override
        public boolean hasNext() {
            this.getNextIfNecessary();
            return this.next != null;
        }
        
        @Override
        public Entry<K, V> next() {
            this.getNextIfNecessary();
            if (this.next == null) {
                throw new NoSuchElementException();
            }
            this.last = this.next;
            this.next = null;
            return this.last;
        }
        
        private void getNextIfNecessary() {
            while (this.next == null) {
                this.moveToNextReference();
                if (this.reference == null) {
                    return;
                }
                this.next = this.reference.get();
            }
        }
        
        private void moveToNextReference() {
            if (this.reference != null) {
                this.reference = this.reference.getNext();
            }
            while (this.reference == null && this.references != null) {
                if (this.referenceIndex >= this.references.length) {
                    this.moveToNextSegment();
                    this.referenceIndex = 0;
                }
                else {
                    this.reference = this.references[this.referenceIndex];
                    ++this.referenceIndex;
                }
            }
        }
        
        private void moveToNextSegment() {
            this.reference = null;
            this.references = null;
            if (this.segmentIndex < ConcurrentReferenceHashMap.this.segments.length) {
                this.references = ConcurrentReferenceHashMap.this.segments[this.segmentIndex].references;
                ++this.segmentIndex;
            }
        }
        
        @Override
        public void remove() {
            Assert.state(this.last != null);
            ConcurrentReferenceHashMap.this.remove(this.last.getKey());
        }
    }
    
    protected enum Restructure
    {
        WHEN_NECESSARY, 
        NEVER;
    }
    
    protected class ReferenceManager
    {
        private final ReferenceQueue<Entry<K, V>> queue;
        
        protected ReferenceManager() {
            this.queue = new ReferenceQueue<Entry<K, V>>();
        }
        
        public Reference<K, V> createReference(final Entry<K, V> entry, final int hash, final Reference<K, V> next) {
            if (ConcurrentReferenceHashMap.this.referenceType == ReferenceType.WEAK) {
                return new WeakEntryReference<K, V>(entry, hash, next, this.queue);
            }
            return new SoftEntryReference<K, V>(entry, hash, next, this.queue);
        }
        
        public Reference<K, V> pollForPurge() {
            return (Reference<K, V>)(Reference)this.queue.poll();
        }
    }
    
    private static final class SoftEntryReference<K, V> extends SoftReference<Entry<K, V>> implements ConcurrentReferenceHashMap.Reference<K, V>
    {
        private final int hash;
        private final ConcurrentReferenceHashMap.Reference<K, V> nextReference;
        
        public SoftEntryReference(final Entry<K, V> entry, final int hash, final ConcurrentReferenceHashMap.Reference<K, V> next, final ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public ConcurrentReferenceHashMap.Reference<K, V> getNext() {
            return this.nextReference;
        }
        
        @Override
        public void release() {
            this.enqueue();
            this.clear();
        }
    }
    
    private static final class WeakEntryReference<K, V> extends WeakReference<Entry<K, V>> implements ConcurrentReferenceHashMap.Reference<K, V>
    {
        private final int hash;
        private final ConcurrentReferenceHashMap.Reference<K, V> nextReference;
        
        public WeakEntryReference(final Entry<K, V> entry, final int hash, final ConcurrentReferenceHashMap.Reference<K, V> next, final ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }
        
        @Override
        public int getHash() {
            return this.hash;
        }
        
        @Override
        public ConcurrentReferenceHashMap.Reference<K, V> getNext() {
            return this.nextReference;
        }
        
        @Override
        public void release() {
            this.enqueue();
            this.clear();
        }
    }
    
    protected interface Reference<K, V>
    {
        Entry<K, V> get();
        
        int getHash();
        
        Reference<K, V> getNext();
        
        void release();
    }
}
