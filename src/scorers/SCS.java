package scorers;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.IndexSearcher;


/**
 * Simplified Clarity Score
 * @author HZ
 *
 */
public class SCS {
	
	public SCS(IndexSearcher searcher){
		m_searcher = searcher;
	}
	
	/**
	 * Calculate the Kullback-Lieber divergence of the guery language model
	 * from the collection language model.
	 * Assume that each query term is unique 
	 * @param queryList
	 * @return
	 * @throws IOException
	 */
	
	public double score(List<Set<String>> queryList) throws IOException{
		double sigma = 0;
		for (Set<String> queryTerms:queryList){
			int tf = 0;
			for(String q:queryTerms){
				tf += m_searcher.termStatistics(new Term(Constants.field,q),TermContext.build(m_searcher.getIndexReader().getContext(), new Term(Constants.field,q))).totalTermFreq();
			}
			
			long docCount = m_searcher.collectionStatistics(Constants.field).docCount();
			double pqD = (double)tf/(double)docCount;
			double pqQ = 1/(double)queryList.size();
			sigma += pqQ*Math.log(pqQ/pqD);
		}
		return sigma;
	}

	/**
	 * Gets the score's name
	 * @return
	 */
	public String getName(){
		return "SCS";
	}
	
	private IndexSearcher m_searcher;

}
