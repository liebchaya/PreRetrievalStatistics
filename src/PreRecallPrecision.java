

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import com.aliasi.classify.ScoredPrecisionRecallEvaluation;


public class PreRecallPrecision {
	private static HashMap<String,Boolean> annoMap = null;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		loadTargetTermsAnnotation();
		
		File postDir = new File("F:\\ScoredFileTest\\WikiTitleExp5\\Pre");
		for(File evalDir:postDir.listFiles()){
			if (!evalDir.isDirectory()||evalDir.getName().equals("Curves"))
				continue;
			String evalFileName = postDir+"\\"+evalDir.getName()+".eval"; 
			BufferedWriter writer = new BufferedWriter(new FileWriter(evalFileName));
			for (File f:evalDir.listFiles()){
			 if (!f.isDirectory()&& f.getName().endsWith(".txt")){
				 ScoredPrecisionRecallEvaluation eval = new ScoredPrecisionRecallEvaluation();
				 BufferedReader reader = new BufferedReader(new FileReader(f));
				 String line = reader.readLine();
				 while (line!=null){
					 String[] tokens = line.split("\t");
					 String targetTerm = tokens[0];
					 if (tokens[1].equals("NA")){
						 line = reader.readLine();
						 continue;
					 }
					 // for CS
					 if (tokens[1].equals("NaN")){
						 line = reader.readLine();
						 continue;
					 }
					 Double score = Double.parseDouble(tokens[1]);
					 System.out.println(f.getName());
//					 if (score.isNaN()){
//						 if(f.getName().contains("VAR"))
//							 score = -1.0;
////						 if(f.getName().contains("WIG"))
////							 score = Double.NEGATIVE_INFINITY;
//					 }
					 Boolean anno = annoMap.get(targetTerm);
					 eval.addCase(anno, score);
					 line = reader.readLine();
				 }
				 reader.close();
				 
				File curvesDir = new File(postDir+"\\"+evalDir.getName()+"\\Curves");
				if (!curvesDir.exists())
					curvesDir.mkdir();
				BufferedWriter curveWriter = new BufferedWriter(new FileWriter(curvesDir+"\\"+f.getName()));
				double[][] pr = eval.prScoreCurve(false);
				for(int i=0;i<eval.numCases();i++){
					for(int j=0;j<3;j++)
						curveWriter.write((pr[i][j]+ "\t"));
					curveWriter.write("\n");
				}
				curveWriter.close();
				writer.write(f.getName()+ "\t" + eval.averagePrecision() + "\n");
			} 
		}
		writer.close();
		}
	}
		
		 	
private static void loadTargetTermsAnnotation() throws IOException{
	 annoMap = new HashMap<String, Boolean>();
	 BufferedReader reader = new BufferedReader(new FileReader("F:\\ScoredFile\\FinalTrainSet_orig.txt"));
	 String line = reader.readLine();
	 while (line!=null){
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
