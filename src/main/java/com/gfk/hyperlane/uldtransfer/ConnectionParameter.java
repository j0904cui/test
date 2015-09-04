package com.gfk.hyperlane.uldtransfer;

import java.io.Serializable;

public class ConnectionParameter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String server = "localhost";

	public String servicePath;
	public int port = 8080;
	public String username = "connectuser@yuandeyun.com";
	public String password = "test1234";
	public int timeoutMillis = 1000000;
	public String protocol = "http";
	// following fields are cluster property
	public String clustername;
	public String active;
	public String readonly;
	public Long serviceid;
	public Boolean uiselection = false;

	public static ConnectionParameter copyConnectionParameter(
			ConnectionParameter old) {
		ConnectionParameter c = new ConnectionParameter();
		c.server = old.server;

		c.servicePath = old.servicePath;
		c.port = old.port;
		c.username = old.username;
		c.password = old.password;
		c.timeoutMillis = old.timeoutMillis;
		c.protocol = old.protocol;
		c.active = old.active;
		c.readonly = old.readonly;
		c.clustername = old.clustername;

		return c;
	}

	 
	public String getServicePathURL() {
		return getServerURL() + ":" + port + servicePath;
	}

	public String getServerURL() {
		return protocol + "://" + server;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getServicePath() {
		return servicePath;
	}

	public void setServicePath(String servicePath) {
		this.servicePath = servicePath;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUser() {
		return username;
	}

	public void setUser(String user) {
		this.username = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getTimeoutMillis() {
		return timeoutMillis;
	}

	public void setTimeoutMillis(int timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getClustername() {
		return clustername;
	}

	public void setClustername(String clustername) {
		this.clustername = clustername;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getReadonly() {
		return readonly;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public Long getServiceid() {
		return serviceid;
	}

	public void setServiceid(Long serviceid) {
		this.serviceid = serviceid;
	}

	@Override
	public String toString() {
		return "ConnectionParameter [server=" + server + ", servicePath="
				+ servicePath + ", port=" + port + ", user=" + username
				+ ", password=***" + ", timeoutMillis=" + timeoutMillis
				+ ", protocol=" + protocol + ", clustername=" + clustername
				+ ", active=" + active + ", readonly=" + readonly
				+ ", serviceid=" + serviceid + "]";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getUiselection() {
		return uiselection;
	}

	public void setUiselection(Boolean uiselection) {
		this.uiselection = uiselection;
	}

}
