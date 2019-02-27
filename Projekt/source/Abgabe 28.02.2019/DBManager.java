package models;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
		String sql = "SELECT AVG(salary) as durchschnittsgehalt, gender as geschlecht FROM employees JOIN titles USING(emp_no) JOIN salaries USING(emp_no) WHERE (salaries.from_date < NOW()) AND (salaries.to_date > NOW()) AND (titles.title = ?) GROUP BY gender;";
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		try {
			//   Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);
			PreparedStatement stm = con.prepareStatement(sql);
			stm.setString(1, job);

			//   Execute a query
			ResultSet rs = stm.executeQuery();
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
			    "druchschnittliches Gehalt der drei bestbezahlten " +
		        "Männer und Frauen einer Abteilung",                   // chart title
				"Geschlecht",                                          // domain axis label
				"Durchschnittsgehalt",                                 // range axis label
				dataset,                                               // data
				PlotOrientation.HORIZONTAL,                            // orientation
				true,                                                  // include legend
				true,                                                  // tooltips
				false);                                                // URLs
			
		ChartPanel chartPanel = new ChartPanel(chart, false);
		chartPanel.setPreferredSize(new Dimension(500, 300));
		//chartPanel.setBounds(50, 0, 500, 300);
		//chartPanel.setAnchor(new Point2D.Double(50, 0));
			
		ApplicationFrame frame = new ApplicationFrame("PayGap");
		frame.setContentPane(chartPanel);
		frame.pack();
		frame.setVisible(true);
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
		
		//NumberAxis xaxis = new NumberAxis("x");
		//xaxis.setLowerBound(60);
		
		ApplicationFrame frame = new ApplicationFrame("Durchschnittsalter");
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
	
	//   - zufällige Person zur Datenbank hinzufügen	
	public int getMinEmpNo() {

		String sql = "SELECT min(emp_no) FROM employees";
		int min = 0;
		try {
			//   Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//   Open a connection
			con = DriverManager.getConnection(DB_URL, USER, PASS);

			//   Execute a query
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			min = rs.getInt(1);
			if (stmt != null) {
				stmt.close();
				rs.close();
			}
			con.close();
		}	
		catch(SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch(Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}
		return min;
	}
	public String getName(Gender gender) throws IOException {
		String file = null;
		if (gender == Gender.M) {
			file = "MaleFirstNames.txt";
		}
		else if (gender == Gender.F) {
			file = "FemaleFirstNames.txt";
		}
		else if (gender == null) {
			file = "LastNames.txt";
		}
		else {
			return null;
		}
		// Daten aus File auslesen 		
		FileReader fr = new FileReader(file);
	    BufferedReader br = new BufferedReader(fr);
	    String zeile = "";
	    ArrayList<String> possibleNames = new ArrayList<String>();
	    do
	    {
	      zeile = br.readLine();
	      possibleNames.add(zeile);
	    }
	    while (zeile != null);
	    br.close();	    
	    // Zufallszahl erzeugen
	    Random randomGenerator = new Random();
	    int randomNumber = randomGenerator.nextInt(100);
	    // Name aus possibleNames auswählen
	    do {
	    	randomNumber = randomGenerator.nextInt(100);
	    }while (randomNumber >= possibleNames.size());
	    	    
	    return possibleNames.get(randomNumber);
	}
	public Date getRandomDate(char type) throws ParseException {
		Random randomGenerator = new Random();		
		java.util.Date date = new java.util.Date();
		java.sql.Date currentDate = new java.sql.Date(date.getTime()); 		
		@SuppressWarnings("deprecation")
		int limitYear = currentDate.getYear();
		int year = 0;
		if (type == 'b') {
			// nextInt((max - min) + 1) + min
			year = randomGenerator.nextInt(((limitYear - 25) - (limitYear - 55)) + 1) + (limitYear - 55);
		}
		else if (type == 't') {
			year = randomGenerator.nextInt(((limitYear +25) - (limitYear + 1)) + 1) + (limitYear + 1);
		}
		else {
			return null;
		}
		int day = randomGenerator.nextInt((28 - 1) + 1) + 1;
		int month = randomGenerator.nextInt((12 - 1) + 1) + 1;
	    
	    @SuppressWarnings("deprecation")
		java.util.Date date2 = new java.util.Date(year, month, day);
        java.sql.Date sqlDate = new java.sql.Date(date2.getTime());
		
		return sqlDate;
	}
	public String getTitle() {
		String sql = "SELECT DISTINCT title FROM titles ORDER BY title;";
		ArrayList<String> foundTitles = new ArrayList<String>();
		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(DB_URL, USER, PASS);	
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columns; i++) {
					foundTitles.add(rs.getString(i));
				}
			}
			if (stmt != null) {
				stmt.close();
				rs.close();
			}
			con.close();
		}
		catch(SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		}
		catch(Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		}	
		Random randomGenerator = new Random();
	    int randomNumber = randomGenerator.nextInt(15);
	    do {
	    	randomNumber = randomGenerator.nextInt(15);
	    }while (randomNumber >= foundTitles.size());
	    	    
	    return foundTitles.get(randomNumber);
	}
	public Person getRandomPerson() throws IOException, ParseException {				
		Random randomGenerator = new Random();
		
		int emp_no = getMinEmpNo() - 1;
		
		Gender gender = null;		
	    boolean randomBool = randomGenerator.nextBoolean();
	    if (randomBool == true) {
	    	gender = Gender.F;
	    }
	    else {
	    	gender = Gender.M;
	    }
		
		String first_name = getName(gender);
		String last_name = getName(null);
		
		String title = getTitle();
		
		int randomIntD = randomGenerator.nextInt((9 - 1) + 1) + 1;
	    String dept_no = "d00" + randomIntD; 
	    
	    int randomIntS = randomGenerator.nextInt();
	    do {
	    	randomIntS = randomGenerator.nextInt();
	    }while ( (randomIntS > 50000) && (randomIntS < 100000));
        int salary = randomIntS;
	    
        java.sql.Date birth_date = getRandomDate('b');	
        java.sql.Date to_date = getRandomDate('t');
             
        java.util.Date date = new java.util.Date();
        java.sql.Date from_date = new java.sql.Date(date.getTime());
        
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //Date langDate = (Date) sdf.parse("2014/04/13"); // ??????
        //java.sql.Date from_date = new java.sql.Date(langDate.getTime());
        
        //java.util.Date dateI = new java.util.Date();
		//java.sql.Date date = new java.sql.Date(dateI.getTime()); 		
        //SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        //String from_date = dateFormatter.format(date);              

		return new Person(emp_no, first_name, last_name, gender, title, dept_no,				
				salary, birth_date, from_date, to_date);
	}
	public void insertRandomPerson(Person personToInsert) throws SQLException {
		// employees
		String sql_emp = "INSERT INTO employees (emp_no, birth_date, first_name, last_name, gender, hire_date) VALUES (?, ?, ?, ?, ?, ?);";
		// titles
		String sql_tit = "INSERT INTO titles (emp_no, title, from_date, to_date) VALUES (?, ?, ?, ?);";
		// dept_emp
		String sql_dep = "INSERT INTO dept_emp (emp_no, dept_no, from_date, to_date) VALUES (?, ?, ?, ?);";
		// salaries
		String sql_sal = "INSERT INTO salaries (emp_no, salary, from_date, to_date) VALUES (?, ?, ?, ?);";

		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(DB_URL, USER, PASS);
			
			PreparedStatement stmt_emp = null;
			stmt_emp = con.prepareStatement(sql_emp);
            stmt_emp.setInt(1, personToInsert.emp_no);
			stmt_emp.setDate(2, personToInsert.birth_date);
			stmt_emp.setString(3, personToInsert.first_name);
			stmt_emp.setString(4, personToInsert.last_name);
			stmt_emp.setString(5, personToInsert.gender.name());
			stmt_emp.setDate(6, personToInsert.from_date);
			stmt_emp.executeUpdate();
			
			PreparedStatement stmt_tit = null;
			stmt_tit = con.prepareStatement(sql_tit);
			stmt_tit.setInt(1, personToInsert.emp_no);
			stmt_tit.setString(2, personToInsert.title);
			stmt_tit.setDate(3, personToInsert.from_date);
			stmt_tit.setDate(4, personToInsert.to_date);
			stmt_tit.executeUpdate();
			
			PreparedStatement stmt_dep = null;
			stmt_dep = con.prepareStatement(sql_dep);
			stmt_dep.setInt(1, personToInsert.emp_no);
			stmt_dep.setString(2, personToInsert.dept_no);
			stmt_dep.setDate(3, personToInsert.from_date);
			stmt_dep.setDate(4, personToInsert.to_date);
			stmt_dep.executeUpdate();
			
			PreparedStatement stmt_sal = null;
			stmt_sal = con.prepareStatement(sql_sal);
			stmt_sal.setInt(1, personToInsert.emp_no);
			stmt_sal.setInt(2, personToInsert.salary);
			stmt_sal.setDate(3, personToInsert.from_date);
			stmt_sal.setDate(4, personToInsert.to_date);
			stmt_sal.executeUpdate();
			
			if (stmt_emp != null) {
				stmt_emp.close();
			}
			if (stmt_tit != null) {
				stmt_tit.close();
			}
			if (stmt_dep != null) {
				stmt_dep.close();
			}
			if (stmt_sal != null) {
				stmt_sal.close();
			}
			con.close();
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

}
