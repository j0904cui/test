package com.gfk.hyperlane.uldtransfer;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.apache.oozie.client.WorkflowJob;

public class UldImport extends HdfsClient {

	private final Log LOG = LogFactory.getLog(getClass().getName());

	public static void main(String[] args) throws IOException,
			OozieClientException, InterruptedException {
		UldImport client = new UldImport();
		client.setConf();

		// printUsage();
		client.rootInput = "/var/cui";
		client.rootOutput = "/user/cui/uld";
		// client.copyFromLocal();
		client.job(args);
		System.exit(1);
	}

	public void job(String[] args) throws OozieClientException,
			InterruptedException {

		// get a OozieClient for local Oozie
		OozieClient wc = new OozieClient("http://vm-cmv-05:11000/oozie");
		// create a workflow job configuration and set the workflow application
		// path
		Properties conf = wc.createConfiguration();

		/*
		 * # app settings input_root=/user/cui/
		 * 
		 * input_data_path_1=utc_date=2015-01-26
		 * 
		 * input_schema_path_1=/user/cui/uld.avsc
		 * 
		 * input_type=avro
		 * 
		 * 
		 * target_collection=uld_test target_dataset=uld target_version=v1
		 * 
		 * provided_slice_fields=slice_year=2015,slice_month=01,slice_day=26,
		 * slice_start=2015-01-26 max_ratio_invalid_valid=0.0
		 * 
		 * 
		 * # system settings user.name=hdfs
		 * oozie.libpath=/user/hyperlane/share/oozie
		 * /datasets/v1/lib/,/user/hyperlane/engines/generic-import/0.2.6-S
		 * NAPSHOT/lib
		 * oozie.wf.application.path=/user/hyperlane/engines/generic-
		 * import/0.2.6-helmut/workflow.xml
		 */
		// setting workflow parameters
		// conf.setProperty("jobTracker", "foo:9001");
		conf.setProperty(OozieClient.APP_PATH,
				"/user/hyperlane/engines/generic-import/0.2.6-helmut/workflow.xml");

		conf.setProperty("input_root", rootOutput);
		conf.setProperty("input_data_path_1", "start_date=2015-01-26");
		conf.setProperty("input_schema_path_1", "/user/cui/uld.avsc");
		conf.setProperty("input_type", "avro");
		conf.setProperty("target_collection", "uld_test");
		conf.setProperty("target_dataset", "uld");
		conf.setProperty("target_version", "v1");
		conf.setProperty("provided_slice_fields",
				"slice_year=2015,slice_month=01,slice_day=26,slice_start=2015-01-26");

		conf.setProperty("max_ratio_invalid_valid", "0.0");
		conf.setProperty(
				"oozie.libpath",
				"/user/hyperlane/share/oozie/datasets/v1/lib/,/user/hyperlane/engines/generic-import/0.2.6-SNAPSHOT/lib");

		// submit and start the workflow job
		String jobId = wc.run(conf);
		System.out.println("Workflow job submitted" + jobId);

		// wait until the workflow job finishes printing the status every 10
		// seconds
		while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {
			System.out.println("Workflow job running ..."
					+ wc.getJobInfo(jobId));
			Thread.sleep(10 * 1000);
		}

		// print the final status o the workflow job
		// System.out.println("Workflow job completed ...");
		// System.out.println(wf.getJobInfo(jobId));
	}
}
