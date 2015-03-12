package mnaseef;

import org.junit.Test;
import org.junit.internal.matchers.IsCollectionContaining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class WordCounterTest {

    @Test
    public void testCountWords() {
        WordCounter instance = new WordCounter();
        instance.countWords("Alpha beta gamma");
        Map<String, Long> counts = instance.getWordCounts();
        assertEquals(3, counts.keySet().size());
        assertThat(counts.keySet(),
                IsCollectionContaining.hasItems("alpha", "beta", "gamma"));
        assertEquals(Long.valueOf(1), counts.get("alpha"));
        assertEquals(Long.valueOf(1), counts.get("beta"));
        assertEquals(Long.valueOf(1), counts.get("gamma"));

        // Add more words
        instance.countWords("alpha beta alpha11");
        assertEquals(4, counts.keySet().size());
        assertThat(counts.keySet(),
                IsCollectionContaining.hasItems("alpha", "beta", "gamma", "alpha11"));
        assertEquals(Long.valueOf(2), counts.get("alpha"));
        assertEquals(Long.valueOf(2), counts.get("beta"));
        assertEquals(Long.valueOf(1), counts.get("gamma"));
        assertEquals(Long.valueOf(1), counts.get("alpha11"));
    }

    @Test
    public void testCountOneWord() {
        WordCounter instance = new WordCounter();
        instance.countWords("Alpha11");
        Map<String, Long> counts = instance.getWordCounts();
        assertEquals(1, counts.keySet().size());
        assertThat(counts.keySet(), IsCollectionContaining.hasItem("alpha11"));
        assertEquals(Long.valueOf(1), counts.get("alpha11"));
    }

    @Test
    public void testCountNoWord() {
        WordCounter instance = new WordCounter();
        instance.countWords("...");
        Map<String, Long> counts = instance.getWordCounts();
        assertEquals(0, counts.keySet().size());
    }

    @Test
    public void testCountEmptyString() {
        WordCounter instance = new WordCounter();
        instance.countWords("");
        Map<String, Long> counts = instance.getWordCounts();
        assertEquals(0, counts.keySet().size());
    }

    @Test
    public void testCountNull() {
        WordCounter instance = new WordCounter();
        instance.countWords(null);
        Map<String, Long> counts = instance.getWordCounts();
        assertEquals(0, counts.keySet().size());
    }

    @Test
    public void testGetTopTen() {
        WordCounter instance = new WordCounter();
        instance.countWords("a b c d e f g h i j k l m n o p");
        instance.countWords("h h h h h h h h h h h h h h");
        instance.countWords("j j j j j j j j j j j j j");
        instance.countWords("e e e e e e e e e");
        instance.countWords("b b b b b b b b");
        instance.countWords("k k k k k k k k");
        instance.countWords("m m m m m");
        instance.countWords("n n n n");
        instance.countWords("c c c");
        instance.countWords("d d");
        instance.countWords("l");

        List<WordCount> expected = new ArrayList<WordCount>(3);
        expected.add(new WordCount("h", 15));
        expected.add(new WordCount("j", 14));
        expected.add(new WordCount("e", 10));
        expected.add(new WordCount("k", 9));
        expected.add(new WordCount("b", 9));
        expected.add(new WordCount("m", 6));
        expected.add(new WordCount("n", 5));
        expected.add(new WordCount("c", 4));
        expected.add(new WordCount("d", 3));
        expected.add(new WordCount("l", 2));

        List<WordCount> topTen = instance.getTopTen();
        assertEquals(expected, topTen);
    }

    @Test
    public void testGetTopTenLessThanTen() {
        WordCounter instance = new WordCounter();
        instance.countWords("Alpha beta beta beta gamma gamma");
        List<WordCount> expected = new ArrayList<WordCount>(3);
        expected.add(new WordCount("beta", 3));
        expected.add(new WordCount("gamma", 2));
        expected.add(new WordCount("alpha", 1));
        List<WordCount> topTen = instance.getTopTen();
        assertEquals(expected, topTen);
    }
}
