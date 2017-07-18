package org.mediator.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.dstu2.resource.Basic;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import ca.uhn.fhir.model.dstu2.resource.ListResource;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.OperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Organization;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Specimen;
import ca.uhn.fhir.model.dstu2.valueset.BundleTypeEnum;
import ca.uhn.fhir.model.dstu2.valueset.HTTPVerbEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
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

    public List<Practitioner> getListOfInvalidPractitioners() {
        return listOfInvalidPractitioners;
    }

    public List<Practitioner> getListOfValidPractitioners() {
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

    public List<ListResource> getListOfValidListResource() {
        return listOfValidListResource;
    }

    List<ListResource> listOfValidListResource=new ArrayList<>();

    public List<Basic> getListOfValidBasic() {
        return listOfValidBasic;
    }

    List<Basic> listOfValidBasic=new ArrayList<>();

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

    public List<ListResource> getListOfInvalidListResource() {
        return listOfInvalidListResource;
    }

    List<ListResource> listOfInvalidListResource=new ArrayList<>();

    public List<Basic> getListOfInvalidBasic() {
        return listOfInvalidBasic;
    }

    List<Basic> listOfInvalidBasic=new ArrayList<>();

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

    public List<Condition> getListOfCondition() {
        return listOfCondition;
    }

    public void setListOfCondition(List<Condition> listOfCondition) {
        this.listOfCondition = listOfCondition;
    }

    List<Condition> listOfCondition=new ArrayList<>();

    public List<Condition> getListOfValidCondition() {
        return listOfValidCondition;
    }

    public void setListOfValidCondition(List<Condition> listOfValidCondition) {
        this.listOfValidCondition = listOfValidCondition;
    }

    List<Condition> listOfValidCondition=new ArrayList<>();

    public List<Condition> getListOfInvalidCondition() {
        return listOfInvalidCondition;
    }

    public void setListOfInvalidCondition(List<Condition> listOfInvalidCondition) {
        this.listOfInvalidCondition = listOfInvalidCondition;
    }

    List<Condition> listOfInvalidCondition=new ArrayList<>();

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

    public List<ListResource> getListOfListResource() {
        return listOfListResource;
    }

    List<ListResource> listOfListResource=new ArrayList<>();

    public List<Basic> getListOfBasic() {
        return listOfBasic;
    }

    List<Basic> listOfBasic=new ArrayList<>();

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
                listOfCondition=fhirProcessor.getListOfCondition();
                listOfDiagnosticOrder=fhirProcessor.getListOfDiagnosticOrder();
                listOfObservation=fhirProcessor.getListOfObservation();
                listOfDiagnosticReport=fhirProcessor.getListOfDiagnosticReport();
                listOfListResource=fhirProcessor.getListOfListResource();
                listOfBasic=fhirProcessor.getListOfBasic();

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
                    listOfCondition.addAll(fhirProcessor.getListOfCondition());
                    listOfDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                    listOfObservation.addAll(fhirProcessor.getListOfObservation());
                    listOfDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                    listOfListResource.addAll(fhirProcessor.getListOfListResource());
                    listOfBasic.addAll(fhirProcessor.getListOfBasic());
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
                        listOfCondition.addAll(fhirProcessor.getListOfCondition());
                        listOfDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                        listOfObservation.addAll(fhirProcessor.getListOfObservation());
                        listOfDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                        listOfListResource.addAll(fhirProcessor.getListOfListResource());
                        listOfBasic.addAll(fhirProcessor.getListOfBasic());
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
                resValidation.setListOfConditionToValidate(listOfCondition);
                resValidation.setListOfDiagnosticOrderToValidate(listOfDiagnosticOrder);
                resValidation.setListOfDiagnosticReportToValidate(listOfDiagnosticReport);
                resValidation.setListOfObservationToValidate(listOfObservation);
                resValidation.setListOfListResourceToValidate(listOfListResource);
                resValidation.setListOfBasicToValidate(listOfBasic);

                resValidation.startValidation();
                //Validation results
                listOfValidPractitioners=resValidation.getlistOfValidePractitioner();
                listOfValidPatient=resValidation.getListOfValidPatient();
                listOfValidOrganization=resValidation.getListOfValidOrganization();
                listOfValidSpecimen=resValidation.getListOfValidSpecimen();
                listOfValidCondition=resValidation.getListOfValidCondition();
                listOfValidDiagnosticOrder=resValidation.getListOfValidDiagnosticOrder();
                listOfValidDiagnosticReport=resValidation.getListOfValidDiagnosticReport();
                listOfValidObservation=resValidation.getListOfValidObservation();
                listOfValidListResource=resValidation.getListOfValidListResource();
                listOfValidBasic=resValidation.getListOfValidBasic();

                //
                listOfInvalidPractitioners=resValidation.getlistOfInvalidePractitioner();
                listOfInvalidPatient=resValidation.getListOfInvalidPatient();
                listOfInvalidOrganization=resValidation.getListOfInvalidOrganization();
                listOfInvalidSpecimen=resValidation.getListOfInvalidSpecimen();
                listOfInvalidCondition=resValidation.getListOfInvalidCondition();
                listOfInvalidDiagnosticOrder=resValidation.getListOfInvalidDiagnosticOrder();
                listOfInvalidDiagnosticReport=resValidation.getListOfInvalidDiagnosticReport();
                listOfInvalidObservation=resValidation.getListOfInvalidObservation();
                listOfInvalidListResource=resValidation.getListOfInvalidListResource();
                listOfInvalidBasic=resValidation.getListOfInvalidBasic();

                if(listOfInvalidPractitioners.size()>0 || listOfInvalidPatient.size()>0 || listOfInvalidOrganization.size()>0
                        || listOfInvalidSpecimen.size()>0 || listOfInvalidDiagnosticOrder.size()>0 || listOfInvalidDiagnosticReport.size()>0
                        || listOfInvalidObservation.size()>0 || listOfInvalidListResource.size()>0 || listOfInvalidBasic.size()>0)
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
    public boolean processBundleJsonResourceWithoutValidation(String filePathError,String sourceServerURI) throws Exception
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
                listOfCondition=fhirProcessor.getListOfCondition();
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
                    listOfCondition.addAll(fhirProcessor.getListOfCondition());
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
                        listOfCondition.addAll(fhirProcessor.getListOfCondition());
                        listOfDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                        listOfObservation.addAll(fhirProcessor.getListOfObservation());
                        listOfDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                        //nextPageBundle=subNextBundle.copy();
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
                //Get the Valide Resource
                isProcessed=true;
            }
        }
        else
        {
            throw new Exception("Failed to parse the json string to Bundle");
        }
        return isProcessed;
    }
    public boolean processBundleEntryResource(Bundle bundleToProcess,String filePathError,String sourceServerURI) throws Exception
    {
        boolean isProcessed=false;
        Bundle oBundle=bundleToProcess;
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
                listOfCondition=fhirProcessor.getListOfCondition();
                listOfDiagnosticOrder=fhirProcessor.getListOfDiagnosticOrder();
                listOfObservation=fhirProcessor.getListOfObservation();
                listOfDiagnosticReport=fhirProcessor.getListOfDiagnosticReport();
                listOfListResource=fhirProcessor.getListOfListResource();

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
                    listOfCondition.addAll(fhirProcessor.getListOfCondition());
                    listOfDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                    listOfObservation.addAll(fhirProcessor.getListOfObservation());
                    listOfDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                    listOfListResource.addAll(fhirProcessor.getListOfListResource());
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
                        listOfCondition.addAll(fhirProcessor.getListOfCondition());
                        listOfDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                        listOfObservation.addAll(fhirProcessor.getListOfObservation());
                        listOfDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                        listOfListResource.addAll(fhirProcessor.getListOfListResource());
                        //nextPageBundle=subNextBundle.copy();
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
                //Get the Valide Resource
                //Skip the resource validation
                listOfValidPractitioners.addAll(listOfPractitioners);
                listOfValidPatient.addAll(listOfPatient);
                listOfValidSpecimen.addAll(listOfSpecimen);
                listOfValidCondition.addAll(listOfCondition);
                listOfValidDiagnosticOrder.addAll(listOfDiagnosticOrder);
                listOfValidObservation.addAll(listOfObservation);
                listOfValidDiagnosticReport.addAll(listOfDiagnosticReport);
                listOfValidListResource.addAll(listOfListResource);
                isProcessed=true;
            }
        }
        else
        {
            throw new Exception("Failed to parse the json string to Bundle");
        }
        return isProcessed;
    }
    public List<Bundle>  processBundleResource(String filePathError,String sourceServerURI) throws Exception
    {
        boolean isProcessed=false;
        List<Bundle> listOfProcessedBundle=new ArrayList<>();

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
                listOfProcessedBundle.add(oBundle);

                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);

                    //There is additional ressource to Extract
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    listOfProcessedBundle.add(nextPageBundle);
                   while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        listOfProcessedBundle.add(subNextBundle);
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }


                isProcessed=true;
            }
        }
        else
        {
            throw new Exception("Failed to parse the json string to Bundle");
        }
        return listOfProcessedBundle;
    }
    public List<Bundle>  processBundleResourceContent(String filePathError,String sourceServerURI) throws Exception
    {
        boolean isProcessed=false;
        List<Bundle> listOfProcessedBundle=new ArrayList<>();

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
                for(Bundle resBundle :fhirProcessor.getListOfBundle())
                {
                    if(resBundle.getType().equals(BundleTypeEnum.COLLECTION.toString().toLowerCase()))
                    {
                        listOfProcessedBundle.add(resBundle);
                        if(resBundle.getLink(Bundle.LINK_NEXT)!=null)
                        {
                            IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);

                            //There is additional ressource to Extract
                            Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                            listOfProcessedBundle.add(nextPageBundle);
                            while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                            {
                                Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                                listOfProcessedBundle.add(subNextBundle);
                                nextPageBundle=CreateBundleCopy(subNextBundle);
                            }
                        }
                    }
                }
                //listOfProcessedBundle.add(oBundle);

                isProcessed=true;
            }
        }
        else
        {
            throw new Exception("Failed to parse the json string to Bundle");
        }
        return listOfProcessedBundle;
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
    private static ca.uhn.fhir.model.api.Bundle CreateBundleCopy(ca.uhn.fhir.model.api.Bundle oBundle)
    {
        ca.uhn.fhir.model.api.Bundle copy=new ca.uhn.fhir.model.api.Bundle();
        copy=oBundle;
        return  copy;
    }
    public List<Practitioner> extractPractitionerFromBundleString(String bundleJsonString,String sourceServerURI) throws Exception
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
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedPractitioner.addAll(fhirProcessor.getListOfPractitioner());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedPractitioner.addAll(fhirProcessor.getListOfPractitioner());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedPractitioner;
    }
    public List<Patient> extractPatientFromBundleString(String bundleJsonString,String sourceServerURI) throws Exception
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
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedPatient.addAll(fhirProcessor.getListOfPatient());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedPatient.addAll(fhirProcessor.getListOfPatient());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedPatient;
    }
    public List<ListResource> extractListResourceFromBundleString(String bundleJsonString,String sourceServerURI) throws Exception
    {
        List<ListResource> extractedListResource=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedListResource;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedListResource=fhirProcessor.getListOfListResource();
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedListResource.addAll(fhirProcessor.getListOfListResource());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedListResource.addAll(fhirProcessor.getListOfListResource());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedListResource;
    }
    public static List<ListResource> extractListResourceFromApiBundleObject(ca.uhn.fhir.model.api.Bundle oBundle, FhirContext oContext,String sourceServerURI) throws Exception
    {
        List<ListResource> extractedListResource=new ArrayList<>();
        if(oBundle!=null ) {
            if (oBundle.getResources(ListResource.class).size() == 0) {
                return extractedListResource;
            }
            else if (oBundle.getResources(ListResource.class).size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedListResource=fhirProcessor.getListOfListResource();
                if(oBundle.getLinkNext().getValue()!=null)
                {
                    IGenericClient oClient=oContext.newRestfulGenericClient(sourceServerURI);
                    ca.uhn.fhir.model.api.Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    //oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedListResource.addAll(fhirProcessor.getListOfListResource());
                    while (nextPageBundle.getLinkNext().getValue()!=null)
                    {
                        ca.uhn.fhir.model.api.Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedListResource.addAll(fhirProcessor.getListOfListResource());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedListResource;
    }
    public List<Basic> extractBasicFromBundleString(String bundleJsonString,String sourceServerURI) throws Exception
    {
        List<Basic> extractedBasic=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedBasic;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedBasic=fhirProcessor.getListOfBasic();
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedBasic.addAll(fhirProcessor.getListOfBasic());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedBasic.addAll(fhirProcessor.getListOfBasic());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedBasic;
    }
    public List<Organization> extractOrganizationFromBundleString(String bundleJsonString,String sourceServerURI ) throws Exception
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
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedOrganization.addAll(fhirProcessor.getListOfOrganization());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedOrganization.addAll(fhirProcessor.getListOfOrganization());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedOrganization;
    }
    public List<Specimen> extractSpecimenFromBundleString(String bundleJsonString,String sourceServerURI) throws Exception
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
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedSpecimen.addAll(fhirProcessor.getListOfSpecimen());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedSpecimen.addAll(fhirProcessor.getListOfSpecimen());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedSpecimen;
    }
    public List<Condition> extractConditionFromBundleString(String bundleJsonString, String sourceServerURI) throws Exception
    {
        List<Condition> extractedCondition=new ArrayList<>();
        if(bundleJsonString.length()<2)
        {
            throw new Exception("Invalide  Json resource");
        }
        Bundle oBundle=_parser.parseResource(Bundle.class,bundleJsonString.toString());
        if(oBundle!=null ) {
            if (oBundle.getEntry().size() == 0) {
                return extractedCondition;
            }
            else if (oBundle.getEntry().size()>0)
            {
                FhirResourceProcessor fhirProcessor=new FhirResourceProcessor(oBundle);
                fhirProcessor.processResourcesBundle();
                extractedCondition=fhirProcessor.getListOfCondition();
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedCondition.addAll(fhirProcessor.getListOfCondition());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedCondition.addAll(fhirProcessor.getListOfCondition());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedCondition;
    }
    public List<DiagnosticOrder> extractDiagnosticOrderFromBundleString(String bundleJsonString,String sourceServerURI) throws Exception
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
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedDiagnosticOrder.addAll(fhirProcessor.getListOfDiagnosticOrder());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedDiagnosticOrder;
    }
    public List<Observation> extractObservationFromBundleString(String bundleJsonString, String sourceServerURI) throws Exception
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
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedObservation.addAll(fhirProcessor.getListOfObservation());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedObservation.addAll(fhirProcessor.getListOfObservation());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
            }

        }


        return extractedObservation;
    }
    public List<DiagnosticReport> extractDiagnosticReportFromBundleString(String bundleJsonString,String sourceServerURI) throws Exception
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
                if(oBundle.getLink(Bundle.LINK_NEXT)!=null)
                {
                    IGenericClient oClient=this._context.newRestfulGenericClient(sourceServerURI);
                    Bundle nextPageBundle=oClient.loadPage().next(oBundle).execute();
                    fhirProcessor=null;
                    fhirProcessor=new FhirResourceProcessor(nextPageBundle);
                    fhirProcessor.processResourcesBundle();
                    extractedDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                    while (nextPageBundle.getLink(Bundle.LINK_NEXT)!=null)
                    {
                        Bundle subNextBundle=oClient.loadPage().next(nextPageBundle).execute();
                        fhirProcessor=null;
                        fhirProcessor=new FhirResourceProcessor(subNextBundle);
                        fhirProcessor.processResourcesBundle();
                        extractedDiagnosticReport.addAll(fhirProcessor.getListOfDiagnosticReport());
                        nextPageBundle=CreateBundleCopy(subNextBundle);
                    }
                }
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

    public List<Condition> getListOfCondition() {
        return _listOfCondition;
    }

    public void setListOfCondition(List<Condition> _listOfCondition) {
        this._listOfCondition = _listOfCondition;
    }

    List<Condition> _listOfCondition;

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

    public List<ListResource> getListOfListResource() {
        return _listOfListResource;
    }

    public List<ListResource> _listOfListResource;

    public List<Basic> getListOfBasic() {
        return _listOfBasic;
    }

    public List<Basic> _listOfBasic;

    public List<Bundle> getListOfBundle() {
        return _listOfBundle;
    }

    List<Bundle> _listOfBundle;

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
    public FhirResourceProcessor(ca.uhn.fhir.model.api.Bundle oBundle)
    {
       // this._oBundle=oBundle;
        this._listOfResources=new ArrayList<IResource>();
        //Extract all the resources in the Bundle
        for(BundleEntry entry: oBundle.getEntries())
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
        this._listOfCondition=new ArrayList<>();
        this._listOfDiagnosticOrder=new ArrayList<>();
        this._listOfDiagnosticReport=new ArrayList<>();
        this._listOfObservation=new ArrayList<>();
        this._listOfListResource=new ArrayList<>();
        this._listOfBasic=new ArrayList<>();
        this._listOfBundle=new ArrayList<>();

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
                    case "Condition":
                        this._listOfCondition.add((Condition) oResource);
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
                    case "List":
                        this._listOfListResource.add((ListResource) oResource);
                        break;
                    case "Basic":
                        this._listOfBasic.add((Basic) oResource);
                        break;
                    case "Bundle":
                        this._listOfBundle.add((Bundle) oResource);
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
    public static String createDiagnosticOrderInTransaction(FhirContext oContext,List<DiagnosticOrder> _listOfDiagnosticOrder,String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;
            for(DiagnosticOrder oDiagnosticOrder: _listOfDiagnosticOrder)
            {
                String searchPattern="DiagnosticOrder?";
                searchPattern+="_id="+oDiagnosticOrder.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oDiagnosticOrder);
                oBundleEntry.setElementSpecificId(oDiagnosticOrder.getId().getValueAsString());
                oBundleEntry.setFullUrl(oDiagnosticOrder.getId());
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
    public static String createPatient(FhirContext oContext,List<Patient> listOfPatient,String serverUrl
    ,String logFileName) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";

            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfPatient.size();
            for(Patient oPatient: listOfPatient)
            {
                try {
                    //specimenToTrace = oSpecimen;
                    respOutcome = client.update()
                            .resource(oPatient)
                            .prettyPrint()
                            .encodedJson()
                            .execute();
                    if(respOutcome.getCreated())
                    {
                        nbreResourceCreated++;
                    }
                }
                catch (Exception exc)
                {
                    FhirMediatorUtilities.writeInLogFile(logFileName,
                            oPatient.toString()+" Creation resource operation failed! "+exc.getMessage(),"Error");
                    continue;

                    //throw new Exception(exc.getMessage());
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";
        return  stringTransactionResult;
    }
    public static String createListResource(FhirContext oContext,List<ListResource> listOfListResource,String serverUrl
            ,String logFileName) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";

        IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

        int compter=0;
        int nbreResourceCreated=0;
        int total=listOfListResource.size();

        for(ListResource oListResource: listOfListResource)
        {
            try {
                //specimenToTrace = oSpecimen;
                respOutcome = client.update()
                        .resource(oListResource)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            catch (Exception exc)
            {
                FhirMediatorUtilities.writeInLogFile(logFileName,
                        oListResource.toString()+" Creation resource operation failed! "+exc.getMessage(),"Error");
                continue;

                //throw new Exception(exc.getMessage());
            }
        }
        int success=nbreResourceCreated;
        int failed=total-success;
        stringTransactionResult="total:"+total+"," +
                "succes:"+success+"," +
                "failed:"+failed+"}";
        return  stringTransactionResult;
    }
    public static String createBasic(FhirContext oContext,List<Basic> listOfBasic,String serverUrl
            ,String logFileName) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";

        IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

        int compter=0;
        int nbreResourceCreated=0;
        int total=listOfBasic.size();

        for(Basic oBasic: listOfBasic)
        {
            try {
                //specimenToTrace = oSpecimen;
                respOutcome = client.update()
                        .resource(oBasic)
                        .prettyPrint()
                        .encodedJson()
                        .execute();
                if(respOutcome.getCreated())
                {
                    nbreResourceCreated++;
                }
            }
            catch (Exception exc)
            {
                FhirMediatorUtilities.writeInLogFile(logFileName,
                        oBasic.toString()+" Creation resource operation failed! "+exc.getMessage(),"Error");
                continue;

                //throw new Exception(exc.getMessage());
            }
        }
        int success=nbreResourceCreated;
        int failed=total-success;
        stringTransactionResult="total:"+total+"," +
                "succes:"+success+"," +
                "failed:"+failed+"}";
        return  stringTransactionResult;
    }
    public static String createListResourceInTransaction(FhirContext oContext,List<ListResource> listOfListResource,String serverUrl
            ,String logFileName) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;
            for(ListResource oListResource: listOfListResource)
            {

                Entry oBundleEntry= new Entry().setResource(oListResource);
                oBundleEntry.getRequest().setMethod(HTTPVerbEnum.PUT);
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
    public static String createBundle(FhirContext oContext,List<Bundle> listOfBundles,String serverUrl) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfBundles.size();
            for(Bundle oBundle: listOfBundles )
            {
                respOutcome=client.update()
                        .resource(oBundle)
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
    public static String createSpecimen(FhirContext oContext,List<Specimen> listOfSpecimen,String serverUrl,
                                        String logFileName)
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        Specimen specimenToTrace=null;

            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfSpecimen.size();
            for(Specimen oSpecimen: listOfSpecimen)
            {
                try {
                    //specimenToTrace = oSpecimen;
                    respOutcome = client.update()
                            .resource(oSpecimen)
                            .prettyPrint()
                            .encodedJson()
                            .execute();
                    if(respOutcome.getCreated())
                    {
                        nbreResourceCreated++;
                    }
                }
                catch (Exception exc)
                {
                    FhirMediatorUtilities.writeInLogFile(logFileName,
                            oSpecimen.toString()+" Creation resource operation failed! "+exc.getMessage(),"Error");
                    continue;

                    //throw new Exception(exc.getMessage());
                }

            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";

        return  stringTransactionResult;
    }
    public static String createCondition(FhirContext oContext,List<Condition> listOfCondition,String serverUrl
    ,String logFileName) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfCondition.size();
            for(Condition oCondition: listOfCondition)
            {
                try {
                    //specimenToTrace = oSpecimen;
                    respOutcome = client.update()
                            .resource(oCondition)
                            .prettyPrint()
                            .encodedJson()
                            .execute();
                    if(respOutcome.getCreated())
                    {
                        nbreResourceCreated++;
                    }
                }
                catch (Exception exc)
                {
                    FhirMediatorUtilities.writeInLogFile(logFileName,
                            oCondition.toString()+" Creation resource operation failed! "+exc.getMessage(),"Error");
                    continue;

                    //throw new Exception(exc.getMessage());
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
    public static String createDiagnosticOrder(FhirContext oContext,List<DiagnosticOrder> listOfDiagnosticOrder,String serverUrl
    , String logFileName) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfDiagnosticOrder.size();
            for(DiagnosticOrder oDiagnosticOrder: listOfDiagnosticOrder)
            {
                try
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
                catch (Exception exc)
                {
                    FhirMediatorUtilities.writeInLogFile(logFileName,
                            oDiagnosticOrder.toString()+" Creation resource operation failed! "+exc.getMessage(),"Error");

                    continue;
                }

            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";
        return  stringTransactionResult;
    }
    public static String createObservation(FhirContext oContext,List<Observation> listOfObservation,String serverUrl
    , String logFileName) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfObservation.size();
            for(Observation oObservation: listOfObservation)
            {
                try
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
                catch (Exception exc)
                {
                    FhirMediatorUtilities.writeInLogFile(logFileName,
                            oObservation.toString()+" Creation resource operation failed! "+exc.getMessage(),"Error");

                    continue;
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";
        return  stringTransactionResult;
    }
    public static String createDiagnosticReport(FhirContext oContext,List<DiagnosticReport> listOfDiagnosticReport,String serverUrl
    ,String logFileName) throws Exception
    {
        MethodOutcome respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);

            int compter=0;
            int nbreResourceCreated=0;
            int total=listOfDiagnosticReport.size();
            for(DiagnosticReport oDiagnosticReport: listOfDiagnosticReport)
            {
                try
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
                catch (Exception exc)
                {
                    FhirMediatorUtilities.writeInLogFile(logFileName,
                            oDiagnosticReport.toString()+" Creation resource operation failed! "+exc.getMessage(),"Error");

                    continue;
                }
            }
            int success=nbreResourceCreated;
            int failed=total-success;
            stringTransactionResult="total:"+total+"," +
                    "succes:"+success+"," +
                    "failed:"+failed+"}";
        return  stringTransactionResult;
    }
    public static void deleteDiagnosticReportBySpecimen(FhirContext oContext,List<String> listIdSpecimenToDelete, String serverUrl, String logFileName)
    {
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            IBaseOperationOutcome resp=client.delete()
                    .resourceConditionalByType("DiagnosticReport")
                    //.where(DiagnosticReport.SPECIMEN.hasId(idSpecimenToDelete))
                    .where(DiagnosticReport.SPECIMEN.hasAnyOfIds(listIdSpecimenToDelete))
                    .execute();
            if(resp!=null)
            {
                OperationOutcome outcome = (OperationOutcome) resp;
                System.out.println(outcome.getIssueFirstRep().getDetailsElement().getValue());
            }

        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(logFileName,
                    exc.getMessage(),"Error");
        }
    }
    public static void deleteObservationBySpecimen(FhirContext oContext,List<String> listIdSpecimenToDelete, String serverUrl, String logFileName)
    {
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            IBaseOperationOutcome resp=client.delete()
                    .resourceConditionalByType("Observation")
                    //.where(DiagnosticReport.SPECIMEN.hasId(idSpecimenToDelete))
                    .where(Observation.SPECIMEN.hasAnyOfIds(listIdSpecimenToDelete))
                    .execute();
            if(resp!=null)
            {
                OperationOutcome outcome = (OperationOutcome) resp;
                System.out.println(outcome.getIssueFirstRep().getDetailsElement().getValue());
            }

        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(logFileName,
                    exc.getMessage(),"Error");
        }
    }
    public static void deleteDiagnosticOrderBySpecimen(FhirContext oContext,List<String> listIdSpecimenToDelete, String serverUrl, String logFileName)
    {
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            IBaseOperationOutcome resp=client.delete()
                    .resourceConditionalByType("DiagnosticOrder")
                    //.where(DiagnosticReport.SPECIMEN.hasId(idSpecimenToDelete))
                    .where(DiagnosticOrder.SPECIMEN.hasAnyOfIds(listIdSpecimenToDelete))
                    .execute();
            if(resp!=null)
            {
                OperationOutcome outcome = (OperationOutcome) resp;
                System.out.println(outcome.getIssueFirstRep().getDetailsElement().getValue());
            }

        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(logFileName,
                    exc.getMessage(),"Error");
        }
    }
    public static void deletePractitioner(FhirContext oContext,List<IdDt> listIdsPractitioner, String serverUrl, String logFileName)
    {
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            for(IdDt idToDelete:listIdsPractitioner)
            {

                IBaseOperationOutcome resp=client.delete()
                        .resourceById(idToDelete)
                        .execute();
                if(resp!=null)
                {
                    OperationOutcome outcome = (OperationOutcome) resp;
                    System.out.println(outcome.getIssueFirstRep().getDetailsElement().getValue());
                }
            }


        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(logFileName,
                    exc.getMessage(),"Error");
        }
    }
    public static void deleteSpecimen(FhirContext oContext,List<IdDt> listIdsSpecimen, String serverUrl, String logFileName)
    {
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            for(IdDt idToDelete:listIdsSpecimen)
            {

                IBaseOperationOutcome resp=client.delete()
                        .resourceById(idToDelete)
                        .execute();
                if(resp!=null)
                {
                    OperationOutcome outcome = (OperationOutcome) resp;
                    System.out.println(outcome.getIssueFirstRep().getDetailsElement().getValue());
                }
            }


        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(logFileName,
                    exc.getMessage(),"Error");
        }
    }
    public static void deleteListResourceSpecimen(FhirContext oContext,List<String> listIdsSpecimen, String serverUrl, String logFileName)
    {
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            for(String idToDelete:listIdsSpecimen)
            {
                //First search for List resource that refers to the specimen
                ca.uhn.fhir.model.api.Bundle oBundle = client.search().forResource(ListResource.class)
                        .where(new StringClientParam("item").matches().value(idToDelete))
                        .execute();

                List<ListResource> listExtractedResource= FhirResourceValidator.extractListResourceFromApiBundleObject(oBundle,oContext,serverUrl);
                for(ListResource oListResource:listExtractedResource)
                {
                    IBaseOperationOutcome resp=client.delete()
                            .resourceById(oListResource.getId())
                            .execute();
                    if(resp!=null)
                    {
                        OperationOutcome outcome = (OperationOutcome) resp;
                        System.out.println(outcome.getIssueFirstRep().getDetailsElement().getValue());
                    }
                }

            }


        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(logFileName,
                    exc.getMessage(),"Error");
        }
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
    public static String updateListResourceInTransaction(FhirContext oContext,List<ListResource> listOfListResource, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (ListResource oListResource:listOfListResource)
            {
                String searchPattern="List?";
                String key=null;
                //getKey
                searchPattern+="_id="+oListResource.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oListResource);
                oBundleEntry.setFullUrl(oListResource.getId());
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
    public static String updateBasicInTransaction(FhirContext oContext,List<Basic> listOfBasic, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (Basic oBasic:listOfBasic)
            {
                String searchPattern="Basic?";
                String key=null;
                //getKey
                searchPattern+="_id="+oBasic.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oBasic);
                oBundleEntry.setFullUrl(oBasic.getId());
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
    public static String updateConditionInTransaction(FhirContext oContext,List<Condition> listOfCondition, String serverUrl) throws Exception
    {
        Bundle respOutcome=null;
        String stringTransactionResult="{TransactionResultStatus:no}";
        try
        {

            Bundle resourceBundle=new Bundle();
            resourceBundle.setType(BundleTypeEnum.TRANSACTION);
            int compter=0;

            for (Condition oCondition:listOfCondition)
            {
                String searchPattern="Condition?";
                String key=null;
                //getKey
                searchPattern+="_id="+oCondition.getId().getValueAsString();
                Entry oBundleEntry= new Entry().setResource(oCondition);
                oBundleEntry.setFullUrl(oCondition.getId());
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
    public static List<Condition> getListConditionTrackedForPatient(FhirContext oContext,String patientId,String serverUrl,String logFileName)
    {
        List<Condition> listOfAssociatedCondition=new ArrayList<>();
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            Bundle oBundle=client.search()
                    .forResource(Condition.class)
                    .where(new StringClientParam("patient").matches().value(patientId))
                    .returnBundle(ca.uhn.fhir.model.dstu2.resource.Bundle.class)
                    .execute();
            for(Entry oEntry:oBundle.getEntry())
            {
                listOfAssociatedCondition.add((Condition)oEntry.getResource());
            }
        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(logFileName,
                    exc.getMessage(),"Error");
        }
        return listOfAssociatedCondition;
    }
    public static void deleteBundle(FhirContext oContext, IdDt idBundle, String serverUrl, String logFileName)
    {
        try
        {
            IGenericClient client=oContext.newRestfulGenericClient(serverUrl);
            IBaseOperationOutcome resp=client.delete().resourceById(idBundle)
                    .execute();
            if(resp!=null)
            {
                OperationOutcome outcome = (OperationOutcome) resp;
                System.out.println(outcome.getIssueFirstRep().getDetailsElement().getValue());
            }

        }
        catch (Exception exc)
        {
            FhirMediatorUtilities.writeInLogFile(logFileName,
                    exc.getMessage(),"Error");
        }
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

    public List<Condition> getListOfConditionToValidate() {
        return _listOfConditionToValidate;
    }

    public void setListOfConditionToValidate(List<Condition> _listOfConditionToValidate) {
        this._listOfConditionToValidate = _listOfConditionToValidate;
    }

    List<Condition> _listOfConditionToValidate;
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

    public void setListOfListResourceToValidate(List<ListResource> _listOfListResourceToValidate) {
        this._listOfListResourceToValidate = _listOfListResourceToValidate;
    }

    List<ListResource> _listOfListResourceToValidate;

    public void setListOfBasicToValidate(List<Basic> _listOfBasicToValidate) {
        this._listOfBasicToValidate = _listOfBasicToValidate;
    }

    List<Basic> _listOfBasicToValidate;


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

    public List<Condition> getListOfValidCondition() {
        return _listOfValidCondition;
    }

    public void setListOfValidCondition(List<Condition> _listOfValidCondition) {
        this._listOfValidCondition = _listOfValidCondition;
    }

    List<Condition> _listOfValidCondition;

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

    public List<ListResource> getListOfValidListResource() {
        return _listOfValidListResource;
    }

    List<ListResource> _listOfValidListResource;

    public List<Basic> getListOfValidBasic() {
        return _listOfValidBasic;
    }

    List<Basic> _listOfValidBasic;

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

    public List<Condition> getListOfInvalidCondition() {
        return _listOfInvalidCondition;
    }

    public void setListOfInvalidCondition(List<Condition> _listOfInvalidCondition) {
        this._listOfInvalidCondition = _listOfInvalidCondition;
    }

    List<Condition> _listOfInvalidCondition;

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

    public List<ListResource> getListOfInvalidListResource() {
        return _listOfInvalidListResource;
    }

    List<ListResource> _listOfInvalidListResource;

    public List<Basic> getListOfInvalidBasic() {
        return _listOfInvalidBasic;
    }

    List<Basic> _listOfInvalidBasic;

    public ResourcesValidation(FhirValidator validator)
    {
        this._validator=validator;
        this._listOfPractitionersToValidate=new ArrayList<>();
        this._listOfPatientToValidate=new ArrayList<>();
        this._listOfOrganizationToValidate=new ArrayList<>();
        this._listOfSpecimenToValidate=new ArrayList<>();
        this._listOfConditionToValidate=new ArrayList<>();
        this._listOfDiagnosticOrderToValidate=new ArrayList<>();
        this._listOfDiagnosticReportToValidate=new ArrayList<>();
        this._listOfObservationToValidate=new ArrayList<>();
        this._listOfListResourceToValidate=new ArrayList<>();
        this._listOfBasicToValidate=new ArrayList<>();
        //
        this._listOfValidPractitioner=new ArrayList<>();
        this._listOfValidPatient=new ArrayList<>();
        this._listOfValidOrganization=new ArrayList<>();
        this._listOfValidSpecimen=new ArrayList<>();
        this._listOfValidCondition=new ArrayList<>();
        this._listOfValidDiagnosticOrder=new ArrayList<>();
        this._listOfValidDiagnosticReport=new ArrayList<>();
        this._listOfValidObservation=new ArrayList<>();
        this._listOfValidListResource=new ArrayList<>();
        this._listOfValidBasic=new ArrayList<>();
        //
        this._listOfInvalidPractitioner=new ArrayList<>();
        this._listOfInvalidPatient=new ArrayList<>();
        this._listOfInvalidOrganization=new ArrayList<>();
        this._listOfInvalidSpecimen=new ArrayList<>();
        this._listOfInvalidCondition=new ArrayList<>();
        this._listOfInvalidDiagnosticOrder=new ArrayList<>();
        this._listOfInvalidDiagnosticReport=new ArrayList<>();
        this._listOfInvalidObservation=new ArrayList<>();
        this._listOfInvalidListResource=new ArrayList<>();
        this._listOfInvalidBasic=new ArrayList<>();

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
        for(Condition oConditionToValidate: this._listOfConditionToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oConditionToValidate);
            if(res.isSuccessful())
            {
                this._listOfValidCondition.add(oConditionToValidate);
            }
            else
            {
                this._listOfInvalidCondition.add(oConditionToValidate);
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
        for(ListResource oListResource:this._listOfListResourceToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oListResource);
            if(res.isSuccessful())
            {
                this._listOfValidListResource.add(oListResource);
            }
            else
            {
                this._listOfInvalidListResource.add(oListResource);
            }
        }
        for(Basic oBasic:this._listOfBasicToValidate)
        {
            ValidationResult res=null;
            res=this._validator.validateWithResult(oBasic);
            if(res.isSuccessful())
            {
                this._listOfValidBasic.add(oBasic);
            }
            else
            {
                this._listOfInvalidBasic.add(oBasic);
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
