package scripts;

import java.io.File;
import java.io.IOException;


import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.IndexSearcher;


import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class TestIndexBySearch {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		ComplexPhraseQueryParser parser = new ComplexPhraseQueryParser(Version.LUCENE_48, "TERM_VECTOR", new StandardAnalyzer(Version.LUCENE_48));
		Query q = parser.parse("\"חסד של אמת\"");
		Directory directory = FSDirectory.open(new File("F:\\Responsa\\indexes\\unigPreFix"));
		DirectoryReader reader = DirectoryReader.open(directory);  
		IndexSearcher searcher = new IndexSearcher(reader);
		int totalDocs = searcher.getIndexReader().numDocs();
		System.out.println(q);
		ScoreDoc[] docs = searcher.search(q, 100000).scoreDocs;
		for(ScoreDoc d:docs){
			Document doc = searcher.doc(d.doc);
			System.out.println(doc.get("ID"));
			System.out.println(doc.get("SOURCE"));
		}
		System.out.println("total docs: " +totalDocs);
		directory.close();
	}

	

}
