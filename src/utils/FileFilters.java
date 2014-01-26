package utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * File filters (by file expansion)
 * @author HZ
 *
 */
public class FileFilters {
	/**
	 * Accept all ".text" or ".txt" files
	 */
	public static class TextFileFilter implements FileFilter {

		public boolean accept(File pathname) {
			try {
				if (pathname.getCanonicalPath().toLowerCase().endsWith(".text")
						|| pathname.getCanonicalPath().toLowerCase().endsWith(".txt")
						|| pathname.getCanonicalPath().toLowerCase().endsWith(".org"))
					return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	}
	
	/**
	 * Accept all ".text" or ".txt" files or files with no expansion
	 */
	public static class TextNoFileFilter implements FileFilter {

		public boolean accept(File pathname) {
			try {
				if (pathname.getCanonicalPath().toLowerCase().endsWith(".text")
						|| pathname.getCanonicalPath().toLowerCase().endsWith(".txt")
						|| pathname.getCanonicalPath().toLowerCase().endsWith(".org"))
					return true;
				if (!pathname.getCanonicalPath().contains("."))
					return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
	}

}