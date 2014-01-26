package test;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import scorers.CSScorer;
import scorers.CSScorerV2;
import scorers.CosineVectorBasedSimilarity;
import scorers.EntropyScorer;
import scorers.ICTFScorer;
import scorers.IDFScorer;
import scorers.OperationScorers;
import scorers.PMI;
import scorers.QS;
import scorers.SCQScorer;
import scorers.SCS;
import scorers.SCSv2;
import scorers.VARScorer;

public class ScorersTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		Directory index = new RAMDirectory();

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);

		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "Lucene fing Action Lucene ball", "193398817");
		addDoc(w, "Lucene action good Dummies", "55320055Z");
		addDoc(w, "Managing Gigabytes Action Lucene", "55063554A");
		addDoc(w, "Art bla blue Computer Science Lucene", "9900333X");
//		addDoc(w, "Lucene in Action Lucene ball", "193398817");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Managing Gigabytes Action Lucene", "55063554A");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "Lucene in Action Lucene", "193398817");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Managing Gigabytes Action Lucene", "55063554A");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "Lucene for Dummies", "55320055Z");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
//		addDoc(w, "The Art of Computer Science Lucene", "9900333X");
		w.close();
		
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		CosineVectorBasedSimilarity cosine = new CosineVectorBasedSimilarity(searcher);
		System.out.println(cosine.cosineScore(0, 1));
		IDFScorer scorer = new IDFScorer(searcher);
//		Query q = new TermQuery(new Term("title","lucene"));
		List<Set<String>> queryList = new LinkedList<Set<String>>();
		Set<String> queries = new HashSet<String>();
		queries.add("lucene");
		queryList.add(queries);
		Set<String> queries2 = new HashSet<String>();
		queries2.add("action");
		queryList.add(queries2);
		System.out.println(scorer.score(queries));
		EntropyScorer scorer3 = new EntropyScorer(searcher);
		System.out.println(scorer3.score(queries));
		VARScorer scorer4 = new VARScorer(searcher);
		OperationScorers op = new OperationScorers(scorer4);
		System.out.println(op.Max(queryList));
		SCQScorer scorer5 = new SCQScorer(searcher);
		System.out.println(scorer5.score(queries));
		SCS scorer6 = new SCS(searcher);
		System.out.println(scorer6.score(queryList));
		SCSv2 scorer7 = new SCSv2(searcher);
		System.out.println(scorer7.score(queryList));
		PMI scorer8 = new PMI(searcher);
		System.out.println("****"+scorer8.score(queryList,0));
		CSScorer scorer2 = new CSScorer(searcher);
		long startTime = System.currentTimeMillis();
		System.out.println("Cosine1: "+scorer2.score(queries));
		long endTime = System.currentTimeMillis();
		System.out.println(endTime-startTime);
		startTime = System.currentTimeMillis();
		CSScorerV2 scorer9 = new CSScorerV2(searcher);
		
//		System.out.println("Cosine2: "+scorer9.score(queryList));
		endTime = System.currentTimeMillis();
		System.out.println(endTime-startTime);

	}
	
	private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
		final FieldType BodyOptions = new FieldType();
		BodyOptions.setIndexed(true);
		BodyOptions.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		BodyOptions.setStored(true);
		BodyOptions.setStoreTermVectors(true);
		  Document doc = new Document();
		  doc.add(new Field("title", title, BodyOptions));
		  doc.add(new StringField("isbn", isbn, Store.YES));
		  doc.add(new StringField("LENGTH", Integer.toString(title.split(" ").length), Store.YES));
		  
		  w.addDocument(doc);
		}

}
