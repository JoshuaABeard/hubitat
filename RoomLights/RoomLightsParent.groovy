/**
 *  ****************  Room Lights  ****************
 *  10/3/2018 Starting version
 */

definition(
    name: "Room Lights",
    namespace: "jbeard",
    author: "Joshua Beard",
    description: "Manages Lights in a Room",
    category: "Convenience",
    singleInstance: true,
    version: "1.0.0",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
)


preferences {
    page (name: "mainPage", title: "", install: true, uninstall: true)
    {
        section (""){
		    app(name: "Room Instances:", appName: "Room Lights Child", namespace: "jbeard", title: "<b>Add a new 'Room Lights' instance</b>", multiple: true)
        }
    }  
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {

}