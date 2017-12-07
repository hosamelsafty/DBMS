package dataBase.control;

import static org.junit.Assert.*;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import java.util.ArrayList;

import org.junit.Test;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DataBaseControlTest {

	DataBaseControl dataBaseControlObj = DataBaseControlImpl.getInstance();
	ArrayList<String> colNames = new ArrayList<String>();
	ArrayList<String> types = new ArrayList<String>();
	ArrayList<String> values = new ArrayList<String>();
	ArrayList<ArrayList<String>> storedData = new ArrayList<ArrayList<String>>();

	@Test
	public void test1CreateAndInsert() {
		// Create Data Base Test
		dataBaseControlObj.createDataBase("db2");

		// Create Table Test
		colNames.add("ID");
		colNames.add("Name");
		types.add("int");
		types.add("varchar");
		dataBaseControlObj.createTable("clients", colNames, types);
		assertEquals("clients", dataBaseControlObj.getTableName());
		assertArrayEquals(colNames.toArray(), dataBaseControlObj.getCoulmnNames().toArray());

		// Insert values in Table Test
		ArrayList<String> values = new ArrayList<String>();
		values.add("12");
		values.add("Mohamed Ahmed");
		dataBaseControlObj.insertIntoTable(colNames, values, "clients");
		storedData.add(colNames);
		storedData.add((ArrayList<String>) values.clone());
		assertArrayEquals(storedData.toArray(), dataBaseControlObj.getWantedData().toArray());

		// Insert other values in Table
		values.set(0, "10");
		values.set(1, "Ahmed");
		dataBaseControlObj.insertIntoTable(colNames, values, "clients");
		storedData.add((ArrayList<String>) values.clone());
		assertArrayEquals(storedData.toArray(), dataBaseControlObj.getWantedData().toArray());
	}

	@Test
	public void test2UpdateAndSelect() {
		dataBaseControlObj.changeDataBase("db2");

		// Select Coulmns from Table in Data Base with Conditions
		ArrayList<String> selectedCols = new ArrayList<String>();
		selectedCols.add(0, "ID");
		selectedCols.add(1, "Name");
		String[] conditions1 = { "ID", "<=", "10" };
		dataBaseControlObj.selectFromTable(selectedCols, conditions1, "clients", null, null);
		storedData.clear();
		values.clear();
		storedData.add(selectedCols);
		values.add(0, "10");
		values.add(1, "Ahmed");
		storedData.add(values);
		assertArrayEquals(storedData.toArray(), dataBaseControlObj.getWantedData().toArray());

		// Update Coulmn with values with Conditions
		selectedCols.clear();
		selectedCols.add("Name");
		values.clear();
		values.add("Ali");
		String[] conditions2 = { "ID", ">", "3" };
		dataBaseControlObj.updateTable(selectedCols, values, conditions2, "clients");
		storedData.clear();
		values.clear();
		selectedCols.set(0, "ID");
		selectedCols.add("Name");
		storedData.add(selectedCols);
		values.add(0, "12");
		values.add(1, "Ali");
		storedData.add((ArrayList<String>) values.clone());
		values.set(1, "Ali");
		values.set(0, "10");
		storedData.add(values);
		assertArrayEquals(storedData.toArray(), dataBaseControlObj.getWantedData().toArray());
	}

	@Test(expected = RuntimeException.class)
	public void test3DeleteFromTableAndDrop() {
		dataBaseControlObj.changeDataBase("db2");

		// Test Delete some rows with specific values from Table
		String[] conditions = { "ID", "=", "12" };
		dataBaseControlObj.deleteFromTable(conditions, "clients");
		storedData.clear();
		colNames.clear();
		colNames.add("ID");
		colNames.add("Name");
		storedData.add(colNames);
		values.clear();
		values.add("10");
		values.add("Ali");
		storedData.add(values);
		assertArrayEquals(storedData.toArray(), dataBaseControlObj.getWantedData().toArray());

		// Drop Table Test
		dataBaseControlObj.dropTable("clients");
		dataBaseControlObj.deleteFromTable(conditions, "clients");
	}

}
