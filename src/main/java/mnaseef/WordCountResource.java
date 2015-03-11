package mnaseef;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Root resource (exposed at "wordcount" path)
 */
@Path("wordcount")
public class WordCountResource {

    /**
     * Accept a file parameter named file from a multipart form POST request.
     *
     * @return String that will be returned as a text/plain response.
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA )
//    @Produces(MediaType.TEXT_PLAIN)
    public String getIt(@FormDataParam("file") final InputStream fileData) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(fileData));
            return in.readLine();
        } catch (IOException e) {
            return "Got an IOException " + e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
