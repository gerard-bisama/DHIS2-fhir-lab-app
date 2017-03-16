package org.mediator.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.HTTPVerbEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Organization;
import ca.uhn.fhir.model.dstu2.resource.Specimen;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
//import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;


//import ca.uhn.fhir.model.api.R;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by server-hit on 10/22/16.
 */
public class FhirResourceValidator  {

    public void set_jsonResource(StringBuilder _jsonResource) {
        this._jsonResource = _jsonResource;
    }

    StringBuilder _jsonResource;

    public FhirContext getContext() {
        return _context;
    }

    FhirContext _context;
    IParser _parser;

    public List<Practitioner> getListOfPractitioners() {
        return listOfPractitioners;
    }

    List<Practitioner> listOfPractitioners=new ArrayList<Practitioner>();

    public List<Practitioner> getListOfInvalidePractitioners() {
        return listOfInvalidPractitioners;
    }

    public List<Practitioner> getListOfValidePractitioners() {
        return listOfValidPractitioners;
    }

    List<Practitioner> listOfValidPractitioners= new ArrayList<Practitioner>();

    public List<Patient> getListOfValidPatient() {
        return listOfValidPatient;
    }

    List<Patient> listOfValidPatient= new ArrayList<>();

    public List<Organization> getListOfValidOrganization() {
        return listOfValidOrganization;
    }

    List<Organization> listOfValidOrganization= new ArrayList<>();

    public List<Specimen> getListOfValidSpecimen() {
        return listOfValidSpecimen;
    }

    List<Specimen> listOfValidSpecimen= new ArrayList<>();

    public List<DiagnosticOrder> getListOfValidDiagnosticOrder() {
        return listOfValidDiagnosticOrder;
    }

    List<DiagnosticOrder> listOfValidDiagnosticOrder= new ArrayList<>();

    public List<DiagnosticReport> getListOfValidDiagnosticReport() {
        return listOfValidDiagnosticReport;
    }

    List<DiagnosticReport> listOfValidDiagnosticReport= new ArrayList<>();

    public List<Observation> getListOfValidObservation() {
        return listOfValidObservation;
    }

    List<Observation> listOfValidObservation= new ArrayList<>();

    public StringBuilder get_jsonResource() {
        return _jsonResource;
    }

    List<Practitioner> listOfInvalidPractitioners= new ArrayList<Practitioner>();

    public List<Patient> getListOfInvalidPatient() {
        return listOfInvalidPatient;
    }

    List<Patient> listOfInvalidPatient= new ArrayList<>();

    public List<Organization> getListOfInvalidOrganization() {
        return listOfInvalidOrganization;
    }

    List<Organization> listOfInvalidOrganization= new ArrayList<>();

    public List<Specimen> getListOfInvalidSpecimen() {
        return listOfInvalidSpecimen;
    }

    List<Specimen> listOfInvalidSpecimen= new ArrayList<>();

    public List<DiagnosticOrder> getListOfInvalidDiagnosticOrder() {
        return listOfInvalidDiagnosticOrder;
    }

    List<DiagnosticOrder> listOfInvalidDiagnosticOrder= new ArrayList<>();

    public List<DiagnosticReport> getListOfInvalidDiagnosticReport() {
        return listOfInvalidDiagnosticReport;
    }

    List<DiagnosticReport> listOfInvalidDiagnosticReport= new ArrayList<>();

    public List<Observation> getListOfInvalidObservation() {
        return listOfInvalidObservation;
    }

    List<Observation> listOfInvalidObservation= new ArrayList<>();

    public List<Patient> getListOfPatient() {
        return listOfPatient;
    }

    List<Patient> listOfPatient=new ArrayList<Patient>();

    public List<Organization> getListOfOrganization() {
        return listOfOrganization;
    }

    List<Organization> listOfOrganization=new ArrayList<Organization>();

    public List<Specimen> getListOfSpecimen() {
        return listOfSpecimen;
    }

    List<Specimen> listOfSpecimen=new ArrayList<Specimen>();


    public List<Observation> getListOfObservation() {
        return listOfObservation;
    }

    List<Observation> listOfObservation=new ArrayList<Observation>();

    public List<DiagnosticOrder> getListOfDiagnosticOrder() {
        return listOfDiagnosticOrder;
    }

    List<DiagnosticOrder> listOfDiagnosticOrder=new ArrayList<DiagnosticOrder>();

    public List<DiagnosticReport> getListOfDiagnosticReport() {
        return listOfDiagnosticReport;
    }

    List<DiagnosticReport> listOfDiagnosticReport=new ArrayList<DiagnosticReport>();

