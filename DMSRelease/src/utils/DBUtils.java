package utils;


import java.sql.ResultSet;

import connections.MySqlConnector;

public class DBUtils {

	public static int findUserId(MySqlConnector mysql, String dmsuser_search_query)
	{
		int uid=0;
		try
		{
			
			ResultSet resultset = mysql.executeQuery(dmsuser_search_query);
			resultset.last();
			int resultsetCount = resultset.getRow();
			if (resultsetCount == 1){
				resultset.beforeFirst();
				resultset.next();  
				/* next() must be called before result is read, as beforefirst() 
				 * takes the pointer to header, not to current row. 
				 */
				uid = resultset.getInt("userid");
				System.out.println("User found in db with user_id = "+uid);
				
			}
			else
			{
				System.out.println("User not found in db");
			}
		}catch(Exception e)
		{
			System.out.println("Exception in execution");
		}
		
		return uid;
		
	}
	
}
