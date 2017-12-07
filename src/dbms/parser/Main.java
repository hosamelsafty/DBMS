package dbms.parser;

import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		IParser parser = Parser.getInstance();
		Scanner scanner = new Scanner(System.in);
		while(true){
			try{
				String input = scanner.nextLine();
				parser.InsertQuery(input);
			
			} catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
	}
}
