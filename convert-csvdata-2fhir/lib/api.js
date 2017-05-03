/**
 *  setup for test/development mode
 */
 var btoa = require('btoa')
 var fs = require("fs");
 var fs_extra = require("fs-extra");
 var path = require("path");
 var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
 var csvWriter = require('csv-write-stream');
 var csvUtil = require('csv-util')
 
//fs.gracefulify(realFs);

//console.log(fs);
//import {fs} from "fs-web";
//console.log("dirname:"+ __dirname);
//var fs;
var production = true;
//var serverUrl = 'http://localhost:8082/api';
//const testURL = 'https://play.dhis2.org/demo/api';
//const testURL = 'http://localhost:8080/api';
//const basicAuth = `Basic ${btoa('admin:district')}`;

/*
 * Setup for production mode
 */
//const manifest = require('../manifest.webapp');
//var manifestFilePath=
const manifest = ReadJSONFile("manifest.webapp");
//console.log(manifest);
const URL = manifest.activities.dhis.href;
//console.log("manifest:", manifest, URL);
const basicAuth = `Basic ${btoa(manifest.authentication)}`;
//Check if we are running development or production mode
var productionURL = URL;
/*
if (URL && URL != "*") {
    var productionURL = URL  + "/api";
    production  = true;
}
*/
const headers = production ? { 'Content-Type': 'application/json' } : {Authorization: basicAuth, 'Content-Type': 'application/json' };
const serverUrl = production ? productionURL : testURL;

//console.log("serverUrl:", serverUrl, "headers:", headers);
/***********************************************************/
//config.baseUrl = serverUrl;
//config.headers=headers;
/**
 * Default options object to send along with each request
 */
const fetchOptions = {
    method: 'GET',
    headers: headers
};

/**
 * `fetch` will not reject the promise on the a http request that is not 2xx, as those requests could also return valid responses.
 * We will only treat status codes in the 200 range as successful and reject the other responses.
 */
function onlySuccessResponses(response) {
    if (response.status >= 200 && response.status < 300) {
        return Promise.resolve(response);
    } else {
        console.log("Request failed:", response);
        alert(`Request failed: ${response.statusText}`);
        return Promise.reject(response);
    }
}

