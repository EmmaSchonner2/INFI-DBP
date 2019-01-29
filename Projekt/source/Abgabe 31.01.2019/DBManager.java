package models;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.TextStyle;
import java.util.Locale;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

public class DBManager {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";   
	static final String DB_URL = "jdbc:mysql://localhost/employees" + 
			"?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";			
	//   Database credentials
	static final String USER = "root";
	static final String PASS = "Mfr7eAffe$";

	private static Connection con;

	public DBManager() throws SQLException{
		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(DB_URL, USER, PASS);	
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	// MUSS:

	//   - zusätzliche Daten hinzufügen
	public void insertData() {

	}

	//   - Pay Gap visualisieren
	public void getAllJobs() {		
		String sql = "SELECT DISTINCT title FROM titles ORDER BY title;";

		try {
			//   Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);

			//   Execute a query	
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int columns = rs.getMetaData().getColumnCount();
			// Tabellenspaltennamen
			for (int i = 1; i <= columns; i++)
				System.out.print(rs.getMetaData().getColumnLabel(i) + "\t\t");
			System.out.println();
			System.out.println();
			// Tabelleninhalte
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
	public void visualizePayGap(String job) {

	}

	//   - Alter der Abteilungen
	@SuppressWarnings("null")
	public void visualizeAge() {
		String sql = "SELECT (AVG(TO_DAYS(NOW()) - TO_DAYS(birth_date)) / 365.25) as durchschnittsalter, dept_name FROM employees JOIN dept_emp USING (emp_no) JOIN departments USING(dept_no) WHERE to_date > now() GROUP BY dept_no ORDER BY dept_name;";

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		try {
			//   Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = con.createStatement();
			ResultSet rs = null;

			//   Execute a query
			rs = stmt.executeQuery(sql);
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columns; i++) {
					dataset.addValue(rs.getDouble(1), "" + rs.getDouble(1), rs.getString(2));
				}
				System.out.println();
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
		
		JFreeChart chart = ChartFactory.createBarChart(
			    "durchschnittliches Alter pro Abteilung",   // chart title
				"Abteilung",                                // domain axis label
				"Durchschnittsalter",                       // range axis label
				dataset,                                    // data
				PlotOrientation.HORIZONTAL,                 // orientation
				true,                                       // include legend
				true,                                       // tooltips
				false);                                     // URLs
			
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(500, 300));
			
		ApplicationFrame frame = new ApplicationFrame("Alter");
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
	}


	// KANN:

	//   - häufigste Nachnamen visualisieren
	public void getAllDepartments() {
		String sql = "SELECT * FROM departments ORDER BY dept_no";

		try {
			//   Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);

			//   Execute a query
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int columns = rs.getMetaData().getColumnCount();
			// Tabellenspaltennamen
			for (int i = 1; i <= columns; i++)
				System.out.print(rs.getMetaData().getColumnLabel(i) + "\t\t");
			System.out.println();
			System.out.println();
			// Tabelleninhalte
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
	public void visualizeLastName(String department) throws SQLException {
		String sql = null;
		if (department == "") {
			sql = "SELECT last_name, count(*) AS c FROM employees GROUP BY last_name ORDER BY c DESC LIMIT 5;";
		}
		else {
			sql = "SELECT last_name, count(*) AS c FROM employees JOIN dept_emp USING (emp_no) WHERE dept_no = ? AND to_date > now() GROUP BY last_name ORDER BY c DESC LIMIT 5;";
			PreparedStatement stm = con.prepareStatement(sql);
			stm.setString(1, department);
		}
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		try {
			//   Register JDBC driver
			Class.forName(JDBC_DRIVER);
			ResultSet rs = null;
			
			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);

			//   Execute a query
			if (department == "") {
				Statement stmt = con.createStatement();
				rs = stmt.executeQuery(sql);
			}
			else {
				PreparedStatement stm = con.prepareStatement(sql);
				stm.setString(1, department);
				rs = stm.executeQuery();
				
			}
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columns; i++) {
					dataset.addValue(rs.getInt(2), "" + rs.getInt(2), rs.getString(1));
				}
				System.out.println();
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
		
		JFreeChart chart = ChartFactory.createBarChart(
			    "häufigste Nachnamen",        // chart title
				"Nachname",                   // domain axis label
				"Anzahl",                     // range axis label
				dataset,                      // data
				PlotOrientation.HORIZONTAL,   // orientation
				true,                         // include legend
				true,                         // tooltips
				false);                       // URLs
			
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(500, 300));
			
		ApplicationFrame frame = new ApplicationFrame("Nachnamen");
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
	}

}
