package com.ofss.fc.utilities.deploytool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ReadReleasesheet extends DeploymentConstants {
	
	public static void loadRelsheet(String lReleasesheet) {
		try {
			f = new File(lReleasesheet);			
			//create workbook object for handling the release sheet file.
			finpt= new FileInputStream(f);
			workbook = WorkbookFactory.create(finpt);
			sheet= workbook.getSheetAt(1);
			
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void returnRelSheetName() {}
	
	public void returnDBUnits() {}
	
	public void returnShellUnits() {}
	
	public void returnNonShellUnits() {}

}
