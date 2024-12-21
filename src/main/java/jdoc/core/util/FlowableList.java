package jdoc.core.util;


import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.PublishProcessor;

import java.util.*;

public class FlowableList<T> implements List<T> {
    private final List<T> list;
    private final PublishProcessor<List<T>> flowable = PublishProcessor.create();
    public FlowableList(List<T> base) {
        this.list = base;
    }
    public FlowableList() {
        this(new ArrayList<>());
    }

    public Flowable<List<T>> flowable() {
        return flowable;
    }

    private void sync() {
        flowable.onNext(List.copyOf(list));
    }
    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T t) {
        var result = list.add(t);
        sync();
        return result;
    }

    @Override
    public boolean remove(Object o) {
        var result = list.remove(o);
        sync();
        return result;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        var result = list.addAll(c);
        sync();
        return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        var result = list.addAll(index, c);
        sync();
        return result;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        var result = list.removeAll(c);
        sync();
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        var result = list.retainAll(c);
        sync();
        return result;
    }

    @Override
    public void clear() {
        list.clear();
        sync();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T set(int index, T element) {
        var result = list.set(index, element);
        sync();
        return result;
    }

    @Override
    public void add(int index, T element) {
        list.add(index, element);
        sync();
    }

    @Override
    public T remove(int index) {
        var result = list.remove(index);
        sync();
        return result;
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }
}
