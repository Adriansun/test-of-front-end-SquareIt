# SquareIt

## Description

SquareIt is a Spring Boot rest-service project. It is to be viewed as
an example of how a rest-service could be built and structured. The parts 
of the project are modular, thus easy to replace or modify.
For example changing of database drivers from a mySQL database to any
other database takes less than a minute. Classes and packages have a good 
layout and structure, which makes the project easy to overview and understand.
The emphasis is on the backend part of this project.

There is a frontend which communicates with the backend. The
backend rest-services process the data, sends the data to a mySQL
database for saving and querying, then returned to the frontend if required. 
E.g. An integer may be sent to the rest-service, saved on the database, and then returned 
squared to the frontend for presentation.

_If this SquareIt project is used in a production environment then a written consent must first be given 
by the creator of this project._

### Flow

The flow of the data through the system:
Frontend -> Backend -> Database --> Backend --> Frontend.

## General information
Project features and general information.

#### Frontend

_The frontend is used for functionality only, thus its purpose is not
to look pretty._

A user can:
 * Create a user profile.
 * Modify a user profile.
 * Delete a user profile.
 * Save a number.
 * Get a number back squared.
 * Delete a number based on a number id.
 * Count the total number of number in the database.
 * See a list of all items in the database.

#### Backend
Contains all of the project parts. It includes:

 * Spring Boot 2.
 * Maven.
 * Modules:
    * API-module.
    * Application-module.
 * API domain models.
 * Bread crumb id.
 * MDC request filters.
 * Swagger UI.
 * API documentation with Swagger2.
 * Web-configuration for headers & routing.
 * Jackson-bind, annotations, and object mapper for (de)serialization.
 * Logging info & debug in console, as well to file with intervals.
 * Builder pattern.
 * CrudRepository.
 * DAO.
 * JPA.
 * DTOs.
 * Hibernate.
 * POM-files.
 * Thread safe.
 * Rest-services.
 * Error handling.
 * MimeMessage - Email creator & sender
 * Personalized exceptions.
 * Custom Spring Boot 2 banner.
 * Service interfaces & service implementations.
 * Liquibase for managing the database tables and its content.
 * H2 in-memory database.
 * mySQL connection driver.
 * Testing with jUnit4 integration tests.
 * Google checkstyle checker.
 * Profiles for developing & production.
 * A good overview of classes & packages. In other words it has a good structure for the 9500+ rows of code.

#### Databases

An in-memory H2 database is used to simulate a database
during testing. mySQL database is used when running Spring Boot.
Liquibase manages the creation and modifications of tables content in the database automatically. 
In other words there is no need to modify your own database directly for management of columns, tables, and their 
properties.

#### Testing

jUnit4 is used during testing with:

 * Assert.
 * Assertions.
 * Before & After annotations.
 * Error code tracking & information.
 * RestTemplates available with exchange:
    * Get.
    * GetList.
    * GetError.
    * Put.
    * PutError.
    * Post.
    * PostError.
  
###### Module tests statistics
    API-module: 94% classes, 95% lines covered by 49 tests.
    APP-module: 100% classes, 90% lines covered by 150 tests.
    
#### IDE
IntelliJ Idea was used during the making of this project.
 * Version: IntelliJ IDEA 2018.2.2.
 * Build: #IU-182.4129.33.
 * Bits: 64-bits.

##### IntelliJ Idea plug-ins
 * CheckStyle-IDEA.
 * InnerBuilder.
 * Statistic.
 * SonarLint.
 
##### Java JRE & JDK
 * Version: Java 8.
 * Build: 1.8.0_181.
 * Bits: 64-bits.

##### Apache Maven
 * Version: 3.5.4.
 
#### Apache Ant
 * Version: 1.10.5. 
 
#### mySql Workbench
 * Version: 8.0.

#### Apache Tomcat
 * Version: 9.0.11.

## Installation & configuration

Localhost installation guide for Windows 10. Install the programs
and files in this order. After installing these items the operating system should
be restarted even though it should not be required in theory.

#### 1. Java

 * Installing & configuring Java.
 
     1) Go to: ' http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html '.
     2) Click accept and download e.g. 'jdk-8u181-windows-x64.exe'.
     3) Install Java JDK.
     4) In Windows in environmental variables under 'System variables' make sure
     you have a variable called 'JAVA_TOOL_OPTIONS' with the value
     '-Dfile.encoding=UTF8'. A variable called 'JAVA_HOME' with 
     value e.g. 'C:\Program Files\Java\jdk1.8.0_181'. In variable 'Path'
     add value e.g. 'C:\Program Files\Java\jdk1.8.0_181\bin' and also
     add value e.g. 'C:\Program Files\Java\jre1.8.0_181\bin'. Add
     variable 'JRE_HOME' with the value e.g.
     'C:\Program Files\Java\jre1.8.0_181'.

