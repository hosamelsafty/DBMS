package dbms.parser;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

public class ParserTest {

	Parser parser = Parser.getInstance();

	@Test(expected = SQLException.class)
	public void test() throws SQLException {
		// worng input
		parser.InsertQuery("lkjsdlf");
	}

	@Test(expected = SQLException.class)
	public void test2() throws SQLException {
		// worng input create
		parser.InsertQuery("crate Database test1;");
	}

	@Test(expected = RuntimeException.class)
	public void test3() throws RuntimeException {
		// existing dataBase
		parser.CreateDataBase("create Database test1;");
		parser.CreateDataBase("create Database test1;");
	}

	@Test(expected = RuntimeException.class)
	public void test4() throws RuntimeException {
		// existing table
		try {
			parser.DropDataBase("drop database test1 ;");
		} catch (Exception e) {

		}
		parser.CreateDataBase("create Database test1;");
		parser.CreateTable("create table testTable (id int ,name varchar, state varchar) ;");
		parser.CreateTable("create table testTable (id int ,name varchar, state varchar) ;");
	}

	@Test(expected = RuntimeException.class)
	public void test5() throws RuntimeException {
		// existing wrong column names
		try {
			parser.DropDataBase("drop database test1 ;");
		} catch (Exception e) {

		}
		parser.CreateDataBase("create Database test1;");
		parser.CreateTable("create table testTable (id int ,name varchar, state varchar) ;");
		parser.Insert("insert into testTable values (1, 'hendy')");
	}

	@Test(expected = RuntimeException.class)
	public void test6() throws RuntimeException {
		// existing wrong column types
		try {
			parser.DropDataBase("drop database test1 ;");
		} catch (Exception e) {

		}
		parser.CreateDataBase("create Database test1;");
		parser.CreateTable("create table testTable (id int ,name varchar, state varchar) ;");
		parser.Insert("insert into testTable values ('1a', 'hendy','foo')");
	}

	@Test(expected = RuntimeException.class)
	public void test7() throws RuntimeException {
		// existing wrong column types
		try {
			parser.DropDataBase("drop database test1 ;");
		} catch (Exception e) {

		}
		parser.CreateDataBase("create Database test1;");
		parser.CreateTable("create table testTable (id int ,name varchar, state varchar) ;");
		parser.Insert("insert into testTable1 (id,nae,stat )" + "values (2, '7osam' , 'foo')");
	}

}
