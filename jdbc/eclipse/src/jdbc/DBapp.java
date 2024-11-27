package jdbc;

import java.sql.*;

public class DBapp {
	Connection conn; //Connection object
	Connection backupconn; //Connection backup
	String backupdbname = null;
	public DBapp() {
		conn=null;	//Initialize
		backupconn = null;
	}
	public void dbConnect (String ip, int port, String database, String username, String password) {
		try {
			// Check if postgres driver is loaded
     		Class.forName("org.postgresql.Driver");
     		// Establish connection with the database
     		System.out.println("Looking for database..");
     		conn = DriverManager.getConnection("jdbc:postgresql://"+ip+":"+port+"/"+database,username,password);
     		System.out.println("Connected with database " + database + "..\n");
     		// Disable auto-commit.
     		conn.setAutoCommit(false);
     	} catch(Exception e) {
            e.printStackTrace(); // Print stack trace in case of an exception
		}
	}
	
	public void detailedGrades(String am) {
		PreparedStatement myStatement = null;
		//Queries		String st_amka = "SELECT amka FROM \"Student\" WHERE am = ?";
		String st_am_name = "SELECT surname,name FROM \"Student\" WHERE am = ?";
		
		String st_get_all_marks = null;
		String st_get_count_per_year = "SELECT count(*), academic_year FROM \"Register\" FULL JOIN \"Semester\" on \"Register\".serial_number = \"Semester\".semester_id WHERE amka = ? AND register_status='pass' group by academic_year";
		String name=null, surname=null;
		int amka=-1;
		
		try {
			//Query for  getting amka of the student with user's am
			myStatement = conn.prepareStatement(st_amka);
			myStatement.setString(1, am);	// Set the parameter for the prepared statement - Change the ? symbol in the query variable
			ResultSet myResSet = myStatement.executeQuery(); //Execute query using sql library
			
			while(myResSet.next()) {
				amka=myResSet.getInt(1); // Store amka in variable
			}
			//Store amka in variable
			
			
			//Query for  getting name, surname of the student with user's am - Same procedure
			myStatement = conn.prepareStatement(st_am_name);
			myStatement.setString(1, am); // Set the parameter for the prepared statement - Change the ? symbol in the query variable
			myResSet = myStatement.executeQuery(); // Execute the query
			while(myResSet.next()) {
				surname=myResSet.getString(1);
				name=myResSet.getString(2);
			}
			
			//Query to find the count of passed courses each year - Same procedure
			myStatement = conn.prepareStatement(st_get_count_per_year);
			myStatement.setInt(1, amka);	 
			myResSet = myStatement.executeQuery();
			
			//Find the years student passed at least one course. Use it to allocate memory
			int count=0;
			while(myResSet.next()) {
				count++;
			}
			
			int course_count[]=new int[count];
			int j=0;
			myResSet = myStatement.executeQuery();
			//Store each years passed course count to a variable
			while(myResSet.next()) {
				course_count[j]=myResSet.getInt(1);
				j++;
			}
			
			
			
			//Query to find the records of passed courses each year - Same procedure
			st_get_all_marks = "SELECT course_code,final_grade, academic_season, academic_year FROM \"Register\" FULL JOIN \"Semester\" on \"Register\".serial_number = \"Semester\".semester_id WHERE amka = ? AND register_status='pass' order by serial_number";	//AND register_status=pass		
			myStatement = conn.prepareStatement(st_get_all_marks);
			myStatement.setInt(1, amka);
			myResSet = myStatement.executeQuery();
			
			//Print detail			System.out.println(" Name: "+ name + "    | Surname:" + surname );
			System.out.println("--->Detailed Grades<----");
			j=0; count=0;
			while(myResSet.next()) {
				System.out.println(" Course: "+ myResSet.getString(1) + "    | Grade:" + myResSet.getBigDecimal(2) + "  --->" + myResSet.getString(3) + " " + myResSet.getInt(4));
				
				//Temp condition to detail in proper manner. Course passed per academic year
				if(count==course_count[j]-1) {
					System.out.println("-------------------------------------------------");
					count=-1;
					j++;
				}
				count++;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NullPointerException e) {
			System.out.println("Please, connect to Server before any action!");
		}
	}
	
	public void updateStudentGrade(String am, String course_code, float grade) {
		PreparedStatement myStatement = null;
		String query = "update \"Register\"  "+ 
				"set final_grade = ? "+ 
				"where amka in (" + 
				"	select amka from \"Register\" natural join \"Student\" " + 
				"	where am  = ?) " + 
				"and course_code = ? " + 
				"and serial_number=(select max(serial_number) from \"Register\" natural join \"Student\" " + 
				"	where am = ? and course_code = ? )";

		try {
			myStatement = conn.prepareStatement(query); // Prepare statement for executing SQL queries
			myStatement.setFloat(1, grade);	//Fill query with variables - Instead ?
			myStatement.setString(2, am);
			myStatement.setString(3, course_code);
			myStatement.setString(4, am);
			myStatement.setString(5, course_code);
			int rowNum = myStatement.executeUpdate();
			//System.out.println(rowNum);
			System.out.println("Table \"Register\" was updated. " + rowNum + " rows updated");
			//System.out.println(myStatement);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NullPointerException e) {
			System.out.println("Please, connect to Server before any action!");
		}
	}
	
}
	
	

