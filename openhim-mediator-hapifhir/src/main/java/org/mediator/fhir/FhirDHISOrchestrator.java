package org.mediator.fhir;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.*;
import com.phloc.commons.base64.*;
import org.apache.http.HttpStatus;
import org.openhim.mediator.engine.MediatorConfig;
import org.openhim.mediator.engine.messages.FinishRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPRequest;
import org.openhim.mediator.engine.messages.MediatorHTTPResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

//import org.hl7.fhir.dstu3.model.Identifier;
//import org.hl7.fhir.dstu3.model.Practitioner;

public class FhirDHISOrchestrator extends UntypedActor {
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
    String responseDhisEvent=null;
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


    public FhirDHISOrchestrator(MediatorConfig config) {
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

    private void queryFhirBundleResources(MediatorHTTPRequest request)
    {
        originalRequest = request;
        ActorSelection httpConnector = getContext().actorSelection(config.userPathFor("http-connector"));
        Map <String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        String resourceInformation="FHIR CSV resources";
        log.info("Querying the CSV Fhir service");



        //builtRequestPath="/baseDstu3/Practitioner?_lastUpdated=>=2016-10-11T09:12:37&_lastUpdated=<=2016-10-13T09:12:45&_pretty=true";

        String ServerApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
        baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                this.mediatorConfiguration.getServerTargetscheme(),
                this.mediatorConfiguration.getServerTargetURI(),
                this.mediatorConfiguration.getServerTargetPort(),
                ServerApp,
                this.mediatorConfiguration.getServerTargetFhirDataModel()
        );
        String uriRepServer=baseServerRepoURI+FhirMediatorUtilities.buildBundleSearchRequestByType();

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

    private void processFhirBundleResourcesResponse(MediatorHTTPResponse response) {
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
                List<Bundle> tempListOfBundle=resourceBundle.processBundleResourceContent(pathOfErrorResource,baseServerRepoURI);
                //finalizeCsvFhirRequest(listOfValidBundle);
                listOfValidBundle.addAll(tempListOfBundle);
                for(Bundle oBundle: listOfValidBundle)
                {
                    //resourceBundle.set_jsonResource();
                    boolean resProcessing= resourceBundle.processBundleEntryResource(oBundle,
                            pathOfErrorResource,baseServerRepoURI);
                    if(resProcessing==false)
                    {
                        FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                                "Failed to process the bundle resource with id"+oBundle.getId().getIdPart()
                                ,"Error");
                        continue;
                    }
                    else
                    {

                        int nbrRetreivedPractitioner=resourceBundle.getListOfPractitioners().size();
                        int nbrRetreivedPatient=resourceBundle.getListOfPatient().size();
                        int nbrRetreivedOrganization=resourceBundle.getListOfOrganization().size();
                        int nbrRetreivedSpecimen=resourceBundle.getListOfSpecimen().size();
                        int nbrRetreivedCondition=resourceBundle.getListOfCondition().size();
                        int nbrRetreivedDiagnosticOrder=resourceBundle.getListOfDiagnosticOrder().size();
                        int nbrRetreivedDiagnosticReport=resourceBundle.getListOfDiagnosticReport().size();
                        int nbrRetreivedObservation=resourceBundle.getListOfObservation().size();

                        //Valid tracked entities after the bundle processing
                        int nbrValidPractitioner=resourceBundle.getListOfValidePractitioners().size();
                        int nbrValidPatient=resourceBundle.getListOfValidPatient().size();
                        int nbrValidOrganization=resourceBundle.getListOfValidOrganization().size();
                        int nbrValidSpecimen=resourceBundle.getListOfValidSpecimen().size();
                        int nbrValidCondition=resourceBundle.getListOfValidCondition().size();
                        int nbrValidDiagnosticOrder=resourceBundle.getListOfValidDiagnosticOrder().size();
                        int nbrValidDiagnosticReport=resourceBundle.getListOfValidDiagnosticReport().size();
                        int nbrValidPObservation=resourceBundle.getListOfValidObservation().size();

                        //InValid tracked entities after the bundle processing

                        PatientOrchestratorActor.ResolvePatientRequest patientRequest =null;
                        SpecimenOrchestratorActor.ResolveSpecimenRequest specimenRequest=null;
                        ConditionOrchestratorActor.ResolveConditionRequest conditionRequest=null;
                        DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderRequest diagnosticOrderRequest=null;
                        ObservationOrchestratorActor.ResolveObservationRequest observationRequest=null;
                        DiagnosticReportOrchestratorActor.ResolveDiagnosticReportRequest diagnosticReportRequest=null;

                        this.listOfValidSpecimen.addAll(resourceBundle.getListOfValidSpecimen());
                        this.listOfValidCondition.addAll(resourceBundle.getListOfValidCondition());
                        this.listOfValidDiagnosticOrder.addAll(resourceBundle.getListOfValidDiagnosticOrder());
                        this.listOfValidObservation.addAll(resourceBundle.getListOfValidObservation());
                        this.listOfValidDiagnosticReport.addAll(resourceBundle.getListOfValidDiagnosticReport());
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
                        else
                        {
                            responsePatient=nullResponse;
                        }

                    }

                }
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
            HttpURLConnection connection =null;
            try
            {
                //finish
                System.out.println(listSolvedPatientResponse.size());
                //Identify List of Patient to add and to Update
                identifyPatientToUpdate(listSolvedPatientResponse);//Always run update identification before the add identification

                //from the original list of Practitioner found, extract the rest of Practitioner to add
                String ServerApp="";
                String baseServerRepoURI="";
                ServerApp=mediatorConfiguration.getServerTrackerAppName().equals("null")?null:mediatorConfiguration.getServerTrackerAppName();
                baseServerRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTrackerScheme(),
                        this.mediatorConfiguration.getServerTrackerURI(),
                        this.mediatorConfiguration.getServerTrackerPort(),
                        ServerApp,
                        this.mediatorConfiguration.getServerTrackerFhirDataModel()
                );
                List<TrackerResourceMap> listTrackerResource=this.mediatorConfiguration.getTrackerResource();
                String  resultUpdate=null;
                if(this.listOfPatientToUpdate.size()>0)
                {
                    List<DhisEvent> listOfEventToAdd=new ArrayList<>();
                   for(Patient oPatient:listOfPatientToUpdate)
                    {
                        List<Specimen> listAssociatedSpecimen=new ArrayList<>();
                        List<Condition> listAssociatedCondition=new ArrayList<>();
                        List<DiagnosticOrder> listAssociatedDiagnosticOrder=new ArrayList<>();
                        List<Observation> listAssociatedObservation=new ArrayList<>();
                        List<DiagnosticReport> listAssociatedDiagnosticReport=new ArrayList<>();

                        for(Specimen oSpecimen: listOfValidSpecimen)
                        {
                            String  refPatientId= oSpecimen.getSubject().getReference().getIdPart();
                            if(oPatient.getId().getIdPart().equals(refPatientId))
                            {
                                listAssociatedSpecimen.add(oSpecimen);
                            }
                        }
                        for(Condition oCondition: listOfValidCondition)
                        {
                            String refPatientId=oCondition.getPatient().getReference().getIdPart();
                            if(oPatient.getId().getIdPart().equals(refPatientId))
                            {
                                listAssociatedCondition.add(oCondition);
                            }
                        }
                        for(DiagnosticOrder oDiagnosticOrder: listOfValidDiagnosticOrder)
                        {
                            String refPatientId=oDiagnosticOrder.getSubject().getReference().getIdPart();
                            if(oPatient.getId().getIdPart().equals(refPatientId))
                            {
                                listAssociatedDiagnosticOrder.add(oDiagnosticOrder);
                            }
                        }
                        for(Observation oObservation: listOfValidObservation)
                        {
                            String refPatientId=oObservation.getSubject().getReference().getIdPart();
                            if(oPatient.getId().getIdPart().equals(refPatientId))
                            {
                                listAssociatedObservation.add(oObservation);
                            }
                        }
                        for(DiagnosticReport oDiagnosticReport: listOfValidDiagnosticReport)
                        {
                            String refPatientId=oDiagnosticReport.getSubject().getReference().getIdPart();
                            if(oPatient.getId().getIdPart().equals(refPatientId))
                            {
                                listAssociatedDiagnosticReport.add(oDiagnosticReport);
                            }
                        }
                        List<DhisEvent> res=FhirMediatorUtilities.constructListOfLabEvents(
                                oPatient,listAssociatedSpecimen,listAssociatedObservation,
                                listTrackerResource);
                        listOfEventToAdd.addAll(res);
                        String trackedEntityId= this.mediatorConfiguration.getTrackedEntity();
                        List<DhisTrackedEntity> resEntityInstance=FhirMediatorUtilities.constructListOfTrackedEntityInstance(
                                oPatient,trackedEntityId,listAssociatedCondition,listAssociatedDiagnosticReport
                        );
                        String trackedEntity="";
                        for(IdentifierDt oIdentifier:oPatient.getIdentifier())
                        {
                            if(oIdentifier.getType().getText().equals("Tracker identifier"))
                            {
                                trackedEntity=oIdentifier.getValue();
                            }
                        }
                        connection=null;
                        String builtRequestPath="";
                        builtRequestPath="/trackedEntityInstances/"+trackedEntity;
                        String trackedEntityJsonString=FhirMediatorUtilities.TransformListTrackedEntityToJson(
                                resEntityInstance
                        );
                        String uriRepServer=baseServerRepoURI+builtRequestPath;

                        String authentication=mediatorConfiguration.getAuthentication();
                        String authEncoded = com.phloc.commons.base64.Base64.encodeBytes(authentication.getBytes());

                        //Handle connection exception
                        try
                        {
                            URL url = new URL(uriRepServer);
                            connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestProperty("Authorization", "Basic " + authEncoded);
                            connection.setRequestMethod("PUT");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);
                            connection.setRequestProperty("Content-Type","application/json");
                            connection.setUseCaches (false);
                            DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
                            wr.writeBytes (trackedEntityJsonString);
                            wr.flush ();
                            wr.close ();
                            //Get Response
                            InputStream content=(InputStream)connection.getInputStream();
                            BufferedReader rd=new BufferedReader (new InputStreamReader(content));
                            StringBuffer response = new StringBuffer();
                            String line;
                            while ((line = rd.readLine()) != null) {
                                response.append(line);
                                response.append('\r');
                            }
                            rd.close();
                            String result= response.toString();


                        }
                        catch (Exception exc)
                        {
                            FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                                    exc.getMessage(),"Error");
                        }
                        finally {

                            if(connection != null) {
                                connection.disconnect();
                            }
                        }

                        System.out.println(0);


                    }//End for Patient
                    if(listOfEventToAdd.size()>0)
                    {
                        String eventJsonString=FhirMediatorUtilities.TransformListEventToJson(listOfEventToAdd);

                        /*

                        ActorRef dhis2EventRequestOrchestrator=getContext().actorOf(
                                Props.create(EventsOrchestratorActor.class,config));
                        dhis2EventRequestOrchestrator.tell(dhis2EventRequest,getSelf());
                        */
                        connection=null;
                        String builtRequestPath="";
                        builtRequestPath="/events";
                        //builtRequestPath="/dhis-web-commons/security/login.action";
                        String uriRepServer=baseServerRepoURI+builtRequestPath;
                        String authentication=mediatorConfiguration.getAuthentication();
                        String authEncoded = com.phloc.commons.base64.Base64.encodeBytes(authentication.getBytes());
                        URL url= new URL(uriRepServer);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Authorization","Basic "+authEncoded);
                        //connection=null;

                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setRequestProperty("Content-Type","application/json");
                        connection.setUseCaches (false);
                        //connection.set
                        //Send request
                        DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
                        wr.writeBytes (eventJsonString);
                        wr.flush ();
                        wr.close ();

                        //Get Response
                        InputStream content=(InputStream)connection.getInputStream();
                        BufferedReader rd=new BufferedReader (new InputStreamReader(content));
                        StringBuffer response = new StringBuffer();
                        String line;
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('\r');
                        }
                        rd.close();
                        String result= response.toString();

