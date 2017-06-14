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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
    private List<ListResource> listOfValidListResource;
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

    TrustManager[] trustAllCerts=new TrustManager[]
    {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    //return new X509Certificate[0];
                    return null;
                }

            }

    };
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
        listOfValidListResource=new ArrayList<>();
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
                    else {

                        int nbrRetreivedPractitioner = resourceBundle.getListOfPractitioners().size();
                        int nbrRetreivedPatient = resourceBundle.getListOfPatient().size();
                        int nbrRetreivedOrganization = resourceBundle.getListOfOrganization().size();
                        int nbrRetreivedSpecimen = resourceBundle.getListOfSpecimen().size();
                        int nbrRetreivedCondition = resourceBundle.getListOfCondition().size();
                        int nbrRetreivedDiagnosticOrder = resourceBundle.getListOfDiagnosticOrder().size();
                        int nbrRetreivedDiagnosticReport = resourceBundle.getListOfDiagnosticReport().size();
                        int nbrRetreivedObservation = resourceBundle.getListOfObservation().size();
                        int nbrRetreivedListResource=resourceBundle.getListOfListResource().size();

                        //Valid tracked entities after the bundle processing
                        int nbrValidPractitioner = resourceBundle.getListOfValidPractitioners().size();
                        int nbrValidPatient = resourceBundle.getListOfValidPatient().size();
                        int nbrValidOrganization = resourceBundle.getListOfValidOrganization().size();
                        int nbrValidSpecimen = resourceBundle.getListOfValidSpecimen().size();
                        int nbrValidCondition = resourceBundle.getListOfValidCondition().size();
                        int nbrValidDiagnosticOrder = resourceBundle.getListOfValidDiagnosticOrder().size();
                        int nbrValidDiagnosticReport = resourceBundle.getListOfValidDiagnosticReport().size();
                        int nbrValidPObservation = resourceBundle.getListOfValidObservation().size();
                        int nbrValidListResource=resourceBundle.getListOfValidListResource().size();

                    }//End of else resProcessing
                }//End of for Bundle
                FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                        "CSV Bundle resources request succeded","Notice");
                this.listOfValidPatient.addAll(resourceBundle.getListOfValidPatient());
                this.listOfValidPractitioner.addAll(resourceBundle.getListOfValidPractitioners());
                this.listOfValidSpecimen.addAll(resourceBundle.getListOfValidSpecimen());
                this.listOfValidCondition.addAll(resourceBundle.getListOfValidCondition());
                this.listOfValidDiagnosticOrder.addAll(resourceBundle.getListOfValidDiagnosticOrder());
                this.listOfValidObservation.addAll(resourceBundle.getListOfValidObservation());
                this.listOfValidDiagnosticReport.addAll(resourceBundle.getListOfValidDiagnosticReport());
                this.listOfValidListResource.addAll(resourceBundle.getListOfValidListResource());

                PatientOrchestratorActor.ResolvePatientRequest patientRequest =null;
                SpecimenOrchestratorActor.ResolveSpecimenRequest specimenRequest=null;
                ConditionOrchestratorActor.ResolveConditionRequest conditionRequest=null;
                DiagnosticOrderOrchestratorActor.ResolveDiagnosticOrderRequest diagnosticOrderRequest=null;
                ObservationOrchestratorActor.ResolveObservationRequest observationRequest=null;
                DiagnosticReportOrchestratorActor.ResolveDiagnosticReportRequest diagnosticReportRequest=null;

                if(this.listOfValidPatient.size()>0)
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
    private void addUniquePatientInList(Patient patientToAdd)
    {
        boolean found=false;
        for(Patient oPatient:listOfValidPatient)
        {
            if(oPatient.getId().getIdPart().equals(patientToAdd.getId().getIdPart()))
            {
                found=true;
            }
        }
        if(found==false)
        {
            this.listOfValidPatient.add(patientToAdd);
        }

    }
    private void addUniqueSpecimenInList(Specimen specimenToAdd)
    {
        boolean found=false;
        for(Specimen oSpecimen:listOfValidSpecimen)
        {
            if(oSpecimen.getId().getIdPart().equals(specimenToAdd.getId().getIdPart()))
            {
                found=true;
            }
        }
        if(found==false)
        {
            this.listOfValidSpecimen.add(specimenToAdd);
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
    private List<ListResource> getListResourceFromItemReference(String resourceType,String resourceId)
    {
        List<ListResource> listListResourceFound=new ArrayList<>();
        for(ListResource oListResource: listOfValidListResource)
        {
            String _resourceType=oListResource.getEntry().get(0).getItem().getReference().getResourceType();
            String _resourceId=oListResource.getEntry().get(0).getItem().getReference().getIdPart();
            if(_resourceType.equals(resourceType) && _resourceId.equals(resourceId))
            {
                listListResourceFound.add(oListResource);
            }
        }
        return listListResourceFound;
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
            //HttpURLConnection connection =null;
            HttpsURLConnection connection=null;

            try
            {
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new java.security.SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                //finish
                System.out.println(listSolvedPatientResponse.size());
                //Identify List of Patient to add and to Update
                identifyPatientToUpdate(listSolvedPatientResponse);//Always run update identification before the add identification
                identifyPatientToAdd();
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
                String ServerTargetApp="";
                String baseServerTargetRepoURI="";
                ServerTargetApp=mediatorConfiguration.getServerTargetAppName().equals("null")?null:mediatorConfiguration.getServerTargetAppName();
                baseServerTargetRepoURI=FhirMediatorUtilities.buidServerRepoBaseUri(
                        this.mediatorConfiguration.getServerTargetscheme(),
                        this.mediatorConfiguration.getServerTargetURI(),
                        this.mediatorConfiguration.getServerTargetPort(),
                        ServerTargetApp,
                        this.mediatorConfiguration.getServerTargetFhirDataModel()
                );
                //List<TrackerResourceMap> listTrackerResource=this.mediatorConfiguration.getTrackerResource();
                String dataMappingFile=this.mediatorConfiguration.getFhirAttributeMapping();
                TrackerResourceMap oTrackedProgramInfo=FhirMediatorUtilities.getTrackedProgramInfo(dataMappingFile);
                List<DataElement> listDataElementMapping= FhirMediatorUtilities.getDataElementMapping(this.mediatorConfiguration.getDataElementMappingFile());

                PatientFhirMapping fhirMappingPatient=FhirMediatorUtilities.getPatientMappingAttributes(dataMappingFile);
                PractitionerFhirMapping fhirMappingPractitioner=FhirMediatorUtilities.getPractitionerMappingAttributes(dataMappingFile);
                PractitionerFhirMapping fhirMappingPractitionerSpecimenHandler=FhirMediatorUtilities.getPractitionerSpecimenHandlerMappingAttributes(dataMappingFile);

                ConditionFhirMapping fhirMappingCondition=FhirMediatorUtilities.getConditionMappingAttributes(dataMappingFile);
                DiagnosticOrderFhirMapping fhirMappingDiagnosticOrder=FhirMediatorUtilities.getDiagnosticOrderMappingAttributes(dataMappingFile);
                DiagnosticReportFhirMapping fhirMappingDiagReport=FhirMediatorUtilities.getDiagnosticReportMappingAttributes(dataMappingFile);
                SpecimenFhirMapping fhirMappingSpecimen=FhirMediatorUtilities.getSpecimenMappingAttributes(dataMappingFile);
                ObservationFhirMapping fhirMappingObservation=FhirMediatorUtilities.getObservationMappingAttributes(dataMappingFile);
                ListResourceFhirMapping fhirMappingListResource=FhirMediatorUtilities.getListResourceMappingAttributes(dataMappingFile);
                boolean specimenIdIsFormattedWithHyphen=FhirMediatorUtilities.checkSpecimenIdFormatWithHyphen(dataMappingFile);
                boolean patientIdIsFormattedWithHyphen=FhirMediatorUtilities.checkPatientIdFormatWithHyphen(dataMappingFile);
                String  resultUpdate=null;
                boolean allowCaseUpdated=mediatorConfiguration.getAllowCaseUpdate().equals("yes")?true:false;
                boolean allowAddNewCase=mediatorConfiguration.getAllowAddNewCase().equals("yes")?true:false;

                if(this.listOfPatientToUpdate.size()>0)
                {

                   for(Patient oPatient:listOfPatientToUpdate)
                    {
                        List<DhisEvent> listOfEventToAdd=new ArrayList<>();
                        List<Specimen> listAssociatedSpecimen=new ArrayList<>();
                        List<Practitioner> listAssociatedPractitioner=new ArrayList<>();
                        List<Practitioner> listAssociatedPractitionerSpecimenHandler=new ArrayList<>();
                        List<Condition> listAssociatedCondition=new ArrayList<>();
                        List<DiagnosticOrder> listAssociatedDiagnosticOrder=new ArrayList<>();
                        List<ListResource> listAssociatedListResource=new ArrayList<>();
                        List<Observation> listAssociatedObservation=new ArrayList<>();
                        List<DiagnosticReport> listAssociatedDiagnosticReport=new ArrayList<>();

                        String careProviderId="";
                        if(oPatient.getCareProvider().size()>0)
                        {
                            careProviderId= oPatient.getCareProvider().get(0).getReference().getIdPart();

                        }
                        if(careProviderId!="")
                        {
                            //Get Care provider Practitioner
                            for(Practitioner oPractitioner:listOfValidPractitioner)
                            {
                                if(oPractitioner.getId().getIdPart().equals(careProviderId))
                                {
                                    listAssociatedPractitioner.add(oPractitioner);
                                }
                            }

                        }
                        //Get Resource from practitioner


                        for(Specimen oSpecimen: listOfValidSpecimen)
                        {
                            String  refPatientId= oSpecimen.getSubject().getReference().getIdPart();
                            if(oPatient.getId().getIdPart().equals(refPatientId))
                            {
                                listAssociatedSpecimen.add(oSpecimen);
                                //Then add ListResource related to reference
                                listAssociatedListResource.addAll(getListResourceFromItemReference("Specimen",oSpecimen.getId().getIdPart()));
                                if(oSpecimen.getCollection()!=null)
                                {
                                    String practitionerCollector=oSpecimen.getCollection().getCollector()
                                            .getReference().getIdPart();
                                    for(Practitioner oPractitioner:listOfValidPractitioner)
                                    {
                                        if(oPractitioner.getId().getIdPart().equals(practitionerCollector))
                                        {
                                            listAssociatedPractitionerSpecimenHandler.add(oPractitioner);
                                        }
                                    }

                                }
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
                        //Get All other resource related to Practitioner
                        for(Practitioner oPractitioner:listAssociatedPractitioner)
                        {
                            listAssociatedListResource.addAll(getListResourceFromItemReference("Practitioner",oPractitioner.getId().getIdPart()));
                        }
                        /*
                        List<Condition> ListTrackedCondition=FhirResourceProcessor.getListConditionTrackedForPatient(
                                resourceBundle.getContext(),oPatient.getId().getIdPart(),
                                baseServerTargetRepoURI,this.mediatorConfiguration.getLogFile()
                        );*/

                        List<DataElement> listEventDataElement=new ArrayList<>();
                        for(DataElement oDataElement:listDataElementMapping)
                        {
                            if(oDataElement.type.equals("element"))
                            {
                                listEventDataElement.add(oDataElement);
                            }
                        }
                        listOfEventToAdd=FhirMediatorUtilities.constructListOfLabEvents(
                                oPatient,listAssociatedPractitioner,listAssociatedPractitionerSpecimenHandler,listAssociatedSpecimen,
                                listAssociatedCondition,listAssociatedDiagnosticOrder,
                                listAssociatedObservation,listAssociatedDiagnosticReport,listAssociatedListResource,
                                oTrackedProgramInfo,listEventDataElement,fhirMappingPractitioner,fhirMappingPractitionerSpecimenHandler,
                                fhirMappingSpecimen,fhirMappingCondition,fhirMappingDiagnosticOrder,
                                fhirMappingObservation,fhirMappingDiagReport,specimenIdIsFormattedWithHyphen);
                        //listOfEventToAdd.addAll(res);
                        String trackedEntityId= this.mediatorConfiguration.getTrackedEntity();

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

                        String authentication=mediatorConfiguration.getAuthentication();
                        String authEncoded = com.phloc.commons.base64.Base64.encodeBytes(authentication.getBytes());

                        List<Condition> listConditionAssociatedToEntity=new ArrayList<>();
                        listConditionAssociatedToEntity.addAll(listAssociatedCondition);
                        List<DataElement> listAttributeTrackedEntity=new ArrayList<>();
                        for(DataElement oDataElement:listDataElementMapping)
                        {
                            if(oDataElement.type.equals("attribute"))
                            {
                                listAttributeTrackedEntity.add(oDataElement);
                            }

                        }
                        List<DhisTrackedEntity> resEntityInstance=FhirMediatorUtilities.constructListOfTrackedEntityInstance(
                                oPatient,trackedEntityId,listConditionAssociatedToEntity,listAssociatedDiagnosticReport
                                ,listAttributeTrackedEntity,fhirMappingPatient,fhirMappingCondition,fhirMappingDiagReport,patientIdIsFormattedWithHyphen);
                        String trackedEntityJsonString=FhirMediatorUtilities.TransformListTrackedEntityToJson(
                                resEntityInstance
                        );

                        //Handle connection exception
                        try
                        {
                            //if(ListTrackedCondition.size()==0)
                            if(allowCaseUpdated==true)
                            {
                                //if no condition has been tracked just update the existing TEI and add
                                //associated events
                                builtRequestPath="/trackedEntityInstances/"+trackedEntity;
                                String uriRepServer=baseServerRepoURI+builtRequestPath;
                                URL url = new URL(uriRepServer);
                                //connection = (HttpURLConnection) url.openConnection();
                                connection=(HttpsURLConnection) url.openConnection();
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
                                //Then add associated events
                                String eventJsonString=FhirMediatorUtilities.TransformListEventToJson(listOfEventToAdd,null);
                                connection=null;
                                builtRequestPath="";
                                builtRequestPath="/events";
                                //builtRequestPath="/dhis-web-commons/security/login.action";
                                uriRepServer=baseServerRepoURI+builtRequestPath;
                                url= new URL(uriRepServer);
                                connection = (HttpsURLConnection) url.openConnection();
                                connection.setRequestProperty("Authorization","Basic "+authEncoded);

                                connection.setRequestMethod("POST");
                                connection.setDoOutput(true);
                                connection.setDoInput(true);
                                connection.setRequestProperty("Content-Type","application/json");
                                connection.setUseCaches (false);
                                wr=null;
                                wr = new DataOutputStream (connection.getOutputStream ());
                                wr.writeBytes (eventJsonString);
                                wr.flush ();
                                wr.close ();
                                content=null;
                                content=(InputStream)connection.getInputStream();
                                rd=null;
                                rd=new BufferedReader (new InputStreamReader(content));
                                response=null;
                                response = new StringBuffer();
                                line=null;
                                while ((line = rd.readLine()) != null) {
                                    response.append(line);
                                    response.append('\r');
                                }
                                rd.close();
                                result=null;
                                result= response.toString();


                            }
                           //else if(ListTrackedCondition.size()>0)
                            else if(allowCaseUpdated==false)
                            {
                                String eventJsonString=FhirMediatorUtilities.TransformListEventToJson(listOfEventToAdd,null);
                                builtRequestPath="/events";
                                //builtRequestPath="/dhis-web-commons/security/login.action";
                                String uriRepServer=baseServerRepoURI+builtRequestPath;
                                URL url= new URL(uriRepServer);
                                connection = (HttpsURLConnection) url.openConnection();
                                connection.setRequestProperty("Authorization","Basic "+authEncoded);

                                connection.setRequestMethod("POST");
                                connection.setDoOutput(true);
                                connection.setDoInput(true);
                                connection.setRequestProperty("Content-Type","application/json");
                                connection.setUseCaches (false);
                                //wr=null;
                                DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
                                wr.writeBytes (eventJsonString);
                                wr.flush ();
                                wr.close ();
                                InputStream content=(InputStream)connection.getInputStream();
                                BufferedReader  rd=new BufferedReader (new InputStreamReader(content));
                                StringBuffer response= new StringBuffer();
                                String line=null;
                                while ((line = rd.readLine()) != null) {
                                    response.append(line);
                                    response.append('\r');
                                }
                                rd.close();
                                String result=null;
                                result= response.toString();
                                //}//End for conditionAssociated
                            }//End elseif allow case update

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
                    FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                            "End of update operation process","Notice");
                }//End of this.listOfPatientToUpdate.size
                if(allowAddNewCase==true && listOfPatientToAdd.size()>0)
                {
                    for(Patient oPatient:listOfPatientToAdd) {
                        List<DhisEvent> listOfEventToAdd = new ArrayList<>();
                        List<Specimen> listAssociatedSpecimen = new ArrayList<>();
                        List<Practitioner> listAssociatedPractitioner = new ArrayList<>();
                        List<Practitioner> listAssociatedPractitionerSpecimenHandler = new ArrayList<>();
                        List<Condition> listAssociatedCondition = new ArrayList<>();
                        List<DiagnosticOrder> listAssociatedDiagnosticOrder = new ArrayList<>();
                        List<ListResource> listAssociatedListResource = new ArrayList<>();
                        List<Observation> listAssociatedObservation = new ArrayList<>();
                        List<DiagnosticReport> listAssociatedDiagnosticReport = new ArrayList<>();

                        String careProviderId = "";
                        if (oPatient.getCareProvider().size() >0) {
                            careProviderId = oPatient.getCareProvider().get(0).getReference().getIdPart();

                        }
                        if (careProviderId != "") {
                            //Get Care provider Practitioner
                            for (Practitioner oPractitioner : listOfValidPractitioner) {
                                if (oPractitioner.getId().getIdPart().equals(careProviderId)) {
                                    listAssociatedPractitioner.add(oPractitioner);
                                }
                            }

                        }
                        //Get Resource from practitioner


                        for (Specimen oSpecimen : listOfValidSpecimen) {
                            String refPatientId = oSpecimen.getSubject().getReference().getIdPart();
                            if (oPatient.getId().getIdPart().equals(refPatientId)) {
                                listAssociatedSpecimen.add(oSpecimen);
                                //Then add ListResource related to reference
                                listAssociatedListResource.addAll(getListResourceFromItemReference("Specimen", oSpecimen.getId().getIdPart()));
                                if (oSpecimen.getCollection() != null) {
                                    String practitionerCollector = oSpecimen.getCollection().getCollector()
                                            .getReference().getIdPart();
                                    for (Practitioner oPractitioner : listOfValidPractitioner) {
                                        if (oPractitioner.getId().getIdPart().equals(practitionerCollector)) {
                                            listAssociatedPractitionerSpecimenHandler.add(oPractitioner);
                                        }
                                    }

                                }
                            }


                        }
                        for (Condition oCondition : listOfValidCondition) {
                            String refPatientId = oCondition.getPatient().getReference().getIdPart();
                            if (oPatient.getId().getIdPart().equals(refPatientId)) {
                                listAssociatedCondition.add(oCondition);
                            }
                        }

                        for (DiagnosticOrder oDiagnosticOrder : listOfValidDiagnosticOrder) {
                            String refPatientId = oDiagnosticOrder.getSubject().getReference().getIdPart();
                            if (oPatient.getId().getIdPart().equals(refPatientId)) {
                                listAssociatedDiagnosticOrder.add(oDiagnosticOrder);
                            }
                        }
                        for (Observation oObservation : listOfValidObservation) {
                            String refPatientId = oObservation.getSubject().getReference().getIdPart();
                            if (oPatient.getId().getIdPart().equals(refPatientId)) {
                                listAssociatedObservation.add(oObservation);
                            }
                        }
                        for (DiagnosticReport oDiagnosticReport : listOfValidDiagnosticReport) {
                            String refPatientId = oDiagnosticReport.getSubject().getReference().getIdPart();
                            if (oPatient.getId().getIdPart().equals(refPatientId)) {
                                listAssociatedDiagnosticReport.add(oDiagnosticReport);
                            }
                        }
                        //Get All other resource related to Practitioner
                        for (Practitioner oPractitioner : listAssociatedPractitioner) {
                            listAssociatedListResource.addAll(getListResourceFromItemReference("Practitioner", oPractitioner.getId().getIdPart()));
                        }
                        /*
                        List<Condition> ListTrackedCondition=FhirResourceProcessor.getListConditionTrackedForPatient(
                                resourceBundle.getContext(),oPatient.getId().getIdPart(),
                                baseServerTargetRepoURI,this.mediatorConfiguration.getLogFile()
                        );*/

                        List<DataElement> listEventDataElement = new ArrayList<>();
                        for (DataElement oDataElement : listDataElementMapping) {
                            if (oDataElement.type.equals("element")) {
                                listEventDataElement.add(oDataElement);
                            }
                        }
                        listOfEventToAdd = FhirMediatorUtilities.constructListOfLabEvents(
                                oPatient, listAssociatedPractitioner, listAssociatedPractitionerSpecimenHandler, listAssociatedSpecimen,
                                listAssociatedCondition, listAssociatedDiagnosticOrder,
                                listAssociatedObservation, listAssociatedDiagnosticReport, listAssociatedListResource,
                                oTrackedProgramInfo, listEventDataElement, fhirMappingPractitioner, fhirMappingPractitionerSpecimenHandler,
                                fhirMappingSpecimen, fhirMappingCondition, fhirMappingDiagnosticOrder,
                                fhirMappingObservation, fhirMappingDiagReport, specimenIdIsFormattedWithHyphen);
                        //listOfEventToAdd.addAll(res);
                        String trackedEntityId = this.mediatorConfiguration.getTrackedEntity();

                        String trackedEntity = "";
                        for (IdentifierDt oIdentifier : oPatient.getIdentifier()) {
                            if (oIdentifier.getType().getText().equals("Tracker identifier")) {
                                trackedEntity = oIdentifier.getValue();
                            }
                        }
                        connection = null;
                        String builtRequestPath = "";

                        String authentication = mediatorConfiguration.getAuthentication();
                        String authEncoded = com.phloc.commons.base64.Base64.encodeBytes(authentication.getBytes());

                        List<Condition> listConditionAssociatedToEntity = new ArrayList<>();
                        listConditionAssociatedToEntity.addAll(listAssociatedCondition);
                        List<DataElement> listAttributeTrackedEntity = new ArrayList<>();
                        for (DataElement oDataElement : listDataElementMapping) {
                            if (oDataElement.type.equals("attribute")) {
                                listAttributeTrackedEntity.add(oDataElement);
                            }

                        }
                        List<DhisTrackedEntity> resEntityInstance = FhirMediatorUtilities.constructListOfTrackedEntityInstance(
                                oPatient, trackedEntityId, listConditionAssociatedToEntity, listAssociatedDiagnosticReport
                                , listAttributeTrackedEntity, fhirMappingPatient, fhirMappingCondition, fhirMappingDiagReport, patientIdIsFormattedWithHyphen);
                        String trackedEntityJsonString = FhirMediatorUtilities.TransformListTrackedEntityToJson(
                                resEntityInstance
                        );
                        try
                        {
                            builtRequestPath="/trackedEntityInstances";
                            String uriRepServer=baseServerRepoURI+builtRequestPath;
                            URL url = new URL(uriRepServer);
                            //connection = (HttpURLConnection) url.openConnection();
                            connection=(HttpsURLConnection) url.openConnection();
                            connection.setRequestProperty("Authorization", "Basic " + authEncoded);
                            connection.setRequestMethod("POST");
                            connection.setDoOutput(true);
                            connection.setDoInput(true);
                            connection.setRequestProperty("Content-Type","application/json");
                            connection.setUseCaches (false);
                            DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
                            wr.writeBytes (trackedEntityJsonString);
                            wr.flush ();
                            wr.close ();
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
                            String trackedEntityReference= FhirMediatorUtilities.getIdReferenceFromPOSTResonse(result);
                            if(trackedEntityReference!=null)
                            {
                                //Then enrol the trackedEntity in a program
                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String date= simpleDateFormat.format(new Date()).toString().split(" ")[0];
                                String enrollmentJsonString="{\"trackedEntityInstance\":"+trackedEntityReference
                                        +",\"orgUnit\":\""+oPatient.getManagingOrganization().getReference().getIdPart()
                                        +"\",\"program\":\""+oTrackedProgramInfo.id
                                        +"\",\"enrollmentDate\":\""+date
                                        +"\",\"incidentDate\":\""+date
                                        +"\"}";
                                connection=null;
                                builtRequestPath="/enrollments";
                                uriRepServer=baseServerRepoURI+builtRequestPath;
                                url=null;
                                url = new URL(uriRepServer);
                                //connection = (HttpURLConnection) url.openConnection();
                                connection=(HttpsURLConnection) url.openConnection();
                                connection.setRequestProperty("Authorization", "Basic " + authEncoded);
                                connection.setRequestMethod("POST");
                                connection.setDoOutput(true);
                                connection.setDoInput(true);
                                connection.setRequestProperty("Content-Type","application/json");
                                connection.setUseCaches (false);
                                wr=null;
                                wr = new DataOutputStream (connection.getOutputStream ());
                                wr.writeBytes (enrollmentJsonString);
                                wr.flush ();
                                wr.close ();
                                content=null;
                                content=(InputStream)connection.getInputStream();
                                rd=null;
                                rd=new BufferedReader (new InputStreamReader(content));
                                response=null;
                                response = new StringBuffer();
                                line=null;
                                while ((line = rd.readLine()) != null) {
                                    response.append(line);
                                    response.append('\r');
                                }
                                rd.close();
                                result=null;
                                result= response.toString();
                                String enrollmentReference= FhirMediatorUtilities.getIdReferenceFromPOSTResonse(result);
                                if(enrollmentReference!=null)
                                {
                                    //Then add associated events
                                    String trackedEntityInstance=trackedEntityReference.replaceAll("\"","");
                                    String eventJsonString=FhirMediatorUtilities.TransformListEventToJson(listOfEventToAdd,trackedEntityInstance);
                                    connection=null;
                                    builtRequestPath="";
                                    builtRequestPath="/events";
                                    //builtRequestPath="/dhis-web-commons/security/login.action";
                                    uriRepServer=baseServerRepoURI+builtRequestPath;
                                    url= new URL(uriRepServer);
                                    connection = (HttpsURLConnection) url.openConnection();
                                    connection.setRequestProperty("Authorization","Basic "+authEncoded);

                                    connection.setRequestMethod("POST");
                                    connection.setDoOutput(true);
                                    connection.setDoInput(true);
                                    connection.setRequestProperty("Content-Type","application/json");
                                    connection.setUseCaches (false);
                                    wr=null;
                                    wr = new DataOutputStream (connection.getOutputStream ());
                                    wr.writeBytes (eventJsonString);
                                    wr.flush ();
                                    wr.close ();
                                    content=null;
                                    content=(InputStream)connection.getInputStream();
                                    rd=null;
                                    rd=new BufferedReader (new InputStreamReader(content));
                                    response=null;
                                    response = new StringBuffer();
                                    line=null;
                                    while ((line = rd.readLine()) != null) {
                                        response.append(line);
                                        response.append('\r');
                                    }
                                    rd.close();
                                    result=null;
                                    result= response.toString();
                                }

                            }

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
                    }
                    FhirMediatorUtilities.writeInLogFile(this.mediatorConfiguration.getLogFile(),
                            "End of insert operation process","Notice");
                }


                //Then delete Bundle treated
                for(Bundle oBundle:listOfValidBundle)
                {
                    FhirResourceProcessor.deleteBundle(
                            resourceBundle.getContext(),oBundle.getId(),
                            baseServerTargetRepoURI,this.mediatorConfiguration.getLogFile()
                    );
                }
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
                for (Patient oPatient :resourceBundle.extractPatientFromBundleString(oBundleSearchResult,baseServerRepoURI))
                {

                    if(listIdsPatientUsedForSearch.contains(oPatient.getId().getIdPart()))
                    {
                        Patient tempPatient=getPatientFromValidList(oPatient.getId().getIdPart());
                        //Get Information on the careProvider collected from lab
                        if(tempPatient.getCareProvider().size()>0)
                        {
                            if(oPatient.getCareProvider().size()>0)
                            {
                                oPatient.getCareProvider().set(0,tempPatient.getCareProvider().get(0));
                            }
                            else
                            {
                                oPatient.getCareProvider().add(tempPatient.getCareProvider().get(0));
                            }

                        }
                        if(mediatorConfiguration.getAllowCaseUpdate().equals("yes"))
                        {

                            //Add Patient Traked entity identifier
                            for(IdentifierDt oIdentifier:oPatient.getIdentifier())
                            {
                                if(oIdentifier.getType().getText().equals("Tracker identifier"))
                                {
                                    tempPatient.addIdentifier(oIdentifier);
                                }
                            }
                            if(tempPatient!=null)
                            {
                                this.listOfPatientToUpdate.add(tempPatient);
                            }

                        }
                        else if (mediatorConfiguration.getAllowCaseUpdate().equals("no"))
                        {
                            this.listOfPatientToUpdate.add(oPatient);

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
