import java.sql.*;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class Main {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	       
	static final String DB_URL = "jdbc:mysql://localhost/Temperaturmessung" + 
	   "?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";
			
    //   Database credentials
	static final String USER = "root";
	static final String PASS = "Mfr7eAffe$";
	
	static double previousTemp;
	static private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd - hh:mm:ss");
	static private Date dateTem = new Date(0);
	static private String dateString = sdf.format(dateTem);	
	static public Calendar cal=Calendar.getInstance();

	
	//   Get temperature (for one day -> 6 per hour)
	public static ArrayList<Double> randomTemperature() throws SQLException {
		ArrayList<Double> temperatures = new ArrayList<Double>();
		double aktTemp;
		double dif = 0.0;
		int pluMin = 0;
		previousTemp = (int) (Math.random() * ((30 - 15) + 1)) + 15;
		
		for (int i = 0; i <= 144; i++) {
			dif = (double) (Math.random() * ((0.9 - 0.1) + 1)) + 0.1;
			pluMin = (int) (Math.random() * ((10 - 1) + 1)) + 1;
			
			if(pluMin < 5) {
				aktTemp = previousTemp + dif;
				previousTemp = aktTemp;
				temperatures.add(aktTemp);
			}
			if(pluMin >= 5) {
				aktTemp = previousTemp - dif;
				previousTemp = aktTemp;
				temperatures.add(aktTemp);
			}		
		}		
		return temperatures;			
	}
	
	//   Create table from .txt
	public static void createTableFromTxt() {
		Connection conn = null;
		Statement stmt = null;
		String sql = "";
		String sql_use = "USE Temperaturmessung;";
		ArrayList<String> text = null;
		Reader r = new Reader();
		try {
			//   Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			
			//   Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			//   Execute a query
            stmt = conn.createStatement();
            stmt.executeUpdate(sql_use);
            text = r.readIn("CREATE_TEMPDB.txt");
            for(String st : text) {
            	System.out.println(st);
            }
            
            for (int i = 0; i < text.size(); i++) {
    			if(!(text.get(i).contains(");"))) {
    				sql += text.get(i);
    				}
    			else {
    				sql += text.get(i);
    				try {
    					stmt.executeUpdate(sql);
    				}
    				catch(SQLException se){
    					se.printStackTrace();
    				}
    				sql = "";
    			}
    		}
		}
		catch(SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch(IOException ie) {
			ie.printStackTrace();
		}
		catch(Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}		
	    try {
			if(stmt != null)
				stmt.close();
		}
		catch(SQLException se2) {
			// nothing we can do
		}
	}
		
	//   Insert temperatures (ArrayList)
	public static void insertTemperature(ArrayList<Double> temperatures) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Calendar cal3 = Calendar.getInstance();
		dateString = sdf.format(cal3.getTime());
		String sql = "INSERT INTO temperature VALUES(?,?)";
		try {
			//   Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			
			//   Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			//   Execute a query
			for(Double temp : temperatures) {
				dateString = cal3.toString();
				dateString = sdf.format(dateTem);
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, dateString);
				stmt.setDouble(2, temp);
				stmt.executeUpdate();
				cal3.add(Calendar.MINUTE, 6);
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
		finally {
			// finally block used to close resources
			try {
				if(stmt != null)
					stmt.close();
			}
			catch(SQLException se2) {
				// nothing we can do
			} // end finally try
		} // end try
	}	
	//   Insert temperature (double, string)
	public static void insertTemperature(Double temperature, Calendar cal2) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "INSERT INTO temperature VALUES(?,?)";
		try {
			//   Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			
			//   Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			//   Execute a query			
			dateString = sdf.format(cal2);
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, dateString);
			stmt.setDouble(2, temperature);
			stmt.executeUpdate();
		}			
		catch(SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch(Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		finally {
			// finally block used to close resources
			try {
				if(stmt != null)
					stmt.close();
			}
			catch(SQLException se2) {
				// nothing we can do
			} // end finally try
		} // end try
	}	
    //   Insert temperature (double)
	public static void insertTemperature(Double temperature) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		String sql = "INSERT INTO temperature VALUES(?,?)";
		try {
			//   Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			
			//   Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			//   Execute a query	
			Calendar cal2 = Calendar.getInstance();
			dateString = sdf.format(cal2.getTime());
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, dateString);
			stmt.setDouble(2, temperature);
			stmt.executeUpdate();
		}			
		catch(SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch(Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		finally {
			// finally block used to close resources
			try {
				if(stmt != null)
					stmt.close();
			}
			catch(SQLException se2) {
				// nothing we can do
			} // end finally try
		} // end try
	}

    //   Input menu
	public static char inputMenu() throws IOException{		
		System.out.println("a ... aktuelle Temperatur eingeben");
		System.out.println("b ... alte Temperatur eingeben");
		System.out.println("c ... Zufallstemperaturen eingeben (automatisch alle 10 Minuten)");
		System.out.println("d ... Zufallstemperaturen als Diagramm ausgeben");
		System.out.println("Ihre Wahl: ");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		char c = scan.next(".").charAt(0);
		return c;
	}
	
	//   Input temperature
	public static Calendar inputDate() throws NumberFormatException, IOException {
		Calendar datetime = Calendar.getInstance();
		Scanner scan = new Scanner(System.in);
		System.out.println("Bitte geben Sie Datum und Uhrzeit an");
		System.out.println("Tag: ");
		int day = scan.nextInt();
		System.out.println("Monat: ");
		int month = scan.nextInt();
		System.out.println("Jahr: ");
		int year = scan.nextInt();
		System.out.println("Stunden: ");
		int hour = scan.nextInt();
		System.out.println("Minuten: ");
		int minute = scan.nextInt();
		scan.close();
		
		datetime.set(year, month-1, day, hour, minute, 0);
		return datetime;
	}
	
	//   Plot random temperatures
	public static void plotTemperatures(ArrayList<Double> tempToPlot) throws SQLException {
		XYSeries series1 = new XYSeries("Temperaturverlauf");

		for (int i = 0; i < tempToPlot.size(); i++) {
			series1.add(i, tempToPlot.get(i));
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);

		XYSplineRenderer dot = new XYSplineRenderer();

		NumberAxis xax = new NumberAxis("Zeit");
		NumberAxis yax = new NumberAxis("Temperatur");
		
		yax.setRange(-35.0, 35.0);
		xax.setRange(0.0, 50.0);

		XYPlot plot = new XYPlot(dataset, xax, yax, dot);

		JFreeChart chart2 = new JFreeChart(plot);

		ApplicationFrame punkteframe = new ApplicationFrame("Temperaturmesskurve");
		
		ChartPanel chartPanel2 = new ChartPanel(chart2);
		punkteframe.setContentPane(chartPanel2);
		punkteframe.pack();
		punkteframe.setVisible(true);
	}
	
			
	// Main
	public static void main(String[] args) {

		// Create table
		createTableFromTxt();
		
		// Menu
		char choice;
		Scanner scan;
		double temp;
		try {
			do {
			    choice = inputMenu();			
				switch(choice) {
				   case 'a':
					   System.out.println("Aktuelle Temperatur: ");
					   scan = new Scanner(System.in);
					   temp = Double.parseDouble(scan.nextLine().replaceAll(",", "."));
					   insertTemperature(temp);
					   break;
				   case 'b':
					   System.out.println("Alte Temperatur: ");
				       scan = new Scanner(System.in);
				       //   Fehler, wenn . statt ,
				       temp = Double.parseDouble(scan.nextLine().replaceAll(",", "."));
				       Calendar date = inputDate();
				       insertTemperature(temp, date);
					   break;
				   case 'c':
					   cal.setTime(dateTem);
				       // Get 144 random temperatures (one day)
				       ArrayList<Double> tempToInsert = randomTemperature();
				   	   // Insert temperatures
				   	   insertTemperature(tempToInsert);
					   break;
				   case 'd':
					   ArrayList<Double> tempToInsertPlot = randomTemperature();
					   insertTemperature(tempToInsertPlot);
					   plotTemperatures(tempToInsertPlot);
					   break;
				}				
			}while(true);		
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
