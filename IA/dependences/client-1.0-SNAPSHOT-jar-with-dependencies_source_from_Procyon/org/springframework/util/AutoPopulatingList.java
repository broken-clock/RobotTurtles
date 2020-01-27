// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.util;

import java.lang.reflect.Modifier;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.List;

public class AutoPopulatingList<E> implements List<E>, Serializable
{
    private final List<E> backingList;
    private final ElementFactory<E> elementFactory;
    
    public AutoPopulatingList(final Class<? extends E> elementClass) {
        this((List)new ArrayList(), elementClass);
    }
    
    public AutoPopulatingList(final List<E> backingList, final Class<? extends E> elementClass) {
        this(backingList, (ElementFactory)new ReflectiveElementFactory(elementClass));
    }
    
    public AutoPopulatingList(final ElementFactory<E> elementFactory) {
        this((List)new ArrayList(), elementFactory);
    }
    
    public AutoPopulatingList(final List<E> backingList, final ElementFactory<E> elementFactory) {
        Assert.notNull(backingList, "Backing List must not be null");
        Assert.notNull(elementFactory, "Element factory must not be null");
        this.backingList = backingList;
        this.elementFactory = elementFactory;
    }
    
    @Override
    public void add(final int index, final E element) {
        this.backingList.add(index, element);
    }
    
    @Override
    public boolean add(final E o) {
        return this.backingList.add(o);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return this.backingList.addAll(c);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        return this.backingList.addAll(index, c);
    }
    
    @Override
    public void clear() {
        this.backingList.clear();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.backingList.contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.backingList.containsAll(c);
    }
    
    @Override
    public E get(final int index) {
        final int backingListSize = this.backingList.size();
        E element = null;
        if (index < backingListSize) {
            element = this.backingList.get(index);
            if (element == null) {
                element = this.elementFactory.createElement(index);
                this.backingList.set(index, element);
            }
        }
        else {
            for (int x = backingListSize; x < index; ++x) {
                this.backingList.add(null);
            }
            element = this.elementFactory.createElement(index);
            this.backingList.add(element);
        }
        return element;
    }
    
    @Override
    public int indexOf(final Object o) {
        return this.backingList.indexOf(o);
    }
    
    @Override
    public boolean isEmpty() {
        return this.backingList.isEmpty();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.backingList.iterator();
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        return this.backingList.lastIndexOf(o);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return this.backingList.listIterator();
    }
    
    @Override
    public ListIterator<E> listIterator(final int index) {
        return this.backingList.listIterator(index);
    }
    
    @Override
    public E remove(final int index) {
        return this.backingList.remove(index);
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.backingList.remove(o);
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.backingList.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.backingList.retainAll(c);
    }
    
    @Override
    public E set(final int index, final E element) {
        return this.backingList.set(index, element);
    }
    
    @Override
    public int size() {
        return this.backingList.size();
    }
    
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return this.backingList.subList(fromIndex, toIndex);
    }
    
    @Override
    public Object[] toArray() {
        return this.backingList.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return this.backingList.toArray(a);
    }
    
    @Override
    public boolean equals(final Object other) {
        return this.backingList.equals(other);
    }
    
    @Override
    public int hashCode() {
        return this.backingList.hashCode();
    }
    
    public static class ElementInstantiationException extends RuntimeException
    {
        public ElementInstantiationException(final String msg) {
            super(msg);
        }
    }
    
    private static class ReflectiveElementFactory<E> implements ElementFactory<E>, Serializable
    {
        private final Class<? extends E> elementClass;
        
        public ReflectiveElementFactory(final Class<? extends E> elementClass) {
            Assert.notNull(elementClass, "Element class must not be null");
            Assert.isTrue(!elementClass.isInterface(), "Element class must not be an interface type");
            Assert.isTrue(!Modifier.isAbstract(elementClass.getModifiers()), "Element class cannot be an abstract class");
            this.elementClass = elementClass;
        }
        
        @Override
        public E createElement(final int index) {
            try {
                return (E)this.elementClass.newInstance();
            }
            catch (InstantiationException ex) {
                throw new ElementInstantiationException("Unable to instantiate element class [" + this.elementClass.getName() + "]. Root cause is " + ex);
            }
            catch (IllegalAccessException ex2) {
                throw new ElementInstantiationException("Cannot access element class [" + this.elementClass.getName() + "]. Root cause is " + ex2);
            }
        }
    }
    
    public interface ElementFactory<E>
    {
        E createElement(final int p0) throws ElementInstantiationException;
    }
}
