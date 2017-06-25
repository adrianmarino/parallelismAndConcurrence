package com.nonosoft;

import static java.util.Arrays.stream;

public class Queue<T> {
    private Node<T> head, tail;

    public void push(T... values) {
        stream(values).forEach(this::push);
    }
    public synchronized Queue<T> push(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) { head = node; notify(); } else tail.next(node);
        tail = node;
        return this;
    }
    public synchronized T pop() throws InterruptedException {
        if (head == null) wait();
        Node<T> node = head;
        head = node.next();
        return node.value();
    }
}
