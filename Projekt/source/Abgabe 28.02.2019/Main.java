import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import models.*;

public class Main {

	// Input menu
	public static char inputMenu() throws IOException{
		System.out.println("a ... Pay Gap visualisieren");
		System.out.println("b ... Alter des Unternehmens visualisieren");
		System.out.println("c ... häufigste Nachnamen");
		System.out.println("d ... häufigste Nachnamen - für eine Abteilung");
		System.out.println("e ... Daten automatisch insertieren");
		System.out.print("Ihre Wahl: ");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		char c = scan.next(".").charAt(0);
		return c;
	}	
	// Input job
	public static String inputJob() throws IOException{
		System.out.print("   Ihre Wahl: ");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		String j = scan.next();
		return j;
	}
	// Input department
	public static String inputDepartment() throws IOException{
		System.out.print("   Ihre Wahl (dept_no): ");
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		String d = scan.next();
		return d;
	}


	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		DBManager db = new DBManager();
		char choice;
		String job = null;
		String department = null;
		Person randomPerson = new Person();

		try {
			do {
				choice = inputMenu();
				switch(choice){
				case 'a':
					db.getAllJobs();
					job = inputJob();
					db.visualizePayGap(job);
					break;
				case 'b':
					db.visualizeAge();
					break;
				case 'c':
					db.visualizeLastName("");
					break;
				case 'd':
					db.getAllDepartments();
					department = inputDepartment();
					db.visualizeLastName(department);				
					break;
				case 'e':	
					for (int i = 0; i < 11; i++) {
						randomPerson = db.getRandomPerson();
						db.insertRandomPerson(randomPerson);
						System.out.println("  " + randomPerson.first_name + " " + randomPerson.last_name + " wurde hinzugefügt");
					}	
					System.out.println("   ->> 10 neue Mitarbeiter wurden erfolgreich hinzugefügt! <<-\n\n");
					break;
				}				
			}while(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}				
	}

}
