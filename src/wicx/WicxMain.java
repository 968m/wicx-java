package wicx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class WicxMain {

	public static void main(String[] args) {

		File folder = new File("in");
		File outfolder = new File("out");
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles.length == 0)
		{
			System.out.println("Folder is empty.");
			return;
		}
		
		String doctext;
		String proprietorInfo;
		String personInfo;
		String regexPerson = "(<DeliveryPerson>)\\s*(<NaturalPerson>)(?s)(.*?)(<\\/NaturalPerson>)\\s*(<\\/DeliveryPerson>)";
		String regexAct = "(<EntitlementDocument>)(?s)(.*?)(<\\/EntitlementDocument>)";

		for (File file : listOfFiles) {
			if (file.isFile()) {
				// load file content
				System.out.print("File: "+file.getName()+" ... ");
				
				doctext = getStringFromFile(folder.getName()+"/"+file.getName());
				
				// find person info in XML, it goes in ProprietorInfo/NaturalPerson section
				proprietorInfo = getNodeContent(doctext, "ProprietorInfo");
				personInfo = getNodeContent(proprietorInfo, "NaturalPerson");
				
				// replace the DeliveryPerson/NaturalPerson element with ProprietorInfo/NaturalPerson 				
				String newdoc = doctext.replaceFirst(regexPerson, "<DeliveryPerson>\n<NaturalPerson>" + personInfo+"</NaturalPerson>\n</DeliveryPerson>");
				
				// get PropertyAcquisitionJustification
				String propertyAcqJust = getNodeContent(doctext,"PropertyAcquisitionJustification");
				
				// replace StateActInfo/EntitlementDocument with PropertyAcquisitionJustification
				String newdoc2 = newdoc.replaceAll(regexAct, "<EntitlementDocument>"+propertyAcqJust+"</EntitlementDocument>");
				
				// saving result to file
				if (writeToFile(outfolder.getName()+"/"+file.getName(),newdoc2))
					System.out.println("done.");
				else
					System.out.println("FAIL, cannot write result.");
				
			} //end if
		} // end for
		
		System.out.println("Mission complete.");

	} // end main
	
	
public static String getNodeContent(String src, String nodename) {
	String patternStart = "<"+ nodename+">";
	String patternEnd = "</"+ nodename+">";
	int startCut = src.toLowerCase().indexOf(patternStart.toLowerCase());
	int endCut = src.toLowerCase().indexOf(patternEnd.toLowerCase());
	if (!((startCut > 0) && (endCut > 0) && (endCut > startCut)))
		return "-1";
//	System.out.println("StartCut: "+ startCut + ", endCut: " + endCut);
	String res = src.substring(startCut+patternStart.length(), endCut);
	return res;
} // end getNodeContent

	
public static String getStringFromFile(String filename) {
		
		StringBuffer s = new StringBuffer();
		try {
			FileInputStream inputFile= new FileInputStream(filename);
			InputStreamReader inputStream = new InputStreamReader(inputFile, "UTF-8");
			BufferedReader bis = new BufferedReader(inputStream);
			int val;
			int count = 0;
			while ((val = bis.read()) != -1) {
				s.append((char)val);
				count++;
			}
			bis.close();
		}
		catch(Exception e)
		{
		  System.out.println(e);
		  System.exit(0);
		}
		
		
		return s.toString();
	} // end getStringFromFile

public static boolean writeToFile(String file, String content) {
	
	BufferedWriter writer = null;
	try
	{
	    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
	    writer.write(content);

	}
	catch ( IOException e)
	{
	}
	finally
	{
	    try
	    {
	        if ( writer != null)
	        writer.close( );
	    }
	    catch ( IOException e)
	    {
	    	return false;
	    }
	}
	
	return true;
};

}
