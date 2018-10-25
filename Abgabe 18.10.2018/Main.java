import java.sql.*;

import java.text.SimpleDateFormat;

import java.io.IOException;

import java.util.ArrayList;

import java.util.Calendar;

import java.util.Scanner;



public class Main {



	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	       

	static final String DB_URL = "jdbc:mysql://localhost/Temperaturmessung" + 

	   "?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Europe/Berlin";

			

    //   Database credentials

	static final String USER = "root";

	static final String PASS = "Mfr7eAffe$";

	

	static double previousTemp = (int) (Math.random() * ((30 - 15) + 1)) + 15;

	static private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/mm/dd - hh:mm:ss");

	static private Date dateTem = new Date(0);

	static private String dateString = sdf.format(dateTem);	

	static public Calendar cal=Calendar.getInstance();

	

	//   Get temperature (for one day -> 6 per hour)

	@SuppressWarnings("null")

	public static ArrayList<Double> randomTemperature() {

		ArrayList<Double> temperatures = null;

		double dif = 0.0;

		int pluMin = 0;

		

		for (int i = 0; i <= 144; i++) {

			dif = (double) (Math.random() * ((0.9 - 0.1) + 1)) + 0.1;

			pluMin = (int) (Math.random() * ((10 - 1) + 1)) + 1;

			

			if(pluMin < 5) {

				previousTemp = previousTemp + dif;

				temperatures.add(previousTemp);

			}

			if(pluMin >= 5) {

				previousTemp = previousTemp - dif;

				temperatures.add(previousTemp);

			}		

		}

		return temperatures;			

	}

	

	//   Create table from .txt

	public static void createTableFromTxt() {

		Connection conn = null;

		Statement stmt = null;

		String sql = "";

		ArrayList<String> text = null;

		Reader r = new Reader();

		try {

			//   Register JDBC driver

			Class.forName("com.mysql.jdbc.Driver");

			

			//   Open a connection

			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			

			//   Execute a query

            stmt = conn.createStatement();

            text = r.readIn("CREATE_TEMPDB.txt");

            for(String st : text) {

            	System.out.println(st);

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

		

	    try {

			if(stmt != null)

				stmt.close();

		}

		catch(SQLException se2) {

			// nothing we can do

		}

	}

		

	//   Insert temperatures (ArrayList)

	public static void insertTemperature(ArrayList<Double> temperatures) {

		Connection conn = null;

		PreparedStatement stmt = null;

		String sql = "INSERT INTO temperature VALUES(?,?)";

		try {

			//   Register JDBC driver

			Class.forName("com.mysql.jdbc.Driver");

			

			//   Open a connection

			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			

			//   Execute a query

			for(Double temp : temperatures) {			

				dateString = cal.getTime().toString();

				dateString = sdf.format(dateTem);

				stmt = conn.prepareStatement(sql);

				stmt.setString(1, dateString);

				stmt.setDouble(2, temp);

				stmt.executeUpdate();

				cal.add(Calendar.MINUTE, 6);

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

	public static void insertTemperature(Double temperature, Calendar cal) {

		Connection conn = null;

		PreparedStatement stmt = null;

		String sql = "INSERT INTO temperature VALUES(?,?)";

		try {

			//   Register JDBC driver

			Class.forName("com.mysql.jdbc.Driver");

			

			//   Open a connection

			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			

			//   Execute a query			

			dateString = sdf.format(cal);

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

    //  Insert temperature (double)

	public static void insertTemperature(Double temperature) {

		Connection conn = null;

		PreparedStatement stmt = null;

		String sql = "INSERT INTO temperature VALUES(?,?)";

		try {

			//   Register JDBC driver

			Class.forName("com.mysql.jdbc.Driver");

			

			//   Open a connection

			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			

			//   Execute a query	

			Calendar cal = Calendar.getInstance();

			dateString = sdf.format(cal.getTime());

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



    //  Input menu

	public static char inputMenu() throws IOException{		

		System.out.println("a ... aktuelle Temperatur eingeben");

		System.out.println("b ... alte Temperatur eingeben");

		System.out.println("c ... Zufallstemperaturen eingeben (automatisch alle 10 Minuten)");

		System.out.println("Ihre Wahl: ");

		@SuppressWarnings("resource")

		Scanner scan = new Scanner(System.in);

		char c = scan.next(".").charAt(0);

		return c;

	}

	

	// Input temperature

	public static Calendar inputTemperature() throws NumberFormatException, IOException {

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

	

			

	// Main

	public static void main(String[] args) {



		// Create table

		createTableFromTxt();

		

		// Menu

		char choice;

		Scanner scan;

		double temp;

		try {

			choice = inputMenu();

			do {

				switch(choice) {

				   case 'a':

					   System.out.println("Aktuelle Temperatur: ");

					   scan = new Scanner(System.in);

					   temp = scan.nextDouble();

					   insertTemperature(temp);

					   break;

				   case 'b':

					   System.out.println("Alte Temperatur: ");

				       scan = new Scanner(System.in);

				       temp = scan.nextDouble();

				       Calendar date = inputTemperature();

				       insertTemperature(temp, date);

					   break;

				   case 'c':

					   cal.setTime(dateTem);

				       // Get 144 random temperatures (one day)

				       ArrayList<Double> tempToInsert = randomTemperature();

				   	   // Insert temperatures

				   	   insertTemperature(tempToInsert);

					   break;

				}				

			}while(true);		

		}

		catch(Exception e){

			e.printStackTrace();

		}

	}



}
