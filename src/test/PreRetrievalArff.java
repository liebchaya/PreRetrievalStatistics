package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

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
import weka.ArffFile;

public class PreRetrievalArff {

	/**
	 * @param args
	 * args[0] - input terms
	 * args[1] - index directory
	 * args[2] = output file
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		//define executor
		int iThreadNum = 20;
		ExecutorService executor = Executors.newFixedThreadPool(iThreadNum);
		// open input file
		BufferedReader bReader = new BufferedReader(new FileReader(args[0]));
		// open Lucene index
//		Directory directory = FSDirectory.open(new File("C:\\ResponsaNew\\indexes\\unigIndex"));
		Directory directory = FSDirectory.open(new File(args[1]));
		DirectoryReader reader = DirectoryReader.open(directory);  
		IndexSearcher searcher = new IndexSearcher(reader);  
		
		// initialize scorers
		TFScorer tf = new TFScorer(searcher);
		IDFScorer idf = new IDFScorer(searcher);
		ICTFScorer ictf = new ICTFScorer(searcher);
		EntropyScorer entropy = new EntropyScorer(searcher);
		QS qs = new QS(searcher);
		SCS scs = new SCS(searcher);
		VARScorer var = new VARScorer(searcher);
		CSScorerV2 cs = new CSScorerV2(searcher);
		SCQScorer scq = new SCQScorer(searcher);
		PMI pmi = new PMI(searcher);
		
		// arff header
		ArffFile arff = new ArffFile(args[2]);
		arff.setRelation("PreRetrievalMeasures");
		arff.addAttribute("TF", "REAL");
		arff.addAttribute("AvgIDF", "REAL");
		arff.addAttribute("MaxIDF", "REAL");
		arff.addAttribute("DevIDF", "REAL");
		
		arff.addAttribute("AvgICTF", "REAL");
		arff.addAttribute("MaxICTF", "REAL");
		arff.addAttribute("DevICTF", "REAL");
		
		arff.addAttribute("AvgEntropy", "REAL");
		arff.addAttribute("MedEntropy", "REAL");
		arff.addAttribute("MaxEntropy", "REAL");
		arff.addAttribute("DevEntropy", "REAL");
		
		arff.addAttribute(qs.getName(), "REAL");
		arff.addAttribute(scs.getName(), "REAL");
		
		arff.addAttribute("AvgVAR", "REAL");
		arff.addAttribute("MaxVAR", "REAL");
		arff.addAttribute("SumVAR", "REAL");
		
		arff.addAttribute("CS", "REAL");
		
		arff.addAttribute("AvgSCQ", "REAL");
		arff.addAttribute("MaxSCQ", "REAL");
		arff.addAttribute("SumSCQ", "REAL");
		
		arff.addAttribute("AvgPMI", "REAL");
		arff.addAttribute("MaxPMI", "REAL");
		
		arff.addAttribute("class", "{0,1}");
		arff.writeArffHeader();
		
		String line = bReader.readLine();
		while(line!=null){
			String dataLine = "";
			
			String query = line.split("\t")[0];
			List<Set<String>> queryList = new LinkedList<Set<String>>();
			for(String t:query.split(" ")){
				Set<String> queries = new HashSet<String>();
				queries.add(t);
				queryList.add(queries);
			}

			dataLine += tf.score(queryList) + ",";
			
			OperationScorers os = new OperationScorers(idf);
			if (queryList.size()>1){
				dataLine += os.Avg(queryList) + ",";
				dataLine += os.Max(queryList) + ",";
				dataLine += os.Dev(queryList) + ",";
			}
			else {
				double score = os.Avg(queryList);
				dataLine += score + ",";
				dataLine += score + ",";
				dataLine += Double.NaN+",";
			}
			
			os = new OperationScorers(ictf);
			if (queryList.size()>1) {
				dataLine += os.Avg(queryList) + ",";
				dataLine += os.Max(queryList) + ",";
				dataLine += os.Dev(queryList) + ",";
			}
			else {
				double score = os.Avg(queryList);
				dataLine += score + ",";
				dataLine += score + ",";
				dataLine += Double.NaN+",";
			}
			
			os = new OperationScorers(entropy);
			if (queryList.size()>1) {
				dataLine += os.Avg(queryList) + ",";
				dataLine += os.Med(queryList) + ",";
				dataLine += os.Max(queryList) + ",";
				dataLine += os.Dev(queryList) + ",";
			}	
			else {
				double score = os.Avg(queryList);
				dataLine += score + ",";
				dataLine += score + ",";
				dataLine += score + ",";
				dataLine += Double.NaN+",";
			}
			
			dataLine += qs.score(queryList) + ",";
			dataLine += scs.score(queryList) + ",";
			
			os = new OperationScorers(var);
			if (queryList.size()>1) {
				dataLine += os.Avg(queryList) + ",";
				dataLine += os.Max(queryList) + ",";
				dataLine += os.Sum(queryList) + ",";	
			} else {
				double score = os.Avg(queryList);
				dataLine += score + ",";
				dataLine += score + ",";
				dataLine += score + ",";
			}
			
			dataLine += cs.score(queryList,executor) + ",";
				
			
			os = new OperationScorers(scq);
			if (queryList.size()>1) {
				dataLine += os.Avg(queryList) + ",";
				dataLine += os.Max(queryList) + ",";
				dataLine += os.Sum(queryList) + ",";
			} else {
				double score = os.Avg(queryList);
				dataLine += score + ",";
				dataLine += score + ",";
				dataLine += score + ",";
			}
			
			if(queryList.size()==1)
				dataLine += "0,0,";
			else {
				dataLine += pmi.score(queryList,0) + ",";
				dataLine += pmi.score(queryList,1) + ",";
			}
			
			dataLine += line.split("\t")[1];
			System.out.println(dataLine);
			arff.writeDataLine(dataLine);
			line = bReader.readLine();
		}
		bReader.close();
		arff.close();
		

	}
	

}
