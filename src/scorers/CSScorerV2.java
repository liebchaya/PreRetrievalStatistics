package scorers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;

public class CSScorerV2{
	
	public CSScorerV2(IndexSearcher searcher) throws IOException{
		m_searcher = searcher;
		N = m_searcher.collectionStatistics(Constants.field).docCount();
	}

	/**
	 * Averages the cosine scores
	 * @param queryList
	 * @param executor 
	 * @return
	 * @throws IOException
	 */
	public double score(List<Set<String>> queryList, ExecutorService executor) throws IOException{
		double sum = 0;
		for(Set<String> q:queryList)
			sum += cosine(q,executor);
		return sum/queryList.size();
	}
	
	/**
	 * Calculate coherence score - the average of pairwise similarity between all pairs of documents
	 * containing one of the query terms
	 * @param queryList
	 * @return
	 * @throws IOException
	 */
	public double cosine(Set<String> queryList,ExecutorService executor) throws IOException {
		
		// load terms' frequencies
		termsDocFreq = new HashMap<String, Integer>();
		Terms terms = MultiFields.getTerms(m_searcher.getIndexReader(),Constants.field);
	    TermsEnum termsEnum = terms.iterator(null);
	    BytesRef text;
	    while((text = termsEnum.next()) != null) {
	    	int freq = termsEnum.docFreq();
	    	if (freq > 0)
	    		termsDocFreq.put(text.utf8ToString(), freq);
	    }
	    System.out.println("Finish loading frequency terms' data");
		
		BooleanQuery query = new BooleanQuery();
		for(String q:queryList)
				query.add(new BooleanClause(new TermQuery(new Term(Constants.field,q)), Occur.SHOULD));
		TopDocs td = m_searcher.search(query, 100000);
		int docFreq = td.totalHits;
		invertedIndex = new HashMap<String, HashMap<Integer,Long>>();
		index = new HashMap<Integer, ArrayList<String>>();
		
		
		if(docFreq<2)
			return Double.NaN;
		
		// build inverted index
		for(ScoreDoc sd:td.scoreDocs){
			index.put(sd.doc, new ArrayList<String>());
			termsEnum = null;
			terms = m_searcher.getIndexReader().getTermVector(sd.doc, Constants.field);
			termsEnum = terms.iterator(termsEnum);
			text = null;
			while ((text = termsEnum.next()) != null) {
				String term = text.utf8ToString();
				// insert only terms with document frequency > 1
				if (termsDocFreq.containsKey(term)){
					index.get(sd.doc).add(term);
					if(!invertedIndex.containsKey(term)){
						HashMap<Integer,Long> idFreqMap = new HashMap<Integer, Long>();
						idFreqMap.put(sd.doc, termsEnum.totalTermFreq());
						invertedIndex.put(term, idFreqMap);
					} else {
						invertedIndex.get(term).put(sd.doc, termsEnum.totalTermFreq());
					}
				}
			}
		}
		System.out.println("Finish building inverted index");
		
		//save vector length
		docsLength = new HashMap<Integer, Double>();
//		for(ScoreDoc sd:td.scoreDocs){
		for(int doc:index.keySet()){
//			termsEnum = null;
//			terms = m_searcher.getIndexReader().getTermVector(sd.doc, Constants.field);
//			termsEnum = terms.iterator(termsEnum);
//			text = null;
			double lengthSum = 0;
//			while ((text = termsEnum.next()) != null) {
			for(int i=0; i< index.get(doc).size(); i++){
				String term = index.get(doc).get(i);
				long termFreq = (invertedIndex.containsKey(term)?invertedIndex.get(term).get(doc):0);
//				long termDocFreq = m_searcher.getIndexReader().docFreq(new Term(Constants.field, text.utf8ToString()));
				long termDocFreq = termsDocFreq.get(term);
				double termTfIdf = (1 + Math.log(N) - Math.log(termDocFreq))*termFreq;
				lengthSum += Math.pow(termTfIdf,2);
//				lengthSum += Math.pow(termFreq,2);
				}
			docsLength.put(doc,Math.sqrt(lengthSum));
		}
		System.out.println("Finish saving vectors norms");
		System.out.println("Total num of docs: " + td.scoreDocs.length);
		
		docsList = new ArrayList<Integer>(index.keySet());
		ArrayList<CosineMultiThread> threadList = new ArrayList<CSScorerV2.CosineMultiThread>();
		int threadsNum = (docsList.size()/20>0?docsList.size()/20:2);
		int docsNum = docsList.size()/threadsNum;
		int startIndex = 0;
		int lastIndex = startIndex+docsNum;
		while(lastIndex<docsList.size()){
			threadList.add(new CosineMultiThread(startIndex,lastIndex));
			startIndex = lastIndex;
			lastIndex += docsNum;
		}
		threadList.add(new CosineMultiThread(startIndex,docsList.size()));
			
		for(CosineMultiThread t:threadList){	
			executor.execute(t);
		}
		
		latch = new CountDownLatch(threadList.size());
//		//join/wait
//		try {
//		executor.shutdown();
//		while (!executor.awaitTermination(1, TimeUnit.HOURS));
//		} catch (InterruptedException e) {
//		
//		}
//		
		try {
			  latch.await();
			} catch (InterruptedException E) {
			   // handle
			}
		
		return counter/(docFreq*(docFreq-1));
		
		
					
	}

