SAATR
=====

SAATR is a prototype of a simple tool for Storing And Analyzing Tests Results. In this context Tests mean automatic tests running on Jenkins CI. If you have tens or hundreds of Jenkins jobs on many platforms with many configuration options then you could consider this tool useful. Our motivation was:

 - to have better overview about the most failing test case, test method, database, environment, etc
 - to easily recognize common failure cause
 - to track history of tests runs


How Does It Work?
-----------------

There are supposed to be two basic modes:  

1. full automatic - triggered on the end of successful job run [not implemented yet]
2. combined - intended for failed jobs which need manual investigation

The Combined mode uses a configuration file, e.g.: 

```xml
<document>
	<name>CrashRecFailed</name>
	<jenkinsMinerClass>org.jboss.qa.tool.saatr.jenkins.miner.crashrec.CrashRecJenkinsMiner</jenkinsMinerClass>
	<fields>
		<field>
			<name>timestamp</name>
		</field>
		<field>
			<name>jobName</name>
		</field>
		<field>
			<name>productVersion</name>
		</field>
		<field>
			<name>testCase</name>
		</field>
		<field>
			<name>testName</name>
		</field>
		<field>
			<name>txType</name>
			<options>
				<option>JTA</option>
				<option>JTS</option>
			</options>
		</field>
		<field>
			<name>failureType</name>
		</field>
		<field>
			<name>exceptionStacktrace</name>
		</field>
		<field>
			<name>solution</name>
		</field>
	</fields>
</document> 
```
which defines what kind of data will be stored. It also contains name of 'jenkinsMinerClass' which is responsible for getting as much data as possible from Jenkins. Besides this it is also planned to store another artifacts as log files, config files, etc.

Based on this config file and custom implementation of IJenkinsMiner (which can add another fields) a web page which allows to fill empty values or correct values filled by a Miner is generated.

The resulting document is stored in MongoDB.

Analyzing part of the tool has not been implemented yet but it is possible to use any MongoDB client for querying data. For example https://github.com/rsercano/mongoclient is in docker hub so it is very simple to use it:

```docker pull mongoclient/mongoclient```    
```docker run -d -p 3000:3000 mongoclient/mongoclient``` 

Modules
-------
	|-- saatr-parent
	    |-- saatr
	    |-- saatr-spi
	    `-- saatr-jenkins
	
- saatr: the core project, includes the web application and basic workflow;
- saatr-spi: contains only IJenkinsMiner which is supposed to be implemented by a third party;
- saatr-jenkins: example of Jenkins Miner implementation;

Run the application
-------------------

If you want to run this application without deploying, run the Start class. But before that you should start MongoDB and adjust these properties files:

 - saatr-parent/saatr/src/main/resources/application.properties
 - saatr-parent/saatr-jenkins/src/main/resources/auth.properties
