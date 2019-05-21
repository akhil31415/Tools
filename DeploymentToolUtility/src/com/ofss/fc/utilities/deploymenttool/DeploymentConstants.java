package com.ofss.fc.utilities.deploymenttool;

import java.util.Properties;

public class DeploymentConstants {
	public static final int DB_UNITS = 1;
	public static final int SHELL_UNITS = 2;
	
	public static Properties properties =null;
	public static  String remoteIP="";
	public static  String remoteUser="";
	public static  String remotePass="";
	public static  String dBserviceName="";
	public static  String dBUser="";
	public static  String dBPass="";
	public static  String remoteLogpath="/opt/fcbhost/datamigration/log/";
	public static  String remoteLogfile="JBKDMlog.txt";
	public static String utility_logFile = "";
	public static String logFile_datePattern = "";

	public static final String SQLFILE="DeplySQL.sql";
	public static final String SPOOLFILE="DeploymentSpool.txt";
	public static final String PROPERIESFILE="Deployment.properties";
	public static final String localeditor="notepad++";
	
	


}
