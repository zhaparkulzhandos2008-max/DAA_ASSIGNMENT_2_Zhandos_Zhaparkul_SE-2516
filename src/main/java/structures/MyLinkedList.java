package structures;

import metrics.OperationMetrics;

/** Singly linked int list with head and tail pointers. */
public final class MyLinkedList implements IntList {
    private static final class Node {
        private final int value;
        private Node next;

        private Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;
    private final OperationMetrics metrics = new OperationMetrics();

    @Override
    public void add(int x) {
        Node node = new Node(x);
        if (size == 0) {
            head = node;
            metrics.move();                 // head pointer update
            tail = node;
            metrics.move();                 // tail pointer update
        } else {
            tail.next = node;
            metrics.move();                 // link update
            tail = node;
            metrics.move();                 // tail pointer update
        }
        size++;
    }

    @Override
    public void add(int index, int x) {
        checkPositionIndex(index);

        if (index == size) {
            add(x);
            return;
        }

        Node node = new Node(x);
        if (index == 0) {
            node.next = head;
            metrics.move();                 // node link update
            head = node;
            metrics.move();                 // head pointer update
            if (size == 0) {
                tail = node;
                metrics.move();
            }
            size++;
            return;
        }

        Node previous = nodeAt(index - 1);
        node.next = previous.next;
        metrics.move();                     // new node link update
        previous.next = node;
        metrics.move();                     // previous node link update
        size++;
    }

    @Override
    public int remove(int index) {
        checkElementIndex(index);

        if (index == 0) {
            int removed = head.value;
            head = head.next;
            metrics.move();                 // head pointer update
            size--;
            if (size == 0) {
                tail = null;
                metrics.move();             // tail pointer update
            }
            return removed;
        }

        Node previous = nodeAt(index - 1);
        Node removed = previous.next;
        previous.next = removed.next;
        metrics.move();                     // bypass removed node
        if (removed == tail) {
            tail = previous;
            metrics.move();                 // tail pointer update
        }
        size--;
        return removed.value;
    }

    @Override
    public int get(int index) {
        checkElementIndex(index);
        return nodeAt(index).value;
    }

    @Override
    public boolean contains(int x) {
        Node current = head;
        while (current != null) {
            metrics.comparison();           // compare current.value with x
            if (current.value == x) {
                return true;
            }
            current = current.next;
            metrics.step();                 // move to the next node
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public OperationMetrics getMetrics() {
        return metrics;
    }

    private Node nodeAt(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
            metrics.step();                 // move to the next node
        }
        return current;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index=" + index + ", size=" + size);
        }
    }
}
