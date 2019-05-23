package com.ofss.fc.utilities.deploytool;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class DeploymentConstants {
	public static final int DB_UNITS = 1;
	public static final int SHELL_UNITS = 2;

	public static Properties properties = null;
	public static String remoteIP = "";
	public static String remoteUser = "";
	public static String remotePass = "";
	public static String dBserviceName = "";
	public static String dBUser = "";
	public static String dBPass = "";
	public static File f = null;
	public static FileInputStream finpt = null;
	public static Workbook workbook = null;
	public static Sheet sheet = null;
	public static int retval=0;
	public static final String REMOTELOGPATH = "/opt/fcbhost/datamigration/log/";
	public static final String REMOTELOGFILE = "JBKDMlog.txt";
	public static final String SQLFILE = "DeplySQL.sql";
	public static final String SPOOLFILE = "DeploymentSpool.txt";
	public static final String PROPERIESFILE = "Deployment.properties";
	public static final String LOCALEDITOR = "notepad++";
	public static final String ERRORSTR = "\nError: ";
	public static final String CMDCOM = "cmd /c start ";

	DeploymentConstants() {		
	}

}
