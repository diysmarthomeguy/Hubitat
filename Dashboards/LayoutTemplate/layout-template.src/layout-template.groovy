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
 *  2023-04-27  New
**/



definition(
    name: "Dashboard Layout Template",
    namespace: "diysmarthomeguy",
    author: "John Stone",
    importURL: "https://raw.githubusercontent.com/diysmarthomeguy/Hubitat/main/Dashboards/LayoutTemplate/layout-template.src/layout-template.groovy",
    description: "Manage a Layout Template",
    category: "My Apps",
    parent: "diysmarthomeguy:Dashboard Layout Template Manager",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    page(name: "mainPage", title: "Layout Template", install: true, uninstall: true,submitOnChange: true) {

       section {
            /** app(name: "layout-template", appName: "Layout Template", namespace: "diysmarthomeguy", title: "Give teh template a name", multiple: true) **/
                  input "templateName", "string", title: "Enter a name for your Layout Template", submitOnChange: true, defaultValue: ""
        }
        
    }
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
    return "${templateName}"
}
