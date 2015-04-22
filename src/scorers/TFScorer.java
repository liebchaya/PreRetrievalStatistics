package scorers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;

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
	public double score(List<LinkedList<String>> queryListTf) throws IOException{
		BooleanQuery fullQuery = new BooleanQuery();
		for(List<String> qList:queryListTf){
			PhraseQuery query = new PhraseQuery();
			for(String q:qList)
				query.add(new Term(Constants.field,q));
			fullQuery.add(query,Occur.SHOULD);
		}
//		System.out.println(fullQuery);
		int docFreq = m_searcher.search(fullQuery, 100000).totalHits;
		return (double)docFreq;
	}

	
	public String getName() {
		return "tf";
	}


private IndexSearcher m_searcher;

}
