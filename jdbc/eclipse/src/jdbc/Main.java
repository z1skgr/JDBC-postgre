/*
 * +-----------------------------------------------------+
 * | Authors  : Christos Ziskas  						 |
 * |                                                     |
 * | Description: JDBC query to college database		 |
 * | 													 |
 * | Date : Spring 2017                         		 |
 * +-----------------------------------------------------+
 */

package jdbc;

import java.io.*;

import java.util.Scanner;

import tuc.ece.cs102.util.StandardInputRead;  

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int answer=-1;
		DBapp dbapp = new DBapp();
		System.out.println("Welcome!\n");
		StandardInputRead br= new StandardInputRead();
		boolean flag=false;
		while(!flag){
			
			do{
				System.out.println("\nChoices\n1)Start Connection");
				System.out.println("2)Show detailed grades for a student using Register Number");
				System.out.println("3)Change grade for a student using Register Number and Course Code ");
				System.out.print("4)Exit program\n");
				answer = br.readPositiveInt("Select option: ");
			}while(answer<0 && answer>5);
			
			
	        if(answer==1){
	        	String database = br.readString("Give Database name to establish connection: ");
	    		dbapp.dbConnect("localhost",5432, database, "postgres", "postgres");
	        	System.out.println("Connection completed");

	        }
	        else if (answer==2){
	        	String input = br.readString("Give Register Number for a student: ");
				dbapp.detailedGrades(input);
	        }
	        else if (answer==3){
	        	String input1 = br.readString("Give Register Number for a student: ");
				String input2= br.readString("Give Course Code for a student: ");
				float input3=0;
				do {
					input3 = br.readPositiveFloat("Give new grade: ");
				}while(input3<0 && input3 > 10);
				
				dbapp.updateStudentGrade(input1, input2, input3);
	        }
	        
	        if(answer==4)
	        	flag=true;
	        	
		}
		System.out.println("\n\nExit program\nBye\n");
	}

}

