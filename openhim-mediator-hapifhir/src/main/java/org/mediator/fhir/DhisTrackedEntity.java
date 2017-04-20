package org.mediator.fhir;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by server-hit on 4/19/17.
 */
public class DhisTrackedEntity {
    public String orgUnit;
    public String trackedEntity;
    public List<Attribute> attributes=new ArrayList<>();
    public void addAttribute(String _attribute,String _value)
    {
        Attribute oAttribute=new Attribute();
        oAttribute.attribute=_attribute;
        oAttribute.value=_value;
        attributes.add(oAttribute);
    }
}
class Attribute
{
    public String attribute;
    public String value;
}