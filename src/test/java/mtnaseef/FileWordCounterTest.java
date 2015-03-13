package mtnaseef;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class FileWordCounterTest {
    private static File testFile;
    private static File testFile2;
    private static ExecutorService poolOfOne;
    private static ExecutorService poolOfTwo;
    @BeforeClass
    public static void setup() throws IOException {
        testFile = File.createTempFile("wordcount1", "txt");
        FileWriter out = new FileWriter(testFile);
        out.write("alpha beta gamma\nline 2\n");
        out.close();
        testFile2 = File.createTempFile("wordcount2", "txt");
        out = new FileWriter(testFile2);
        out.write("alpha delta\n");
        out.close();
        poolOfOne = Executors.newFixedThreadPool(1);
        poolOfTwo = Executors.newFixedThreadPool(2);
    }

    @AfterClass
    public static void cleanup() throws IOException {
        poolOfOne.shutdownNow();
        poolOfTwo.shutdownNow();
        Files.delete(testFile.toPath());
        Files.delete(testFile2.toPath());
    }

    @Test
    public void testCountWords() {
        FileWordCounter instance = new FileWordCounter(poolOfOne);
        ArrayList<File> files = new ArrayList<>(1);
        files.add(testFile);

        WordCounter wordCounter = instance.countWords(files);

        List<WordCount> expected = new ArrayList<>(5);
        expected.add(new WordCount("line", 1));
        expected.add(new WordCount("gamma", 1));
        expected.add(new WordCount("beta", 1));
        expected.add(new WordCount("alpha", 1));
        expected.add(new WordCount("2", 1));

        assertEquals(expected, wordCounter.getTopTen());
    }

    @Test
    public void testCountWordsTwoFiles() {
        FileWordCounter instance = new FileWordCounter(poolOfOne);
        ArrayList<File> files = new ArrayList<>(1);
        files.add(testFile);
        files.add(testFile2);

        WordCounter wordCounter = instance.countWords(files);

        List<WordCount> expected = new ArrayList<>(5);
        expected.add(new WordCount("alpha", 2));
        expected.add(new WordCount("line", 1));
        expected.add(new WordCount("gamma", 1));
        expected.add(new WordCount("delta", 1));
        expected.add(new WordCount("beta", 1));
        expected.add(new WordCount("2", 1));

        assertEquals(expected, wordCounter.getTopTen());
    }

    @Test
    public void testCountWordsTwoFilesConcurrent() {
        FileWordCounter instance = new FileWordCounter(poolOfTwo);
        ArrayList<File> files = new ArrayList<>(1);
        files.add(testFile);
        files.add(testFile2);

        WordCounter wordCounter = instance.countWords(files);

        List<WordCount> expected = new ArrayList<>(5);
        expected.add(new WordCount("alpha", 2));
        expected.add(new WordCount("line", 1));
        expected.add(new WordCount("gamma", 1));
        expected.add(new WordCount("delta", 1));
        expected.add(new WordCount("beta", 1));
        expected.add(new WordCount("2", 1));

        assertEquals(expected, wordCounter.getTopTen());
    }
}
