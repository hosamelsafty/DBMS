package dbms.parser;

import java.sql.SQLException;

public interface IParser {
	/**
	 * take a Query and check if it valid then start excute.
	 * 
	 * @param query
	 */
	void InsertQuery(String query) throws SQLException;
	
}