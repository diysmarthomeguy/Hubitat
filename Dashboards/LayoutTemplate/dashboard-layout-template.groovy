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
 *  2023-04-28  0.02 strawman code to confirm layout and operations
 *  2023-04-27  0.01 New
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
    page(name: "mainPage", title: "Layout Template", install: true, uninstall: true,submitOnChange: true) {
        section() {
            paragraph "<OL><li>Select a Dashboard to use as a Layout Template.<li>Then select the Dashboard(s) to use the selected layout.<li>Press Done to apply the Template to the target Dashboard(s).<li>This will include all tile sizes and custom colors.</OL>"
        }
        section("Layout Template (Origin Dashboard)") {
            
            input "templateName", "string", title: "Enter a name for your Layout Template", submitOnChange: true, defaultValue: ""
            paragraph ""
            input "SwitchOrig", "capability.switch", title: "Select Dashboard to use as the Layout Template", submitOnChange: true, required: true, multiple: false
        }
        section("Destination Dashboards") {
            input "SwitchDest", "capability.switch", title: "Select the Dashboard(s) to update <i><b>from</b></i> the Template", submitOnChange: true, required: true, multiple: true
        }
        section() {
            input "applyButton", "button", title: "Apply"
           
        }
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
    def theback

}

def defaultLabel() {
    return "${templateName}"
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






