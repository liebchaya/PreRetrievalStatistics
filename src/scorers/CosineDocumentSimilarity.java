package scorers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

public class CosineDocumentSimilarity {

	public static final String CONTENT = "TERM_VECTOR";

	private IndexReader reader;
	private Set<String> terms;
	private RealVector v1;
	private RealVector v2;

	CosineDocumentSimilarity(IndexReader pReader) {
		reader = pReader;
	}

	public double getCosineSimilarity(int docId1, int docId2)
			throws IOException {
		terms = new HashSet<String>();
		Map<String, Integer> f1 = getTermFrequencies(docId1);
		Map<String, Integer> f2 = getTermFrequencies(docId2);
		v1 = toRealVector(f1);
		v2 = toRealVector(f2);
		return (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm());
	}

	private Map<String, Integer> getTermFrequencies(int docId)
			throws IOException {
		Terms vector = reader.getTermVector(docId, CONTENT);
		TermsEnum termsEnum = null;
		termsEnum = vector.iterator(termsEnum);
		Map<String, Integer> frequencies = new HashMap<String, Integer>();
		BytesRef text = null;
		while ((text = termsEnum.next()) != null) {
			String term = text.utf8ToString();
			int freq = (int) termsEnum.totalTermFreq();
			frequencies.put(term, freq);
			terms.add(term);
		}
		return frequencies;
	}

	private RealVector toRealVector(Map<String, Integer> map) {
		RealVector vector = new ArrayRealVector(terms.size());
		int i = 0;
		for (String term : terms) {
			int value = map.containsKey(term) ? map.get(term) : 0;
			vector.setEntry(i++, value);
		}
		return (RealVector) vector.mapDivide(vector.getL1Norm());
	}
}
