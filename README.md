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
```
Go to the dhis2 ->apps-> maintenance -> program ->tracked entity. Identify the entity used by Programs to track Patient,Provider,Organization,Specimen,Observation,Diagnostic Order and Diagnostic Report, get their ids (in the show details context menu) and map in the following section of manifest.webapp.
```sh
"entities_mapping":{
	"patient":"fZZDtGHhXT3", # Replace all ids by their correspondants, tracked entity ID in dhis2
	"provider":"WO3xOdPaJfL",#if the entity is not concerned used "000000" as entity ID and increment by 1 if there is others non tracked entities
	"specimen":"IJooTQOZbfy",
	"order":"mob9DySuMOS",
	"observation":"KEc7eJXhZw3",
	"diagnosticReport":"Onw0t8l5Dyp"
  }
```
The ones need to map now entity tracker attributes.  Go to dhis2 ->apps-> Programs/Attribute. This is the place where attributes are linked to the entities. Then identify the program used to track one of the entity. Click on the ProgramName -> Edit -> (Check if the tracke entity field is the one we want) -> Go in the Available Attributes Section and -> Select attributes. Use the attributes to map them with their correspondance in FHIR. The mapping is done in the manifest.webapp file, by changing attributes. if An attribute is not tracked leave the default name. But ensure only that there not name duplication.
P. exemple the mapping of attributes for the Practitioner Entity is "
```sh
"practitioner_attribute_mapping":
  {
	"id":"trackedEntityInstance",
	"identifier":"Identifier",
	"name_family":"LastName",
	"name_given":"FirstName",
	"gender":"Sex",
	"telecom_phone":"Telephone",
	"telecom_email":"Email",
	"address":"Residence"
  }
```
Enter authentication by specifying the username:password. 
```sh
 "authentication":"admin:district" # Replace admin with the user and district with the password
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
cp  /thelocationofyoursourcecode/fhirmediator.properties  /home/mediator/config/
sudo usermod -a -G youruser fhirmediator
sudo chown -R fhirmediator:yourusergroup /home/fhirmediator/config/
 ```
- Change the source of data and the destination.
```sh 
sudo gedit /home/mediator/config/fhirmediator.properties
```
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
3. Configuration of the HAPI JPA Server
To make the JPA server working ones need to create a new mysql database and change the configuration files of JPA local server. 
To install mysql go to "https://doc.ubuntu-fr.org/mysql"
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
npm start
```
the run the app on port 8083 by default. Can check if the up return the bundle of related DHIS2 tracked entities with
```sh
curl http://localhost:8083/trackedentities
```
Prerequisites: Install nodejs, npm
2. FHIR-mediator
```sh
cd  /thelocationofyoursourcecode/openhim-mediator-hapifhir
mvn install
java -jar target/fhir-mediator-0.1.0-jar-with-dependencies.jar
```
- Prerequisites: 

To install the mediator, you have to install OpenHIM
OpenHIM stands for the Open Health Information Mediator. The OpenHIM is an interoperability layer: a software component that eases integration between disparate information systems by connecting client and infrastructure components together. Its role is to provide a facade to client systems - providing a single point-of-control for defining web service APIs and adapting and orchestrating requests between infrastructure services. The OpenHIM also provides security, client management and transaction monitoring. 
> http://openhim.readthedocs.io/en/latest/about.html)
To install OpenHIM go to  
> http://openhim.readthedocs.io/en/latest/getting-started.html)

Install openjdk7 at least. If you experiencing the SunCertPathBuilderException error. Follow the instruction on the link > http://openhim.readthedocs.io/en/latest/tutorial/3-creating-a-passthrough-mediator.html#suncertpathbuilderexception-unable-to-find-valid-certification-path-to-requested-target

3. HAPI JPA server

``sh
cd /thelocationofyoursourcecode /hapi-fhir-jpaserver-local
mvn install
mvn jetty:run
```
Then, the Fhir local server can be accessed on http://localhost:8084/hapi-fhir-jpaserver-local (localhost: can be replaced by a public IP address)
> http://hapifhir.io/doc_jpa.html

#Launch the synchronization
To run the synchronization of data, run the command
```sh
 curl -k -u tut:tut https://localhost:5000/entitytrackers/1
 ```
Replace localhost by the IP of the server.