    public FhirResourceValidator(StringBuilder jsonResource)
    {
        this._jsonResource=jsonResource;
        _context=FhirContext.forDstu2();
        _parser=_context.newJsonParser();
    }
    public FhirResourceValidator()
    {
        this._jsonResource=new StringBuilder();
        _context=FhirContext.forDstu2();
        _parser=_context.newJsonParser();
    }
    public boolean processBundleJsonResource(String filePathError) throws Exception
    {
        boolean isProcessed=false;
        /*
        if(this._jsonResource.toString().length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,this._jsonResource.toString());
        if(oBundle!=null )
        {
            if(oBundle.getEntry().size()==0)
            {
                throw new Exception("No entries found in the Bundle");
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processPractitionerBundle();
                listOfPractitioners=fhirProcessor.getListOfPractitioner();

                //Get the Valide Resource
                FhirValidator validator=this._context.newValidator();
                ResourcesValidation resValidation=new ResourcesValidation(validator,listOfPractitioners);
                resValidation.startValidation();
                listOfValidePractitioners=resValidation.getlistOfValidePractitioner();
                listOfInvalidePractitioners=resValidation.getlistOfInvalidePractitioner();
                if(listOfInvalidePractitioners.size()>0)
                {
                    //Create a new Bundle
                    Bundle invalidResourceBundle=new Bundle();
                    invalidResourceBundle.setType(Bundle.BundleType.COLLECTION);
                    for(Practitioner invalidPractitioner: listOfInvalidePractitioners)
                    {
                        Bundle.BundleEntryComponent oBundleEntry= new Bundle.BundleEntryComponent().setResource(invalidPractitioner);
                        invalidResourceBundle.addEntry(oBundleEntry);
                    }
                    char[] stringArray= _parser.encodeResourceToString(invalidResourceBundle).toCharArray();
                    StringBuilder strBuilder=new StringBuilder();
                    int compter=0;
                    for(char oCharacter:stringArray)
                    {
                        strBuilder.append(oCharacter);
                        compter++;
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                    Date currentDate = new Date();
                    String fileName="bundle_invalide_practitioners-"+ dateFormat.format(currentDate);
                    filePathError+="/"+fileName;
                    ManageJsonFile.saveResourceInJSONFile(strBuilder,compter,filePathError);
                }
                isProcessed=true;
            }
        }
        else
        {
            throw new Exception("Failed to parse the json string to Bundle");
        }*/
        return isProcessed;
    }
    public boolean processBundleJsonResource(String filePathError,String sourceServerURI) throws Exception
    {
        boolean isProcessed=false;

        if(this._jsonResource.toString().length()<2)
        {
            throw new Exception("Invalid  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,this._jsonResource.toString());
        if(oBundle!=null )
        {
            if(oBundle.getEntry().size()==0)
            {
                throw new Exception("No entries found in the Bundle");
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                listOfPractitioners=fhirProcessor.getListOfPractitioner();
                listOfPatient=fhirProcessor.getListOfPatient();
                listOfOrganization=fhirProcessor.getListOfOrganization();
                listOfSpecimen=fhirProcessor.getListOfSpecimen();
                listOfDiagnosticOrder=fhirProcessor.getListOfDiagnosticOrder();
                listOfObservation=fhirProcessor.getListOfObservation();
                listOfDiagnosticReport=fhirProcessor.getListOfDiagnosticReport();

                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);

                    //There is additional ressource to Extract
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    listOfPractitioners.addAll(fhirProcessor.getListOfPractitioner());
                    listOfPatient.addAll(fhirProcessor.getListOfPatient());
                    listOfOrganization.addAll(fhirProcessor.getListOfOrganization());
                    listOfSpecimen.addAll(fhirProcessor.getListOfSpecimen());
                    listOfDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                    listOfObservation.addAll(fhirProcessor.getListOfObservation());
                    listOfDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        listOfPractitioners.addAll(fhirProcessor.getListOfPractitioner());
                        listOfPatient.addAll(fhirProcessor.getListOfPatient());
                        listOfOrganization.addAll(fhirProcessor.getListOfOrganization());
                        listOfSpecimen.addAll(fhirProcessor.getListOfSpecimen());
                        listOfDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                        listOfObservation.addAll(fhirProcessor.getListOfObservation());
                        listOfDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                        //nextPageBundle=subNextBundle.copy();
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
                //Get the Valide Resource
                FhirValidator validator=this._context.newValidator();
                ResourcesValidation resValidation=new ResourcesValidation(validator);
                resValidation.setListOfPractitionersToValidate(listOfPractitioners);
                resValidation.setListOfPatientToValidate(listOfPatient);
                resValidation.setListOfOrganizationToValidate(listOfOrganization);
                resValidation.setListOfSpecimenToValidate(listOfSpecimen);
                resValidation.setListOfDiagnosticOrderToValidate(listOfDiagnosticOrder);
                resValidation.setListOfDiagnosticReportToValidate(listOfDiagnosticReport);
                resValidation.setListOfObservationToValidate(listOfObservation);

                resValidation.startValidation();
                //Validation results
                listOfValidPractitioners=resValidation.getlistOfValidePractitioner();
                listOfValidPatient=resValidation.getListOfValidPatient();
                listOfValidOrganization=resValidation.getListOfValidOrganization();
                listOfValidSpecimen=resValidation.getListOfValidSpecimen();
                listOfValidDiagnosticOrder=resValidation.getListOfValidDiagnosticOrder();
                listOfValidDiagnosticReport=resValidation.getListOfValidDiagnosticReport();
                listOfValidObservation=resValidation.getListOfValidObservation();
                //
                listOfInvalidPractitioners=resValidation.getlistOfInvalidePractitioner();
                listOfInvalidPatient=resValidation.getListOfInvalidPatient();
                listOfInvalidOrganization=resValidation.getListOfInvalidOrganization();
                listOfInvalidSpecimen=resValidation.getListOfInvalidSpecimen();
                listOfInvalidDiagnosticOrder=resValidation.getListOfInvalidDiagnosticOrder();
                listOfInvalidDiagnosticReport=resValidation.getListOfInvalidDiagnosticReport();
                listOfInvalidObservation=resValidation.getListOfInvalidObservation();

                if(listOfInvalidPractitioners.size()>0 || listOfInvalidPatient.size()>0 || listOfInvalidOrganization.size()>0
                        || listOfInvalidSpecimen.size()>0 || listOfInvalidDiagnosticOrder.size()>0 || listOfInvalidDiagnosticReport.size()>0
                        || listOfInvalidObservation.size()>0)
                {
                    //Create a new Bundle
                    Bundle invalidResourceBundle=new Bundle();
                    //invalidResourceBundle.setType(Bundle.BundleType.COLLECTION);
                    invalidResourceBundle.setType(BundleTypeEnum.COLLECTION);
                    for(Practitioner invalidPractitioner: listOfInvalidPractitioners)
                    {
                        Entry oBundleEntry=new Entry().setResource(invalidPractitioner);
                        invalidResourceBundle.addEntry(oBundleEntry);
                    }
                    for(Patient invalidPatient: listOfInvalidPatient)
                    {
                        Entry oBundleEntry=new Entry().setResource(invalidPatient);
                        invalidResourceBundle.addEntry(oBundleEntry);
                    }
                    for(Organization invalidOrganization: listOfInvalidOrganization)
                    {
                        Entry oBundleEntry=new Entry().setResource(invalidOrganization);
                        invalidResourceBundle.addEntry(oBundleEntry);
                    }
                    for(Specimen invalidSpecimen: listOfInvalidSpecimen)
                    {
                        Entry oBundleEntry=new Entry().setResource(invalidSpecimen);
                        invalidResourceBundle.addEntry(oBundleEntry);
                    }
                    for(DiagnosticOrder invalidDiagnosticOrder: listOfInvalidDiagnosticOrder)
                    {
                        Entry oBundleEntry=new Entry().setResource(invalidDiagnosticOrder);
                        invalidResourceBundle.addEntry(oBundleEntry);
                    }
                    for(DiagnosticReport invalidDiagnosticReport: listOfInvalidDiagnosticReport)
                    {
                        Entry oBundleEntry=new Entry().setResource(invalidDiagnosticReport);
                        invalidResourceBundle.addEntry(oBundleEntry);
                    }
                    for(Observation invalidObservation: listOfInvalidObservation)
                    {
                        Entry oBundleEntry=new Entry().setResource(invalidObservation);
                        invalidResourceBundle.addEntry(oBundleEntry);
                    }
                    char[] stringArray= _parser.encodeResourceToString(invalidResourceBundle).toCharArray();
                    StringBuilder strBuilder=new StringBuilder();
                    int compter=0;
                    for(char oCharacter:stringArray)
                    {
                        strBuilder.append(oCharacter);
                        compter++;
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
                    Date currentDate = new Date();
                    String fileName="bundle_invalide_trackedentities-"+ dateFormat.format(currentDate)+".json";
                    filePathError+="/"+fileName;
                    ManageJsonFile.saveResourceInJSONFile(strBuilder,compter,filePathError);
                }
                isProcessed=true;
            }
        }
        else
        {
            throw new Exception("Failed to parse the json string to Bundle");
        }
        return isProcessed;
    }
    private Bundle CreateBundleCopy(Bundle oBundle)
    {
        Bundle copy=new Bundle();
        copy.setId(oBundle.getId());
        copy.setResourceMetadata(oBundle.getResourceMetadata());
        copy.setLanguage(oBundle.getLanguage());
        //Iterate in the Link
        if(oBundle.getLink()!=null)
        {
            Iterator oIterator=oBundle.getLink().iterator();
            while(oIterator.hasNext()) {
                Bundle.Link oLink=(Bundle.Link)oIterator.next();
                copy.addLink(oLink);

            }
        }
        if(oBundle.getEntry()!=null)
        {
            Iterator oIterator=oBundle.getEntry().iterator();
            while(oIterator.hasNext())
            {
                Entry oEntry=(Entry)oIterator.next();
                copy.addEntry(oEntry);
            }
        }
        return copy;
    }
    public List<Practitioner> extractPractitionerFromBundleString(String bundleJsonString) throws Exception
    {
        List<Practitioner> extractedPractitioner=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedPractitioner;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedPractitioner=fhirProcessor.getListOfPractitioner();
            }

        }


        return extractedPractitioner;
    }
    public List<Patient> extractPatientFromBundleString(String bundleJsonString) throws Exception
    {
        List<Patient> extractedPatient=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedPatient;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedPatient=fhirProcessor.getListOfPatient();
            }

        }


        return extractedPatient;
    }
    public List<Organization> extractOrganizationFromBundleString(String bundleJsonString) throws Exception
    {
        List<Organization> extractedOrganization=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedOrganization;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedOrganization=fhirProcessor.getListOfOrganization();
            }

        }


        return extractedOrganization;
    }
    public List<Specimen> extractSpecimenFromBundleString(String bundleJsonString) throws Exception
    {
        List<Specimen> extractedSpecimen=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedSpecimen;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedSpecimen=fhirProcessor.getListOfSpecimen();
            }

        }


        return extractedSpecimen;
    }
    public List<DiagnosticOrder> extractDiagnosticOrderFromBundleString(String bundleJsonString) throws Exception
    {
        List<DiagnosticOrder> extractedDiagnosticOrder=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedDiagnosticOrder;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedDiagnosticOrder=fhirProcessor.getListOfDiagnosticOrder();
            }

        }


        return extractedDiagnosticOrder;
    }
    public List<Observation> extractObservationFromBundleString(String bundleJsonString) throws Exception
    {
        List<Observation> extractedObservation=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedObservation;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedObservation=fhirProcessor.getListOfObservation();
            }

        }


        return extractedObservation;
    }
    public List<DiagnosticReport> extractDiagnosticReportFromBundleString(String bundleJsonString) throws Exception
    {
        List<DiagnosticReport> extractedDiagnosticReport=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedDiagnosticReport;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedDiagnosticReport=fhirProcessor.getListOfDiagnosticReport();
            }

        }


        return extractedDiagnosticReport;
    }

    public boolean bundleStringContainsPractitionerEntry(String bundleJsonString) throws Exception
    {
        boolean containsEntry=false;
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return false;
            }
            else if (oBundle.getEntry().size()>0)
            {
                containsEntry=true;
            }

        }

        return containsEntry;
    }

}
class FhirResourceProcessor
{

