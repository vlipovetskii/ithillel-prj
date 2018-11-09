package vlfsoft.ithillel.jee;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DistinctWordsFromTextFile {

    /**
     * Write a program to find all distinct words from a text file.
     * Ignore chars like    ".,/-;:" and Ignore case sensitivity.
     */
    @Test
    void loadAndTest() {

        try (Stream<String> stream = Files.lines(Paths.get("1.txt"))) {
            // Ignore chars like    ".,/-;:" and Ignore case sensitivity.
            // Assume, that word contains only case insensitive letters and digits.
            // If the condition sounded "Ignore chars ".,/-;:" and Ignore case sensitivity" (without like), another pattern would be leveraged.
            Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");

            // Map to store distinct words, since Map is an object that maps keys to values. A map cannot contain duplicate keys; each key can map to at most one value.
            // Map<String, Integer> distinctWords = new HashMap<>();
            // Leverage LinkedHashMap, since it (unlike HashMap) is ..., with predictable iteration order and sorting below is not required.
            Map<String, Integer> distinctWords = new LinkedHashMap<>();

            // To overcome java restriction: variable in lambda expression should be final ...
            Integer[] wordPosition = {0};
            stream.forEach(line -> {
                // Extracting words from a line
                Matcher mather = pattern.matcher(line);

                // public Stream<MatchResult> results​() is available since Java 9
                while (mather.find()) {
                    String word = mather.group();
                    if (!distinctWords.containsKey(word)) distinctWords.put(word, ++wordPosition[0]);
                }
            });

            distinctWords.forEach((key, value) -> System.out.printf("(%s, %d)\n", key, value));
            System.out.printf("distinctWords.size = %d\n", distinctWords.size());

            assertEquals(17, distinctWords.size());

/*
            // Leverage LinkedHashMap, since it (unlike HashMap) is ..., with predictable iteration order.
            Map<String, Integer> sortedDistinctWords = distinctWords
                    .entrySet()
                    .stream()
                    .sorted(comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

            sortedDistinctWords.forEach((key, value) -> System.out.printf("(%s, %d)\n", key, value));
            System.out.printf("sortedDistinctWords.size = %d\n", sortedDistinctWords.size());

            assertEquals(17, sortedDistinctWords.size());
*/

        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

}
