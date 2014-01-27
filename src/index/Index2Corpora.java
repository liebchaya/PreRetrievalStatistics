package index;

import index.DocReader;
import index.Indexer;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;




/**
 * Main class for indexing 2 corpura simultaneously (with Lucene)
 * @author HZ
 */
public class Index2Corpora
{		
	/**
	 * Indexes two corpura to one index
	 * @param args (index directory, corpus directory1, document reader1, corpus directory2 and document reader2 ({@link DocReader}))
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		
		long start = new Date().getTime();
		long end = new Date().getTime();
		
		
		System.out.println("start :"+start);
		String indexFolder = args[0];
		File corpus1Dir = new File(args[1]);
		File corpus2Dir = new File(args[3]);
		Class<?> cls;
		String docReader1Class =args[2];
		cls = Class.forName(docReader1Class);
		DocReader reader1 = (DocReader) cls.getDeclaredConstructor(File.class).newInstance(corpus1Dir);
		String docReader2Class =args[4];
		cls = Class.forName(docReader2Class);
		DocReader reader2 = (DocReader) cls.getDeclaredConstructor(File.class).newInstance(corpus2Dir);
//		String analyzerClass =params.get("analyzer-class");
//		cls = Class.forName(analyzerClass);
//		Analyzer analyzer = (Analyzer) cls.getDeclaredConstructor(Version.class).newInstance(Version.LUCENE_31);

		File indexDir = new File(indexFolder);
		Indexer manager = new Indexer();
		Set<Indexer.DocField> fields = new HashSet<Indexer.DocField>();
		fields.add(Indexer.DocField.ID);
		//fields.add(SearchManager.DocField.TEXT);
		fields.add(Indexer.DocField.TERM_VECTOR);
		fields.add(Indexer.DocField.PERIOD);
		fields.add(Indexer.DocField.SOURCE);
		fields.add(Indexer.DocField.LENGTH);
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		manager.index2Corpora(analyzer, reader1, reader2, indexDir , fields, false, true);
		analyzer.close();
		
		end = new Date().getTime();
		System.out.println("total run time : "+(end-start)/1000+" seconds"+"("+(end-start)/60000+" minutes)");
	}

}
