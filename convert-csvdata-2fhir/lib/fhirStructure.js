var Period={
  // from Element: extension
  "start" : "", // C? Starting time with inclusive boundary
  "end" : "" // C? End time with inclusive boundary, if not ongoing
};
var Identifier ={
  // from Element: extension
  "use" : "", // usual | official | temp | secondary (If known)
  "type" : { }, // Description of identifier
  "system" : "", // The namespace for the identifier
  "value" : "", // The value that is unique
  "period" : { Period }, // Time period when id is/was valid for use
  "assigner" : { } // Organization that issued id (may be just text)
};
var ContactPoint={
  "resourceType" : "ContactPoint",
  // from Element: extension
  "system" : "", // C? phone | fax | email | pager | other
  "value" : "", // The actual contact point details
  "use" : "", // home | work | temp | old | mobile - purpose of this contact point
  "rank" : "", // Specify preferred order of use (1 = highest)
  "period" : { Period } // Time period when the contact point was/is in use
};
var Address={
  "resourceType" : "Address",
  // from Element: extension
  "use" : "", // home | work | temp | old - purpose of this address
  "type" : "", // postal | physical | both
  "text" : "", // Text representation of the address
  "line" : [], // Street name, number, direction & P.O. Box etc.
  "city" : "", // Name of city, town etc.
  "district" : "", // District name (aka county)
  "state" : "", // Sub-unit of country (abbreviations ok)
  "postalCode" : "", // Postal code for area
  "country" : "", // Country (can be ISO 3166 3 letter code)
  "period" : { Period } // Time period when address was/is in use
};
var Coding={
  // from Element: extension
  "system" : "", // Identity of the terminology system
  "version" : "", // Version of the system - if relevant
  "code" : "", // Symbol in syntax defined by the system
  "display" : "", // Representation defined by the system
  "userSelected" : true //Boolean If this coding was chosen directly by the user
}
var CodeableConcept={
  // from Element: extension
  "coding" : [{ Coding }], // Code defined by a terminology system
  "text" : "" // Plain text representation of the concept
};
var  HumanName={
  "resourceType" : "HumanName",
  // from Element: extension
  "use" : "", // usual | official | temp | nickname | anonymous | old | maiden
  "text" : "", // Text representation of the full name
  "family" : [], // Family name (often called 'Surname')
  "given" : [], // Given names (not always 'first'). Includes middle names
  "prefix" : [], // Parts that come before the name
  "suffix" : [], // Parts that come after the name
  "period" : { Period } // Time period when name was/is in use
};
var ContactOrganization={ // Contact for the organization for a certain purpose
    "purpose" : { CodeableConcept }, // The type of contact
    "name" : { HumanName }, // A name associated with the contact
    "telecom" : [], //{ ContactPoint } Contact details (telephone, email, etc.)  for a contact
    "address" : { Address } // Visiting or postal addresses for the contact
  };
var Organization={
  "resourceType" : "Organization",
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "id":"",
  "meta":{"versionId":"","lastUpdated":""},
  "identifier" : [], // { Identifier }C? Identifies this organization  across multiple systems
  "active" : true, // Whether the organization's record is still in active use
  "type" : {CodeableConcept}, // Kind of organization
  "name" : "", // C? Name used for the organization
  "telecom" : [], // { ContactPoint }C? A contact detail for the organization
  "address" : [], //{ Address } C? An address for the organization
  "partOf" : "", //{Reference(Organization)} The organization of which this organization forms a part
  "contact" : []//contact for the organization for a certain purpose
};

