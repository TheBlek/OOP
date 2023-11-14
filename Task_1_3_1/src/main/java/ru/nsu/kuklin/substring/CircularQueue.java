package ru.nsu.kuklin.substring;

import java.io.*;
import java.lang.*;
import java.util.*;

public class CircularQueue<T> {
    @SuppressWarnings("unchecked")
    public CircularQueue(int capacity) {
        storage = (T[])new Object[capacity]; 
        length = 0;
        head = 0;
    }

    public boolean push(T element) {
        if (length == storage.length) {
            return false;
        }
        storage[(head + length) % storage.length] = element;
        length++;
        return true;
    }

    public T peek() {
        if (length == 0) {
            return null;
        }
        return storage[head];
    }

    public T pop() {
        if (length == 0) {
            return null;
        }
        var res = storage[head];
        head = (head + 1) % storage.length;
        length--;
        return res;
    }

    public T getAt(int pos) {
        if (pos >= length) {
            throw new NoSuchElementException();
        }
        return storage[(head + pos) % storage.length];
    }

    public int size() {
        return length;
    }

    private int head;
    private int length;
    private T[] storage;
}


