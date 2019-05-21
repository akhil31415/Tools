package com.ofss.fc.utilities.deploytool;
import java.util.ArrayList;    

public class ShellExecuter extends DeploymentConstants{

	public int execute(String path, String file) {

		String command="cd "+path +" && "+ "./"+file;
		System.out.println("Executing "+file+" present at path "+path);
		
		RemoteHostConnection remoteSession= new RemoteHostConnection();
		remoteSession.initSession();

		ArrayList<Object> output=remoteSession.runCommand(command);
		if(0!=(int)output.get(0)) {
			System.out.println("Command failed: "+command+"\nReturned "+output.get(1));
			System.exit(1);
		}
		remoteSession.disConnectSession();	
		
		return 0;
	}
}