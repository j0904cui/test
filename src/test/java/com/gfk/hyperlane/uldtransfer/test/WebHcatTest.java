package com.gfk.hyperlane.uldtransfer.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import com.gfk.hyperlane.uldtransfer.ConnectionParameter;
import com.gfk.hyperlane.uldtransfer.RestServiceUtil;

public class WebHcatTest extends AbstractIntegrationTest {

	private final Log LOG = LogFactory.getLog(getClass().getName());

	ConnectionParameter c = connectionParameter();

	@Before
	public void setUp() {
		c = connectionParameter();

	}

	// given openid and user
	@Test
	public void reguser() throws Exception {
		ConnectionParameter c1 = connectionParameter();
		c1.setServicePath("/templeton/v1/ddl/database/production/table");
		userResource = RestServiceUtil.createWebResource(c1);
		Response response = userResource
				.path("uld_de__e5defea7c853232")
				.request().get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
		String output = response.readEntity(String.class);
		LOG.debug("weixinhistory UserContext " + output);

		// return Json.jsonmapper().readValue(output, UserContext.class);

	}

	public void checkResult(String result) throws Exception {

	}

	public ConnectionParameter connectionParameter() {
		ConnectionParameter c = new ConnectionParameter();
		c.setProtocol("http");
		c.setServer("localhost");
		c.setPort(50111);
		c.setServicePath("");
		c.setUser("connectuser@yuandeyun.com");
		c.setPassword("test1234");
		c.setTimeoutMillis(100000);

		return c;
	}

}