var Patient={
  "resourceType" : "Patient",
  "id":"",
  "meta":{"versionId":"","lastUpdated":""},
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "identifier" : [], //{ Identifier } An identifier for this patient
  "active" : true, // Whether this patient's record is in active use
  "name" : [], //{ HumanName } A name associated with the patient
  "telecom" : [], //{ ContactPoint } A contact detail for the individual
  "gender" : "", // male | female | other | unknown
  "birthDate" : "", // The date of birth for the individual
  // deceased[x]: Indicates if the individual is deceased or not. One of these 2:
  "deceasedBoolean" : false,
  "deceasedDateTime" : "",
  "address" : [], //{ Address } Addresses for the individual
  "maritalStatus" : { CodeableConcept }, // Marital (civil) status of a patient
  // multipleBirth[x]: Whether patient is part of a multiple birth. One of these 2:
  "multipleBirthBoolean" : false,
  "multipleBirthInteger" : 0,
  "contact" : [],//{"relationship" : [{ CodeableConcept }],"name" : { HumanName },"telecom" : [{ ContactPoint }], "address" : { Address },
  //"gender" : "<code>", "organization" : { Reference(Organization) },"period" : { Period }}]
  //// The kind of relationship,A name associated with the contact person, A contact detail for the person,Address for the contact person,male | female | other | unknown, Organization that is associated with the contact
  //
   "communication" : [],//[{ "language" : { CodeableConcept },"preferred" : <boolean>}]
   //The language which can be used to communicate with the patient about his or her health ,Language preference indicator
   "careProvider" :[], // { Reference(Organization|Practitioner) } Patient's nominated primary care provider
   "managingOrganization" : "",// Organization that is the custodian of the patient record
   "link" : []//// Link to another patient resource that concerns the same actual person,
   //  [ "other" : { Reference(Patient) }, "type" : "<code>"], // R!  The other patient resource that the link refers to,replace | refer | seealso - type of link
};

var Practitioner={
  "resourceType" : "Practitioner",
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "id":"",
  "meta":{"versionId":"","lastUpdated":""},
  "identifier" : [], // A identifier for the person as this agent
  "active" : true, // Whether this practitioner's record is in active use
  "name" : {},//HumanName A name associated with the person
  "telecom" : [], //{ ContactPoint } A contact detail for the practitioner
  "address" : [], //{ Address } Where practitioner can be found/visited
  "gender" : "", // male | female | other | unknown
  "birthDate" : "", // The date  on which the practitioner was born
  "practitionerRole" :[],
  //// Roles/organizations the practitioner is associated with
  //[{"managingOrganization" : { Reference(Organization) },"role" : { CodeableConcept },"specialty" : [{ CodeableConcept }],"period" : { Period },"period" : { Period },"location" : [{ Reference(Location) }],"healthcareService" : [{ Reference(HealthcareService) }] }]
  // Organization where the roles are performed, Roles which this practitioner may perform,Specific specialty of the practitioner,The period during which the practitioner is authorized to perform in these role(s),he location(s) at which this practitioner provides care,he list of healthcare services that this worker provides for this role's Organization/Location(s)
  "qualification" : [],
  //Qualifications obtained by training and certification
  //[{"identifier" : [{ Identifier }],"code" : { CodeableConcept }, "period" : { Period },"issuer" : { Reference(Organization) }}]
  //An identifier for this qualification for the practitioner,R!  Coded representation of the qualification,Period during which the qualification is valid,Organization that regulates and issues the qualification
  "communication" : [] //{ CodeableConcept } A language the practitioner is able to use in patient communication 
};
var Quantity={
  // from Element: extension
  "value" : 0, // Numerical value (with implicit precision)
  "comparator" : "", // < | <= | >= | > - how to understand the value
  "unit" : "", // Unit representation
  "system" : "", // C? System that defines coded unit form
  "code" : "" // Coded form of the unit
};
var Collection={ // Collection details
    "collector" :"", // { Reference(Practitioner) } Who collected the specimen
    "comment" : [], //"<string>" Collector comments
    "collectedDateTime" : "",//<dateTime>
    "collectedPeriod" : { Period },
    //"quantity" : { "value":"","unit":"","code":"","system":""}, //Quantity(SimpleQuantity) The quantity of specimen collected
    "quantity":{Quantity},
    "method" : { CodeableConcept }, // Technique used to perform collection
    "bodySite" : { CodeableConcept } // Anatomical collection site
  };
 var Container={
	 "identifier" : [{ Identifier }],
	 "description" : "",
	 "type" : { CodeableConcept },
	 "capacity" : { Quantity},
	 "specimenQuantity" : { Quantity},
	 "additiveCodeableConcept" : { CodeableConcept },
	 "additiveReference" : {}
	 };
