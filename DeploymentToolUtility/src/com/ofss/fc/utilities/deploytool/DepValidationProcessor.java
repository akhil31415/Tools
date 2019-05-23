package com.ofss.fc.utilities.deploytool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DepValidationProcessor extends DeploymentConstants {
	
	public static boolean doValidations(String file, String relsheetpath, String spath, int unit) {
		String fullFilePath = relsheetpath + "\\" + spath;
		File sourceFile = new File(fullFilePath.trim(), file);
		// check file exist
		if (!sourceFile.exists()) {
			System.out.println(ERRORSTR + sourceFile.getAbsoluteFile() + " does not exist!");
			retval=1;
			return false;
		}

		try (InputStreamReader isr = new InputStreamReader(new FileInputStream(sourceFile))) {

			// check encoding SJIS or MS932 System.out.println(isr.getEncoding());
			if (!detectCharset(sourceFile)) {
				System.out.println(ERRORSTR + sourceFile.getName() + " does not have Shift-JIS endcoding!");
				retval=1;
				return false;
			}

			// check file contain spaces
			if (file.contains(" ")) {
				System.out.println(ERRORSTR + file + " contains spaces in name!");
				retval=1;
				return false;
			}

			// checks for shell files
			if (unit == SHELL_UNITS) {
				int i;
				// check for LF line feed
				while ((i = isr.read()) != -1) {
					if ((char) i == '\r' && (char) isr.read() == '\n') {
						System.out.println(ERRORSTR + file + " contains CRLF line terminators!");
						retval=1;
						return false;
					}
				}
			} // checks for DB files
			else if (unit == DB_UNITS) {
				StringBuilder builder = new StringBuilder();
				BufferedReader br = new BufferedReader(isr);
				// check SQL file contains ; and / both
				if (file.toLowerCase().endsWith(".sql")) {
					String line;
					while ((line = br.readLine()) != null)
						builder.append(line);
					// "\\;(\\n|\\r\\n)+\\/"
					Matcher match = Pattern.compile("\\;(\\s)*\\/", Pattern.MULTILINE).matcher(builder.toString());
					if (match.find()) {
						System.out.println(ERRORSTR + file + " contains both (;) and (/) characters!");
						retval=1;
						return false;
					}
				}
				// check package file with / at the end
				else if (file.toLowerCase().endsWith(".ora") && file.toLowerCase().startsWith("pk_")) {
					String line = "";
					String prevLine = "";
					while (line != null) {
						line = br.readLine();
						if (line == null || line.trim().startsWith("CREATE OR REPLACE PACKAGE BODY"))
							if (!"/".equals(prevLine.trim())) {
								System.out.println(ERRORSTR + file + " does not contain (/) at the end!");
								retval=1;
								return false;
							}
						prevLine = line;
					}
					builder.append(line);
				}
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean detectCharset(File sfile) {
		Charset cset = Charset.forName("SJIS");
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sfile))) {
			CharsetDecoder decoder = cset.newDecoder();
			decoder.reset();

			byte[] buffer = new byte[512];
			while (bis.read(buffer) != -1) {
				try {
					decoder.decode(ByteBuffer.wrap(buffer));
				} catch (CharacterCodingException e) {
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
