package vlfsoft.ithillel.jee

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test

class KArrayToCollection {

    private fun <T> arrayToCollection(array: Array<T>): Collection<T> = array.toList() //Arrays.asList(*array)
    private fun <T> arrayToCollection(array: Array<out T>, collection: MutableCollection<in T>)  {
        array.forEach {
            collection += it
        }
    }

    // Write a generic method that takes an array of objects and a collection, and puts all objects in the array into the collection
    @Test
    fun arrayToCollectionTest() {

        val expectedArray = arrayOf(1, 2, 3, 4, 5)
        val collection = arrayToCollection(expectedArray)
        assertEquals(expectedArray.size, collection.size)

        val actualArray = collection.toTypedArray()

        assertArrayEquals(expectedArray, actualArray)

        val numberCollection : MutableCollection<Number> = mutableListOf()
        arrayToCollection(expectedArray, numberCollection)

    }

}
