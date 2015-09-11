package com.gfk.hyperlane.uldtransfer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpClientConnect {

	private final Log LOG = LogFactory.getLog(getClass().getName());
	public static HashMap<ConnectionParameter, BasicCookieStore> HttpClientCookies = new HashMap<ConnectionParameter, BasicCookieStore>();

	public static HttpClient getHttpClient(
			ConnectionParameter connectionParameter) {
		return getHttpClientBasic(connectionParameter);
	}

	public static HttpClient getHttpClientParameter(
			ConnectionParameter connectionParameter) {
		if (connectionParameter.getServer() == null) {
			throw new IllegalArgumentException("host name cannot be null");
		}
		SSLContext sslContext;
		try {
			sslContext = SSLContexts.custom()
					.loadTrustMaterial(null, new TrustSelfSignedStrategy())
					.useTLS().build();
		} catch (Exception e) {

			throw new IllegalArgumentException(e);
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));

		BasicCookieStore cookieStore = HttpClientCookies
				.get(connectionParameter);
		if (cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				// .set(new UrlEncodedFormEntity(urlParameters))
				.setDefaultCookieStore(cookieStore).build();
		return httpclient;

	}

	public static HttpClient getHttpClientBasic(
			ConnectionParameter connectionParameter) {
		if (connectionParameter.getServer() == null) {
			throw new IllegalArgumentException("host name cannot be null");
		}
		SSLContext sslContext;
		try {
			sslContext = SSLContexts.custom()
					.loadTrustMaterial(null, new TrustSelfSignedStrategy())
					.useTLS().build();
		} catch (Exception e) {

			throw new IllegalArgumentException(e);
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
				new AuthScope(connectionParameter.getServer(),
						connectionParameter.getPort()),
				new UsernamePasswordCredentials(connectionParameter.getUser(),
						connectionParameter.getPassword()));
		BasicCookieStore cookieStore = HttpClientCookies
				.get(connectionParameter);
		if (cookieStore == null) {
			cookieStore = new BasicCookieStore();
		}
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.setDefaultCredentialsProvider(credsProvider)
				.setDefaultCookieStore(cookieStore).build();
		return httpclient;

	}

	/*
	 * type = "json" or "xml"
	 */
	public String execute(ConnectionParameter connectionParameter, String type,
			String content) throws ClientProtocolException, IOException {
		CloseableHttpResponse re;
		LOG.debug("context : " + content);
		CloseableHttpClient httpclient = null;
		String login_success = "";
		try {
			// String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);
			HttpPost httppost = new HttpPost(
					connectionParameter.getServicePathURL());

			if ("xml".equalsIgnoreCase(type)) {
				httppost.addHeader(HTTP.CONTENT_TYPE, "text/" + type);
			} else {
				httppost.addHeader(HTTP.CONTENT_TYPE, "application/" + type);
			}

			StringEntity se = new StringEntity(content, "UTF-8");
			se.setContentType("text/" + type);

			if ("xml".equalsIgnoreCase(type)) {
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"text/" + type));
			} else {
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/" + type));
			}
			httppost.setEntity(se);
			httpclient = (CloseableHttpClient) getHttpClient(connectionParameter);
			re = (CloseableHttpResponse) httpclient.execute(httppost);
			HttpEntity entity = re.getEntity();
			if (entity != null) {
				login_success = EntityUtils.toString(entity, "UTF-8");
			}

		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (Exception e) {
				// NO Exception
				// e.printStackTrace();
			}
		}
		return login_success;
	}

	/*
	 *  
	 */
	public String execute(ConnectionParameter connectionParameter,
			List<NameValuePair> params) throws ClientProtocolException,
			IOException {
		CloseableHttpResponse re;

		CloseableHttpClient httpclient = null;
		String login_success = "";

		try {

			HttpPost httppost = new HttpPost(
					connectionParameter.getServicePathURL());
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			LOG.debug("executing request " + httppost.getURI());
			// 提交登录数据
			httpclient = (CloseableHttpClient) getHttpClient(connectionParameter);
			re = httpclient.execute(httppost);

			HttpEntity entity = re.getEntity();
			try {
				if (entity != null) {
					System.out
							.println("--------------------------------------");
					login_success = EntityUtils.toString(entity, "UTF-8");
					LOG.debug("Response content: " + login_success);

					System.out
							.println("--------------------------------------");
				}
			} finally {
				re.close();
			}
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return login_success;

	}

	public static HttpClient getHttpClientFormLogin(
			ConnectionParameter connectionParameter) {
		if (connectionParameter.getServer() == null) {
			throw new IllegalArgumentException("host name cannot be null");
		}
		SSLContext sslContext;
		try {
			sslContext = SSLContexts.custom()
					.loadTrustMaterial(null, new TrustSelfSignedStrategy())
					.useTLS().build();
		} catch (Exception e) {

			throw new IllegalArgumentException(e);
		}

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslContext,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		BasicCookieStore cookieStore = HttpClientCookies
				.get(connectionParameter);
		if (cookieStore != null) {

			CloseableHttpClient httpclient = HttpClients.custom()
					.setSSLSocketFactory(sslsf)
					.setDefaultCookieStore(cookieStore).build();
			return httpclient;
		} else {
			cookieStore = new BasicCookieStore();
			CloseableHttpClient httpclient = HttpClients.custom()
					.setSSLSocketFactory(sslsf)
					.setDefaultCookieStore(cookieStore).build();
			HttpPost httpost = new HttpPost(
					"https://www.yuandeyun.com/cas/login?service=https%3A%2F%2Fwww.yuandeyun.com%3A443%2Fj_spring_cas_security_check");
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", connectionParameter
					.getUser()));
			nvps.add(new BasicNameValuePair("password", connectionParameter
					.getPassword()));

			httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
			CloseableHttpResponse response2 = null;
			try {
				response2 = httpclient.execute(httpost);
				HttpEntity entity = response2.getEntity();

				// LOG.debug("Login form get: " + response2.getStatusLine());
				EntityUtils.consume(entity);

				// LOG.debug("Post logon cookies:");
				List<Cookie> cookies = cookieStore.getCookies();
				if (cookies.isEmpty()) {
					// LOG.debug("None");
				} else {
					for (int i = 0; i < cookies.size(); i++) {
						// LOG.debug("- " + cookies.get(i).toString());
					}
				}
			} catch (Exception e) {

			} finally {
				try {
					response2.close();
				} catch (Exception e) {

				}
			}
			HttpClientCookies.put(connectionParameter, cookieStore);
			return httpclient;
		}
	}
}
