package mnaseef;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a word and the count for that word.
 */
@XmlRootElement
public class WordCount {
    private String word;
    private long count;

    public WordCount() {}

    public WordCount(String word, long count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
    /**
     * Two WordCount instances are considered equal if the word and count
     * fields are equal.
     * @param o the object to compare to this object
     * @return true if the two instances have the same word and count values.
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof WordCount)) {
            return false;
        }
        WordCount other = (WordCount)o;
        return other.word.equals(word) && other.count == count;
    }

    @Override
    public String toString() {
        return "WordCount: " + word + ":" + count;
    }
}
