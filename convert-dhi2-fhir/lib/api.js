/**
 *  setup for test/development mode
 */
 var btoa = require('btoa')
 var fs = require("fs");
 var fs_extra = require("fs-extra");
 var path = require("path");
 var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
 var csvWriter = require('csv-write-stream');
 var csvUtil = require('csv-util');
 var formidable = require('formidable');

process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';
 
//fs.gracefulify(realFs);

//console.log(fs);
//import {fs} from "fs-web";
//console.log("dirname:"+ __dirname);
//var fs;
var production = false;
//var serverUrl = 'http://localhost:8082/api';
//const testURL = 'https://play.dhis2.org/demo/api';
const testURL = 'http://localhost:8080/api';
//const basicAuth = `Basic ${btoa('admin:district')}`;

/*
 * Setup for production mode
 */
//const manifest = require('../manifest.webapp');
//var manifestFilePath=
const manifest = ReadJSONFile("manifest.webapp");
//console.log(manifest);
const URL = manifest.activities.dhis.href;
const URLHapi = manifest.activities.hapi.href;
//console.log("manifest:", manifest, URL);
const basicAuth = `Basic ${btoa(manifest.authentication)}`;
//Check if we are running development or production mode
if (URL && URL != "*") {
    var productionURL = URL  + "/api";
    production  = true;
}
var productionURLHapi = URLHapi;

const headers = production ? { 'Content-Type': 'application/json' } : {Authorization: basicAuth, 'Content-Type': 'application/json' };
const headersHapi = { 'Content-Type': 'application/json' };
const serverUrl = production ? productionURL : testURL;
const serverUrlHapi = productionURLHapi;

console.log("serverUrl:", serverUrl, "headers:", headers);
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
const fetchOptionsHapi = {
    method: 'GET',
    headers: headersHapi
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
/////////////////************************Function for csv data processing******************///////
//Return an Object with organisationUnits based on the name
exports.GetOrgUnitId= function GetOrgUnitId(orgUnitNameList,listAssociatedDataRow,listAssociatedResource,callback)
{
	var urlRequest=`${serverUrlHapi}/Organization?name=${orgUnitNameList}`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/json' );
	request.setRequestHeader( 'Accept', 'application/json' ); 
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

exports.getAllOrgUnits= function getAllOrgUnits(bundleParam,entryStoredData,listAssociatedDataRow,listAssociatedResource,callback)
{
	var urlRequest="";
	if(bundleParam=="")
	{
		urlRequest=`${serverUrlHapi}/Organization`;
	}
	else
	{
		urlRequest=`${bundleParam}`;
	}
	//var urlRequest=`${serverUrlHapi}/Organization`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/json' );
	request.setRequestHeader( 'Accept','application/json' ); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var myArr = JSON.parse(this.responseText);
			if(myArr.entry!="undefined")
			{
				for(var iterator=0;iterator<myArr.entry.length;iterator++)
				{
					entryStoredData.push(myArr.entry[iterator]);
				}
				//console.log(entryStoredData.length);
				if(myArr.link.length>0)
				{
					var hasNextPageBundle=false;
					var iterator=0;
					for(;iterator <myArr.link.length;iterator++)
					{
						if(myArr.link[iterator].relation=="next")
						{
							hasNextPageBundle=true;
							break;
							//GetOrgUnitId(myArr.link[iterator].url,listAssociatedDataRow,listAssociatedResource,callback);
						}
					}
					if(hasNextPageBundle==true)
					{
						getAllOrgUnits(myArr.link[iterator].url,entryStoredData,listAssociatedDataRow,listAssociatedResource,callback);
					}
					else
					{
						 var modifiedArray = [entryStoredData,listAssociatedDataRow,listAssociatedResource];
						 return callback(modifiedArray);
					}
					
				}
			
			}
			
		}
		else
		{
			//console.log(this.responseText);
		}
	};
	
	request.send();
	
}

