package com.BLEEventLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
  
public class DBUtils {
 

  public static void resetTags(Connection conn) throws SQLException {
      String sql1 = "UPDATE tags SET PRESENT = 0";
      
      PreparedStatement pstm = conn.prepareStatement(sql1);
      pstm.executeUpdate();
      
      }
  
  public static void updateTags(Connection conn, String ObjectID, String LocationID, Integer Presence) throws SQLException {
      String sql1 = "Update demo3.tags SET PRESENT = " + Presence + ", LOCATION = '" + LocationID + "' WHERE TAG_ID = '" + ObjectID + "'";

      PreparedStatement pstm = conn.prepareStatement(sql1);
      pstm.executeUpdate();
      
      }
}
