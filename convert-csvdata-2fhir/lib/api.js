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
 

var production = true;

const manifest = ReadJSONFile("manifest.webapp");
//console.log(manifest);
const URLHapi = manifest.activities.hapi.href;
//Check if we are running development or production mode
var productionURLHapi = URLHapi;

const headersHapi = { 'Content-Type': 'application/json' };
const serverUrl = productionURLHapi;

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
exports.getLocationSourceFile = function getLocationSourceFile()
{
	return manifest.source_directory;
}
exports.getLocationTempResource = function getLocationTempResource()
{
	return manifest.resource_temp;
}
exports.getMinDataSetRecord = function getMinDataSetRecord()
{
	return manifest.minimun_dataset_record;
}
exports.resolvePathDirectory = function resolvePathDirectory(curentDirName,fileName)
{
	return path.join(curentDirName,fileName);
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
	var filename=path.resolve(path.join(manifest.source_directory, "/", _filename));
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
	var source=path.resolve(path.join(manifest.source_directory, "/", fileName));
	var destination=path.resolve(path.join(manifest.treated_directory, "/", newFileName));
	fs_extra.moveSync(source,destination);
}
exports.moveFileToErrors=function moveFileToErrors(fileName)
{
	var source=path.resolve(path.join(manifest.source_directory, "/", fileName));
	var destination=path.resolve(path.join(manifest.errors_directory, "/", fileName));
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
	// parse the incoming request containing the form data
	form.parse(req);
	
}
