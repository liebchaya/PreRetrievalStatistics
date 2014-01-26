package scorers;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TermQuery;

public class TFScorer{
	
	public TFScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	/**
	 * Calculate tf(t)=|Dt|
	 * @param query
	 * @return
	 * @throws IOException
	 */
	public double score(List<Set<String>> queryList) throws IOException{
//		BooleanQuery query = new BooleanQuery();
		PhraseQuery query = new PhraseQuery();
		for(Set<String> qList:queryList)
			for(String q:qList)
				query.add(new Term(Constants.field,q));
//				query.add(new BooleanClause(new TermQuery(new Term(Constants.field,q)), Occur.SHOULD));
		System.out.println(query);
		int docFreq = m_searcher.search(query, 100000).totalHits;
		return (double)docFreq;
	}

	
	public String getName() {
		return "tf";
	}


private IndexSearcher m_searcher;

}
