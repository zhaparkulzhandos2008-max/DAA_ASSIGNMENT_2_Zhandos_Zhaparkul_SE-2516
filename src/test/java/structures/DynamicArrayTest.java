package structures;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    @Test
    void emptyAndInvalidIndexes() {
        DynamicArray array = new DynamicArray();
        assertEquals(0, array.size());
        assertFalse(array.contains(10));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 5));
    }

    @Test
    void oneElementDuplicatesFirstAndLastIndex() {
        DynamicArray array = new DynamicArray();
        array.add(7);
        assertEquals(7, array.get(0));

        array.add(0, 7);
        array.add(9);
        assertEquals(3, array.size());
        assertEquals(7, array.get(0));
        assertEquals(9, array.get(array.size() - 1));
        assertTrue(array.contains(7));

        assertEquals(7, array.remove(0));
        assertEquals(9, array.remove(array.size() - 1));
        assertEquals(7, array.get(0));
    }

    @Test
    void doublesCapacityWhenFull() {
        DynamicArray array = new DynamicArray(2);
        array.add(1);
        array.add(2);
        assertEquals(2, array.capacity());
        array.add(3);
        assertEquals(4, array.capacity());
    }

    @Test
    void matchesArrayListOnRandomData() {
        Random random = new Random(42);
        DynamicArray actual = new DynamicArray();
        ArrayList<Integer> expected = new ArrayList<>();

        for (int i = 0; i < 2_000; i++) {
            int value = random.nextInt(100);
            if (expected.isEmpty() || random.nextBoolean()) {
                int index = random.nextInt(expected.size() + 1);
                actual.add(index, value);
                expected.add(index, value);
            } else {
                int index = random.nextInt(expected.size());
                assertEquals(expected.remove(index).intValue(), actual.remove(index));
            }

            assertEquals(expected.size(), actual.size());
            for (int j = 0; j < expected.size(); j++) {
                assertEquals(expected.get(j).intValue(), actual.get(j));
            }
        }
    }
}
