package vlfsoft.ithillel.jee

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException

internal class KDistinctWordsFromTextFile {

    /**
     * Write a program to find all distinct words from a text file.
     * Ignore chars like    ".,/-;:" and Ignore case sensitivity.
     */
    @Test
    fun loadAndTest() {

        try {
            File("1.txt").useLines { sequence ->
                // Ignore chars like    ".,/-;:" and Ignore case sensitivity.
                // Assume, that word contains only case insensitive letters and digits.
                // If the condition sounded "Ignore chars ".,/-;:" and Ignore case sensitivity" (without like), another pattern would be leveraged.
                val pattern = "[a-zA-Z0-9]+".toRegex()

                // Map to store distinct words, since Map is an object that maps keys to values. A map cannot contain duplicate keys; each key can map to at most one value.
                // Since mutableMapOf creates LinkedHashMap, sorting below is not required.
                val distinctWords = mutableMapOf<String, Int>()

                // Kotlin doesn't have java restriction: variable in lambda expression should be final ...
//                var wordPosition = 0
//                sequence.forEach { line ->
//                    // Extracting words from a line
//                    pattern.findAll(line).forEach {
//                        val word = it.value
//                        if (!distinctWords.containsKey(word)) distinctWords[word] = ++wordPosition
//                    }
//                }

                // This solution is in more functional style, but index will be with gaps (where duplicate words appear)
                sequence
                        // Extracting words from a line
                        .flatMap { pattern.findAll(it) }
                        .withIndex()
                        .forEach {
                            val word = it.value.value
                            if (!distinctWords.containsKey(word)) distinctWords[word] = it.index + 1
                        }

                distinctWords.forEach { key, value -> println("($key, $value)") }
                println("distinctWords.size = ${distinctWords.size}")

                assertEquals(17, distinctWords.size)

/*
                // Leverage LinkedHashMap, since it (unlike HashMap) is ..., with predictable iteration order.
                val sortedDistinctWords = distinctWords
                        .toList()
                        .sortedBy { (_, value) -> value}
                        // toMap() ... toMap(LinkedHashMap ...
                        .toMap()

                sortedDistinctWords.forEach { key, value -> System.out.printf("(%s, %d)\n", key, value) }
                System.out.printf("sortedDistinctWords.size = %d\n", sortedDistinctWords.size)

                assertEquals(17, sortedDistinctWords.size)
*/

            }
        } catch (e: IOException) {
            println(e.toString())
        }

    }

}
