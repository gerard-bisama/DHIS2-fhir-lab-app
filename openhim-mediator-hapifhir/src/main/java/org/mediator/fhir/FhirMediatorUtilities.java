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
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.google.gson.Gson;
import scala.util.parsing.combinator.testing.Ident;

import javax.tools.Diagnostic;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    ,List<Observation> listAssociatedObservation,List<TrackerResourceMap> listTrackerResource)
    {
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
                oEvent.program=listTrackerResource.get(0).programId;
                oEvent.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
                oEvent.trackedEntityInstance=trackedEntity;
                oEvent.programStage="aornzRfUbDZ";
                DateTimeDt oDateTime=(DateTimeDt)oObservation.getEffective();
                //cal.
                String builtDate="";
                builtDate=oDateTime.getYear().toString()+"-";
                builtDate+=oDateTime.getMonth().toString().length()>1?oDateTime.getMonth().toString():"0"+oDateTime.getMonth().toString();
                builtDate+="-";
                builtDate+=oDateTime.getDay().toString().length()>1?oDateTime.getDay().toString():"0"+oDateTime.getDay().toString();

                oEvent.eventDate=builtDate;

                String refSpecimenId=oObservation.getSpecimen().getReference().getIdPart().split("-")[0];
                oEvent.addDataValue("Ju7cS1sUwcF",refSpecimenId);
                String testResult=oObservation.getInterpretation().getText();
                String confirmedDisease=oObservation.getComments();
                oEvent.addDataValue("ovY6E8BSdto",testResult);
                oEvent.addDataValue("qIlO7yEpiVv",confirmedDisease);
                listOfEvent.add(oEvent);
                //if the current event is the lab result, it means that the request has
                //been made
                DhisEvent oEventRequested=new DhisEvent();
                oEventRequested.program=listTrackerResource.get(0).programId;
                oEventRequested.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
                oEventRequested.trackedEntityInstance=trackedEntity;
                oEventRequested.programStage="YLMRmho6SdY";
                Date oDate=(Date)oObservation.getIssued();
                DateTimeDt dateTimeIssued=new DateTimeDt(oDate);
                builtDate="";;
                builtDate=dateTimeIssued.getYear().toString()+"-";
                builtDate+=dateTimeIssued.getMonth().toString().length()>1?dateTimeIssued.getMonth().toString():"0"+dateTimeIssued.getMonth().toString();
                builtDate+="-";
                builtDate+=dateTimeIssued.getDay().toString().length()>1?dateTimeIssued.getDay().toString():"0"+dateTimeIssued.getDay().toString();

                oEventRequested.eventDate=builtDate;
                String refTestingLab=oObservation.getPerformer().get(0).getReference().getIdPart();
                oEventRequested.addDataValue("ZJTVYdXtUdo",refTestingLab);
                refSpecimenId="";
                refSpecimenId=oObservation.getSpecimen().getReference().getIdPart().split("-")[0];
                oEventRequested.addDataValue("Ju7cS1sUwcF",refSpecimenId);
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
                oEventRequested.addDataValue("kL7PTi4lRSl",refSpecimenType);
                listOfEvent.add(oEventRequested);


            }//End if oObservation.getStatus()
            else if (oObservation.getStatus().equals("registered"))
            {
                DhisEvent oEventRequested=new DhisEvent();
                oEventRequested.program=listTrackerResource.get(0).programId;
                oEventRequested.orgUnit=patientToProcess.getManagingOrganization().getReference().getIdPart();
                oEventRequested.trackedEntityInstance=trackedEntity;
                oEventRequested.programStage="YLMRmho6SdY";
                Date oDate=(Date)oObservation.getIssued();
                DateTimeDt dateTimeIssued=new DateTimeDt(oDate);
                String builtDate="";
                builtDate=dateTimeIssued.getYear().toString()+"-";
                builtDate+=dateTimeIssued.getMonth().toString().length()>1?dateTimeIssued.getMonth().toString():"0"+dateTimeIssued.getMonth().toString();
                builtDate+="-";
                builtDate+=dateTimeIssued.getDay().toString().length()>1?dateTimeIssued.getDay().toString():"0"+dateTimeIssued.getDay().toString();

                oEventRequested.eventDate=builtDate;
                String refTestingLab=oObservation.getPerformer().get(0).getReference().getIdPart();
                oEventRequested.addDataValue("ZJTVYdXtUdo",refTestingLab);
                String refSpecimenId="";
                refSpecimenId=oObservation.getSpecimen().getReference().getIdPart().split("-")[0];
                oEventRequested.addDataValue("Ju7cS1sUwcF",refSpecimenId);
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
                oEventRequested.addDataValue("kL7PTi4lRSl",refSpecimenType);
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
                                                                   List<DiagnosticReport> listAssociatedDiagnosticReport)
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
        oTrackedEntity.trackedEntity=trackedEntityId;
        oTrackedEntity.addAttribute("BChcyqBJCQN",patientToProcess.getId().getIdPart()
                .split("-")[0]);
        oTrackedEntity.addAttribute("sB1IHYu2xQT",patientToProcess.getName().get(0)
                .getFamily().get(0).toString());
        oTrackedEntity.addAttribute("OhGYnwya3jg",patientToProcess.getName().get(0)
                .getGiven().get(0).toString());
        if(patientToProcess.getBirthDate().toString()!="")
        {
            DateTimeDt dateTimeBirth=new DateTimeDt(patientToProcess.getBirthDate());
            String builtDate="";
            builtDate=dateTimeBirth.getYear().toString()+"-";
            builtDate+=dateTimeBirth.getMonth().toString().length()>1?dateTimeBirth.getMonth().toString():"0"+dateTimeBirth.getMonth().toString();
            builtDate+="-";
            builtDate+=dateTimeBirth.getDay().toString().length()>1?dateTimeBirth.getDay().toString():"0"+dateTimeBirth.getDay().toString();
            oTrackedEntity.addAttribute("UezutfURtQG",builtDate);

        }
        oTrackedEntity.addAttribute("ttjZl7jvrRj",patientToProcess.getAddress().get(0).getText());



        for(Condition oCondition:listAssociatedCondition)
        {
            if(oCondition.getCode().getText()!="")
            {
                oTrackedEntity.addAttribute("rpkGPScBEus",oCondition.getCode().getText());

            }
            if(oCondition.getCategory().getText()!="")
            {
                oTrackedEntity.addAttribute("TwGc5gV9ZXZ",oCondition.getCategory().getText());

            }

        }//End listAssociatedCondition
        for(DiagnosticReport oReport:listAssociatedDiagnosticReport)
        {
            if(oReport.getConclusion()!="")
            {
                oTrackedEntity.addAttribute("Rx2fEI9zDJ3",oReport.getConclusion());
            }

        }
        IDatatype oDeceased=patientToProcess.getDeceased();
        if (oDeceased.toString().equals("true"))
        {
            oTrackedEntity.addAttribute("Fs892x4qy0d","Dead");
        }
        else
        {
            oTrackedEntity.addAttribute("Fs892x4qy0d","Alive");
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


}
