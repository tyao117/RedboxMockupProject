import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.HashMap;


public class timeCompare {
	private HashMap<String, String> movieIdMapping;
	private HashMap<String, String> dummyData;
	private Statement statement;
	
	public timeCompare(HashMap<String, String> mapping, Statement statement2) {
		movieIdMapping = mapping;
		this.statement = statement2;
		dummyData = new HashMap<String, String>();
	}
	
	public void runTimeCompare() {
		createTable();
		method1();
		dropTable();

		createTable();
		method2();
		dropTable();

		createTable();
		method3();
		dropTable();
	}
	
	public void createTable() {
		try {
			String query = "create table movie_id_maps ("
					 + "xml_id varchar(10) not null,"
					 + "sql_id varchar(10) not null"
			 + ");";
			
			statement.execute(query);
			
			query = "select id from movies limit 100";
			ResultSet rs = statement.executeQuery(query);
			ArrayList<String> queries = new ArrayList<String>();
			while(rs.next()) {
				String id = rs.getString("id");
				query = "insert into movie_id_maps values ('" + id + "','" + id +"');";
				queries.add(query);
			}
			for(int i = 0; i < queries.size(); ++i) {
				statement.execute(queries.get(i));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void method1() {
		long startTime = System.nanoTime();
		String query;
		for(String key : movieIdMapping.keySet()) {
			try {
				// check if pair already exists in database
				query = "select xml_id from movie_id_maps where xml_id='" + key + "';";
				ResultSet rs = statement.executeQuery(query);
				
				// if query returns empty, insert new data
				if(!rs.next()) {
					query = "insert into movie_id_maps values ('" + key + "','" + movieIdMapping.get(key) + "');";
					statement.execute(query);
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		long endTime = System.nanoTime();
		long executeTime = endTime - startTime;
		System.out.println("Method 1 took " + executeTime/1000000 + " miliseconds to complete");
	}
	
	public void method2() {
		long startTime = System.nanoTime();
		try {
			// make in memory copy of table
			String query = "select xml_id from movie_id_maps;";
			ResultSet rs = statement.executeQuery(query);
			
			while(rs.next()) {
				String id = rs.getString("xml_id");
				dummyData.put(id, id);
			}
			
			for(String key : movieIdMapping.keySet()) {
					
					// if key is not in table, insert into database
					if(!dummyData.containsKey(key)) {
						String value =  movieIdMapping.get(key);
						query = "insert into movie_id_maps values ('" + key + "','" + value + "');";
						statement.execute(query);
						//update table with recent insert
						dummyData.put(key, value);
					}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dummyData.clear();
		long endTime = System.nanoTime();
		long executeTime = endTime - startTime;
		System.out.println("Method 2 took " + executeTime/1000000 + " miliseconds to complete");
	}
	
	public void method3() {
		long startTime = System.nanoTime();
		try {
			// make in memory copy of table
			String query = "select xml_id from movie_id_maps;";
			ResultSet rs = statement.executeQuery(query);
			
			while(rs.next()) {
				String id = rs.getString("xml_id");
				dummyData.put(id, id);
			}
			Writer my_writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.txt"), "ISO-8859-1"));
			
			for(String key : movieIdMapping.keySet()) {
					
					// if key is not in table, write to file
					if(!dummyData.containsKey(key)) {
						String value =  movieIdMapping.get(key);

		    			String mapping = key + "|" + value + "\n";
		    			my_writer.write(mapping);
						//update table with recent insert
						dummyData.put(key, value);
					}
			}
			
			my_writer.close();
			
			query = "load data local infile 'test.txt' into table movie_id_maps "
				  + "columns terminated by '|' "
				  + "lines terminated by '\\n';";
			statement.execute(query);
			
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		dummyData.clear();
		long endTime = System.nanoTime();
		long executeTime = endTime - startTime;
		System.out.println("Method 3 took " + executeTime/1000000 + " miliseconds to complete");
	}
	
	public void dropTable() {
		try {
			String query = "drop table movie_id_maps;";
			statement.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
