# db/JDBC
> Communication PostgreSQL with external java platform
 
  
 ## Table of contents
* [General Info](#general-information)
* [Features](#features)
* [Technologies Used](#technologies-used)
* [How to run](#how-to-run)
* [Acknowledgements](#acknowledgements)

## General Information
_JDBC (J ava D ata b ase C onnectivity)_ is a standard Java API for database and java language connection). In this project, we implement a java project to link a database (implemented in postgreSQL) and build an interface between them.  


## Features
1. Opening of Conciliation / Validation. 
2. Presentation of a detailed student score based on A.M. 
3. Change of score based on A.M. and course code. The change will concern the most recent course run 
4. Back up on a different basis to the same server[^1].

 ## Technologies Used
* Java IDE environment (Eclipse implemented)
* PostgreSQL

## How to run
1. Make a new java project in IDE.
2. Build a '.db' file or clone the one in the archive
2. Import  `java.sql` libraries.
3. Create a connection with database (_driverManager.getConnection(url)_)
4. Build and run.

## Acknowledgements
- This project was created for the requirements of the lesson Databases.


[^1]: It will be given as input the name of the table and the name of the base to be created the copy of the table.
