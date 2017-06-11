package org.mediator.fhir;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public String getLogFile() {
        this.logFile=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            this.logFile= prop.getProperty("logFile");
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
        return this.logFile;
    }

    String logFile;

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

    public String getServerSourceDataAppName() {
        this.serverSourceDataAppName=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverSourceDataAppName= prop.getProperty("serverSourceDataAppName");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverSourceDataAppName;
    }

    String serverSourceDataAppName;

    public int getServerSourceDataPort() {
        this.serverSourceDataPort=-1;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverSourceDataPort=Integer.parseInt(prop.getProperty("serverSourceDataPort"));
        }
        catch (IOException exc)
        {
            return -1;
        }
        catch (Exception exc)
        {
            return -1;
        }
        return serverSourceDataPort;
    }

    int serverSourceDataPort;

    public String getServerSourceDataScheme() {
        this.serverSourceDataScheme=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverSourceDataScheme= prop.getProperty("serverSourceDataScheme");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverSourceDataScheme;
    }

    String serverSourceDataScheme;

    public String getServerSourceDataURI() {
        this.serverSourceDataURI=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverSourceDataURI= prop.getProperty("serverSourceDataURI");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverSourceDataURI;
    }

    String serverSourceDataURI;

    public String getServerSourceDataFhirDataModel() {
        this.serverSourceDataFhirDataModel=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverSourceDataFhirDataModel= prop.getProperty("serverSourceDataFhirDataModel");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverSourceDataFhirDataModel;
    }

    String serverSourceDataFhirDataModel;



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

    public String getServerTrackerURI() {

        this.serverTrackerURI=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTrackerURI= prop.getProperty("serverSourceTrackerURI");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverTrackerURI;
    }

    String serverTrackerURI;

    public int getServerTrackerPort() {
        this.serverTrackerPort=-1;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTrackerPort=Integer.parseInt(prop.getProperty("serverSourceTrackerPort"));
        }
        catch (IOException exc)
        {
            return -1;
        }
        catch (Exception exc)
        {
            return -1;
        }
        return serverTrackerPort;
        //return serverTrackerPort;
    }

    int serverTrackerPort;

    public String getServerTrackerScheme() {
        this.serverTrackerScheme=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTrackerScheme= prop.getProperty("serverSourceTrackerScheme");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        //return serverTargetscheme;
        return serverTrackerScheme;
    }

    String serverTrackerScheme;

    public String getServerTrackerAppName() {
        this.serverTrackerAppName=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTrackerAppName= prop.getProperty("serverSourceTrackerAppName");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return serverTrackerAppName;
    }

    String serverTrackerAppName;

    public String getServerTrackerFhirDataModel() {
        this.serverTrackerFhirDataModel=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            serverTrackerFhirDataModel= prop.getProperty("serverSourceTrackerFhirDataModel");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        //return serverTrackerAppName;
        return serverTrackerFhirDataModel;
    }

    String serverTrackerFhirDataModel;

    public List<TrackerResourceMap> getTrackerResource() {
        List<TrackerResourceMap> listResourceMap=new ArrayList<>();
        this.trackerResource=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            trackerResource= prop.getProperty("trackersResourceId");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        //return serverTrackerAppName;
        //transfor the resource to class
        Gson gson=new Gson();
        String[] trackerResourceTable=this.trackerResource.split(";");
        for(String oTrackerResource:trackerResourceTable)
        {
            TrackerResourceMap oTrackerMap=gson.fromJson(oTrackerResource,TrackerResourceMap.class);
            listResourceMap.add(oTrackerMap);
        }

        return listResourceMap;
        //return trackerResource;
    }

    String trackerResource;

    public String getAuthentication() {
        this.authentication=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            authentication= prop.getProperty("authentication");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        //return serverTrackerAppName;
        return authentication;
    }

    String authentication;

    public String getTrackedEntity() {
        this.trackedEntity=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            trackedEntity= prop.getProperty("trackedEntity");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        //return serverTrackerAppName;
        return trackedEntity;
    }

    String trackedEntity;

    public String getAllowCaseUpdate() {
        this.allowCaseUpdate=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            allowCaseUpdate= prop.getProperty("allowCaseUpdate");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return allowCaseUpdate;
    }

    String allowCaseUpdate;
    public String getAllowAddNewCase() {
        this.allowAddNewCase=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            allowAddNewCase= prop.getProperty("allowAddNewCase");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return allowAddNewCase;
    }

    String allowAddNewCase;


    public String getResourceTempLocation() {
        this.resourceTempLocation=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            resourceTempLocation= prop.getProperty("resourceTempLocation");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return resourceTempLocation;
    }

    String resourceTempLocation;

    public String getDataElementMappingFile() {
        this.dataElementMappingFile=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            dataElementMappingFile= prop.getProperty("dataElementMappingFile");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return dataElementMappingFile;
    }

    String dataElementMappingFile;

    public String getFhirAttributeMapping() {
        this.fhirAttributeMapping=null;
        try
        {
            prop.load(new FileInputStream(this.propFilePath));
            fhirAttributeMapping= prop.getProperty("fhirAttributeMapping");
        }
        catch (IOException exc)
        {
            return null;
        }
        catch (Exception exc)
        {
            return null;
        }
        return fhirAttributeMapping;
    }

    String fhirAttributeMapping;


}
