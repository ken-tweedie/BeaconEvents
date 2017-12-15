package com.BLEEventLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MySQLConnUtils {
	public static Connection getMySQLConnection()
	        throws ClassNotFoundException, SQLException {
	  
	    // Note: Change the connection parameters accordingly.
	    String hostName = "";
	    String dbName = "";
	    String userName = "";
	    String password = "";
	    
	    Context env = null;
		try {
			env = (Context)new InitialContext().lookup("java:comp/env");
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	   // Get a env values
		try {
			hostName = (String)env.lookup("hostName");
			dbName = (String)env.lookup("dbName");
			userName = (String)env.lookup("userName");
			password = (String)env.lookup("password");
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	    return getMySQLConnection(hostName, dbName, userName, password);
	}
	 
	public static Connection getMySQLConnection(String hostName, String dbName,
	        String userName, String password) throws SQLException,
	        ClassNotFoundException {
	    
	    // Declare the class Driver for MySQL DB
	    // This is necessary with Java 5 (or older)
	    // Java6 (or newer) automatically find the appropriate driver.
	    // If you use Java> 5, then this line is not needed.
	    Class.forName("com.mysql.jdbc.Driver");
	 
	    // URL Connection for MySQL
	    // Example: jdbc:mysql://localhost:3306/simplehr
	    String connectionURL = "jdbc:mysql://" + hostName + ":3306/" + dbName + "?autoReconnect=true&useSSL=false";
	 
	    Connection conn = DriverManager.getConnection(connectionURL, userName,
	            password);
	    return conn;
	}
}
