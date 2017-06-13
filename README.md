# DHIS2 Tracker FHIR APP
This tools is used to search for DHIS2 tracked entities related to Lab based Disease Surveillance (Patient,Practitioner,Organization,Specimen,Observation,Diagnostic Order and Diagnostic Report 
( > Ref:https://www.hl7.org/fhir/resourcelist.html) in the Tracker and convert then to DHIR related DSTU2 resources and synchronize the in the HAPI FHIR server,so the synchronized resources could be accessed through HAPI FHIR API by third party application for IDSR.
Two main generic use cases has been implemented:
1. Publish facility and Geographic Hierarchy data in the FHIR Repository
2. Publish and query Health Worker demographic information in the FHIR Repository
3. Creation and query of client demographic information
4. Creation and query of Laboratory test and result for reporting and verification of Suspected Disease.

The Laboratory Testing Workflow (LTW) Integration  and the Sharing Laboratory Reports Profile are used as model to produce the data for the Lab based IDSR.

The Laboratory Testing Workflow (LTW) Integration covers the workflow related to tests performed by a clinical laboratory inside a healthcare institution, for both identified orders and unknown orders, related to both identified patients and unidentified or misidentified patients.The Sharing Laboratory Reports Content Profile defines the laboratory report, observation and interpretation as electronic content to be shared in a community of healthcare settings and care providers.(Ref: IHE Laboratory Technical Framework, Volume 1 (LAB TF-1))

#Getting started guide
The tools is composed of 3 mains applications:
1. The dhis2-fhir convertion: used to convert dhis2 organisation unit and tracked entities to FHIR based resource. The configuration is done here to map tracked entities and tracked entities attributes to fhir related resource to allow the conversion. For Organization unit there is no need for mapping.
2. The HAPI FHIR Server: used to store and validate dhis tracked entities converted to FHIR resources. Act as FHIR Repository an could be used by any fhir client to request resource.
3. The fhir mediator: used to synchronize data between dhis2 and the Fhir Repository. A cronjob can be configured to synchronize tracked entities in the FHIR repository on time base.

To begin, copy the source: ```sh git clone https://github.com/gerard-bisama/DHIS2-fhir-lab-app.git```. The tree components are copied in the source.

#Configuration
1. Configuration of dhis2-fhir convertion. 
A mapping should be done between FHIR based resources and tracked entities in DHIS2. Except for the Organization unit.
The dhis2-fhir convertion is running on the port 8083.
```sh
cd convert-dhi2-fhir
gedit manifest.webapp
```
In the manifest.webapp, configure the following values:
```sh
href": "http://localhost:8082" # to replace with DHIS2 url and port
"temp_directory":"/home/user/datalab/" #replace with the directory that will be used to generate csv file and data elements mapping file
"authentication":"admin:district" # Replace admin with the user and district with the password 
```
The file maniferst.webapp also contains FHIR data model template to be filled by the corresponding entity tracker attributes and dataelements.Ones needs first to specify the model and stage/Event used to register cases and related laboratory information.
the steps are:
a. Go in DHIS2 indentify program ID and stages ID involved in the Lab registration. Then open the manifest.webapp to configure this information. We can add as many stages as they are involved.
```sh
"programs_progstages_tracked":[
	  {
		"id":"pxxxxxxxx",
		"stages":[
			"Yxxxxxxx",
			"axxxxxxx"
		]
	  }
	],
```
Then follow the installation instruction as explained  at the section installation (1). But instead of execute the link http://localhost:8083/trackedentities use this link http://localhost:8083/trackedentities_csv. This will generate 2 files, the first is a csv file with the name of program tracked and it contains all attributes and data elements used in the concerned program to enter data. The csv file  is used to perform the mapping with Fhir data model. 
The second file is named "FileMapping.json" and it contains the mapping between names of data elements and their ids, since to configure the data mapping we use displayName instead of ids.
Each column of the csv file  that must be collected should be mapped with the corresponding FHIR resource model. A bit knowlegde of the FHIR resource and the metadata structure of DHIS2 is necessary.

For example, for patient resource the mapping of csv column names and FHIR resource looks like this:
```sh
"patient_attribute_mapping":
  {
	"id":"trackedEntityInstance",
	"managingOrganization":"Registering Unit",
	"identifier":"Unique Case ID",
	"name_family":"First Name",
	"name_given":"Last name",
	"telecom_phone":"", # When there is no a corresponding value in the csv leave the field empty
	"telecom_email":"",
	"gender":"",
	"birthDate":"Age (Years)",
	"address":"Village/Domicile",
	"deceasedBoolean":"Immediate Outcome"
  }
```
Do the same for other FHIR resource model.

2. Configuration of the FHIR-mediator

The first step is to allow the mediator to read the configuration file.
The step are:
- Create the user that has the right to read the config file
```sh
useradd -d /home/fhirmediator -m fhirmediator -s /bin/bash
cd /home/ fhirmediator
mkdir config
 ```
- Copy the configuration file from the source the the config folder and give all right to the user fhirmediator
```sh
cp  /thelocationofyoursourcecode/fhirmediator.properties  /home/fhirmediator/config/
sudo usermod -a -G youruser fhirmediator
sudo chown -R fhirmediator:yourusergroup /home/fhirmediator/config/
 ```
- Change the source of data and the destination.
```sh 
sudo gedit /home/mediator/config/fhirmediator.properties
```
Make sure that the fhirmediator user is the owner of files in the fhirmediator directory.(use chown)

In the fhirmediator.properties change the value as shown 

```sh 
serverSourceURI=localhost #IP address of the dhis2-fhir convertion app
serverSourcePort=8083 #the default port of dhis2-fhir convertion app
serverSourceScheme=http #could be https also depending on the server configuration
serverSourceAppName=null
minTresholdSyncDate=2016-10-06T08:08:52
serverSourceFhirDataModel=trackedentities
lastSyncDate=0
pathForResourceWithError=/home/server-hit/Documents # When the parse of the resource bundle return from dhis2-fhir convertion failed, the file is place here
serverTargetURI=localhost #IP of the HAPI Server
serverTargetPort=8084 # Port of the HAPI server
serverTargetScheme=http # could be https also depending on the server configuration
serverTargetAppName=hapi-fhir-jpaserver-local
serverTargetFhirDataModel=baseDstu2
```
The second step is to configure the connection with the openHIM core
```sh 
sudo gedit /thelocationofyoursourcecode/openhim-mediator-hapifhir/src/main/resources/mediator.properties
```
In the mediator.properties, change the values as shown

```sh
core.host=localhost
core.api.port=8080 # the port of openhim-core, configure during the installation of openhim-core. Open the config.json to get it 
core.api.user=root@openhim.org #Default openhim admin username
core.api.password=xxxx #password defined during the installation of openhim
``` 
Copy also the FileMapping.json file generated by the app  the convert-dhis2-fhir and the configured manifest.webapp in /home/mediator/config.

3. Configuration of the HAPI JPA Server

To make the JPA server working ones need to create a new mysql database and change the configuration files of JPA local server. 
To install mysql go to "https://doc.ubuntu-fr.org/mysql". Create a database named "dhis2_fhir"
- Open the config file locate here: /thelocationofyoursourcecode/dhis2-fhir/hapi-fhir-jpaserver-local/src/main/java/ca/uhn/fhir/jpa/demo.
- Open the file FhirServerConfig.java, locate the code the public DataSource dataSource(), then  replace the lines below to the corresponding values.

```sh
public DataSource dataSource() {
		BasicDataSource retVal = new BasicDataSource();
		try
		{
			retVal.setDriver(new com.mysql.jdbc.Driver());
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
		}
		retVal.setUrl("jdbc:mysql://localhost:3306/dhis2_fhir");
		retVal.setUsername("dbuser"); //Replace by the username
		retVal.setPassword("bdpassword");
		
		return retVal;
	}
```
The JPA server will run on the port 8084 but if Ones needs to change this value. Open the pom.xml file and locate the tag 
```sh
<httpConnector>
<port>8084</port>
</httpConnector>
```
#Installation
1. dhis2-fhir convertion
```sh
cd convert-dhi2-fhir
npm install #to instal all dependencies
npm start
```
the run the app on port 8083 by default. Can check if the up return the bundle of related DHIS2 tracked entities with
```sh
curl http://localhost:8083/trackedentities
```

Prerequisites: Install nodejs v4 or greater (>https://nodejs.org/en/download/) , npm

2. FHIR-mediator
```sh
cd  /thelocationofyoursourcecode/openhim-mediator-hapifhir
mvn install
java -jar target/fhir-mediator-0.1.0-jar-with-dependencies.jar
```
The maven package should also be install to allow the project build
If you are attempting to start your Java Mediator and you are experiencing a SunCertPathBuilderException error then you will need to follow the instructions on the link to install the self signed certificate.

> http://openhim.readthedocs.io/en/latest/tutorial/3-creating-a-passthrough-mediator.html#suncertpathbuilderexception-unable-to-find-valid-certification-path-to-requested-target

Then navigate to the Mediators section on OpenHIM to see your Mediator and check in the client section if the client "tut" is created. If not create the client manualy as shown in the link below.
> http://openhim.readthedocs.io/en/latest/tutorial/2-creating-a-channel.html#

- Prerequisites: 

To install the mediator, you have to install OpenHIM
OpenHIM stands for the Open Health Information Mediator. The OpenHIM is an interoperability layer: a software component that eases integration between disparate information systems by connecting client and infrastructure components together. Its role is to provide a facade to client systems - providing a single point-of-control for defining web service APIs and adapting and orchestrating requests between infrastructure services. The OpenHIM also provides security, client management and transaction monitoring. 
> http://openhim.readthedocs.io/en/latest/about.html)
To install OpenHIM go to  
> http://openhim.readthedocs.io/en/latest/getting-started.html)

Install openjdk7 at least. If you experiencing the SunCertPathBuilderException error. Follow the instruction on the link > http://openhim.readthedocs.io/en/latest/tutorial/3-creating-a-passthrough-mediator.html#suncertpathbuilderexception-unable-to-find-valid-certification-path-to-requested-target

3. HAPI JPA server

```sh
cd /thelocationofyoursourcecode/hapi-fhir-jpaserver-local
mvn install
mvn jetty:run
```
If the build failed, check the "hapi-fhir-base" corresponding fhir version at >https://mvnrepository.com/artifact/ca.uhn.hapi.fhir/hapi-fhir-base.
Then change the version in the pom.xml file.

```sh
<parent>
	<groupId>ca.uhn.hapi.fhir</groupId>
	<artifactId>hapi-fhir</artifactId>
	<version>XXXXX</version> 
	<relativePath>../pom.xml</relativePath>
</parent>
```

Then, the Fhir local server can be accessed on http://localhost:8084/hapi-fhir-jpaserver-local (localhost: can be replaced by a public IP address)
> http://hapifhir.io/doc_jpa.html

#Launch the synchronization
To run the synchronization of data, run the command
```sh
 curl -k -u tut:tut https://localhost:5000/entitytrackers/1
 ```
Replace localhost by the IP of the server.

