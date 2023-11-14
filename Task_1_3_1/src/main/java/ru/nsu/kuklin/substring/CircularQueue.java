package ru.nsu.kuklin.substring;

import java.io.*;
import java.lang.*;
import java.util.*;

/**
 * Circular buffer queue.
 * Allocates once, at the creation time
 */
public class CircularQueue<T> {
    /**
     * Constructor.
     * Determines the capacity
     */
    @SuppressWarnings("unchecked")
    public CircularQueue(int capacity) {
        storage = (T[]) new Object[capacity]; 
        length = 0;
        head = 0;
    }

    /**
     * Pushes element to the queue.
     * Returns true on success.
     */
    public boolean push(T element) {
        if (length == storage.length) {
            return false;
        }
        storage[(head + length) % storage.length] = element;
        length++;
        return true;
    }

    /**
     * Returns next element and removes it.
     */
    public T pop() {
        if (length == 0) {
            return null;
        }
        var res = storage[head];
        head = (head + 1) % storage.length;
        length--;
        return res;
    }

    /**
     * Returns element at a given pos.
     */
    public T getAt(int pos) {
        if (pos >= length) {
            throw new NoSuchElementException();
        }
        return storage[(head + pos) % storage.length];
    }

    /**
     * Returns current number of elements in the queue.
     */
    public int size() {
        return length;
    }

    private int head;
    private int length;
    private T[] storage;
}


