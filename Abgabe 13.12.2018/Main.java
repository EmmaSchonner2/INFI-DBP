import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class Main {
	
	// Input menu
	public static char inputMenu() throws IOException{		
		System.out.println("a ... Ausgabe Flüge pro Monat");
		System.out.println("b ... Ausgabe Flüge eines selbstgewählten Monats");
		System.out.println("c ... Visualisierung Flüge pro Monat letztes Jahr");
		System.out.println("d ... Visualisierung Flüge pro Monat letztes Jahr - von einem selbstgewählten Zeitpunkt aus zurück");	
		System.out.println("Ihre Wahl: ");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		char c = scan.next(".").charAt(0);
		return c;
	}

	// Input month
	public static int inputMonth() throws IOException{		
		System.out.println("Gewünschter Monat: ");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		int m = Integer.parseInt(scan.next());
		return m;
	}
	
	// Input startCal
	@SuppressWarnings("deprecation")
	public static Date inputStartDate() throws IOException{		
		// TODO
		System.out.println("Gewünschter Monat: ");
		@SuppressWarnings("resource")
		Scanner scanM = new Scanner(System.in);
		int m = Integer.parseInt(scanM.next());
		
		System.out.println("Gewünschtes Jahr: ");
		@SuppressWarnings("resource")
		Scanner scanY = new Scanner(System.in);
		int y = Integer.parseInt(scanY.next());
		
		return new Date(y, m, 1);
	}
	
		
	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		DBManager db = new DBManager();
		char choice;
		int month;
		// TODO
		Date date = new Date(0, 0, 1);
		
		try {
			do {
				choice = inputMenu();
				switch(choice) {
				case 'a':
					db.getFlightsPerMonthInAYear();
					break;
				case 'b':
					month = inputMonth();
					db.getFlightsPerMonth(month);
					break;
				case 'c':
					db.plotFlightPerMonthInAYear(null);
					break;
				case 'd':
					date = inputStartDate();
					db.plotFlightPerMonthInAYear(date);
					break;
				}
			}while(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
