
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.iflex.fcr.app.service.command.MessageHelper;
import com.iflex.fcr.infra.error.ErrorManager;
import com.iflex.fcr.infra.exception.FatalException;
import com.iflex.fcr.infra.xml.FCRJXMLParser;

public class Test {

	/**
	 * @param args
	 */

	public static String inputXml = "";
	public static String responseXml;
	public static String requestXml = "";
	public static String path1 = "D://TranXML//RequestAutoChrg.xml";

	public static void main(String[] args) {		
//		System.setProperty("file.encoding", "Shift-JIS");
//		System.out.println(System.getProperty("user.home"));
		System.setProperty("user.home",	"D://FLEXCUBE_HOST//flexcube//host//runarea//env//");
		FCRJXMLParser inputData = null;
		FileReader file = null;
		try {				

			file = new FileReader(path1);
			BufferedReader buf = new BufferedReader(file);

			while ((inputXml = buf.readLine()) != null) {
				//System.out.println(inputXml);				
				requestXml = requestXml.concat(inputXml).trim();
				System.out.println("\n RequestXml : ");
				prettyPrintXML(requestXml);
			}

			if (requestXml != null) {
				inputData = FCRJXMLParser.getInstance();
				inputData.parse(requestXml);
			} else {
				try {
					ErrorManager.throwFatalException(
							"Failed to execute service", null);
				} catch (FatalException e) {					
					e.printStackTrace();
				}
			}
			MessageHelper mesg = new MessageHelper();
			//System.out.println(requestXml);
			 try {
					responseXml = mesg.executeMessage(requestXml);
				} catch (FatalException e) {
					e.printStackTrace();
				}
			 //System.out.println("responseXml Thread 1-->" + responseXml);
			 System.out.println("\n ResponseXml : ");
			 prettyPrintXML(responseXml);
			buf.close(); 
	/*	while (true)
		{
			Thread thread1 =new Thread(){
				public void run()
				{   	MessageHelper mesg1 = new MessageHelper();
				        System.out.println(requestXml);
					    try {
							responseXml = mesg1.executeMessage(requestXml);
						} catch (FatalException e) {
							e.printStackTrace();
						}
						System.out.println("responseXml Thread 1-->" + responseXml);
				};
				
			};
			  thread1.run();
		}
		*/
			
		/*	Thread thread2 =new Thread(){
				public void run()
				{   	MessageHelper mesg2 = new MessageHelper();
				        System.out.println(requestXml);
					    try {
							responseXml = mesg2.executeMessage(requestXml);
						} catch (FatalException e) {
							e.printStackTrace();
						}
						System.out.println("responseXml Thread 2-->" + responseXml);
				};
				
			};
			*/
			//thread1.start();	
			//thread2.start();	
			/*thread1.run();
			thread2.run();
			*/
			/* System.out.println("responseXml-->" + responseXml); */
			// }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void prettyPrintXML(String input) {
		
		try {
		DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
		DocumentBuilder builder= factory.newDocumentBuilder();
		Document doc= builder.parse(new InputSource(new StringReader(input)));
		
		Transformer tform=TransformerFactory.newInstance().newTransformer();
		tform.setOutputProperty(OutputKeys.ENCODING, "SJIS");
		tform.setOutputProperty(OutputKeys.INDENT, "yes");
		tform.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		tform.transform(new DOMSource(doc), new StreamResult(System.out));
		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
