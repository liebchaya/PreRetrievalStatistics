package scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

public class GenerateTrainAndTestSet {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("C:\\Documents and Settings\\HZ\\Desktop\\thesaurus terms\\fromJonathan.txt"));
		ArrayList<String> posExmples = new ArrayList<String>();
		ArrayList<String> negExmples = new ArrayList<String>();
		// skip the first line
		String line = reader.readLine();
		line = reader.readLine();
		while (line!=null){
			String[] tokens = line.split("\t");
			int elazar = (tokens[1].isEmpty()?0:Integer.parseInt(tokens[1]));
			int jonathan = (tokens[2].isEmpty()?0:Integer.parseInt(tokens[2]));
			if (elazar == 1 && jonathan == 1)
				posExmples.add(tokens[0]);
			else if (elazar != 1 && jonathan != 1)
				negExmples.add(tokens[0]);
			line = reader.readLine();
		}
		reader.close();
		System.out.println(posExmples.size() + " positive examples");
		System.out.println(negExmples.size() + " negative examples");
		Random r = new Random();
		ArrayList<String> randPosExamples = new ArrayList<String>();
		ArrayList<String> randNegExamples = new ArrayList<String>();
		
		BitSet randSet = new BitSet(posExmples.size());
		int counter = 0 ;
		while (counter < 500){
		   int randIndex = r.nextInt(posExmples.size());
		   if (!randSet.get(randIndex)){
			   randSet.set(randIndex);
			   randPosExamples.add(posExmples.get(randIndex));
			   counter ++;
		   } 
		}
		System.out.println(randPosExamples);
		System.out.println(randPosExamples.size() + " randomly selected examples");
		
		randSet = new BitSet(negExmples.size());
		counter = 0 ;
		while (counter < 500){
		   int randIndex = r.nextInt(negExmples.size());
		   if (!randSet.get(randIndex)){
			   randSet.set(randIndex);
			   randNegExamples.add(negExmples.get(randIndex));
			   counter ++;
		   } 
		}
		System.out.println(randNegExamples);
		System.out.println(randNegExamples.size() + " randomly selected examples");
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Documents and Settings\\HZ\\Desktop\\thesaurus terms\\TrainSet.txt"));
		for(int i=0; i<250; i++)
			writer.write(randNegExamples.get(i) + "\t0\n");
		for(int i=0; i<250; i++)
			writer.write(randPosExamples.get(i) + "\t1\n");
		writer.close();
		
		writer = new BufferedWriter(new FileWriter("C:\\Documents and Settings\\HZ\\Desktop\\thesaurus terms\\TestSet.txt"));
		for(int i=250; i<500; i++)
			writer.write(randNegExamples.get(i) + "\t0\n");
		for(int i=250; i<500; i++)
			writer.write(randPosExamples.get(i) + "\t1\n");
		writer.close();
	}

}