#### 2. Maven

 * Installing & configuring Maven.
 
    1) Go to: ' https://maven.apache.org/download.cgi '.
    2) Download 'apache-maven-3.5.4-bin.zip'.
    3) Unzip and move the catalogue to e.g. 'C:\Program Files\apache-maven-3.5.4'.
    4) In environmental variables under 'System variables' make sure you
    have a variable called 'MAVEN_HOME' with value e.g.
    'C:\Program Files\apache-maven-3.5.4' and a variable called
    'M2_HOME' with the value 'C:\Program Files\apache-maven-3.5.4'. In
    variable 'Path' add value '%M2_HOME%\bin'.

#### 3. ANT

 * Installing & configuring ANT.
 
    1) Go to: ' https://ant.apache.org/bindownload.cgi '.
    2) Download 'apache-ant-1.10.5-bin.zip'.
    3) Unzip and move the catalogue to e.g.
    'C:\Program Files\apache-ant-1.10.5'.
    4) In environmental variables under 'System variables' make sure you
    have a variable called 'ANT_HOME' with the value e.g.
    'C:\Program Files\apache-ant-1.10.5'. In variable 'Path' add the
    value '%ANT_HOME%\bin'.

#### 4. mySQL

 * Installing & configuring mySQL.
 
    1) Go to: ' https://dev.mysql.com/downloads/windows/ '.
    2) Click on 'MySQL Installer'.
    3) Download ' Windows (x86, 32-bit), MSI Installer '.
    4) Install mySQL with any root username and password.
    5) Start mySQL Workbench.
    6) Create a database named 'squareit'.
    7) Create a user named e.g. 'everyone' with e.g. password 'Adrian12'.
    8) Grant all privileges to user 'everyone' on database 'squareit'.
    9) Make sure that the server is running on port '3306'.

_Nothing else is required. Liquibase in the Spring Boot project will handle the rest._

#### 5. Tomcat

_For now Tomcat does not work with Java 9. You must use Java 8 and only
have Java 8 installed on your system during the installing of Tomcat._

 * Installing & configuring Tomcat.
    1) Go to: ' https://tomcat.apache.org/download-90.cgi '.
    2) Download: '64-bit Windows zip', unzip it, and install Tomcat.
    3) In environmental variables under 'System variables' make sure you
    have a variable called 'CATALINA_HOME' with the value e.g.
    'C:\Program Files\Apache Software Foundation\Tomcat 9.0'.
    4) In the 'Path' variable add the value '%CATALINA_HOME%\bin'.
    5) Go to a terminal and the install catalogue and enter the bin catalogue. Write: " service.bat install ".
    Step back one step and enter ' /conf/tomcat-users.xml '. In this file add your own username and password 
    so it looks like this: "&lt;tomcat-users&gt;                            
                            	&lt;role rolename="manager-gui"/&gt;
                            	&lt;user username="admin" password="admin" roles="manager-gui"/&gt;
                            &lt;/tomcat-users&gt;".
    6) Restart your computer. If Windows can find it: In Windows start-menu go to Apache Tomcat 9.0 
    Tomcat9 and start 'Monitor Tomcat'. Right click on Tomcats tray icon and make sure that 'Start service' is on.

 * __Skip for now: Adding the project WAR file to Tomcat__
 
    7) Go to URL: ' localhost:8080 '.
    8) Click on the 'Manager App' button and enter credentials.
    9) Go down to 'WAR file to deploy' and browse for the WAR file that
    the project produced for you and push the deploy button.
    10) The project is now accessible through the URL:
    'localhost:8080'.

