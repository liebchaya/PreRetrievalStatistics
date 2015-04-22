package weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class GeneratePredictionArff {

	private static SortedMap<String,Boolean> annoMap = null;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		loadTargetTermsAnnotation();
		
		String[] trainDirs = new String[]{"F:\\ScoredFileTest\\WikiTitleExp5\\Pre\\Train","F:\\ScoredFileTest\\WikiTitleExp5\\Post\\Train"};
		String[] testDirs = new String[]{"F:\\ScoredFileTest\\WikiTitleExp5\\Pre\\Test","F:\\ScoredFileTest\\WikiTitleExp5\\Post\\Test"};
		GenerateArffFile(trainDirs,"Train");
		GenerateArffFile(testDirs,"Test");
		
	}
	
	
	private static void GenerateArffFile(String[] dirs,String expName) throws IOException {	
		ArffFile arff = new ArffFile("F:\\ScoredFileTest\\WikiTitleExp5\\"+expName+".arff");
		arff.setRelation("PredictionMeasures");
		HashMap<String,String> dataMap = new HashMap<String, String>();
		for(String dir:dirs){
			File expFolder = new File(dir);
			for (File expFile:expFolder.listFiles()) {
				if (expFile.isFile()&&expFile.getName().endsWith(".txt")){
					String measureName = expFile.getName().replace(".txt", "");
					arff.addAttribute(measureName, "REAL");
					if (expFile.getName().equals("CLARITY.txt"))
						arff.addAttribute("QF", "REAL");
					BufferedReader reader = new BufferedReader(new FileReader(expFile));
					String line = reader.readLine();
					while (line!=null){
						 String[] tokens = line.split("\t");
						 String targetTerm = tokens[0];
						 Double score = 0.0;
						 Double score2 = 0.0;
						 if (tokens[1].equals("NA") || tokens[1].equals("NaN")) // NaN appears only in CS.txt
							 score = 0.0;
						 else
							 score = Double.parseDouble(tokens[1]);
						if (expFile.getName().equals("CLARITY.txt")){
							if (tokens[2].equals("NA"))
								 score2 = 0.0;	
							score2 = Double.parseDouble(tokens[2]);
						}
						
//						if (score.isNaN()){
//							 if(expFile.getName().contains("VAR"))
//								 score = -1.0;
//							 if(expFile.getName().contains("WIG"))
////								 score = Double.NEGATIVE_INFINITY;
//								 score = -50.0;
//							 if(expFile.getName().contains("NQC"))
//								 score = -1.0;
//						}
//						if (score.isInfinite()&&(expFile.getName().contains("PMI")||expFile.getName().contains("SCQ")))
//							 score = 0.0;
//						if (score.isInfinite()&&(expFile.getName().contains("ICTF")||expFile.getName().contains("IDF")||expFile.getName().contains("SCS")))
//							score = 50.0;
						if(dataMap.containsKey(targetTerm)){
							String dataLine = dataMap.get(targetTerm);
							dataLine += score + ",";
							if (expFile.getName().equals("CLARITY.txt"))
								dataLine += score2 + ",";
							dataMap.put(targetTerm, dataLine);
						} else {
							String dataLine = score + ",";
							if (expFile.getName().equals("CLARITY.txt"))
								dataLine += score2 + ",";
							dataMap.put(targetTerm, dataLine);
						}
						line = reader.readLine();
					 }
					 reader.close();
				}
			}
		}
		arff.addAttribute("class", "{0,1}");
		arff.writeArffHeader();
		for(String targetTerm:dataMap.keySet()){
			String dataLine = dataMap.get(targetTerm) + (annoMap.get(targetTerm)?"1":"0");
			arff.writeDataLine(dataLine);
		}
		arff.close();	
	}
	
		

	
	private static void loadTargetTermsAnnotation() throws IOException{
		 annoMap = new TreeMap<String, Boolean>();
		 BufferedReader reader = new BufferedReader(new FileReader("F:\\ScoredFile\\FinalTrainSet_orig.txt"));
		 String line = reader.readLine();
		 while (line!=null){
			 System.out.println(line);
			 annoMap.put(line.split("\t")[0], (line.split("\t")[1].equals("0")?false:true));
			 line = reader.readLine();
		 }
		 reader.close();
		 
		 reader = new BufferedReader(new FileReader("F:\\ScoredFile\\FinalTestSet_orig.txt"));
		 line = reader.readLine();
		 while (line!=null){
			 annoMap.put(line.split("\t")[0], (line.split("\t")[1].equals("0")?false:true));
			 line = reader.readLine();
		 }
		 reader.close();
		
	}
}
