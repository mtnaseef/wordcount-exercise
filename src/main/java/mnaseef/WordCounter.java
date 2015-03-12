package mnaseef;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Count words in text documents. Maintains state to allow
 * count across multiple documents.
 */
public class WordCounter {
    private Map<String, Long> wordCounts;
    private static final Pattern wordSplit;
    private static final Long ONE = 1L;

    static {
        wordSplit = Pattern.compile("[^a-zA-Z0-9]+");
    }

    public WordCounter() {
        wordCounts = new HashMap<>();
    }

    public void countWords(final CharSequence text) {
        if (text == null || text.length() == 0) {
            return;
        }

        String[] words = wordSplit.split(text);
        for (String w : words) {
            final String word = w.toLowerCase();
            Long c = wordCounts.get(word);
            if (c == null) {
                wordCounts.put(word, ONE);
            } else {
                wordCounts.put(word, c + 1);
            }
        }
    }

    /**
     * Returns a reference to the underlying map of word counts.
     * Note that updates to this class will be reflected in the
     * map returned and vice versa, and such access is not thread safe.
     * TODO - make a copy or make this protected/private
     * @return The map of word counts backing this instance.
     */
    public Map<String, Long> getWordCounts() {
        return wordCounts;
    }

    /**
     * Returns an ordered list of the words and corresponding counts
     * for the 10 words with the highest counts. Note that the list
     * will be shorter than 10 if less than 10 words have been counted.
     * When two words have the same count, the ordering for those words
     * will be reverse String sort order.
     *
     * @return List of top ten words in descending order of count.
     */
    public List<WordCount> getTopTen() {
        // create a map of word:count pairs sorted by count.
        final WordCountComparator comparator = new WordCountComparator();
        final TreeMap<String, Long> sortedCounts = new TreeMap<>(comparator);
        final List<WordCount> result = new ArrayList<>(10);
        sortedCounts.putAll(wordCounts);

        // get the last (highest) 10 entries
        for (int i = 0; i < 10; i++) {
            Map.Entry<String, Long> entry = sortedCounts.pollLastEntry();
            if (entry == null) {
                // no more entries in the map - exit the loop
                break;
            }
            result.add(new WordCount(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    /**
     * Compares two words based on the counts from the word count map.
     * Words with the same count will be ordered alphabetically.
     */
    private class WordCountComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int result = wordCounts.get(o1).compareTo(wordCounts.get(o2));

            // If same count, order by the word.
            if (result == 0) {
                result = o1.compareTo(o2);
            }

            return result;
        }
    }

}