#### 6. IntelliJ

 * Installing & configuring IntelliJ.
    1) Go to URL: ' https://www.jetbrains.com/idea/download/#section=windows '.
    2) Click on 'Community' download .EXE file to download intelliJ.
    3) Install IntelliJ and start it.
    
    ##### Configure IntelliJ
    
    4) At the start-up screen, in the lower right corner, click on 'Configure' and select 'Settings'.
    5) Go to the menu 'Build, Execution, Deployment' -> 'Build Tools' -> 'Maven'.
    6) In the box 'Maven home directory:' enter the path to your Maven location, e.g. 
    "C:/Program Files/apache-maven-3.5.4".
    7) Enter the sub-menu 'Importing' and click/enable 'Import Maven projects automatically'.
    8) Click on 'Plugins' in the menu to the left.
    9) Click on 'Browse repositories...'.
    10) Search for and install the plugins mentioned earlier in this documentation.
    11) Click the 'OK' button when done. IntelliJ will ask you to restart IntelliJ for the plugins to work.

#### 7. Download the project
 
 * Downloading the project.
 
    1) In a terminal (e.g. Start menu in Windows -> Write "cmd" -> Windows will suggest "Command Prompt") write: 
    ' git clone https://github.com/adriansun/SquareIt.git '. The project will be downloaded to that location on your 
    computer.
    2) In IntelliJ's start menu pick 'Open' and select the downloaded project.
    3) Wait for Maven to automatically download all dependencies. This can take awhile.
    4) Sometimes the right Java version will not be set for the project. To solve this in IntelliJ go to menu 'File' ->
    'Project Structure...'. In this menu look at the menu 'Project'. Under 'Project SDK:' make sure that 
    '1.8 (java version "1.8.0_181)' has been selected. If not click on the 'New...' button -> 'JDK', and find your Java 
    JDK location press the 'OK' button and then 'OK' button again to get back to your project. IntelliJ should now be 
    building the project and will be ready in a few seconds.
    5) Enable custom checkstyle: Go to IntelliJ's menu 'File' -> 'Settings...' -> Menu 'Other Settings' -> 'Checkstyle'. 
    To the right of the 'Configuration File' there is a small '+' button. Click on it and select the location of the
    file from the root of this project the file 'google_checkstyle.xml' and enter a description name, click the 'Next'
    button and then the 'Finish' button, and then the 'OK' button. In the lower corner of IntelliJ there will be a 
    'CheckStyle' button in which you can select a profile to use and run it by pushing the play button.

#### 8. Configure Spring Boots application.properties
  _Personal information must be entered into the applications.properties files for the database, email, and IP-address._
  
    Correct example: spring.jpa.hibernate.ddl-auto=validate
    Incorrect example: spring.jpa.hibernate.ddl-auto='validate'
  
 * The location of this file is in squareit-app/src/main/resources/application.properties. Remember that you must not use 
    quotation marks in this file.
    
    1) Enter your own database username and password in the fields: 'spring.datasource.username' and 
    'spring.datasource.password'.
    2) Enter your own Google email username and password in the fields: 'spring.mail.username' and 
    'spring.mail.password'.
    3) Enter your own IP-address in the field: 'project.host-ip'. Use any terminal (Command Prompt) and write 
    'ipconfig'. The 'IPv4 Address' number will be your IP-number.

## Running the project

To run the project it is assumed that mySQL and Tomcat are running, and
that the project has been imported into IntelliJ with all dependencies
downloaded by Maven. When this documentation says "Write" then it refers to
IntelliJ's internal terminal where the input should be written.

#### Start the project

 * Alternative 1 - Running in the IDE:
    1) Change the URLs in squareit-app/src/main/resources/public/js/user.js to
        look at port 8082 with IP named 'localhost' on each service.
    2) Write 'mvn clean install'.
    3) Write 'cd squareit-app'.
    4) Write 'mvn spring-boot:run'.
    5) Go to a browser and write in the URL: "localhost:8082".

 * Alternative 2 - Running in Tomcat server:
    1) Change the URLs in squareit-app/src/main/resources/public/js/user.js to
    look at port 8080 with IP named 'localhost' or your specific IP on each service.
    2) Write 'mvn clean install'.
    3) Look in squareit-app/target and get the file named
    'squareit-app-1.0.0.war'.
    4) Rename it to "squareit.war".
    5) Go to this manuals part called '5. Tomcat' under the installation
    section and follow the part 'Adding the project WAR file to Tomcat'.

## API documentation - Swagger UI
The documentation of the API relies on Swagger2 documentation in favor
of plain old standard Javadoc. This enables real-time usage of the system as well
as documentation of the API. The project must be running in the IDE or Tomcat server.

 * Localhost URL: http://localhost:8082/swagger-ui.html
 * Localhost URL in Tomcat: http://localhost:8080/squareit/swagger-ui.html
 * Remote URL: http://ip-number:8080/squareit/swagger-ui.html
