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

    private final MediatorConfig config;
    private MediatorFhirConfig mediatorConfiguration;
    private SynDateConfigFile syncConfiguration;
    private MediatorHTTPRequest originalRequest;
    private String solvedPractitionerResponse;
    private List<String> listSolvedPractitionerResponse;
    private List<String> listSolvedPatientResponse;
    private List<String> listSolvedOrganizationResponse;
    private List<String> listSolvedSpecimenResponse;
    private List<String> listSolvedDiagnosticOrderResponse;
    private List<String> listSolvedObservationResponse;
    private List<String> listSolvedDiagnosticReportResponse;
    private List<Practitioner> listOfValidPractitioner;
    private List<Patient> listOfValidPatient;
    private List<Specimen> listOfValidSpecimen;
    private List<Organization> listOfValidOrganization;
    private List<DiagnosticOrder> listOfValidDiagnosticOrder;
    private List<Observation> listOfValidObservation;
    private List<DiagnosticReport> listOfValidDiagnosticReport;
    private List<Practitioner> listOfPractitionerToUpdate;
    private List<Patient> listOfPatientToUpdate;
    private List<Organization> listOfOrganizationToUpdate;
    private List<Specimen> listOfSpecimenToUpdate;
    private List<DiagnosticOrder> listOfDiagnosticOrderToUpdate;
    private List<Observation> listOfObservationToUpdate;
    private List<DiagnosticReport> listOfDiagnosticReportToUpdate;
    private List<Practitioner> listOfPractitionerToAdd;
    private List<Patient> listOfPatientToAdd;
    private List<Organization> listOfOrganizationToAdd;
    private List<Specimen> listOfSpecimenToAdd;
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
    String responseDiagnosticOrder=null;
    String responseObservation=null;
    String responseDiagnosticReport=null;


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
        listSolvedOrganizationResponse=new ArrayList<>();
        listSolvedSpecimenResponse=new ArrayList<>();
        listSolvedDiagnosticOrderResponse=new ArrayList<>();
        listSolvedObservationResponse=new ArrayList<>();
        listSolvedDiagnosticReportResponse=new ArrayList<>();
        listOfValidPractitioner=new ArrayList<>();
        listOfValidPatient=new ArrayList<>();
        listOfValidOrganization=new ArrayList<>();
        listOfValidSpecimen=new ArrayList<>();
        listOfValidDiagnosticOrder=new ArrayList<>();
        listOfValidObservation=new ArrayList<>();
        listOfValidDiagnosticReport=new ArrayList<>();
        listOfPractitionerToUpdate=new ArrayList<>();
        listOfOrganizationToUpdate=new ArrayList<>();
        listOfPatientToUpdate=new ArrayList<>();
        listOfSpecimenToUpdate=new ArrayList<>();
        listOfDiagnosticOrderToUpdate=new ArrayList<>();
        listOfObservationToUpdate=new ArrayList<>();
        listOfDiagnosticReportToUpdate=new ArrayList<>();
        listOfPractitionerToAdd=new ArrayList<>();
        listOfPatientToAdd=new ArrayList<>();
        listOfOrganizationToAdd=new ArrayList<>();
        listOfSpecimenToAdd=new ArrayList<>();
        listOfDiagnosticOrderToAdd=new ArrayList<>();
        listOfObservationToAdd=new ArrayList<>();
        listOfDiagnosticReportToAdd=new ArrayList<>();
        nbrOfSearchRequestToWaitFor=0;
        listIdsPractitionerUsedForSearch=new ArrayList<>();
        listIdsPatientUsedForSearch=new ArrayList<>();
        listIdsOrganizationUsedForSearch=new ArrayList<>();
        listIdsSpecimenUsedForSearch=new ArrayList<>();
        listIdsDiagnosticOrderUsedForSearch=new ArrayList<>();
        listIdsObservationUsedForSearch=new ArrayList<>();
        listIdsDiagnosticReportUsedForSearch=new ArrayList<>();
        listMapIdentifierUsedForUpdate=new ArrayList<>();
        listMapIdentifierdForUpdate=new ArrayList<>();
        listIdentifiedPractitionerAndIdForUpdateSource=new HashMap<>();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logResult=simpleDateFormat.format(new Date()).toString()+"::";
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
                int nbrRetreivedDiagnosticOrder=resourceBundle.getListOfDiagnosticOrder().size();
                int nbrRetreivedDiagnosticReport=resourceBundle.getListOfDiagnosticReport().size();
                int nbrRetreivedObservation=resourceBundle.getListOfObservation().size();

                //Valid tracked entities after the bundle processing
                int nbrValidPractitioner=resourceBundle.getListOfValidePractitioners().size();
                int nbrValidPatient=resourceBundle.getListOfValidPatient().size();
                int nbrValidOrganization=resourceBundle.getListOfValidOrganization().size();
                int nbrValidSpecimen=resourceBundle.getListOfValidSpecimen().size();
                int nbrValidDiagnosticOrder=resourceBundle.getListOfValidDiagnosticOrder().size();
                int nbrValidDiagnosticReport=resourceBundle.getListOfValidDiagnosticReport().size();
                int nbrValidPObservation=resourceBundle.getListOfValidObservation().size();

                //InValid tracked entities after the bundle processing
                int nbreInvalidPractitioner=resourceBundle.getListOfInvalidePractitioners().size();
                int nbreInvalidPatient=resourceBundle.getListOfInvalidPatient().size();
                int nbreInvalidOrganization=resourceBundle.getListOfInvalidOrganization().size();
                int nbreInvalidSpecimen=resourceBundle.getListOfInvalidSpecimen().size();
                int nbreInvalidDiagnosticOrder=resourceBundle.getListOfInvalidDiagnosticOrder().size();
                int nbreInvalidDiagnosticReport=resourceBundle.getListOfInvalidDiagnosticReport().size();
                int nbreInvalidObservation=resourceBundle.getListOfInvalidObservation().size();

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
                ;

                PractitionerOrchestratorActor.ResolvePractitionerRequest PractitionerRequest =null;
                PatientOrchestratorActor.ResolvePatientRequest patientRequest =null;
                OrganizationOrchestratorActor.ResolveOrganizationRequest organizationRequest=null;
                SpecimenOrchestratorActor.ResolveSpecimenRequest specimenRequest=null;
                DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderRequest diagnosticOrderRequest;
                ObservationOrchestratorActor.ResolveObservationRequest observationRequest;
                DiagnosticReportOrchestratorActor.ResolveDiagnosticReportRequest diagnosticReportRequest;
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
                if(resourceBundle.getListOfValidePractitioners().size()>0)
                {
                    this.listOfValidPractitioner=resourceBundle.getListOfValidePractitioners();
                    nbrOfSearchRequestToWaitFor=resourceBundle.getListOfValidePractitioners().size();
                    List<String> listOfId=new ArrayList<>();
                    for(Practitioner oPractitionerToIdentify:resourceBundle.getListOfValidePractitioners())
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
                if(resourceBundle.getListOfValidPatient().size()>0)
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
                if(resourceBundle.getListOfValidSpecimen().size()>0)
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
                if(resourceBundle.getListOfValidDiagnosticOrder().size()>0)
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
                if(resourceBundle.getListOfValidObservation().size()>0)
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
                if(resourceBundle.getListOfValidDiagnosticReport().size()>0)
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
            log.error(exc.getMessage());
            logResult+="::error:"+exc.getMessage();
            originalRequest.getRespondTo().tell(logResult, getSelf());
            return;
        }

    }
    private void finalizeRequest(String practitionerResponse) {


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
                identifyPractitionerToUpdate(listSolvedPractitionerResponse);//Always run update identification before the add identification
                identifyPractitionerToAdd();
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
                //Identify List of Patient to add and to Update
                identifyPatientToUpdate(listSolvedPatientResponse);//Always run update identification before the add identification
                identifyPatientToAdd();
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
                //resultOutPutHeader+="res:";
                if(listOfPatientToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createPatient(resourceBundle.getContext(),
                            this.listOfPatientToAdd,
                            baseServerRepoURI);
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
                //Identify List of Patient to add and to Update
                identifyOrganizationToUpdate(listSolvedOrganizationResponse);//Always run update identification before the add identification
                identifyOrganizationToAdd();
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
                //resultOutPutHeader+="res:";
                if(listOfOrganizationToAdd.size()>0){
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
                if(this.listOfOrganizationToUpdate.size()>0)
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
                //Identify List of Patient to add and to Update
                identifySpecimenToUpdate(listSolvedSpecimenResponse);//Always run update identification before the add identification
                identifySpecimenToAdd();
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
                //resultOutPutHeader+="res:";
                if(listOfSpecimenToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createSpecimen(resourceBundle.getContext(),
                            this.listOfSpecimenToAdd,
                            baseServerRepoURI);
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
                //Identify List of Patient to add and to Update
                identifyDiagnosticOrderToUpdate(listSolvedDiagnosticOrderResponse);//Always run update identification before the add identification
                identifyDiagnosticOrderToAdd();
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
                //resultOutPutHeader+="res:";
                if(listOfDiagnosticOrderToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createDiagnosticOrder(resourceBundle.getContext(),
                            this.listOfDiagnosticOrderToAdd,
                            baseServerRepoURI);
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
                //Identify List of Patient to add and to Update
                identifyObservationToUpdate(listSolvedObservationResponse);//Always run update identification before the add identification
                identifyObservationToAdd();
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
                //resultOutPutHeader+="res:";
                if(listOfObservationToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createObservation(resourceBundle.getContext(),
                            this.listOfObservationToAdd,
                            baseServerRepoURI);
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
                //Identify List of Patient to add and to Update
                identifyDiagnosticReportToUpdate(listSolvedDiagnosticReportResponse);//Always run update identification before the add identification
                identifyDiagnosticReportToAdd();
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
                //resultOutPutHeader+="res:";
                if(listOfDiagnosticReportToAdd.size()>0){
                    resultInsertion =FhirResourceProcessor.createDiagnosticReport(resourceBundle.getContext(),
                            this.listOfDiagnosticReportToAdd,
                            baseServerRepoURI);
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
                || responseSpecimen==null || responseObservation==null || responseDiagnosticReport==null)
        {
            return ;
        }
        else
        {
            resultOutPutHeader+="res:[";
            finalizeOrganizationRequest(responseOrganization);
            finalizeRequest(responsePractitioner);
            finalizePatientRequest(responsePatient);
            finalizeSpecimenRequest(responseSpecimen);
            finalizeDiagnosticOrderRequest(responseDiagnosticOrder);
            finalizeObservationRequest(responseObservation);
            finalizeDiagnosticReportRequest(responseDiagnosticReport);
            FinishRequest _fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            originalRequest.getRespondTo().tell(_fr, getSelf());
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

    void identifyPractitionerToUpdate(List<String> bundleSearchResultSet)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Practitioner oPractitioner :resourceBundle.extractPractitionerFromBundleString(oBundleSearchResult))
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
    void identifyPatientToUpdate(List<String> bundleSearchResultSet)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Patient oPatient :resourceBundle.extractPatientFromBundleString(oBundleSearchResult))
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
    void identifyOrganizationToUpdate(List<String> bundleSearchResultSet)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Organization oOrganization :resourceBundle.extractOrganizationFromBundleString(oBundleSearchResult))
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
    void identifySpecimenToUpdate(List<String> bundleSearchResultSet)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Specimen oSpecimen :resourceBundle.extractSpecimenFromBundleString(oBundleSearchResult))
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
    void identifyDiagnosticOrderToUpdate(List<String> bundleSearchResultSet)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (DiagnosticOrder oDiagnosticOrder :resourceBundle.extractDiagnosticOrderFromBundleString(oBundleSearchResult))
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
    void identifyObservationToUpdate(List<String> bundleSearchResultSet)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (Observation oObservation :resourceBundle.extractObservationFromBundleString(oBundleSearchResult))
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
    void identifyDiagnosticReportToUpdate(List<String> bundleSearchResultSet)
    {
        //List<Practitioner> ListIdentifiedForUpdateTarget=new ArrayList<>();
        try
        {
            for (String oBundleSearchResult:bundleSearchResultSet)
            {
                for (DiagnosticReport oDiagnosticReport :resourceBundle.extractDiagnosticReportFromBundleString(oBundleSearchResult))
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
            for(Specimen oSpecimen  : this.listOfValidSpecimen)
            {
                boolean isToDiscard=false;
                if(listOfIdsForUpdate.contains(oSpecimen.getId().getIdPart())==false)
                {
                    this.listOfSpecimenToAdd.add(oSpecimen);
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
            mainFinalize();
        }
        else if (msg instanceof PatientOrchestratorActor.ResolvePatientResponse){
            responsePatient =((PatientOrchestratorActor.ResolvePatientResponse)msg).getResponseObject();
            //finalizePatientRequest(responsePatient);
            mainFinalize();
        }
        else if (msg instanceof OrganizationOrchestratorActor.ResolveOrganizationResponse){
            responseOrganization =((OrganizationOrchestratorActor.ResolveOrganizationResponse)msg).getResponseObject();
            //finalizePatientRequest(responseObject);
            //finalizeOrganizationRequest(responseOrganization);
            mainFinalize();
        }
        else if (msg instanceof SpecimenOrchestratorActor.ResolveSpecimenResponse){
            responseSpecimen =((SpecimenOrchestratorActor.ResolveSpecimenResponse)msg).getResponseObject();
            //finalizePatientRequest(responseObject);
            //finalizeOrganizationRequest(responseOrganization);
            mainFinalize();
        }
        else if (msg instanceof DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderResponse){
            responseDiagnosticOrder =((DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderResponse)msg).getResponseObject();
            //finalizePatientRequest(responseObject);
            //finalizeOrganizationRequest(responseOrganization);
            mainFinalize();
        }
        else if (msg instanceof ObservationOrchestratorActor.ResolveObservationResponse){
            responseObservation =((ObservationOrchestratorActor.ResolveObservationResponse)msg).getResponseObject();
            mainFinalize();
        }
        else if (msg instanceof DiagnosticReportOrchestratorActor.ResolveDiagnosticReportResponse){
            responseDiagnosticReport =((DiagnosticReportOrchestratorActor.ResolveDiagnosticReportResponse)msg).getResponseObject();
            mainFinalize();
        }
        else
        {
            unhandled(msg);
        }
    }
}
