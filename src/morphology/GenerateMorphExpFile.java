/**
 * Morphology nice package
 */
package morphology;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Common morphology prefixes for target term expressions' generation
 * @author HZ
 *
 */
public class GenerateMorphExpFile {
	
	public static String[] prefixes = {"о", "щ", "д", "е", "л", "м" , "б", "ео", "ещ", "ед", "ел", "ем" , "еб"};

	/**
	 * Generates the target term's morphological variants, file name should end with "_orig.txt" 
	 * @param targetTermFile original target terms' list
	 * @param index in order to avoid complex queries with terms that don't appear in the corpus
	 * @param firstId first id of the terms that should be treated
	 * @return String re-formated target terms' file with morphology prefixes
	 * @throws IOException
	 * @throws ParseException 
	 */
	public static String generateMorphExpFile(String targetTermFile, IndexSearcher iSearcher) throws IOException, ParseException {
		 ComplexPhraseQueryParser parser = new ComplexPhraseQueryParser(Version.LUCENE_48, "TERM_VECTOR", new StandardAnalyzer(Version.LUCENE_48));
		IndexReader reader = iSearcher.getIndexReader();
		BufferedReader fileReader = new BufferedReader(new FileReader(targetTermFile));
		String morphFile = targetTermFile.replace("_orig.txt","_morph.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(morphFile));
		String line = fileReader.readLine();
		while (line != null) {
			String[] tokens = line.split("\t");
			String morphExpan = "";
			for(String clsElem:tokens[0].split(",")){
				String[] ngram = clsElem.trim().split(" ");
				String term;
				int freq;
				if (ngram.length == 2) { // bigram expression
					for(String prefix:prefixes) {
						term = prefix + ngram[0] + " " + ngram[1];
//						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						Query query =parser.parse("\""+term+ "\"");
						TotalHitCountCollector collector = new TotalHitCountCollector();
						iSearcher.search(query, collector);
					    freq = collector.getTotalHits();
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
						// try to add д before the second word
						term = prefix + ngram[0] + " д" + ngram[1];
//						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						query =parser.parse("\""+term+ "\"");
						collector = new TotalHitCountCollector();
						iSearcher.search(query, collector);
					    freq = collector.getTotalHits();
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
					}
					// try to add д before the second word, without any other prefix
					term = ngram[0] + " д" + ngram[1];
//					freq = reader.docFreq(new Term("TERM_VECTOR",term));
					Query query =parser.parse("\""+term+ "\"");
					TotalHitCountCollector collector = new TotalHitCountCollector();
					iSearcher.search(query, collector);
				    freq = collector.getTotalHits();
					if (freq > 0)
						morphExpan = morphExpan + "\t" + term;
					// try to replace н in 
					if (ngram[1].endsWith("н")) {
						term = ngram[0] + " " + ngram[1].substring(0,ngram[1].length()-1) + "п";
//						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						query =parser.parse("\""+term+ "\"");
						collector = new TotalHitCountCollector();
						iSearcher.search(query, collector);
					    freq = collector.getTotalHits();
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
						}
				}
				if (ngram.length == 3) { // trigram expression
					for(String prefix:prefixes) {
						term = prefix + ngram[0] + " " + ngram[1] + " " + ngram[2];
//						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						Query query =parser.parse("\""+term+ "\"");
						TotalHitCountCollector collector = new TotalHitCountCollector();
						iSearcher.search(query, collector);
					    freq = collector.getTotalHits();
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
						// try to add д before the second word
						term = prefix + ngram[0] + " д" + ngram[1] + " " + ngram[2];
//						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						query =parser.parse("\""+term+ "\"");
						collector = new TotalHitCountCollector();
						iSearcher.search(query, collector);
					    freq = collector.getTotalHits();
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
						// try to add д before the third word
						term = prefix + ngram[0] + " " + ngram[1] + " д" + ngram[2];
//						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						query =parser.parse("\""+term+ "\"");
						collector = new TotalHitCountCollector();
						iSearcher.search(query, collector);
					    freq = collector.getTotalHits();
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
						// try to add д before the second and the third word
						term = prefix + ngram[0] + " д" + ngram[1] + " д" + ngram[2];
//						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						query =parser.parse("\""+term+ "\"");
						collector = new TotalHitCountCollector();
						iSearcher.search(query, collector);
					    freq = collector.getTotalHits();
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
					}
					// try to add д before the second word, without any other prefix
					term = ngram[0] + " д" + ngram[1] + " " + ngram[2];
//					freq = reader.docFreq(new Term("TERM_VECTOR",term));
					Query query =parser.parse("\""+term+ "\"");
					TotalHitCountCollector collector = new TotalHitCountCollector();
					iSearcher.search(query, collector);
				    freq = collector.getTotalHits();
					if (freq > 0)
						morphExpan = morphExpan + "\t" + term;
					// try to add д before the second and the third word without any other prefix
					term = ngram[0] + " д" + ngram[1]  + " д" + ngram[2];
//					freq = reader.docFreq(new Term("TERM_VECTOR",term));
					query =parser.parse("\""+term+ "\"");
					collector = new TotalHitCountCollector();
					iSearcher.search(query, collector);
				    freq = collector.getTotalHits();
					if (freq > 0)
						morphExpan = morphExpan + "\t" + term;
					// try to add д before the  the third word without any other prefix
					term = ngram[0] + " " + ngram[1]  + " д" + ngram[2];
//					freq = reader.docFreq(new Term("TERM_VECTOR",term));
					query =parser.parse("\""+term+ "\"");
					collector = new TotalHitCountCollector();
					iSearcher.search(query, collector);
				    freq = collector.getTotalHits();
					if (freq > 0)
						morphExpan = morphExpan + "\t" + term;
					// try to replace н in 
					if (ngram[2].endsWith("н")) {
						term = ngram[0] + " " + ngram[1] + " " + ngram[2].substring(0,ngram[2].length()-1) + "п";
//						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						query =parser.parse("\""+term+ "\"");
						collector = new TotalHitCountCollector();
						iSearcher.search(query, collector);
					    freq = collector.getTotalHits();
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
						}
				}
				if (ngram.length == 1) { // unigram expression
					for(String prefix:prefixes) {
						term = prefix + ngram[0];
						freq = reader.docFreq(new Term("TERM_VECTOR",term));
						if (freq > 0)
							morphExpan = morphExpan + "\t" + term;
					}
				}
			}
			String newLine = line.trim().split("\t")[0] + "\t" + morphExpan.trim();
			writer.write(newLine.trim() + "\n");
			line = fileReader.readLine();
		}
		reader.close();
		writer.close();
		fileReader.close();
		return morphFile;
	}

}
