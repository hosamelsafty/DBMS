package model;

import java.util.ArrayList;

import dbms.parser.Parser;

public class Printer implements PrinterIF {
	
	private static Printer instancePrinter;
	
	private Printer() {
		
	}
	
	@Override
	public void printTable(ArrayList<String> columnNames, ArrayList<ArrayList<String>> data, String tableName) {
		if (tableName == null)
			return;
		System.out.println(tableName + ":");
		ArrayList<Integer> width = findMaxWidth(data);
		for (int i = 0; i < data.size(); i++) {
			printLine(width);
			System.out.print("| ");
			printRow(width, data.get(i), columnNames.size());
		}
		printLine(width);
		System.out.println();
	}

	private ArrayList<Integer> findMaxWidth(ArrayList<ArrayList<String>> data) {
		ArrayList<Integer> width = new ArrayList<Integer>();
		int max = 0;
		for (int i = 0; i < data.get(0).size(); i++) {
			for (int j = 0; j < data.size(); j++) {
				if (i < data.get(j).size() && data.get(j).get(i) != null && max < data.get(j).get(i).length()) {
					max = data.get(j).get(i).length();
				}
			}
			width.add(max + 2);
			max = 0;
		}
		return width;
	}

	private void printLine(ArrayList<Integer> width) {
		System.out.print("+");
		for (int i = 0; i < width.size(); i++) {
			for (int j = 0; j < width.get(i); j++) {
				System.out.print("-");
			}
			System.out.print("+");
		}
		System.out.println();
	}

	private void printRow(ArrayList<Integer> width, ArrayList<String> row, int columnNumber) {
		for (int i = 0; i < columnNumber; i++) {
			if (i < row.size()) {
				System.out.print(row.get(i));
				for (int j = 0; j < width.get(i) - row.get(i).length() - 1; j++) {
					System.out.print(" ");
				}
				System.out.print("| ");
			} else {
				for (int j = 0; j < width.get(i) - 1; j++) {
					System.out.print(" ");
				}
				System.out.print("| ");
			}
		}
		System.out.println();
	}
	
	public static Printer getInstance(){
		if(instancePrinter == null){
			instancePrinter = new Printer();
		}
		return instancePrinter;
	}

}
