# FHIR Lab Integration Application

National Health Management Information Systems (HMIS) require real-time information on notifiable disease surveillance cases and events. This capability is typically captured in an Integrated Disease Surveillance and Response (IDSR) framework, though laboratories may have different information systems and reporting data types and structures. Thus, it is necessary to make the process of reporting easy and available to users of any lab system so that it can be available in a national HMIS. 

The fast healthcare interoperability resources (FHIR) lab app tools enable district and regional laboratories to report data on disease surveillance cases to an IDSR system that is based on the use of DHIS2 for a national HMIS and uses the FHIR standard for national warehousing and data sharing.

Specifically, the tools support district and regional laboratories to upload data using a simple CSV template. The tools then make the submitted data available in DHIS2 and in the FHIR server. The FHIR format enables sharing with other systems and the FHIR server is a national repository for all lab data.

## Workflows for Users and Admins

From the perspective of a user in a laboratory:
* The user obtains an account in DHIS2 that gives them permission to upload documents.
* The user logs into the FHIR lab app and downloads the CSV template that they will fill out with details.
* When the user has completed the form, they return to the FHIR lab app and submit the CSV.
* All other steps are done in the background, there is no more user intervention.

Users update the information system using CSV. Lab systems do not update DHIS2 directly. (At the present time, there is no smartphone app to do reporting. Just the main web interface for FHIR lab data and programmatic ways to interact with the FHIR lab app and FHIR server.)

From the perspective of an administrator:
* The DHIS is set up with IDSR support.
* There is an initial synchronization that is done prior to allowing any CSV uploads that synchronizes existing reference data from DHIS2 to FHIR server to create the structures in the FHIR server.
* When a CSV is uploaded, it transforms from CSV to collection of FHIR resources. During that transformation it resolves all references and checks for errors. If there are no errors, it submits those resources to FHIR as temporary data submission.
* Then, the FHIR lab app checks in DHIS2 for the data, and if the submission is valid and does not exist or needs to be updated it conducts an update and insert operation in DHIS2.
* Next, it syncs again to ensure that the new data is synched with the FHIR server that DHIS2 and FHIR have the same status.


## Architecture

The architecture requires the following components:
* **DHIS2 with IDSR**: A DHIS2 instance that is configured with IDSR. This is a prerequisite. It is not included in this repository.
* **FHIR Lab App (convert-dhi2-fhir)** This is a Node.js application used to convert DHIS2 programs and tracked entities to FHIR resources. The configuration is done here to map tracked entities and tracked entities attributes to FHIR related resource to allow the conversion. There is no need for mapping DHIS2 organization units. This module provide also a web UI to allow facilities to upload data.
* **HAPI FHIR Server (hapi-fhir-jpaserver-local)**: This is used to store and validate DHIS2 tracked entities converted to FHIR resources and to act as the FHIR Repository. Any FHIR client can request resources from this repository.
* **OpenHIM Console and Core API Server** This is not included the repository but is used as the reverse proxy to the FHIR lab mediator.
* **FHIR lab mediator (openhim-mediator-hapifhir)**: This is an OpenHIM mediator used to synchronize interation between the conversion module, the IDSR, and the FHIR Repository. A cronjob can be configured to trigger different actions. The main action concern the dhis2->Fhir conversion, the sync of converted Fhir resource with the HAPI-FHIR server, the extraction and conversion of CSV data to FHIR resources and then, the sync of CSV converted data with DHIS2.

> Note: The repossitory includes legacy software that can be ignored (convert-csvdata-2fhir has been incorporated into convert-dhi-fhir and convert-dhi2-fhir-ssa is not used.)

## Installation

### DHIS2, IDSR and OpenHIM

