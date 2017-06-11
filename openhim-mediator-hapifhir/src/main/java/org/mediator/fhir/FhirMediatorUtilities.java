package org.mediator.fhir;

//import org.hl7.fhir.dstu3.model.Identifier;
//import org.hl7.fhir.dstu3.model.Practitioner;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.resource.*;

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
            case "Basic":
                buildRequestPath="/Basic?_id=";
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
    public static List<DhisEvent> constructListOfLabEvents(Patient patientToProcess,List<Practitioner> listAssociatedPractitioner,
    List<Practitioner> listAssociatedPractitionerSpecimenHandler,
    List<Specimen> listAssociatedSpecimen,List<Condition> listAssociatedCondition,List<DiagnosticOrder> listAssociatedDiagnosticOrder
    ,List<Observation> listAssociatedObservation,List<DiagnosticReport> listAssociatedDiagnosticReport,
    List<ListResource>    listAssociatedListResource,TrackerResourceMap trackedProgramInfo
    ,List<DataElement> listDataElementMappingList,PractitionerFhirMapping practitionerMapping,PractitionerFhirMapping practitionerSpecimenHandlerMapping,
    SpecimenFhirMapping specimenMapping, ConditionFhirMapping conditionMapping,
    DiagnosticOrderFhirMapping diagnosticOrderMapping,ObservationFhirMapping observationMapping,
    DiagnosticReportFhirMapping diagnosticReportMapping,boolean specimenIdIsFormattedWithHyphen

    )
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
                DhisEvent oEvent=new DhisEvent();
                oEvent.program=trackedProgramInfo.id;
                oEvent.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
                oEvent.trackedEntityInstance=trackedEntity;
                oEvent.programStage=stageId;

                //Extract Practitioner DataElements
                for(Practitioner oPractitioner:listAssociatedPractitioner)
                {
                    for(DataElement oDataElement:listDataElementStage)
                    {
                        String displayName=oDataElement.displayName;
                        if(oPractitioner.getName().getGiven()!=null && practitionerMapping.name_given.equals(displayName))
                        {
                            if(oPractitioner.getName().getGiven().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getName().getGiven().get(0).getValue());
                            }
                            continue;
                        }
                        if (oPractitioner.getName().getFamily()!=null && practitionerMapping.name_family.equals(displayName))
                        {
                            if(oPractitioner.getName().getFamily().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getName().getFamily().get(0).getValue());
                            }
                            continue;

                        }
                        if (oPractitioner.getGender()!=null && practitionerMapping.gender.equals(displayName))
                        {
                            oEvent.addDataValue(oDataElement.id,oPractitioner.getGender());
                            continue;
                        }
                        if (oPractitioner.getTelecom()!=null && practitionerMapping.telecom_phone.equals(displayName))
                        {
                            if(oPractitioner.getTelecom().size()>0)
                            {
                                for(ContactPointDt oContactPoint:oPractitioner.getTelecom())
                                {
                                    if(oContactPoint.getSystem().equals("phone"))
                                    {
                                        oEvent.addDataValue(oDataElement.id,oContactPoint.getValue());
                                    }
                                }
                            }
                            continue;

                        }
                        if (oPractitioner.getTelecom()!=null && practitionerMapping.telecom_email.equals(displayName))
                        {
                            if(oPractitioner.getTelecom().size()>0)
                            {
                                for(ContactPointDt oContactPoint:oPractitioner.getTelecom())
                                {
                                    if(oContactPoint.getSystem().equals("email"))
                                    {
                                        oEvent.addDataValue(oDataElement.id,oContactPoint.getValue());
                                    }
                                }
                            }
                            continue;

                        }
                        if (oPractitioner.getAddress()!=null && practitionerMapping.address.equals(displayName))
                        {
                            if(oPractitioner.getAddress().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getAddress().get(0).getText());
                            }
                            continue;
                        }
                        if (oPractitioner.getPractitionerRole()!=null && practitionerMapping.practitionerRole_managingOrganization.equals(displayName))
                        {
                            if(oPractitioner.getPractitionerRole().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getPractitionerRole().get(0).getManagingOrganization().getReference().getIdPart());
                            }
                            continue;
                        }
                        if (oPractitioner.getPractitionerRole()!=null && practitionerMapping.practitionerRole_role.equals(displayName))
                        {
                            if(oPractitioner.getPractitionerRole().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getPractitionerRole().get(0).getRole().getText());
                            }
                            continue;
                        }
                        if (oPractitioner.getPractitionerRole()!=null && practitionerMapping.practitionerRole_specialty.equals(displayName))
                        {
                            if(oPractitioner.getPractitionerRole().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getPractitionerRole().get(0).getSpecialty().get(0).getText());
                            }
                            continue;
                        }

                    }//End For DataElement

                }//End for Practitioner
                //Extract Practitioner DataElements SpecimenHandler
                for(Practitioner oPractitioner:listAssociatedPractitionerSpecimenHandler)
                {
                    for(DataElement oDataElement:listDataElementStage)
                    {
                        String displayName=oDataElement.displayName;
                        if(oPractitioner.getName().getGiven()!=null && practitionerSpecimenHandlerMapping.name_given.equals(displayName))
                        {
                            if(oPractitioner.getName().getGiven().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getName().getGiven().get(0).getValue());
                            }
                            continue;
                        }
                        if (oPractitioner.getName().getFamily()!=null && practitionerSpecimenHandlerMapping.name_family.equals(displayName))
                        {
                            if(oPractitioner.getName().getFamily().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getName().getFamily().get(0).getValue());
                            }
                            continue;

                        }
                        if (oPractitioner.getGender()!=null && practitionerSpecimenHandlerMapping.gender.equals(displayName))
                        {
                            oEvent.addDataValue(oDataElement.id,oPractitioner.getGender());
                            continue;
                        }
                        if (oPractitioner.getTelecom()!=null && practitionerSpecimenHandlerMapping.telecom_phone.equals(displayName))
                        {
                            if(oPractitioner.getTelecom().size()>0)
                            {
                                for(ContactPointDt oContactPoint:oPractitioner.getTelecom())
                                {
                                    if(oContactPoint.getSystem().equals("phone"))
                                    {
                                        oEvent.addDataValue(oDataElement.id,oContactPoint.getValue());
                                    }
                                }
                            }
                            continue;

                        }
                        if (oPractitioner.getTelecom()!=null && practitionerSpecimenHandlerMapping.telecom_email.equals(displayName))
                        {
                            if(oPractitioner.getTelecom().size()>0)
                            {
                                for(ContactPointDt oContactPoint:oPractitioner.getTelecom())
                                {
                                    if(oContactPoint.getSystem().equals("email"))
                                    {
                                        oEvent.addDataValue(oDataElement.id,oContactPoint.getValue());
                                    }
                                }
                            }
                            continue;

                        }
                        if (oPractitioner.getAddress()!=null && practitionerSpecimenHandlerMapping.address.equals(displayName))
                        {
                            if(oPractitioner.getAddress().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getAddress().get(0).getText());
                            }
                            continue;
                        }
                        if (oPractitioner.getPractitionerRole()!=null && practitionerSpecimenHandlerMapping.practitionerRole_managingOrganization.equals(displayName))
                        {
                            if(oPractitioner.getPractitionerRole().size()>0)
                            {
                                    oEvent.addDataValue(oDataElement.id,oPractitioner.getPractitionerRole().get(0).getManagingOrganization().getReference().getIdPart());
                            }
                            continue;
                        }
                        if (oPractitioner.getPractitionerRole()!=null && practitionerSpecimenHandlerMapping.practitionerRole_role.equals(displayName))
                        {
                            if(oPractitioner.getPractitionerRole().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getPractitionerRole().get(0).getRole().getText());
                            }
                            continue;
                        }
                        if (oPractitioner.getPractitionerRole()!=null && practitionerSpecimenHandlerMapping.practitionerRole_specialty.equals(displayName))
                        {
                            if(oPractitioner.getPractitionerRole().size()>0)
                            {
                                oEvent.addDataValue(oDataElement.id,oPractitioner.getPractitionerRole().get(0).getSpecialty().get(0).getText());
                            }
                            continue;
                        }

                    }//End For DataElement

                }//End for Practitioner Specimen Handler
                for(Specimen oSpecimen: listAssociatedSpecimen) {
                    for (DataElement oDataElement : listDataElementStage) {
                        String displayName = oDataElement.displayName;
                        if (oSpecimen.getIdentifier().size() > 0 && specimenMapping.identifier.contains(displayName) == true) {
                            if (!checkDataElementInEvent(oEvent, oDataElement.id)) {
                                if (specimenIdIsFormattedWithHyphen) {
                                    oEvent.addDataValue(oDataElement.id, oSpecimen.getId().getIdPart().split("-")[0]);
                                } else {
                                    oEvent.addDataValue(oDataElement.id, oSpecimen.getId().getIdPart());
                                }
                            }
                            continue;
                        }
                        if (oSpecimen.getId() != null && specimenMapping.id.equals(displayName)) {
                            oEvent.addDataValue(oDataElement.id, oSpecimen.getId().getIdPart());
                            continue;
                        }
                        if (oSpecimen.getStatus() != null && specimenMapping.status.equals(displayName)) {
                            oEvent.addDataValue(oDataElement.id, oSpecimen.getStatus());
                            continue;
                        }
                        if (oSpecimen.getType() != null && specimenMapping.type.equals(displayName)) {
                            oEvent.addDataValue(oDataElement.id, oSpecimen.getType().getText());
                            continue;
                        }
                        if (oSpecimen.getAccessionIdentifier() != null && specimenMapping.accession.equals(displayName)) {
                            oEvent.addDataValue(oDataElement.id, oSpecimen.getAccessionIdentifier().getValue());
                            continue;
                        }
                        if (oSpecimen.getReceivedTime() != null && specimenMapping.receivedTime.equals(displayName)) {
                            DateTimeDt oDateTime = new DateTimeDt(oSpecimen.getReceivedTime());
                            String builtDate = "";
                            builtDate = oDateTime.getYear().toString() + "-";
                            builtDate += oDateTime.getMonth().toString().length() > 1 ? oDateTime.getMonth() + 1 : "0" + (oDateTime.getMonth() + 1);
                            builtDate += "-";
                            builtDate += oDateTime.getDay().toString().length() > 1 ? oDateTime.getDay().toString() : "0" + oDateTime.getDay().toString();
                            oEvent.addDataValue(oDataElement.id, builtDate);
                            continue;

                        }
                        if (oSpecimen.getCollection() != null && specimenMapping.collector.equals(displayName)) {
                            if (oSpecimen.getCollection().getCollector() != null) {
                                oEvent.addDataValue(oDataElement.id, oSpecimen.getCollection().getCollector().getReference().getIdPart());
                                continue;
                            }

                        }
                        if (oSpecimen.getCollection() != null && specimenMapping.collectedDateTime.equals(displayName)) {
                            if (oSpecimen.getCollection().getCollected() != null) {
                                DateTimeDt oDateTime = (DateTimeDt) oSpecimen.getCollection().getCollected();
                                String builtDate = "";
                                builtDate = oDateTime.getYear().toString() + "-";
                                builtDate += oDateTime.getMonth().toString().length() > 1 ? oDateTime.getMonth() + 1 : "0" + (oDateTime.getMonth() + 1);
                                builtDate += "-";
                                builtDate += oDateTime.getDay().toString().length() > 1 ? oDateTime.getDay().toString() : "0" + oDateTime.getDay().toString();
                                oEvent.addDataValue(oDataElement.id, builtDate);
                            }

                            continue;

                        }
                        if (oSpecimen.getCollection() != null && specimenMapping.collection_comment.equals(displayName)) {
                            if (oSpecimen.getCollection().getComment().size() > 0) {
                                oEvent.addDataValue(oDataElement.id, oSpecimen.getCollection().getComment().get(0).getValue());
                            }
                            continue;
                        }
                        if (oSpecimen.getCollection() != null && specimenMapping.collection_quantity_unit.equals(displayName)) {
                            if (oSpecimen.getCollection().getQuantity() != null) {
                                oEvent.addDataValue(oDataElement.id, oSpecimen.getCollection().getQuantity().getUnit());
                            }
                            continue;
                        }
                        if (oSpecimen.getCollection() != null && specimenMapping.collection_quantity_value.equals(displayName)) {
                            if (oSpecimen.getCollection().getQuantity() != null) {
                                oEvent.addDataValue(oDataElement.id, oSpecimen.getCollection().getQuantity().getValue().toString());
                            }
                            continue;
                        }
                        if (oSpecimen.getCollection() != null && specimenMapping.collection_method.equals(displayName)) {
                            if (oSpecimen.getCollection().getMethod() != null) {
                                oEvent.addDataValue(oDataElement.id, oSpecimen.getCollection().getMethod().getText());
                            }
                            continue;
                        }
                        if (oSpecimen.getCollection() != null && specimenMapping.collection_bodySite.equals(displayName)) {
                            if (oSpecimen.getCollection().getBodySite() != null) {
                                oEvent.addDataValue(oDataElement.id, oSpecimen.getCollection().getBodySite().getText());
                            }
                            continue;

                        }
                        if (oSpecimen.getTreatment().size() > 0 && specimenMapping.traitment_description.equals(displayName)) {
                            oEvent.addDataValue(oDataElement.id, oSpecimen.getTreatment().get(0).getDescription());
                            continue;
                        }
                        if (oSpecimen.getTreatment().size() > 0 && specimenMapping.traitment_procedure.equals(displayName)) {
                            oEvent.addDataValue(oDataElement.id, oSpecimen.getTreatment().get(0).getProcedure().getText());
                            continue;
                        }

                    }//End For dataElement
                }//End for Specimen
                for(Observation oObservation: listAssociatedObservation)
                {
                    for(DataElement oDataElement:listDataElementStage)
                    {
                        String displayName=oDataElement.displayName;
                        if(oObservation.getId()!=null && observationMapping.id.equals(displayName))
                        {
                            oEvent.addDataValue(oDataElement.id,oObservation.getId().getIdPart());
                            continue;
                        }
                        if(oObservation.getIdentifier().size()>0 && observationMapping.identifier.equals(displayName))
                        {
                            oEvent.addDataValue(oDataElement.id,oObservation.getIdentifier().get(0).getValue());
                            continue;
                        }
                        if(oObservation.getStatus()!=null && observationMapping.status.equals(displayName))
                        {
                            oEvent.addDataValue(oDataElement.id,oObservation.getStatus());
                            continue;
                        }
                        if(oObservation.getCategory()!=null && observationMapping.category.equals(displayName))
                        {
                            oEvent.addDataValue(oDataElement.id,oObservation.getCategory().getText());
                            continue;
                        }
                        if(oObservation.getCode()!=null && observationMapping.code.equals(displayName))
                        {
                            if(oObservation.getCode().getText()!=null) {
                                oEvent.addDataValue(oDataElement.id, oObservation.getCode().getText());
                            }
                            continue;
                        }
                        if(oObservation.getEffective()!=null && observationMapping.effectiveDateTime.equals(displayName))
                        {
                            DateTimeDt oDateTime=(DateTimeDt)oObservation.getEffective();
                            String builtDate="";
                            builtDate=oDateTime.getYear().toString()+"-";
                            builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth()+1:"0"+(oDateTime.getMonth()+1);
                            builtDate+="-";
                            builtDate+=oDateTime.getDay().toString().length()>1?oDateTime.getDay().toString():"0"+oDateTime.getDay().toString();
                            if(oDataElement.id.equals("eventDate"))
                            {
                                oEvent.eventDate=builtDate;
                            }
                            else
                            {
                                oEvent.addDataValue(oDataElement.id,builtDate);
                            }
                            continue;
                        }
                        if(oObservation.getIssued()!=null && observationMapping.issued.equals(displayName))
                        {
                            DateTimeDt oDateTime=new DateTimeDt(oObservation.getIssued());
                            String builtDate="";
                            builtDate=oDateTime.getYear().toString()+"-";
                            builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth()+1:"0"+(oDateTime.getMonth()+1);
                            builtDate+="-";
                            builtDate+=oDateTime.getDay().toString().length()>1?oDateTime.getDay().toString():"0"+oDateTime.getDay().toString();
                            //oEvent.eventDate=builtDate;
                            if(oDataElement.id.equals("eventDate"))
                            {
                                oEvent.eventDate=builtDate;
                            }
                            else
                            {
                                oEvent.addDataValue(oDataElement.id,builtDate);
                            }
                            continue;
                        }
                        if(oObservation.getPerformer()!=null && observationMapping.performer.equals(displayName))
                        {
                            oEvent.addDataValue(oDataElement.id,oObservation.getPerformer().get(0).getReference().getIdPart());
                            continue;
                        }
                        if(oObservation.getValue()!=null && observationMapping.valueQuantity_value.equals(displayName))
                        {
                            IDatatype dataValue=oObservation.getValue();
                            oEvent.addDataValue(oDataElement.id,dataValue.toString());
                            continue;
                        }
                        if(oObservation.getSpecimen()!=null && observationMapping.specimen.equals(displayName))
                        {
                            if(oObservation.getSpecimen().getReference()!=null) {
                                if (specimenIdIsFormattedWithHyphen) {
                                    if(oObservation.getSpecimen().getReference().getIdPart()!=null)
                                    {
                                        String refSpecimenId = oObservation.getSpecimen().getReference().getIdPart().split("-")[0];
                                        oEvent.addDataValue(oDataElement.id, refSpecimenId);

                                    }

                                } else {
                                    if(oObservation.getSpecimen().getReference().getIdPart()!=null) {
                                        String refSpecimenId = oObservation.getSpecimen().getReference().getIdPart();
                                        oEvent.addDataValue(oDataElement.id, refSpecimenId);
                                    }
                                }
                            }
                            continue;
                        }
                        if(oObservation.getInterpretation()!=null && observationMapping.interpretation.equals(displayName))
                        {
                            if(oObservation.getInterpretation().getText()!=null) {
                                oEvent.addDataValue(oDataElement.id, oObservation.getInterpretation().getText());
                            }
                            continue;

                        }
                        if(oObservation.getComments()!=null && observationMapping.comments.equals(displayName))
                        {
                            if(oObservation.getComments()!="") {
                                oEvent.addDataValue(oDataElement.id, oObservation.getComments());
                            }
                            continue;

                        }
                        if(oObservation.getMethod()!=null && observationMapping.method.equals(displayName))
                        {
                            oEvent.addDataValue(oDataElement.id,oObservation.getMethod().getText()  );
                            continue;

                        }

                    }

                    //listOfEvent.add(oEvent);

                }//End for Observation
                for(ListResource oListResource:listAssociatedListResource)
                {

                    for(DataElement oDataElement:listDataElementStage) {
                        String displayName = oDataElement.displayName;
                        if(oListResource.getTitle().equals(displayName))
                        {
                            if(oDataElement.id.equals("eventDate"))
                            {
                                DateTimeDt oDateTime=new DateTimeDt(oListResource.getNote());
                                String builtDate="";
                                builtDate=oDateTime.getYear().toString()+"-";
                                builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth()+1:"0"+(oDateTime.getMonth()+1);
                                builtDate+="-";
                                builtDate+=oDateTime.getDay().toString().length()>1?oDateTime.getDay().toString():"0"+oDateTime.getDay().toString();

                                oEvent.eventDate=builtDate;
                            }
                            else {
                                oEvent.addDataValue(oDataElement.id, oListResource.getNote());
                            }
                        }
                    }
                }//End for resource
                if(oEvent.dataValues.size()>0) {
                    listOfEvent.add(oEvent);
                }
            }//End if listDataElementStage
        }//End for StageId

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
                builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth()+1:"0"+(oDateTime.getMonth()+1);
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
    public static String TransformListEventToJson(List<DhisEvent> listEvents,String trackedEntityInstance)
    {
        Gson gson=new Gson();
        String jsonString="{\"events\":[";
        int compter=0;
        for(DhisEvent oEvent : listEvents)
        {
            if(trackedEntityInstance!=null)
            {
                oEvent.trackedEntityInstance=trackedEntityInstance;
            }
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
    private static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }
    public static List<DhisTrackedEntity> constructListOfTrackedEntityInstance(Patient patientToProcess,String trackedEntityId,List<Condition> listAssociatedCondition,
                                                                               List<DiagnosticReport> listAssociatedDiagnosticReport,List<DataElement> listDataElementMappingList,
                                                                               PatientFhirMapping oPatientMapping,
                                                                               ConditionFhirMapping oConditionMapping,
                                                                               DiagnosticReportFhirMapping oDiagnosticReportMapping,boolean patientIdIsFormattedWithHyphen)
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
        //Extract Patient attribute information
        for(DataElement oDataElement:listDataElementMappingList) {
            String displayName = oDataElement.displayName;
            if(patientToProcess.getId()!=null && oPatientMapping.id.equals(displayName))
            {
                if (patientIdIsFormattedWithHyphen) {
                    oTrackedEntity.addAttribute(oDataElement.id, patientToProcess.getId().getIdPart()
                            .split("-")[0]);
                } else {
                    oTrackedEntity.addAttribute(oDataElement.id, patientToProcess.getId().getIdPart());
                }
            }
            else if (patientToProcess.getIdentifier().size() > 0 && oPatientMapping.identifier.equals(displayName)) {
                for (IdentifierDt oIdentifier : patientToProcess.getIdentifier()) {
                    if (oIdentifier.getType().getText().equals(displayName)) {
                        oTrackedEntity.addAttribute(oDataElement.id, oIdentifier.getValue());

                    }
                }
            } else if (patientToProcess.getName() != null && oPatientMapping.name_family.equals(displayName)) {
                if (patientToProcess.getName().get(0).getFamily().get(0) != null) {
                    oTrackedEntity.addAttribute(oDataElement.id, patientToProcess.getName().get(0)
                            .getFamily().get(0).toString());
                }
            } else if (patientToProcess.getName() != null && oPatientMapping.name_given.equals(displayName)) {
                if (patientToProcess.getName().get(0).getGiven().get(0) != null) {
                    oTrackedEntity.addAttribute(oDataElement.id, patientToProcess.getName().get(0)
                            .getGiven().get(0).toString());
                }
            } else if (patientToProcess.getBirthDate() != null && oPatientMapping.birthDate.equals(displayName)) {

                DateTimeDt dateTimeBirth = new DateTimeDt(patientToProcess.getBirthDate());
                String builtDate = "";
                builtDate = dateTimeBirth.getYear().toString() + "-";
                builtDate += dateTimeBirth.getMonth().toString().length() > 1 ? dateTimeBirth.getMonth() + 1 : "0" + (dateTimeBirth.getMonth() + 1);
                builtDate += "-";
                builtDate += dateTimeBirth.getDay().toString().length() > 1 ? dateTimeBirth.getDay().toString() : "0" + dateTimeBirth.getDay().toString();
                /*
                Date date= new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int currentYear = cal.get(Calendar.YEAR);
                int age=currentYear-dateTimeBirth.getYear();
                 */
                oTrackedEntity.addAttribute(oDataElement.id, builtDate);
            } else if (patientToProcess.getAddress() != null && oPatientMapping.address_text.equals(displayName)) {
                if (patientToProcess.getAddress().size() > 0) {
                    oTrackedEntity.addAttribute(oDataElement.id, patientToProcess.getAddress().get(0).getText());

                }

            } else if (patientToProcess.getAddress() != null && oPatientMapping.address_city.equals(displayName)) {
                if (patientToProcess.getAddress().size() > 0) {
                    oTrackedEntity.addAttribute(oDataElement.id, patientToProcess.getAddress().get(0).getCity());

                }

            } else if (patientToProcess.getDeceased() != null && oPatientMapping.deceasedBoolean.equals(displayName)) {
                IDatatype oDeceased = patientToProcess.getDeceased();
                if (oDeceased != null) {

                    if (oDeceased.toString().equals("true")) {
                        oTrackedEntity.addAttribute(oDataElement.id, "99");
                    } else {
                        oTrackedEntity.addAttribute(oDataElement.id, "1");
                    }
                }
            }
            else if (patientToProcess.getGender()!=null && oPatientMapping.gender.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, capitalize(patientToProcess.getGender()));
            }
            else if(patientToProcess.getTelecom().size()>0 && oPatientMapping.telecom_phone.equals(displayName))
            {

                for(ContactPointDt oContactPoint:patientToProcess.getTelecom())
                {
                    if(oContactPoint.getSystem().equals("phone"))
                    {
                        oTrackedEntity.addAttribute(oDataElement.id, oContactPoint.getValue());
                    }
                }
            }
            else if(patientToProcess.getTelecom().size()>0 && oPatientMapping.telecom_email.equals(displayName))
            {

                for(ContactPointDt oContactPoint:patientToProcess.getTelecom())
                {
                    if(oContactPoint.getSystem().equals("email"))
                    {
                        oTrackedEntity.addAttribute(oDataElement.id, oContactPoint.getValue());
                    }
                }
            }
        }
        for(DataElement oDataElement:listDataElementMappingList) {

            String displayName = oDataElement.displayName;
            if (trackedCondition.getId() != null && oConditionMapping.id.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, trackedCondition.getId().getIdPart());
            } else if (trackedCondition.getIdentifier().size() > 0 && oConditionMapping.identifier.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, trackedCondition.getIdentifier().get(0).getValue());
            } else if (trackedCondition.getDateRecorded() != null && oConditionMapping.dateRecorded.equals(displayName)) {
                DateTimeDt dateTimeBirth = new DateTimeDt(trackedCondition.getDateRecorded());
                String builtDate = "";
                builtDate = dateTimeBirth.getYear().toString() + "-";
                builtDate += dateTimeBirth.getMonth().toString().length() > 1 ? dateTimeBirth.getMonth() + 1 : "" + dateTimeBirth.getMonth() + 1;
                builtDate += "-";
                builtDate += dateTimeBirth.getDay().toString().length() > 1 ? dateTimeBirth.getDay().toString() : "0" + dateTimeBirth.getDay().toString();
                oTrackedEntity.addAttribute(oDataElement.id, builtDate);

            } else if (trackedCondition.getCode() != null && oConditionMapping.code.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, trackedCondition.getCode().getText());
            } else if (trackedCondition.getCategory() != null && oConditionMapping.category.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, trackedCondition.getCategory().getText());
            } else if (trackedCondition.getClinicalStatus() != null && oConditionMapping.clinicalStatus.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, trackedCondition.getClinicalStatus());
            } else if (trackedCondition.getVerificationStatus() != null && oConditionMapping.verificationStatus.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, trackedCondition.getVerificationStatus());
            } else if (trackedCondition.getSeverity() != null && oConditionMapping.severity.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, trackedCondition.getSeverity().getText());
            } else if (trackedCondition.getOnset() != null && oConditionMapping.onsetDateTime.equals(displayName)) {
                DateTimeDt dateTimeBirth = (DateTimeDt) trackedCondition.getOnset();
                String builtDate = "";
                builtDate = dateTimeBirth.getYear().toString() + "-";
                builtDate += dateTimeBirth.getMonth().toString().length() > 1 ? dateTimeBirth.getMonth() + 1 : "" + dateTimeBirth.getMonth() + 1;
                builtDate += "-";
                builtDate += dateTimeBirth.getDay().toString().length() > 1 ? dateTimeBirth.getDay().toString() : "0" + dateTimeBirth.getDay().toString();
                oTrackedEntity.addAttribute(oDataElement.id, builtDate);
            } else if (trackedCondition.getNotes() != null && oConditionMapping.notes.equals(displayName)) {
                oTrackedEntity.addAttribute(oDataElement.id, trackedCondition.getNotes());
            }
        }

        for(DataElement oDataElement:listDataElementMappingList) {

            String displayName = oDataElement.displayName;
            if ((trackedDiagReport.getId()!=null && oDiagnosticReportMapping.id.equals(displayName)))
            {
                oTrackedEntity.addAttribute(oDataElement.id,trackedDiagReport.getId().getIdPart());
            }
            else if ((trackedDiagReport.getStatus()!="" && oDiagnosticReportMapping.status.equals(displayName)))
            {
                oTrackedEntity.addAttribute(oDataElement.id,trackedDiagReport.getStatus());
            }
            else if ((trackedDiagReport.getCategory()!=null && oDiagnosticReportMapping.category.equals(displayName)))
            {
                oTrackedEntity.addAttribute(oDataElement.id,trackedDiagReport.getCategory().getText());
            }
            else if ((trackedDiagReport.getCode()!=null && oDiagnosticReportMapping.code.equals(displayName)))
            {
                oTrackedEntity.addAttribute(oDataElement.id,trackedDiagReport.getCode().getText());
            }
            else if ((trackedDiagReport.getEffective()!=null && oDiagnosticReportMapping.effectiveDateTime.equals(displayName)))
            {
                DateTimeDt dateTimeBirth = (DateTimeDt) trackedDiagReport.getEffective();
                String builtDate = "";
                builtDate = dateTimeBirth.getYear().toString() + "-";
                builtDate += dateTimeBirth.getMonth().toString().length() > 1 ? dateTimeBirth.getMonth() + 1 : "" + dateTimeBirth.getMonth() + 1;
                builtDate += "-";
                builtDate += dateTimeBirth.getDay().toString().length() > 1 ? dateTimeBirth.getDay().toString() : "0" + dateTimeBirth.getDay().toString();
                oTrackedEntity.addAttribute(oDataElement.id, builtDate);
            }
            else if ((trackedDiagReport.getIssued()!=null && oDiagnosticReportMapping.issued.equals(displayName)))
            {
                DateTimeDt dateTimeBirth = new DateTimeDt(trackedDiagReport.getIssued());
                String builtDate = "";
                builtDate = dateTimeBirth.getYear().toString() + "-";
                builtDate += dateTimeBirth.getMonth().toString().length() > 1 ? dateTimeBirth.getMonth() + 1 : "" + dateTimeBirth.getMonth() + 1;
                builtDate += "-";
                builtDate += dateTimeBirth.getDay().toString().length() > 1 ? dateTimeBirth.getDay().toString() : "0" + dateTimeBirth.getDay().toString();
                oTrackedEntity.addAttribute(oDataElement.id, builtDate);
            }
            else if ((trackedDiagReport.getConclusion()!=null && oDiagnosticReportMapping.conclusion.equals(displayName)))
            {
                oTrackedEntity.addAttribute(oDataElement.id, trackedDiagReport.getConclusion());
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
    //public static String
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
    public static PractitionerFhirMapping getPractitionerMappingAttributes (String filePath) throws Exception
    {
        PractitionerFhirMapping practitionerAttributeMapping=new PractitionerFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("practitioner_attribute_mapping").toString();
            practitionerAttributeMapping=gson.fromJson(jsonAttributeMappingString,PractitionerFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return practitionerAttributeMapping;
    }
    public static PractitionerFhirMapping getPractitionerSpecimenHandlerMappingAttributes (String filePath) throws Exception
    {
        PractitionerFhirMapping practitionerAttributeMapping=new PractitionerFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("practitioner_specimen_handling_attribute_mapping").toString();
            practitionerAttributeMapping=gson.fromJson(jsonAttributeMappingString,PractitionerFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return practitionerAttributeMapping;
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
    public static DiagnosticOrderFhirMapping getDiagnosticOrderMappingAttributes (String filePath) throws Exception
    {
        DiagnosticOrderFhirMapping diagnosticOrderAttributeMapping=new DiagnosticOrderFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("order_attribute_mapping").toString();
            diagnosticOrderAttributeMapping=gson.fromJson(jsonAttributeMappingString,DiagnosticOrderFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return diagnosticOrderAttributeMapping;
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
    public static ListResourceFhirMapping getListResourceMappingAttributes (String filePath) throws Exception
    {
        ListResourceFhirMapping listResourceAttributeMapping=new ListResourceFhirMapping();
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("list_attribute_mapping").toString();
            listResourceAttributeMapping=gson.fromJson(jsonAttributeMappingString,ListResourceFhirMapping.class);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return listResourceAttributeMapping;
    }

    /**
     * Check if PatientIdIsFormatted with hyphen
     * @param filePath
     * @return
     * @throws Exception
     */
    public static boolean checkPatientIdFormatWithHyphen (String filePath) throws Exception
    {
        boolean res;
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("format_patientid_hyphen").toString();
            res=Boolean.parseBoolean(jsonAttributeMappingString);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return res;
    }
    public static boolean checkSpecimenIdFormatWithHyphen (String filePath) throws Exception
    {
        boolean res;
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(new FileReader(filePath), JsonElement.class);
            String jsonAttributeMappingString=((JsonObject)json).get("format_specimenid_hyphen").toString();
            res=Boolean.parseBoolean(jsonAttributeMappingString);
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return res;
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
    public static String getIdReferenceFromPOSTResonse (String response) throws Exception
    {
        String res=null;
        Gson gson=new Gson();
        try {
            JsonElement json=gson.fromJson(response, JsonElement.class);
            String jsonAttributeString=((JsonObject)json).get("httpStatus").toString();
            if(jsonAttributeString.equals("\"OK\""))
            {
                res=((JsonObject)gson.fromJson(response, JsonElement.class)).get("response").getAsJsonObject().get("importSummaries").getAsJsonArray().get(0).getAsJsonObject().get("reference").toString();
            }
            //System.out.print(0);
        }
        catch (Exception exc)
        {
            throw new Exception(exc.toString());
        }
        return res;
    }


}
