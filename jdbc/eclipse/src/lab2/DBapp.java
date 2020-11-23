package lab2;

import java.sql.*;
import java.util.Scanner;

public class DBapp {
	Connection conn;
	Connection backupconn;
	String backupdbname = null;
	public DBapp() {
		conn=null;
		backupconn = null;
	}
	public void dbConnect (String ip, int port, String database, String username, String password) {
		try {
			// Check if postgres driver is loaded
     		Class.forName("org.postgresql.Driver");
     		// Establish connection with the database
     		System.out.println("Looking for database..");
     		conn = DriverManager.getConnection("jdbc:postgresql://"+ip+":"+port+"/"+database,username,password);
     		System.out.println("Connected with database..\n");
     		// Disable autocommit.
     		conn.setAutoCommit(false);
     	} catch(Exception e) {
            e.printStackTrace();
		}
	}
	
	public void detailedGrades(String am) {
		
		int curSem = 0;
		PreparedStatement myStatement = null;
		String query = "Select serial_number, course_code, final_grade, name, surname from \"Register\" natural join \"Student\" where am = ? and final_grade is not null order by serial_number";
			
		try {
			//Statement myStatement = conn.createStatement();
			
			myStatement = conn.prepareStatement(query);
			myStatement.setString(1, am);
			
			ResultSet myResSet = myStatement.executeQuery();
			myResSet.next();
			System.out.println("----Όνομα: " + myResSet.getString(3) + ", Επώνυμο: " + myResSet.getString(4)+" ----");
			curSem = myResSet.getInt(1);
			System.out.println(">>> Εξάμηνο Νο:" + curSem);
			int i = 1;
			do {
				
				System.out.println("   "+i+". | Μάθημα: "+ myResSet.getString(2) + "    | Βαθμός:" + myResSet.getBigDecimal(3) );
				if(curSem != myResSet.getInt(1)) {
					curSem = myResSet.getInt(1);
					System.out.println(">>>>>>> Εξάμηνο Νο:" + curSem);
				}
				i++;
			}while(myResSet.next());
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NullPointerException e) {
			System.out.println("Please, connect to Server before any action!");
		}
	}
	
	public void updateStudentGrade(String am, String course_code) {
		
		double newGrade = 0;
		Scanner scanner = null;
		try{
			System.out.println("Δώστε νέα βαθμολογία: ");
			 scanner = new Scanner(System.in);
			 
			newGrade = scanner.nextDouble();
		}catch(NumberFormatException ex){
			System.out.println("Δε δώσατε σωστή βαθμολογία. \nΕγκατάλειψη προσπάθειας.\nΠαρακαλώ, επαναλάβετε.");
		}
		
		PreparedStatement myStatement = null;
		String query = "update \"Register\"  "+ 
				"set final_grade = ? "+ 
				"where amka in (" + 
				"	select amka from \"Register\" natural join \"Student\" " + 
				"	where am = ?) " + 
				"and course_code = ? " + 
				"and serial_number=(select max(serial_number) from \"Register\" natural join \"Student\" " + 
				"	where am = ? and course_code = ? )";
		try {
			myStatement = conn.prepareStatement(query);
			myStatement.setDouble(1, newGrade);
			myStatement.setString(2, am);
			myStatement.setString(3, course_code);
			myStatement.setString(4, am);
			myStatement.setString(5, course_code);
			int rowNum = myStatement.executeUpdate();
			System.out.println("Table \"Register\" was updated. " + rowNum + " rows updated");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NullPointerException e) {
			System.out.println("Please, connect to Server before any action!");
		}
		
		scanner.close();
	}
	
