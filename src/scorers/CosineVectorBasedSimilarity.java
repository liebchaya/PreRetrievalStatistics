package scorers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;

/**
 * Cosine vector similarity - Lucene
 * @author HZ
 *
 */
public class CosineVectorBasedSimilarity {

	public CosineVectorBasedSimilarity(IndexSearcher searcher)
			throws IOException {
		m_searcher = searcher;
		N = m_searcher.collectionStatistics(Constants.field).docCount();

	}

	/**
	 * Calculate cosine similarity between two Lucene documents
	 * @param docId1
	 * @param docId2
	 * @return
	 * @throws IOException
	 */
	public double cosineScore(int docId1,int docId2) throws IOException{
		 terms = new HashSet<String>();
		 Map<String, Double> f1 = getWeights(docId1);
		 Map<String, Double> f2 = getWeights(docId2);
		 RealVector v1 = toRealVector(f1);
		 RealVector v2 = toRealVector(f2);
		 return getCosineSimilarity(v1,v2);
	}

	/**
	 * Calculate cosine similarity between two RealVector vectors
	 * @param v1
	 * @param v2
	 * @return
	 */
	private double getCosineSimilarity(RealVector v1,RealVector v2) {
		 double dotProduct = v1.dotProduct(v2);
		 double normalization = (v1.getNorm() * v2.getNorm());
		 return dotProduct / normalization;
	}
	
	/**
	 * Get tf*idf scores of the terms in a Lucene document
	 * @param docId
	 * @return
	 * @throws IOException
	 */
	private Map<String, Double> getWeights(int docId) throws IOException {
		IndexReader reader = m_searcher.getIndexReader();
		Terms vector = reader.getTermVector(docId, Constants.field);
		Map<String, Integer> docFrequencies = new HashMap<String, Integer>();
		Map<String, Integer> termFrequencies = new HashMap<String, Integer>();
		Map<String, Double> tf_Idf_Weights = new HashMap<String, Double>();
		TermsEnum termsEnum = null;
		DocsEnum docsEnum = null;

		termsEnum = vector.iterator(termsEnum);
		BytesRef text = null;
		while ((text = termsEnum.next()) != null) {
			String term = text.utf8ToString();
//			int docFreq = termsEnum.docFreq();
			docFrequencies.put(term,
					reader.docFreq(new Term(Constants.field, term)));

			docsEnum = termsEnum.docs(null, null);
			while (docsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
				termFrequencies.put(term, docsEnum.freq());
			}

			terms.add(term);
		}

		for (String term : docFrequencies.keySet()) {
			int tf = termFrequencies.get(term);
			int df = docFrequencies.get(term);
			double idf = (1 + Math.log(N) - Math.log(df));
			double w = tf * idf;
			tf_Idf_Weights.put(term, w);
		}
		 return tf_Idf_Weights;
	}

	/**
	 * Convert HashMap<String,Double> to RealVector
	 * @param map
	 * @return
	 */
	private RealVector toRealVector(Map<String, Double> map) {
		RealVector vector = new ArrayRealVector(terms.size());
		int i = 0;
		double value = 0;
		for (String term : terms) {
			if (map.containsKey(term)) {
				value = map.get(term);
			} else {
				value = 0;
			}
			vector.setEntry(i++, value);
		}
		return vector;
	}

	/**
	 * Gets the score's name
	 * @return
	 */
	public String getName(){
		return "CosineSimilarity";
	}
	
	private long N;
	private IndexSearcher m_searcher;
	private Set<String> terms;
}
