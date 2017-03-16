package org.mediator.fhir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by server-hit on 10/26/16.
 */
public class SynDateConfigFile {
    String lastSyncDate;
    String propSyncFile;
    Properties prop=new Properties();
    public SynDateConfigFile()
    {
        this.propSyncFile="/home/fhirmediator/config/fhirmediator_sync.properties";
    }
    public String getLastSyncDate() {
        //return lastSyncDate;
        this.lastSyncDate=null;
        try
        {
            prop.load(new FileInputStream(this.propSyncFile));
            lastSyncDate= prop.getProperty("lastSyncDate");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        //return sourceServerFhirDataModel;
        return lastSyncDate;
    }

    public void setLastSyncDate(String lastSyncDate) throws Exception {

        try
        {
            prop.setProperty("lastSyncDate",lastSyncDate);
            prop.store(new FileOutputStream(this.propSyncFile), null);
        }
        catch (IOException exc)
        {
            throw new Exception(exc.getMessage());
        }
        catch (Exception exc)
        {
            throw new Exception(exc.getMessage());
        }
    }



}
