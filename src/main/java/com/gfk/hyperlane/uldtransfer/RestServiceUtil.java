package com.gfk.hyperlane.uldtransfer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class RestServiceUtil {

	private static final Logger LOG = LoggerFactory
			.getLogger(RestServiceUtil.class);

	public static WebTarget createWebResource(
			ConnectionParameter connectionParameter) {
		ClientConfig clientConfig = new ClientConfig();
		JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
		provider.setMapper(Json.jsonmapper());

		Client client = ClientBuilder.newClient(clientConfig).register(
				MultiPartFeature.class);
		client.register(provider);
		HttpAuthenticationFeature feature = HttpAuthenticationFeature
				.basicBuilder()
				.nonPreemptive()
				.credentials(connectionParameter.getUser(),
						connectionParameter.getPassword()).build();
		client.register(feature);

		// client.addFilter(new GZIPContentEncodingFilter(true));
		return client.target(connectionParameter.getServicePathURL());

	}

	public static Client createClient(final String username,
			final String password) {
		ClientConfig clientConfig = new ClientConfig();

		Client client = ClientBuilder.newClient(clientConfig);
		HttpAuthenticationFeature feature = HttpAuthenticationFeature
				.basicBuilder().nonPreemptive().credentials(username, password)
				.build();
		client.register(feature);
		// client.addFilter(new GZIPContentEncodingFilter(true));
		return client;

	}
}
