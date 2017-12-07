package xml.saveAndLoad;

import java.io.File;
import java.util.ArrayList;

public interface SaveAndLoad {

	/**
	 * current xml file
	 */
	File currentFile = null;
	/**
	 * current loaded data form xml file and if the file changed ,we will change
	 * the data else we will return the value of this variable using getData()
	 * method.
	 */
	ArrayList<ArrayList<String>> currentData = null;

	/**
	 * save modified data to a specific file.
	 * 
	 * @param file
	 *            file to save data to.
	 * @param data
	 *            the data after any modifications.
	 */
	public void save(File file, ArrayList<ArrayList<String>> data, ArrayList<String> coulmnNames,
			ArrayList<String> coulmnTypes, String tableName);

	/**
	 * load data form a specific file and if the file didn't change we will
	 * return currentData variable.
	 * 
	 * @param file
	 * @return data from the xml file.
	 */
	public ArrayList<ArrayList<String>> load(File file);

	public ArrayList<String> getCoulmnNames();

	public ArrayList<String> getCoulmnTypes();
}
