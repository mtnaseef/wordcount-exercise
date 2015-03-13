package mnaseef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Unzip a zip formatted resource, then calculate word counts for
 * all files contained in the zip archive.
 */
public class ZipFileWordCounter {

    private FileWordCounter fileWordCounter;
    private static final int BUFFER_SIZE = 2048;

    /**
     * Create an instance which will use the specified FileWordCounter
     * to process files.
     * @param fileWordCounter FileWordCounter instance to use to process
     *                        all files from the zip file at once.
     */
    public ZipFileWordCounter(final FileWordCounter fileWordCounter) {
        this.fileWordCounter = fileWordCounter;
    }

    /**
     * Process the files in the zip data represented in the given input stream.
     * Counts the words in each file and returns the result.
     * @param fileData an InputStream that must contain zip formatted data.
     * @return a WordCount instance with the results of the operation.
     * @throws IOException If there is an error accessing the input stream.
     * @throws java.util.zip.ZipException if the data is not properly zip formatted.
     */
    public WordCounter countZipFileWords(final InputStream fileData)
            throws IOException {

        final ArrayList<File> fileList = new ArrayList<>();
        final ZipInputStream zipIn = new ZipInputStream(fileData);
        try {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory()) {
                    fileList.add(writeTempFile(zipIn));
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }

            final WordCounter wordCounter = fileWordCounter.countWords(fileList);
            for (File file : fileList) {
                Files.delete(file.toPath());
            }
            return wordCounter;
        } finally {
            zipIn.close();
        }
    }

    private File writeTempFile(final ZipInputStream zipIn)
            throws IOException {
        final byte[] buf = new byte[BUFFER_SIZE];
        // FIXME - this can fill up /tmp - use a configurable temporary file location
        final File tempFile = File.createTempFile("wordcount", null);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
            int sz;
            while ((sz = zipIn.read(buf, 0, BUFFER_SIZE)) != -1) {
                out.write(buf, 0, sz);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return tempFile;
    }
}
