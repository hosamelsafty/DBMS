package dbms.parser;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dataBase.control.DataBaseControl;
import dataBase.control.DataBaseControlImpl;

public class Parser implements IParser {
	
	private static Parser instanceParser; 
	/**
	 * object of DB controller
	 */
	private DataBaseControl Dpms; 
	/**
	 * boolean to syntax error
	 */
	private boolean error;
	
	private Parser() {
		Dpms = DataBaseControlImpl.getInstance(); 
		error = false;
	}
	
	
	/**
	 * assign error
	 */
	private void error() {
		error = true;
	}

	/**
	 * take a Query and check if it is a valid operation then call the operation
	 * method.
	 * 
	 * @param query
	 * @throws SQLException
	 */

	@Override
	public void InsertQuery(String query) throws SQLException {
		if (query == null || query.replaceAll("^\\s*", "").equals("")) {
			error();
			return;
		}
		ArrayList<String> words = new ArrayList<String>(Arrays.asList(query.toLowerCase().split("\\s+")));
		while (words.remove(""))
		;
		switch (words.get(0).toLowerCase()) {
		case "select":
			Select(query);
			break;
		case "drop":
			if (words.get(1).equals("table")) {
				DropTable(query);
			} else if (words.get(1).equals("database")) {
				DropDataBase(query);
			} else {
				error();
			}
			break;
		case "delete":
			Delete(query);
			break;
		case "update":
			Update(query);
			break;
		case "use":
			useDatabase(query);
			break;
		case "insert":
			Insert(query);
			break;
		case "create":
			if (words.get(1).equals("table")) {
				CreateTable(query);
			} else if (words.get(1).equals("database")) {
				CreateDataBase(query);
			} else {
				error();
				return;
			}
			break;
		default:
			error();
			break;
		}
		if (error) {
			error = false;
			throw new SQLException("SQL syntax error");
		}
	}

	public void CreateDataBase(String query) {
		Pattern pat = Pattern.compile("^(\\s*)((?i)create)(\\s+)((?i)database)(\\s+)(\\w+)(\\s*);?(\\s*)$");
		Matcher ma = pat.matcher(query);
		if (ma.matches()) {
			Dpms.createDataBase(ma.group(6));
		} else {
			error();
			return;
		}
	}

	/**
	 * check create table syntax and extract data
	 * 
	 * @param query
	 */
	public void CreateTable(String query) {
		String table_name = new String();
		ArrayList<String> coloums = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		Pattern pat = Pattern.compile("^(\\s*)((?i)create)(\\s+)((?i)table)(\\s+)(\\w+)(\\s*)"
				+ "\\((((\\s*)(\\w+)(\\s+)((?i)varchar|(?i)int)(\\s*),)*((\\s*)(\\w+)(\\s+)((?i)varchar|(?i)int)"
				+ "(\\s*))\\)(\\s*))(\\s*);?(\\s*)$");
		Matcher ma = pat.matcher(query);
		if (ma.matches()) {
			table_name = ma.group(6);
			ArrayList<String> NameAndType = new ArrayList<String>(
					Arrays.asList(ma.group(8).replaceAll("[()]", "").split(",")));
			int size = NameAndType.size();
			for (int i = 0; i < size; i++) {
				ArrayList<String> temp = new ArrayList<String>(Arrays.asList(NameAndType.get(i).split("\\s+")));
				if (temp.get(0).equals(""))
					temp.remove(0);
				coloums.add(temp.get(0));
				value.add(temp.get(1));
			}
			Dpms.createTable(table_name, coloums, value);
		}
		else{
			error();
			return;
		}
	}

