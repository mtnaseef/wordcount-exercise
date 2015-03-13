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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

    @Test
    public void testCountWords() throws IOException {
        InputStream data = new FileInputStream("testdata/test.zip");
        FormDataMultiPart mp = new FormDataMultiPart();
        mp.bodyPart(new FormDataBodyPart(
                FormDataContentDisposition.name("file").fileName("test.zip").build(),
                data,
                MediaType.APPLICATION_OCTET_STREAM_TYPE
        ));
        Entity<FormDataMultiPart> postData =
                Entity.entity(mp, MediaType.MULTIPART_FORM_DATA_TYPE);
        String responseMsg =
                target.path("wordcount").request().post(postData, String.class);
        assertEquals(
                "[{\"count\":5,\"word\":\"this\"},{\"count\":5,\"word\":\"test\"},{\"count\":4,\"word\":\"is\"},{\"count\":2,\"word\":\"a\"},{\"count\":1,\"word\":\"the\"},{\"count\":1,\"word\":\"testing\"},{\"count\":1,\"word\":\"please\"},{\"count\":1,\"word\":\"only\"},{\"count\":1,\"word\":\"me\"},{\"count\":1,\"word\":\"how\"}]",
                responseMsg);
        data.close();
    }
}