	public void backupDatabase() {
		
		PreparedStatement statement = null, statement1 = null;
		ResultSet rs = null, rs1 = null;
		
		String selectdbs = "SELECT * FROM pg_database";
		String lookForBackupDBQuery = "SELECT datname FROM pg_database where datname=?";
		
		try {
			statement = conn.prepareStatement(lookForBackupDBQuery);
			Statement newstatement = conn.createStatement();
			
			System.out.println("Write the database name: ");
			Scanner scanner = new Scanner(System.in);
			backupdbname = scanner.nextLine();
			statement.setString(1, backupdbname);
			
			System.out.println("Looking for BackupDB...");
			rs = newstatement.executeQuery(selectdbs);
			
			if (!rs.next()) {
				System.out.println("Database not found. Please, Create a backup database....");
			}
			else { 
				System.out.println("BackUp Database Found");
				getBackupConnection("localhost",5432,backupdbname, "postgres", "postgres");
				
				
				DatabaseMetaData md = conn.getMetaData();
				ResultSet tablers = md.getTables(null, null, "%", null);
				
				while(tablers.next()) {
					backupTable(tablers.getString(3));
					}
				
				scanner.close();
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		       if (conn != null) {
		           try {
		                System.err.print("Transaction is being rolled back");
		                conn.rollback();
		            } catch(SQLException excep) {
		               excep.printStackTrace();
		         }
		     }
		}catch (NullPointerException e) {
			System.out.println("Please, connect to Server before any action!");
		}
		
	}
		
	public void backupTable(String tableName) {
		
		try {
			
				String createTableQuery = "Select * FROM generatetableddl(" + tableName + ", " + backupdbname + ")";
				Statement stmt = conn.createStatement();
				ResultSet caca = stmt.executeQuery(createTableQuery);
				
				conn.commit();
				
				String getValuesQuery = "Select * FROM " + tableName;
				Statement getValsStmt = conn.createStatement();
				ResultSet gVrs = getValsStmt.executeQuery(getValuesQuery);
				conn.commit();
				
				while(gVrs.next()) {
					String insertSetQuery = "INSERT INTO \"" + tableName + "\" (";
					String insertValuesQuery = ") values(";
					
					for(int i = 1; i<= gVrs.getMetaData().getColumnCount(); i++) {
						insertSetQuery = insertSetQuery + gVrs.getMetaData().getColumnName(i);
						if(i!=gVrs.getMetaData().getColumnCount())
							insertSetQuery += ", ";
						
						insertValuesQuery = insertValuesQuery + gVrs.getString(i);
						if(i!=gVrs.getMetaData().getColumnCount())
							insertValuesQuery += ", ";
					}
					
					String finalInsertQuery = insertSetQuery + insertValuesQuery + ")";
					PreparedStatement fillRowStmt = backupconn.prepareStatement(finalInsertQuery);
					System.out.println(finalInsertQuery);
					//fillRowStmt.executeUpdate();
					//conn.commit();
				}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NullPointerException e) {
			System.out.println("Please, connect to Server before any action!");
		}
		
	}
	
	public void getBackupConnection(String ip, int port, String backupdbname, String username, String password) {
		try {
			// Check if postgres driver is loaded
     		Class.forName("org.postgresql.Driver");
     		// Establish connection with the database
     		System.out.println("Looking for database..");
     		backupconn = DriverManager.getConnection("jdbc:postgresql://"+ip+":"+port+"/"+backupdbname,username,password);
     		System.out.println("Connected with database..\n");
     		// Disable autocommit.
     		backupconn.setAutoCommit(false);
     	} catch(Exception e) {
            e.printStackTrace();
		}
	}
}
	
	/*public void test() {

		int j = 1;
		do{
			
			System.out.println("Row: " + j + " and Column count: "+ rs.getMetaData().getColumnCount() +"\n" );
			
			for(int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				
				Object key = new Object();
				
				System.out.println("-------------------------");
				if (rs.getMetaData().getColumnTypeName(i) == "name" || rs.getMetaData().getColumnTypeName(i) == "text" || rs.getMetaData().getColumnTypeName(i) == "character") {
					key = rs.getString(i);
				}else if(rs.getMetaData().getColumnTypeName(i) == "int" || rs.getMetaData().getColumnTypeName(i) == "integers") {
					System.out.println("Int test");
					key = rs.getInt(i);
				}else if(rs.getMetaData().getColumnTypeName(i) == "numeric" || rs.getMetaData().getColumnTypeName(i) == "decimal") {
					key = rs.getBigDecimal(i);
				}else if(rs.getMetaData().getColumnTypeName(i) == "bool" || rs.getMetaData().getColumnTypeName(i) == "bit") {
					System.out.println("Bool test");
					key = rs.getBoolean(i);
				}else if(rs.getMetaData().getColumnTypeName(i) == "date") {
					key = rs.getDate(i);
				}else 
					key = rs.getString(i);
				System.out.println("Name: " + rs.getMetaData().getColumnName(i) + ", Value: " + key + " of type " + rs.getMetaData().getColumnTypeName(i));
			}
			j++;
		}while(rs.next());
	}
	
}*/

