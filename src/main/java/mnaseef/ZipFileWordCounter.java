package mnaseef;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Unzip a zip formatted resource, then calculate word counts for
 * all files contained in the zip archive.
 */
public class ZipFileWordCounter {

    private static final int BUFFER_SIZE = 2048;

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

        final ZipInputStream zipIn = new ZipInputStream(fileData);
        final WordCounter result = new WordCounter();
        try {
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory()) {
                    writeTempFile(zipIn);
                    // TODO start a worker
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            // TODO aggregate worker results
            return result;
        } finally {
            zipIn.close();
        }
    }

    private void writeTempFile(final ZipInputStream zipIn)
            throws IOException {
        final byte[] buf = new byte[BUFFER_SIZE];
        // TODO configurable temporary file location
        final File tempFile = File.createTempFile("wordcount", null);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
            while (zipIn.read(buf, 0, BUFFER_SIZE) != -1) {
                out.write(buf);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
