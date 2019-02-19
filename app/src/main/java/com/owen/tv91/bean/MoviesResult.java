package com.owen.tv91.bean;

import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/2/18
 */
public class MoviesResult {

    public List<Movie> content;

    public boolean last;

    public int totalPages;

    public int totalElements;

    public int numberOfElements;

    public int size;

    public int number;

    public boolean first;

    public boolean empty;


    public List<Movie> getContent() {
        return content;
    }

    public void setContent(List<Movie> content) {
        this.content = content;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
