package com.ofss.fc.utilities.deploymenttool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class DepToolProcessor extends DeploymentConstants{

	static {
		try {
			File pfile = new File("Deployment.properties");
			properties = new Properties();
			properties.load(new FileInputStream(pfile));
			remoteIP = (String) properties.get("REMOTEIP");
			remoteUser = (String) properties.get("REMOTEUSERID");
			remotePass = (String) properties.get("REMOTEPASS");
			dBserviceName = (String) properties.get("DBSERVICE");
			dBUser = (String) properties.get("DBUSER");
			dBPass = (String) properties.get("DBPASS");
			utility_logFile = (String) properties.get("UTILITY.LOGFILE");
			logFile_datePattern=(String) properties.get("UTILITY.LOGFILE.DATEPATTERN");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static int startToolProcess(String lDeploymentTag, String lTargetReleasesheet, int lUnitsToDeploy) throws IOException {

		int retstatus;
		if("execute".equals(lDeploymentTag)) {
			System.out.println("Start executing shell files...");
			retstatus=executeShellFiles(lTargetReleasesheet);
		}else {
			retstatus=deployUnits(lTargetReleasesheet, lUnitsToDeploy);
			System.out.println("End of deployment process...");
		}		
		return retstatus;
	}

	public static int deployUnits(String lReleasesheet, int lUnitsToDeploy) {
		try {

			File f = new File(lReleasesheet);
			String aboslutePath = f.getParent();
			//create workbook object for handling the release sheet file.
			FileInputStream finpt= new FileInputStream(f);
			Workbook workbook = WorkbookFactory.create(finpt);
			// Getting the Sheet at index 1 for "ResourceList"
			Sheet sheet = workbook.getSheetAt(1);	

			//get the release sheet tag
			Row row= sheet.getRow(0);
			Cell cell= row.getCell(0);
			FormulaEvaluator feval= workbook.getCreationHelper().createFormulaEvaluator();        	
			String releasesheet= feval.evaluate(cell).formatAsString();
			releasesheet= releasesheet.replaceAll("\"", "");

			//variable to hold cell values
			String cellValue;
			String spathCellValue;
			String tpathCellValue; 
			String orderCellValue; 
			String unitNameCell;

			// Create a DataFormatter to format and get each cell's value as String
			DataFormatter dataFormatter = new DataFormatter();
			Map<Integer, String> dbOrderMap= new TreeMap<>();

			boolean flgDepSh=true;
			boolean flgDepDB=true;
			//logic to deploy shell/java or DB or both
			if(lUnitsToDeploy==DB_UNITS) {
				flgDepSh=false;
				System.out.println("Deploying DB units ... ");
			}
			else if (lUnitsToDeploy==SHELL_UNITS) {
				flgDepDB=false;
				System.out.println("Deploying shell units ... ");
			}else System.out.println("Deploying all units ... ");


			//use a for loop to iterate over the rows and columns
			for(int i=4; i<sheet.getLastRowNum();i++) { 
				row= sheet.getRow(i);
				cell= row.getCell(9);
				cellValue = dataFormatter.formatCellValue(cell);
				unitNameCell =dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex()+1));

				if ("CP".equals(cellValue) && flgDepSh) {
					spathCellValue =dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex()+3));
					tpathCellValue =dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex()+7));
					DeployUnits.deployShellJava(unitNameCell, aboslutePath+"\\"+releasesheet, spathCellValue, tpathCellValue);
				}
				else if ("DB".equals(cellValue) && flgDepDB) {
					//list down all the DB units into a SQL file and execute the file by calling deployDB					
					spathCellValue =dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex()+3));
					orderCellValue =dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex()+12));
					if(orderCellValue==null)
						orderCellValue="1";
					System.out.println("Found DB unit in release sheet "+unitNameCell);
					dbOrderMap.put(Integer.parseInt(orderCellValue), "@"+"\""+aboslutePath+"\\"+releasesheet+spathCellValue+"\\"+unitNameCell+"\"\n");
				}
			}
			if(!dbOrderMap.isEmpty()){				
				File sqlFile= new File(SQLFILE);			
				if(!sqlFile.exists()) {
					if(!sqlFile.createNewFile()) {
						System.out.println("DB unit deployment error!");
						return 1;
					}
				}

				FileWriter fr=new FileWriter(sqlFile);
				fr.write("SET DEFINE OFF \nSPOOL "+SPOOLFILE+"\r\nSET ECHO ON\n");

				for(Map.Entry<Integer, String> entry : dbOrderMap.entrySet()) {
					fr.write(entry.getValue());
				}

				fr.write("SPOOL OFF\r\nEXIT;");
				fr.close();

				DeployUnits.deployDB(sqlFile);
			}

			workbook.close();
			finpt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return 1;			
		}
		return 0;
	}

	public static int executeShellFiles(String lReleasesheet) {
		try {
			int returnCode=0;
			//create workbook object for handling the release sheet file.
			FileInputStream finpt= new FileInputStream(new File(lReleasesheet));
			Workbook workbook = WorkbookFactory.create(finpt);

			// Getting the Sheet at index 1 for "ResourceList"
			Sheet sheet = workbook.getSheetAt(1);
			System.out.println("=> " + sheet.getSheetName());
			// Create a DataFormatter to format and get each cell's value as String
			DataFormatter dataFormatter = new DataFormatter();
			ShellExecuter exec = new ShellExecuter();
			//use a for loop to iterate over the rows and columns
			for(int i=0; i<sheet.getLastRowNum();i++) { 
				Row row= sheet.getRow(i);
				Cell cell= row.getCell(9);
				String cellValue = dataFormatter.formatCellValue(cell);
				String nextCellValue =dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex()+1));
				String pathCellValue =dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex()+7));
				if ("CP".equals(cellValue) && nextCellValue.endsWith(".sh")) {
					returnCode=exec.execute(pathCellValue, nextCellValue);
					if(returnCode!=0)
						System.out.println("Execution of "+nextCellValue+" failed with return status "+returnCode);
				}
			}
			workbook.close();
			finpt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	public static void displayExecLog() {
		try {
			String copyCommand="pscp -pw "+remotePass+" "+remoteUser+"@"+remoteIP+":"+remoteLogpath+remoteLogfile+" .";
			Process runtime= Runtime.getRuntime().exec("cmd /c start "+copyCommand);	        

			BufferedReader reader= new BufferedReader(new InputStreamReader(runtime.getInputStream()));
			String line;
			while((line= reader.readLine())!=null) {
				System.out.println(line);
			}

			int retCode=runtime.waitFor();
			if(retCode!=0)
				System.out.println("LogFile Copy unsuccessul!");
			else
				Runtime.getRuntime().exec("cmd /c start "+localeditor+" "+remoteLogfile);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void displayDeploymentLog() {
		try {
			File spoolFile =new File(SPOOLFILE);
			if (spoolFile.exists())
				Runtime.getRuntime().exec("cmd /c start "+localeditor+" "+SPOOLFILE);	
			else System.out.println("SpoolFile not found!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void displayPropertiesFile() {
		try {
			File propertiesFile =new File(PROPERIESFILE);
			if (propertiesFile.exists())
				Runtime.getRuntime().exec("cmd /c start "+localeditor+" "+PROPERIESFILE);	
			else System.out.println("Properties File not found!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}