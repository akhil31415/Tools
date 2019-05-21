package com.ofss.fc.utilities.deploymenttool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DeployUnits extends DeploymentConstants{
	
	public static void deployShellJava(String file, String releasesheet, String spath, String tpath) {
		try {			
			//create command to copy shell and java units to remote deployment server
			String copyCommand="pscp -pw "+remotePass+" \"\\"+releasesheet+spath+"\\"+file+"\"" +" "+remoteUser+"@"+remoteIP+":"+tpath;
			Process runtime= Runtime.getRuntime().exec("cmd /c start "+copyCommand);	        
			
			BufferedReader reader= new BufferedReader(new InputStreamReader(runtime.getInputStream()));
			String line;
			while((line= reader.readLine())!=null) {
				System.out.println(line);
			}
			
			int retCode=runtime.waitFor();
			if(retCode!=0)
				System.out.println("Failed while copying file "+file);
			else
				System.out.println(file+" deployed at path "+tpath);
			
			//provide permission to shell files
			if(file.endsWith(".sh"))
				permissionToshell(file, tpath);	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deployDB(File sqlFile) {
		
		try {			
			String filePath=sqlFile.getAbsolutePath();
			String command="SQLPLUS "+dBUser+"/"+dBPass+"@"+dBserviceName+" @"+filePath;
			
			Process runtime= Runtime.getRuntime().exec("cmd /c start "+command);	        
			
			BufferedReader reader= new BufferedReader(new InputStreamReader(runtime.getInputStream()));
			String line;
			while((line= reader.readLine())!=null) {
				System.out.println(line);
			}
			
			int retCode=runtime.waitFor();
			if(retCode!=0)
				System.out.println("Failed while deploying DB file "+sqlFile.getName());
			else
				System.out.println(" All DB units deployed!");
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
	}
	
	public static void permissionToshell(String file, String path) {
		JSch jsch = new JSch();
		Session session;
		String command="cd "+path +" && "+ "chmod 777 "+file;
		System.out.println("Granting permission to "+file+" present at path "+path);
		
		try {
			session = jsch.getSession(remoteUser, remoteIP, 22);			
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(remotePass);
			session.connect();

	
			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setCommand(command);
			channelExec.setInputStream(null);
			channelExec.setErrStream(System.err);          

			InputStream in = channelExec.getInputStream();
			channelExec.connect();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {				
				System.out.println("Output: "+line);
			}			
			reader.close();
			in.close();
			
			int exitStatus = channelExec.getExitStatus();
			if (exitStatus > 0) {
				System.out.println("Remote script execution error. ExitCode retuned is  " + exitStatus);
			}
			//Disconnect the Session
			channelExec.disconnect();
			session.disconnect();
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
}
