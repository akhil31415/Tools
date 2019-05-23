package com.ofss.fc.utilities.deploytool;

import java.util.ArrayList;

public class ShellExecuter extends DeploymentConstants {

	public void execute(String path, String file) throws Exception {

		String command = "cd " + path + " && " + "./" + file;
		System.out.println("\nExecuting " + file + " present at path " + path);

		RemoteHostConnection remoteSession = new RemoteHostConnection();
		remoteSession.initSession();

		ArrayList<Object> output = remoteSession.runCommand(command);		
		System.out.println("Command: "+command + "\nReturned " + output.get(1));		
		
		remoteSession.disConnectSession();
	}
}