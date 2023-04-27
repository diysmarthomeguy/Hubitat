definition(
    name: "Dashboard Layout Template Tool",
    namespace: "diysmarthomeguy",
    author: "John Stone",
    description: "Create and Manage Layout Templates for Dashboards",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

preferences {
    page(name: "mainPage", title: "Layout Templates", install: true, uninstall: true,submitOnChange: true) {
        section {
            app(name: "association", appName: "Dashboard Template", namespace: "diysmarthomeguy", title: "Create New Template", multiple: true)
            }
        section {
            paragraph "This tool does important stuff"
            href(name: "hrefNotRequired",
             title: "blah",
             required: false,
             style: "external",
             url: "https://github.com/diysmarthomeguy/Hubitat/Dashboards/LayoutTemplate",
             description: "Tap to view information about this spink")
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
    log.debug "there are ${childApps.size()} child smartapps"
    childApps.each {child ->
        log.debug "child app: ${child.label}"
    }
}
