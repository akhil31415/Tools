package com.ofss.fc.utilities.deploymenttool;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;    

public class ShellExecuter extends DeploymentConstants{

	public int execute(String path, String file) {

		JSch jsch = new JSch();

		Session session;
		String Command="cd "+path +" && "+ "./"+file;
		System.out.println("Executing "+file+" present at path "+path);
		try {

			// Open a Session to remote SSH server and Connect.
			// Set User and IP of the remote host and SSH port.
			session = jsch.getSession(remoteUser, remoteIP, 22);			
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(remotePass);
			session.connect();

			// create the execution channel over the session
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			// Set the command to execute on the channel and execute the command
			channelExec.setCommand(Command);
			channelExec.setInputStream(null);
			channelExec.setErrStream(System.err);          

			InputStream in = channelExec.getInputStream();
			channelExec.connect();

			// Get an InputStream from this channel and read messages, generated 
			// by the executing command, from the remote side.
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {				
				System.out.println("Output: "+line);
			}

			// Command execution completed here.
			reader.close();
			in.close();
			// Retrieve the exit status of the executed command
			int exitStatus = channelExec.getExitStatus();
			if (exitStatus > 0) {
				return exitStatus;
			}
			//Disconnect the Session
			channelExec.disconnect();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return 0;
	}
}