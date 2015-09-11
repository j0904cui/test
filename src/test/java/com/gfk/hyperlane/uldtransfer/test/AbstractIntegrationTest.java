package com.gfk.hyperlane.uldtransfer.test;

import java.io.File;
import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.gfk.hyperlane.uldtransfer.ConnectionParameter;

public abstract class AbstractIntegrationTest {

	 

	public WebTarget userResource;

	public ConnectionParameter connectionParameter() {
		ConnectionParameter c = new ConnectionParameter();
		c.setProtocol("https");
		c.setServer("localhost");
		c.setPort(8443);
		c.setServicePath("/snservice/api/");
		c.setUser("connectuser@yuandeyun.com");
		c.setPassword("test1234");
		c.setTimeoutMillis(100000);

		return c;
	}

	protected void uploadFile(String resource, String api) throws Exception {
		final URL url = getClass().getResource(resource);
		final File file = new File(url.toURI());
		 uploadFile(file,api);

	}

	public void uploadFile(File file,String api) throws Exception {
		 MultiPart multiPart = new MultiPart();
		    multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
		 FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
				 file,
		            MediaType.MULTIPART_FORM_DATA_TYPE);
		    multiPart.bodyPart(fileDataBodyPart);

		    Response response = userResource.path(api).request(MediaType.APPLICATION_JSON_TYPE)
		            .post(Entity.entity(multiPart, multiPart.getMediaType()));

		    System.out.println(response.getStatus() + " "
		            + response.getStatus() + " " + response);
		    
		 
  

	}
}
