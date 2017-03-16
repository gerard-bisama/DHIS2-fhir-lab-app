package org.mediator.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import org.hl7.fhir.dstu3.model.Bundle;
//import org.hl7.fhir.dstu3.model.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by server-hit on 10/22/16.
 */
public class TestProcess {
    public static void main(String[] args)
    {
        MediatorFhirConfig mediatorConfiguration=new MediatorFhirConfig();
        try {
            Practitioner oPractitioner=new Practitioner();
            oPractitioner.setId("123");
            HumanNameDt name=new HumanNameDt();
            name.addFamily("Gerard");
            oPractitioner.setName(name);
            List<IResource> listOfResource=new ArrayList<IResource>();
            listOfResource.add(oPractitioner);
            System.out.print(listOfResource.get(0).getResourceName());



        } catch (Exception e) {
           System.out.print(e.getMessage());
        }
    }

}
