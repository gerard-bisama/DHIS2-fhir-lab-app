package org.mediator.fhir;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by server-hit on 4/18/17.
 */
public class DhisEvent {
    public String program;
    public String programStage;
    public String orgUnit;
    public String eventDate;
    public String trackedEntityInstance;
    public List<dataValue> dataValues=new ArrayList<>();
    public void addDataValue(String _dataElement,String _value)
    {
        dataValue oDataValue=new dataValue();
        oDataValue.dataElement=_dataElement;
        oDataValue.value=_value;
        dataValues.add(oDataValue);
    }
}
class dataValue
{
    public String dataElement;
    public String value;
    //public dataValue
}