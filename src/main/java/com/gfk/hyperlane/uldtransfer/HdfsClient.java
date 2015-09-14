package com.gfk.hyperlane.uldtransfer;

/*
 transfer uld data
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;

public class HdfsClient {
	private final Log LOG = LogFactory.getLog(getClass().getName());

	public Configuration conf = new Configuration();
	FileSystem fileSystem;
	public String rootInput;
	public String rootOutput;
 
	public boolean ifExists(Path source) throws IOException {

		FileSystem hdfs = FileSystem.get(conf);
		boolean isExists = hdfs.exists(source);
		return isExists;
	}

	public void getHostnames() throws IOException {

		FileSystem fs = FileSystem.get(conf);
		DistributedFileSystem hdfs = (DistributedFileSystem) fs;
		DatanodeInfo[] dataNodeStats = hdfs.getDataNodeStats();

		String[] names = new String[dataNodeStats.length];
		for (int i = 0; i < dataNodeStats.length; i++) {
			names[i] = dataNodeStats[i].getHostName();
			LOG.debug((dataNodeStats[i].getHostName()));
		}
	}

	public void getBlockLocations(String source) throws IOException {

		
		Path srcPath = new Path(source);

		// Check if the file already exists
		if (!(ifExists(srcPath))) {
			LOG.debug("No such destination " + srcPath);
			return;
		}
		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		FileStatus fileStatus = fileSystem.getFileStatus(srcPath);

		BlockLocation[] blkLocations = fileSystem.getFileBlockLocations(
				fileStatus, 0, fileStatus.getLen());
		int blkCount = blkLocations.length;

		LOG.debug("File :" + filename + "stored at:");
		for (int i = 0; i < blkCount; i++) {
			String[] hosts = blkLocations[i].getHosts();
			System.out.format("Host %d: %s %n", i, hosts);
		}

	}

	public void getModificationTime(String source) throws IOException {

		
		Path srcPath = new Path(source);

		// Check if the file already exists
		if (!(fileSystem.exists(srcPath))) {
			LOG.debug("No such destination " + srcPath);
			return;
		}
		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		FileStatus fileStatus = fileSystem.getFileStatus(srcPath);
		long modificationTime = fileStatus.getModificationTime();

		System.out.format("File %s; Modification time : %0.2f %n", filename,
				modificationTime);

	}

	/*
	 * import to hdfs the target is defined as meter_type_name/slice_start,
	 * slice_year, slice_month, slice_day,
	 */
	public void copyFromLocal() throws IOException {

	}

	public void copyFromLocal(String source, String dest) throws IOException {

		
		Path srcPath = new Path(source);

		Path dstPath = new Path(dest);
		// Check if the file already exists
		if (!(fileSystem.exists(dstPath))) {
			LOG.debug("No such destination " + dstPath);
			return;
		}

		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		fileSystem.copyFromLocalFile(srcPath, dstPath);
		LOG.debug("File " + filename + "copied to " + dest);

	}

	/*
	 * export the uld to local file for transfer the source directory is
	 * meter_type_name=[meter_type_name]/ type_name=[type_name]/
	 * start_date=[yyyy-MM-dd]/ transaction_timestamp=[transaction_timestamp]
	 * the target is defined as meter_type_name/slice_start, slice_year,
	 * slice_month, slice_day,
	 */
	public void copyToLocal() throws IOException {

		
		try {
			FileStatus[] meter_type_name = fileSystem.listStatus(new Path(
					rootInput));
			for (int i = 0; i < meter_type_name.length; i++) {
				// LOG.debug("File " + meter_type_name[i] + "");
				if (meter_type_name[i].isDirectory()) {
					FileStatus[] type_name = fileSystem
							.listStatus(meter_type_name[i].getPath());
					for (int j = 0; j < type_name.length; j++) {
						if (type_name[j].isDirectory()) {
							FileStatus[] start_date = fileSystem
									.listStatus(type_name[j].getPath());
							for (int k = 0; k < start_date.length; k++) {
								if (start_date[k].isDirectory()) {
									FileStatus[] transaction_timestamp = fileSystem
											.listStatus(start_date[k].getPath());
									for (int h = 0; h < transaction_timestamp.length; h++) {
										if (transaction_timestamp[h]
												.isDirectory()) {
											FileStatus[] lo = fileSystem
													.listStatus(transaction_timestamp[h]
															.getPath());
											copyToLocal(lo[0], start_date[k]);
										} else {
											copyToLocal(
													transaction_timestamp[h],
													start_date[k]);
										}
									}
								}
							}
						}
					}
				}
				;
			}
		} finally {
			fileSystem.close();
		}
	}

	private void copyToLocal(FileStatus source, FileStatus start_date)
			throws IOException {
		String d = rootOutput + "/" + start_date.getPath().getName();
		File local = new File(d);
		if (!local.exists()) {
			local.mkdirs();
		}
		
		Path targetDir = new Path(d);
		if (source.isDirectory()) {
			FileStatus[] l = fileSystem.listStatus(source.getPath());
			for (int h = 0; h < l.length; h++) {
				copyToLocal(l[h].getPath(), targetDir);
			}
		} else {
			copyToLocal(source.getPath(), targetDir);
		}
	}

	public void copyToLocal(Path srcPath, Path dstPath) throws IOException {

		

		// Check if the file already exists
		if (!(fileSystem.exists(srcPath))) {
			LOG.debug("No such destination " + srcPath);
			return;
		}

		fileSystem.copyToLocalFile(srcPath, dstPath);
		LOG.debug("File " + srcPath + "copied to " + dstPath);

	}

	public void renameFile(String fromthis, String tothis) throws IOException {

		
		Path fromPath = new Path(fromthis);
		Path toPath = new Path(tothis);

		if (!(fileSystem.exists(fromPath))) {
			LOG.debug("No such destination " + fromPath);
			return;
		}

		if (fileSystem.exists(toPath)) {
			LOG.debug("Already exists! " + toPath);
			return;
		}

		boolean isRenamed = fileSystem.rename(fromPath, toPath);
		if (isRenamed) {
			LOG.debug("Renamed from " + fromthis + "to " + tothis);
		}

	}

	public void addFile(String source, String dest) throws IOException {

		

		// Get the filename out of the file path
		String filename = source.substring(source.lastIndexOf('/') + 1,
				source.length());

		// Create the destination path including the filename.
		if (dest.charAt(dest.length() - 1) != '/') {
			dest = dest + "/" + filename;
		} else {
			dest = dest + filename;
		}

		// Check if the file already exists
		Path path = new Path(dest);
		if (fileSystem.exists(path)) {
			LOG.debug("File " + dest + " already exists");
			return;
		}

		// Create a new file and write data to it.
		FSDataOutputStream out = fileSystem.create(path);
		InputStream in = new BufferedInputStream(new FileInputStream(new File(
				source)));

		byte[] b = new byte[1024];
		int numBytes = 0;
		while ((numBytes = in.read(b)) > 0) {
			out.write(b, 0, numBytes);
		}

		// Close all the file descripters
		in.close();
		out.close();
	 
	}

	public void readDir(String file) throws IOException {
		
		FileStatus[] dir = fileSystem.listStatus(new Path(file));
		for (int i = 0; i < dir.length; i++) {
			LOG.debug("File " + dir[i] + " does not exists");
		}
	}

	public void setConf() throws IOException {

		conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		conf.addResource(new Path("/etc/hadoop/conf/mapred-site.xml"));
		fileSystem = FileSystem.get(conf);
	}

	public void readFile(String file) throws IOException { 

		Path path = new Path(file);
		if (!fileSystem.exists(path)) {
			LOG.debug("File " + file + " does not exists");
			return;
		}

		FSDataInputStream in = fileSystem.open(path);

		String filename = file.substring(file.lastIndexOf('/') + 1,
				file.length());

		OutputStream out = new BufferedOutputStream(new FileOutputStream(
				new File(filename)));

		byte[] b = new byte[1024];
		int numBytes = 0;
		while ((numBytes = in.read(b)) > 0) {
			out.write(b, 0, numBytes);
		}

		in.close();
		out.close();
	 
	}

	public void deleteFile(String file) throws IOException { 

		Path path = new Path(file);
		if (!fileSystem.exists(path)) {
			LOG.debug("File " + file + " does not exists");
			return;
		}

		fileSystem.delete(new Path(file), true);

	 
	}

	public void mkdir(String dir) throws IOException { 

		Path path = new Path(dir);
		if (fileSystem.exists(path)) {
			LOG.debug("Dir " + dir + " already exists!");
			return;
		}

		fileSystem.mkdirs(path);
 
	}

}
