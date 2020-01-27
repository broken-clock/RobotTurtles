// 
// Decompiled by Procyon v0.5.36
// 

package org.springframework.beans.support;

import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.Serializable;

public class PagedListHolder<E> implements Serializable
{
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_MAX_LINKED_PAGES = 10;
    private List<E> source;
    private Date refreshDate;
    private SortDefinition sort;
    private SortDefinition sortUsed;
    private int pageSize;
    private int page;
    private boolean newPageSet;
    private int maxLinkedPages;
    
    public PagedListHolder() {
        this((List)new ArrayList(0));
    }
    
    public PagedListHolder(final List<E> source) {
        this(source, new MutableSortDefinition(true));
    }
    
    public PagedListHolder(final List<E> source, final SortDefinition sort) {
        this.pageSize = 10;
        this.page = 0;
        this.maxLinkedPages = 10;
        this.setSource(source);
        this.setSort(sort);
    }
    
    public void setSource(final List<E> source) {
        Assert.notNull(source, "Source List must not be null");
        this.source = source;
        this.refreshDate = new Date();
        this.sortUsed = null;
    }
    
    public List<E> getSource() {
        return this.source;
    }
    
    public Date getRefreshDate() {
        return this.refreshDate;
    }
    
    public void setSort(final SortDefinition sort) {
        this.sort = sort;
    }
    
    public SortDefinition getSort() {
        return this.sort;
    }
    
    public void setPageSize(final int pageSize) {
        if (pageSize != this.pageSize) {
            this.pageSize = pageSize;
            if (!this.newPageSet) {
                this.page = 0;
            }
        }
    }
    
    public int getPageSize() {
        return this.pageSize;
    }
    
    public void setPage(final int page) {
        this.page = page;
        this.newPageSet = true;
    }
    
    public int getPage() {
        this.newPageSet = false;
        if (this.page >= this.getPageCount()) {
            this.page = this.getPageCount() - 1;
        }
        return this.page;
    }
    
    public void setMaxLinkedPages(final int maxLinkedPages) {
        this.maxLinkedPages = maxLinkedPages;
    }
    
    public int getMaxLinkedPages() {
        return this.maxLinkedPages;
    }
    
    public int getPageCount() {
        final float nrOfPages = this.getNrOfElements() / (float)this.getPageSize();
        return (int)((nrOfPages > (int)nrOfPages || nrOfPages == 0.0) ? (nrOfPages + 1.0f) : nrOfPages);
    }
    
    public boolean isFirstPage() {
        return this.getPage() == 0;
    }
    
    public boolean isLastPage() {
        return this.getPage() == this.getPageCount() - 1;
    }
    
    public void previousPage() {
        if (!this.isFirstPage()) {
            --this.page;
        }
    }
    
    public void nextPage() {
        if (!this.isLastPage()) {
            ++this.page;
        }
    }
    
    public int getNrOfElements() {
        return this.getSource().size();
    }
    
    public int getFirstElementOnPage() {
        return this.getPageSize() * this.getPage();
    }
    
    public int getLastElementOnPage() {
        final int endIndex = this.getPageSize() * (this.getPage() + 1);
        final int size = this.getNrOfElements();
        return ((endIndex > size) ? size : endIndex) - 1;
    }
    
    public List<E> getPageList() {
        return this.getSource().subList(this.getFirstElementOnPage(), this.getLastElementOnPage() + 1);
    }
    
    public int getFirstLinkedPage() {
        return Math.max(0, this.getPage() - this.getMaxLinkedPages() / 2);
    }
    
    public int getLastLinkedPage() {
        return Math.min(this.getFirstLinkedPage() + this.getMaxLinkedPages() - 1, this.getPageCount() - 1);
    }
    
    public void resort() {
        final SortDefinition sort = this.getSort();
        if (sort != null && !sort.equals(this.sortUsed)) {
            this.sortUsed = this.copySortDefinition(sort);
            this.doSort(this.getSource(), sort);
            this.setPage(0);
        }
    }
    
    protected SortDefinition copySortDefinition(final SortDefinition sort) {
        return new MutableSortDefinition(sort);
    }
    
    protected void doSort(final List<E> source, final SortDefinition sort) {
        PropertyComparator.sort(source, sort);
    }
}
