package scorers;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;

public class IDFScorer implements Scorer{
	
	public IDFScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	/**
	 * Calculate idf(t)=log(|D|/|Dt|)
	 * @param query
	 * @return
	 * @throws IOException
	 */
	@Override
	public double score(Set<String> queryTerms) throws IOException {
		BooleanQuery query = new BooleanQuery();
		for(String q:queryTerms)
			query.add(new BooleanClause(new TermQuery(new Term(Constants.field,q)), Occur.SHOULD));
		int docFreq = m_searcher.search(query, 100000).totalHits;
		long docCount = m_searcher.collectionStatistics(Constants.field).docCount();
		return Math.log(1+(double)docCount/(double)docFreq);
	}

	@Override
	public String getName() {
		return "idf";
	}


private IndexSearcher m_searcher;

}