//Return an Object with organisationUnits attribute
exports.GetAllOrganisationUnits= function GetAllOrganisationUnits(callback)
{
	var urlRequest=`${serverUrl}/organisationUnits/?paging=false&fields=:all`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        //console.log(myArr);
        return callback(myArr);
		}
	};
	
	request.send();
	
}
//Return an Object with organisationUnits based on the name
exports.GetOrgUnitId= function GetOrgUnitId(orgUnitNameList,listAssociatedDataRow,listAssociatedResource,callback)
{
	var urlRequest=`${serverUrl}/Organization?name=${orgUnitNameList}`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	//request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifiedArray = [myArr,listAssociatedDataRow,listAssociatedResource];
        //console.log(myArr);
        return callback(modifiedArray);
		}
	};
	
	request.send();
	
}
exports.postData= function postData(resourceId,jsonData,fileName,callback)
{
	var urlRequest=`${serverUrl}/Bundle/${resourceId}`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('PUT',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/fhir+json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	//request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && (this.status == 200||this.status == 201)) {
        var myArr = JSON.parse(this.responseText);
        var modifiedArray=[myArr,fileName];
        return callback(modifiedArray);
		}
	};
	
	request.send(jsonData);
	
}
exports.GetAllOrganisationUnitsCallbakListPrograms= function GetAllOrganisationUnitsCallbakListPrograms(listOfPrograms,callback)
{
	var urlRequest=`${serverUrl}/organisationUnits?paging=false&fields=:all`;
	//var orgUnitId=
	//var urlRequest=`${serverUrl}/organisationUnits?filer=id:eq:${orgUnitId}&paging=false&fields=:all`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifiedArray=[myArr,listOfPrograms];
        //console.log(myArr);
        return callback(modifiedArray);
		}
	};
	
	request.send();
	
}
exports.GetDataElementsFromProgramStages= function GetDataElementsFromProgramStages(trackerEntityInstanceId,programStageId,callback)
{
	var urlRequest=`${serverUrl}/events.json?trackedEntityInstance=${trackerEntityInstanceId}&programStage=${programStageId}&paging=false&fields=:all`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        //var modifiedArray=[myArr,listOfProgramIds];
        //console.log(myArr);
        return callback(myArr);
		}
	};
	
	request.send();
	
}
exports.getAllProgramEvents= function getAllProgramEvents(programId,listoProgramStages,callback)
{
	var urlRequest=`${serverUrl}/events.json?program=${programId}&paging=false&fields=:all`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifiedArray=[myArr,listoProgramStages];
        //console.log(myArr);
        return callback(modifiedArray);
		}
	};
	
	request.send();
	
}
exports.GetTrackedEntityInstances= function GetTrackedEntityInstances(orgUnitId,callback)
{
	var urlRequest=`${serverUrl}/trackedEntityInstances.json?ou=${orgUnitId}&paging=false&fields=:all`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth);
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var refList=[];
        //console.log(myArr);
        return callback(myArr);
		}
	};
	
	request.send();
}
exports.GetTrackedEntityInstancesFromOrgunitListAndProgramList=function GetTrackedEntityInstancesFromOrgunitListAndProgramList(listOfrgUnitId,listOfProgramId,listOfStages,callback)
{
	var queryOrgUnits="";
	var queryPrograms="";
	for(var i=0;i<listOfrgUnitId.length;i++)
	{
		if(i==0)
		{
			queryOrgUnits+=listOfrgUnitId[i];
		}
		else
		{
			queryOrgUnits+=";"+listOfrgUnitId[i];
		}
		
	}
	for(var i=0;i<listOfProgramId.length;i++)
	{
		if(i==0)
		{
			queryPrograms+=listOfProgramId[i];
		}
		else
		{
			queryPrograms+=";"+listOfProgramId[i];
		}
		
	}
	var urlRequest=`${serverUrl}/trackedEntityInstances.json?ou=${queryOrgUnits}&program=${queryPrograms}&paging=false&fields=:all`;
	console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth);
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifiedArray=[myArr,listOfProgramId,listOfStages];
        //var refList=[];
        //console.log(myArr);
        return callback(modifiedArray);
		}
	};
	
	request.send();
}
exports.GetTrackedEntityInstancesFromOrgunitListAndProgramId=function GetTrackedEntityInstancesFromOrgunitListAndProgramId(listOfrgUnitId,programId,listOfStages,callback)
{
	var queryOrgUnits="";
	var queryPrograms="";
	for(var i=0;i<listOfrgUnitId.length;i++)
	{
		if(i==0)
		{
			queryOrgUnits+=listOfrgUnitId[i];
		}
		else
		{
			queryOrgUnits+=";"+listOfrgUnitId[i];
		}
		
	}
	queryPrograms=programId;
	var urlRequest=`${serverUrl}/trackedEntityInstances.json?ou=${queryOrgUnits}&program=${queryPrograms}&paging=false&fields=:all`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth);
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifiedArray=[myArr,programId,listOfStages];
        //var refList=[];
        //console.log(myArr);
        return callback(modifiedArray);
		}
	};
	
	request.send();
}
exports.GetTrackedEntityInstancesFromOrgunitListAndProgramIdAndKeepDataElementsTrack=function GetTrackedEntityInstancesFromOrgunitListAndProgramIdAndKeepDataElementsTrack(listOfrgUnitId,programId,listOfStages,listDataElementObject,callback)
{
	var queryOrgUnits="";
	var queryPrograms="";
	for(var i=0;i<listOfrgUnitId.length;i++)
	{
		if(i==0)
		{
			queryOrgUnits+=listOfrgUnitId[i];
		}
		else
		{
			queryOrgUnits+=";"+listOfrgUnitId[i];
		}
		
	}
	queryPrograms=programId;
	var urlRequest=`${serverUrl}/trackedEntityInstances.json?ou=${queryOrgUnits}&program=${queryPrograms}&paging=false&fields=:all`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth);
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifiedArray=[myArr,programId,listOfStages,listDataElementObject];
        //var refList=[];
        //console.log(myArr);
        return callback(modifiedArray);
		}
	};
	
	request.send();
}