                        System.out.println("Traratataa!");

                    }

                }//End of this.listOfPatientToUpdate.size


                resultOutPutHeader+=resultOutPutTail+",";
                logResult+=resultOutPutHeader;
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
            finally {

                if(connection != null) {
                    connection.disconnect();
                }
            }

            //originalRequest.getRespondTo().tell(fr, getSelf());
            //stopRequestProcessing(fr);
        }

        //return;

    }
    private void mainFinalize() {
        if(responsePatient ==null)
        {
            return ;
        }
        else
        {
            resultOutPutHeader+="res:[";
            finalizePatientRequest(responsePatient);
            FinishRequest _fr = new FinishRequest(logResult, "text/plain", HttpStatus.SC_OK);
            originalRequest.getRespondTo().tell(_fr, getSelf());
        }
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
                        this.listOfPatientToUpdate.add(oPatient);
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

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof MediatorHTTPRequest) {
            queryFhirBundleResources((MediatorHTTPRequest) msg);
        } else if (msg instanceof MediatorHTTPResponse){
            processFhirBundleResourcesResponse((MediatorHTTPResponse) msg);
            //finalizeRequest(null);
        }
        else if (msg instanceof PatientOrchestratorActor.ResolvePatientResponse){
            responsePatient =((PatientOrchestratorActor.ResolvePatientResponse)msg).getResponseObject();
            //finalizePatientRequest(responsePatient);
            mainFinalize();
        }
        else if (msg instanceof EventsOrchestratorActor.ResolveEventResponse){
            responseDhisEvent =((EventsOrchestratorActor.ResolveEventResponse)msg).getResponseObject();
            //finalizePatientRequest(responsePatient);
            //mainFinalize();
        }
        else
        {
            unhandled(msg);
        }
    }
}
