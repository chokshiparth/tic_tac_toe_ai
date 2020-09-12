import java.sql.*;

//connection object
public class connectToDB {
	
	private Connection con;
	private String dburl;
	private String passwd;
	private String userName;
	
	//default constructor
	public connectToDB() {
		// TODO Auto-generated constructor stub
		this.con=null;
		this.dburl=null;
		this.passwd=null;
		this.userName=null;
	}
	
	//build connection with database
	private void buildConnection()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			this.userName="root";
			this.passwd="";
			this.dburl="jdbc:mysql://localhost:3306/";
			this.con=DriverManager.getConnection(this.dburl,this.userName,this.passwd);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	
	//get connection object
	public Connection getConectionDB()
	{
		this.buildConnection();
		return this.con;
	}
	
}