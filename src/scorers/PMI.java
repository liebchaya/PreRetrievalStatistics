package scorers;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
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
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.util.Bits;

import utils.MathUtils;


/**
 * PMI Score
 * @author HZ
 *
 */
public class PMI {
	
	public PMI(IndexSearcher searcher){
		m_searcher = searcher;
	}
	
	/**
	 * Calculate the Point-wise Mutual Information 
	 * @param queryList
	 * @param type 0=Avg, 1=Max
	 * @return
	 * @throws Exception 
	 */
	
	public double score(List<Set<String>> queryList, int type) throws Exception{
		System.out.println(queryList);
		if (queryList.size()==3)
			System.out.println("3");
		if(type==0){
			double sum = 0;
			int count = 0;
			for(int i=0;i<queryList.size()-1;i++){
				for(int j=i+1;j<queryList.size();j++){
					sum += calcPMI(queryList.get(i),queryList.get(j));
					count++;
				}
			}
			BigInteger s1 = MathUtils.factorial(queryList.size()-2);
			BigInteger s2 = s1.multiply(BigInteger.valueOf(2));
			BigInteger s3 = MathUtils.factorial(queryList.size());
			double compCount = s2.doubleValue()/s3.doubleValue();
			double inverseCount = Math.pow(count, -1);
//			BigInteger compCount = (MathUtils.factorial(queryList.size()-2).multiply(BigInteger.valueOf(2))).divide(MathUtils.factorial(queryList.size()));
			if(compCount!= inverseCount)
				throw new Exception("Incorrect count PMI - average");
			return sum/(double)count;
		} else{
			double max = -Double.MAX_VALUE;
			for(int i=0;i<queryList.size()-1;i++){
				for(int j=i+1;j<queryList.size();j++){
					double pmi = calcPMI(queryList.get(i),queryList.get(j));
					if (max < pmi)
						max = pmi;
				}
			}
			return max;
		}
				
	}
	
	private double calcPMI1(Set<String> query1Terms,Set<String> query2Terms) throws IOException{
		int tf = 0;
		for(String q:query1Terms){
			tf += m_searcher.termStatistics(new Term(Constants.field,q),TermContext.build(m_searcher.getIndexReader().getContext(), new Term(Constants.field,q))).totalTermFreq();
		}
		
		long docCount = m_searcher.collectionStatistics(Constants.field).docCount();
		double pq1D = (double)tf/(double)docCount;
		
		tf = 0;
		for(String q:query2Terms){
			tf += m_searcher.termStatistics(new Term(Constants.field,q),TermContext.build(m_searcher.getIndexReader().getContext(), new Term(Constants.field,q))).totalTermFreq();
		}
		double pq2D = (double)tf/(double)docCount;
		
		// extract the set of documents containing term t1 and t2
		BooleanQuery query1 = new BooleanQuery();
		for(String q:query1Terms)
			query1.add(new BooleanClause(new TermQuery(new Term(Constants.field,q)), Occur.SHOULD));
		BooleanQuery query2 = new BooleanQuery();
		for(String q:query2Terms)
			query2.add(new BooleanClause(new TermQuery(new Term(Constants.field,q)), Occur.SHOULD));
		BooleanQuery query = new BooleanQuery();
		query.add(query1,Occur.MUST);
		query.add(query2,Occur.MUST);
		TopDocs td = m_searcher.search(query, 100000);
		int sum = 0;
		for (ScoreDoc scoreDoc : td.scoreDocs) {
			int tf1 = 0;
			int tf2 = 0;
			int docId = scoreDoc.doc;
			Fields termVector = m_searcher.getIndexReader().getTermVectors(docId);
			Terms terms = termVector.terms(Constants.field);
			TermsEnum te = terms.iterator(null);
			while (te.next() != null) {
				// recognize one of the query's terms
				if (query1Terms.contains(te.term().utf8ToString()))
					tf1+=te.totalTermFreq();
				else if (query2Terms.contains(te.term().utf8ToString()))
					tf2+=te.totalTermFreq();
				
			}
			sum += Math.min(tf1, tf2);
		}
		double pqD = (double)sum/(double)docCount;
		double pmi = Math.log(pqD/(pq1D*pq2D));
		return pmi;
	}

	private double calcPMI(Set<String> query1Terms,Set<String> query2Terms) throws IOException{
		int tf = 0;
		for(String q:query1Terms){
			tf += m_searcher.termStatistics(new Term(Constants.field,q),TermContext.build(m_searcher.getIndexReader().getContext(), new Term(Constants.field,q))).totalTermFreq();
		}
		
		long docCount = m_searcher.collectionStatistics(Constants.field).sumTotalTermFreq();
		double pq1D = (double)tf/(double)docCount;
		
		tf = 0;
		for(String q:query2Terms){
			tf += m_searcher.termStatistics(new Term(Constants.field,q),TermContext.build(m_searcher.getIndexReader().getContext(), new Term(Constants.field,q))).totalTermFreq();
		}
		double pq2D = (double)tf/(double)docCount;
		
		// extract the set of documents containing term t1 and t2
		SpanOrQuery query1 = new SpanOrQuery();
		for(String q:query1Terms)
			query1.addClause(new SpanTermQuery(new Term(Constants.field,q)));
		SpanOrQuery query2 = new SpanOrQuery();
		for(String q:query2Terms)
			query2.addClause(new SpanTermQuery(new Term(Constants.field,q)));
		SpanQuery[] arr = new SpanQuery[]{query1,query2};
		SpanNearQuery query = new SpanNearQuery(arr,0,true);
		// calculate average w(t,d)
		double sum = 0;
		//this is not the best way of doing this, but it works for the example.  See http://www.slideshare.net/lucenerevolution/is-your-index-reader-really-atomic-or-maybe-slow for higher performance approaches
		AtomicReader wrapper = SlowCompositeReaderWrapper.wrap(m_searcher.getIndexReader());
		Map<Term, TermContext> termContexts = new HashMap<Term, TermContext>();
		Spans spans = query.getSpans(wrapper.getContext(), new Bits.MatchAllBits(m_searcher.getIndexReader().numDocs()), termContexts);
//		HashMap<Integer,Integer> spansMap = new HashMap<Integer, Integer>();
		while(spans.next()){
//			if(spansMap.containsKey(spans.doc())){
//				int freq = spansMap.get(spans.doc());
//				spansMap.put(spans.doc(),freq+1);
//			}
//			else
//				spansMap.put(spans.doc(),1);
			sum++;
		}
		double pqD = (double)sum/(double)docCount;
		double pmi = Math.log(pqD/(pq1D*pq2D));
		return pmi;
	}

	private IndexSearcher m_searcher;

}
