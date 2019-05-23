package com.ofss.fc.utilities.deploytool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class RemoteHostConnection extends DeploymentConstants {

	Session session;

	public void initSession() {

		JSch jsch = new JSch();
		try {
			session = jsch.getSession(remoteUser, remoteIP, 22);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(remotePass);
			session.connect();
		} catch (JSchException e) {
			System.out.println("Error while creating session with remote server :" + remoteIP);
			e.printStackTrace();
		}
	}

	public void disConnectSession() {
		session.disconnect();
	}

	public ArrayList<Object> runCommand(String runCommand) throws Exception {

		StringBuilder outputString = new StringBuilder();
		ArrayList<Object> returnVar = new ArrayList<>();
		String line;
		try {
			// Open channel and set the command to execute on the channel and execute the
			// command
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(runCommand);
			channelExec.setInputStream(null);
			channelExec.setErrStream(System.err);
			channelExec.setOutputStream(System.out);

			InputStream in = channelExec.getInputStream();
			channelExec.connect();

			// sleep for 3 seconds while waiting for command to execute, checking every 100
			// milliseconds
			int sleepCount = 0;
			do {
				Thread.sleep(100);
			} while (!channelExec.isClosed() && sleepCount++ < 50);

			// Retrieve the exit status of the executed command
			int exitStatus = channelExec.getExitStatus();			

			// Get an InputStream from this channel and read messages, generated
			// by the executing command, from the remote side.
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			while ((line = reader.readLine()) != null) {
				outputString.append(line + "\n");
			}

			returnVar.add(exitStatus);
			returnVar.add(outputString.length() == 0 ? exitStatus : outputString.toString());

			// Command execution completed here.
			reader.close();
			in.close();
			channelExec.disconnect();
		} catch (JSchException | IOException | InterruptedException e) {
			System.out.println("Error while executing command :" + runCommand);
			retval=1;
			throw e;
		}
		return returnVar;
	}
}
