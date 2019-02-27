package models;

import java.sql.Date;

public class Person {

	public int emp_no;
	public String first_name;
	public String last_name;
    public Gender gender;
    public String title;
    public String dept_no;
    public int salary;
    public java.sql.Date birth_date;
    public java.sql.Date from_date;
    public java.sql.Date to_date;
 
    
	public Person() { }
	public Person(int emp_no, String first_name, String last_name, Gender gender, String title,
			String dept_no, int salary, java.sql.Date birth_date, java.sql.Date from_date,
			java.sql.Date to_date) {
		this.emp_no = emp_no;
		this.first_name = first_name;
		this.last_name = last_name;
		this.gender = gender;
		this.title = title;
		this.dept_no = dept_no;
		this.salary = salary;
		this.birth_date = birth_date;
		this.from_date = from_date;
		this.to_date = to_date;
	}
	
}
