package org.mediator.fhir;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by server-hit on 10/21/16.
 */
public class MediatorFhirConfig {

    Properties prop=new Properties();
    String propFilePath;
    String propSyncFile;
    String sourceServerScheme;

    public void setProp(Properties prop) {
        this.prop = prop;
    }

    String lastSyncDate;
    public MediatorFhirConfig()
    {
        this.propFilePath="/home/fhirmediator/config/fhirmediator.properties";
        this.propSyncFile="/home/fhirmediator/config/fhirmediator_sync.properties";
    }


    public String getSourceServerURI() {
        this.sourceServerURI=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            sourceServerURI= prop.getProperty("serverSourceURI");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return sourceServerURI;
    }

    public void setSourceServerURI(String sourceServerURI) {
        this.sourceServerURI = sourceServerURI;
    }

    String sourceServerURI;

    public int getSourceServerPort() {
        this.sourceServerPort=-1;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            sourceServerPort=Integer.parseInt(prop.getProperty("serverSourcePort"));
        }
        catch (IOException exc)
        {
            return -1;
        }
        catch (Exception exc)
        {
            return -1;
        }
        return sourceServerPort;
    }

    public void setSourceServerPort(int sourceServerPort) {
        this.sourceServerPort = sourceServerPort;
    }

    int sourceServerPort;

    public String getSourceServerScheme() {
        this.sourceServerScheme =null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            sourceServerScheme = prop.getProperty("serverSourceScheme");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return sourceServerScheme;
    }

    public void setSourceServerScheme(String sourceServerScheme) {
        this.sourceServerScheme = sourceServerScheme;
    }



    public String getSourceServerFhirDataModel() {
        this.sourceServerFhirDataModel=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            sourceServerFhirDataModel= prop.getProperty("serverSourceFhirDataModel");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return sourceServerFhirDataModel;
    }

    public void setSourceServerFhirDataModel(String sourceServerFhirDataModel) {
        this.sourceServerFhirDataModel = sourceServerFhirDataModel;
    }

    String sourceServerFhirDataModel;

    public String getMinTresholdSyncDate() {
        this.minTresholdSyncDate=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            minTresholdSyncDate= prop.getProperty("minTresholdSyncDate");
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
        return minTresholdSyncDate;
    }

    public void setMinTresholdSyncDate(String minTresholdSyncDate) {
        this.minTresholdSyncDate = minTresholdSyncDate;
    }

    String minTresholdSyncDate;

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



    public String getPathForResourceWithError() {
        this.pathForResourceWithError=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            pathForResourceWithError= prop.getProperty("pathForResourceWithError");
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
        return pathForResourceWithError;
    }

    public void setPathForResourceWithError(String pathForResourceWithError) {
        this.pathForResourceWithError = pathForResourceWithError;
    }

    String pathForResourceWithError;

    public String getServerSourceAppName() {
        this.serverSourceAppName=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverSourceAppName= prop.getProperty("serverSourceAppName");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverSourceAppName;
    }

    public void setServerSourceAppName(String serverSourceAppName) {
        this.serverSourceAppName = serverSourceAppName;
    }

    String serverSourceAppName;
    String serverTargetURI;
    int serverTargetPort;
    String serverTargetscheme;
    String serverTargetAppName;
    String serverTargetFhirDataModel;

    public String getServerTargetFhirDataModel() {
        this.serverTargetFhirDataModel=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTargetFhirDataModel= prop.getProperty("serverTargetFhirDataModel");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverTargetFhirDataModel;
    }

    public void setServerTargetFhirDataModel(String serverTargetFhirDataModel) {
        this.serverTargetFhirDataModel = serverTargetFhirDataModel;
    }
    public String getServerTargetAppName() {
        this.serverTargetAppName=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTargetAppName= prop.getProperty("serverTargetAppName");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverTargetAppName;
    }

    public void setServerTargetAppName(String serverTargetAppName) {
        this.serverTargetAppName = serverTargetAppName;
    }



    public String getServerTargetscheme() {
        this.serverTargetscheme=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTargetscheme= prop.getProperty("serverTargetScheme");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverTargetscheme;
    }

    public void setServerTargetscheme(String serverTargetscheme) {
        this.serverTargetscheme = serverTargetscheme;
    }



    public int getServerTargetPort() {
        this.serverTargetPort=-1;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTargetPort=Integer.parseInt(prop.getProperty("serverTargetPort"));
        }
        catch (IOException exc)
        {
            return -1;
        }
        catch (Exception exc)
        {
            return -1;
        }
        return serverTargetPort;
    }

    public void setServerTargetPort(int serverTargetPort) {
        this.serverTargetPort = serverTargetPort;
    }



    public String getServerTargetURI() {
        this.serverTargetURI=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTargetURI= prop.getProperty("serverTargetURI");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverTargetURI;
    }

    public void setServerTargetURI(String serverTargetURI) {
        this.serverTargetURI = serverTargetURI;
    }



}