	/**
	 * check insert syntax and extract data
	 * 
	 * @param query
	 */
	public void Insert(String query) {
		String table_name = new String();
		ArrayList<String> coloums = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		Pattern pat = Pattern.compile("^(\\s*)((?i)insert)(\\s+)((?i)into)(\\s+)(\\w+)"
				+ "((\\s*)\\(((\\s*)(\\w+)(\\s*),)*((\\s*)(\\w+)(\\s*)\\)(\\s*)))?" + "((\\s+)((?i)values))(\\s*)\\("
				+ "(((\\s*)(('[^']*')|(\\d+))(\\s*),)*((\\s*)(('[^']*')|(\\d+))))(\\s*)\\)(\\s*)(\\s*);?(\\s*)$");
		Matcher ma = pat.matcher(query.replaceAll("\\)(?i)values", "\\) values"));
		if (ma.matches()) {
			table_name = ma.group(6);
			if (ma.group(7) == null)
				coloums = new ArrayList<String>();
			else
				coloums = new ArrayList<String>(
						Arrays.asList(ma.group(7).replaceAll("\\s+", "").replaceAll("[()]", "").split(",")));
			String trim = ma.group(22) + ",";
			while (!trim.replaceAll("\\s+", "").replaceAll("[,]", "").equals("")) {
				Pattern patt = Pattern.compile("('(\\s*[^']+\\s*)')|(\\s*(\\d+)\\s*,)");
				Matcher matc = patt.matcher(trim);

				if (matc.find()) {
					if (trim.charAt(matc.start()) == '\'') {
						value.add(trim.substring(matc.start() + 1, matc.end() - 1));
					} else {
						value.add(trim.substring(matc.start(), matc.end() - 1));
					}
					trim = trim.substring(matc.end());
				}
			}
			Dpms.insertIntoTable(coloums, value, table_name);
		} else {
			error();
			return;
		}
	}

	/**
	 * check use table syntax and extract table name
	 * 
	 * @param query
	 */
	public void useDatabase(String query) {
		String usePattern = "^\\s*((?i)use)\\s+(\\w+)\\s*;?\\s*$";
		Pattern pat = Pattern.compile(usePattern);
		Matcher ma = pat.matcher(query);
		if (!ma.matches()) {
			error();
			return;
		}
		Dpms.changeDataBase(ma.group(2));
	}

	/**
	 * check drop database syntax and extract data
	 * 
	 * @param query
	 */
	public void DropDataBase(String query) {
		String usePattern = "^\\s*((?i)drop)\\s+((?i)database)\\s+(\\w+)\\s*;?\\s*$";
		Pattern pat = Pattern.compile(usePattern);
		Matcher ma = pat.matcher(query);
		if (!ma.find()) {
			error();
			return;
		}
		Dpms.dropDataBase(ma.group(3));
	}

	/**
	 * check drop table syntax and extract data
	 * 
	 * @param query
	 */
	public void DropTable(String query) {
		String DropPattern = "^\\s*((?i)drop)\\s+((?i)table)\\s+(\\w+)\\s*;?\\s*$";
		Pattern pat = Pattern.compile(DropPattern);
		Matcher ma = pat.matcher(query);
		if (!ma.find()) {
			error();
			return;
		}
		Dpms.dropTable(ma.group(3));
	}

	/**
	 * check delete syntax and extract data
	 * 
	 * @param query
	 */
	public void Delete(String query) {
		Pattern pat = Pattern.compile("^(\\s*)((?i)delete)(\\s+)((?i)from)(\\s+)(\\w+)"
				+ "(\\s+((?i)where)\\s+((\\w+)(\\s*)(>|<|=|>=|<=|<>)\\s*(('[^']*')|(\\d+))))?\\s*;?$");
		Matcher ma = pat.matcher(query);
		if (ma.matches()) {
			Dpms.deleteFromTable(Where((ma.group(9))), ma.group(6));
		} else {
			error();
			return;
		}
	}

	/**
	 * check select syntax and extract data selected
	 * 
	 * @param query
	 */
	public void Select(String query) {
		query = query.replaceFirst("^\\s*((?i)select\\s*\\*)", "select * ");
		ArrayList<String> colomsName = new ArrayList<>();
		String tableName, order = "asc";
		String selectPattern = "^\\s*((?i)select\\s+)"
				+ "((\\*\\s*)|((\\s*(\\w+)\\s*,)*(\\s*(\\w+)\\s+)))\\s*((?i)from\\s+)(\\w+)"
				+ "(\\s+((?i)where)\\s+((\\w+)(\\s*)(>|<|=|>=|<=|<>)\\s*(('[^']*')|(\\d+))))?"
				+ "(\\s+(?i)order\\s+(?i)by\\s+(\\w+)(\\s+((?i)asc|(?i)desc))?)?\\s*;?\\s*$";
		Pattern pat = Pattern.compile(selectPattern);
		Matcher ma = pat.matcher(query);
		if (!ma.find()) {
			error();
			return;
		} else {
			colomsName = new ArrayList<String>(Arrays.asList(ma.group(2).replaceAll("\\s+", "").split(",")));
			tableName = ma.group(10);
			String[] wherecondition = Where(ma.group(13));
			if (colomsName.get(0).equals("*"))
				colomsName = new ArrayList<>();
			if (ma.group(20) != null && ma.group(22) != null) {
				order = ma.group(22).trim();
				Dpms.selectFromTable(colomsName, wherecondition, tableName, ma.group(21), order);
			} else {
				Dpms.selectFromTable(colomsName, wherecondition, tableName, null, null);
			}
		}
	}

