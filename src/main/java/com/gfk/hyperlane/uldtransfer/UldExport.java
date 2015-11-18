package com.gfk.hyperlane.uldtransfer;

import java.io.IOException;

public class UldExport extends HdfsClient {

	public static void main(String[] args) throws IOException {
		UldExport client = new UldExport();
		client.setConf("D:\\Users\\jcui19\\git\\testsqoop\\src\\hadoopconf\\");

		// printUsage();
		client.rootInput = "/user/cui/uld_id__e5defea7c853232";
		client.rootOutput = "/var/cui";
		client.copyToLocal();
		System.exit(1);
	}

}