    List<IResource> _listOfResources;
    Bundle _oBundle;
    public List<IResource> getListOfResources() {
        return _listOfResources;
    }
    public List<Practitioner> getListOfPractitioner() {
        return _listOfPractitioner;
    }

    public void setListOfPractitioner(List<Practitioner> _listOfPractitioner) {
        this._listOfPractitioner = _listOfPractitioner;
    }

    List<Practitioner> _listOfPractitioner;

    public List<Patient> getListOfPatient() {
        return _listOfPatient;
    }

    List<Patient> _listOfPatient;

    public List<Organization> getListOfOrganization() {
        return _listOfOrganization;
    }

    List<Organization> _listOfOrganization;

    public List<Specimen> getListOfSpecimen() {
        return _listOfSpecimen;
    }

    List<Specimen> _listOfSpecimen;

    public List<Observation> getListOfObservation() {
        return _listOfObservation;
    }

    List<Observation> _listOfObservation;

    public List<DiagnosticOrder> getListOfDiagnosticOrder() {
        return _listOfDiagnosticOrder;
    }

    List<DiagnosticOrder> _listOfDiagnosticOrder;

    public List<DiagnosticReport> getListOfDiagnosticReport() {
        return _listOfDiagnosticReport;
    }

    List<DiagnosticReport> _listOfDiagnosticReport;