exports.GetTrackedEntityInstancesFromOrgunitList=function GetTrackedEntityInstancesFromOrgunitList(listOfrgUnitId,callback)
{
	var queryOrgUnits="";
	for(var i=0;i<listOfrgUnitId.length;i++)
	{
		if(i==0)
		{
			queryOrgUnits+=listOfrgUnitId[i];
		}
		else
		{
			queryOrgUnits+=";"+listOfrgUnitId[i];
		}
		
	}
	var urlRequest=`${serverUrl}/trackedEntityInstances.json?ou=${queryOrgUnits}&paging=false&fields=:all`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth);
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var refList=[];
        //console.log(myArr);
        return callback(myArr);
		}
	};
	
	request.send();
}
exports.getProgramMetaDataInfo= function getProgramMetaDataInfo(programId,callback)
{
	var urlRequest=`${serverUrl}/programs.json?paging=false&filter=id:in:[${programId}]&fields=id,name,programTrackedEntityAttributes,programStages`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        //console.log(myArr);
        return callback(myArr);
		}
	};
	
	request.send();
	
}
exports.getProgramStageMetaDataInfo= function getProgramStageMetaDataInfo(programStageIds,listOfAttributeFields,callback)
{
	var queryStageIds="";
	for(var i=0;i<programStageIds.length;i++)
	{
		if(i==0)
		{
			queryStageIds+=programStageIds[i];
		}
		else
		{
			queryStageIds+=","+programStageIds[i];
		}
	}
	var urlRequest=`${serverUrl}/programStages.json?filter=id:in:[${queryStageIds}]&fields=:all&paging=false`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifierArray=[myArr,listOfAttributeFields,programStageIds];
        //console.log(myArr);
        return callback(modifierArray);
		}
	};
	
	request.send();
	
}
exports.getDatsElementsMetaDataInfo= function getDatsElementsMetaDataInfo(dataElemtsIds,progStage,listOfAttributeFields,callback)
{
	var urlRequest=`${serverUrl}/dataElements.json?paging=false&filter=id:in:[${dataElemtsIds}]`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifierArray=[progStage,myArr,listOfAttributeFields];
        //myArr.push("parent");
        //console.log(myArr);
        return callback(modifierArray);
		}
	};
	
	request.send();
	
}
/*
export function WriteJsonFile(jsonString)
{
	var fileRep=manifest.temp_directory;
	if(fileRep!="")
	{
		//console.log(fs);
		var result=false;
		checkDirectory(fileRep, function(error) { 
			 if(error) {
				console.log("Error:", error);
			}
			else
			{
				console.log("Good!!");
			}
		});
		
	}
}
//check if the directory exist, and if not create it

export function  checkDirectory(directory) {
	 fs.stat(directory, function(err, stats) {
    //Check if error defined and the error code is "not exists"
    if (err && err.errno === 34) {
      //Create the directory, call the callback.
      fs.mkdir(directory, callback);
    } else {
      //just in case there was a different error:
      callback(err)
    }
  });
}
* */
exports.GetTrackedEntitiesMapping = function GetTrackedEntitiesMapping()
{
	return manifest.entities_mapping;
}
exports.GetPatientAttributesMapping = function GetPatientAttributesMapping()
{
	return manifest.patient_attribute_mapping;
}
exports.GetPractitionerAttributesMapping = function GetPractitionerAttributesMapping()
{
	return manifest.practitioner_attribute_mapping;
}
exports.GetSpecimenAttributesMapping = function GetSpecimenAttributesMapping()
{
	return manifest.specimen_attribute_mapping;
}
exports.getOrderAttributesMapping = function getOrderAttributesMapping()
{
	return manifest.order_attribute_mapping;
}
exports.getObservationAttributesMapping = function getObservationAttributesMapping()
{
	return manifest.observation_attribute_mapping;
}
exports.getConditionAttributesMapping = function getConditionAttributesMapping()
{
	return manifest.condition_attribute_mapping;
}
exports.getDiagnosticReportAttributesMapping = function getDiagnosticReportAttributesMapping()
{
	return manifest.diagnosticreport_attribute_mapping;
}
exports.getProgramsAndStagesToTrack = function getProgramsAndStagesToTrack()
{
	return manifest.programs_progstages_tracked;
}
exports.getLocationDataFile = function getLocationDataFile()
{
	return manifest.temp_directory;
}
exports.getLocationTempResource = function getLocationTempResource()
{
	return manifest.resource_temp;
}
exports.getMinDataSetRecord = function getMinDataSetRecord()
{
	return manifest.minimun_dataset_record;
}

