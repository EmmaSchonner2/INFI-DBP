package models;

import java.sql.Date;

public class Person {

	int _emp_no;
	String _first_name;
	String _last_name;
    char _gender;
    String _title;
    int _dept_no;
    int _salary;
    Date _birth_date;
    Date _from_date;
    Date _to_date;
 
    
	public Person() {	}
	public Person(int emp_no, String first_name, String last_name, char gender, String title,
			int dept_no, int salary, Date birth_date, Date from_date, Date to_date) {
		this._emp_no = emp_no;
		this._first_name = first_name;
		this._last_name = last_name;
		this._gender = gender;
		this._title = title;
		this._dept_no = dept_no;
		this._salary = salary;
		this._birth_date = birth_date;
		this._from_date = from_date;
		this._to_date = to_date;
	}
	
}
