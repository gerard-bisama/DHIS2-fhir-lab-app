# Fhir Lab integration app
The FHIR Lab apps is utilizing the HL7 FHIR standard to represent IDSR data to ensure the sharing of  data between the IDSR, laboratory facilities and other systems that could need to have access to the laboratory data as well as patient demographic and disease specifics information. 

The case is represented by the patient demographic information, disease to follow up and the symptoms description and is recorded in the IDSR in a specific programs. Laboratory data are  registered for the specifics case as events. The laboratory data comes from the facilities and contain information on providers (Organization and care provider), specimen handing, laboratory order, laboratory observation and test performed. 

Those information  are collected by facilities and submitted to a central IDSR which is a DHIS2 based system. To submit the laboratory data, the facilities should be able to have information from the IDSR in the standardized format to allow them to reference case and related laboratory data in their own system or procedure, in order to share (pull or push) data without caring of the  data structure of the client or the IDRS service providers.

That is why the app architure is composed of the Fhir conversion module that generates HL7 Fhir resource from the IDSR data and  csv based Lab data. Then the generated Fhir resource are stored in a Fhir repository which provides the appropriate interface for search and CRUD operation. Then, all the requests could be made to the  Fhir repository to get formalized information on Lab request, specimen handling information, status of the results, disease diagnosed, etc.

#Getting started guide
The tools is composed of 3 mains applications:
1. The dhis2-fhir conversion: used to convert dhis2 organisation unit and tracked entities to FHIR based resource. The configuration is done here to map tracked entities and tracked entities attributes to fhir related resource to allow the conversion. For Organization unit there is no need for mapping. This module provide also a web UI to allow facilities to upload data. 
2. The HAPI FHIR Server: used to store and validate dhis tracked entities converted to FHIR resources. Act as FHIR Repository an could be used by any fhir client to request resource.
3. The fhir mediator: used to synchronize interation between the conversion module, the IDSR and the Fhir Repository. A cronjob can be configured to trigger different actions. The main action concern the dhis2->Fhir conversion, the sync of converted Fhir resource with the Hapi server, the extraction and conversion of csv data in to Fhir resources and then, the sync of csv converted data with DHIS2.

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
Do the same for others FHIR resource model.

An other challenge is that more than one practitioner can be involved on the laboratory management process. There is no way for the app to categorized the role of practitioner at any stage of the program. The main role in the laboratory management is the care provider who requested the lab exam, the provider who collect the specimen and the provider who perferm the examination. The role are hard coded in like this: care_provider, specimen_collector,observation_performer
```sh
"practitioner_stage_nature":[
{
	"stage":"Initial investigation and diagnosis",# Name of stage as defined in the program. if no practitioner information is concerned leave empty all stage attribute
	"nature":"care_provider"
},
{
	"stage":"Specimen Handling",
	"nature":"specimen_collector"
},
{
	"stage":"Lab Results",
	"nature":"observation_performer"
}
],
```

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
.
.
resourceTempLocation=/home/datalab/temp #make sure that the following path are pointed the appropriate file. this allow the configuration to be done in one place but they are used by more than one app
dataElementMappingFile=/home/datalab/FileMapping.json
fhirAttributeMapping=/media/dhis2-fhir/convert-dhi2-fhir/manifest.webapp
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
To open the UI for file uploading and csv template downloading, enter the following address in the browser
```sh
http://localhost:8083/uploadfile
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
To run the synchronization of data, perform the following action in the defined order:
a. run the synchronization of data with the Hapi Server to ensure that all IDSR lab data are in the Fhir Server
b. process all uploaded file and push the extracted Fhir resource in the Hapi Server as Bundle of type collection
c. check in there is collection in pending status and process them to sync data to the IDSR. The redo again a.

Here is a cron tab that you can create to perform these actions

```sh
 */5 * * * * curl -k -u tut:tut https://localhost:5000/entitytrackers/1  
*/7 * * * * curl http://localhost:8083/csv2fhir 
*/8 * * * * curl -k -u tut:tut https://localhost:5000/pushfhir/1 
 ```
Replace localhost by the IP of the server and the time to the estimation that may fit

