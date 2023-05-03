/**

 *  Hubitat Dashboard Layout Templates
 *  Author: John Stone (diysmarthomeguy)
 *  Date: 2023-04-27
 *
 *  Copyright 2023 John Stone / DIY Smart Home Guy
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  2023-05-02  0.004    More attempts to read and write JSON - can read only with dashboard token (not OAuth), and can't write yet.
 *  2023-04-29  0.003    Updated code to read users dashboard list - replaced strawman Switch stubs
 *  2023-04-28  0.002    strawman code to confirm layout and operations
 *  2023-04-27  0.001    New

NOTES:
    1) OAuth needs to be activated for this app to work.
        a) click OAuth
        b) accept the defaults
        c) click Update
        d) click close




**/

def doDebug() { return  [ "debug": 0, "info": 1,"trace": 1, "warn":1, "error":1, "test": 1 ] }

def appParams() {
    def func = "appParams"
    
	def params = [
        uri: "http://127.0.0.1:8080/installedapp/list",
		textParser: true,
		headers: [
			Cookie: state.cookie
		]
	  ]
    rv = doLog("debug", "$func", "1", "App Params", "$params")
    return params
}

definition(
    name: "Dashboard Layout Template",
    namespace: "diysmarthomeguy",
    author: "John Stone",
    importURL: "https://raw.githubusercontent.com/diysmarthomeguy/Hubitat/main/Dashboards/LayoutTemplate/dashboard-layout-template.groovy",
    description: "Manage a Layout Template",
    category: "My Apps",
    parent: "diysmarthomeguy:Dashboard Layout Template Manager",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
 
    getDashList()
               
    page(name: "mainPage", title: "Layout Template", install: true, uninstall: true,submitOnChange: true) {
        section() {
            paragraph "<OL><li>Select a Dashboard to use as a Layout Template.<li>Then select the Dashboard(s) to use the selected layout.<li>Press Done to apply the Template to the target Dashboard(s).<li>This will include all tile sizes and custom colors.</OL>"
        }
        section("Layout Template (Origin Dashboard)") {
            
            input "templateName", "string", title: "Enter a name for your Layout Template", submitOnChange: true, defaultValue: "Dashboard Layout Template"
            paragraph ""

            input "layoutTemplates", "enum", title: "Select Layout Dashboard", required:true, multiple:false, options: state.allDashNames
            
            input "dashAccessToken", "string", title: "Paste the Access Token for this Dashboard (temp workaround)", submitOnChange: true, defaultValue: "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
            
        }
        section("Destination Dashboards") {
            input "targetDashboards", "enum", title: "Select Target Dashboard", required:true, multiple:true, options: state.allDashNames
        }
        section() {
            input "applyButton", "button", title: "Apply"
           
        }
    }
}

private initAccessToken() {
    // modified from code by @bertabcd1234
	if (!state.accessToken) {
		try {
			def accessToken = createAccessToken()
			if (accessToken) {
                state.accessToken = accessToken
			}
		} 
		catch(e) {
			state.accessToken = null
		}
	}	
	return state.accessToken
}

private getHttp(params) {
    def func = "getHttp" as String
    def retult
    
    try {
        	httpGet(params) { resp ->
			result = resp.data.text.replaceAll("\n","").replaceAll("\r","")
		}

	} catch (e) {
		doLog("error", "$func", "-9001", "Error retrieving installed apps: ", "${e}")
        doLog("error", "$func", "-9002", "Exceptions: ", getExceptionMessageWithLine(e))

        //        log.error "Error retrieving installed apps: ${e}"
        //log.error(getExceptionMessageWithLine(e))
        result = null
	}
    
    return result
    
}


def getDashList() {        
    // Modified from code by gavincampbell
    def func = "getDashList()" as String
    def rv
    def allAppNames = []
    
    rv = doLog("debug", "$func", "1", "Fetching App List", "...")
    
    login() 
    
    def params = appParams()
    
    def matcherText = getHttp(params).replaceAll("\n","").replaceAll("\r","")
    
    def id = getDashAppId(matcherText)
    
    rv = doLog("debug", "$func", "5", "ID", "$id")
    
    def matcher = (matcherText =~ /(?m)<div class="grid-childArea childOf$id">.*?title="">(.*?)(\s?)<\/a>.*?<\/div>/)    
    
    matcher.find()
    def matches = matcher.iterator()
    Map results = [:]
    def int i = 0
                
    matches.each {
        allAppNames << it[1]
    }
                
    rv = doLog("debug", "$func", "6", "dashMatch", "$allAppNames")

    state.allDashNames = allAppNames.sort { a, b -> a.toLowerCase() <=> b.toLowerCase() }
    rv = doLog("debug", "$func", "20", "Dash Names", "$state.allDashNames")
    
    return
    
}

