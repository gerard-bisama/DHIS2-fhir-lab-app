package org.mediator.fhir;

//import org.hl7.fhir.dstu3.model.Identifier;
//import org.hl7.fhir.dstu3.model.Practitioner;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Organization;
import ca.uhn.fhir.model.dstu2.resource.Specimen;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.Bundle;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import scala.util.parsing.combinator.testing.Ident;

import javax.tools.Diagnostic;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by server-hit on 10/22/16.
 */
public class FhirMediatorUtilities {

    public static boolean checkRequestDateValidity(String dateString)
    {
        boolean isValid=false;
        try
        {
            if(dateString.indexOf("T")<0)
            {
                isValid=false;
            }
            String dateSource=dateString;
            dateSource=dateSource.trim();
            dateSource=dateSource.replaceAll("T"," ");

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date convertedDate=simpleDateFormat.parse(dateSource);
            isValid=true;
            //System.out.println(convertedDate.toString());
        }
        catch (ParseException exc)
        {
            //throw new Exception("");
            return false;
        }
        return isValid;

    }
    public static String BuildSearchPathRequestToFhirServerHapi(String httpResquestPath,String lastSyncDate)
    {



        String BuildRequestPath="";
        if(httpResquestPath.indexOf("/")<0)
        {
            return null;
        }

        String[] pathComponent=httpResquestPath.split("/");

        if(pathComponent.length<3)
        {
            return  null;
        }
        String syncDate=pathComponent[2];
        boolean isSyncDataValid=FhirMediatorUtilities.checkRequestDateValidity(syncDate);
        if(!isSyncDataValid)
        {
            return null;
        }
        BuildRequestPath+="/Practitioner?"+"_lastUpdated=>="+lastSyncDate+"&_lastUpdated=<="+syncDate+"&_sort=_lastUpdated"+"&_pretty=true";

        return BuildRequestPath;
    }
    public static String buildPractitionerSearchRequestByIdentifier(String identifierSystem,String identifierValue)
    {
        String buildRequestPath="/Practitioner?";
        if(identifierSystem!=null)
        {
            buildRequestPath+="identifier="+identifierSystem+"|"+identifierValue+"&_pretty=true";
        }
        else
        {
            buildRequestPath+="identifier="+identifierValue+"&_pretty=true";
        }
        return buildRequestPath;
    }
    public static String buildPractitionerSearchRequestByIds(List<String> listOfIds)
    {
        String buildRequestPath="/Practitioner?_id=";

        if(listOfIds.size()>0)
        {
            boolean firstStep=true;
            for (String idResource:listOfIds ) {
                if(firstStep)
                {
                    buildRequestPath+=idResource;
                    firstStep=false;
                }
                else
                {
                    buildRequestPath+=","+idResource;
                }

            }
            buildRequestPath+="&_pretty=true";
        }
        else//In the case that there is no Id for the HTTP Request to return an empty bundle
        {
            buildRequestPath+="-1";
        }
        return buildRequestPath;
    }
    public static String buildBundleSearchRequestByType()
    {
        String buildRequestPath="/Bundle?type=collection&_pretty=true";
        return buildRequestPath;
    }
    public static String buildResourcesSearchRequestByIds(String resourceType,List<String> listOfIds)
    {
        String buildRequestPath="";
        switch (resourceType)
        {
            case "Practitioner":
                buildRequestPath="/Practitioner?_id=";
                break;
            case "Patient":
                buildRequestPath="/Patient?_id=";
                break;
            case "Organization":
                buildRequestPath="/Organization?_id=";
                break;
            case "Specimen":
                buildRequestPath="/Specimen?_id=";
                break;
            case "DiagnosticOrder":
                buildRequestPath="/DiagnosticOrder?_id=";
                break;
            case "Observation":
                buildRequestPath="/Observation?_id=";
                break;
            case "Condition":
                buildRequestPath="/Condition?_id=";
                break;
            case "DiagnosticReport":
                buildRequestPath="/DiagnosticReport?_id=";
                break;
            case "List":
                buildRequestPath="/List?_id=";
                break;

        }

        if(listOfIds.size()>0)
        {
            boolean firstStep=true;
            for (String idResource:listOfIds ) {
                if(firstStep)
                {
                    buildRequestPath+=idResource;
                    firstStep=false;
                }
                else
                {
                    buildRequestPath+=","+idResource;
                }

            }
            buildRequestPath+="&_pretty=true";
        }
        else//In the case that there is no Id for the HTTP Request to return an empty bundle
        {
            buildRequestPath+="-1";
        }
        return buildRequestPath;
    }
    public static String buildPractitionerUpdateRequest(String identifierSystem,String identifierValue)
    {
        String buildRequestPath="/Practitioner?";
        if(identifierSystem!=null)
        {
            buildRequestPath+="identifier="+identifierSystem+"|"+identifierValue+"&_pretty=true";
        }
        else
        {
            buildRequestPath+="identifier="+identifierValue+"&_pretty=true";
        }
        return buildRequestPath;
    }
    public static String buidServerRepoBaseUri(String scheme,String serverUri,int port,String fhirDataModel)
    {
        String ServerBaseUri=scheme+"://"+serverUri;
        if(port>0)
        {
            ServerBaseUri+=":"+port;
        }
        if(fhirDataModel!=null)
        {
            ServerBaseUri+="/"+fhirDataModel;
        }
        //String ServerUri=scheme+serverUri+port+fhirDataModel;
        return ServerBaseUri;

    }
    public static String buidServerRepoBaseUri(String scheme,String serverUri,int port,String serverAppName,String fhirDataModel)
    {
        String ServerBaseUri=scheme+"://"+serverUri;
        if(port>0)
        {
            ServerBaseUri+=":"+port;
        }
        if(serverAppName!=null)
        {
            ServerBaseUri+="/"+serverAppName;
        }
        if(fhirDataModel!=null)
        {
            ServerBaseUri+="/"+fhirDataModel;
        }
        //String ServerUri=scheme+serverUri+port+fhirDataModel;
        return ServerBaseUri;

    }
    public static Date getDateFromRequestStringDate(String dateString )
    {

        Date resDate=null;
        try
        {
            String dateSource=dateString;
            dateSource=dateSource.trim();
            dateSource=dateSource.replaceAll("T"," ");

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //simpleDateFormat.
            resDate=simpleDateFormat.parse(dateSource);
        }
        catch (ParseException exc)
        {
            return null;
        }
        return resDate;
    }
    public static Date getDateFromRequestPath(String hhtpResquestPath)
    {
        Date resDate=null;
        if(hhtpResquestPath.indexOf("/")<0)
        {
            return null;
        }

        String[] pathComponent=hhtpResquestPath.split("/");

        if(pathComponent.length<3)
        {
            return  null;
        }
        String syncDate=pathComponent[2];
        boolean isSyncDataValid=FhirMediatorUtilities.checkRequestDateValidity(syncDate);
        if(!isSyncDataValid)
        {
            return null;
        }
        resDate=FhirMediatorUtilities.getDateFromRequestStringDate(syncDate);
        return resDate;

    }
    public static String getStringDateFromRequestPath(String hhtpResquestPath)
    {
        String resDate=null;
        if(hhtpResquestPath.indexOf("/")<0)
        {
            return null;
        }

        String[] pathComponent=hhtpResquestPath.split("/");

        if(pathComponent.length<3)
        {
            return  null;
        }
        String syncDate=pathComponent[2];
        boolean isSyncDataValid=FhirMediatorUtilities.checkRequestDateValidity(syncDate);
        if(!isSyncDataValid)
        {
            return null;
        }
        resDate=syncDate;
        //resDate=FhirMediatorUtilities.getDateFromRequestStringDate(syncDate);
        return resDate;

    }
    public static String encodeUrlToHttpFormat(String urlString)
    {
        String encodedUrl=null;
        try
        {
            URL url=new URL(urlString);
            URI uri= new URI(url.getProtocol(),url.getUserInfo(),url.getHost(),url.getPort(),url.getPath(),
                    url.getQuery(),url.getRef());
            //System.out.println(uri.toURL().toString());
            encodedUrl=uri.toURL().toString();
        }
        catch (Exception exc)
        {
            return null;
        }
        return  encodedUrl;
    }
    public static List<Practitioner> removeDuplicateInTheList(List<Practitioner> listOfPractitioner)
    {
        int counter=0;
        List<Practitioner> cleanedList=new ArrayList<>();
        for(Practitioner oPractitioner :listOfPractitioner)

        {
            if(counter==0)
            {
                cleanedList.add(oPractitioner);
            }
            else
            {
                if(oPractitioner.getIdentifier().size()>0)
                {
                    boolean isDuplicate=false;
                    for (Practitioner cleanedPractitioner: cleanedList)
                    {
                        if(cleanedPractitioner.getIdentifier().size()>0)
                        {
                            List<IdentifierDt> practitionerListIdentifier=oPractitioner.getIdentifier();
                            List<IdentifierDt> cleanedListIdentifier=cleanedPractitioner.getIdentifier();
                            for (IdentifierDt practitionerIdentifier:practitionerListIdentifier)
                            {
                                for (IdentifierDt cleanedIdentifier:cleanedListIdentifier)
                                {
                                    if(practitionerIdentifier.getSystem().equals(cleanedIdentifier.getSystem())
                                            && practitionerIdentifier.getValue().equals(cleanedIdentifier.getValue()))
                                    {
                                        isDuplicate=true;
                                        break;
                                    }
                                }
                                if(isDuplicate)
                                {
                                    break;
                                }
                            }
                            if(isDuplicate)
                            {
                                break;
                            }
                        }
                        else {
                            continue;
                        }
                    }
                    if(!isDuplicate)
                    {
                        cleanedList.add(oPractitioner);
                        continue;
                    }
                }
                else
                {
                    cleanedList.add(oPractitioner);
                }
            }
            counter++;
        }
        return cleanedList;
    }
    public static void writeInLogFile(String logFileName,String entry,String logLevel)
    {
        BufferedWriter bw = null;
        FileWriter fw = null;
        String fileName=logFileName;
        try
        {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date tempDate=new Date();
            String convertedDate=simpleDateFormat.format(tempDate);
            File file = new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            // true = append file
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            String fileLine=convertedDate+" | "+"["+logLevel+"]: "+entry;
            bw.write(fileLine);
            bw.newLine();
        }
        catch (IOException exc)
        {
            System.err.println(exc);
        }
        finally
        {
            try
            {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }
    }
    public static List<DhisEvent> constructListOfLabEvents(Patient patientToProcess,List<Specimen> listAssociatedSpecimen
    ,List<Observation> listAssociatedObservation,TrackerResourceMap trackedProgramInfo
    ,List<DataElement> listDataElementMappingList,ObservationFhirMapping observationMapping,
    SpecimenFhirMapping specimenMapping)
    {
        String labResultSpecimenIdDataElementId="";
        String labResultTestResultDataElementId="";
        String labResultconfirmedDiseaseDataElementId="";
        String labRequestTestingLabDataElementId="";
        String labRequestSpecimenTypeDataElementId="";
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        List<DhisEvent> listOfEvent=new ArrayList<>();
        String trackedEntity="";
        for(IdentifierDt oIdentifier:patientToProcess.getIdentifier())
        {
            if(oIdentifier.getType().getText().equals("Tracker identifier"))
            {
                trackedEntity=oIdentifier.getValue();
            }
        }
        for(String stageId:trackedProgramInfo.stages)
        {
            List<DataElement> listDataElementStage=new ArrayList<>();
            for(DataElement oDataElement:listDataElementMappingList) {
                if(oDataElement.stage.equals(stageId))
                {
                    listDataElementStage.add(oDataElement);
                }
            }
            if(listDataElementStage.size()>0)
            {
                for(Observation oObservation: listAssociatedObservation)
                {
                    DhisEvent oEvent=new DhisEvent();
                    oEvent.program=trackedProgramInfo.id;
                    oEvent.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
                    oEvent.trackedEntityInstance=trackedEntity;
                    oEvent.programStage=stageId;

                    for(DataElement oDataElement:listDataElementStage)
                    {
                        String displayName=oDataElement.displayName;
                        if(observationMapping.effectiveDateTime!="" && observationMapping.effectiveDateTime.equals(displayName))
                        {
                            if(oObservation.getEffective()!=null)
                            {
                                DateTimeDt oDateTime=(DateTimeDt)oObservation.getEffective();
                                String builtDate="";
                                builtDate=oDateTime.getYear().toString()+"-";
                                builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth()+1:""+oDateTime.getMonth()+1;
                                builtDate+="-";
                                builtDate+=oDateTime.getDay().toString().length()>1?oDateTime.getDay().toString():"0"+oDateTime.getDay().toString();
                                oEvent.eventDate=builtDate;
                                continue;
                            }

                        }
                        if(observationMapping.issued!="" && observationMapping.issued.equals(displayName))
                        {
                            if(oObservation.getIssued()!=null)
                            {
                                DateTimeDt oDateTime=new DateTimeDt(oObservation.getIssued());
                                String builtDate="";
                                builtDate=oDateTime.getYear().toString()+"-";
                                builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth()+1:""+oDateTime.getMonth()+1;
                                builtDate+="-";
                                builtDate+=oDateTime.getDay().toString().length()>1?oDateTime.getDay().toString():"0"+oDateTime.getDay().toString();
                                oEvent.eventDate=builtDate;
                                continue;
                            }

                        }
                        if(observationMapping.specimen!="" && observationMapping.specimen.equals(displayName))
                        {
                            if(oObservation.getSpecimen()!=null)
                            {
                                String refSpecimenId=oObservation.getSpecimen().getReference().getIdPart().split("-")[0];
                                oEvent.addDataValue(oDataElement.id,refSpecimenId);
                                continue;
                            }

                        }
                        if(observationMapping.interpretation!="" && observationMapping.interpretation.equals(displayName))
                        {
                            if(oObservation.getInterpretation()!=null)
                            {
                                oEvent.addDataValue(oDataElement.id,oObservation.getInterpretation().getText());
                                continue;
                            }

                        }
                        if(observationMapping.comments!="" && observationMapping.comments.equals(displayName))
                        {
                            if(oObservation.getComments()!="")
                            {
                                oEvent.addDataValue(oDataElement.id,oObservation.getComments());
                                continue;
                            }

                        }
                        if(observationMapping.performer!="" && observationMapping.performer.equals(displayName))
                        {
                            if(oObservation.getPerformer().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oObservation.getPerformer().get(0).getReference().getIdPart());
                                continue;
                            }

                        }
                        if(observationMapping.status!="" && observationMapping.status.equals(displayName))
                        {
                            if(oObservation.getStatus()!="")
                            {
                                oEvent.addDataValue(oDataElement.id,oObservation.getStatus());
                                continue;
                            }
                        }
                    }
                    for(Specimen oSpecimen: listAssociatedSpecimen)
                    {
                        for(DataElement oDataElement:listDataElementStage) {
                            String displayName = oDataElement.displayName;
                            if(specimenMapping.identifier!="" && specimenMapping.identifier.contains(displayName)==true)
                            {
                                if (!checkDataElementInEvent(oEvent,oDataElement.id))
                                {
                                    oEvent.addDataValue(oDataElement.id,oSpecimen.getId().getIdPart().split("-")[0]);
                                    continue;
                                }
                            }
                            if(specimenMapping.type!="" && specimenMapping.type.equals(displayName))
                            {
                                if (!checkDataElementInEvent(oEvent,oDataElement.id)) {
                                    oEvent.addDataValue(oDataElement.id, oSpecimen.getId().getIdPart().split("-")[0]);
                                    continue;
                                }

                            }
                            if(specimenMapping.receivedTime!="" && specimenMapping.receivedTime.equals(displayName))
                            {
                                if(oSpecimen.getReceivedTime()!=null)
                                {
                                    DateTimeDt oDateTime=new DateTimeDt(oSpecimen.getReceivedTime());
                                    String builtDate="";
                                    builtDate=oDateTime.getYear().toString()+"-";
                                    builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth()+1:""+oDateTime.getMonth()+1;
                                    builtDate+="-";
                                    builtDate+=oDateTime.getDay().toString().length()>1?oDateTime.getDay().toString():"0"+oDateTime.getDay().toString();
                                    if (!checkDataElementInEvent(oEvent,oDataElement.id)) {
                                        oEvent.addDataValue(oDataElement.id, builtDate);
                                    }
                                    continue;
                                }
                            }
                        }
                    }
                    listOfEvent.add(oEvent);

                }


            }
        }

        return listOfEvent;
    }
    public static List<DhisEvent> constructListOfLabEvents(Patient patientToProcess,List<Specimen> listAssociatedSpecimen
            ,List<Observation> listAssociatedObservation,TrackerResourceMap trackedProgramInfo
            ,List<DataElement> listDataElementMappingList)
    {
        String labResultSpecimenIdDataElementId="";
        String labResultTestResultDataElementId="";
        String labResultconfirmedDiseaseDataElementId="";
        String labRequestTestingLabDataElementId="";
        String labRequestSpecimenTypeDataElementId="";
        for(DataElement oDataElement:listDataElementMappingList)
        {
            String displayName=oDataElement.displayName;
            switch (displayName)
            {
                case "Lab Results/Specimen ID":
                    labResultSpecimenIdDataElementId=oDataElement.id;
                    break;
                case "Lab Results/Test Result":
                    labResultTestResultDataElementId=oDataElement.id;
                    break;
                case "Lab Results/Confirmed Disease":
                    labResultconfirmedDiseaseDataElementId=oDataElement.id;
                    break;
                case "Lab Request/Testing Laboratory":
                    labRequestTestingLabDataElementId=oDataElement.id;
                    break;
                case "Lab Request/Specimen Type":
                    labRequestSpecimenTypeDataElementId=oDataElement.id;
                    break;
            }
        }
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        List<DhisEvent> listOfEvent=new ArrayList<>();
        String trackedEntity="";
        for(IdentifierDt oIdentifier:patientToProcess.getIdentifier())
        {
            if(oIdentifier.getType().getText().equals("Tracker identifier"))
            {
                trackedEntity=oIdentifier.getValue();
            }
        }
        for(Observation oObservation: listAssociatedObservation)
        {

            //If status is final Construct the lab result then the lab request
            if(oObservation.getStatus().equals("final"))
            {
                DhisEvent oEvent=new DhisEvent();
                oEvent.program=trackedProgramInfo.id;
                oEvent.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
                oEvent.trackedEntityInstance=trackedEntity;
                oEvent.programStage="aornzRfUbDZ";
                DateTimeDt oDateTime=(DateTimeDt)oObservation.getEffective();
                String builtDate="";
                builtDate=oDateTime.getYear().toString()+"-";
                builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth()+1:""+oDateTime.getMonth()+1;
                builtDate+="-";
                builtDate+=oDateTime.getDay().toString().length()>1?oDateTime.getDay().toString():"0"+oDateTime.getDay().toString();

                oEvent.eventDate=builtDate;

                String refSpecimenId=oObservation.getSpecimen().getReference().getIdPart().split("-")[0];
                oEvent.addDataValue(labResultSpecimenIdDataElementId,refSpecimenId);
                String testResult=oObservation.getInterpretation().getText();
                String confirmedDisease=oObservation.getComments();
                oEvent.addDataValue(labResultTestResultDataElementId,testResult);
                oEvent.addDataValue(labResultconfirmedDiseaseDataElementId,confirmedDisease);
                listOfEvent.add(oEvent);
                //if the current event is the lab result, it means that the request has
                //been made
                DhisEvent oEventRequested=new DhisEvent();
                oEventRequested.program=trackedProgramInfo.id;
                oEventRequested.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
                oEventRequested.trackedEntityInstance=trackedEntity;
                oEventRequested.programStage="YLMRmho6SdY";
                Date oDate=(Date)oObservation.getIssued();
                DateTimeDt dateTimeIssued=new DateTimeDt(oDate);
                builtDate="";
                builtDate=dateTimeIssued.getYear().toString()+"-";
                builtDate+=dateTimeIssued.getMonth().toString().length()>1?dateTimeIssued.getMonth()+1:""+dateTimeIssued.getMonth()+1;
                builtDate+="-";
                builtDate+=dateTimeIssued.getDay().toString().length()>1?dateTimeIssued.getDay().toString():"0"+dateTimeIssued.getDay().toString();

                oEventRequested.eventDate=builtDate;
                String refTestingLab=oObservation.getPerformer().get(0).getReference().getIdPart();
                oEventRequested.addDataValue(labRequestTestingLabDataElementId,refTestingLab);
                refSpecimenId="";
                refSpecimenId=oObservation.getSpecimen().getReference().getIdPart().split("-")[0];
                oEventRequested.addDataValue(labResultSpecimenIdDataElementId,refSpecimenId);
                //Get SpecimenRef to observation
                Specimen oSpecimenRef=null;
                for(Specimen oSpecimen: listAssociatedSpecimen)
                {
                    if(oSpecimen.getId().getIdPart().split("-")[0].equals(refSpecimenId))
                    {
                        oSpecimenRef=oSpecimen;
                    }
                }
                String refSpecimenType=oSpecimenRef.getType().getText();
                oEventRequested.addDataValue(labRequestSpecimenTypeDataElementId,refSpecimenType);
                listOfEvent.add(oEventRequested);


            }//End if oObservation.getStatus()
            else if (oObservation.getStatus().equals("registered"))
            {
                DhisEvent oEventRequested=new DhisEvent();
                oEventRequested.program=trackedProgramInfo.id;
                oEventRequested.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
                oEventRequested.trackedEntityInstance=trackedEntity;
                oEventRequested.programStage="YLMRmho6SdY";
                Date oDate=(Date)oObservation.getIssued();
                DateTimeDt dateTimeIssued=new DateTimeDt(oDate);
                String builtDate="";
                builtDate=dateTimeIssued.getYear().toString()+"-";
                builtDate+=dateTimeIssued.getMonth().toString().length()>1?dateTimeIssued.getMonth()+1:""+dateTimeIssued.getMonth()+1;
                builtDate+="-";
                builtDate+=dateTimeIssued.getDay().toString().length()>1?dateTimeIssued.getDay().toString():"0"+dateTimeIssued.getDay().toString();

                oEventRequested.eventDate=builtDate;
                String refTestingLab=oObservation.getPerformer().get(0).getReference().getIdPart();
                oEventRequested.addDataValue(labRequestTestingLabDataElementId,refTestingLab);
                String refSpecimenId="";
                refSpecimenId=oObservation.getSpecimen().getReference().getIdPart().split("-")[0];
                oEventRequested.addDataValue(labResultSpecimenIdDataElementId,refSpecimenId);
                //Get SpecimenRef to observation
                Specimen oSpecimenRef=null;
                for(Specimen oSpecimen: listAssociatedSpecimen)
                {
                    if(oSpecimen.getId().getIdPart().split("-")[0].equals(refSpecimenId))
                    {
                        oSpecimenRef=oSpecimen;
                    }
                }
                String refSpecimenType=oSpecimenRef.getType().getText();
                oEventRequested.addDataValue(labRequestSpecimenTypeDataElementId,refSpecimenType);
                listOfEvent.add(oEventRequested);
            }//end elseif oObservation.getStatus()
            //oEvent.dataValues.add()

        }
        return listOfEvent;
    }
    public static String TransformListEventToJson(List<DhisEvent> listEvents)
    {
        Gson gson=new Gson();
        String jsonString="{\"events\":[";
        int compter=0;
        for(DhisEvent oEvent : listEvents)
        {
            String res=gson.toJson(oEvent);
            if(compter==0)
            {
                jsonString+=res;
            }
            else
            {
                jsonString+=","+res;
            }
            compter++;
        }
        jsonString+="]}";
        return jsonString;
    }
    public static List<DhisTrackedEntity> constructListOfTrackedEntityInstance(Patient patientToProcess,String trackedEntityId,List<Condition> listAssociatedCondition,
                                                                   List<DiagnosticReport> listAssociatedDiagnosticReport,List<DataElement> listDataElementMappingList)
    {
        List<DhisTrackedEntity> listOfTrackedEntity=new ArrayList<>();
        String trackedEntity="";
        for(IdentifierDt oIdentifier:patientToProcess.getIdentifier())
        {
            if(oIdentifier.getType().getText().equals("Tracker identifier"))
            {
                trackedEntity=oIdentifier.getValue();
            }
        }
        DhisTrackedEntity oTrackedEntity=new DhisTrackedEntity();
        oTrackedEntity.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
        if(trackedEntityId!=null) {
            oTrackedEntity.trackedEntity = trackedEntityId;
        }
        String uniqueCaseDataElementId="";
        String firstNameDataElementId="";
        String lastNameDataElementId="";
        String dateOfBirthDataElementId="";
        String villageDataElementId="";
        String notifiableDiseaseDataElementId="";
        String mainSymptomDataElementId="";
        String classificationDataElementId="";
        String immediateOutcomeDataElementId="";

        for(DataElement oDataElement:listDataElementMappingList)
        {
            String displayName=oDataElement.displayName;
            switch (displayName)
            {
                case "Unique Case ID":
                    uniqueCaseDataElementId=oDataElement.id;
                    break;
                case "First Name":
                    firstNameDataElementId=oDataElement.id;
                    break;
                case "Last name":
                    lastNameDataElementId=oDataElement.id;
                    break;
                case "Age (Years)":
                    dateOfBirthDataElementId=oDataElement.id;
                    break;
                case "Village/Domicile":
                    villageDataElementId=oDataElement.id;
                    break;
                case "Notifiable Disease/Condition":
                    notifiableDiseaseDataElementId=oDataElement.id;
                    break;
                case "Main Symptom":
                    mainSymptomDataElementId=oDataElement.id;
                    break;
                case "Classification of disease/event":
                    classificationDataElementId=oDataElement.id;
                    break;
                case "Immediate Outcome":
                    immediateOutcomeDataElementId=oDataElement.id;
                    break;
            }
        }



        //for(DataElement)
        oTrackedEntity.addAttribute(uniqueCaseDataElementId,patientToProcess.getId().getIdPart()
                .split("-")[0]);
        if(patientToProcess.getName()!=null)
        {
            oTrackedEntity.addAttribute(firstNameDataElementId,patientToProcess.getName().get(0)
                    .getFamily().get(0).toString());
            oTrackedEntity.addAttribute(lastNameDataElementId,patientToProcess.getName().get(0)
                    .getGiven().get(0).toString());
        }

        if(patientToProcess.getBirthDate()!=null)
        {
            DateTimeDt dateTimeBirth=new DateTimeDt(patientToProcess.getBirthDate());
            String builtDate="";
            builtDate=dateTimeBirth.getYear().toString()+"-";
            builtDate+=dateTimeBirth.getMonth().toString().length()>1?dateTimeBirth.getMonth()+1:""+dateTimeBirth.getMonth()+1;
            builtDate+="-";
            builtDate+=dateTimeBirth.getDay().toString().length()>1?dateTimeBirth.getDay().toString():"0"+dateTimeBirth.getDay().toString();
            Date date= new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int currentYear = cal.get(Calendar.YEAR);
            int age=currentYear-dateTimeBirth.getYear();
            oTrackedEntity.addAttribute(dateOfBirthDataElementId,""+age);

        }
        if(patientToProcess.getAddress().size()>0) {
            oTrackedEntity.addAttribute(villageDataElementId, patientToProcess.getAddress().get(0).getText());
        }

        for(Condition oCondition:listAssociatedCondition)
        {
            if(oCondition.getCode().getText()!="")
            {
                oTrackedEntity.addAttribute(notifiableDiseaseDataElementId,oCondition.getCode().getText());

            }
            if(oCondition.getCategory().getText()!="")
            {
                oTrackedEntity.addAttribute(mainSymptomDataElementId,oCondition.getCategory().getText());

            }

        }//End listAssociatedCondition
        for(DiagnosticReport oReport:listAssociatedDiagnosticReport)
        {
            if(oReport.getConclusion()!="")
            {
                oTrackedEntity.addAttribute(classificationDataElementId,oReport.getConclusion());
            }

        }
        IDatatype oDeceased=patientToProcess.getDeceased();
        if(oDeceased!=null)
        {

            if (oDeceased.toString().equals("true"))
            {
                oTrackedEntity.addAttribute(immediateOutcomeDataElementId,"Dead");
            }
            else
            {
                oTrackedEntity.addAttribute(immediateOutcomeDataElementId,"Alive");
            }
        }
        listOfTrackedEntity.add(oTrackedEntity);
        return listOfTrackedEntity;

    }
    public static List<DhisTrackedEntity> constructListOfTrackedEntityInstance(Patient patientToProcess,String trackedEntityId,List<Condition> listAssociatedCondition,
                                                                               List<DiagnosticReport> listAssociatedDiagnosticReport,List<DataElement> listDataElementMappingList,
                                                                               PatientFhirMapping oPatientMapping,
                                                                               ConditionFhirMapping oConditionMapping,
                                                                               DiagnosticReportFhirMapping oDiagnosticReportMapping)
    {
        List<DhisTrackedEntity> listOfTrackedEntity=new ArrayList<>();
        String trackedEntity="";
        Condition trackedCondition=new Condition();
        DiagnosticReport trackedDiagReport=new DiagnosticReport();
        for(IdentifierDt oIdentifier:patientToProcess.getIdentifier())
        {
            if(oIdentifier.getType().getText().equals("Tracker identifier"))
            {
                trackedEntity=oIdentifier.getValue();
            }
        }
        DhisTrackedEntity oTrackedEntity=new DhisTrackedEntity();
        oTrackedEntity.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
        if(trackedEntityId!=null) {
            oTrackedEntity.trackedEntity = trackedEntityId;
        }
        String uniqueCaseDataElementId="";
        String firstNameDataElementId="";
        String lastNameDataElementId="";
        String dateOfBirthDataElementId="";
        String villageDataElementId="";
        String notifiableDiseaseDataElementId="";
        String mainSymptomDataElementId="";
        String classificationDataElementId="";
        String immediateOutcomeDataElementId="";
        if(listAssociatedCondition.size()>0)
        {
            trackedCondition=listAssociatedCondition.get(0);
        }
        if(listAssociatedDiagnosticReport.size()>0)
        {
            trackedDiagReport=listAssociatedDiagnosticReport.get(0);
        }
        for(DataElement oDataElement:listDataElementMappingList) {
            String displayName=oDataElement.displayName;
            if(oPatientMapping.identifier!="" && oPatientMapping.identifier.equals(displayName))
            {
                oTrackedEntity.addAttribute(oDataElement.id,patientToProcess.getId().getIdPart()
                        .split("-")[0]);
            }
            else if(oPatientMapping.name_family!="" && oPatientMapping.name_family.equals(displayName))
            {
                if(patientToProcess.getName()!=null)
                {
                    oTrackedEntity.addAttribute(oDataElement.id,patientToProcess.getName().get(0)
                            .getFamily().get(0).toString());
                }
            }
            else if(oPatientMapping.name_given!="" && oPatientMapping.name_given.equals(displayName))
            {
                if(patientToProcess.getName()!=null)
                {
                    oTrackedEntity.addAttribute(oDataElement.id,patientToProcess.getName().get(0)
                            .getGiven().get(0).toString());
                }
            }
            else if(oPatientMapping.birthDate!="" && oPatientMapping.birthDate.equals(displayName))
            {
                if(patientToProcess.getBirthDate()!=null)
                {
                    DateTimeDt dateTimeBirth=new DateTimeDt(patientToProcess.getBirthDate());
                    String builtDate="";
                    builtDate=dateTimeBirth.getYear().toString()+"-";
                    builtDate+=dateTimeBirth.getMonth().toString().length()>1?dateTimeBirth.getMonth()+1:""+dateTimeBirth.getMonth()+1;
                    builtDate+="-";
                    builtDate+=dateTimeBirth.getDay().toString().length()>1?dateTimeBirth.getDay().toString():"0"+dateTimeBirth.getDay().toString();
                    Date date= new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int currentYear = cal.get(Calendar.YEAR);
                    int age=currentYear-dateTimeBirth.getYear();
                    oTrackedEntity.addAttribute(oDataElement.id,""+age);

                }
            }
            else if(oPatientMapping.address!="" && oPatientMapping.address.equals(displayName))
            {
                if(patientToProcess.getAddress().size()>0)
                {
                    oTrackedEntity.addAttribute(oDataElement.id, patientToProcess.getAddress().get(0).getText());

                }

            }
            else if(oPatientMapping.deceasedBoolean!="" && oPatientMapping.deceasedBoolean.equals(displayName))
            {
                IDatatype oDeceased=patientToProcess.getDeceased();
                if(oDeceased!=null)
                {

                    if (oDeceased.toString().equals("true"))
                    {
                        oTrackedEntity.addAttribute(oDataElement.id,"Dead");
                    }
                    else
                    {
                        oTrackedEntity.addAttribute(oDataElement.id,"Alive");
                    }
                }
            }
            else if (oConditionMapping.code!="" && oConditionMapping.code.equals(displayName))
            {
                if(trackedCondition.getCode().getText()!="")
                {
                    oTrackedEntity.addAttribute(oDataElement.id,trackedCondition.getCode().getText());

                }
            }
            else if (oConditionMapping.category!="" && oConditionMapping.category.equals(displayName))
            {
                if(trackedCondition.getCategory().getText()!="")
                {
                    oTrackedEntity.addAttribute(oDataElement.id,trackedCondition.getCategory().getText());

                }
            }
            else if (oConditionMapping.dateRecorded!="" && oConditionMapping.dateRecorded.equals(displayName))
            {
                if(trackedCondition.getDateRecorded()!=null)
                {
                    DateTimeDt dateTimeBirth=new DateTimeDt(trackedCondition.getDateRecorded());
                    String builtDate="";
                    builtDate=dateTimeBirth.getYear().toString()+"-";
                    builtDate+=dateTimeBirth.getMonth().toString().length()>1?dateTimeBirth.getMonth()+1:""+dateTimeBirth.getMonth()+1;
                    builtDate+="-";
                    builtDate+=dateTimeBirth.getDay().toString().length()>1?dateTimeBirth.getDay().toString():"0"+dateTimeBirth.getDay().toString();

                    oTrackedEntity.addAttribute(oDataElement.id,builtDate);

                }
            }
            else if (oConditionMapping.onsetDateTime!="" && oConditionMapping.onsetDateTime.equals(displayName))
            {
                IDatatype onSetDateTime=trackedCondition.getOnset();
                if(onSetDateTime!=null)
                {
                    oTrackedEntity.addAttribute(oDataElement.id,onSetDateTime.toString());
                }
            }
            else if (oDiagnosticReportMapping.conclusion!="" && oDiagnosticReportMapping.conclusion.equals(displayName))
            {

                if(trackedDiagReport.getConclusion()!="")
                {
                    oTrackedEntity.addAttribute(oDataElement.id,trackedDiagReport.getConclusion());
                }
            }


        }
        listOfTrackedEntity.add(oTrackedEntity);
        return listOfTrackedEntity;

    }
    public static String TransformListTrackedEntityToJson(List<DhisTrackedEntity> listOfTrackedEntity)
    {
        Gson gson=new Gson();
        String jsonString="";
        int compter=0;
        for(DhisTrackedEntity oTrackedEntity:listOfTrackedEntity)
        {
            jsonString=gson.toJson(oTrackedEntity);
        }
        return jsonString;
    }
    public static List<String> getListOfFile(String directoryName)
    {
        List<String> listOfFile=new ArrayList<>();
        File directory=new File(directoryName);
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0L;
        while (elapsedTime < 2*30*1000) {
            elapsedTime = (new Date()).getTime() - startTime;
        }

        for(File oFile : directory.listFiles())
        {
            listOfFile.add(oFile.getName());
        }
        return listOfFile;
    }
    public static Bundle getBundleObjectFromJsonFile(String filePath) throws Exception
    {
        Gson gson=new Gson();
        Bundle oBundle=null;
        try {
            FileReader oFileReader = new FileReader(filePath);
            oBundle= gson.fromJson(oFileReader, Bundle.class);
            //return oBundle;
        }
        catch (IOException exc)
        {
            throw new Exception(exc.toString());
        }
        return oBundle;
    }
    public static List<DataElement> getDataElementMapping (String filePath) throws Exception
    {
        List<DataElement> listDataElement=new ArrayList<>();
        Gson gson=new Gson();
        try {
            JsonArray json=(JsonArray)gson.fromJson(new FileReader(filePath), JsonElement.class);
            for(int iterator=0;iterator<json.size();iterator++)
            {
                JsonElement oElement=json.get(iterator);
                DataElement oDataElement=gson.fromJson(oElement,DataElement.class);
                listDataElement.add(oDataElement);

            }
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return listDataElement;
    }
    public static PatientFhirMapping getPatientMappingAttributes (String filePath) throws Exception
    {
        PatientFhirMapping patientAttributeMapping=new PatientFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonPatientAttributeMappingString=((JsonObject)json).get("patient_attribute_mapping").toString();
            patientAttributeMapping=gson.fromJson(jsonPatientAttributeMappingString,PatientFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return patientAttributeMapping;
    }
    public static ConditionFhirMapping getConditionMappingAttributes (String filePath) throws Exception
    {
        ConditionFhirMapping conditionAttributeMapping=new ConditionFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonConditionAttributeMappingString=((JsonObject)json).get("condition_attribute_mapping").toString();
            conditionAttributeMapping=gson.fromJson(jsonConditionAttributeMappingString,ConditionFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return conditionAttributeMapping;
    }
    public static DiagnosticReportFhirMapping getDiagnosticReportMappingAttributes (String filePath) throws Exception
    {
        DiagnosticReportFhirMapping diagnosticReportAttributeMapping=new DiagnosticReportFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("diagnosticreport_attribute_mapping").toString();
            diagnosticReportAttributeMapping=gson.fromJson(jsonAttributeMappingString,DiagnosticReportFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return diagnosticReportAttributeMapping;
    }
    public static TrackerResourceMap getTrackedProgramInfo (String filePath) throws Exception
    {
        TrackerResourceMap programInfo=new TrackerResourceMap();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("programs_progstages_tracked").getAsJsonArray().
                    get(0).toString();
            programInfo=gson.fromJson(jsonAttributeMappingString,TrackerResourceMap.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return programInfo;
    }
    public static ObservationFhirMapping getObservationMappingAttributes (String filePath) throws Exception
    {
        ObservationFhirMapping observationAttributeMapping=new ObservationFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("observation_attribute_mapping").toString();
            observationAttributeMapping=gson.fromJson(jsonAttributeMappingString,ObservationFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return observationAttributeMapping;
    }
    public static SpecimenFhirMapping getSpecimenMappingAttributes (String filePath) throws Exception
    {
        SpecimenFhirMapping specimenAttributeMapping=new SpecimenFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("specimen_attribute_mapping").toString();
            specimenAttributeMapping=gson.fromJson(jsonAttributeMappingString,SpecimenFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return specimenAttributeMapping;
    }

    /**
     * Check if the DataElement exist already in the Event datavalues collection list
     * True if exist, False if not
     * @param oEvent
     * @param _idDataElement
     * @return
     */
    public static boolean checkDataElementInEvent(DhisEvent oEvent,String _idDataElement)
    {
        boolean found=false;
        for(dataValue oDataValue:oEvent.dataValues)
        {
            if(oDataValue.dataElement.equals(_idDataElement))
            {
                found=true;
                break;
            }
        }
        return found;
    }


}
