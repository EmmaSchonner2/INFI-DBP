import java.awt.Dimension;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class DBManager {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";   
	static final String DB_URL = "jdbc:mysql://localhost/aircraft" + 
	   "?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";			
    //   Database credentials
	static final String USER = "root";
	static final String PASS = "Mfr7eAffe$";
	
	private static Connection con;
	
	DBManager() throws SQLException{
		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(DB_URL, USER, PASS);	
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	//   - Flüge per Monat (für ein Jahr)
	public void getFlightsPerMonthInAYear() throws SQLException{

		String sql = "SELECT DISTINCT MONTH(FROM_UNIXTIME(seentime)), YEAR(FROM_UNIXTIME(seentime)), COUNT(*) FROM aircraft.dump1090data GROUP BY MONTH(FROM_UNIXTIME(seentime)), YEAR(FROM_UNIXTIME(seentime));";

		try {
		    //   Register JDBC driver
			Class.forName(JDBC_DRIVER);
					
			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);
			
			//   Execute a query	
			// TODO: Monate 4 und 11 gibt es nicht
			Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery(sql);
	        int columns = rs.getMetaData().getColumnCount();
	        System.out.println("Flüge per Monat: ");
	        //for (int i = 1; i <= columns; i++)
	        //   System.out.print(rs.getMetaData().getColumnLabel(i) + "\t\t");
	        //System.out.println();
	        System.out.println();
	        while (rs.next()) {
	            for (int i = 1; i <= columns; i++) {
	                System.out.print(rs.getString(i) + "\t\t");
	            }
	            System.out.println();
	        }
	        System.out.println();
	        if (stmt != null) {
	        	stmt.close();
	        	rs.close();
	        }
		}
		catch(SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch(Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}		
	}
	// TODO
	//   - Flüge für einen bestimmten Monat
	public void getFlightsPerMonth(int month) throws SQLException{

        String sql = "SELECT DISTINCT COUNT(*) FROM aircraft.dump1090data WHERE MONTH(FROM_UNIXTIME(seentime)) = ? GROUP BY MONTH(FROM_UNIXTIME(seentime)), YEAR(FROM_UNIXTIME(seentime));";
		try {
		    //   Register JDBC driver
			Class.forName(JDBC_DRIVER);
					
			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);
			
			//   Execute a query	
			PreparedStatement stm = con.prepareStatement(sql);
			stm.setInt(1, month);
			System.out.println(stm.toString());
			
	        ResultSet rs = stm.executeQuery();
	        rs.next();

	        System.out.println("Flüge in diesem Monat: ");
	        
	        while(rs.next()) {
	        	System.out.println(rs.getInt(1) + "\t\t");
	        }
	        if(stm != null) {
	        	stm.close();
	        	rs.close();
	        }				
		}
		catch(SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch(Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
	}
	// TODO
	public void plotFlightPerMonthInAYear() throws SQLException{
		
		String[] months = new String[] {
				"Jänner", "Februar", "März", "April", "Mai", "Juni", "Juli", 
				"August", "September", "Oktober", "November", "Dezember"
				};
		
        String sql = "SELECT DISTINCT COUNT(*) FROM aircraft.dump1090data WHERE MONTH(FROM_UNIXTIME(seentime)) = ? AND YEAR(FROM_UNIXTIME(seentime)) = ? GROUP BY MONTH(FROM_UNIXTIME(seentime)), YEAR(FROM_UNIXTIME(seentime));";
			
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		try {
		    //   Register JDBC driver
			Class.forName(JDBC_DRIVER);
							
			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement stm = null;
			ResultSet rs = null;
			
			//   Execute a query
			for (int i = 1; i <= 12; i++) {
				
				if ( (i == 4) || (i == 11) || (i == 12)) {
					dataset.addValue(0, "Row " + i, months[i-1]);
				}
				else {
					stm = con.prepareStatement(sql);
					stm.setInt(1, i);
					stm.setInt(2, 2018);
							
					System.out.println(stm.toString());
							
			        rs = stm.executeQuery();
					rs.next();
					
					dataset.addValue(rs.getInt(1), "Row " + i, months[i-1]);
				}				
			}
			
			if(stm != null) {
			    stm.close();
			    rs.close();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		};
			
	    JFreeChart chart = ChartFactory.createBarChart(
			    "Flüge der letzten 12 Monate",   // chart title
				"Monate",                        // domain axis label
				"Anzahl der Flüge",              // range axis label
				dataset,                         // data
				PlotOrientation.HORIZONTAL,      // orientation
				true,                            // include legend
				true,                            // tooltips
				false);                          // URLs
			
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(500, 300));
			
		ApplicationFrame frame = new ApplicationFrame("Flüge");
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);			
	}
}
	
