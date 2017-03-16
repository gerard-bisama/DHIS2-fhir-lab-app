/**
 *  setup for test/development mode
 */
 var btoa = require('btoa')
 var fs = require("fs");
 var path = require("path");
 var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
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
//console.log("manifest:", manifest, URL);
const basicAuth = `Basic ${btoa(manifest.authentication)}`;
//Check if we are running development or production mode
if (URL && URL != "*") {
    var productionURL = URL  + "/api";
    production  = true;
}

const headers = production ? { 'Content-Type': 'application/json' } : {Authorization: basicAuth, 'Content-Type': 'application/json' };
const serverUrl = production ? productionURL : testURL;

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
exports.getDiagnosticReportAttributesMapping = function getDiagnosticReportAttributesMapping()
{
	return manifest.diagnosticreport_attribute_mapping;
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
