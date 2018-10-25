import java.sql.*;

import java.text.SimpleDateFormat;

import java.io.IOException;

import java.util.ArrayList;

import java.util.Calendar;

import java.util.Scanner;

import java.util.Random;



public class Main {



	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	static final String DB_URL = "jdbc:mysql://localhost/Temperaturmessung";

			

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

				previousTemp = previousTemp + dif;

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

	

	

	//   Insert temperatures

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

				cal.add(Calendar.MINUTE, 6);

				dateString = cal.getTime().toString();

				stmt = conn.prepareStatement(sql);

				stmt.setDouble(1, temp);

				stmt.setString(2, dateString);

				stmt.executeUpdate();

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

			

	// Main

	public static void main(String[] args) {

		// TODO Auto-generated method stub



		cal.setTime(dateTem);

		// Create table

		createTableFromTxt();

        // Get 144 random temperatures (one day)

		ArrayList<Double> tempToInsert = randomTemperature();

		// Insert temperatures

		insertTemperature(tempToInsert);

		

		while(true) {}

	}



}
