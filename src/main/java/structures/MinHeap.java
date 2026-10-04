package structures;

import metrics.OperationMetrics;

/** Array-based binary min-heap for primitive int values. */
public final class MinHeap {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] heap;
    private int size;
    private final OperationMetrics metrics = new OperationMetrics();

    public MinHeap() {
        this(DEFAULT_CAPACITY);
    }

    public MinHeap(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity must be positive");
        }
        heap = new int[initialCapacity];
    }

    public void insert(int x) {
        ensureCapacity();
        heap[size] = x;
        int index = size;
        size++;
        bubbleUp(index);
    }

    public int peekMin() {
        ensureNotEmpty();
        metrics.step();
        return heap[0];
    }

    public int extractMin() {
        ensureNotEmpty();

        metrics.step();
        int minimum = heap[0];
        size--;

        if (size > 0) {
            metrics.step();
            heap[0] = heap[size];
            metrics.move();                 // move last element to root
            bubbleDown(0);
        }
        return minimum;
    }

    public int size() {
        return size;
    }

    public OperationMetrics getMetrics() {
        return metrics;
    }

    /** Test helper: validates the heap property without changing benchmark counters. */
    public boolean isHeapPropertyValid() {
        for (int child = 1; child < size; child++) {
            int parent = (child - 1) / 2;
            if (heap[parent] > heap[child]) {
                return false;
            }
        }
        return true;
    }

    private void bubbleUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;

            metrics.step();
            int childValue = heap[index];
            metrics.step();
            int parentValue = heap[parent];
            metrics.comparison();

            if (parentValue <= childValue) {
                return;
            }

            heap[parent] = childValue;
            metrics.move();
            heap[index] = parentValue;
            metrics.move();
            index = parent;
        }
    }

    private void bubbleDown(int index) {
        while (true) {
            int left = index * 2 + 1;
            if (left >= size) {
                return;
            }

            int right = left + 1;
            int smallerChild = left;

            if (right < size) {
                metrics.step();
                int leftValue = heap[left];
                metrics.step();
                int rightValue = heap[right];
                metrics.comparison();
                if (rightValue < leftValue) {
                    smallerChild = right;
                }
            }

            metrics.step();
            int parentValue = heap[index];
            metrics.step();
            int childValue = heap[smallerChild];
            metrics.comparison();

            if (parentValue <= childValue) {
                return;
            }

            heap[index] = childValue;
            metrics.move();
            heap[smallerChild] = parentValue;
            metrics.move();
            index = smallerChild;
        }
    }

    private void ensureCapacity() {
        if (size < heap.length) {
            return;
        }

        int[] grown = new int[heap.length * 2];
        for (int i = 0; i < size; i++) {
            metrics.step();
            grown[i] = heap[i];
            metrics.move();
        }
        heap = grown;
    }

    private void ensureNotEmpty() {
        if (size == 0) {
            throw new IllegalStateException("heap is empty");
        }
    }
}
