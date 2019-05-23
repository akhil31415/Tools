package com.ofss.fc.utilities.deploytool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
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

public class DepToolProcessor extends DeploymentConstants {

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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int startToolProcess(String lDeploymentTag, String lTargetReleasesheet, int lUnitsToDeploy)
			throws IOException {

		ReadReleasesheet.loadRelsheet(lTargetReleasesheet);

		if ("execute".equals(lDeploymentTag)) {
			System.out.println("Start executing shell files...");
			retval = executeShellFiles();
		} else {
			retval = deployUnits(lUnitsToDeploy);
			System.out.println("\nEnd of deployment process...");
		}
		return retval;
	}

	public static int deployUnits(int lUnitsToDeploy) {

		try {
			String aboslutePath = f.getParent();

			// get the release sheet tag
			Row row = sheet.getRow(0);
			Cell cell = row.getCell(0);
			FormulaEvaluator feval = workbook.getCreationHelper().createFormulaEvaluator();
			String releasesheet = feval.evaluate(cell).formatAsString();
			releasesheet = releasesheet.replaceAll("\"", "");

			// variable to hold cell values
			String cellValue;
			String spathCellValue;
			String tpathCellValue;
			String orderCellValue;
			String unitNameCell;

			// Create a DataFormatter to format and get each cell's value as String
			DataFormatter dataFormatter = new DataFormatter();
			Map<Integer, String> dbOrderMap = new TreeMap<>();

			boolean flgDepSh = true;
			boolean flgDepDB = true;
			// logic to deploy shell/java or DB or both
			if (lUnitsToDeploy == DB_UNITS) {
				flgDepSh = false;
				System.out.println("Deploying DB units .. ");
			} else if (lUnitsToDeploy == SHELL_UNITS) {
				flgDepDB = false;
				System.out.println("Deploying shell units ... ");
			} else
				System.out.println("Deploying all units ... ");

			// use a for loop to iterate over the rows and columns
			for (int i = 4; i < sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				cell = row.getCell(9);
				cellValue = dataFormatter.formatCellValue(cell);
				unitNameCell = dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex() + 1));
				spathCellValue = dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex() + 3));
				orderCellValue = dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex() + 12));
				if (unitNameCell != null && unitNameCell.length() != 0)
					System.out.println("\nFound unit in release sheet: " + unitNameCell);

				if ("CP".equals(cellValue) && flgDepSh && DepValidationProcessor.doValidations(unitNameCell,
						aboslutePath, releasesheet + spathCellValue, SHELL_UNITS)) {
					tpathCellValue = dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex() + 7));
					retval = DeployUnits.deployShellJava(unitNameCell, aboslutePath, releasesheet + spathCellValue,
							tpathCellValue);
				} else if ("DB".equals(cellValue) && flgDepDB && DepValidationProcessor.doValidations(unitNameCell,
						aboslutePath, releasesheet + spathCellValue, DB_UNITS)) {
					// list down all the DB units into a SQL file and execute the file by calling
					// deployDB
					if (orderCellValue == null)
						orderCellValue = "1";
					dbOrderMap.put(Integer.parseInt(orderCellValue), "@" + "\"" + aboslutePath + "\\" + releasesheet
							+ spathCellValue + "\\" + unitNameCell + "\"\n");
				}
			}
			if (!dbOrderMap.isEmpty()) {
				File sqlFile = new File(SQLFILE);
				if (!sqlFile.exists()) {
					if (!sqlFile.createNewFile()) {
						System.out.println(ERRORSTR + "DB unit deployment error!");
						return 1;
					}
				}

				FileWriter fr = new FileWriter(sqlFile);
				fr.write("SET DEFINE OFF \nSPOOL " + SPOOLFILE + "\nSET ECHO ON\n");

				for (Map.Entry<Integer, String> entry : dbOrderMap.entrySet()) {
					fr.write(entry.getValue());
				}

				fr.write("SPOOL OFF \nEXIT;");
				fr.close();
				retval = DeployUnits.deployDB(sqlFile);
			}

			workbook.close();
			finpt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		return retval;
	}

	public static int executeShellFiles() {
		try {
			String aboslutePath = f.getParent();
			// Create a DataFormatter to format and get each cell's value as String
			DataFormatter dataFormatter = new DataFormatter();
			ShellExecuter exec = new ShellExecuter();

			Row row = sheet.getRow(0);
			Cell cell = row.getCell(0);
			FormulaEvaluator feval = workbook.getCreationHelper().createFormulaEvaluator();
			String releasesheet = feval.evaluate(cell).formatAsString();
			releasesheet = releasesheet.replaceAll("\"", "");

			// use a for loop to iterate over the rows and columns
			for (int i = 4; i < sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				cell = row.getCell(9);
				String cellValue = dataFormatter.formatCellValue(cell);
				String unitNameCell = dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex() + 1));
				String spathCellValue = dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex() + 3));
				if ("CP".equals(cellValue) && unitNameCell.endsWith(".sh") && DepValidationProcessor
						.doValidations(unitNameCell, aboslutePath, releasesheet + spathCellValue, SHELL_UNITS)) {
					String pathCellValue = dataFormatter.formatCellValue(row.getCell(cell.getColumnIndex() + 7));
					exec.execute(pathCellValue, unitNameCell);
				}
			}
			workbook.close();
			finpt.close();

		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		return retval;
	}

	public static void displayExecLog() {
		try {
			String copyCommand = "pscp -q -pw " + remotePass + " " + remoteUser + "@" + remoteIP + ":" + REMOTELOGPATH
					+ REMOTELOGFILE + " .";
			Process runtime = Runtime.getRuntime().exec(CMDCOM + copyCommand);

			BufferedReader reader = new BufferedReader(new InputStreamReader(runtime.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			int retCode = runtime.waitFor();
			if (retCode != 0)
				System.out.println("LogFile Copy unsuccessul!");
			else
				Runtime.getRuntime().exec(CMDCOM + LOCALEDITOR + " " + REMOTELOGFILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void displayDeploymentLog() {
		try {
			File spoolFile = new File(SPOOLFILE);
			if (spoolFile.exists())
				Runtime.getRuntime().exec(CMDCOM + LOCALEDITOR + " " + SPOOLFILE);
			else
				System.out.println("SpoolFile not found!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void displayPropertiesFile() {
		try {
			File propertiesFile = new File(PROPERIESFILE);
			if (propertiesFile.exists())
				Runtime.getRuntime().exec(CMDCOM + LOCALEDITOR + " " + PROPERIESFILE);
			else
				System.out.println("Properties File not found!!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int checkSpoolFile() {
		File sqlFile = new File(SPOOLFILE);
		int errorCount = 0;
		if (!sqlFile.exists()) {
			System.out.println(ERRORSTR + sqlFile.getAbsolutePath() + " does not exist!\"");
			return 1;
		}
		try (BufferedReader br = new BufferedReader(new FileReader(sqlFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("ORA-"))
					errorCount++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}

		return errorCount;
	}
}