var Specimen={
  "resourceType" : "Specimen",
  "id":"",
  "meta":{"versionId":"","lastUpdated":""},
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "identifier" : [], //{ Identifier } External Identifier
  "status" : "", // available | unavailable | unsatisfactory | entered-in-error
  "type" : { CodeableConcept }, // Kind of material that forms the specimen
  "parent" : [], //{ Reference(Specimen) } Specimen from which this specimen originated
  "subject" : "", //**Reference(Patient|Group|Device|Substance) R!  Where the specimen came from. This may be from the patient(s) or from the environment or a device
  "accession" : {}, // Identifier, Identifier assigned by the lab
  "receivedTime" : "", // The time when specimen was received for processing
  "collection" : {}, // Collection details,
  //{"collector" : { Reference(Practitioner) },"comment" : ["<string>"],"collectedDateTime" : "<dateTime>","collectedPeriod" : { Period },"quantity" : { Quantity(SimpleQuantity) },"method" : { CodeableConcept },"bodySite" : { CodeableConcept }}
  //// Who collected the specimen, Collector comments,The quantity of specimen collected, Technique used to perform collection,Anatomical collection site
  "treatment" : [], // Treatment and processing step details,
  //[{"description" : "<string>","procedure" : { CodeableConcept },"additive" : [{ Reference(Substance) }]
  //Textual description of procedure,Indicates the treatment or processing step  applied to the specimen,Material used in the processing step
  "container" : [] // Direct container of specimen (tube/slide, etc.)
  //[{"identifier" : [{ Identifier }], "description" : "<string>","type" : { CodeableConcept },"capacity" : { Quantity(SimpleQuantity) }, "specimenQuantity" : { Quantity(SimpleQuantity) },"additiveCodeableConcept" : { CodeableConcept },"additiveReference" : { Reference(Substance) }
  //// Id for the container,Textual description of the container,Kind of container directly associated with specimen,Container volume or size,Quantity of specimen within container
};
var OrderEvent={//A list of events of interest in the lifecycle
	"status" : "", // R!  proposed | draft | planned | requested | received |...
	"description" : { CodeableConcept }, // More information about the event and its context
	"dateTime" : "", // R!  The date at which the event happened
	"actor" : {} // "actor" : { Reference(Practitioner|Device) } // Who recorded or did this Who recorded or did this
	};
