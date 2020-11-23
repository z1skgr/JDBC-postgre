package lab2;

import java.io.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int answer=-1;
		DBapp dbapp = new DBapp();
		System.out.println("Welcome!\n");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));//Create a variable to read from console
		boolean flag=false;
		while(!flag){
			try{
				do{
					System.out.println("\nChoices\n1)Start Connection");
					System.out.println("2)Show detailed grades for a student using AM");
					System.out.println("3)Change grade for a student using AM and course code ");
					System.out.println("4)Create backup database");
					System.out.print("5)Exit program\n");
					System.out.print("Select option:");
					String input = br.readLine();
					answer= Integer.parseInt(input);
				}while(answer<0 && answer>5);
			}catch(NumberFormatException | IOException e){
				System.out.println("Your option does not represent a number");
			}
			
			
	        if(answer==1){
	    		dbapp.dbConnect("localhost",5432,"postgres", "postgres", "postgres");
	        	System.out.println("Connection completed");
	        	flag=false;
	        }
	        if (answer==2){
	        	System.out.println("Give am for a student:");
	        	try {
					String input = br.readLine();
					dbapp.detailedGrades(input);
				} catch ( IOException e) {
					e.printStackTrace();
				}
	        	flag=false;}
	        if (answer==3){
	        	String input1 = null;
	        	System.out.println("Give am for a student:");
	        	try {
					input1 = br.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.out.println("Give course code for a student:");
	        	try {
					String input2= br.readLine();
					dbapp.updateStudentGrade(input1, input2);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	flag=false;}
	        if (answer==4){
	        	dbapp.backupDatabase();
	        	System.out.println("Backup database created successfully");
	        	flag=false;}
	        if(answer==5)
	        	flag=true;
		}
		System.out.println("\n\nExit program\nBye\n");
	}

}