def getDashAppId(String appList) {
    def id = appList.find(/data-id="(\d+)">\s+Hubitat® Dashboard/) { match, id -> return id.trim() }
    return id
}

def getDashChildAppId(String appList, String dashName) {
    def func = "getDashChildAppId" as String
    def id = appList.find(/<a href="\/installedapp\/configure\/(\d+)"\sclass=""\s+title="">\s?$dashName/) { match, id -> return id.trim() }
    doLog("debug", "$func", "9001", "Child App Id", "$id")                     
                    
    return id
}

def login() {        
    // Modified from code by @dman2306
    def func = "login()" as String
    def rv
    
    doLog("debug", "$func", "1", "In login", "Checking Hub Security")
    

    state.cookie = ""
    if(hubSecurity) {
        try{
            httpPost(
                [
                    uri: "http://127.0.0.1:8080",
                    path: "/login",
                    query: 
                    [
                        loginRedirect: "/"
                    ],
                    body:
                    [
                        username: hubUsername,
                        password: hubPassword,
                        submit: "Login"
                    ],
                    textParser: true,
                    ignoreSSLIssues: true
                ]
            )
            { resp ->
                if (resp.data?.text?.contains("The login information you supplied was incorrect.")) {
                    rv = doLog("warn", "$func", "10", "Incorrect Login", "$hubUsername")
                } else {
                    state.cookie = resp?.headers?.'Set-Cookie'?.split(';')?.getAt(0)
                    rv = doLog("debug", "$func", "11", "Login Correct", "$hubUsername")
                }
            }
        } catch (e) {
            //log.error(getExceptionMessageWithLine(e))
            rv = doLog("error", "$func", "20", "Login Correct", "getExceptionMessageWithLine(e)")
        }
    } else {
        rv = doLog("debug", "$func", "30", "No Hub Security", "...")
    }
}

def appButtonHandler(buttonName) {
    def func = "appButtonHandler()" as String
    def rv
    initAccessToken()
    def oAuth_accessToken = "$state.accessToken"
    def dash_accessToken = "$dashAccessToken"
    
        doLog("debug", "$func", "9001", "Button Pressed", "$buttonName")
        doLog("test", "$func", "9002", "Access Token", "$state.accessToken")
        doLog("test", "$func", "9003", "Selected Dashboard Name", "${layoutTemplates}")

    def appList = getHttp(appParams()).replaceAll("\n","").replaceAll("\r","")
    
    def appId = getDashAppId(appList).toInteger()
        doLog("test", "$func", "9004", "App Id", "$appId")
    
	def childId = getDashChildAppId(appList, "${layoutTemplates}")
        doLog("test", "$func", "9005", "Child App Id", "$childId")
    
    def params = [
        //uri: "http://127.0.0.1:8080/apps/api/$appId/dashboard/$childId/layout?access_token=${oAuth_accessToken}",  // throws error Unauthorized
		//uri: "http://127.0.0.1:8080/apps/api/$childId/dashboard/0/layout?access_token=${oAuth_accessToken}",    // Dashboard token no longer valid! Either token was revoked or access removed.
        uri: "http://127.0.0.1:8080/apps/api/$childId/dashboard/0/layout?access_token=${dash_accessToken}",
        
        textParser: true,
		headers: [
			Cookie: state.cookie
		]
	  ]
    
        doLog("debug", "$func", "9010", "URI", "$params.uri")
    
    def dshJson = getHttp(params).replaceAll("\n","").replaceAll("\r","") as String
        doLog("test", "$func", "9020", "JSON", "$dshJson")
    
    // simple replace of the font size to test the write back.
    dshJson = dshJson.replace("fontSize\":12","fontSize\":14")
        doLog("test", "$func", "9021", "JSON", "$dshJson")
    
    
	if(security) cookie = getCookie()  //if using hub security
    
	Map requestParams = [
        //uri: "http://127.0.0.1:8080/apps/api/$id/dashboard/$childId/layout",
        uri: "http://127.0.0.1:8080/apps/api/$childId/dashboard/0/layout",
        
        headers: [
            //Authorization: "${dash_accessToken}",
            Authorization: "${oAuth_accessToken}",
            requestContentType: 'application/json',
		    contentType: 'application/json',
			"Cookie": cookie,
			body: "$dshJson"
        ]
	]


    try {
        asynchttpPost("postResp", requestParams)
        doLog("test","$func", "9110", "Yippee","")
    } catch (e) {
        doLog("test","$func", "-9100", "Bummer","")
    }
        
    //applyTemplate()
    
}


