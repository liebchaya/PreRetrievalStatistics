package scorers;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;

/**
 * Query scope
 * @author HZ
 *
 */
public class QS {
	
	public QS(IndexSearcher searcher){
		m_searcher = searcher;
	}
	
	/**
	 * Calculate the percentage of documents in the collection containing at least
	 * one of the query terms.
	 * @param queryList
	 * @return
	 * @throws IOException
	 */
	public double score(List<Set<String>> queryList) throws IOException{
		BooleanQuery query = new BooleanQuery();
		for(Set<String> qList:queryList)
			for(String q:qList)
				query.add(new BooleanClause(new TermQuery(new Term(Constants.field,q)), Occur.SHOULD));
		int docFreq = m_searcher.search(query, 100000).totalHits;
		long docCount = m_searcher.collectionStatistics(Constants.field).docCount();
		return (double)docFreq/(double)docCount;
	}

	/**
	 * Gets the score's name
	 * @return
	 */
	public String getName(){
		return "QS";
	}
	
	private IndexSearcher m_searcher;

}
