/**
 *  ****************  Intrusion Alert  ****************
 *  10/3/2018 Starting version
 */

definition(
    name: "Door and Window Alerts",
    namespace: "jbeard",
    author: "Joshua Beard",
    description: "This was designed to announce if any contacts are open while a switch is on",
    category: "Convenience",
    version: "1.1.0",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
)


preferences {
    page(name: "mainPage", title: "", uninstall: true, install: true)
    {
        section()
        {
            paragraph "This was designed to announce if any contacts are open while a switch is on"
        }

        section()
        {
            input "enableSwitch", "capability.switch", title: "Select Control Switch", required: true, multiple: false
        }

        section()
        {
            input "openSensors", "capability.contactSensor", title: "Contact Sensor(s) to Notify on Open", required: true, multiple: true
            input "repeatDelay", "number", title: "Re-Check Delay (Seconds - 0 to Disable)", description: "0...9999", required: true, defaultValue: 0
        }

        section()
        {
            input "closeSensors", "capability.contactSensor", title: "Contact Sensor(s) to Notify on Close", required: false, multiple: true
        }

        section()
        {
            input "notification", "capability.speechSynthesis", title: "Choose Notification Device(s)", required: true, multiple: true
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
    log.info "Initialised with settings: ${settings}"

    unschedule()
    unsubscribe()

    subscribe(enableSwitch, "switch", notificationHandler)
    subscribe(openSensors, "contact.open", openNotificationHandler)

    subscribe(closeSensors, "contact.closed", closeNotificationHandler)

    checkOpenSensors()
}

def notificationHandler(evt) {
    unschedule(checkOpenSensors)
    checkOpenSensors()
}

def openNotificationHandler(evt) {
    if(enableSwitch.currentValue("switch") == 'on')
    {
        def newmsg = "${evt.displayName} is open"
        notification.speak(newmsg)
    
        if(repeatDelay != 0)
        {
            runIn(repeatDelay, checkOpenSensors)
        }
    }
}

def closeNotificationHandler(evt) {
    if(enableSwitch.currentValue("switch") == 'on')
    {
        def newmsg = "${evt.displayName} is closed"
        notification.speak(newmsg)
    }
}

def checkOpenSensors()
{
    if(enableSwitch.currentValue("switch") == 'on')
    {
        def openDevices = openSensors.findAll { it?.currentValue("contact") == 'open' }
        for(device in openDevices) 
        {
            def newmsg = "${device} is open"
            notification.speak(newmsg)
        }

        if(openDevices && repeatDelay != 0)
        {
            runIn(repeatDelay, checkOpenSensors)
        }
    }
}