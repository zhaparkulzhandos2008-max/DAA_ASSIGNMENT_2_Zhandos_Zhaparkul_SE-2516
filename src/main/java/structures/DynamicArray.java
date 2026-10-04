package structures;

import metrics.OperationMetrics;

/** A resizable int array that doubles its capacity when full. */
public final class DynamicArray implements IntList {
    private static final int DEFAULT_CAPACITY = 10;

    private int[] data;
    private int size;
    private final OperationMetrics metrics = new OperationMetrics();

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity must be positive");
        }
        data = new int[initialCapacity];
    }

    @Override
    public void add(int x) {
        ensureCapacity();
        data[size] = x;
        size++;
    }

    @Override
    public void add(int index, int x) {
        checkPositionIndex(index);
        ensureCapacity();

        for (int i = size; i > index; i--) {
            metrics.step();                 // read data[i - 1]
            data[i] = data[i - 1];
            metrics.move();                 // shift one existing element right
        }

        data[index] = x;
        size++;
    }

    @Override
    public int remove(int index) {
        checkElementIndex(index);

        metrics.step();
        int removed = data[index];

        for (int i = index; i < size - 1; i++) {
            metrics.step();                 // read data[i + 1]
            data[i] = data[i + 1];
            metrics.move();                 // shift one existing element left
        }

        size--;
        return removed;
    }

    @Override
    public int get(int index) {
        checkElementIndex(index);
        metrics.step();
        return data[index];
    }

    @Override
    public boolean contains(int x) {
        for (int i = 0; i < size; i++) {
            metrics.step();                 // read one array cell
            metrics.comparison();           // compare two int values
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    @Override
    public OperationMetrics getMetrics() {
        return metrics;
    }

    private void ensureCapacity() {
        if (size < data.length) {
            return;
        }

        int[] grown = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            metrics.step();                 // read old array cell
            grown[i] = data[i];
            metrics.move();                 // move existing element to new array
        }
        data = grown;
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
