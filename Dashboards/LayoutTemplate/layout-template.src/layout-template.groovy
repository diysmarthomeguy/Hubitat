definition(
    name: "Layout Template",
    namespace: "diysmarthomeguy",
    author: "John Stone",
    description: "Manage a Layout Template",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")

    preferences {
    page(name: "mainPage", title: "Layout Template", install: true, uninstall: true,submitOnChange: true) {
        section {
            app(name: "association", appName: "Layout Template", namespace: "diysmarthomeguy", title: "Give teh template a name", multiple: true)
            }
    }
}