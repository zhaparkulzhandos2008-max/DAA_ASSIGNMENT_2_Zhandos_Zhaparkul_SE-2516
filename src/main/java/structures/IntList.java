package structures;

import metrics.OperationMetrics;

/** Common API used by DynamicArray and MyLinkedList in the benchmark. */
public interface IntList {
    void add(int x);

    void add(int index, int x);

    int remove(int index);

    int get(int index);

    boolean contains(int x);

    int size();

    OperationMetrics getMetrics();
}
