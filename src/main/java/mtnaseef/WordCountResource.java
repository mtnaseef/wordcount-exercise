package mtnaseef;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Root resource (exposed at "wordcount" path)
 */
@Path("wordcount")
public class WordCountResource {
    /** Number of threads to use in the file processing pool */
    public static final int THREADS = 3;
    private ZipFileWordCounter zipFileWordCounter;

    public WordCountResource() {
        // TODO configurable pool size
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        FileWordCounter fileWordCounter = new FileWordCounter(pool);
        zipFileWordCounter = new ZipFileWordCounter(fileWordCounter);
    }

    /**
     * Accept a zip file from a multi-part form POST request and
     * return counts for the 10 most popular words across all files
     * within the zip file.
     * The file must be zip formatted and be represented in a
     * parameter named "file".
     *
     * @return A list of the 10 most counted words in order of count from
     * highest to lowest.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public List<WordCount> countWords(@FormDataParam("file") final InputStream fileData) {
        if (fileData == null) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                    .entity("Request must include a \"file\" multi-part form parameter containing a zip file.")
                    .build());
        }
        WordCounter wordCounter = null;
        try {
            wordCounter = zipFileWordCounter.countZipFileWords(fileData);
        } catch (IOException e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("The file parameter must be present and reference a valid zip file.")
                            .build());
        }
        return wordCounter.getTopTen();
    }
}
