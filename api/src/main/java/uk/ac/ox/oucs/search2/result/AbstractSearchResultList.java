package uk.ac.ox.oucs.search2.result;

import uk.ac.ox.oucs.search2.filter.FilterChain;
import uk.ac.ox.oucs.search2.filter.SearchFilter;

import java.util.*;

/**
 * @author Colin Hebert
 */
public abstract class AbstractSearchResultList<T> implements SearchResultList {
    private final List<SearchResult> results;

    protected AbstractSearchResultList(T result) {
        this(result, Collections.<SearchFilter>emptyList());
    }

    protected AbstractSearchResultList(T result, Iterable<SearchFilter> searchFilters) {
        this.results = Collections.unmodifiableList(getSearchResults(result, searchFilters));
    }

    protected abstract List<? extends SearchResult> getSearchResults(T result, Iterable<SearchFilter> filters);

    //----------------------------------------
    // Methods from List delegated internally
    //----------------------------------------

    @Override
    public int size() {
        return results.size();
    }

    @Override
    public boolean isEmpty() {
        return results.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return results.contains(o);
    }

    @Override
    public Iterator<SearchResult> iterator() {
        return results.iterator();
    }

    @Override
    public Object[] toArray() {
        return results.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return results.toArray(a);
    }

    @Override
    public boolean add(SearchResult searchResult) {
        return results.add(searchResult);
    }

    @Override
    public boolean remove(Object o) {
        return results.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return results.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends SearchResult> c) {
        return results.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends SearchResult> c) {
        return results.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return results.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return results.retainAll(c);
    }

    @Override
    public void clear() {
        results.clear();
    }

    @Override
    public boolean equals(Object o) {
        return results.equals(o);
    }

    @Override
    public int hashCode() {
        return results.hashCode();
    }

    @Override
    public SearchResult get(int index) {
        return results.get(index);
    }

    @Override
    public SearchResult set(int index, SearchResult element) {
        return results.set(index, element);
    }

    @Override
    public void add(int index, SearchResult element) {
        results.add(index, element);
    }

    @Override
    public SearchResult remove(int index) {
        return results.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return results.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return results.lastIndexOf(o);
    }

    @Override
    public ListIterator<SearchResult> listIterator() {
        return results.listIterator();
    }

    @Override
    public ListIterator<SearchResult> listIterator(int index) {
        return results.listIterator(index);
    }

    @Override
    public List<SearchResult> subList(int fromIndex, int toIndex) {
        return results.subList(fromIndex, toIndex);
    }
}
