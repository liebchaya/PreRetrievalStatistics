package scorers;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

public class CSScorer implements Scorer{
	
	public CSScorer(IndexSearcher searcher) throws IOException{
		m_searcher = searcher;
		m_cosineScorer = new CosineVectorBasedSimilarity(searcher);
	}

	/**
	 * Calculate coherence score - the average of pairwise similarity between all pairs of documents
	 * containing one of the query terms
	 * @param queryTerms
	 * @return
	 * @throws IOException
	 */
	@Override
	public double score(Set<String> queryTerms) throws IOException {
		BooleanQuery query = new BooleanQuery();
		for(String q:queryTerms)
			query.add(new BooleanClause(new TermQuery(new Term(Constants.field,q)), Occur.SHOULD));
		TopDocs td = m_searcher.search(query, 100000);
		int docFreq = td.totalHits;
		
		int i=0;
		int[] docsArray = new int[docFreq];
		for (ScoreDoc scoreDoc : td.scoreDocs) 
			docsArray[i++]= scoreDoc.doc;
		double sum = 0;
		System.out.println("Docs num: "+docsArray.length);
////		for(i=0;i<docsArray.length;i++)
////			for(int j=0;j<docsArray.length;j++){
//				if(i!=j){
		for(i=0;i<docsArray.length-1;i++){
			for(int j=i+1;j<docsArray.length;j++){
					sum += m_cosineScorer.cosineScore(docsArray[i], docsArray[j]);
					System.out.println("Cosine score bertween: " +docsArray[i] + " and " + docsArray[j]);
				}
			}
	
		return sum/(docFreq*(docFreq-1));
			
	}

	@Override
	public String getName() {
		return "cs";
	}


private IndexSearcher m_searcher;
private CosineVectorBasedSimilarity m_cosineScorer;

}
