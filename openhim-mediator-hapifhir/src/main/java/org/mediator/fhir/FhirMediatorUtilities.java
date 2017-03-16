package org.mediator.fhir;

//import org.hl7.fhir.dstu3.model.Identifier;
//import org.hl7.fhir.dstu3.model.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import scala.util.parsing.combinator.testing.Ident;

import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

}
