package dao;

/*
 MySQL Layer

 +----------+-------------+------+-----+---------+----------------+
 | Field    | Type        | Null | Key | Default | Extra          |
 +----------+-------------+------+-----+---------+----------------+
 | userid   | int(11)     | NO   | PRI | NULL    | auto_increment | # Internal System Id
 | email    | varchar(50) | YES  |     | NULL    |                | # Account email
 | password | varchar(20) | YES  |     | NULL    |                | # Account password
 | fname    | varchar(20) | YES  |     | NULL    |                | # First name
 | lname    | varchar(20) | YES  |     | NULL    |                | # Last name
 +----------+-------------+------+-----+---------+----------------+
 
 */

public class AdminUser {
	private int id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	
	public AdminUser(){
		this.id        = (Integer)null;
		this.email     = null;
		this.password  = null;
		this.firstName = null;
		this.lastName  = null;
	}
	public AdminUser(int id, String email, String password, String fName, String lName){
		this.id        = id;
		this.email     = email;
		this.password  = password;
		this.firstName = fName;
		this.lastName  = lName;
	}
	/*
	 * Get and Set Methods
	 */
	
	// Id Access Methods
	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	
	// Email Access Methods
	public String getEmail(){
		return this.email;
	}
	public void setEmail(String email){
		this.email = email;
	}
	
	// Password Access Methods
	public String getPassword(){
		return this.password;
	}
	public void setPassword(String password){
		this.password = password;
	}
	
	// First Name Access Methods
	public String getFirstName(){
		return this.firstName;
	}
	public void setFirstName(String firstName){
		this.firstName = firstName;
	}
	
	// Last Name Access Methods
	public String getLastName(){
		return this.lastName;
	}
	public void setLastName(String lastName){
		this.lastName = lastName;
	}
}