	public String getName() {
		return "cs";
	}


private IndexSearcher m_searcher;
private long N;

private HashMap<String,HashMap<Integer,Long>> invertedIndex;
private HashMap<Integer, ArrayList<String>> index;
private HashMap<String,Integer> termsDocFreq;
HashMap <Integer,Double> docsLength;
List<Integer> docsList;

private double counter = 0;
CountDownLatch latch;

public synchronized void incrementCounter(double val) {
    counter += val;
}

public class CosineMultiThread implements Runnable {
	
	int firstId, lastId;
	CosineMultiThread(int firstId, int lastId){
		this.firstId = firstId;
		this.lastId = lastId;
	}
	public void run() {
		double sum = 0;
		for(int docIndex=firstId;docIndex<lastId;docIndex++){
			int doc = docsList.get(docIndex);
			HashMap<Integer,Double> sumMap = new HashMap<Integer, Double>();
			for(int i=0;i<index.get(doc).size();i++){
				String term = index.get(doc).get(i);
				long termFreq = invertedIndex.get(term).get(doc);
				long termDocFreq = termsDocFreq.get(term);
				double termTfIdf = (1 + Math.log(N) - Math.log(termDocFreq))*termFreq;
				for(int docId:invertedIndex.get(term).keySet()){
					if(docId != doc){
						if(sumMap.containsKey(docId)){
							double prevSum = sumMap.get(docId);
							int docFreqTerm = termsDocFreq.get(term);
							double tfIdf = (1 + Math.log(N) - Math.log(docFreqTerm))*invertedIndex.get(term).get(docId);
							sumMap.put(docId,prevSum+termTfIdf*tfIdf);
//							sumMap.put(docId,prevSum+invertedIndex.get(text.utf8ToString()).get(docId)*termFreq);
						}
						else {
							int docFreqTerm = termsDocFreq.get(term);
							double tfIdf = (1 + Math.log(N) - Math.log(docFreqTerm))*invertedIndex.get(term).get(docId);
							sumMap.put(docId,termTfIdf*tfIdf);
//							sumMap.put(docId,(double)invertedIndex.get(text.utf8ToString()).get(docId)*termFreq);
						}
					}
				}
			}
			for(int key:sumMap.keySet()){
				sum += sumMap.get(key)/(double)(docsLength.get(key)*docsLength.get(doc));
			}
			
		}
		
		incrementCounter(sum);
		latch.countDown();

	}

}
}
