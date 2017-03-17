'use strict';
var webpack = require('webpack');
var path = require('path');
var colors = require('colors');
var fs = require("fs");
var btoa = require('btoa');
//var configInfo=require('./src/api');
//var constants = __webpack_require__("./node_modules/constants-browserify/constants.json");

const isDevBuild = process.argv[1].indexOf('webpack-dev-server') !== -1;
const dhisConfigPath = process.env.DHIS2_HOME && `${process.env.DHIS2_HOME}/config`;
let dhisConfig;
const manifest = ReadJSONFile("manifest.webapp");

try {
    dhisConfig = require(dhisConfigPath);
    console.log('\nLoaded DHIS config:');
} catch (e) {
    // Failed to load config file - use default config
    console.warn(`\nWARNING! Failed to load DHIS config:`, e.message);
    console.info('Using default config');
    dhisConfig = {
        //baseUrl: 'http://localhost:8080',
        baseUrl:manifest.activities.dhis.href+":"+manifest.activities.dhis.port,
        //authorization: 'Basic YWRtaW46ZGlzdHJpY3Q=', // admin:district
        authorization:  `Basic ${btoa(manifest.authentication)}`
    };
}
//console.log(JSON.stringify(dhisConfig, null, 2), '\n');

function log(req, res, opt) {
    req.headers.Authorization = dhisConfig.authorization;
    console.log('[PROXY]'.cyan.bold, req.method.green.bold, req.url.magenta, '=>'.dim, opt.target.dim);
}

const webpackConfig = {
    context: __dirname,
    contentBase: __dirname,
    entry: './src/app.js',
    devtool: 'source-map',
    output: {
        path: __dirname + '/build',
        filename: 'app.js',
        publicPath: manifest.activities.dhis.href+":"+manifest.activities.dhis.port,
    },
	//node: {fs: "empty"},
    module: {
        loaders: [
            {
                test: /\.jsx?$/,
                //test: /\.json?$/,
                exclude: /node_modules/,
                loader: 'babel',
                //loader:'json-loader',
                query: {
                    presets: ['es2015', 'stage-0', 'react'],
                },
                
            },
            {	
				test: /\.json$/,
				loader:'json-loader',
			},
        ],
      
    },
    resolve: {
		extensions: [ "", ".js", ".node",".json"],
        alias: {
            react: path.resolve('./node_modules/react'),
            'material-ui': path.resolve('./node_modules/material-ui'),
        },
    },
    devServer: {
        progress: true,
        colors: true,
        port: manifest.activities.app.port,
        inline: true,
        compress: true,
        proxy: [
            { path: '/api/*', target: dhisConfig.baseUrl, bypass: log },
            { path: '/dhis-web-commons/*', target: dhisConfig.baseUrl, bypass: log },
            { path: '/icons/*', target: dhisConfig.baseUrl, bypass: log },
            { path: '/jquery.min.js', target: 'http://localhost:'+manifest.activities.dhis.port+'/node_modules/jquery/dist', bypass: log },
            { path: '/polyfill.min.js', target: 'http://localhost:'+manifest.activities.dhis.port+'/node_modules/babel-polyfill/dist', bypass: log },
        ],
    },
};

if (!isDevBuild) {
    webpackConfig.plugins = [
        // Replace any occurance of process.env.NODE_ENV with the string 'production'
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': '"production"',
            DHIS_CONFIG: JSON.stringify({}),
        }),
        new webpack.optimize.DedupePlugin(),
        new webpack.optimize.OccurenceOrderPlugin(),
        new webpack.optimize.UglifyJsPlugin({
            //     compress: {
            //         warnings: false,
            //     },
            comments: false,
            beautify: true,
        }),
    ];
} else {
    webpackConfig.plugins = [
        new webpack.DefinePlugin({
            DHIS_CONFIG: JSON.stringify(dhisConfig)
        }),
    ];
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
	var filePath=path.resolve(path.join(parentDirectory, "/convert-dhi2-fhir-ssa/", fileName));
	//console.log(filePath);
	
	var contents = fs.readFileSync(filePath);
	var jsonContent = JSON.parse(contents);
	return jsonContent;
}

module.exports = webpackConfig;