var DiagnosticOrder={
  "resourceType" : "DiagnosticOrder",
  "id":"",
  "meta":{"versionId":"","lastUpdated":""},
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "subject" : "", // { Reference(Patient|Group|Location|Device) } R!  Who and/or what test is about
  "orderer" : "", // { Reference(Practitioner) } Who ordered the test
  "identifier" : [], // { Reference(Practitioner) } Identifiers assigned to this order
  "encounter" :"" , // { Reference(Encounter) } The encounter that this diagnostic order is associated with
  "reason" : [], // { CodeableConcept } Explanation/Justification for test
  "supportingInformation" : "", //[ { Reference(Observation|Condition|DocumentReference) }]Additional clinical information
  "specimen" : [], // { Reference(Specimen) } If the whole order relates to specific specimens
  "status" : "", //<code> proposed | draft | planned | requested | received | accepted | in-progress | review | completed | cancelled | suspended | rejected | failed
  "priority" : "", //<code> routine | urgent | stat | asap
  "event" : [], // A list of events of interest in the lifecycle
  //[{"status" : "<code>","description" : { CodeableConcept },"dateTime" : "<dateTime>","actor" : { Reference(Practitioner|Device) } }] 
  //// **R!  proposed | draft | planned | requested |...,More information about the event and its context, **The date at which the event happened, Who recorded or did this
  "item" : [],// The items the orderer requested
  //[{"code" : { CodeableConcept },"specimen" : [{ Reference(Specimen) }],"bodySite" : { CodeableConcept },"status" : "<code>","event" : [{ Content as for DiagnosticOrder.event }]}]
  //R!  Code to indicate the item (test or panel) being ordered,If this item relates to specific specimens,Location of requested test (if applicable),proposed | draft | planned | requested ...,Events specific to this item
  "note" : [] // { Annotation } Other notes and comments
};
var Range={
  // from Element: extension
  "low" : { Quantity}, // C? Low limit
  "high" : { Quantity} // C? High limit
};
var Ratio={
  // from Element: extension
  "numerator" : { Quantity }, // Numerator value
  "denominator" : { Quantity } // Denominator value
};
var SampledData={
  // from Element: extension
  "origin" : {Quantity}, // R!  Zero value and units
  "period" : 0, // <decimal>R!  Number of milliseconds between samples
  "factor" : 0, // <decimal> Multiply data by this before adding to origin
  "lowerLimit" : 0, //<decimal> Lower limit of detection
  "upperLimit" : 0, //<decimal> Upper limit of detection
  "dimensions" : "", //<positiveInt> R!  Number of sample points at each time point
  "data" : "" // R! <string> Decimal values with spaces, or "E" | "U" | "L"
};

