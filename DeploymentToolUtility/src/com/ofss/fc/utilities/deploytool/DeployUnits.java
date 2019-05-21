package com.ofss.fc.utilities.deploytool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeployUnits extends DeploymentConstants{

	public static void deployShellJava(String file, String relsheetpath, String spath, String tpath) {
		try {
			String fullFilePath=relsheetpath+"\\"+spath+"\\"+file;

			//create command to copy shell and java units to remote deployment server
			String copyCommand="pscp -pw "+remotePass+" \""+ fullFilePath +"\" "+remoteUser+"@"+remoteIP+":"+tpath;
			Process runtime= Runtime.getRuntime().exec("cmd /c start "+copyCommand);	        

			BufferedReader reader= new BufferedReader(new InputStreamReader(runtime.getInputStream()));
			String line;
			while((line= reader.readLine())!=null) {
				System.out.println(line);
			}

			int retCode=runtime.waitFor();
			if(retCode!=0) {
				System.out.println("\nError: Failed while copying file "+file);
				return;
			}
			else
				System.out.println(file+" deployed at path "+tpath);

			//provide permission to shell files
			if(file.endsWith(".sh")) {
				permissionToshell(file, tpath);
				//checkLineFeed(file, tpath);
			}

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
				System.out.println("\nError: Failed while deploying DB file "+sqlFile.getName());
			else
				System.out.println("DB units deployment completed!");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public static void permissionToshell(String file, String path) {

		String command="cd "+path +" && "+ "chmod 777 "+file;
		System.out.println("Granting permission to "+file+" present at path "+path);

		RemoteHostConnection remoteSession= new RemoteHostConnection();
		remoteSession.initSession();

		ArrayList<Object> output=remoteSession.runCommand(command);
		if(0!=(int)output.get(0)) {
			System.out.println("\nError: Command "+command+" Failed.\nReturned "+output.get(1));
		}
		remoteSession.disConnectSession();	
	}

	/*	public static void checkLineFeed(String file, String path) {

		String command="file "+path+"/" +file +" | grep CRLF";
		System.out.println("Checking linefeed of "+file+" present at path "+path);

		RemoteHostConnection remoteSession= new RemoteHostConnection();
		remoteSession.initSession();

		ArrayList<Object> output=remoteSession.runCommand(command);
		//GREP returns 0 if found match and non zero if not found 
		if(0==(int)output.get(0)) {
			System.out.println("\nError: "+file+" has CRLF line terminators!");
		} else {
			System.out.println("File "+file+" has LF line terminators!");
		}
		remoteSession.disConnectSession();	
	}*/

	public static boolean checkValidUnit(String file, String relsheetpath, String spath, int unit) {	
		InputStreamReader isr=null;		
		try {
			//check file exist
			String fullFilePath=relsheetpath+"\\"+spath;
			File sourceFile= new File(fullFilePath.trim(), file);

			if(!sourceFile.exists()) {
				System.out.println("\nError: "+sourceFile.getAbsoluteFile() +" does not exist!");
				return false;
			}

			//check encoding SJIS or MS932
			isr= new InputStreamReader(new FileInputStream(sourceFile));
			if(!"MS932".equals(isr.getEncoding())){
				System.out.println("\nError: "+sourceFile.getName() +" does not have Shift-JIS/MS932 endcoding!");					
				return false;				
			}

			//check file contain spaces
			if(file.contains(" ")){
				System.out.println("\nError: "+file +" contains spaces in name!");
				return false;
			}

			//checks for shell files			
			if(unit==SHELL_UNITS) {
				int i;
				//check for LF line feed		
				while((i=isr.read())!=-1) {	
					if((char)i=='\r'&& (char)isr.read()=='\n') {
						System.out.println("\nError: "+file +" contains CRLF line terminators!");
						return false;	
					}
				}
			}//checks for DB files
			else if (unit==DB_UNITS) {
				StringBuilder builder= new  StringBuilder();
				BufferedReader br = new BufferedReader(isr);
				//check SQL file contains ; and / both
				if(file.toLowerCase().endsWith(".sql")){
					String line;					
					while((line=br.readLine())!=null)
						builder.append(line);
					//"\\;(\\n|\\r\\n)+\\/"
					Matcher match = Pattern.compile("\\;(\\s)*\\/",Pattern.MULTILINE).matcher(builder.toString());
					if(match.find())
						System.out.println("\nError: "+file +" contains both (;) and (/) characters!");
				}
				//check package file with / at the end 
				else if(file.toLowerCase().endsWith(".ora")&&file.toLowerCase().startsWith("pk_")){
					String line="", prevLine="";					
					while(line!=null && prevLine!=null) {
						line=br.readLine();						
						if(line==null||line.trim().startsWith("CREATE OR REPLACE PACKAGE BODY") )
							if(!prevLine.trim().equals("/"))
								System.out.println("\nError: "+file +" does not contains (/) at the end!");
						prevLine=line;				
					}

					builder.append(line);
				}
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isr!=null)
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}			
		}

		return true;
	}
}


