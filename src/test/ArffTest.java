package test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import weka.ArffFile;

public class ArffTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		ArffFile arff = new ArffFile("C:\\arffTest.arff");
		arff.setRelation("test");
		arff.addAttribute("feature1", "REAL");
		arff.addAttribute("feature2", "REAL");
		arff.addAttribute("feature3", "REAL");
		arff.addAttribute("feature4", "REAL");
		
		arff.addAttribute("class", "{0,1}");
		arff.writeArffHeader();
		arff.writeDataLine("1.2,3.4,5.6,6.7,1");
		arff.writeDataLine("1.2,3.4,5.6,6.7,1");
		arff.writeDataLine("1.2,3.4,5.6,6.7,1");
		arff.writeDataLine("1.2,3.4,5.6,6.7,1");
		arff.writeDataLine("1.2,3.4,5.6,6.7,0");
		arff.writeDataLine("1.2,3.4,5.6,6.7,0");
		arff.writeDataLine("1.2,3.4,5.6,6.7,0");
		arff.writeDataLine("1.2,3.4,5.6,6.7,0");
		
		arff.close();

	}

}