    public Bundle get_oBundle() {
        return _oBundle;
    }

    public void set_oBundle(Bundle _oBundle) {
        this._oBundle = _oBundle;
    }


    public FhirResourceProcessor(Bundle oBundle)
    {
        this._oBundle=oBundle;
        this._listOfResources=new ArrayList<IResource>();
        //Extract all the resources in the Bundle
        for(Bundle.Entry entry: this._oBundle.getEntry())
        {
            this._listOfResources.add(entry.getResource());

        }
    }
    //Operation Type 1: insert,2:update
    public static String extractResponseStaticsFromBundleTransactionRespons(Bundle bundleResponse, int operationType) throws Exception
    {
        int succes=0;
        int failed=0;
        String res="";

        int total=bundleResponse.getEntry().size();
        for(Entry entry: bundleResponse.getEntry())
        {
            if(operationType==1)
            {
                if(entry.getResponse().getStatus().trim().toUpperCase().contains("201 CREATED"))
                {
                    succes++;
                }
                else
                {
                    failed++;
                }
            }
            else
            {
                if(entry.getResponse().getStatus().trim().contains("200 OK"))
                {
                    succes++;
                }
                else
                {
                    failed++;
                }
            }

        }
        if(operationType==1)
        {
            res ="{id:insert operation,";
        }
        else if (operationType==2)
        {
            res ="{id:update operation,";
        }
        res+="total:"+total+"," +
                "succes:"+succes+"," +
                "failed:"+failed+"}";
        return res;

    }
    public static String extractResponseStaticsFromBundleTransactionRespons(MethodOutcome bundleResponse, int operationType) throws Exception
    {
        int succes=0;
        int failed=0;
        String res="";

        int total=1;
        if(operationType==1)
        {
            if(bundleResponse.getCreated())
            {
                succes++;
            }
            else
            {
                failed++;
            }
        }
        else
        {
            if(bundleResponse.getCreated())
            {
                succes++;
            }
            else
            {
                failed++;
            }
        }
        if(operationType==1)
        {
            res ="{id:insert operation,";
        }
        else if (operationType==2)
        {
            res ="{id:update operation,";
        }
        res+="total:"+total+"," +
                "succes:"+succes+"," +
                "failed:"+failed+"}";
        return res;

    }

