package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.mozilla.universalchardet.UniversalDetector;

/**
 * Utilities for file treatment
 * @author HZ
 *
 */
public class FileUtils {
	
	/**
	 * Reads text from an open reader into a list of strings, each containing one line of the file.
	 * @param reader
	 * @return list of strings (each line is a string)
	 * @throws IOException
	 */
	public static List<String> loadReaderToList(Reader reader) throws IOException {
		List<String> outList = new LinkedList<String>();
		String line;
		BufferedReader bufferedReader = new BufferedReader(reader);
		while ((line = bufferedReader.readLine()) != null) 
			outList.add(line);
		return outList;
	}
	
	/**
	 * Reads text from a local file into a list of strings, each containing one line of the file.
	 * @param iFile
	 * @return list of strings (each line is a string)
	 * @throws IOException
	 */
	public static List<String> loadFileToList(File iFile) throws IOException {
		return loadReaderToList(new FileReader(iFile));
	}
	
		
	/**
	 * Reads a file into a String
	 * @param file
	 * @param encoding
	 * @return a string with all the file content
	 * @throws IOException
	 */
	public static String loadFileToString(File file, String encoding) throws IOException 
	{
		/*
		* To convert the File to String we use the
		* Reader.read(char[] buffer) method. We iterate until the
		* Reader return -1 which means there's no more data to
		* read. We use the StringWriter class to produce the string.
		*/
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
		int n;
		while ((n = reader.read(buffer)) != -1) {
		writer.write(buffer, 0, n);
		}
		reader.close();
		return writer.toString();
	}
	
	/**
	 * Reads a text file into a list of strings, each containing one line of the file
	 * @param iFile
	 * @return a set of strings
	 * @throws IOException
	 */
	public static HashSet<String> loadFileToSet(File iFile) throws IOException {
		HashSet<String> outList = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(iFile));
		String line;
		while ((line = reader.readLine()) != null) {
			outList.add(line);
		}
		reader.close();
		return outList;
	}

	/**
	 * Deletes a directory
     * By default File#delete fails for non-empty directories, it works like "rm". 
     * We need something a little more brutual - this does the equivalent of "rm -r"
     * @param path Root File Path
     * @return true iff the file and all sub files/directories have been removed
     * @throws FileNotFoundException
     */
    public static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && FileUtils.deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }
    
    /**
	 * Detects a file encoding
	 * @param file
	 * @return encoding string
	 * @throws IOException
	 */
	public static String getFileEncoding(File file) throws IOException{
		 byte[] buf = new byte[4096];
		    java.io.FileInputStream fis = new java.io.FileInputStream(file);

		    // (1)
		    UniversalDetector detector = new UniversalDetector(null);

		    // (2)
		    int nread;
		    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
		      detector.handleData(buf, 0, nread);
		    }
		    // (3)
		    detector.dataEnd();

		    // (4)
		    String encoding = detector.getDetectedCharset();
		    if (encoding != null) {
		      System.out.println("Detected encoding = " + encoding);
		    } else {
		      System.out.println("No encoding detected.");
		    }

		    // (5)
		    detector.reset();
		    return encoding;
	}



}