var Observation={
  "resourceType" : "Observation",
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "id":"",
  "meta":{"versionId":"","lastUpdated":""},
  "identifier" : [], //{ Identifier } Unique Id for this particular observation
  "status" : "", // R!  registered | preliminary | final | amended +
  "category" : { CodeableConcept }, // Classification of  type of observation
  "code" : { CodeableConcept }, // R!  Type of observation (code / type)
  "subject" :"", // { Reference(Patient|Group|Device|Location) } Who and/or what this is about
  "encounter" : "", //{ Reference(Patient|Group|Device|Location) } Healthcare event during which this observation is made
  // effective[x]: Clinically relevant time/time-period for observation. One of these 2:
  "effectiveDateTime" : "",
  "effectivePeriod" : {},//Period
  "issued" : "", // DateTime this was made available
  "performer" : [], //{ Reference(Practitioner|Organization|Patient|RelatedPerson) } Who is responsible for the observation
  // value[x]: Actual result. One of these 10:
  "valueQuantity" : {},//Quantity
  "valueCodeableConcept" : { CodeableConcept },
  "valueString" : "",//<string>
  "valueRange" : {  },//Range
  "valueRatio" : { Ratio },
  "valueSampledData" : {},
  "valueTime" : "",//<time>
  "valueDateTime" : "",//<dateTime>
  "valuePeriod" : {  },//Period
  "dataAbsentReason" : { CodeableConcept }, // C? Why the result is missing
  "interpretation" : "", //{CodeableConcept} High, low, normal, etc.
  "comments" : "", //<string> Comments about result
  "bodySite" : {  }, //CodeableConcept Observed body part
  "method" : { CodeableConcept }, // How it was done
  "specimen" :{}, // { Reference(Specimen) } Specimen used for this observation
  "device" : {}, //{ Reference(Device|DeviceMetric) } (Measurement) Device
  "referenceRange" : [],
  //[{"low" : { Quantity(SimpleQuantity) },"high" : { Quantity(SimpleQuantity) },"meaning" : { CodeableConcept }, "age" : { Range },"text" : "<string>"}]
  //C? Low Range, if relevant,C? High Range, if relevant,Indicates the meaning/use of this range of this range,Applicable age range, if relevant,Text based reference range in an observation
  "related" : [],
  //[{"type" : "<code>","target" : { Reference(Observation|QuestionnaireResponse) }}]
  //// has-member | derived-from | sequel-to | replaces | qualified-by | interfered-by, R!  Resource that is related to this one
  "component" : []// Component results
  //"code" : { CodeableConcept },"valueQuantity" : { Quantity },"valueCodeableConcept" : { CodeableConcept },"valueString" : "<string>","valueRange" : { Range },"valueRatio" : { Ratio },"valueSampledData" : { SampledData },"valueAttachment" : { Attachment },
  //"valueTime" : "<time>","valueDateTime" : "<dateTime>","valuePeriod" : { Period },"dataAbsentReason" : { CodeableConcept },"referenceRange" : [{ Content as for Observation.referenceRange }]
  //
};
var DiagnosticReport={
  "resourceType" : "DiagnosticReport",
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "id":"",
  "meta":{"versionId":"","lastUpdated":""},
  "identifier" : [], //{ Identifier } Id for external references to this report
  "status" : "", // <code>R!  registered | partial | final | corrected | appended | cancelled | entered-in-error
  "category" : { CodeableConcept }, // Service category
  "code" : { CodeableConcept }, // R!  Name/Code for this diagnostic report
  "subject" :"", //{ Reference(Patient|Group|Device|Location) } R!  The subject of the report, usually, but not always, the patient
  "encounter" :"", //{ Reference(Encounter) } Health care event when test ordered
  // effective[x]: Clinically Relevant time/time-period for report. One of these 2:
  "effectiveDateTime" : "",//<dateTime>
  "effectivePeriod" : "",//{Period}
  "issued" : "", //<instant> R!  DateTime this version was released
  "performer" :"", //{ Reference(Practitioner|Organization) } R!  Responsible Diagnostic Service
  "request" : [], //{ Reference(DiagnosticOrder|ProcedureRequest|ReferralRequest) } What was requested
  "specimen" : [], // { Reference(Specimen) } Specimens this report is based on
  "result" : [], //{ Reference(Observation) } Observations - simple, or complex nested groups
  "imagingStudy" : [], //{ Reference(ImagingStudy|ImagingObjectSelection) } Reference to full details of imaging associated with the diagnostic report
  "image" : [],
  //[{"comment" : "<string>","link" : { Reference(Media) }}]
  //Comment about the image (e.g. explanation),Reference to the image source
  "conclusion" : "", //<string> Clinical Interpretation of test results
  "codedDiagnosis" : [], //{ CodeableConcept } Codes for the conclusion
  "presentedForm" : [] // { Attachment } Entire report as issued
};


var Condition={
  "resourceType" : "Condition",
  // from Resource: id, meta, implicitRules, and language
  // from DomainResource: text, contained, extension, and modifierExtension
  "id":"",
  "identifier" : [], //{ Identifier } External Ids for this condition
  "patient" :"", //{ Reference(Patient) } R!  Who has the condition?
  "encounter" :"", // { Reference(Encounter) } Encounter when condition first asserted
  "asserter" : "", //{ Reference(Practitioner|Patient) } Person who asserts this condition
  "dateRecorded" : "", //<date> When first entered
  "code" :"", // R!  { CodeableConcept } Identification of the condition, problem or diagnosis
  "category" : "", //{ CodeableConcept } complaint | symptom | finding | diagnosis
  "clinicalStatus" : "", //<code> active | relapse | remission | resolved
  "verificationStatus" : "", //<code> R!  provisional | differential | confirmed | refuted | entered-in-error | unknown
  "severity" : "", //{ CodeableConcept } Subjective severity of condition
  // onset[x]: Estimated or actual date,  date-time, or age. One of these 5:
  "onsetDateTime" : "",//<dateTime>
  "onsetQuantity" : "",//{ Quantity(Age) }
  "onsetPeriod" : "",//{ Period }
  "onsetRange" : "",//{ Range }
  "onsetString" : "",//<string>
  // abatement[x]: If/when in resolution/remission. One of these 6:
  "abatementDateTime" : "",//<dateTime>
  "abatementQuantity" : "",//{ Quantity(Age) }
  "abatementBoolean" : "",//<boolean>
  "abatementPeriod" : "",//{ Period }
  "abatementRange" : "",//{ Range }
  "abatementString" : "",//<string>
  "stage" :"",  // Stage/grade, usually assessed formally
  /*
    {"summary" : { CodeableConcept }, // C? Simple summary (disease specific)
    "assessment" : [{ Reference(ClinicalImpression|DiagnosticReport|Observation) }] // C? Formal record of assessment
	},
	* */
  "evidence" : "",
  /*
	  [{ // Supporting evidence
		"code" : { CodeableConcept }, // C? Manifestation/symptom
		"detail" : [{ Reference(Any) }] // C? Supporting information found elsewhere
	  }],
	**/
  "bodySite" :"" , //[{ CodeableConcept }] Anatomical location, if relevant
  "notes" : "" // <string> Additional information about the Condition
};


