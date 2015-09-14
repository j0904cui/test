package com.gfk.hyperlane.uldtransfer;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.Path;

public class UldImport extends HdfsClient {

	private final Log LOG = LogFactory.getLog(getClass().getName());


	public static void main(String[] args) throws IOException {
		UldImport client = new UldImport();
		client.setConf();

		// printUsage();
		client.rootInput = "/var/cui";
		client.rootOutput = "/user/cui/uld"+ System.currentTimeMillis();
		client.copyFromLocal();
		System.exit(1);
	}

}
