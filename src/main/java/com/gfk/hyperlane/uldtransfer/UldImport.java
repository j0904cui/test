package com.gfk.hyperlane.uldtransfer;

import java.io.IOException;

public class UldImport extends HdfsClient {

	public static void main(String[] args) throws IOException {
		UldImport client = new UldImport();
		client.setConf();

		// printUsage();
		client.rootInput = "/user/cui/uld_id__e5defea7c853232";
		client.rootOutput = "/var/cui";
		client.copyToLocal();
		System.exit(1);
	}

}
