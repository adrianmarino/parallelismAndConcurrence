package com.nonosoft;

class Node<D> {
    private D value;
    private Node<D> next;

    public Node(D value) {
        this.value = value;
    }

    public D value() {
        return value;
    }
    public Node<D> next() {
        return next;
    }
    public void next(Node<D> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(").append(value).append(")");
        if (next != null) sb.append("->").append(next);
        return sb.toString();
    }
}
