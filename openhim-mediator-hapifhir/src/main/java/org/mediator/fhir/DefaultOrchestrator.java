package org.mediator.fhir;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ca.uhn.fhir.model.dstu2.resource.*;
import org.apache.http.HttpStatus;
//import org.hl7.fhir.dstu3.model.Identifier;
//import org.hl7.fhir.dstu3.model.Practitioner;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;

import java.text.SimpleDateFormat;
import java.util.*;

public class DefaultOrchestrator extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    String logFileName;
    private final MediatorConfig config;
    private MediatorFhirConfig mediatorConfiguration;
    private SynDateConfigFile syncConfiguration;
    private MediatorHTTPRequest originalRequest;
    private String solvedPractitionerResponse;
    private List<String> listSolvedPractitionerResponse;
    private List<String> listSolvedPatientResponse;
    private List<String> listSolvedListResourceResponse;
    private List<String> listSolvedBasicResponse;
    private List<String> listSolvedOrganizationResponse;
    private List<String> listSolvedSpecimenResponse;
    private List<String> listSolvedConditionResponse;
    private List<String> listSolvedDiagnosticOrderResponse;
    private List<String> listSolvedObservationResponse;
    private List<String> listSolvedDiagnosticReportResponse;
    private List<Practitioner> listOfValidPractitioner;
    private List<Patient> listOfValidPatient;
    private List<Specimen> listOfValidSpecimen;
    private List<Organization> listOfValidOrganization;
    private List<DiagnosticOrder> listOfValidDiagnosticOrder;
    private List<Condition> listOfValidCondition;
    private List<ListResource> listOfValidListResource;
    private List<Basic> listOfValidBasic;
    private List<Observation> listOfValidObservation;
    private List<DiagnosticReport> listOfValidDiagnosticReport;
    private List<Practitioner> listOfPractitionerToUpdate;
    private List<Patient> listOfPatientToUpdate;
    private List<ListResource> listOfListResourceToUpdate;
    private List<Basic> listOfBasicToUpdate;
    private List<Organization> listOfOrganizationToUpdate;
    private List<Specimen> listOfSpecimenToUpdate;
    private List<Condition> listOfConditionToUpdate;
    private List<DiagnosticOrder> listOfDiagnosticOrderToUpdate;
    private List<Observation> listOfObservationToUpdate;
    private List<DiagnosticReport> listOfDiagnosticReportToUpdate;
    private List<Practitioner> listOfPractitionerToAdd;
    private List<Patient> listOfPatientToAdd;
    private List<ListResource> listOfListResourceToAdd;
    private List<Basic> listOfBasicToAdd;
    private List<Organization> listOfOrganizationToAdd;
    private List<Specimen> listOfSpecimenToAdd;
    private List<Condition> listOfConditionToAdd;
    private List<DiagnosticOrder> listOfDiagnosticOrderToAdd;
    private List<Observation> listOfObservationToAdd;
    private List<DiagnosticReport> listOfDiagnosticReportToAdd;

    int nbrOfSearchRequestToWaitFor;
    String baseServerRepoURI;
    private List<Map<String,String>> listMapIdentifierUsedForUpdate;
    private List<String> listIdsPractitionerUsedForSearch;
    private List<String> listIdsPatientUsedForSearch;
    private List<String> listIdsOrganizationUsedForSearch;
    private List<String> listIdsSpecimenUsedForSearch;
    private List<String> listIdsConditionUsedForSearch;
    private List<String> listIdsListResourceUsedForSearch;
    private List<String> listIdsBasicUsedForSearch;
    private List<String> listIdsDiagnosticOrderUsedForSearch;
    private List<String> listIdsObservationUsedForSearch;
    private List<String> listIdsDiagnosticReportUsedForSearch;

    private List<Map<String,String>> listMapIdentifierdForUpdate;
    private Map<String,Practitioner> listIdentifiedPractitionerAndIdForUpdateSource;
    String resultOutPutHeader;
    String resultOutPutTail;
    String stringSyncDate;
    String logResult;
    enum operationTypeFlag {INSERT,UPDATE};

    FhirResourceValidator resourceBundle=null;
    boolean organizationProcessed=false;
    String responsePatient=null;
    String responsePractitioner=null;
    String responseOrganization=null;
    String responseSpecimen=null;
    String responseBasic=null;
    String responseListResource=null;
    String responseCondition=null;
    String responseDiagnosticOrder=null;
    String responseObservation=null;
    String responseDiagnosticReport=null;
    String nullResponse="{\n" +
            "  \"resourceType\": \"Bundle\",\n" +
            "  \"id\": \"a01c54cb-8794-491e-8100-5c8c737bbb49\",\n" +
            "  \"meta\": {\n" +
            "    \"lastUpdated\": \"2017-03-23T11:18:03.000+03:00\"\n" +
            "  },\n" +
            "  \"type\": \"searchset\",\n" +
            "  \"total\": 0,\n" +
            "  \"link\": [\n" +
            "    {\n" +
            "      \"relation\": \"self\",\n" +
            "      \"url\": \"http://localhost:8084/hapi-fhir-jpaserver-local/baseDstu2/Patient?_pretty=true&family=xxxxx\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    public DefaultOrchestrator(MediatorConfig config) {
        this.config = config;
        this.mediatorConfiguration=new MediatorFhirConfig();
        this.syncConfiguration=new SynDateConfigFile();
        this.resourceBundle=new FhirResourceValidator();
        baseServerRepoURI="";
        resultOutPutHeader="{outputId:Synchronization result,";
        resultOutPutTail="}";
        solvedPractitionerResponse=null;
        listSolvedPractitionerResponse=new ArrayList<>();
        listSolvedPatientResponse=new ArrayList<>();
        listSolvedListResourceResponse=new ArrayList<>();
        listSolvedOrganizationResponse=new ArrayList<>();
        listSolvedBasicResponse=new ArrayList<>();
        listSolvedSpecimenResponse=new ArrayList<>();
        listSolvedConditionResponse=new ArrayList<>();
        listSolvedDiagnosticOrderResponse=new ArrayList<>();
        listSolvedObservationResponse=new ArrayList<>();
        listSolvedDiagnosticReportResponse=new ArrayList<>();
        listOfValidPractitioner=new ArrayList<>();
        listOfValidPatient=new ArrayList<>();
        listOfValidOrganization=new ArrayList<>();
        listOfValidSpecimen=new ArrayList<>();
        listOfValidCondition=new ArrayList<>();
        listOfValidDiagnosticOrder=new ArrayList<>();
        listOfValidObservation=new ArrayList<>();
        listOfValidDiagnosticReport=new ArrayList<>();
        listOfPractitionerToUpdate=new ArrayList<>();
        listOfOrganizationToUpdate=new ArrayList<>();
        listOfPatientToUpdate=new ArrayList<>();
        listOfListResourceToUpdate=new ArrayList<>();
        listOfBasicToUpdate=new ArrayList<>();
        listOfSpecimenToUpdate=new ArrayList<>();
        listOfConditionToUpdate=new ArrayList<>();
        listOfDiagnosticOrderToUpdate=new ArrayList<>();
        listOfObservationToUpdate=new ArrayList<>();
        listOfDiagnosticReportToUpdate=new ArrayList<>();
        listOfPractitionerToAdd=new ArrayList<>();
        listOfPatientToAdd=new ArrayList<>();
        listOfListResourceToAdd=new ArrayList<>();
        listOfBasicToAdd=new ArrayList<>();
        listOfOrganizationToAdd=new ArrayList<>();
        listOfSpecimenToAdd=new ArrayList<>();
        listOfConditionToAdd=new ArrayList<>();
        listOfDiagnosticOrderToAdd=new ArrayList<>();
        listOfObservationToAdd=new ArrayList<>();
        listOfDiagnosticReportToAdd=new ArrayList<>();
        nbrOfSearchRequestToWaitFor=0;
        listIdsPractitionerUsedForSearch=new ArrayList<>();
        listIdsPatientUsedForSearch=new ArrayList<>();
        listIdsOrganizationUsedForSearch=new ArrayList<>();
        listIdsSpecimenUsedForSearch=new ArrayList<>();
        listIdsConditionUsedForSearch=new ArrayList<>();
        listIdsListResourceUsedForSearch=new ArrayList<>();
        listIdsBasicUsedForSearch=new ArrayList<>();
        listIdsDiagnosticOrderUsedForSearch=new ArrayList<>();
        listIdsObservationUsedForSearch=new ArrayList<>();
        listIdsDiagnosticReportUsedForSearch=new ArrayList<>();
        listMapIdentifierUsedForUpdate=new ArrayList<>();
        listMapIdentifierdForUpdate=new ArrayList<>();
        listIdentifiedPractitionerAndIdForUpdateSource=new HashMap<>();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logResult=simpleDateFormat.format(new Date()).toString()+"::";
        logFileName=this.mediatorConfiguration.getLogFile();
    }

    private void queryDHIS2FhirRepositoryResources(MediatorHTTPRequest request)
    {
        originalRequest = request;
        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        Map <String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        String resourceInformation="FHIR Trackers resources";
        log.info("Querying the DHIS2 tracker server");



        //builtRequestPath="/baseDstu3/Practitioner?_lastUpdated=>=2016-10-11T09:12:37&_lastUpdated=<=2016-10-13T09:12:45&_pretty=true";

        String ServerApp=mediatorConfiguration.getServerSourceAppName().equals("null")?null:mediatorConfiguration.getServerSourceAppName();

        baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                this.mediatorConfiguration.getSourceServerScheme(),
                this.mediatorConfiguration.getSourceServerURI(),
                this.mediatorConfiguration.getSourceServerPort(),
                ServerApp,
                this.mediatorConfiguration.getSourceServerFhirDataModel()
        );
        String uriRepServer=baseServerRepoURI;

        String encodedUriSourceServer=FhirMediatorUtilities.encodeUrlToHttpFormat(uriRepServer);
        MediatorHTTPRequest serviceRequest = new MediatorHTTPRequest(
                request.getRequestHandler(),
                getSelf(),
                resourceInformation,
                "GET",
                encodedUriSourceServer,
                null,
                headers,
                null);

        resultOutPutHeader+="requestDateTime:"+new Date().toString()+",";
        httpConnector.tell(serviceRequest, getSelf());

    }

    private void processFhirRepositoryServiceResponse(MediatorHTTPResponse response) {
        log.info("Received response Fhir repository Server");
        //originalRequest.getRespondTo().tell(response.toFinishRequest(), getSelf());
        //Perform the resource validation from the response
        try
        {
            if (response.getStatusCode() == HttpStatus.SC_OK) {
                StringBuilder strResponse=new StringBuilder();
                //Copy the response Char by char to avoid the string size limitation issues
                strResponse.append(response.getBody());
                resourceBundle.set_jsonResource(strResponse);
                String pathOfErrorResource=this.mediatorConfiguration.getPathForResourceWithError();
                //boolean resProcessing= resourceBundle.processBundleJsonResource(pathOfErrorResource);
                boolean resProcessing= resourceBundle.processBundleJsonResource(pathOfErrorResource,baseServerRepoURI);
                if(!resProcessing)
                {
                    throw new Exception("Failed to process the fhir synchronization request");
                }
                //Received Tracked entities

                int nbrRetreivedPractitioner=resourceBundle.getListOfPractitioners().size();
                int nbrRetreivedPatient=resourceBundle.getListOfPatient().size();
                int nbrRetreivedOrganization=resourceBundle.getListOfOrganization().size();
                int nbrRetreivedSpecimen=resourceBundle.getListOfSpecimen().size();
                int nbrRetreivedCondition=resourceBundle.getListOfCondition().size();
                int nbrRetreivedDiagnosticOrder=resourceBundle.getListOfDiagnosticOrder().size();
                int nbrRetreivedDiagnosticReport=resourceBundle.getListOfDiagnosticReport().size();
                int nbrRetreivedObservation=resourceBundle.getListOfObservation().size();
                int nbrRetreivedListResource=resourceBundle.getListOfListResource().size();
                int nbrRetreivedBasic=resourceBundle.getListOfBasic().size();

                //Valid tracked entities after the bundle processing
                int nbrValidPractitioner=resourceBundle.getListOfValidPractitioners().size();
                int nbrValidPatient=resourceBundle.getListOfValidPatient().size();
                int nbrValidOrganization=resourceBundle.getListOfValidOrganization().size();
                int nbrValidSpecimen=resourceBundle.getListOfValidSpecimen().size();
                int nbrValidCondition=resourceBundle.getListOfValidCondition().size();
                int nbrValidDiagnosticOrder=resourceBundle.getListOfValidDiagnosticOrder().size();
                int nbrValidDiagnosticReport=resourceBundle.getListOfValidDiagnosticReport().size();
                int nbrValidPObservation=resourceBundle.getListOfValidObservation().size();
                int nbrValidListResource=resourceBundle.getListOfValidListResource().size();
                int nbrValidListBasic=resourceBundle.getListOfValidBasic().size();

                //InValid tracked entities after the bundle processing
                int nbreInvalidPractitioner=resourceBundle.getListOfInvalidPractitioners().size();
                int nbreInvalidPatient=resourceBundle.getListOfInvalidPatient().size();
                int nbreInvalidOrganization=resourceBundle.getListOfInvalidOrganization().size();
                int nbreInvalidSpecimen=resourceBundle.getListOfInvalidSpecimen().size();
                int nbreInvalidCondition=resourceBundle.getListOfInvalidCondition().size();
                int nbreInvalidDiagnosticOrder=resourceBundle.getListOfInvalidDiagnosticOrder().size();
                int nbreInvalidDiagnosticReport=resourceBundle.getListOfInvalidDiagnosticReport().size();
                int nbreInvalidObservation=resourceBundle.getListOfInvalidObservation().size();
                int nbreInvalidListResource=resourceBundle.getListOfInvalidListResource().size();
                int nbreInvalidBasic=resourceBundle.getListOfInvalidBasic().size();

                //Log request initiation
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        "DHIS2 tracker resources request succeded","Notice");
                resultOutPutHeader+="totalPractitionerFound:"+nbrRetreivedPractitioner+","+
                        "totalNbreOfValidPractitioner:"+nbrValidPractitioner+","+
                        "totalNbreOfInvalidPractitioner:"+nbreInvalidPractitioner+","+
                        "totalPatientFound:"+nbrRetreivedPatient+","+
                        "totalNbreOfValidPatient:"+nbrValidPatient+","+
                        "totalNbreOfInvalidPatient:"+nbreInvalidPatient+","+
                        "totalOrganizationFound:"+nbrRetreivedOrganization+","+
                        "totalNbreOfValidOrganization:"+nbrValidOrganization+","+
                        "totalNbreOfInvalidOrganization:"+nbreInvalidOrganization+","+
                        "totalSpecimenFound:"+nbrRetreivedSpecimen+","+
                        "totalNbreOfValidSpecimen:"+nbrValidSpecimen+","+
                        "totalNbreOfInvalidSpecimen:"+nbreInvalidSpecimen+","+
                        "totalDiagnosticOrderFound:"+nbrRetreivedDiagnosticOrder+","+
                        "totalNbreOfValidDiagnosticOrder:"+nbrValidDiagnosticOrder+","+
                        "totalNbreOfInvalidDiagnosticOrder:"+nbreInvalidDiagnosticOrder+","+
                        "totalDiagnosticReportFound:"+nbrRetreivedDiagnosticReport+","+
                        "totalNbreOfValidDiagnosticReport:"+nbrValidDiagnosticReport+","+
                        "totalNbreOfInvalidDiagnosticReport:"+nbreInvalidDiagnosticReport+","+
                        "totalObservationFound:"+nbrRetreivedObservation+","+
                        "totalNbreOfValidObservation:"+nbrValidPObservation+","+
                        "totalNbreOfInvalidObservation:"+nbreInvalidObservation+",";



                logResult+="::[request resource]:";
                logResult+="totalPractitionerFound="+nbrRetreivedPractitioner+","+
                        "totalNbreOfValidPractitioner="+nbrValidPractitioner+","+
                        "totalNbreOfInvalidPractitioner="+nbreInvalidPractitioner+","+
                        "totalPatientFound:"+nbrRetreivedPatient+","+
                        "totalNbreOfValidPatient:"+nbrValidPatient+","+
                        "totalNbreOfInvalidPatient:"+nbreInvalidPatient+","+
                        "totalOrganizationFound:"+nbrRetreivedOrganization+","+
                        "totalNbreOfValidOrganization:"+nbrValidOrganization+","+
                        "totalNbreOfInvalidOrganization:"+nbreInvalidOrganization+","+
                        "totalSpecimenFound:"+nbrRetreivedSpecimen+","+
                        "totalNbreOfValidSpecimen:"+nbrValidSpecimen+","+
                        "totalNbreOfInvalidSpecimen:"+nbreInvalidSpecimen+","+
                        "totalDiagnosticOrderFound:"+nbrRetreivedDiagnosticOrder+","+
                        "totalNbreOfValidDiagnosticOrder:"+nbrValidDiagnosticOrder+","+
                        "totalNbreOfInvalidDiagnosticOrder:"+nbreInvalidDiagnosticOrder+","+
                        "totalDiagnosticReportFound:"+nbrRetreivedDiagnosticReport+","+
                        "totalNbreOfValidDiagnosticReport:"+nbrValidDiagnosticReport+","+
                        "totalNbreOfInvalidDiagnosticReport:"+nbreInvalidDiagnosticReport+","+
                        "totalObservationFound:"+nbrRetreivedObservation+","+
                        "totalNbreOfValidObservation:"+nbrValidPObservation+","+
                        "totalNbreOfInvalidObservation:"+nbreInvalidObservation+",";


                PractitionerOrchestratorActor.ResolvePractitionerRequest PractitionerRequest =null;
                PatientOrchestratorActor.ResolvePatientRequest patientRequest =null;
                OrganizationOrchestratorActor.ResolveOrganizationRequest organizationRequest=null;
                SpecimenOrchestratorActor.ResolveSpecimenRequest specimenRequest=null;
                ListResourceOrchestratorActor.ResolveListResourceRequest listResourceRequest=null;
                BasicOrchestratorActor.ResolveBasicRequest basicRequest=null;
                ConditionOrchestratorActor.ResolveConditionRequest conditionRequest=null;
                DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderRequest diagnosticOrderRequest=null;
                ObservationOrchestratorActor.ResolveObservationRequest observationRequest=null;
                DiagnosticReportOrchestratorActor.ResolveDiagnosticReportRequest diagnosticReportRequest=null;
                //Identify the number of Practitioner that are used to be searched
                //By their id, the id is supposed to be the same (Internal DHIS2 ID)
                /*
                if(nbrValidPractitioner>0)
                {
                    List<Practitioner> tempListOfValidePractitioner= resourceBundle.getListOfValidePractitioners();
                    listOfValidePractitioner=FhirMediatorUtilities.removeDuplicateInTheList(tempListOfValidePractitioner);
                    //listOfValidePractitioner=resourceBundle.getListOfValidePractitioners();
                    for(Practitioner oPractitionerToIdentify:resourceBundle.getListOfValidePractitioners()) {
                        if(oPractitionerToIdentify.getIdentifier().size()>0)
                        {
                            nbrOfSearchRequestToWaitFor++;
                        }
                    }
                }*/
                organizationProcessed=false;
                if(resourceBundle.getListOfValidOrganization().size()>0)
                {
                    this.listOfValidOrganization=resourceBundle.getListOfValidOrganization();
                    List<String> listOfId=new ArrayList<>();
                    for(Organization oOrganizationToIdentify:resourceBundle.getListOfValidOrganization())
                    {
                        listOfId.add(oOrganizationToIdentify.getId().getIdPart());

                    }
                    listIdsOrganizationUsedForSearch=listOfId;
                    organizationRequest=new OrganizationOrchestratorActor.ResolveOrganizationRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef organizationRequestOrchestrator=getContext().actorOf(
                            Props.create(OrganizationOrchestratorActor.class,config));
                    organizationRequestOrchestrator.tell(organizationRequest,getSelf());
                    organizationProcessed=true;

                }
                else
                {
                    responseOrganization=nullResponse;
                }
                /*if(resourceBundle.getListOfValidPractitioners().size()>0)
                if(false)
                {
                    this.listOfValidPractitioner=resourceBundle.getListOfValidPractitioners();
                    nbrOfSearchRequestToWaitFor=resourceBundle.getListOfValidPractitioners().size();
                    List<String> listOfId=new ArrayList<>();
                    for(Practitioner oPractitionerToIdentify:resourceBundle.getListOfValidPractitioners())
                    {
                        listOfId.add(oPractitionerToIdentify.getId().getIdPart());

                    }
                    listIdsPractitionerUsedForSearch=listOfId;
                    PractitionerRequest=new PractitionerOrchestratorActor.ResolvePractitionerRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef practitionerRequestOrchestrator=getContext().actorOf(
                            Props.create(PractitionerOrchestratorActor.class,config));
                    practitionerRequestOrchestrator.tell(PractitionerRequest,getSelf());

                }
                else
                {
                    responsePractitioner=nullResponse;
                }*/
                /*if(resourceBundle.getListOfValidPatient().size()>0)
                if(false)
                {
                    this.listOfValidPatient=resourceBundle.getListOfValidPatient();
                    //nbrOfSearchRequestToWaitFor=resourceBundle.getListOfValidePractitioners().size();
                    List<String> listOfId=new ArrayList<>();
                    for(Patient oPatientToIdentify:resourceBundle.getListOfValidPatient())
                    {
                        listOfId.add(oPatientToIdentify.getId().getIdPart());

                    }
                    listIdsPatientUsedForSearch=listOfId;
                    patientRequest=new PatientOrchestratorActor.ResolvePatientRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef patientRequestOrchestrator=getContext().actorOf(
                            Props.create(PatientOrchestratorActor.class,config));
                    patientRequestOrchestrator.tell(patientRequest,getSelf());

                }
                else
                {
                    responsePatient=nullResponse;
                }*/
                /*if(resourceBundle.getListOfValidSpecimen().size()>0)
                if(false)
                {
                    this.listOfValidSpecimen=resourceBundle.getListOfValidSpecimen();
                    List<String> listOfId=new ArrayList<>();
                    for(Specimen oSpecimenToIdentify:resourceBundle.getListOfValidSpecimen())
                    {
                        listOfId.add(oSpecimenToIdentify.getId().getIdPart());

                    }
                    listIdsSpecimenUsedForSearch=listOfId;
                    specimenRequest=new SpecimenOrchestratorActor.ResolveSpecimenRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef specimenRequestOrchestrator=getContext().actorOf(
                            Props.create(SpecimenOrchestratorActor.class,config));
                    specimenRequestOrchestrator.tell(specimenRequest,getSelf());

                }
                else
                {
                    responseSpecimen=nullResponse;
                }
                */
                /*if(resourceBundle.getListOfValidListResource().size()>0)
                if(false)
                {
                    this.listOfValidListResource=resourceBundle.getListOfValidListResource();
                    List<String> listOfId=new ArrayList<>();
                    for(ListResource oListResource:this.listOfValidListResource)
                    {
                        listOfId.add(oListResource.getId().getIdPart());
                    }
                    listIdsListResourceUsedForSearch=listOfId;
                    listResourceRequest=new ListResourceOrchestratorActor.ResolveListResourceRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef  listResourceRequestOrchestrator=getContext().actorOf(
                            Props.create(ListResourceOrchestratorActor.class,config));
                    listResourceRequestOrchestrator.tell(listResourceRequest,getSelf());
                }
                else
                {
                    responseListResource=nullResponse;
                }
                */
                /*if(resourceBundle.getListOfValidBasic().size()>0)
                if(false)
                {
                    this.listOfValidBasic=resourceBundle.getListOfValidBasic();
                    List<String> listOfId=new ArrayList<>();
                    for(Basic oBasic:this.listOfValidBasic)
                    {
                        listOfId.add(oBasic.getId().getIdPart());
                    }
                    listIdsBasicUsedForSearch=listOfId;
                    basicRequest=new BasicOrchestratorActor.ResolveBasicRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef  basicRequestOrchestrator=getContext().actorOf(
                            Props.create(BasicOrchestratorActor.class,config));
                    basicRequestOrchestrator.tell(basicRequest,getSelf());
                }
                else
                {
                    responseBasic=nullResponse;
                }
                */
                /*if(resourceBundle.getListOfValidCondition().size()>0)
                if(false)
                {
                    this.listOfValidCondition=resourceBundle.getListOfValidCondition();
                    List<String> listOfId=new ArrayList<>();
                    for(Condition oCondition:this.listOfValidCondition)
                    {
                        listOfId.add(oCondition.getId().getIdPart());
                    }
                    listIdsConditionUsedForSearch=listOfId;
                    conditionRequest=new ConditionOrchestratorActor.ResolveConditionRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef conditionRequestOrchestrator=getContext().actorOf(
                            Props.create(ConditionOrchestratorActor.class,config));
                    conditionRequestOrchestrator.tell(conditionRequest,getSelf());
                }
                else
                {
                    responseCondition=nullResponse;
                }
                */
                /*if(resourceBundle.getListOfValidDiagnosticOrder().size()>0)
                if(false)
                {
                    this.listOfValidDiagnosticOrder=resourceBundle.getListOfValidDiagnosticOrder();
                    List<String> listOfId=new ArrayList<>();
                    for(DiagnosticOrder oDiagnosticOrder:this.listOfValidDiagnosticOrder)
                    {
                        listOfId.add(oDiagnosticOrder.getId().getIdPart());
                    }
                    listIdsDiagnosticOrderUsedForSearch=listOfId;
                    diagnosticOrderRequest=new DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef diagnosticOrderRequestOrchestrator=getContext().actorOf(
                            Props.create(DiagnosticOrderOrchestratorActor.class,config));
                    diagnosticOrderRequestOrchestrator.tell(diagnosticOrderRequest,getSelf());

                }
                else
                {
                    responseDiagnosticOrder=nullResponse;
                }*/
                /*if(resourceBundle.getListOfValidObservation().size()>0)
                if(false)
                {
                    this.listOfValidObservation=resourceBundle.getListOfValidObservation();
                    List<String> listOfId=new ArrayList<>();
                    for(Observation oObservation:this.listOfValidObservation)
                    {
                        listOfId.add(oObservation.getId().getIdPart());
                    }
                    listIdsObservationUsedForSearch=listOfId;
                    observationRequest=new ObservationOrchestratorActor.ResolveObservationRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef observationRequestOrchestrator=getContext().actorOf(
                            Props.create(ObservationOrchestratorActor.class,config));
                    observationRequestOrchestrator.tell(observationRequest,getSelf());

                }
                else
                {
                    responseObservation=nullResponse;
                }
                */
                /*if(resourceBundle.getListOfValidDiagnosticReport().size()>0)
                if(false)
                {
                    this.listOfValidDiagnosticReport=resourceBundle.getListOfValidDiagnosticReport();
                    List<String> listOfId=new ArrayList<>();
                    for(DiagnosticReport oDiagnosticReport:this.listOfValidDiagnosticReport)
                    {
                        listOfId.add(oDiagnosticReport.getId().getIdPart());
                    }
                    listIdsDiagnosticReportUsedForSearch=listOfId;
                    diagnosticReportRequest=new DiagnosticReportOrchestratorActor.ResolveDiagnosticReportRequest(
                            originalRequest.getRequestHandler(),
                            getSelf(),
                            listOfId
                    );
                    ActorRef diagnosticReportRequestOrchestrator=getContext().actorOf(
                            Props.create(DiagnosticReportOrchestratorActor.class,config));
                    diagnosticReportRequestOrchestrator.tell(diagnosticReportRequest,getSelf());
                }
                else
                {
                    responseDiagnosticReport=nullResponse;
                }

                */


                //System.out.print(listSolvedPractitionerResponse.size());
                /*

                String jsonResponse="{\"response\":\"OK\"," +
                        "\"Total practitioner found\":"+nbrRetreivedPractitioner+"," +
                        "\"Valid Practitioner\":"+nbrValidPractitioner+"," +
                        "\"Invalid Practitioner\":"+nbreInvalidPractitioner+"}";
                FinishRequest fr = new FinishRequest(jsonResponse, "application/json", HttpStatus.SC_OK);*/
                FinishRequest fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
                originalRequest.getRespondTo().tell(logResult, getSelf());
                //originalRequest.getRespondTo().tell(fr, getSelf());

            }
            else {
                logResult+="::warning:"+response.toFinishRequest().toString();
                //originalRequest.getRespondTo().tell(response.toFinishRequest(), getSelf());
                originalRequest.getRespondTo().tell(logResult, getSelf());

            }

        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                    exc.getMessage(),"Error");
            log.error(exc.getMessage());
            logResult+="::error:"+exc.getMessage();
            originalRequest.getRespondTo().tell(logResult, getSelf());
            return;
        }

    }
    private void triggerPractitionerOrchestrator(List<Practitioner> listPractitionerToProcess,
                                                 PractitionerOrchestratorActor.ResolvePractitionerRequest practitionerRequest)
    {
        this.listOfValidPractitioner=listPractitionerToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidPractitioner.size();
        List<String> listOfId=new ArrayList<>();
        for(Practitioner oPractitionerToIdentify:listOfValidPractitioner)
        {
            listOfId.add(oPractitionerToIdentify.getId().getIdPart());

        }
        listIdsPractitionerUsedForSearch=listOfId;
        practitionerRequest=new PractitionerOrchestratorActor.ResolvePractitionerRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef practitionerRequestOrchestrator=getContext().actorOf(
                Props.create(PractitionerOrchestratorActor.class,config));
        practitionerRequestOrchestrator.tell(practitionerRequest,getSelf());
    }
    private void triggerPatientOrchestrator(List<Patient> listPatientToProcess,
                                                 PatientOrchestratorActor.ResolvePatientRequest patientRequest)
    {
        this.listOfValidPatient=listPatientToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidPatient.size();
        List<String> listOfId=new ArrayList<>();
        for(Patient oPatientToIdentify:listOfValidPatient)
        {
            listOfId.add(oPatientToIdentify.getId().getIdPart());

        }
        listIdsPatientUsedForSearch=listOfId;
        patientRequest=new PatientOrchestratorActor.ResolvePatientRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef patientRequestOrchestrator=getContext().actorOf(
                Props.create(PatientOrchestratorActor.class,config));
        patientRequestOrchestrator.tell(patientRequest,getSelf());
    }
    private void triggerSpecimenOrchestrator(List<Specimen> listSpecimenToProcess,
                                            SpecimenOrchestratorActor.ResolveSpecimenRequest specimenRequest)
    {
        this.listOfValidSpecimen=listSpecimenToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidSpecimen.size();
        List<String> listOfId=new ArrayList<>();
        for(Specimen oSpecimenToIdentify:listOfValidSpecimen)
        {
            listOfId.add(oSpecimenToIdentify.getId().getIdPart());

        }
        listIdsSpecimenUsedForSearch=listOfId;
        specimenRequest=new SpecimenOrchestratorActor.ResolveSpecimenRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef specimenRequestOrchestrator=getContext().actorOf(
                Props.create(SpecimenOrchestratorActor.class,config));
        specimenRequestOrchestrator.tell(specimenRequest,getSelf());
    }
    private void triggerListResourceOrchestrator(List<ListResource> listListResourceToProcess,
                                             ListResourceOrchestratorActor.ResolveListResourceRequest listResourceRequest)
    {
        this.listOfValidListResource=listListResourceToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidListResource.size();
        List<String> listOfId=new ArrayList<>();
        for(ListResource oListResourceToIdentify:listOfValidListResource)
        {
            listOfId.add(oListResourceToIdentify.getId().getIdPart());

        }
        listIdsListResourceUsedForSearch=listOfId;
        listResourceRequest=new ListResourceOrchestratorActor.ResolveListResourceRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef listResourceRequestOrchestrator=getContext().actorOf(
                Props.create(ListResourceOrchestratorActor.class,config));
        listResourceRequestOrchestrator.tell(listResourceRequest,getSelf());
    }
    private void triggerConditionOrchestrator(List<Condition> listConditionToProcess,
                                                 ConditionOrchestratorActor.ResolveConditionRequest conditionRequest)
    {
        this.listOfValidCondition=listConditionToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidCondition.size();
        List<String> listOfId=new ArrayList<>();
        for(Condition oConditionToIdentify:listOfValidCondition)
        {
            listOfId.add(oConditionToIdentify.getId().getIdPart());

        }
        listIdsConditionUsedForSearch=listOfId;
        conditionRequest=new ConditionOrchestratorActor.ResolveConditionRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef conditionRequestOrchestrator=getContext().actorOf(
                Props.create(ConditionOrchestratorActor.class,config));
        conditionRequestOrchestrator.tell(conditionRequest,getSelf());
    }
    private void triggerDiagnosticOrderOrchestrator(List<DiagnosticOrder> listDiagnosticOrderToProcess,
                                              DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderRequest diagnosticOrderRequest)
    {
        this.listOfValidDiagnosticOrder=listDiagnosticOrderToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidDiagnosticOrder.size();
        List<String> listOfId=new ArrayList<>();
        for(DiagnosticOrder oOrderToIdentify:listOfValidDiagnosticOrder)
        {
            listOfId.add(oOrderToIdentify.getId().getIdPart());

        }
        listIdsDiagnosticOrderUsedForSearch=listOfId;
        diagnosticOrderRequest=new DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef orderRequestOrchestrator=getContext().actorOf(
                Props.create(DiagnosticOrderOrchestratorActor.class,config));
        orderRequestOrchestrator.tell(diagnosticOrderRequest,getSelf());
    }
    private void triggerObservationOrchestrator(List<Observation> listObservationToProcess,
                                                   ObservationOrchestratorActor.ResolveObservationRequest observationRequest)
    {
        this.listOfValidObservation=listObservationToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidObservation.size();
        List<String> listOfId=new ArrayList<>();
        for(Observation oObservation:listOfValidObservation)
        {
            listOfId.add(oObservation.getId().getIdPart());

        }
        listIdsObservationUsedForSearch=listOfId;
        observationRequest=new ObservationOrchestratorActor.ResolveObservationRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef observationRequestOrchestrator=getContext().actorOf(
                Props.create(ObservationOrchestratorActor.class,config));
        observationRequestOrchestrator.tell(observationRequest,getSelf());
    }
    private void triggerDiagnosticReportOrchestrator(List<DiagnosticReport> listDiagnosticReportToProcess,
                                                DiagnosticReportOrchestratorActor.ResolveDiagnosticReportRequest reportRequest)
    {
        this.listOfValidDiagnosticReport=listDiagnosticReportToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidDiagnosticReport.size();
        List<String> listOfId=new ArrayList<>();
        for(DiagnosticReport oReport:listOfValidDiagnosticReport)
        {
            listOfId.add(oReport.getId().getIdPart());

        }
        listIdsDiagnosticReportUsedForSearch=listOfId;
        reportRequest=new DiagnosticReportOrchestratorActor.ResolveDiagnosticReportRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef diagnosticReportRequestOrchestrator=getContext().actorOf(
                Props.create(DiagnosticReportOrchestratorActor.class,config));
        diagnosticReportRequestOrchestrator.tell(reportRequest,getSelf());
    }
    private void triggerBasicOrchestrator(List<Basic> listBasicToProcess,
                                                     BasicOrchestratorActor.ResolveBasicRequest basicRequest)
    {
        this.listOfValidBasic=listBasicToProcess;
        nbrOfSearchRequestToWaitFor=this.listOfValidBasic.size();
        List<String> listOfId=new ArrayList<>();
        for(Basic oBasic:listOfValidBasic)
        {
            listOfId.add(oBasic.getId().getIdPart());

        }
        listIdsBasicUsedForSearch=listOfId;
        basicRequest=new BasicOrchestratorActor.ResolveBasicRequest(
                originalRequest.getRequestHandler(),
                getSelf(),
                listOfId
        );
        ActorRef basicRequestOrchestrator=getContext().actorOf(
                Props.create(BasicOrchestratorActor.class,config));
        basicRequestOrchestrator.tell(basicRequest,getSelf());
    }
    private void finalizePractitionerRequest(String practitionerResponse) {


        //if(practitionerResponse==null || organizationProcessed==true)
        if(practitionerResponse==null)
        {
            return;
        }
        else
        {
            listSolvedPractitionerResponse.add(practitionerResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedPractitionerResponse.size());
                System.out.println(nbrOfSearchRequestToWaitFor);
                //Identify List of Practitioner to Update
                //this.listOfPractitionerToUpdate=IdentifyPractitionerToUpdate(listSolvedPractitionerResponse);
                //identifyMapCouplePractitionerAndIdToUpdate(listSolvedPractitionerResponse);

                //from the original list of Practitioner found, extract the rest of Practitioner to add
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                String resultInsertion=null;
                identifyPractitionerToUpdate(listSolvedPractitionerResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyPractitionerToAdd();
                //resultOutPutHeader+="res:";
                if(listOfPractitionerToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createPractitioner(resourceBundle.getContext(),
                            this.listOfPractitionerToAdd,
                            baseServerRepoURI);
                }
                if(resultInsertion!=null)
                {
                    resultOutPutHeader+=""+resultInsertion+",";
                }

                /*
                String  resultUpdate=FhirResourceProcessor.updatePractitionerInTransaction(resourceBundle.getContext(),
                        this.listOfPractitionerToUpdate,
                        baseServerRepoURI);
                 */
                String  resultUpdate=null;
                if(this.listOfPractitionerToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updatePractitionerInTransaction(resourceBundle.getContext(),
                            this.listOfPractitionerToUpdate,
                            baseServerRepoURI);
                }
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    resultOutPutHeader+="Practitioner:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    resultOutPutHeader+="";
                }
                else
                {
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                log.error(exc.getMessage());
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }
    private void finalizePatientRequest(String patientResponse) {


        //if(patientResponse==null || organizationProcessed==true )
        if(patientResponse==null)
        {
            return;
        }
        else
        {
            listSolvedPatientResponse.add(patientResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedPatientResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifyPatientToUpdate(listSolvedPatientResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyPatientToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfPatientToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createPatient(resourceBundle.getContext(),
                            this.listOfPatientToAdd,
                            baseServerRepoURI,logFileName);
                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }

                /*
                String  resultUpdate=FhirResourceProcessor.updatePractitionerInTransaction(resourceBundle.getContext(),
                        this.listOfPractitionerToUpdate,
                        baseServerRepoURI);
                 */
                String  resultUpdate=null;
                if(this.listOfPatientToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updatePatientInTransaction(resourceBundle.getContext(),
                            this.listOfPatientToUpdate,
                            baseServerRepoURI);
                }
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Patient:";
                    resultOutPutHeader+="Patient:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }
    private void finalizeListResourceRequest(String listResourceResponse) {


        //if(patientResponse==null || organizationProcessed==true )
        if(listResourceResponse==null)
        {
            return;
        }
        else
        {
            listSolvedListResourceResponse.add(listResourceResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedListResourceResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifyListResourceToUpdate(listSolvedListResourceResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyListResourceToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfListResourceToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createListResource(resourceBundle.getContext(),
                            this.listOfListResourceToAdd,
                            baseServerRepoURI,logFileName);
                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }

                /*
                String  resultUpdate=FhirResourceProcessor.updatePractitionerInTransaction(resourceBundle.getContext(),
                        this.listOfPractitionerToUpdate,
                        baseServerRepoURI);
                 */
                String  resultUpdate=null;
                if(this.listOfListResourceToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updateListResourceInTransaction(resourceBundle.getContext(),
                            this.listOfListResourceToUpdate,
                            baseServerRepoURI);
                }
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Patient:";
                    resultOutPutHeader+="ListResource:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }
    private void finalizeBasicRequest(String listBasicResponse) {


        //if(patientResponse==null || organizationProcessed==true )
        if(listBasicResponse==null)
        {
            return;
        }
        else
        {
            listSolvedBasicResponse.add(listBasicResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedBasicResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifyBasicToUpdate(listSolvedBasicResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyBasicToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfBasicToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createBasic(resourceBundle.getContext(),
                            this.listOfBasicToAdd,
                            baseServerRepoURI,logFileName);
                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }

                /*
                String  resultUpdate=FhirResourceProcessor.updatePractitionerInTransaction(resourceBundle.getContext(),
                        this.listOfPractitionerToUpdate,
                        baseServerRepoURI);
                 */
                String  resultUpdate=null;
                if(this.listOfBasicToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updateBasicInTransaction(resourceBundle.getContext(),
                            this.listOfBasicToUpdate,
                            baseServerRepoURI);
                }
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Patient:";
                    resultOutPutHeader+="Basic:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }

    private void finalizeOrganizationRequest(String organizationResponse) {


        if(organizationResponse==null )
        {
            return;
        }
        else
        {
            listSolvedOrganizationResponse.add(organizationResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedOrganizationResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifyOrganizationToUpdate(listSolvedOrganizationResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyOrganizationToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfOrganizationToAdd.size()>0 ){
                    resultInsertion =FhirResourceProcessor.createOrganization(resourceBundle.getContext(),
                            this.listOfOrganizationToAdd,
                            baseServerRepoURI);
                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }
                String  resultUpdate=null;
                if(this.listOfOrganizationToUpdate.size()>0 && listOfOrganizationToAdd.size()>0 )
                {
                    resultUpdate=FhirResourceProcessor.updateOrganizationInTransaction(resourceBundle.getContext(),
                            this.listOfOrganizationToUpdate,
                            baseServerRepoURI);
                }
                organizationProcessed=false;
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Organization:";
                    resultOutPutHeader+="Organization:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }
    private void finalizeSpecimenRequest(String specimenResponse) {


        if(specimenResponse==null )
        {
            return;
        }
        else
        {
            listSolvedSpecimenResponse.add(specimenResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedSpecimenResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifySpecimenToUpdate(listSolvedSpecimenResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifySpecimenToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfSpecimenToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createSpecimen(resourceBundle.getContext(),
                            this.listOfSpecimenToAdd,
                            baseServerRepoURI,logFileName);
                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }
                String  resultUpdate=null;
                if(this.listOfSpecimenToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updateSpecimenInTransaction(resourceBundle.getContext(),
                            this.listOfSpecimenToUpdate,
                            baseServerRepoURI);
                }
                //organizationProcessed=false;
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Specimen:";
                    resultOutPutHeader+="Specimen:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }
    private void finalizeConditionRequest(String conditionResponse) {


        if(conditionResponse==null )
        {
            return;
        }
        else
        {
            listSolvedConditionResponse.add(conditionResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedConditionResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifyConditionToUpdate(listSolvedConditionResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyConditionToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfConditionToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createCondition(resourceBundle.getContext(),
                            this.listOfConditionToAdd,
                            baseServerRepoURI,logFileName);
                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }
                String  resultUpdate=null;
                if(this.listOfConditionToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updateConditionInTransaction(resourceBundle.getContext(),
                            this.listOfConditionToUpdate,
                            baseServerRepoURI);
                }
                //organizationProcessed=false;
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Specimen:";
                    resultOutPutHeader+="Condition:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }

    private void finalizeDiagnosticOrderRequest(String diagnosticOrderResponse) {


        if(diagnosticOrderResponse==null )
        {
            return;
        }
        else
        {
            listSolvedDiagnosticOrderResponse.add(diagnosticOrderResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedDiagnosticOrderResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifyDiagnosticOrderToUpdate(listSolvedDiagnosticOrderResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyDiagnosticOrderToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfDiagnosticOrderToAdd.size()>0){

                    resultInsertion =FhirResourceProcessor.createDiagnosticOrder(resourceBundle.getContext(),
                            this.listOfDiagnosticOrderToAdd,
                            baseServerRepoURI,logFileName);
                    /*resultInsertion =FhirResourceProcessor.createDiagnosticOrderInTransaction(resourceBundle.getContext(),
                            this.listOfDiagnosticOrderToAdd,
                            baseServerRepoURI);*/

                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }
                String  resultUpdate=null;
                if(this.listOfDiagnosticOrderToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updateDiagnosticOrderInTransaction(resourceBundle.getContext(),
                            this.listOfDiagnosticOrderToUpdate,
                            baseServerRepoURI);
                }
                //organizationProcessed=false;
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Specimen:";
                    resultOutPutHeader+="DiagnosticOrder:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }
    private void finalizeObservationRequest(String observationResponse) {


        if(observationResponse==null )
        {
            return;
        }
        else
        {
            listSolvedObservationResponse.add(observationResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedObservationResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifyObservationToUpdate(listSolvedObservationResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyObservationToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfObservationToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createObservation(resourceBundle.getContext(),
                            this.listOfObservationToAdd,
                            baseServerRepoURI,logFileName);
                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }
                String  resultUpdate=null;
                if(this.listOfObservationToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updateObservationInTransaction(resourceBundle.getContext(),
                            this.listOfObservationToUpdate,
                            baseServerRepoURI);
                }
                //organizationProcessed=false;
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Specimen:";
                    resultOutPutHeader+="Observation:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }
    private void finalizeDiagnosticReportRequest(String diagnosticReportResponse) {


        if(diagnosticReportResponse==null )
        {
            return;
        }
        else
        {
            listSolvedDiagnosticReportResponse.add(diagnosticReportResponse);
            logResult="";
            FinishRequest fr =null;
            try
            {
                //finish
                System.out.println(listSolvedDiagnosticReportResponse.size());
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //Identify List of Patient to add and to Update
                identifyDiagnosticReportToUpdate(listSolvedDiagnosticReportResponse,baseServerRepoURI);//Always run update identification before the add identification
                identifyDiagnosticReportToAdd();
                //from the original list of Practitioner found, extract the rest of Practitioner to add

                String resultInsertion=null;
                //resultOutPutHeader+="res:";
                if(listOfDiagnosticReportToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createDiagnosticReport(resourceBundle.getContext(),
                            this.listOfDiagnosticReportToAdd,
                            baseServerRepoURI,logFileName);
                }
                if(resultInsertion!=null)
                {
                    //resultOutPutHeader+="["+resultInsertion+",";
                    resultOutPutHeader+=resultInsertion+",";
                }
                String  resultUpdate=null;
                if(this.listOfDiagnosticReportToUpdate.size()>0)
                {
                    resultUpdate=FhirResourceProcessor.updateDiagnosticReportInTransaction(resourceBundle.getContext(),
                            this.listOfDiagnosticReportToUpdate,
                            baseServerRepoURI);
                }
                //organizationProcessed=false;
                if(resultUpdate!=null || resultInsertion!=null)
                {
                    //resultOutPutHeader+="[Specimen:";
                    resultOutPutHeader+="DiagnosticReport:";
                    if(resultUpdate!=null)
                    {
                        resultOutPutHeader+=resultUpdate;
                    }
                    if(resultInsertion!=null)
                    {
                        resultOutPutHeader+=","+resultInsertion;
                    }
                    //resultOutPutHeader+="]";
                    resultOutPutHeader+="";
                }
                else
                {
                    //resultOutPutHeader+="[]";
                    resultOutPutHeader+="";
                }
                resultOutPutHeader+=resultOutPutTail+",";

                System.out.print(0);
                logResult+=resultOutPutHeader;
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
                logResult+="Operation completed!";

                //this.syncConfiguration.setLastSyncDate(this.stringSyncDate);
                //fr = new FinishRequest(resultOutPutHeader, "application/json", HttpStatus.SC_OK);
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            catch (Exception exc)
            {
                log.error(exc.getMessage());
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        exc.getMessage(),"Error");
                String errorMessage="{error:"+exc.getMessage()+"}";
                logResult+="::error:"+exc.getMessage();
                fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            }
            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }

    private void mainFinalize() {
        if(responseOrganization==null || responsePatient ==null || responsePractitioner==null
                || responseSpecimen==null || responseObservation==null || responseDiagnosticReport==null
                || responseDiagnosticOrder==null || responseCondition==null || responseListResource==null
                || responseBasic==null)

        //if(responseOrganization==null || responsePatient ==null)
        {
            return ;
        }
        else
        {
            resultOutPutHeader+="res:[";
            finalizeOrganizationRequest(responseOrganization);
            finalizePractitionerRequest(responsePractitioner);
            finalizePatientRequest(responsePatient);
            finalizeSpecimenRequest(responseSpecimen);
            finalizeConditionRequest(responseCondition);
            finalizeDiagnosticOrderRequest(responseDiagnosticOrder);
            finalizeObservationRequest(responseObservation);
            finalizeDiagnosticReportRequest(responseDiagnosticReport);
            finalizeListResourceRequest(responseListResource);
            finalizeBasicRequest(responseBasic);
            FinishRequest _fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            originalRequest.getRespondTo().tell(_fr, getSelf());
        }
    }
    private void mainFinalizeOrganization()
    {
        if(responseOrganization==null)
        {
            return ;
        }
        else
        {
            PractitionerOrchestratorActor.ResolvePractitionerRequest practitionerRequest =null;
            if(resourceBundle.getListOfValidPractitioners().size()>0)
            {
            triggerPractitionerOrchestrator(resourceBundle.getListOfValidPractitioners(),
                     practitionerRequest );
            }
            else
            {
                responsePractitioner=nullResponse;
            }
        }
    }
    private void mainFinalizePractitioner()
    {
        if(responsePractitioner==null)
        {
            return ;
        }
        else
        {
            //System.out.print(0);
            PatientOrchestratorActor.ResolvePatientRequest patientRequest =null;
            if(resourceBundle.getListOfValidPatient().size()>0)
            {
                triggerPatientOrchestrator(resourceBundle.getListOfValidPatient(),
                        patientRequest );
            }
            else
            {
                responsePatient=nullResponse;
            }

        }
    }
    private void mainFinalizePatient()
    {
        if(responsePatient==null)
        {
            return ;
        }
        else
        {
            //System.out.print(0);
           SpecimenOrchestratorActor.ResolveSpecimenRequest specimenRequest =null;
            if(resourceBundle.getListOfValidSpecimen().size()>0)
            {
                triggerSpecimenOrchestrator(resourceBundle.getListOfValidSpecimen(),
                        specimenRequest );
            }
            else
            {
                responseSpecimen=nullResponse;
            }

        }
    }
    private void mainFinalizeSpecimen()
    {
        if(responseSpecimen==null)
        {
            return ;
        }
        else
        {
            //System.out.print(0);
            ConditionOrchestratorActor.ResolveConditionRequest conditionRequest =null;
            if(resourceBundle.getListOfValidCondition().size()>0)
            {
                triggerConditionOrchestrator(resourceBundle.getListOfValidCondition(),
                        conditionRequest );
            }
            else
            {
                responseCondition=nullResponse;
            }

        }
    }
    private void mainFinalizeCondition()
    {
        if(responseCondition==null)
        {
            return ;
        }
        else
        {
            //System.out.print(0);
            DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderRequest diagnosticOrderRequest =null;
            if(resourceBundle.getListOfValidDiagnosticOrder().size()>0)
            {
                triggerDiagnosticOrderOrchestrator(resourceBundle.getListOfValidDiagnosticOrder(),
                        diagnosticOrderRequest );
            }
            else
            {
                responseDiagnosticOrder=nullResponse;
            }

        }
    }
    private void mainFinalizeDiagnosticOrder()
    {
        if(responseDiagnosticOrder==null)
        {
            return ;
        }
        else
        {
            //System.out.print(0);
           ObservationOrchestratorActor.ResolveObservationRequest observationRequest =null;
            if(resourceBundle.getListOfValidObservation().size()>0)
            {
                triggerObservationOrchestrator(resourceBundle.getListOfValidObservation(),
                        observationRequest );
            }
            else
            {
                responseObservation=nullResponse;
            }

        }
    }
    private void mainFinalizeObservation()
    {
        if(responseObservation==null)
        {
            return ;
        }
        else
        {
            //System.out.print(0);
            DiagnosticReportOrchestratorActor.ResolveDiagnosticReportRequest reportRequest =null;
            if(resourceBundle.getListOfValidDiagnosticReport().size()>0)
            {
                triggerDiagnosticReportOrchestrator(resourceBundle.getListOfValidDiagnosticReport(),
                        reportRequest );
            }
            else
            {
                responseDiagnosticReport=nullResponse;
            }

        }
    }
    private void mainFinalizeDiagnosticReport()
    {
        if(responseDiagnosticReport==null)
        {
            return ;
        }
        else
        {
            //System.out.print(0);
            ListResourceOrchestratorActor.ResolveListResourceRequest reportRequest =null;
            if(resourceBundle.getListOfValidListResource().size()>0)
            {
                triggerListResourceOrchestrator(resourceBundle.getListOfValidListResource(),
                        reportRequest );
            }
            else
            {
                responseListResource=nullResponse;
            }

        }
    }
    private void mainFinalizeListResource()
    {
        if(responseListResource==null)
        {
            return ;
        }
        else
        {
            //System.out.print(0);
            BasicOrchestratorActor.ResolveBasicRequest reportRequest =null;
            if(resourceBundle.getListOfValidBasic().size()>0)
            {
                triggerBasicOrchestrator(resourceBundle.getListOfValidBasic(),
                        reportRequest );
            }
            else
            {
                responseBasic=nullResponse;
            }

        }
    }
    private void mainFinalizeBasic()
    {
        if(responseBasic==null)
        {
            return ;
        }
        else
        {
            mainFinalize();

        }
    }
    void stopRequestProcessing(FinishRequest _fr)
    {
        if(responsePatient!=null && responsePractitioner!=null && responseOrganization !=null
                && responseSpecimen!=null)
        {
            originalRequest.getRespondTo().tell(_fr, getSelf());
        }
    }

    void identifyPractitionerToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Practitioner oPractitioner :resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult,serverRepoURI))
                {
                    if(listIdsPractitionerUsedForSearch.contains(oPractitioner.getId().getIdPart()))
                    {
                        //this.listOfPractitionerToUpdate.add(oPractitioner);
                        Practitioner tempPractitioner=getPractitionerFromValidList(oPractitioner.getId().getIdPart());
                        if(tempPractitioner!=null)
                        {
                            this.listOfPractitionerToUpdate.add(tempPractitioner);
                        }

                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    Practitioner getPractitionerFromValidList(String id)
    {
        for (Practitioner oPracitioner:this.listOfValidPractitioner) {
            if(oPracitioner.getId().getIdPart().equals(id))
            {
                return oPracitioner;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    Patient getPatientFromValidList(String id)
    {
        for (Patient oPatient:this.listOfValidPatient) {
            if(oPatient.getId().getIdPart().equals(id))
            {
                return oPatient;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    ListResource getListResourceFromValidList(String id)
    {
        for (ListResource oListResource:this.listOfValidListResource) {
            if(oListResource.getId().getIdPart().equals(id))
            {
                return oListResource;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    Basic getBasicFromValidList(String id)
    {
        for (Basic oBasic:this.listOfValidBasic) {
            if(oBasic.getId().getIdPart().equals(id))
            {
                return oBasic;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    Organization getOrganizationFromValidList(String id)
    {
        for (Organization oOrganization:this.listOfValidOrganization) {
            if(oOrganization.getId().getIdPart().equals(id))
            {
                return oOrganization;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    Specimen getSpecimenFromValidList(String id)
    {
        for (Specimen oSpecimen:this.listOfValidSpecimen) {
            if(oSpecimen.getId().getIdPart().equals(id))
            {
                return oSpecimen;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    Condition getConditionFromValidList(String id)
    {
        for (Condition oCondition:this.listOfValidCondition) {
            if(oCondition.getId().getIdPart().equals(id))
            {
                return oCondition;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    DiagnosticOrder getDiagnosticOrderFromValidList(String id)
    {
        for (DiagnosticOrder oDiagnosticOrder:this.listOfValidDiagnosticOrder) {
            if(oDiagnosticOrder.getId().getIdPart().equals(id))
            {
                return oDiagnosticOrder;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    Observation getObservationFromValidList(String id)
    {
        for (Observation oObservation:this.listOfValidObservation) {
            if(oObservation.getId().getIdPart().equals(id))
            {
                return oObservation;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    DiagnosticReport getDiagnosticReportFromValidList(String id)
    {
        for (DiagnosticReport oDiagnosticReport:this.listOfValidDiagnosticReport) {
            if(oDiagnosticReport.getId().getIdPart().equals(id))
            {
                return oDiagnosticReport;
            }
            else
            {
                continue;
            }
        }
        return null;
    }
    void identifyPatientToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Patient oPatient :resourceBundle.extractPatientFromBundleString(oBundleSearchResult,serverRepoURI))
                {
                    if(listIdsPatientUsedForSearch.contains(oPatient.getId().getIdPart()))
                    {
                        Patient tempPatient=getPatientFromValidList(oPatient.getId().getIdPart());
                        if(tempPatient!=null)
                        {
                            this.listOfPatientToUpdate.add(tempPatient);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    void identifyListResourceToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (ListResource oListResource :resourceBundle.extractListResourceFromBundleString(oBundleSearchResult,serverRepoURI))
                {
                    if(listIdsListResourceUsedForSearch.contains(oListResource.getId().getIdPart()))
                    {
                        ListResource tempListResource=getListResourceFromValidList(oListResource.getId().getIdPart());
                        if(tempListResource!=null)
                        {
                            this.listOfListResourceToUpdate.add(tempListResource);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    void identifyBasicToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Basic oBasic :resourceBundle.extractBasicFromBundleString(oBundleSearchResult,serverRepoURI))
                {
                    if(listIdsBasicUsedForSearch.contains(oBasic.getId().getIdPart()))
                    {
                        Basic tempBasic=getBasicFromValidList(oBasic.getId().getIdPart());
                        if(tempBasic!=null)
                        {
                            this.listOfBasicToUpdate.add(tempBasic);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    void identifyOrganizationToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Organization oOrganization :resourceBundle.extractOrganizationFromBundleString(oBundleSearchResult,serverRepoURI))
                {
                    if(listIdsOrganizationUsedForSearch.contains(oOrganization.getId().getIdPart()))
                    {
                        //this.listOfOrganizationToUpdate.add(oOrganization);
                        Organization tempOrganization=getOrganizationFromValidList(oOrganization.getId().getIdPart());
                        if(tempOrganization!=null)
                        {
                            this.listOfOrganizationToUpdate.add(tempOrganization);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    void identifySpecimenToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Specimen oSpecimen :resourceBundle.extractSpecimenFromBundleString(oBundleSearchResult, serverRepoURI))
                {
                    if(listIdsSpecimenUsedForSearch.contains(oSpecimen.getId().getIdPart()))
                    {
                        //this.listOfOrganizationToUpdate.add(oOrganization);
                        Specimen tempSpecimen=getSpecimenFromValidList(oSpecimen.getId().getIdPart());
                        if(tempSpecimen!=null)
                        {
                            this.listOfSpecimenToUpdate.add(tempSpecimen);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    void identifyConditionToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Condition oCondition :resourceBundle.extractConditionFromBundleString(oBundleSearchResult,serverRepoURI))
                {
                    if(listIdsConditionUsedForSearch.contains(oCondition.getId().getIdPart()))
                    {
                        //this.listOfOrganizationToUpdate.add(oOrganization);
                        Condition tempCondition=getConditionFromValidList(oCondition.getId().getIdPart());
                        if(tempCondition!=null)
                        {
                            this.listOfConditionToUpdate.add(tempCondition);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    void identifyDiagnosticOrderToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (DiagnosticOrder oDiagnosticOrder :resourceBundle.extractDiagnosticOrderFromBundleString(oBundleSearchResult,serverRepoURI))
                {
                    if(listIdsDiagnosticOrderUsedForSearch.contains(oDiagnosticOrder.getId().getIdPart()))
                    {
                        //this.listOfOrganizationToUpdate.add(oOrganization);
                        DiagnosticOrder tempDiagnosticOrder=getDiagnosticOrderFromValidList(oDiagnosticOrder.getId().getIdPart());
                        if(tempDiagnosticOrder!=null)
                        {
                            this.listOfDiagnosticOrderToUpdate.add(tempDiagnosticOrder);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    void identifyObservationToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Observation oObservation :resourceBundle.extractObservationFromBundleString(oBundleSearchResult, serverRepoURI))
                {
                    if(listIdsObservationUsedForSearch.contains(oObservation.getId().getIdPart()))
                    {
                        //this.listOfOrganizationToUpdate.add(oOrganization);
                        Observation tempObservation=getObservationFromValidList(oObservation.getId().getIdPart());
                        if(tempObservation!=null)
                        {
                            this.listOfObservationToUpdate.add(tempObservation);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }
    void identifyDiagnosticReportToUpdate(List<String> bundleSearchResultSet,String serverRepoURI)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (DiagnosticReport oDiagnosticReport :resourceBundle.extractDiagnosticReportFromBundleString(oBundleSearchResult,serverRepoURI))
                {
                    if(listIdsDiagnosticReportUsedForSearch.contains(oDiagnosticReport.getId().getIdPart()))
                    {
                        //this.listOfOrganizationToUpdate.add(oOrganization);
                        DiagnosticReport tempDiagnosticReport=getDiagnosticReportFromValidList(oDiagnosticReport.getId().getIdPart());
                        if(tempDiagnosticReport!=null)
                        {
                            this.listOfDiagnosticReportToUpdate.add(tempDiagnosticReport);
                        }
                    }
                    else
                    {
                        continue;
                    }
                }
                //ListIdentifiedForUpdateTarget.addAll(resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult));
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return ;
        }
    }

    void identifyPractitionerToAdd()
    {
        //List<Practitioner> listOfPractitio
        //Build Id list of Practitioner tagged for update

        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(Practitioner oPractitioner : this.listOfPractitionerToUpdate)
            {
                listOfIdsForUpdate.add(oPractitioner.getId().getIdPart());
            }
            for(Practitioner oPractitioner : this.listOfValidPractitioner)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oPractitioner.getId().getIdPart())==false)
                {
                    this.listOfPractitionerToAdd.add(oPractitioner);
                }


            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
            //return null;
        }
    }
    void identifyPatientToAdd()
    {

        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(Patient oPatient : this.listOfPatientToUpdate)
            {
                listOfIdsForUpdate.add(oPatient.getId().getIdPart());
            }
            for(Patient oPatient : this.listOfValidPatient)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oPatient.getId().getIdPart())==false)
                {
                    this.listOfPatientToAdd.add(oPatient);
                }
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }
    void identifyListResourceToAdd()
    {

        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(ListResource oListResource : this.listOfListResourceToUpdate)
            {
                listOfIdsForUpdate.add(oListResource.getId().getIdPart());
            }
            for(ListResource oListResource : this.listOfValidListResource)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oListResource.getId().getIdPart())==false)
                {
                    this.listOfListResourceToAdd.add(oListResource);
                }
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }
    void identifyBasicToAdd()
    {

        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(Basic oBasic : this.listOfBasicToUpdate)
            {
                listOfIdsForUpdate.add(oBasic.getId().getIdPart());
            }
            for(Basic oBasic : this.listOfValidBasic)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oBasic.getId().getIdPart())==false)
                {
                    this.listOfBasicToAdd.add(oBasic);
                }
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }
    void identifyOrganizationToAdd()
    {

        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(Organization oOrganization : this.listOfOrganizationToUpdate)
            {
                listOfIdsForUpdate.add(oOrganization.getId().getIdPart());
            }
            for(Organization oOrganization : this.listOfValidOrganization)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oOrganization.getId().getIdPart())==false)
                {
                    this.listOfOrganizationToAdd.add(oOrganization);
                }
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }
    void identifySpecimenToAdd()
    {
        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(Specimen oSpecimen : this.listOfSpecimenToUpdate)
            {
                listOfIdsForUpdate.add(oSpecimen.getId().getIdPart());
            }
            String inTheList="";
            String notInThelist="";
            for(Specimen oSpecimen  : this.listOfValidSpecimen)
            {
                boolean isToDiscard=false;

                if(listOfIdsForUpdate.contains(oSpecimen.getId().getIdPart())==false)
                {
                    this.listOfSpecimenToAdd.add(oSpecimen);
                    inTheList+=oSpecimen.getId().getIdPart()+",";
                }
                else
                {
                    notInThelist+=oSpecimen.getId().getIdPart()+",";
                }
            }
            System.out.print(0);
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }
    void identifyConditionToAdd()
    {
        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(Condition oCondition : this.listOfConditionToUpdate)
            {
                listOfIdsForUpdate.add(oCondition.getId().getIdPart());
            }
            for(Condition oCondition  : this.listOfValidCondition)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oCondition.getId().getIdPart())==false)
                {
                    this.listOfConditionToAdd.add(oCondition);
                }
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }
    void identifyDiagnosticOrderToAdd()
    {
        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(DiagnosticOrder oDiagnosticOrder : this.listOfDiagnosticOrderToUpdate)
            {
                listOfIdsForUpdate.add(oDiagnosticOrder.getId().getIdPart());
            }
            for(DiagnosticOrder oDiagnosticOrder  : this.listOfValidDiagnosticOrder)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oDiagnosticOrder.getId().getIdPart())==false)
                {
                    this.listOfDiagnosticOrderToAdd.add(oDiagnosticOrder);
                }
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }
    void identifyObservationToAdd()
    {
        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(Observation oObservation : this.listOfObservationToUpdate)
            {
                listOfIdsForUpdate.add(oObservation.getId().getIdPart());
            }
            for(Observation oObservation  : this.listOfValidObservation)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oObservation.getId().getIdPart())==false)
                {
                    this.listOfObservationToAdd.add(oObservation);
                }
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }
    void identifyDiagnosticReportToAdd()
    {
        try
        {
            List<String> listOfIdsForUpdate=new ArrayList<>();
            for(DiagnosticReport oDiagnosticReport : this.listOfDiagnosticReportToUpdate)
            {
                listOfIdsForUpdate.add(oDiagnosticReport.getId().getIdPart());
            }
            for(DiagnosticReport oDiagnosticReport  : this.listOfValidDiagnosticReport)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oDiagnosticReport.getId().getIdPart())==false)
                {
                    this.listOfDiagnosticReportToAdd.add(oDiagnosticReport);
                }
            }
        }
        catch (Exception exc)
        {
            log.error(exc.getMessage());
        }
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            queryDHIS2FhirRepositoryResources((MediatorHTTPRequest) msg);
        } else if (msg instanceof MediatorHTTPResponse){
            processFhirRepositoryServiceResponse((MediatorHTTPResponse) msg);
            //finalizeRequest(null);
        }
        else if (msg instanceof PractitionerOrchestratorActor.ResolvePractitionerResponse){
            responsePractitioner =((PractitionerOrchestratorActor.ResolvePractitionerResponse)msg).getResponseObject();
            //finalizeRequest(responsePractitioner);
            //mainFinalize();
            mainFinalizePractitioner();
        }
        else if (msg instanceof PatientOrchestratorActor.ResolvePatientResponse){
            responsePatient =((PatientOrchestratorActor.ResolvePatientResponse)msg).getResponseObject();
            //finalizePatientRequest(responsePatient);
            mainFinalizePatient();
        }
        else if (msg instanceof OrganizationOrchestratorActor.ResolveOrganizationResponse){
            responseOrganization =((OrganizationOrchestratorActor.ResolveOrganizationResponse)msg).getResponseObject();
            //finalizePatientRequest(responseObject);
            //finalizeOrganizationRequest(responseOrganization);
            //mainFinalize();
            mainFinalizeOrganization();
        }
        else if (msg instanceof SpecimenOrchestratorActor.ResolveSpecimenResponse){
            responseSpecimen =((SpecimenOrchestratorActor.ResolveSpecimenResponse)msg).getResponseObject();
            //finalizePatientRequest(responseObject);
            //finalizeOrganizationRequest(responseOrganization);
            mainFinalizeSpecimen();
        }
        else if (msg instanceof ConditionOrchestratorActor.ResolveConditionResponse){
            responseCondition =((ConditionOrchestratorActor.ResolveConditionResponse)msg).getResponseObject();
            //finalizePatientRequest(responseObject);
            //finalizeOrganizationRequest(responseOrganization);
            mainFinalizeCondition();
        }
        else if (msg instanceof DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderResponse){
            responseDiagnosticOrder =((DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderResponse)msg).getResponseObject();
            //finalizePatientRequest(responseObject);
            //finalizeOrganizationRequest(responseOrganization);
            mainFinalizeDiagnosticOrder();
        }
        else if (msg instanceof ObservationOrchestratorActor.ResolveObservationResponse){
            responseObservation =((ObservationOrchestratorActor.ResolveObservationResponse)msg).getResponseObject();
            mainFinalizeObservation();
        }
        else if (msg instanceof DiagnosticReportOrchestratorActor.ResolveDiagnosticReportResponse){
            responseDiagnosticReport =((DiagnosticReportOrchestratorActor.ResolveDiagnosticReportResponse)msg).getResponseObject();
            mainFinalizeDiagnosticReport();
        }
        else if (msg instanceof ListResourceOrchestratorActor.ResolveListResourceResponse){
            responseListResource =((ListResourceOrchestratorActor.ResolveListResourceResponse)msg).getResponseObject();
            mainFinalizeListResource();
        }
        else if (msg instanceof BasicOrchestratorActor.ResolveBasicResponse){
            responseBasic =((BasicOrchestratorActor.ResolveBasicResponse)msg).getResponseObject();
            mainFinalizeBasic();
        }
        else
        {
            unhandled(msg);
        }
    }
}
