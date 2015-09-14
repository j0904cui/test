package com.gfk.hyperlane.uldtransfer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.codehaus.plexus.util.cli.Commandline;

public class DataTransfer {
 


	public static void checkMd5sum(String m1, String m2) throws Exception {

	}

	public static String shellExecuteLocal(List<String> remoteCommandList)
			throws Exception {
		StringWriter re = new StringWriter();
		for (String s : remoteCommandList) {
			re.append(shellExecuteLocal(s));
		}
		return re.toString();
	}

	public static String shellExecuteLocal(String remoteCommand)
			throws Exception {
		// private final Log LOG = LogFactory.getLog(getClass().getName());

		// using the command
		// LOG.debug(" Commandline cmd " + remoteCommand);

		Commandline cmd = new Commandline();
		cmd.setExecutable(remoteCommand);
		StringWriter swriter = new StringWriter();
		Process process = cmd.execute();
		// Use Async for normal process.waitFor();
		Reader reader = new InputStreamReader(process.getInputStream());

		char[] chars = new char[16];
		int read = -1;
		while ((read = reader.read(chars)) > -1) {
			swriter.write(chars, 0, read);
		}
		// LOG.debug("   Commandline cmd output " + swriter.toString().trim());
		// LOG.debug(" Finish Commandline cmd " + remoteCommand);

		return swriter.toString().trim();
	}

 
	public static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return encoding.decode(ByteBuffer.wrap(encoded)).toString();
	}

	public static void main(String[] args) throws  Exception {
		
		DataTransfer aDataTransfer= new DataTransfer();
		String rootInput = "/user/cui/uld_id__e5defea7c853232";
		String rootOutput = "/var/cui";
		String remoteCommand= " rsync -avz "+ rootInput + rootOutput;
		DataTransfer.shellExecuteLocal(remoteCommand);
			System.exit(1);
		 
	}
}
