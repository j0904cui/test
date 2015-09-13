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

	public HdfsClient() {

	}

	Configuration conf = new Configuration();
	String rootInput;
	String rootOutput;

	public static void printUsage() {
		System.out
				.println("Usage: hdfsclient add" + "<local_path> <hdfs_path>");
		System.out.println("Usage: hdfsclient read" + "<hdfs_path>");
		System.out.println("Usage: hdfsclient delete" + "<hdfs_path>");
		System.out.println("Usage: hdfsclient mkdir" + "<hdfs_path>");
		System.out.println("Usage: hdfsclient copyfromlocal"
				+ "<local_path> <hdfs_path>");
		System.out.println("Usage: hdfsclient copytolocal"
				+ " <hdfs_path> <local_path> ");
		System.out
				.println("Usage: hdfsclient modificationtime" + "<hdfs_path>");
		System.out.println("Usage: hdfsclient getblocklocations"
				+ "<hdfs_path>");
		System.out.println("Usage: hdfsclient gethostnames");
	}

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

		FileSystem fileSystem = FileSystem.get(conf);
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
		;

		FileSystem fileSystem = FileSystem.get(conf);
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

		FileSystem fileSystem = FileSystem.get(conf);
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

		try {
			fileSystem.copyFromLocalFile(srcPath, dstPath);
			LOG.debug("File " + filename + "copied to " + dest);
		} catch (Exception e) {
			System.err.println("Exception caught! :" + e);
			System.exit(1);
		} finally {
			fileSystem.close();
		}
	}

	/*
	 * export the uld to local file for transfer the source directory is
	 * meter_type_name=[meter_type_name]/ type_name=[type_name]/
	 * start_date=[yyyy-MM-dd]/ transaction_timestamp=[transaction_timestamp]
	 * the target is defined as meter_type_name/slice_start, slice_year,
	 * slice_month, slice_day,
	 */
	public void copyToLocal() throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);
		FileStatus[] meter_type_name = fileSystem
				.listStatus(new Path(rootInput));
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
									copyToLocal(transaction_timestamp[h],
											start_date[k]);
								}
							}
						}
					}
				}
			}
			;
		}

	}

	private void copyToLocal(FileStatus source, FileStatus start_date)
			throws IOException {
		
		Path targetDir = new Path(rootOutput+ "/"+start_date.getPath().getName());
		copyToLocal(source.getPath(), targetDir );
	}

	public void copyToLocal(Path srcPath, Path dstPath) throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);

		// Check if the file already exists
		if (!(fileSystem.exists(srcPath))) {
			LOG.debug("No such destination " + srcPath);
			return;
		}

		try {
			fileSystem.copyToLocalFile(srcPath, dstPath);
			LOG.debug("File " + srcPath + "copied to " + dstPath);
		} catch (Exception e) {
			System.err.println("Exception caught! :" + e);
			System.exit(1);
		} finally {
			fileSystem.close();
		}
	}

	public void renameFile(String fromthis, String tothis) throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);
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

		try {
			boolean isRenamed = fileSystem.rename(fromPath, toPath);
			if (isRenamed) {
				LOG.debug("Renamed from " + fromthis + "to " + tothis);
			}
		} catch (Exception e) {
			LOG.debug("Exception :" + e);
			System.exit(1);
		} finally {
			fileSystem.close();
		}

	}

	public void addFile(String source, String dest) throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);

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
		fileSystem.close();
	}

	public void readDir(String file) throws IOException {
		FileSystem fileSystem = FileSystem.get(conf);
		FileStatus[] dir = fileSystem.listStatus(new Path(file));
		for (int i = 0; i < dir.length; i++) {
			LOG.debug("File " + dir[i] + " does not exists");
		}
	}

	public void setConf() throws IOException {

		conf.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		conf.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		conf.addResource(new Path("/etc/hadoop/conf/mapred-site.xml"));

	}

	public void readFile(String file) throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);

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
		fileSystem.close();
	}

	public void deleteFile(String file) throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(file);
		if (!fileSystem.exists(path)) {
			LOG.debug("File " + file + " does not exists");
			return;
		}

		fileSystem.delete(new Path(file), true);

		fileSystem.close();
	}

	public void mkdir(String dir) throws IOException {

		FileSystem fileSystem = FileSystem.get(conf);

		Path path = new Path(dir);
		if (fileSystem.exists(path)) {
			LOG.debug("Dir " + dir + " already exists!");
			return;
		}

		fileSystem.mkdirs(path);

		fileSystem.close();
	}

	public static void main(String[] args) throws IOException {
		HdfsClient client = new HdfsClient();
		client.setConf();
		if (args.length < 1) {
			// printUsage();
			client.rootInput="/user/cui";
			client.rootInput="/var/cui";
			client.readDir("");
			System.exit(1);
		}

		if (args[0].equals("add")) {
			if (args.length < 3) {
				System.out.println("Usage: hdfsclient add <local_path> "
						+ "<hdfs_path>");
				System.exit(1);
			}
			client.addFile(args[1], args[2]);

		} else if (args[0].equals("read")) {
			if (args.length < 2) {
				System.out.println("Usage: hdfsclient read <hdfs_path>");
				System.exit(1);
			}
			client.readFile(args[1]);

		} else if (args[0].equals("delete")) {
			if (args.length < 2) {
				System.out.println("Usage: hdfsclient delete <hdfs_path>");
				System.exit(1);
			}

			client.deleteFile(args[1]);
		} else if (args[0].equals("mkdir")) {
			if (args.length < 2) {
				System.out.println("Usage: hdfsclient mkdir <hdfs_path>");
				System.exit(1);
			}

			client.mkdir(args[1]);
		} else if (args[0].equals("copyfromlocal")) {
			if (args.length < 3) {
				System.out
						.println("Usage: hdfsclient copyfromlocal <from_local_path> <to_hdfs_path>");
				System.exit(1);
			}

			client.copyFromLocal(args[1], args[2]);
		} else if (args[0].equals("rename")) {
			if (args.length < 3) {
				System.out
						.println("Usage: hdfsclient rename <old_hdfs_path> <new_hdfs_path>");
				System.exit(1);
			}

			client.renameFile(args[1], args[2]);
		} else if (args[0].equals("copytolocal")) {
			if (args.length < 3) {
				System.out
						.println("Usage: hdfsclient copytolocal <from_hdfs_path> <to_local_path>");
				System.exit(1);
			}

			client.copyToLocal(new Path(args[1]), new Path(args[2]));
		} else if (args[0].equals("modificationtime")) {
			if (args.length < 2) {
				System.out
						.println("Usage: hdfsclient modificationtime <hdfs_path>");
				System.exit(1);
			}

			client.getModificationTime(args[1]);
		} else if (args[0].equals("getblocklocations")) {
			if (args.length < 2) {
				System.out
						.println("Usage: hdfsclient getblocklocations <hdfs_path>");
				System.exit(1);
			}

			client.getBlockLocations(args[1]);
		} else if (args[0].equals("gethostnames")) {

			client.getHostnames();
		} else {

			printUsage();
			System.exit(1);
		}

		System.out.println("Done!");
	}
}
