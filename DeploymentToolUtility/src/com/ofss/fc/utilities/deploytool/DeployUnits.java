package com.ofss.fc.utilities.deploytool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DeployUnits extends DeploymentConstants {

	public static int deployShellJava(String file, String relsheetpath, String spath, String tpath) throws Exception {
		try {
			String fullFilePath = relsheetpath + "\\" + spath + "\\" + file;
			// create command to copy shell and java units to remote deployment server
			String copyCommand = "pscp -pw " + remotePass + " \"" + fullFilePath + "\" " + remoteUser + "@" + remoteIP
					+ ":" + tpath;
			Process runtime = Runtime.getRuntime().exec(CMDCOM + copyCommand);

			BufferedReader reader = new BufferedReader(new InputStreamReader(runtime.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			int retCode = runtime.waitFor();
			if (retCode != 0) {
				System.out.println("\nError: Failed while copying file " + file);
				retval = 1;
				return retval;
			} else
				System.out.println(file + " deployed at path " + tpath);

			// provide permission to shell files
			if (file.endsWith(".sh")) {
				permissionToshell(file, tpath);
				// checkLineFeed(file, tpath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return retval;
	}

	public static int deployDB(File sqlFile) throws Exception {

		try {
			String filePath = sqlFile.getAbsolutePath();
			String command = "SQLPLUS " + dBUser + "/" + dBPass + "@" + dBserviceName + " @" + filePath;

			Process runtime = Runtime.getRuntime().exec(CMDCOM + command);

			BufferedReader reader = new BufferedReader(new InputStreamReader(runtime.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			int retCode = runtime.waitFor();
			if (retCode != 0) {
				System.out.println(ERRORSTR + "Failed while deploying DB file " + sqlFile.getName());
				retval = 1;
				return retval;
			} else
				System.out.println("\nDB units deployment completed!");

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			throw e;
		}
		return retval;
	}

	public static void permissionToshell(String file, String path) throws Exception {

		String command = "cd " + path + " && " + "chmod 777 " + file;
		System.out.println("Granting permission to " + file + " present at path " + path);

		RemoteHostConnection remoteSession = new RemoteHostConnection();
		remoteSession.initSession();

		ArrayList<Object> output = remoteSession.runCommand(command);
		if (0 != (int) output.get(0)) {
			System.out.println(ERRORSTR + "Command " + command + " Failed.\nReturned " + output.get(1));
		}
		remoteSession.disConnectSession();
	}

	/*
	 * public static void checkLineFeed(String file, String path) {
	 * 
	 * String command="file "+path+"/" +file +" | grep CRLF";
	 * System.out.println("Checking linefeed of "+file+" present at path "+path);
	 * 
	 * RemoteHostConnection remoteSession= new RemoteHostConnection();
	 * remoteSession.initSession();
	 * 
	 * ArrayList<Object> output=remoteSession.runCommand(command); //GREP returns 0
	 * if found match and non zero if not found if(0==(int)output.get(0)) {
	 * System.out.println(ERRORSTR+file+" has CRLF line terminators!"); } else {
	 * System.out.println("File "+file+" has LF line terminators!"); }
	 * remoteSession.disConnectSession(); }
	 */
}