A full setup requires a DHIS instance with IDSR configured and the OpenHIM console and API server. Easy-to-use Docker builds are available:
* [DHIS2 web application](https://hub.docker.com/r/dhis2/dhis2-web/) This does not include IDSR.
* [DHIS2 database](https://hub.docker.com/r/pgracio/dhis2-db/)
* A [DHIS2-docker repository](https://github.com/pgracio/dhis2-docker) docker-compose that links these together and includes the necessary PostGIS database.
* [OpenHIM-Core](https://hub.docker.com/r/jembi/openhim-core/)
* [OpenHIM Console](https://hub.docker.com/r/jembi/openhim-console/)
* An example of a working [OpenHIM stack](https://github.com/citizenrich/openhim-stack-docker) docker-compose file that includes the necessary MongoDB.


### FHIR Lab App

```
git clone https://github.com/gerard-bisama/DHIS2-fhir-lab-app
cd convert-dhi2-fhir
```
In the manifest.webapp, configure the following values:

```sh
"http://localhost:8082" # to replace with DHIS2 url and port
"temp_directory":"/home/user/datalab/" # replace with the directory that will be used to generate csv file and data elements mapping file
"authentication":"admin:district" # Replace admin with the user and district with the password
```
The authentication requires an account that is also configured on DHIS2 with admin privileges.

Install the application using docker:
```sh
docker build --build-arg USER_PASS="user:pass" . # See below on authentication.
docker run -p 8083:8083 -d <image hash>
```
Or, the installation may be done manually and requires [Node.js](https://nodejs.org/en/download/) v4 or greater.
```sh
# First configure the
npm install # install dependencies
npm start
```

The run the app on port 8083 by default. Can check if the up return the bundle of related DHIS2 tracked entities with
```sh
curl http://localhost:8083/trackedentities
```
To open the UI for file uploading and csv template downloading, enter the following address in the browser
```sh
http://localhost:8083/uploadfile
```


### FHIR Mediator

The OpenHIM FHIR mediator is a Java application. To use the mediator, you have to install [OpenHIM](http://openhim.readthedocs.io/en/latest/about.html) Console, API Server and MongoDB first.

The mediator requires openjdk7 or greater and Maven. If you experiencing the SunCertPathBuilderException error. Follow the instruction on the link > http://openhim.readthedocs.io/en/latest/tutorial/3-creating-a-passthrough-mediator.html#suncertpathbuilderexception-unable-to-find-valid-certification-path-to-requested-target

```sh
cd  openhim-mediator-hapifhir
mvn install
java -jar target/fhir-mediator-0.1.0-jar-with-dependencies.jar
```

If you are attempting to start the Java mediator and you are experiencing a SunCertPathBuilderException error then you will need to follow the [instructions](http://openhim.readthedocs.io/en/latest/tutorial/3-creating-a-passthrough-mediator.html#suncertpathbuilderexception-unable-to-find-valid-certification-path-to-requested-target) to install the self-signed certificate.

Then navigate to the Mediators section on OpenHIM to see your Mediator and check in the client section if the client "tut" is created. If not create the client [manualy](http://openhim.readthedocs.io/en/latest/tutorial/2-creating-a-channel.html)



### HAPI-FHIR JPA server

The [HAPI-FHIR JPA server](http://hapifhir.io/doc_jpa.html) is a RESTful server implementation that can use the Java Persistence API (JPA) as an abstraction to allow for HAPI to connect to your preferred database.

```sh
cd hapi-fhir-jpaserver-local
mvn install
mvn jetty:run
```
If the build failed, check the "hapi-fhir-base" corresponding fhir version [here](https://mvnrepository.com/artifact/ca.uhn.hapi.fhir/hapi-fhir-base) and then change the version in the pom.xml file.

```sh
<parent>
	<groupId>ca.uhn.hapi.fhir</groupId>
	<artifactId>hapi-fhir</artifactId>
	<version>XXXXX</version>
	<relativePath>../pom.xml</relativePath>
</parent>
```

The Fhir local server can be accessed on http://localhost:8084/hapi-fhir-jpaserver-local (localhost: can be replaced by a public IP address)

### Synchronization

To run the synchronization of data, perform the following action in the defined order:
1. run the synchronization of data with the Hapi Server to ensure that all IDSR lab data are in the Fhir Server
2. process all uploaded file and push the extracted Fhir resource in the Hapi Server as Bundle of type collection
3. check in there is collection in pending status and process them to sync data to the IDSR. The redo again a.

Here is a cron tab that you can create to perform these actions:
```sh
 */5 * * * * curl -k -u tut:tut https://localhost:5000/entitytrackers/1
*/7 * * * * curl http://localhost:8083/csv2fhir
*/8 * * * * curl -k -u tut:tut https://localhost:5000/pushfhir/1
 ```

Replace localhost by the IP of the server and the time to the estimation that may fit
Once the apps are running, ones could monitor the operation activities with this command
```sh
 tail -f ~/fhirmediator/fhirmediator.log
```


## Advanced Configuration


### Configuration of dhis2-fhir convertion.

A mapping should be done between FHIR based resources and tracked entities in DHIS2. Except for the Organization unit.
The dhis2-fhir convertion is running on the port 8083.

The file maniferst.webapp also contains FHIR data model template to be filled by the corresponding entity tracker attributes and dataelements. One needs first to specify the model and stage/Event used to register cases and related laboratory information.

Go in DHIS2 indentify program ID and stages ID involved in the Lab registration. Then open the manifest.webapp to configure this information. We can add as many stages as they are involved.

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

Another challenge is that more than one practitioner can be involved on the laboratory management process. There is no way for the app to categorized the role of practitioner at any stage of the program. The main role in the laboratory management is the care provider who requested the lab exam, the provider who collects the specimen and the provider who performs the examination. The roles are hard coded in like this: care_provider, specimen_collector,observation_performer
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

### Configuration of the FHIR-mediator

The first step is to allow the mediator to read the configuration file. The step are to create the user that has the right to read the config file:
```sh
useradd -d /home/fhirmediator -m fhirmediator -s /bin/bash
cd /home/ fhirmediator
mkdir config
 ```

Copy the configuration file from the source the the config folder and give all right to the user fhirmediator
```sh
cp  /thelocationofyoursourcecode/fhirmediator.properties  /home/fhirmediator/config/
sudo usermod -a -G youruser fhirmediator
sudo chown -R fhirmediator:yourusergroup /home/fhirmediator/config/
sudo chmod +r /home/fhirmediator/config/fhirmediator.properties
 ```

Change the source of data and the destination.
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

The second step is to configure the connection with the openHIM core:
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


### Configuration of the HAPI JPA Server

To make the JPA server working, one needs to create a new mysql database and change the configuration files of JPA local server.
To install mysql go to "https://doc.ubuntu-fr.org/mysql". Create a database named "dhis2_fhir".
The HAPI JPA Server can also be configured with PostgresSQL database.
Create a postgres database and a role. Then assign the database to the role.
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

The JPA server will run on the port 8084 but one may change this value. Open the pom.xml file and locate the tag

```sh
<httpConnector>
<port>8084</port>
</httpConnector>
```

The FHIR Lab apps is utilizing the HL7 FHIR standard to represent IDSR data to ensure the sharing of  data between the IDSR, laboratory facilities and other systems that could need to have access to the laboratory data as well as patient demographic and disease specific information.

The case is represented by the patient demographic information, disease to follow up and the symptoms description and is recorded in the IDSR in a specific programs. Laboratory data are registered for the specific case as events. The laboratory data comes from the facilities and contains information on providers (Organization and care provider), specimen handing, laboratory order, laboratory observation and test performed.

That information is collected by facilities and submitted to a central IDSR which is a DHIS2 based system. To submit the laboratory data, the facilities should be able to have information from the IDSR in the standardized format to allow them to reference case and related laboratory data in their own system or procedure, in order to share (pull or push) data without caring of the  data structure of the client or the IDRS service providers.

That is why the app architure is composed of the Fhir conversion module that generates HL7 Fhir resource from the IDSR data and  csv based Lab data. Then the generated Fhir resource are stored in a Fhir repository which provides the appropriate interface for search and CRUD operation. Then, all the requests could be made to the  Fhir repository to get formalized information on Lab request, specimen handling information, status of the results, disease diagnosed, etc.
