package vlfsoft.ithillel.jee;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ArrayToCollection {

    /*
        private static <T> Collection<T> arrayToCollection(T[] array) {
            return Arrays.asList(array);
        }
    */
    // Write a generic method that takes an array of objects and a collection, and puts all objects in the array into the collection
    private static <T> void arrayToCollection(T[] array, Collection<? super  T> collection) {
        collection.addAll(Arrays.asList(array));
    }

    @Test
    void arrayToCollectionTest() {

        Integer[] expectedArray = {1, 2, 3, 4, 5};
        Collection<Integer> IntegerCollection = new ArrayList<>();

        assertEquals(0, IntegerCollection.size());

        arrayToCollection(expectedArray, IntegerCollection);
        assertEquals(expectedArray.length, IntegerCollection.size());

        Integer[] actualArray = IntegerCollection.toArray(new Integer[0]);
        assertArrayEquals(expectedArray, actualArray);

        // Number is super type of Integer and it's safe to place Integer items to Collection<Number>.
        Collection<Number> numberCollection = new ArrayList<>();
        arrayToCollection(expectedArray, numberCollection);

    }

}
