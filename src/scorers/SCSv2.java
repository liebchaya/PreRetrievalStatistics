package scorers;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.IndexSearcher;


/**
 * Simplified Clarity Score
 * @author HZ
 *
 */
public class SCSv2 {
	
	public SCSv2(IndexSearcher searcher){
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
		OperationScorers opScorer = new OperationScorers(new ICTFScorer(m_searcher));
		return Math.log(1/(double)queryList.size())+opScorer.Avg(queryList);
	}

	/**
	 * Gets the score's name
	 * @return
	 */
	public String getName(){
		return "SCSv2";
	}
	
	private IndexSearcher m_searcher;

}