    public void processResourcesBundle()
    {
        //Extract All the Practitioner Resources
        this._listOfPractitioner=new ArrayList<>();
        this._listOfPatient=new ArrayList<>();
        this._listOfOrganization=new ArrayList<>();
        this._listOfSpecimen=new ArrayList<>();
        this._listOfDiagnosticOrder=new ArrayList<>();
        this._listOfDiagnosticReport=new ArrayList<>();
        this._listOfObservation=new ArrayList<>();

            for (IResource oResource:this._listOfResources){
                //oResource.getRes
                String resourceType=oResource.getResourceName();
                switch (resourceType)
                {

                    case "Practitioner":
                        this._listOfPractitioner.add((Practitioner)oResource);
                        break;
                    case "Patient":
                        this._listOfPatient.add((Patient) oResource);
                        break;
                    case "Organization":
                        this._listOfOrganization.add((Organization) oResource);
                        break;
                    case "Specimen":
                        this._listOfSpecimen.add((Specimen) oResource);
                        break;
                    case "DiagnosticOrder":
                        this._listOfDiagnosticOrder.add((DiagnosticOrder) oResource);
                        break;
                    case "DiagnosticReport":
                        this._listOfDiagnosticReport.add((DiagnosticReport) oResource);
                        break;
                    case "Observation":
                        this._listOfObservation.add((Observation) oResource);
                        break;
                }
        }

    }
    public static String createPractitionerInTransaction(FhirContext oContext,List<Practitioner> listOfPractitioners,String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;
            for(Practitioner oPractitioner: listOfPractitioners)
            {
                String searchPattern="Practitioner?";
                searchPattern+="_id="+oPractitioner.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oPractitioner);
                oBundleEntry.setElementSpecificId(oPractitioner.getId().getValueAsString());
                oBundleEntry.setFullUrl(oPractitioner.getId());
                oBundleEntry.getRequest().setUrl(searchPattern).setMethod(HTTPVerbEnum.PUT);
                resourceBundle.addEntry(oBundleEntry);
                compter++;
                //if(compter==5) break;
            }
            //String filePath1="/home/server-hit/Desktop/"+"Bundle.json";
            //ManageJsonFile.saveResourceInJSONFile(resourceBundle,oContext,filePath1);
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            String resJSONFormat=oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resourceBundle);
            respOutcome=client.transaction().withBundle(resourceBundle).execute();
            //System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(respOutcome));
            if(respOutcome!=null)
            {
                stringTransactionResult=FhirResourceProcessor.extractResponseStaticsFromBundleTransactionRespons(respOutcome,1);
            }

        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return  stringTransactionResult;
    }
    public static String createPractitioner(FhirContext oContext,List<Practitioner> listOfPractitioners,String serverUrl) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfPractitioners.size();
            for(Practitioner oPractitioner: listOfPractitioners)
            {
                //oPractitioner.getPractitionerRole().get(0).getManagingOrganization().se
                respOutcome=client.update()
                        .resource(oPractitioner)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            int succes=nbreResourceCreated;
            int failed=total-succes;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+succes+"," +
                    "failed:"+failed+"}";

        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return  stringTransactionResult;
    }
    public static String createPatient(FhirContext oContext,List<Patient> listOfPatient,String serverUrl) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfPatient.size();
            for(Patient oPatient: listOfPatient)
            {
                respOutcome=client.update()
                        .resource(oPatient)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";

        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return  stringTransactionResult;
    }
    public static String createOrganization(FhirContext oContext,List<Organization> listOfOrganization,String serverUrl) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfOrganization.size();
            for(Organization oOrganization: listOfOrganization)
            {
                respOutcome=client.update()
                        .resource(oOrganization)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";

        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return  stringTransactionResult;
    }
    public static String createSpecimen(FhirContext oContext,List<Specimen> listOfSpecimen,String serverUrl) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfSpecimen.size();
            for(Specimen oSpecimen: listOfSpecimen)
            {
                respOutcome=client.update()
                        .resource(oSpecimen)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";

        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return  stringTransactionResult;
    }
    public static String createDiagnosticOrder(FhirContext oContext,List<DiagnosticOrder> listOfDiagnosticOrder,String serverUrl) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfDiagnosticOrder.size();
            for(DiagnosticOrder oDiagnosticOrder: listOfDiagnosticOrder)
            {
                respOutcome=client.update()
                        .resource(oDiagnosticOrder)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";

        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return  stringTransactionResult;
    }
    public static String createObservation(FhirContext oContext,List<Observation> listOfObservation,String serverUrl) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfObservation.size();
            for(Observation oObservation: listOfObservation)
            {
                respOutcome=client.update()
                        .resource(oObservation)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";

        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return  stringTransactionResult;
    }
    public static String createDiagnosticReport(FhirContext oContext,List<DiagnosticReport> listOfDiagnosticReport,String serverUrl) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfDiagnosticReport.size();
            for(DiagnosticReport oDiagnosticReport: listOfDiagnosticReport)
            {
                respOutcome=client.update()
                        .resource(oDiagnosticReport)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";

        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return  stringTransactionResult;
    }

    public static String updatePractitionerInTransaction(FhirContext oContext,List<Practitioner> listOfPractitioner, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (Practitioner oPractitioner:listOfPractitioner)
            {
                String searchPattern="Practitioner?";
                String key=null;
                //getKey
                searchPattern+="_id="+oPractitioner.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oPractitioner);
                oBundleEntry.setFullUrl(oPractitioner.getId());
                oBundleEntry.getRequest().setUrl(searchPattern).setMethod(HTTPVerbEnum.PUT);
                resourceBundle.addEntry(oBundleEntry);
            }
            //ManageJsonFile.saveResourceInJSONFile(resourceBundle,oContext,filePath1);
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            //System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resourceBundle));
            respOutcome=client.transaction().withBundle(resourceBundle).execute();
            System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(respOutcome));
            if(respOutcome!=null)
            {
                stringTransactionResult=FhirResourceProcessor.extractResponseStaticsFromBundleTransactionRespons(respOutcome,2);
            }

            //respOutcome.gere


        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return stringTransactionResult;
    }
    public static String updatePatientInTransaction(FhirContext oContext,List<Patient> listOfPatient, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (Patient oPatient:listOfPatient)
            {
                String searchPattern="Patient?";
                String key=null;
                //getKey
                searchPattern+="_id="+oPatient.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oPatient);
                oBundleEntry.setFullUrl(oPatient.getId());
                oBundleEntry.getRequest().setUrl(searchPattern).setMethod(HTTPVerbEnum.PUT);
                resourceBundle.addEntry(oBundleEntry);
            }
            //ManageJsonFile.saveResourceInJSONFile(resourceBundle,oContext,filePath1);
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            //System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resourceBundle));
            respOutcome=client.transaction().withBundle(resourceBundle).execute();
            System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(respOutcome));
            if(respOutcome!=null)
            {
                stringTransactionResult=FhirResourceProcessor.extractResponseStaticsFromBundleTransactionRespons(respOutcome,2);
            }

            //respOutcome.gere


        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return stringTransactionResult;
    }
    public static String updateOrganizationInTransaction(FhirContext oContext,List<Organization> listOfOrganization, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (Organization oOrganization:listOfOrganization)
            {
                String searchPattern="Organization?";
                String key=null;
                //getKey
                searchPattern+="_id="+oOrganization.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oOrganization);
                oBundleEntry.setFullUrl(oOrganization.getId());
                oBundleEntry.getRequest().setUrl(searchPattern).setMethod(HTTPVerbEnum.PUT);
                resourceBundle.addEntry(oBundleEntry);
            }
            //ManageJsonFile.saveResourceInJSONFile(resourceBundle,oContext,filePath1);
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            //System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resourceBundle));
            respOutcome=client.transaction().withBundle(resourceBundle).execute();
            System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(respOutcome));
            if(respOutcome!=null)
            {
                stringTransactionResult=FhirResourceProcessor.extractResponseStaticsFromBundleTransactionRespons(respOutcome,2);
            }

            //respOutcome.gere


        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return stringTransactionResult;
    }
    public static String updateSpecimenInTransaction(FhirContext oContext,List<Specimen> listOfSpecimen, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (Specimen oSpecimen:listOfSpecimen)
            {
                String searchPattern="Specimen?";
                String key=null;
                //getKey
                searchPattern+="_id="+oSpecimen.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oSpecimen);
                oBundleEntry.setFullUrl(oSpecimen.getId());
                oBundleEntry.getRequest().setUrl(searchPattern).setMethod(HTTPVerbEnum.PUT);
                resourceBundle.addEntry(oBundleEntry);
            }
            //ManageJsonFile.saveResourceInJSONFile(resourceBundle,oContext,filePath1);
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            //System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resourceBundle));
            respOutcome=client.transaction().withBundle(resourceBundle).execute();
            System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(respOutcome));
            if(respOutcome!=null)
            {
                stringTransactionResult=FhirResourceProcessor.extractResponseStaticsFromBundleTransactionRespons(respOutcome,2);
            }

            //respOutcome.gere


        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return stringTransactionResult;
    }
    public static String updateDiagnosticOrderInTransaction(FhirContext oContext,List<DiagnosticOrder> listOfDiagnosticOrder, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (DiagnosticOrder oDiagnosticOrder:listOfDiagnosticOrder)
            {
                String searchPattern="DiagnosticOrder?";
                String key=null;
                //getKey
                searchPattern+="_id="+oDiagnosticOrder.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oDiagnosticOrder);
                oBundleEntry.setFullUrl(oDiagnosticOrder.getId());
                oBundleEntry.getRequest().setUrl(searchPattern).setMethod(HTTPVerbEnum.PUT);
                resourceBundle.addEntry(oBundleEntry);
            }
            //ManageJsonFile.saveResourceInJSONFile(resourceBundle,oContext,filePath1);
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            //System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resourceBundle));
            respOutcome=client.transaction().withBundle(resourceBundle).execute();
            System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(respOutcome));
            if(respOutcome!=null)
            {
                stringTransactionResult=FhirResourceProcessor.extractResponseStaticsFromBundleTransactionRespons(respOutcome,2);
            }

            //respOutcome.gere


        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return stringTransactionResult;
    }
    public static String updateObservationInTransaction(FhirContext oContext,List<Observation> listOfObservation, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (Observation oObservation:listOfObservation)
            {
                String searchPattern="Observation?";
                String key=null;
                //getKey
                searchPattern+="_id="+oObservation.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oObservation);
                oBundleEntry.setFullUrl(oObservation.getId());
                oBundleEntry.getRequest().setUrl(searchPattern).setMethod(HTTPVerbEnum.PUT);
                resourceBundle.addEntry(oBundleEntry);
            }
            //ManageJsonFile.saveResourceInJSONFile(resourceBundle,oContext,filePath1);
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            //System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resourceBundle));
            respOutcome=client.transaction().withBundle(resourceBundle).execute();
            System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(respOutcome));
            if(respOutcome!=null)
            {
                stringTransactionResult=FhirResourceProcessor.extractResponseStaticsFromBundleTransactionRespons(respOutcome,2);
            }

            //respOutcome.gere


        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return stringTransactionResult;
    }
    public static String updateDiagnosticReportInTransaction(FhirContext oContext,List<DiagnosticReport> listOfDiagnosticReport, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (DiagnosticReport oDiagnosticReport:listOfDiagnosticReport)
            {
                String searchPattern="DiagnosticReport?";
                String key=null;
                //getKey
                searchPattern+="_id="+oDiagnosticReport.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oDiagnosticReport);
                oBundleEntry.setFullUrl(oDiagnosticReport.getId());
                oBundleEntry.getRequest().setUrl(searchPattern).setMethod(HTTPVerbEnum.PUT);
                resourceBundle.addEntry(oBundleEntry);
            }
            //ManageJsonFile.saveResourceInJSONFile(resourceBundle,oContext,filePath1);
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            //System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(resourceBundle));
            respOutcome=client.transaction().withBundle(resourceBundle).execute();
            System.out.print(oContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(respOutcome));
            if(respOutcome!=null)
            {
                stringTransactionResult=FhirResourceProcessor.extractResponseStaticsFromBundleTransactionRespons(respOutcome,2);
            }

            //respOutcome.gere


        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
        return stringTransactionResult;
    }


}
class ResourcesValidation
{
    FhirValidator _validator;

