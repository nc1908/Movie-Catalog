open up a command prompt, navigate to source folder where it contains the pom.xml file

execute maven command 'mvn package' on the directory to generate executable jar

once the maven command has finished, navigate to target directory

execute the follow command to run the application:
java -jar demo-1.0.0.jar

once the application is running you can access the app on the browser via the following link:
http://localhost:8080/

You can access the REST API via the following link when the app is running:
http://localhost:8080/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/