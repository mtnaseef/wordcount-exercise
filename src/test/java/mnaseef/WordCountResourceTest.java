package mnaseef;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.grizzly.http.server.HttpServer;

import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import static org.junit.Assert.assertEquals;

public class WordCountResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client with multi-part form support
        final Client c = ClientBuilder.newClient()
                .register(MultiPartFeature.class);

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    /**
     * Test to see that the message is sent in the response.
     */
    @Test
    public void testGetIt() {
        InputStream data = new ByteArrayInputStream("A test line".getBytes());
        FormDataMultiPart mp = new FormDataMultiPart();
        mp.bodyPart(new FormDataBodyPart(
                FormDataContentDisposition.name("file").fileName("fakefile").build(),
                data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE
        ));
        Entity<FormDataMultiPart> postData =
                Entity.entity(mp, MediaType.MULTIPART_FORM_DATA_TYPE);
        String responseMsg =
                target.path("wordcount").request().post(postData, String.class);
        assertEquals("A test line", responseMsg);
    }
}
