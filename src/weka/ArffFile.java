package weka;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.Pair;

/**
 * Train/Test Weka (.arff) file generation 
 * @author HZ
 *
 */
public class ArffFile {

	/**
	 * Initialization
	 * @throws IOException 
	 */
	public ArffFile(String outputFile) throws IOException{
		m_attributes = new ArrayList<Pair<String,String>>();
		m_writer = new BufferedWriter(new FileWriter(outputFile));
	}
	
	public void close() throws IOException{
		m_writer.close();
	}
	
	/**
	 * Writes a line of features' data
	 * @param dataLine
	 * @throws IOException
	 */
	public void writeDataLine(String dataLine) throws IOException{
		if (dataLine.split(",").length==m_attributes.size())
			m_writer.write(dataLine+"\n");
	}
	/**
	 * Writes .arff file's header
	 * @throws IOException
	 */
	public void writeArffHeader() throws IOException{
		if(m_writer!=null){
			if (m_relation!=null)
				writeRelation();
			if (m_attributes != null) {
				writeAttributes();
				m_writer.write("@DATA\n");
			}
		}
	}
	/**
	 * Adds an attribute to the attribute list
	 * @param attName
	 * @param attType
	 */
	public void addAttribute(String attName, String attType){
		m_attributes.add(new Pair<String, String>(attName,attType));
	}
	
	/**
	 * Writes the attributes' section of the arff file
	 * @throws IOException
	 */
	private void writeAttributes() throws IOException{
		for(Pair<String,String> att:m_attributes)
			m_writer.write("@ATTRIBUTE "+att.getKey()+" "+att.getValue()+"\n");
		m_writer.write("\n");
	}
	
	/**
	 * Sets the relation name (not necessary)
	 * @param relation
	 */
	public void setRelation(String relation) {
		m_relation = relation;
	}
	
	/**
	 * Gets the relation name (not necessary)
	 * @param relation
	 */
	public String getRelation(String relation) {
		return m_relation;
	}
	
	private void writeRelation() throws IOException{
		m_writer.write("@RELATION "+m_relation+"\n\n");
	}
	
	private List<Pair<String,String>> m_attributes;
	private String m_relation = null;
	private BufferedWriter m_writer = null;
}
