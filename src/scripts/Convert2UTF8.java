package scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Convert2UTF8 {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File inputFolder = new File("C:\\Users\\Admin\\Desktop\\REsponsaAll\\ansi");
		String outputFolder = "C:\\Users\\Admin\\Desktop\\REsponsaAll\\utf\\";
		for(File d:inputFolder.listFiles()){
			String folderName = d.getName();
			File folderDir = new File(outputFolder+folderName);
			if(!folderDir.exists())
				folderDir.mkdir();
			for(File f:d.listFiles()){
				BufferedReader in = new BufferedReader(
						   new InputStreamReader(
				                      new FileInputStream(f), "CP1255"));
				String outFileName = outputFolder+folderName+"\\"+f.getName();
				BufferedWriter out = new BufferedWriter(
						   new OutputStreamWriter(
				                      new FileOutputStream(outFileName), "UTF8"));
				 
				String str;
		 
				while ((str = in.readLine()) != null) {
				    out.write(str+"\n");
				}
		 
                in.close();
                out.close();
			}
		}

	}

}
