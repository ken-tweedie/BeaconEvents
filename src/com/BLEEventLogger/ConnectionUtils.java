package com.BLEEventLogger;

import java.sql.Connection;
import java.sql.SQLException;
 
public class ConnectionUtils {
 
   public static Connection getConnection()
             throws ClassNotFoundException, SQLException {
      
       // Here I using MySQL Database.
	   return MySQLConnUtils.getMySQLConnection();
       // return OracleConnUtils.getOracleConnection();
       // return MySQLConnUtils.getMySQLConnection();
       // return SQLServerConnUtils_JTDS.getSQLServerConnection_JTDS();
       // return SQLServerConnUtils_SQLJDBC.getSQLServerConnection_SQLJDBC();
	   // Other connection Utils available here:
	   // http://o7planning.org/en/10285/create-a-simple-java-web-application-using-servlet-jsp-and-jdbc
   }
    
   public static void closeQuietly(Connection conn) {
       try {
           conn.close();
       } catch (Exception e) {
       }
   }
 
   public static void rollbackQuietly(Connection conn) {
       try {
           conn.rollback();
       } catch (Exception e) {
       }
   }
}
