package connections;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MySqlConnector {
	public Connection db;
	String user     = null; // MySQL UserID
	String password = null; // MySQL Password
	String host     = null;
	String dbName   = null; // Database Name

	public MySqlConnector(){
		this.user     = "root";
		this.password = "root";
		this.host     = "jdbc:mysql://localhost/";
		this.dbName   = "dms"; 
	}

	public MySqlConnector(String dbName, String user, String password){
		if(dbName != null){
			this.dbName = dbName;
		}
		if(user != null){
			this.user = user;
		}
		if(password != null){
			this.password = password;
		}
	}

	public Connection getConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			db =  DriverManager.getConnection(this.host + this.dbName, this.user, this.password);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return db;
	}

	public void insertQuery(String query){
		Statement statement = null;
		try {
			statement = (Statement)db.createStatement();
			statement.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateQuery(String query){
		Statement statement = null;
		try {
			statement = (Statement)db.createStatement();
			statement.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String query){
		Statement statement= null;
		ResultSet resultset= null;
		try {
			statement = (Statement) db.createStatement();
			statement.executeQuery(query);
			resultset = statement.getResultSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultset;
	}
	public void closeConnection() throws SQLException{
		db.close();
	}
}