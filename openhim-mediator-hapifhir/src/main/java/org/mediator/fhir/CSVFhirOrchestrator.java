package org.mediator.fhir;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ca.uhn.fhir.model.dstu2.resource.*;
import org.apache.http.HttpStatus;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;

import java.text.SimpleDateFormat;
import java.util.*;

//import org.hl7.fhir.dstu3.model.Identifier;
//import org.hl7.fhir.dstu3.model.Practitioner;

public class CSVFhirOrchestrator extends UntypedActor {
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
    private List<Observation> listOfValidObservation;
    private List<DiagnosticReport> listOfValidDiagnosticReport;
    private List<Bundle> listOfValidBundle;
    private List<Practitioner> listOfPractitionerToUpdate;
    private List<Patient> listOfPatientToUpdate;
    private List<Organization> listOfOrganizationToUpdate;
    private List<Specimen> listOfSpecimenToUpdate;
    private List<Condition> listOfConditionToUpdate;
    private List<DiagnosticOrder> listOfDiagnosticOrderToUpdate;
    private List<Observation> listOfObservationToUpdate;
    private List<DiagnosticReport> listOfDiagnosticReportToUpdate;
    private List<Practitioner> listOfPractitionerToAdd;
    private List<Patient> listOfPatientToAdd;
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


    public CSVFhirOrchestrator(MediatorConfig config) {
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
        listOfValidBundle=new ArrayList<>();
        listOfPractitionerToUpdate=new ArrayList<>();
        listOfOrganizationToUpdate=new ArrayList<>();
        listOfPatientToUpdate=new ArrayList<>();
        listOfSpecimenToUpdate=new ArrayList<>();
        listOfConditionToUpdate=new ArrayList<>();
        listOfDiagnosticOrderToUpdate=new ArrayList<>();
        listOfObservationToUpdate=new ArrayList<>();
        listOfDiagnosticReportToUpdate=new ArrayList<>();
        listOfPractitionerToAdd=new ArrayList<>();
        listOfPatientToAdd=new ArrayList<>();
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
        listIdsDiagnosticOrderUsedForSearch=new ArrayList<>();
        listIdsObservationUsedForSearch=new ArrayList<>();
        listIdsDiagnosticReportUsedForSearch=new ArrayList<>();
        listMapIdentifierUsedForUpdate=new ArrayList<>();
        listMapIdentifierdForUpdate=new ArrayList<>();
        listIdentifiedPractitionerAndIdForUpdateSource=new HashMap<>();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        logResult=simpleDateFormat.format(new Date()).toString()+"::";
    }

    private void queryCSVFhirResources(MediatorHTTPRequest request)
    {
        originalRequest = request;
        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        Map <String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        String resourceInformation="FHIR CSV resources";
        log.info("Querying the CSV Fhir service");



        //builtRequestPath="/baseDstu3/Practitioner?_lastUpdated=>=2016-10-11T09:12:37&_lastUpdated=<=2016-10-13T09:12:45&_pretty=true";

        String ServerApp=mediatorConfiguration.getServerSourceDataAppName().equals("null")?null:mediatorConfiguration.getServerSourceDataAppName();

        baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                this.mediatorConfiguration.getServerSourceDataScheme(),
                this.mediatorConfiguration.getServerSourceDataURI(),
                this.mediatorConfiguration.getServerSourceDataPort(),
                ServerApp,
                this.mediatorConfiguration.getServerSourceDataFhirDataModel()
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

    private void processCSVFhirServiceResponse(MediatorHTTPResponse response) {
        log.info("Received response CSV Fhir service");
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
                listOfValidBundle=resourceBundle.processBundleResource(pathOfErrorResource,baseServerRepoURI);
                finalizeCsvFhirRequest(listOfValidBundle);


                FinishRequest fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
                //originalRequest.getRespondTo().tell(logResult, getSelf());
                //originalRequest.getRespondTo().tell(fr, getSelf());

            }
            else {
                logResult+="::warning:"+response.toFinishRequest().toString();
                //originalRequest.getRespondTo().tell(response.toFinishRequest(), getSelf());
                //originalRequest.getRespondTo().tell(logResult, getSelf());

            }

        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                    exc.getMessage(),"Error");
            log.error(exc.getMessage());
            logResult+="::error:"+exc.getMessage();

            //return;
        }
        finally {
            originalRequest.getRespondTo().tell(logResult, getSelf());
        }

    }
    private void finalizeCsvFhirRequest(List<Bundle> listBundle2Process)
    {
        FinishRequest fr =null;
        try
        {
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

            resultInsertion =FhirResourceProcessor.createBundle(resourceBundle.getContext(),
                    listBundle2Process,
                    baseServerRepoURI);
            logResult+=resultInsertion;
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logResult+="::"+simpleDateFormat.format(new Date()).toString()+":";
            logResult+="Operation completed!";
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
    }
    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            queryCSVFhirResources((MediatorHTTPRequest) msg);
        } else if (msg instanceof MediatorHTTPResponse){
            processCSVFhirServiceResponse((MediatorHTTPResponse) msg);
            //finalizeRequest(null);
        }
        else
        {
            unhandled(msg);
        }
    }
}
