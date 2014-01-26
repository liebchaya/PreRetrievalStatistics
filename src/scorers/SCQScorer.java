package scorers;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.IndexSearcher;


public class SCQScorer implements Scorer{
	
	public SCQScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	/**
	 * Calculate SCQ(t)=(1+log(tf(t,D)))*idf(t)
	 * @param query
	 * @return
	 * @throws IOException
	 */
	@Override
	public double score(Set<String> queryTerms) throws IOException {
		IDFScorer idf = new IDFScorer(m_searcher);
		int sum = 0;
		for(String q:queryTerms){
			sum += m_searcher.termStatistics(new Term(Constants.field,q),TermContext.build(m_searcher.getIndexReader().getContext(), new Term(Constants.field,q))).totalTermFreq();
		}
		
		double scq = (1+Math.log((double)sum))*idf.score(queryTerms);
		return scq;
	}

	@Override
	public String getName() {
		return "SCQ";
	}


private IndexSearcher m_searcher;

}