    public void setListOfPractitionersToValidate(List<Practitioner> _listOfPractitionersToValidate) {
        this._listOfPractitionersToValidate = _listOfPractitionersToValidate;
    }

    List<Practitioner> _listOfPractitionersToValidate;

    public void setListOfPatientToValidate(List<Patient> _listOfPatientToValidate) {
        this._listOfPatientToValidate = _listOfPatientToValidate;
    }

    List<Patient> _listOfPatientToValidate;

    public void setListOfOrganizationToValidate(List<Organization> _listOfOrganizationToValidate) {
        this._listOfOrganizationToValidate = _listOfOrganizationToValidate;
    }

    List<Organization> _listOfOrganizationToValidate;

    public void setListOfSpecimenToValidate(List<Specimen> _listOfSpecimenToValidate) {
        this._listOfSpecimenToValidate = _listOfSpecimenToValidate;
    }

    List<Specimen> _listOfSpecimenToValidate;

    public void setListOfDiagnosticOrderToValidate(List<DiagnosticOrder> _listOfDiagnosticOrderToValidate) {
        this._listOfDiagnosticOrderToValidate = _listOfDiagnosticOrderToValidate;
    }

    List<DiagnosticOrder> _listOfDiagnosticOrderToValidate;

    public void setListOfDiagnosticReportToValidate(List<DiagnosticReport> _listOfDiagnosticReportToValidate) {
        this._listOfDiagnosticReportToValidate = _listOfDiagnosticReportToValidate;
    }

