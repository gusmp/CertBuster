# ** NOTE: This project has been superseeded by CertBuster 2**


1.What is CertBuster?
=====================

CerBuster is tiny tool which let you keep under control which and when your SSL certificates will expired. This is very useful in big organizations which the knowledge of the server state is blur or inexistent.

CertBuster will scan the server you want and will do for you:

* A report about what was found
* Send you an e-mail with the next certificates to expire / already expired

CertBuster is an command tool. However, you can automatize it which as a job using tools like Cron in Linux systems.
Doing this, you will have a fresh report and a warning email periodically.

2.Requirements
===============

CertBuster is a Java application. Verify you have an updated Java version. If you don't, you can download from http://www.java.com/es/download/

3.Compile with dependencies (recommended!)
==========================================

You can create the package with the dependencies in a single jar file. To generate this jar just type:

mvn clean compile assembly:single

Or

mvn clean compile assembly:single -DskipTests=true

To skip the tests.

NOTE: In order to execute the tests correctly you will need to configure your smtp properties

If the process is successful in the folder "target" will be a jar file called certBuster-VERSION-jar-with-dependencies.jar

4.Compile without dependencies (for experts)
============================================

CertBuster uses Maven. First install it and execute:

mvn clean package


Or

mvn clean package -DskipTests=true

In case you want to execute the test.

If the process is successful in the folder "target" will be a jar file called certBuster-VERSION.jar

4.Usage
=======

Before starting using CertBuster you need two files:

* Configuration file
* List of host to scan

4.1 Configuration file
----------------------

The configuration file let you enable/disable features of CertBuster. Please see the example file of \src\test\resources\config.properties for further details.

4.2 Hosts file
--------------

This is a list of hosts to analyse in CSV format. Although you can user a plain text editor, I suggest to use a
spreadsheet such as Calc or Excel.
The first line is made by the headers. Second a next lines are the host to scan. These lines follows the structure:

HOST; PORT

or

HOST; LOWER_PORT_RANGE-UPPER_PORT_RANGE

or

HOST

In the latter case the port used is https (443)

The folder \src\test\resources\hosts.csv has an example with correct / wrong entries.

5.Run CertBuster (with dependencies)
====================================

In the target folder you will have the file:

certBuster-<VERSION>-jar-with-dependencies.jar

Just type:

java -jar certBuster-<VERSION>-jar-with-dependencies.jar <CONFIG_FILE>

6.Run CertBuster (without dependencies)
========================================

First verify that the files (included in libs folder):

activation-1.1.1.jar
bcprov-ext-jdk15on-1.48.jar
mail-1.4.5.jar

Are in the same folder of CertBuster. Now execute it with:

java -jar certBuster-VERSION.jar <CONFIG_FILE>