def postResp(params, data){  
    def func = "postResp"
    doLog("test","$func", "9001", "${params}","${data}")
}

String getCookie(){
    try{
  	  httpPost(
		[
		uri: "http://127.0.0.1:8080",
		path: "/login",
		query: [ loginRedirect: "/" ],
		body: [
			username: username,
			password: password,
			submit: "Login"
			]
		]
	  ) { resp -> 
		cookie = ((List)((String)resp?.headers?.'Set-Cookie')?.split(';'))?.getAt(0) 
        if(debugEnable)
            log.debug "$cookie"
	  }
    } catch (e){
        cookie = ""
    }
    return "$cookie"
}

def applyTemplate() {
    def func = "applyTemplate()" as String
    def origLayout = String
    def templateName = String
    def myJSON = new String[2]
    def targets = String[]
    def rv
    
    rv = doLog("debug","$func", "9001", "variables declared", "")
    rv = doLog("info","$func", "9002", "Dashboard Layout Template deploy activated","")
    
    // templateName = << get the template name from the input >>
    templateName = "Mobile Dashboard"
    
    rv = doLog("debug","$func", "2", "Template Name returned", "$templateName")
    
    origLayout = getJSON("$templateName") 
    
    rv = doLog("debug","$func", "3", "Original JSON Layout returned", "$origLayout")
    
    //myJSON = splitJSON(${origLayout})
    //rv = putJSON(myJSON)
    
    return 

}

def splitJSON(theJSON) {
    def jsonElements = string[2]
    def rv

    // regex to split the elements and toss the tiles

    return jsonElements
    
}

def getJSON(String theDashboard) {
    def func = "getJSON()" as String
    def result
    def rv
    
    rv = doLog("debug","$func", "1", "theDashboard", "$val")
    
    // some code to get the json from the selected dashboard
    
    result = "{\"name\": \"value\" }"
    
    return result
}

def putJSON(theJSON) {
    // def theDash = << get a list of target dashboards >>
    // theJSON is an array of the top and bottom of the JSON excluding the tiles
    def tileJSON
    def toWrite

    // loop through theDash
    //      get that JSON (to strip the tiles)
    //      tileJSON = /\"tiles\"\:\s+\[(.*)\]/
    //      toWrite = "${theJSON[0] \n ${tileJSON} \n ${theJSON[1]}
    //      toWrite = s/name": ".*?",/name": "${target}"/i // however this is done
    //      some code to write the json

    return
}

def installed() {
    log.debug "Installed with settings: ${settings}"
    initialize()
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    app.updateLabel(defaultLabel())
    initAccessToken()

}

def defaultLabel() {
    def String labelName = ""
   
    if (app.getLabel() == null) {
        labelName = "Dashboard Layout Template"
    } else {   
        return "${templateName}"
    }
    return labelName
}

def doLog (logType, funcName, seq, msg, msgVal) {
// rv = doLog("debug","$func", "1", "Message", "$val")
    def logger = "<b>$funcName</b> : $seq : <b>$msg</b> [$msgVal]"  
    def dbg = doDebug()
    
    
        switch("$logType") {
            case "info":
                if (dbg['info'] == 1) {
                    log.info "${logger}"
                    
                }
                break
            
            case "debug":
                if (dbg['debug'] == 1) {
                    log.debug "$logger"
                
                }
                break
            
            case "test":
                if (dbg['test'] == 1) {
                    log.debug "$logger"
                
                }
                break
            
            case "trace":
                log.trace "$logger"
 
                break
            case "warn":
                log.warn "$logger"
                break
            
            case "error":
                log.error "$logger"
                break
            
        }
    
    return 1
}