    List<DiagnosticReport> _listOfDiagnosticReportToValidate;

    public void setListOfObservationToValidate(List<Observation> _listOfObservationToValidate) {
        this._listOfObservationToValidate = _listOfObservationToValidate;
    }

    List<Observation> _listOfObservationToValidate;

    public List<Practitioner> getlistOfValidePractitioner() {
        return _listOfValidPractitioner;
    }

    List<Practitioner> _listOfValidPractitioner;

    public List<Patient> getListOfValidPatient() {
        return _listOfValidPatient;
    }

    List<Patient> _listOfValidPatient;

    public List<Organization> getListOfValidOrganization() {
        return _listOfValidOrganization;
    }

    List<Organization> _listOfValidOrganization;

    public List<Specimen> getListOfValidSpecimen() {
        return _listOfValidSpecimen;
    }

    List<Specimen> _listOfValidSpecimen;

    public List<DiagnosticOrder> getListOfValidDiagnosticOrder() {
        return _listOfValidDiagnosticOrder;
    }

    List<DiagnosticOrder> _listOfValidDiagnosticOrder;

    public List<DiagnosticReport> getListOfValidDiagnosticReport() {
        return _listOfValidDiagnosticReport;
    }

    List<DiagnosticReport> _listOfValidDiagnosticReport;

    public List<Observation> getListOfValidObservation() {
        return _listOfValidObservation;
    }

    List<Observation> _listOfValidObservation;

    public List<Practitioner> getlistOfInvalidePractitioner() {
        return _listOfInvalidPractitioner;
    }

    List<Practitioner> _listOfInvalidPractitioner;

    public List<Patient> getListOfInvalidPatient() {
        return _listOfInvalidPatient;
    }

    List<Patient> _listOfInvalidPatient;

    public List<Organization> getListOfInvalidOrganization() {
        return _listOfInvalidOrganization;
    }

    List<Organization> _listOfInvalidOrganization;

    public List<Specimen> getListOfInvalidSpecimen() {
        return _listOfInvalidSpecimen;
    }

    List<Specimen> _listOfInvalidSpecimen;

    public List<DiagnosticOrder> getListOfInvalidDiagnosticOrder() {
        return _listOfInvalidDiagnosticOrder;
    }

    List<DiagnosticOrder> _listOfInvalidDiagnosticOrder;

    public List<DiagnosticReport> getListOfInvalidDiagnosticReport() {
        return _listOfInvalidDiagnosticReport;
    }

    List<DiagnosticReport> _listOfInvalidDiagnosticReport;

    public List<Observation> getListOfInvalidObservation() {
        return _listOfInvalidObservation;
    }

    List<Observation> _listOfInvalidObservation;

    public ResourcesValidation(FhirValidator validator)
    {
        this._validator=validator;
        this._listOfPractitionersToValidate=new ArrayList<>();
        this._listOfPatientToValidate=new ArrayList<>();
        this._listOfOrganizationToValidate=new ArrayList<>();
        this._listOfSpecimenToValidate=new ArrayList<>();
        this._listOfDiagnosticOrderToValidate=new ArrayList<>();
        this._listOfDiagnosticReportToValidate=new ArrayList<>();
        this._listOfObservationToValidate=new ArrayList<>();
        //
        this._listOfValidPractitioner=new ArrayList<>();
        this._listOfValidPatient=new ArrayList<>();
        this._listOfValidOrganization=new ArrayList<>();
        this._listOfValidSpecimen=new ArrayList<>();
        this._listOfValidDiagnosticOrder=new ArrayList<>();
        this._listOfValidDiagnosticReport=new ArrayList<>();
        this._listOfValidObservation=new ArrayList<>();
        //
        this._listOfInvalidPractitioner=new ArrayList<>();
        this._listOfInvalidPatient=new ArrayList<>();
        this._listOfInvalidOrganization=new ArrayList<>();
        this._listOfInvalidSpecimen=new ArrayList<>();
        this._listOfInvalidDiagnosticOrder=new ArrayList<>();
        this._listOfInvalidDiagnosticReport=new ArrayList<>();
        this._listOfInvalidObservation=new ArrayList<>();

    }

