package scorers;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.IndexSearcher;


public class ICTFScorer implements Scorer{
	
	public ICTFScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	/**
	 * Calculate ictf(t)=log(|D|/tf(t,D))
	 * @param query
	 * @return
	 * @throws IOException
	 */
	@Override
	public double score(Set<String> queryTerms) throws IOException {
		int sum = 0;
		for(String q:queryTerms){
			sum += m_searcher.termStatistics(new Term(Constants.field,q),TermContext.build(m_searcher.getIndexReader().getContext(), new Term(Constants.field,q))).totalTermFreq();
		}
		
		long docCount = m_searcher.collectionStatistics(Constants.field).docCount();
		return Math.log((double)docCount/(double)sum);
	}

	@Override
	public String getName() {
		return "ictf";
	}


private IndexSearcher m_searcher;

}