function ReadJSONFile(fileName)
{
	var arrayPath=__dirname.split('/');
	var parentDirectory="/";
	for(var i=0;i<(arrayPath.length)-1;i++)
	{
		parentDirectory+=arrayPath[i]+"/";
	}
	//console.log("-------------");
	var filePath=path.resolve(path.join(parentDirectory, "/", fileName));
	//console.log(filePath);
	
	var contents = fs.readFileSync(filePath);
	var jsonContent = JSON.parse(contents);
	return jsonContent;
}
exports.readResourceJSONFile=  function readResourceJSONFile(fileName)
{
	var filePath=path.resolve(path.join(manifest.resource_temp, "/", fileName));
	//console.log(filePath);
	
	var contents = fs.readFileSync(filePath);
	var jsonContent = JSON.parse(contents);
	return jsonContent;
}
exports.writeJSONFile= function writeJSONFile(fileName,oResource)
{
	var filePath=path.resolve(path.join(manifest.resource_temp, "/", fileName));
	if(fs.existsSync(filePath)==true)
	{
		var previousContents = fs.readFileSync(filePath);
		var oBundle = JSON.parse(previousContents);
		for(var iteratorEntry=0; iteratorEntry<oResource.entry.length;iteratorEntry++)
		{
			oBundle.entry.push(oResource.entry[iteratorEntry]);
		}
		var contents=JSON.stringify(oBundle);
		fs.writeFileSync(filePath,contents);
	}
	else
	{
		var contents=JSON.stringify(oResource);
		fs.writeFileSync(filePath,contents);
	}
}
exports.readCSVFile=function readCSVFile(_filename,callback)
{
	var filename=path.resolve(path.join(manifest.temp_directory, "/", _filename));
	var csvParser = csvUtil.csvParser;
	var csvData = csvParser(filename,function(row){
	  var newRow = row.map(function(value,index){
		return value;
	  })
	  return newRow
	}).then(function(csvData){
  		//console.log(csvData)
  		//var listCsvData=[];
  		//listCsvData=[csvData,filename]
  		return callback(csvData);
	});
}
exports.getListOfFiles=function getListOfFiles(filePath,callback)
{
	const folder =filePath;
	var listOfFiles=fs.readdir(folder,(err,files)=>
	{
		files.forEach(file => {
		//console.log(file);
		//var listModifiedFileRef=[file,listOfResourceTrace];
		callback(file);
	  });
	});
}
exports.moveFileToTreated=function moveFileToTreated(fileName)
{
	var fileComponents=fileName.split(".");
	var newPart=new Date().toJSON();
	var tempResult=newPart.replace(/:/g,"");//replace all occurence of : by ""
	newPart=tempResult.replace(".","");
	var newFileName="";
	newFileName+=fileComponents[0]+newPart;
	newFileName+="."+fileComponents[1];
	var source=path.resolve(path.join(manifest.temp_directory, "/", fileName));
	var destination=path.resolve(path.join(manifest.treated_directory, "/", newFileName));
	fs_extra.moveSync(source,destination);
}
exports.moveFileToErrors=function moveFileToErrors(fileName)
{
	var source=path.resolve(path.join(manifest.temp_directory, "/", fileName));
	var destination=path.resolve(path.join(manifest.errors_directory, "/", fileName));
	fs_extra.moveSync(source,destination);
}
