package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import morphology.GenerateMorphExpFile;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import scorers.CSScorer;
import scorers.CSScorerV2;
import scorers.EntropyScorer;
import scorers.ICTFScorer;
import scorers.IDFScorer;
import scorers.OperationScorers;
import scorers.PMI;
import scorers.QS;
import scorers.SCQScorer;
import scorers.SCS;
import scorers.TFScorer;
import scorers.VARScorer;

public class CreateScoredFiles {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//define executor
//		int iThreadNum = 20;
//		ExecutorService executor = Executors.newFixedThreadPool(iThreadNum);
		int lineCount = 0;
		// open Lucene index
		Directory directory = FSDirectory.open(new File("F:\\Responsa\\indexes\\unigPre"));
		DirectoryReader reader = DirectoryReader.open(directory);  
		IndexSearcher searcher = new IndexSearcher(reader);  
//		String targetTermFile ="F:\\ScoredFile\\FinalTestSet_orig.txt";
//		GenerateMorphExpFile.generateMorphExpFile(targetTermFile, searcher);
		
		String targetTermFile ="F:\\ScoredFile\\FinalTrainSet_morph.txt";
		File preTestScored = new File("F:\\ScoredFile\\Pre\\Train");
		BufferedReader fileReader = new BufferedReader(new FileReader(targetTermFile));
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(preTestScored.getAbsolutePath()+"\\CS.txt"));
//		// initialize scorer
//		TFScorer tf = new TFScorer(searcher);
//		IDFScorer idf = new IDFScorer(searcher);
//		ICTFScorer ictf = new ICTFScorer(searcher);
//		EntropyScorer entropy = new EntropyScorer(searcher);
//		QS qs = new QS(searcher);
//		SCS scs = new SCS(searcher);
//		VARScorer var = new VARScorer(searcher);
//		SCQScorer scq = new SCQScorer(searcher);
//		PMI pmi = new PMI(searcher);
		CSScorer cs = new CSScorer(searcher);
		String line = fileReader.readLine();
		while (line != null) {
			lineCount++;
//			List<LinkedList<String>> queryListTf = new LinkedList<LinkedList<String>>();
//			// build query list for tfscorer
//			for(String t:line.split("\t")){
//				LinkedList<String> queries = new LinkedList<String>();
//				for(String t1:t.split(" "))
//					queries.add(t1);
//				queryListTf.add(queries);
//			}
//			fileWriter.write(line.split("\t")[0]+"\t"+tf.score(queryListTf)+"\n");
			
			// build query list for other scorers
			LinkedList<Set<String>> queryList = new LinkedList<Set<String>>();
			for(String t:line.split("\t")){
				for(int pos=0; pos< t.split(" ").length; pos++){
					if (queryList.size()-1 < pos){
						Set<String> queries = new HashSet<String>();
						queries.add( t.split(" ")[pos]);
						queryList.add(pos, queries);
					} else {
						queryList.get(pos).add(t.split(" ")[pos]);
					}
				}
				
			}	
//			fileWriter.write(line.split("\t")[0]+"\t"+scs.score(queryList)+"\n");
			
//			if(queryList.size()==1)
//				fileWriter.write(line.split("\t")[0] + "\tNA\tNA\n");
//			else {
//				fileWriter.write(line.split("\t")[0]+"\t"+pmi.score(queryList,0)+ "\t" + pmi.score(queryList,1) +"\n");
//			}
			fileWriter.write(line.split("\t")[0]+"\t"+cs.score(queryList,500)+"\n");
			if(lineCount%50==0){
				System.out.println(lineCount);
				fileWriter.flush();
			}
			
//			OperationScorers os = new OperationScorers(scq);
//			if (queryList.size()>1){
//				fileWriter.write(line.split("\t")[0]+"\t"+os.Avg(queryList) + "\t"+os.Max(queryList)+"\t"+os.Sum(queryList)+"\n");
////				fileWriter.write(line.split("\t")[0]+"\t"+pmi.score(queryList, 0) +"\t"+pmi.score(queryList, 1)+"\n");
//			}
//			else {
//				double score = os.Avg(queryList);
//				fileWriter.write(line.split("\t")[0]+"\t"+ score +"\t"+ score +"\t"+ score  + "\n");
////				fileWriter.write(line.split("\t")[0]+"\tNA\tNA\n");
//			}
//						
			line = fileReader.readLine();
		}
		reader.close();
		directory.close();
		fileWriter.close();
		fileReader.close();

	}

}