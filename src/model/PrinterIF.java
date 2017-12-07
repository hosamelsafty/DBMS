package model;

import java.util.ArrayList;

public interface PrinterIF {

	public void printTable(ArrayList<String> columnNames, ArrayList<ArrayList<String>> data, String tableName);
}
