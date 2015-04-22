package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import qe.QeTreatment;

import scorers.OperationScorers;
import scorers.Scorer;
import scorers.TFScorer;

public class QeScoredFilesExp {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	
		String targetTermsFile = "F:\\ScoredFile\\FinalTrainSet_morph.txt";
		String expFile = "F:\\tfIdfTrainWikiTopDocs50.txt";
		File preScoredFile = new File("F:\\ScoredFileTest\\WikiTop50Exp5\\Pre\\Train");
		int expNum = 5;
		
		if(!preScoredFile.exists())
			preScoredFile.mkdirs();
		
		int lineCount = 0;
		// open Lucene index
		Directory directory = FSDirectory.open(new File("F:\\Responsa\\indexes\\unigPre"));
		DirectoryReader reader = DirectoryReader.open(directory);  
		IndexSearcher searcher = new IndexSearcher(reader);  
		
		QeTreatment qe = new QeTreatment();
		qe.loadQeTerms(new File(targetTermsFile),new File(expFile), expNum);
		
		HashMap<String, LinkedList<Set<String>>> expandedQueriesMap = new HashMap<String, LinkedList<Set<String>>>();
		HashMap<String, LinkedList<LinkedList<String>>> expandedTFQueriesMap = new HashMap<String, LinkedList<LinkedList<String>>>();
		BufferedReader fileReader = new BufferedReader(new FileReader(targetTermsFile));
		String line = fileReader.readLine();
		while (line!=null){
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
			String targetTerm = line.split("\t")[0];
			System.out.println(targetTerm);
			qe.addQeTerms(targetTerm, queryList);
			expandedQueriesMap.put(targetTerm, queryList);
			
			LinkedList<LinkedList<String>> queryListTf = new LinkedList<LinkedList<String>>();
			// build query list for tfscorer
			for(String t:line.split("\t")){
				LinkedList<String> queries = new LinkedList<String>();
				for(String t1:t.split(" "))
					queries.add(t1);
				queryListTf.add(queries);
			}
			queryListTf.addAll(qe.getQeTerms4TFquery(targetTerm));
			expandedTFQueriesMap.put(targetTerm, queryListTf);
			
			line = fileReader.readLine();
		}
		fileReader.close();
	
		
		String [] opScorers = {"VARScorer"};
//		String [] opScorers = {"IDFScorer","ICTFScorer", "EntropyScorer","SCQScorer","VARScorer"};
		String [] addScorers = {"QS","SCS","CSScorer","PMI"};
		
		TreeSet<String> sortedSet = new TreeSet<String>();
		sortedSet.addAll(expandedQueriesMap.keySet());
		
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(preScoredFile.getAbsolutePath()+"\\expansions.exp"));
		//print expansions data
		for(String target:sortedSet){
			String expData = "";
			for(Set<String> expS:expandedQueriesMap.get(target)){
				for(String exp:expS)
					expData+=exp+" ";
				expData = expData.trim()+"#";
			}
			expData = expData.substring(0,expData.length()-2) + "\t";
			for(LinkedList<String> expS:expandedTFQueriesMap.get(target)){
				for(String exp:expS)
					expData+=exp+" ";
				expData = expData.trim()+"#";
			}
			expData = expData.substring(0,expData.length()-2) + "\n";
			fileWriter.write(target+"\t"+expData);
		}
		fileWriter.close();
		
		fileWriter = new BufferedWriter(new FileWriter(preScoredFile.getAbsolutePath()+"\\confData.conf"));
		fileWriter.write("Expansions file: " + expFile + "\n");
		fileWriter.write("Expansions number: " + expNum + "\n");
		fileWriter.write("Expansions filter: " + "None" + "\n");
		fileWriter.close();
				

		
		for(String opS:opScorers){
			lineCount = 0;
			Class<?> c = Class.forName("scorers."+opS);
			Constructor<?> cons[] = c.getConstructors();
			Object scorer =  cons[0].newInstance(searcher);
			OperationScorers opScorer = new OperationScorers((Scorer) scorer);
			
			fileWriter = new BufferedWriter(new FileWriter(preScoredFile.getAbsolutePath()+"\\"+opS.replaceAll("Scorer", "")+".txt"));
			for(String target:sortedSet){
				lineCount++;
				fileWriter.write(target+"\t" + opScorer.Avg(expandedQueriesMap.get(target))+"\n");
				if(lineCount%50==0){
					System.out.println(opS + ": " + lineCount);
					fileWriter.flush();
				}
			}
			fileWriter.close();
		}
		
		TFScorer tfscorer = new TFScorer(searcher);
		fileWriter = new BufferedWriter(new FileWriter(preScoredFile.getAbsolutePath()+"\\TF.txt"));
		for(String target:sortedSet)
			fileWriter.write(target+"\t" + tfscorer.score(expandedTFQueriesMap.get(target))+"\n");
		fileWriter.close();
		
		
		for(String addS:addScorers){
			lineCount=0;
			Class<?> c = Class.forName("scorers."+addS);
			Constructor<?> cons[] = c.getConstructors();
			Object scorer =  cons[0].newInstance(searcher);
			
			fileWriter = new BufferedWriter(new FileWriter(preScoredFile.getAbsolutePath()+"\\"+addS.replaceAll("Scorer", "")+".txt"));
			for(String target:sortedSet){
				lineCount++;
				double score = 0;
				if(addS.equals("CSScorer")) {
					Method m = scorer.getClass().getMethod("score", new Class[]{List.class, int.class});
					score = (Double) m.invoke(scorer, expandedQueriesMap.get(target), 50);
				} else if(addS.equals("PMI")) {
					Method m = scorer.getClass().getMethod("score", new Class[]{List.class, int.class});
					if (expandedQueriesMap.get(target).size()>1)
						score = (Double) m.invoke(scorer, expandedQueriesMap.get(target), 0);
					else {
						fileWriter.write(target+"\tNA\n");
						continue;
					}
				} else {
					Method m = scorer.getClass().getMethod("score", new Class[]{List.class});
					score = (Double) m.invoke(scorer, expandedQueriesMap.get(target));
				}
				fileWriter.write(target+"\t" + score +"\n");
				if(lineCount%50==0){
					System.out.println(addS + ": " + lineCount);
					fileWriter.flush();
				}
			}
			fileWriter.close();
		}
	}
		
}