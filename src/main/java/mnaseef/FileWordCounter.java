package mnaseef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Class for generating word counts from one or more files.
 * Files are processed in worker threads according to the nature
 * of the ExecutorService passed to the constructor.
 */
public class FileWordCounter {
    private ExecutorService pool;

    /**
     * Create a FileWordCounter with an ExecutorService to manage
     * concurrent processing of files.
     * @param service An ExecutorService to use for processing
     *                individual files concurrently.
     */
    public FileWordCounter(final ExecutorService service) {
        pool = service;
    }

    /**
     * Count the words in the text files specified and returns a WordCounter
     * instance with the aggregated counts from all files.
     * Individual files may be processed concurrently depending on the
     * ExecutorService configured for this instance, in which case this
     * method blocks until all files have been processed.
     * @param files The list of files to process.
     * @return A WordCounter containing aggregated counts for all words from
     * all files. Null if there was an error processing any files or any workers
     * were interrupted.
     */
    public WordCounter countWords(final Collection<File> files) {
        WordCounter result = new WordCounter();
        List<Future<WordCounter>> futures = new ArrayList<>(files.size());
        for (File file : files) {
            Future<WordCounter> future = pool.submit(new FileWorker(file));
            futures.add(future);
        }

        try {
            for (Future<WordCounter> future : futures) {
                WordCounter counter = future.get();
                result.merge(counter);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // We are aborting due to an exception - cancel any outstanding tasks.
        for (Future<WordCounter> future : futures) {
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
        return null;
    }

    /**
     * A worker class that counts the words in a single file and may be
     * executed concurrently with other workers.
     */
    private class FileWorker implements Callable<WordCounter> {
        private File file;
        public FileWorker(final File file) {
            this.file = file;
        }

        public WordCounter call() {
            WordCounter result = new WordCounter();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                String line;
                while ((line = in.readLine()) != null) {
                    result.countWords(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ignored) {}
            }
            return result;
        }
    }
}
