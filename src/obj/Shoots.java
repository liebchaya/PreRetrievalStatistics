package obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Shoots information (loaded from a file)
 * @author HZ
 *
 */
public class Shoots {
	public static HashMap<String,Shoot> shoots = new HashMap<String, Shoot>();
	
	private  String shootsfile ;
	private  String shootsDir ;
	
	/**
	 * @param sf
	 * @param sd
	 */
	public Shoots(String sf,String sd){
		shootsfile = sf;
		shootsDir = sd;
	}
	
	/**
	 * Loads Shoots information
	 * @throws IOException
	 */
	public void loadShoots() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader( new File(shootsfile)));
		String line = br.readLine();
		while (line!=null){
			//System.out.println(line);
			String[] lineArr = line.split("\t");
			Shoot shoot = new Shoot(lineArr[0],lineArr[1],lineArr[2],shootsDir,lineArr[3],lineArr[4],lineArr[5],lineArr[6]);
			System.out.println(shoot.toString());
			shoots.put(lineArr[0],shoot);
			line = br.readLine();
		}
	}
	
	/**
	 * Gets Shoot by its id (folder name)
	 * @param id
	 * @return Shoot object
	 */
	public Shoot getShootById(String id){
		if (id.startsWith("000"))
			return shoots.get(id);
		System.out.println(id+"- id is not legal");
		return null;
	}
	
	/**
	 * Gets Shoot by its folder name 
	 * @param dir
	 * @return Shoot object
	 */
	public Shoot getShootByDir(String dir){
		if (!dir.startsWith(shootsDir)){
				System.out.println(dir+" - directory is not in corpus!!");
				return null;	
		}
		String[] arr = dir.split("\\\\");
		int n = arr.length;
		String dirName = arr[n-1];
		String id = dirName.substring(0, 6);
		return shoots.get(id);
	}
	
	/**
	 * Gets Shoot by its file name 
	 * @param filename
	 * @return Shoot object
	 */
	public Shoot getShootByFile(String filename){
		File f = new File(filename);		
		
		if (!filename.startsWith(shootsDir)){
			System.out.println(filename +" - is not in responsa corpus !!!");
			System.out.println(filename + " - is unknown file");
			return null;
		}
			
		if (f.isDirectory()){
			System.out.println(filename = " - is directory and not a file!!");
			return null;
		}
		File p = f.getParentFile();
		File d = new File(shootsDir);
		File temp = f;
		while(!p.equals(d)){
			temp = p;
			p = p.getParentFile();	
		}
		//here, temp is the shoot folder of the given file
		String tempStr = temp.getName();
		String tempId = tempStr.substring(0, 6);
		return getShootById(tempId);
		
	}
	 

}
