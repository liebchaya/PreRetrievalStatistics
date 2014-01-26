package scorers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;

import utils.MathUtils;


public class EntropyScorer implements Scorer{
	
	public EntropyScorer(IndexSearcher searcher){
		m_searcher = searcher;
	}

	
	/**
	 * Calculate entropy(t)=sum(p(d)*log_p(d))
	 * @param query
	 * @return
	 * @throws IOException
	 */
//	@Override
	public double score1(Set<String> queryTerms) throws IOException {
		// extract the set of documents containing term t
		BooleanQuery query = new BooleanQuery();
		for(String q:queryTerms)
			query.add(new BooleanClause(new TermQuery(new Term(Constants.field,q)), Occur.SHOULD));
		TopDocs td = m_searcher.search(query, 100000);
		long docCount = m_searcher.collectionStatistics(Constants.field).docCount();
		double sum = 0;
		for (ScoreDoc scoreDoc : td.scoreDocs) {
			int tf = 0;
			int d = 0;
			int docId = scoreDoc.doc;
			Fields termVector = m_searcher.getIndexReader().getTermVectors(docId);
			Terms terms = termVector.terms(Constants.field);
			TermsEnum te = terms.iterator(null);
			while (te.next() != null) {
				// recognize one of the query's terms
				if (queryTerms.contains(te.term().utf8ToString()))
					tf+=te.totalTermFreq();
				d+=te.totalTermFreq();
			}
			double ptd = (double)tf/(double)d;
			sum += ptd*MathUtils.Log(ptd, docCount);
		}
		
		return sum;
	}

	/**
	 * Calculate entropy(t)=sum(p(d)*log_p(d))
	 * @param query
	 * @return
	 * @throws IOException
	 */
	@Override
	public double score(Set<String> queryTerms) throws IOException {
		// extract the set of documents containing term t
		SpanOrQuery query = new SpanOrQuery();
		for(String q:queryTerms)
			query.addClause(new SpanTermQuery(new Term(Constants.field,q)));
		long docCount = m_searcher.collectionStatistics(Constants.field).docCount();
		double sum = 0;
		//this is not the best way of doing this, but it works for the example.  See http://www.slideshare.net/lucenerevolution/is-your-index-reader-really-atomic-or-maybe-slow for higher performance approaches
		AtomicReader wrapper = SlowCompositeReaderWrapper.wrap(m_searcher.getIndexReader());
		Map<Term, TermContext> termContexts = new HashMap<Term, TermContext>();
		Spans spans = query.getSpans(wrapper.getContext(), new Bits.MatchAllBits(m_searcher.getIndexReader().numDocs()), termContexts);
		HashMap<Integer,Integer> spansMap = new HashMap<Integer, Integer>();
		while(spans.next()){
			if(spansMap.containsKey(spans.doc())){
				int freq = spansMap.get(spans.doc());
				spansMap.put(spans.doc(),freq+1);
			}
			else
				spansMap.put(spans.doc(),1);
		}
		for(int id:spansMap.keySet()){
			int d = Integer.parseInt(m_searcher.getIndexReader().document(id).get("LENGTH"));
			double ptd = (double)spansMap.get(id)/(double)d;
			sum += ptd*MathUtils.Log(ptd, docCount);
		}
		return sum;
	}

	
	@Override
	public String getName() {
		return "entropy";
	}


private IndexSearcher m_searcher;

}
