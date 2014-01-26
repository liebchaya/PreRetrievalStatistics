/**
 * 
 */
package index;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import obj.Shoot;
import obj.Shoots;
import utils.FileFilters;
import utils.FileUtils;
import utils.RecursiveFileListIterator;
import utils.Utils;


/**
 * Responsa document reader
 * @author HZ
 */
public class ResponsaDocReader extends DocReader
{
	private static final String SPACE = " ";
	private RecursiveFileListIterator iter;
	private String currID = null;
	private File currFile;
	private StringReader currReader;
	private Iterator<String> termIter;
	private String currDocText;
	private String inputDir;
	private Shoots shoots;
//	private String encoding = null;

	/**
	 * @param dir folder containing files to read
	 * @throws IndexerException
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	public ResponsaDocReader(File dir) throws IndexerException, InstantiationException, IOException
	{
		super();
		if (dir == null || !dir.exists())
			throw new IndexerException("the directory is null or nonexistant");
		init(dir);
	}
	
	/* (non-Javadoc)
	 * @see new_search.DocReader#next()
	 */
	@Override
	public boolean next() throws IndexerException
	{
		List<String> words = new LinkedList<String>(); 
		boolean ret;
		if (ret = iter.hasNext())
		{
			currFile = iter.next();
			try
			{
//				if(encoding == null)
//					encoding = FileUtils.getFileEncoding(currFile);
				if (currFile.getAbsolutePath().endsWith(".org")) {
					currDocText = FileUtils.loadFileToString(currFile,"Windows-1255");
					currDocText = currDocText.replaceAll("[\\x07\t\f\n-]", " ");
					currDocText = currDocText.replaceAll("[^�?בגדה�?וזחטיכג�?נסעפצקרשתץף�?�?�?ת\'\\s\"]", "");
				}
				else {
					currDocText = FileUtils.loadFileToString(currFile,"UTF-8");
					currDocText = currDocText.replaceAll("(\\p{P})", "");
				}
//				termIter = getNgrams(currDocText, 3).iterator();
				termIter = Utils.arrayToCollection(currDocText.split(SPACE),words).iterator();
				if (currReader != null)
					currReader.close();
				currReader = new StringReader(currDocText);
			} catch (FileNotFoundException e)
			{
				throw new IndexerException("bad file " + currFile);
			} catch (IOException e)
			{
				throw new IndexerException("IO error with this file or the one before it "+ currFile);
			}
			currID = currFile.getAbsolutePath();
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see new_search.DocReader#docId()
	 */
	@Override
	public String docId() 
	{
		return currID;
	}

	/* (non-Javadoc)
	 * @see new_search.DocReader#doc()
	 */
	@Override
	public String doc()
	{
		return currDocText;
	}

	/* (non-Javadoc)
	 * @see new_search.DocReader#readToken()
	 */
	@Override
	public String readToken() throws IndexerException
	{
		if (termIter == null)
			throw new IndexerException("you must call next() before calling readToken()");
		
		return termIter.hasNext() ? termIter.next() : null;
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return currReader.read(cbuf, off, len);
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException
	{
		if (currReader != null)
			currReader.close();
	}

	/* (non-Javadoc)
	 * @see new_search.DocReader#period()
	 */
	@Override
	public String period(){
		Shoot s = shoots.getShootByFile(currID);
		return s.getPeriodId();
	}

	/* (non-Javadoc)
	 * @see new_search.DocReader#source()
	 */
	@Override
	public String source() {
		return getSourceName();
	}
	
	/* (non-Javadoc)
	 * @see new_search.DocReader#length()
	 */
	@Override
	public String length(){
		return Integer.toString(currDocText.split(" ").length);
	}

	//
	////////////////////////////////////////////////////////// PRIVATE /////////////////////////////////////////////
	//
	
	/**
	 * @param dir folder containing files to read
	 * @throws IndexerException
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	private void init(File dir) throws IndexerException, InstantiationException, IOException
	{
		inputDir = dir.getAbsolutePath();
		iter = new RecursiveFileListIterator(dir, new FileFilters.TextFileFilter()); 
		shoots = new Shoots(inputDir+"/ResponsaAllInfo.info",inputDir);
		shoots.loadShoots();
	}
	
	/**
	 * @return String containing upper folder name to be used as a source name
	 */
	private String getSourceName() 
	{
		File p = currFile.getParentFile();
		File d = new File(inputDir);
		File temp = currFile;
		while(!p.equals(d)){
			temp = p;
			p = p.getParentFile();	
		}
		//here, temp is the upper folder of the given file
		String tempStr = temp.getName();
		return tempStr;
	}
	
	/*
	private List<String> getNgrams(String content, int n){
		LinkedList<String> ngrams = new LinkedList<String>();
		StringTokenizer tokens = new StringTokenizer(content);
		String[] tokenArr = new String[n];
		String ngram = "";
		for (int i=0; i<n-1; i++)
			if (tokens.hasMoreTokens()){
				tokenArr[i] = tokens.nextToken();
		}
		while(tokens.hasMoreTokens()){
			tokenArr[n-1] = tokens.nextToken();
			ngram = "";
			for (int i=0; i<n; i++)
				ngram = ngram+  " " + tokenArr[i];
			ngram = ngram.trim();
			ngrams.add(ngram);
			for (int i=0; i<n-1; i++){
				tokenArr[i] = tokenArr[i+1]; // step forward
			}
		}
		return ngrams;
	}
	*/
}
	