    public ResourcesValidation(FhirValidator validator,List<Practitioner> listOfPractitionersToValidate)
    {
        this._validator=validator;
        this._listOfPractitionersToValidate=listOfPractitionersToValidate;
        this._listOfValidPractitioner=new ArrayList<Practitioner>();
        this._listOfInvalidPractitioner=new ArrayList<Practitioner>();
    }
    public void startValidation()
    {
        for(Practitioner oPractionerToValidate: this._listOfPractitionersToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oPractionerToValidate);
            if(res.isSuccessful())
            {
                this._listOfValidPractitioner.add(oPractionerToValidate);
            }
            else
            {
                this._listOfInvalidPractitioner.add(oPractionerToValidate);
            }

        }
        for(Patient oPatientToValidate: this._listOfPatientToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oPatientToValidate);
            if(res.isSuccessful())
            {
                this._listOfValidPatient.add(oPatientToValidate);
            }
            else
            {
                this._listOfInvalidPatient.add(oPatientToValidate);
            }
        }
        for(Organization oOrganizationToValidate: this._listOfOrganizationToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oOrganizationToValidate);
            if(res.isSuccessful())
            {
                this._listOfValidOrganization.add(oOrganizationToValidate);
            }
            else
            {
                this._listOfInvalidOrganization.add(oOrganizationToValidate);
            }
        }
        for(Specimen oSpecimenToValidate: this._listOfSpecimenToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oSpecimenToValidate);
            if(res.isSuccessful())
            {
                this._listOfValidSpecimen.add(oSpecimenToValidate);
            }
            else
            {
                this._listOfInvalidSpecimen.add(oSpecimenToValidate);
            }
        }
        for(DiagnosticOrder oDiagnosticOrderToValidate: this._listOfDiagnosticOrderToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oDiagnosticOrderToValidate);
            if(res.isSuccessful())
            {
                this._listOfValidDiagnosticOrder.add(oDiagnosticOrderToValidate);
            }
            else
            {
                this._listOfInvalidDiagnosticOrder.add(oDiagnosticOrderToValidate);
            }
        }
        for(DiagnosticReport oDiagnosticReportToValidate: this._listOfDiagnosticReportToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oDiagnosticReportToValidate);
            if(res.isSuccessful())
            {
                this._listOfValidDiagnosticReport.add(oDiagnosticReportToValidate);
            }
            else
            {
                this._listOfInvalidDiagnosticReport.add(oDiagnosticReportToValidate);
            }
        }
        for(Observation oObservationToValidate : this._listOfObservationToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oObservationToValidate);
            if(res.isSuccessful())
            {
                this._listOfValidObservation.add(oObservationToValidate);
            }
            else
            {
                this._listOfInvalidObservation.add(oObservationToValidate);
            }
        }


    }

}
class ManageJsonFile
{
    String _filePath;

    public ManageJsonFile(String filePath)
    {
        this._filePath=filePath;
    }
    public String returnJsonFileContent()
    {
        try
        {
            String absolutePath=this._filePath;
            File fHandler=new File(absolutePath);
            if(fHandler.exists())
            {
                FileInputStream f =new FileInputStream(absolutePath);
                StringBuilder sb = new StringBuilder();
                BufferedReader br=new BufferedReader(new FileReader(absolutePath));
                String line=br.readLine();
                while (line != null) {
                    sb.append(line);
                    line=br.readLine();
                }
                return sb.toString();
            }
            else
            {
                return null;
            }
        }
        catch (IOException exc)
        {
            System.out.print(exc.getStackTrace());
        }
        return  null;
    }
    public static void saveResourceInJSONFile(StringBuilder strBuilder,String filePath)
    {
        try
        {
            FileWriter file=new FileWriter(filePath);
            System.out.print(strBuilder.toString());
            file.write(strBuilder.toString().toCharArray());
        }
        catch (IOException exc){
            System.out.print(exc.getStackTrace());

        }
        catch (Exception exc)
        {
            System.out.print(exc.getStackTrace());
        }
    }
    public static void saveResourceInJSONFile(StringBuilder strBuilder,int strBuiderSize,String filePath)
    {
        try
        {
            BufferedWriter writer=new BufferedWriter(new FileWriter(filePath));
            //System.out.print(strBuilder.toString());
            char[] charTab=new char[strBuiderSize];
            for(int i=0;i<strBuiderSize;i++)
            {
                charTab[i]=strBuilder.charAt(i);
                //System.out.print(strBuilder.charAt(i));
                writer.write(""+strBuilder.charAt(i));
            }
            writer.close();
        }
        catch (IOException exc){
            System.out.print(exc.getStackTrace());

        }
        catch (Exception exc)
        {
            System.out.print(exc.getStackTrace());
        }
    }
    public static void saveResourceInJSONFile(Bundle oBuncle,FhirContext oContext,String filePath)
    {
        try
        {
            IParser oParser = oContext.newJsonParser();
            char[] stringArray= oParser.encodeResourceToString(oBuncle).toCharArray();
            int strBuiderSize=stringArray.length;
            StringBuilder strBuilder=new StringBuilder();
            for(char oCharacter:stringArray)
            {
                strBuilder.append(oCharacter);
            }
            BufferedWriter writer=new BufferedWriter(new FileWriter(filePath));
            //System.out.print(strBuilder.toString());
            char[] charTab=new char[strBuiderSize];
            for(int i=0;i<strBuiderSize;i++)
            {
                charTab[i]=strBuilder.charAt(i);
                //System.out.print(strBuilder.charAt(i));
                writer.write(""+strBuilder.charAt(i));
            }
            writer.close();
        }
        catch (IOException exc){
            System.out.print(exc.getStackTrace());

        }
        catch (Exception exc)
        {
            System.out.print(exc.getStackTrace());
        }
    }
}