exports.getAllBasics= function getAllBasics(bundleParam,entryStoredData,listResolvedOrganization,listAssociatedDataRow,listAssociatedResource,callback)
{
	var urlRequest="";
	if(bundleParam=="")
	{
		urlRequest=`${serverUrlHapi}/Basic`;
	}
	else
	{
		urlRequest=`${bundleParam}`;
	}
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/json' );
	request.setRequestHeader( 'Accept','application/json' ); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var myArr = JSON.parse(this.responseText);
			//console.log(myArr);
			for(var iterator=0;iterator<myArr.entry.length;iterator++)
			{
				entryStoredData.push(myArr.entry[iterator]);
			}
			if(myArr.link.length>0)
			{
				var hasNextPageBundle=false;
				var iterator=0;
				for(;iterator <myArr.link.length;iterator++)
				{
					if(myArr.link[iterator].relation=="next")
					{
						hasNextPageBundle=true;
						break;
						//GetOrgUnitId(myArr.link[iterator].url,listAssociatedDataRow,listAssociatedResource,callback);
					}
				}
				if(hasNextPageBundle==true)
				{
					getAllBasics(myArr.link[iterator].url,entryStoredData,listResolvedOrganization,listAssociatedDataRow,listAssociatedResource,callback);
				}
				else
				{
					 var modifiedArray = [entryStoredData,listResolvedOrganization,listAssociatedDataRow,listAssociatedResource];
					 return callback(modifiedArray);
				}
			}
		}
		
	};
	request.send();
}
exports.postData= function postData(resourceId,jsonData,fileName,callback)
{
	var urlRequest=`${serverUrlHapi}/Bundle/${resourceId}`;
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

exports.readCSVFile=function readCSVFile(_filename,callback)
{
	var filename=path.resolve(path.join(manifest.source_directory, "/", _filename));
	var csvParser = csvUtil.csvParser;
	var csvData = csvParser(filename,function(row){
	  var newRow = row.map(function(value,index){
		return value;
	  })
	  return newRow
	}).then(function(csvData){
  		return callback(csvData);
	});
}

exports.getListOfFiles=function getListOfFiles(filePath,callback)
{
	const folder =filePath;
	var listOfFiles=fs.readdir(folder,(err,files)=>
	{
		files.forEach(file => {
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
	var source=path.resolve(path.join(manifest.source_directory, "/", fileName));
	var destination=path.resolve(path.join(manifest.treated_directory, "/", newFileName));
	fs_extra.moveSync(source,destination);
}

exports.moveFileToErrors=function moveFileToErrors(fileName)
{
	var fileComponents=fileName.split(".");
	var newPart=new Date().toJSON();
	var tempResult=newPart.replace(/:/g,"");//replace all occurence of : by ""
	newPart=tempResult.replace(".","");
	var newFileName="";
	newFileName+=fileComponents[0]+newPart;
	newFileName+="."+fileComponents[1];
	var source=path.resolve(path.join(manifest.source_directory, "/", fileName));
	var destination=path.resolve(path.join(manifest.errors_directory, "/", newFileName));
	fs_extra.moveSync(source,destination);
}

exports.processUploadData=function processUploadData (req,res)
{
	var form = new formidable.IncomingForm();
	form.multiples = true;
	form.uploadDir = manifest.source_directory;
	//console.log(req);
	form.on('file', function(field, file) {
		//console.log(file);
		fs.rename(file.path, path.join(form.uploadDir, file.name));
	});
	// log any errors that occur
	form.on('error', function(err) {
		console.log('An error has occured: \n' + err);
	 });
	 // once all the files have been uploaded, send a response to the client
	form.on('end', function() {
		res.end('success');
	
	});
	form.parse(req);
	
}

exports.getLocationDataFile = function getLocationDataFile()
{
	return manifest.temp_directory;
}
exports.getLocationSourceFile = function getLocationSourceFile()
{
	return manifest.source_directory;
}
exports.getLocationTempResource = function getLocationTempResource()
{
	return manifest.resource_temp;
}
exports.resolvePathDirectory = function resolvePathDirectory(curentDirName,fileName)
{
	return path.join(curentDirName,fileName);
}
exports.getMinDataSetRecord = function getMinDataSetRecord()
{
	return manifest.minimun_dataset_record;
}

/////////////////**********************************************************************///////

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

function getOptionsDetails(listOptionIds,dataTrace,callback)
{
	var urlRequest=`${serverUrl}/options.json?filter=id:in:[${listOptionIds}]&fields=id,displayName,code&paging=false`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth);
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			var myArr = JSON.parse(this.responseText);
			//console.log(myArr);
			var modifiedArray=[myArr,dataTrace];
			return callback(modifiedArray);
		}
		//var myArr = JSON.parse(this.responseText);
		//console.log(myArr);
		
	};
	request.send();
}

function getOptions(listOptionSetIds,dataTrace,callback)
{
	var urlRequest=`${serverUrl}/optionSets.json?filter=id:in:[${listOptionSetIds}]&paging=false&fields=id,options`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth);
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
		var myArr = JSON.parse(this.responseText);
		//console.log(myArr.optionSets);
		var listOptionIds="";
		//console.log(myArr);
		for(var iterator=0;iterator<myArr.optionSets.length;iterator++)
		{
			if(myArr.optionSets[iterator].options.length>0)
			{
				for(var iteratorOption=0;iteratorOption<myArr.optionSets[iterator].options.length;iteratorOption++)
				{
					//console.log(myArr.optionSets[iterator].options[iteratorOption].id);
					if(iterator==0 && iteratorOption==0)
					{
						listOptionIds=myArr.optionSets[iterator].options[iteratorOption].id;
					}
					else
					{
						listOptionIds+=","+myArr.optionSets[iterator].options[iteratorOption].id;
					}
				}
			}
		}
		if(listOptionIds!="")
		{
			getOptionsDetails(listOptionIds,dataTrace,callback);
		}
		else
		{
			return callback([{ "options":[]},dataTrace]);
		}
		//console.log(listOptionIds);
		//return callback(listOptionIds);
	}
	};
	request.send();
}
exports.getOptionSetFromAttribute= function getOptionSetFromAttribute(listIdAttributes,listIdDataElement,dataTrace,callback)
{
	var urlRequest=`${serverUrl}/trackedEntityAttributes.json?paging=false&filter=id:in:[${listIdAttributes}]&fields=id,displayName,optionSet`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        //console.log(myArr.trackedEntityAttributes[0]);
        var listOptionSetIds="";
        for(var iterator=0;iterator<myArr.trackedEntityAttributes.length;iterator++)
        {
			if(iterator==0)
			{
				listOptionSetIds=myArr.trackedEntityAttributes[iterator].optionSet.id;
			}
			else 
			{
				listOptionSetIds+=","+myArr.trackedEntityAttributes[iterator].optionSet.id;
			}
			
		}
		
		
		if(listOptionSetIds!="" )
		{
			getOptionSetFromDataElement(listOptionSetIds,listIdDataElement,dataTrace,callback)
		}
		else
		{
			return callback([{ "options":[]},dataTrace])
		}
		
		//console.log(listOptionSetIds);
        //return callback(myArr);
		}
	};
	
	request.send();
	
}
function getOptionSetFromDataElement(_listOptionSetIds,listIdAttributes,dataTrace,callback)
{
	var urlRequest=`${serverUrl}/dataElements.json?paging=false&filter=id:in:[${listIdAttributes}]&fields=id,displayName,optionSet`;
	//console.log(urlRequest);
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type','application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        //console.log(myArr.trackedEntityAttributes[0]);
        var listOptionSetIds="";
        for(var iterator=0;iterator<myArr.dataElements.length;iterator++)
        {
			if(iterator==0)
			{
				listOptionSetIds=myArr.dataElements[iterator].optionSet.id;
			}
			else 
			{
				listOptionSetIds+=","+myArr.dataElements[iterator].optionSet.id;
			}
			
		}
		if(_listOptionSetIds!="")
		{
			listOptionSetIds+=","+_listOptionSetIds;
		}
		if(listOptionSetIds!="")
		{
			//console.log(listOptionSetIds);
			getOptions(listOptionSetIds,dataTrace,callback)
		}
		else
		{
			return callback([{ "options":[]},dataTrace]);
		}
		//console.log(listOptionSetIds);
        //return callback(myArr);
		}
	};
	
	request.send();
	
}
exports.GetAllOrganisationUnitsCallbakListPrograms= function GetAllOrganisationUnitsCallbakListPrograms(listOfPrograms,callback)
{
	//var urlRequest=`${serverUrl}/organisationUnits?paging=false&fields=:all`;
	//var orgUnitId="V5XvX1wr1kF";
	var urlRequest=`${serverUrl}/organisationUnits?paging=false&fields=:all`;
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
		else
		{
			//console.log(this.responseText);
		}
	};
	
	request.send();
	
}
exports.GetAllOrganisationUnitsCallbakListProgramsAndOptionSets= function GetAllOrganisationUnitsCallbakListProgramsAndOptionSets(listOfPrograms,listOptionSets,callback)
{
	//var urlRequest=`${serverUrl}/organisationUnits?paging=false&fields=:all`;
	//var orgUnitId="V5XvX1wr1kF";
	var urlRequest=`${serverUrl}/organisationUnits?paging=false&fields=:all`;
	var request = new XMLHttpRequest();
	request.open('GET',urlRequest, true);
	request.setRequestHeader( 'Content-Type',   'application/json' );
	request.setRequestHeader( 'Accept', 'application/json' );
	request.setRequestHeader("Authorization", basicAuth); 
	request.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
        var myArr = JSON.parse(this.responseText);
        var modifiedArray=[myArr,listOfPrograms,listOptionSets];
        //console.log(myArr);
        return callback(modifiedArray);
		}
		else
		{
			//console.log(this.responseText);
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
	var urlRequest=`${serverUrl}/events.json?program=${programId}&skipPaging=true&fields=:all`;
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
	var urlRequest=`${serverUrl}/trackedEntityInstances.json?ou=${queryOrgUnits}&program=${queryPrograms}&skipPaging=true&fields=:all`;
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
	//console.log(urlRequest);
	
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
		else
		{
			//console.log(this.responseText);
		}
	};
	
	request.send();
	
}
exports.getProgramStageMetaDataInfo= function getProgramStageMetaDataInfo(programStageIds,listOfAttributeFields,listDisplayNameIdMapping,callback)
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
        var modifierArray=[myArr,listOfAttributeFields,programStageIds,listDisplayNameIdMapping];
        //console.log(myArr);
        return callback(modifierArray);
		}
	};
	
	request.send();
	
}
exports.getDatsElementsMetaDataInfo= function getDatsElementsMetaDataInfo(dataElemtsIds,progStage,listOfAttributeFields,listDisplayNameIdMapping,callback)
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
        var modifierArray=[progStage,myArr,listOfAttributeFields,listDisplayNameIdMapping];
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
exports.getPractitionerAttributesMapping = function getPractitionerAttributesMapping()
{
	return manifest.practitioner_attribute_mapping;
}
exports.getPractitionerObservationPerformerAttributesMapping = function getPractitionerObservationPerformerAttributesMapping()
{
	return manifest.practitioner_observation_performer_attribute_mapping;
}
exports.getPractitionerSpecimenHandlingAttributesMapping = function getPractitionerSpecimenHandlingAttributesMapping()
{
	return manifest.practitioner_specimen_handling_attribute_mapping;
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
exports.getListAttributesMapping = function getListAttributesMapping()
{
	return manifest.list_attribute_mapping;
}
exports.getProgramsAndStagesToTrack = function getProgramsAndStagesToTrack()
{
	return manifest.programs_progstages_tracked;
}
exports.getVitalStatus = function getVitalStatus()
{
	return manifest.vital_status;
}
exports.getPractitionerStageNature= function getPractitionerStageNature()
{
	return manifest.practitioner_stage_nature;
}
exports.getFormatPatientId= function getFormatPatientId()
{
	return manifest.format_patientid_hyphen;
}
exports.getFormatSpecimenId= function getFormatSpecimenId()
{
	return manifest.format_specimenid_hyphen;
}
exports.getAttributeWithOptionSetValue= function getAttributeWithOptionSetValue()
{
	return manifest.attribute_with_optionset_value;
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
	var contents = fs.readFileSync(filePath);
	var jsonContent = JSON.parse(contents);
	return jsonContent;
}


exports.writeJSONFile= function writeJSONFile(fileName,jsonContent)
{
	var filePath=manifest.temp_directory+"/"+fileName+".json";
	var res = fs.writeFileSync(filePath,jsonContent,"utf-8");
}
exports.generateCSVFile = function generateCSVFile(listHeader,filename,programName)
{
	
	var writer = csvWriter({ headers: listHeader});
	writer.pipe(fs.createWriteStream(manifest.temp_directory+"/"+filename+'.csv'));
	var listContentCSV=[];
	listContentCSV.push(programName);
	for(var i=0;i<listHeader.length-1;i++)
	{
		listContentCSV.push('');
	}
	for(var j=0;j<100;j++)
	{
		writer.write(listContentCSV);
	}
	writer.end();
}
