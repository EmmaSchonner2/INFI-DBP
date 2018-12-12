import java.awt.Dimension;
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
import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.jdbc.JDBCCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;

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
	//   - Visualisierung
	@SuppressWarnings("deprecation")
	public void plotFlightPerMonthInAYear(Date date) throws SQLException{	
		LocalDateTime now = null;
		if (date == null) {
		    now = LocalDate.now().atTime(0,0,0);
		}
		else {
		    now = LocalDateTime.of(date.getYear(), date.getMonth(), 1, 0, 0);
		}
		
		LocalDateTime nowWithFirstOfMonth = now.withDayOfMonth(1);		 		 
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
			for(int i=0; i<12; i++) {				
			    LocalDateTime monthBefore = nowWithFirstOfMonth.minusMonths(i);			    
			    stm = con.prepareStatement(sql);
				stm.setInt(1, monthBefore.getMonthValue());
				stm.setInt(2, monthBefore.getYear());				
				rs = stm.executeQuery();
				if (!rs.next()) { 
					dataset.addValue(0, "" + 0, monthBefore.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN));
				} 
				else {
					dataset.addValue(rs.getInt(1), "" + rs.getInt(1), monthBefore.getMonth().getDisplayName(TextStyle.FULL, Locale.GERMAN));
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
	
