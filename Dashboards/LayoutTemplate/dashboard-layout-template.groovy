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
 *  2023-04-29  0.003    Updated code to read users dashboard list - replaced strawman Switch stubs
 *  2023-04-28  0.002    strawman code to confirm layout and operations
 *  2023-04-27  0.001    New
**/

def doDebug() { return  [ "debug": 1, "info": 1,"trace": 1, "warn":1, "error":1 ] }



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
            
        }
        section("Destination Dashboards") {
            input "targetDashboards", "enum", title: "Select Target Dashboard", required:true, multiple:true, options: state.allDashNames
        }
        section() {
            input "applyButton", "button", title: "Apply"
           
        }
    }
}

def getDashList() {        
    // Modified from code by gavincampbell
    def func = "getDashList()" as String
    def rv
    rv = doLog("debug", "$func", "1", "Fetching App List", "...")
    
    login() 
    
	def params = [
		//uri: "http://127.0.0.1:8080/app/list",
        uri: "http://127.0.0.1:8080/installedapp/list",
		textParser: true,
		headers: [
			Cookie: state.cookie
		]
	  ]
	
    
	def allAppsList = []
    def allAppNames = []
	try {

        // MY CODE TO MAKE IT WORK
        	httpGet(params) { resp ->     
			def matcherText = resp.data.text.replaceAll("\n\r","").replace("\n","").replace("\r","")
            
            // find dashboard appId
            def id = matcherText.find(/data-id="(\d+)">\s+Hubitat® Dashboard/) { match, id -> return id.trim() }
            rv = doLog("debug", "$func", "5", "ID", "$id")
                
                def matcher = (matcherText =~ /(?m)<div class="grid-childArea childOf$id">.*?title="">(.*?)(\s?)<\/a>.*?<\/div>/)    
                    matcher.find()
                    def matches = matcher.iterator()
                    Map results = [:]
                    def int i = 0
                
                matches.each {
                    allAppNames << it[1]
                }
                
                rv = doLog("debug", "$func", "6", "dashMatch", "$results")
		}
        
        
	} catch (e) {
		log.error "Error retrieving installed apps: ${e}"
        log.error(getExceptionMessageWithLine(e))
	}
    

    state.allDashNames = allAppNames.sort { a, b -> a.toLowerCase() <=> b.toLowerCase() }
    rv = doLog("debug", "$func", "20", "Dash Names", "$state.allDashNames")
    
    return
    
}

def login() {        
    // Modified from code by @dman2306
    def func = "login()" as String
    def rv
    
    rv = doLog("debug", "$func", "1", "In login", "Checking Hub Security")
    

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
    
    rv = doLog("debug", "$func", "1", "Button Pressed", "$buttonName")
    
    rv = applyTemplate()
    
}


def applyTemplate() {
    def func = "applyTemplate()" as String
    def origLayout = String
    def templateName = String
    def myJSON = new String[2]
    def targets = String[]
    def rv
    
    rv = doLog("debug","$func", "1", "variables declared", "")
    rv = doLog("info","Dashboard Layout Template deploy activated", "","","")
    
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
    def logger = "$funcName $seq : $msg [$msgVal]"  
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






