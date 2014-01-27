package index;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import utils.FileFilters;
import utils.FileUtils;
import utils.RecursiveFileListIterator;
import utils.Utils;


/**
 * Modern jewish document reader
 * @author HZ
 */
public class ModernJewishDocReader extends DocReader
{
	private static final String SPACE = "\\s+";
	private RecursiveFileListIterator iter;
	private String currID = null;
	private File currFile;
	private StringReader currReader;
	private Iterator<String> termIter;
	private String currDocText;
	private String inputDir;
	private String encoding = null;
	private String currSource = null;

	/**
	 * @param dir folder containing files to read
	 * @throws IndexerException
	 * @throws InstantiationException 
	 */
	public ModernJewishDocReader(File dir) throws IndexerException, InstantiationException
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
//				currDocText = FileUtils.loadFileToString(currFile);
				String source = getSourceName();
				if(encoding == null || !source.equals(currSource))
					encoding = FileUtils.getFileEncoding(currFile);
				currDocText = FileUtils.loadFileToString(currFile,encoding);
				currDocText = currDocText.replaceAll("[\\x07\t\f\n-]", " ");
				currDocText = currDocText.replaceAll("[^אבגדהלוזחטיכגמנסעפצקרשתץףךםןת\'\\s\"]", "");
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
	 * @see index.DocReader#docId()
	 */
	@Override
	public String docId() throws IndexerException
	{
		return currID;
	}

	/* (non-Javadoc)
	 * @see index.DocReader#doc()
	 */
	@Override
	public String doc()
	{
		return currDocText;
	}

	/* (non-Javadoc)
	 * @see index.DocReader#readToken()
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
	 * @see index.DocReader#period()
	 */
	@Override
	public String period() throws IndexerException {
		// modern period = 4
		// only Rambam period = 1
		if (getSourceName().equals("Rambam"))
			return "1";
		return "4";
	}

	/* (non-Javadoc)
	 * @see index.DocReader#source()
	 */
	@Override
	public String source() throws IndexerException {
		// source name is the upper directory name
		currSource = getSourceName();
		return currSource;
	}
	
	@Override
	public int length() throws IndexerException {
		// TODO Auto-generated method stub
		return currDocText.split(" ").length;
	}
	
	//
	////////////////////////////////////////////////////////// PRIVATE /////////////////////////////////////////////
	//
	
	/**
	 * Initializes folders' files
	 * @param dir folder containing files to read
	 * @throws IndexerException
	 * @throws InstantiationException 
	 */
	private void init(File dir) throws IndexerException, InstantiationException
	{
		inputDir = dir.getAbsolutePath();
		iter = new RecursiveFileListIterator(dir, new FileFilters.TextNoFileFilter()); 
	}
	
	/**
	 * Gets the source folder
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

	
}
	
