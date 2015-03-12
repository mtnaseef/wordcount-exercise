package mnaseef;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;
import java.util.zip.ZipException;

/**
 * Root resource (exposed at "wordcount" path)
 */
@Path("wordcount")
public class WordCountResource {

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
                    .entity("Request must include a \"file\" multipart form parameter referencing a zip file")
                    .build());
        }
        WordCounter wordCounter = null;
        try {
            wordCounter = new ZipFileWordCounter().countZipFileWords(fileData);
        } catch (IOException e) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("The file parameter must be present and reference a valid zip file.")
                            .build());
        }
        return wordCounter.getTopTen();
    }
}