var Entry={ // Entry in the bundle - will have a resource, or information
    "link" : [], //{ Content as for Bundle.link } Links related to this entry
    "fullUrl" : "", //<uri> Absolute URL for resource (server address, or UUID/OID)
    "resource" : { }, // Resource A resource in the bundle
    "search" : { // C? Search related information
      "mode" : "", //<code> match | include | outcome - why this is in the result set
      "score" :0 //<decimal> Search ranking (between 0 and 1)
    },
    "request" : { // C? Transaction Related Information
      "method" : "", // <code>R!  GET | POST | PUT | DELETE
      "url" : "", //<uri> R!  URL for HTTP equivalent of this entry
      "ifNoneMatch" : "", //<string> For managing cache currency
      "ifModifiedSince" : "", //<instant> For managing update contention
      "ifMatch" : "", // <string> For managing update contention
      "ifNoneExist" : "" //<string> For conditional creates
    },
    "response" : { // C? Transaction Related Information
      "status" : "", //<string> R!  Status return code for entry
      "location" : "", //<uri> The location, if the operation returns a location
      "etag" : "", //<string> The etag for the resource (if relevant)
      "lastModified" : "" //<instant> Server's date time modified
    }
  };
var Bundle={
  "resourceType" : "Bundle",
  // from Resource: id, meta, implicitRules, and language
  "id":"",
  "meta":{"versionId":"","lastUpdated":""},
  "type" : "", // R!  document | message | transaction | transaction-response | batch | batch-response | history | searchset | collection
  "total" :0 , //"<unsignedInt>" C? If search, the total number of matches
  "link" : [], // Links related to this Bundle,
  //[{ "relation" : "<string>","url" : "<uri>"}]
  //// R!  http://www.iana.org/assignments/link-relations/link-relations.xhtml,R!  Reference details for the link
  "entry" : [], // Entry in the bundle - will have a resource, or information,
  "signature" : { } // Signature Digital Signature
}

exports.Identifier=Identifier;
exports.ContactPoint=ContactPoint;
exports.Address=Address;
exports.Organization=Organization;
exports.CodeableConcept=CodeableConcept;
exports.Patient=Patient;
exports.HumanName=HumanName;
exports.Practitioner=Practitioner;
exports.Specimen=Specimen;
exports.OrderEvent=OrderEvent;
exports.Collection=Collection;
exports.Container=Container;
exports.DiagnosticOrder=DiagnosticOrder;
exports.Observation=Observation;
exports.SampledData=SampledData;
exports.Quantity=Quantity;
exports.Period=Period;
exports.Range=Range;
exports.Ratio=Ratio;
exports.DiagnosticReport=DiagnosticReport;
exports.Condition=Condition;
exports.Bundle=Bundle;
exports.Entry=Entry;