	/**
	 * check update syntax and extract data to update
	 * 
	 * @param query
	 */

	public void Update(String query) {

		String updatepattern = "^\\s*((?i)update)\\s+(\\w+)\\s+((?i)set)\\s+"
				+ "((\\s*(\\w+)\\s*=\\s*(('[^']*')|(\\d+))\\s*,)*(\\s*(\\w+)\\s*=\\s*(('[^']*')|(\\d+))))"
				+ "(\\s*((?i)where)\\s+((\\w+)(\\s*)(>|<|=|>=|<=|<>)\\s*(('[^']*')|(\\d+))))?\\s*;?\\s*$";
		Pattern pat = Pattern.compile(updatepattern);
		Matcher ma = pat.matcher(query);
		if (!ma.find()) {
			error();
			return;
		}
		String tableName = ma.group(2);
		ArrayList<ArrayList<String>> clmAndVlu = colomValuesSpliter(ma.group(4));
		String[] wherecondition = Where(ma.group(17));
		ArrayList<String> columns = new ArrayList<>();

		ArrayList<String> value = new ArrayList<>();
		for (ArrayList<String> st : clmAndVlu) {
			columns.add(st.get(0));
			value.add(st.get(1));
		}
		Dpms.updateTable(columns, value, wherecondition, tableName);
	}

	/**
	 * extract the condition from its state
	 * 
	 * @param condition
	 * @return String[3] condition
	 */
	public String[] Where(String condition) {
		if (condition == null) {
			return new String[0];
		}

		String WhereCondtionPattern = "(\\w+)\\s*(>|<|=|>=|<=|<>)\\s*(('[^']*')|(\\d+))";
		Pattern pat = Pattern.compile(WhereCondtionPattern);
		Matcher ma = pat.matcher(condition);
		String[] cndition = new String[3];
		if (ma.find()) {
			cndition[0] = ma.group(1);
			cndition[1] = ma.group(2);
			cndition[2] = ma.group(3);
			if (cndition[2].endsWith("'")) {
				cndition[2] = cndition[2].substring(1, cndition[2].length() - 1);
			}
		}
		return cndition;
	}

	/**
	 * split coloms and values
	 * 
	 * @param colomStateMent
	 *            of the update
	 * @return colome and values in array list
	 */
	public ArrayList<ArrayList<String>> colomValuesSpliter(String colomStateMent) {
		ArrayList<ArrayList<String>> clmAndVlu = new ArrayList<>();
		ArrayList<String> temp = new ArrayList<>();
		// if (colomStateMent.endsWith("'"))
		colomStateMent = colomStateMent.replaceAll("'\\s*$", "");
		colomStateMent = colomStateMent.replaceAll("\\s*=\\s*'?", "=");
		colomStateMent = colomStateMent.replaceAll("'\\s*", "'");
		colomStateMent = colomStateMent.replaceAll("(\\d+)\\s*,\\s*", "$1',");
		String oneClm = "\\s*(\\w+)\\s*=\\s*(.*)\\s*";
		Pattern pa = Pattern.compile(oneClm);
		ArrayList<String> coloms = new ArrayList<String>(Arrays.asList(colomStateMent.split("((('))\\s*,\\s*)")));
		for (String ss : coloms) {
			Matcher ma = pa.matcher(ss);
			if (ma.find()) {
				temp = new ArrayList<>();
				temp.add(ma.group(1));
				temp.add(ma.group(2));
				clmAndVlu.add(temp);
			}
		}
		return clmAndVlu;
	}
	
	public static Parser getInstance(){
		if(instanceParser == null){
			instanceParser = new Parser();
		}
		return instanceParser;
	}
}
