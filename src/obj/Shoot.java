package obj;

import java.io.File;
import java.util.HashSet;

/**
 * Shoot information
 * @author HZ
 *
 */
public class Shoot {

	
	private String id;
	private String engName;
	private String hebName;
	private String rootDir;
	private String year;
	private String period;
	private String periodId;
	private String writer;
	private HashSet<String> files =  new HashSet<String>();
	
	/**
	 * @param i
	 * @param heb
	 * @param eng
	 * @param shootsDir
	 * @param y
	 * @param w
	 * @param per
	 * @param perId
	 */
	public Shoot(String i,String heb,String eng, String shootsDir, String y,String w, String per, String perId){
		id=i;
		engName=eng;
		hebName=heb;
		if (id.startsWith("000"))
			rootDir=shootsDir+"/"+id+eng;
		else {	rootDir=""; 
				//System.out.println(i+eng+heb+" - not in corpus!!");
		}
		
		year=y;
		period=per;
		periodId=perId;
		writer = w;
		File dir = new File(rootDir);
		if (!dir.isDirectory())
			System.out.println(rootDir+" - is not a directory !!");
		else {
			String[] list = dir.list();
			for (String file: list)
				files.add(file);				
		}
	}
	
	/**
	 * Checks whether the file is in the shoot
	 * @param filename
	 * @return true/false
	 */
	public boolean contains(String filename){
		return files.contains(filename);
	}
	
	/**
	 * Gets Shoot id
	 * @return Shoot id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets Shoot English name
	 * @return Shoot English name
	 */
	public String getEngName() {
		return engName;
	}

	/**
	 * Sets Shoot Hebrew name
	 * @return Shoot Hebrew name
	 */
	public String getHebName() {
		return hebName;
	}

	/**
	 * Gets root directory
	 * @return root directory
	 */
	public String getRootDir() {
		return rootDir;
	}

	/**
	 * Gets Shoot year
	 * @return Shoot year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * Gets Shoot period (out of 4 periods)
	 * @return Shoot period
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 * Gets Shoot period id (1-4)
	 * @return Shoot period id
	 */
	public String getPeriodId() {
		return periodId;
	}

	/**
	 * Gets Shoot writer
	 * @return Shoot writer
	 */
	public String getWriter() {
		return writer;
	}
	
	/**
	 * Gets Shoot files
	 * @return Shoot files
	 */
	public HashSet<String> getFiles(){
		return files;
	}
	
	/**
	 * Gets full Shoot information
	 * @return Shoot information (string)
	 */
	public String toString(){
		return id+"\t"+hebName+"\t"+engName+"\t"+year+"\t"+writer+"\t"+period+"\t"+periodId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((engName == null) ? 0 : engName.hashCode());
		result = prime * result + ((files == null) ? 0 : files.hashCode());
		result = prime * result + ((hebName == null) ? 0 : hebName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((periodId ==null) ? 0 : periodId.hashCode());
		result = prime * result + ((rootDir == null) ? 0 : rootDir.hashCode());
		result = prime * result + ((writer == null) ? 0 : writer.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shoot other = (Shoot) obj;
		if (engName == null) {
			if (other.engName != null)
				return false;
		} else if (!engName.equals(other.engName))
			return false;
		if (files == null) {
			if (other.files != null)
				return false;
		} else if (!files.equals(other.files))
			return false;
		if (hebName == null) {
			if (other.hebName != null)
				return false;
		} else if (!hebName.equals(other.hebName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (periodId != other.periodId)
			return false;
		if (rootDir == null) {
			if (other.rootDir != null)
				return false;
		} else if (!rootDir.equals(other.rootDir))
			return false;
		if (writer == null) {
			if (other.writer != null)
				return false;
		} else if (!writer.equals(other.writer))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}


	
}
