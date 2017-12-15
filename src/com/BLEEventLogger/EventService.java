package com.BLEEventLogger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.BLEEventLogger.ConnectionUtils;
import com.BLEEventLogger.DBUtils;

@Path("/BeaconEventService") 

public class EventService {  
	
	   @POST
	   @Path("/events")
	   @Produces(MediaType.APPLICATION_XML)
	   //@Consumes(MediaType.TEXT_XML)
	   @Consumes(MediaType.APPLICATION_JSON)
	   public String LogEvent(String InValue) throws IOException{
	    	WriteToLogFile("/BeaconEventService/events web service invoked.", "FULL");
		   // Log the incoming XML to a text file
		   String EventDetail = "Web service call successful";
		   // Get the base naming context
		   Context env = null;
			try {
				env = (Context)new InitialContext().lookup("java:comp/env");
			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			WriteToLogFile("Getting Config Settings", "FULL");
		   // Get a single value
		   String LogToFileOnly = "Yes";
			try {
				LogToFileOnly = (String)env.lookup("LogToFileOnly");
		    	WriteToLogFile("LogToFileOnly set to " + LogToFileOnly, "FULL");
				
			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		   
		    if (new String(LogToFileOnly).equals("Yes"))
		    {
		    	WriteToLogFile("Writing out incoming JSON.", "FULL");
		    	WriteToLogFile(InValue, "BASIC");
		    }
		    else
		    {
		    	WriteToLogFile("Writing out incoming XML.", "FULL");
		    	WriteToLogFile(InValue, "FULL");
			    // Process the incoming JSON and write to the MySQL database

				// First reset the tags
				Connection conn = null;
				try {
				    // Create connection
					WriteToLogFile("Making database connection.", "FULL");
				    conn = ConnectionUtils.getConnection();
				    try {
				    	WriteToLogFile("Resetting tags.", "FULL");
				        DBUtils.resetTags(conn);
				    } catch (SQLException e) {
				        e.printStackTrace();
				    }
				} catch (Exception e) {
				    e.printStackTrace();
				    ConnectionUtils.rollbackQuietly(conn);
				} finally {
				    ConnectionUtils.closeQuietly(conn);
				}
				    
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) jsonParser.parse(InValue);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String Location = (String) jsonObject.get("LOCATION");
				String Major = (String) jsonObject.get("MAJOR");
				String BeaconID = (String) jsonObject.get("BEACONID");
				String Customer = (String) jsonObject.get("CUSTOMER");
				String Minor = (String) jsonObject.get("MINOR");
				String DetectionStatus = (String) jsonObject.get("DETECTIONSTATUS");
				String BeaconType = (String) jsonObject.get("BEACONTYPE");

				WriteToLogFile("The Location is: " + Location, "FULL");
				WriteToLogFile("The BeaconID is: " + BeaconID, "FULL");
				WriteToLogFile("The Major is: " + Major, "FULL");
				WriteToLogFile("The Minor is: " + Minor, "FULL");
				WriteToLogFile("The DetectionStatus is: " + DetectionStatus, "FULL");
				
				conn = null;
		        try {
		            // Create connection
		            conn = ConnectionUtils.getConnection();
		            try {
					    WriteToLogFile("Updating Beacon_ID " + BeaconID + " from ReadPoint " + Location + " - " + DetectionStatus + ".", "FULL");
					    if (new String(DetectionStatus).equals("BEACON-LOST"))
					    {
					    	DBUtils.updateTags(conn, BeaconID, Location, 0);
					    }
					    else if (new String(DetectionStatus).equals("BEACON-DETECTED"))
					    {
					    	DBUtils.updateTags(conn, BeaconID, Location, 1);
					    }
		 			} catch (SQLException e) {
		                e.printStackTrace();
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		            ConnectionUtils.rollbackQuietly(conn);
		        } finally {
		            ConnectionUtils.closeQuietly(conn);
		        }
			    
			}
		  WriteToLogFile("/EventService/events web service done.", "FULL");
	      return "<result>" + EventDetail + "</result>";
	   }
	   
	   private void WriteToLogFile(String LogData, String LogType)
	   {
		   // LogLevel can be FULL or BASIC
		   String LogLevel = "BASIC";
		   String LogFile = "C:\\logs\testlog.txt";
		   try
		   {
		   // Get the base naming context
		   Context env = null;
			try {
				env = (Context)new InitialContext().lookup("java:comp/env");
			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		   // Get a single value
			try {
				LogLevel = (String)env.lookup("LogLevel");
				LogFile = (String)env.lookup("LogFile");
				
			} catch (NamingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// Only log this if the Logging Level is set
			if (new String(LogLevel).equals(LogType) || new String(LogLevel).equals("FULL"))
		    {
			    FileWriter fw = new FileWriter(LogFile,true); //the true will append the new data
			    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			    String strTimestamp = dateFormat.format(new Date());
			    fw.write(strTimestamp + ":----------------------------------------------------------------------------------------------------------" + System.getProperty("line.separator")); 
			    fw.write(strTimestamp + ":- " + LogData + System.getProperty("line.separator")); //appends the string to the file
			    fw.close();
		    }
		   }
		   catch (IOException e)
		   {
			    System.out.println("Exception:- " + e.getMessage());
		   }
	   }
	   
}