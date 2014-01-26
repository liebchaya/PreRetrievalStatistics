package scripts;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class FilterTerms {
	public static final Set<String> writers = new HashSet<String>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
			    new FileInputStream("C:\\ResponsaClean\\ResponsaAllInfo.info"), "UTF-8"));
		String line = reader.readLine();
		while(line != null){
			writers.add(line.split("\t")[4]);
			line = reader.readLine();
		}
		reader.close();
		reader = new BufferedReader(new FileReader("C:\\Documents and Settings\\HZ\\Desktop\\PreRetrieval\\TrainSet.txt"));
		line = reader.readLine();
		while(line != null){
			String term = line.split("\t")[0];
			for(String writer:writers)
				if(writer.contains(term)&&term.split(" ").length>1){
					System.out.println(term);
					break;
				}
			line = reader.readLine();
		}
		reader.close();

	}

}
