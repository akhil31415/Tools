package com.ofss.fc.utilities.deploytool;

import java.util.ArrayList;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class TestingClass extends DeploymentConstants{

	public static void main(String args[]) {

		String path="/opt/fcbhost/datamigration";
		String file="startJBKDM_JTE0013.sh";
		String command="cd "+path +" && "+ "./"+file;
		AnsiConsole.systemInstall();
		
		System.out.println(ansi().fg(RED).a("Executing "+file+" present at path "+path).reset());	
		System.out.println("LOLWA");
		
		RemoteHostConnection remoteSession= new RemoteHostConnection();
		remoteSession.initSession();

		ArrayList<Object> output=remoteSession.runCommand(command);
		if(0!=(int)output.get(0)) {
			System.out.println("Command failed: "+command+"\nReturned "+output.get(1));
			System.exit(1);
		}
		remoteSession.disConnectSession();	
	}
}