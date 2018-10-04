/**
 *  ****************  Intrusion Alert  ****************
 *  10/3/2018 Starting version
 */

definition(
    name: "Intrusion Alert",
    namespace: "jbeard",
    author: "Joshua Beard",
    description: "This was designed to announce if any contacts are open or becomes opened when a switch turns on",
    category: "",

    iconUrl: "",
    iconX2Url: "",
    iconX3Url: "",
)


preferences {

    section()
    {
        paragraph "This was designed to announce if any contacts are open or becomes opened when a switch turns on"
    }

    section()
    {
        input "enableSwitch", "capability.switch", title: "Select Enable Switch", required: true, multiple: false
    }

    section()
    {
        input "sensors", "capability.contactSensor", title: "Contact Sensors to check", required: true, multiple: true
    }

    section()
    {
        input "notification", "capability.speechSynthesis", title: "Choose notification device(s)", required: true, multiple: true
    }

    section()
    {
        input "repeatDelay", "number", title: "Repeat delay before rechecking (Seconds - enter 0 to disable)", description: "0...9999", required: true
    }
}

def installed() {
    log.info "Initialised with settings: ${settings}"

    subscribe(enableSwitch, "switch", notificationHandler)
    subscribe(sensors, "contact", notificationHandler)
}

def notificationHandler(evt) {
    unschedule()

    if(enableSwitch.currentValue("switch") == 'on')
    {
        def openDevices = sensors.findAll { it?.latestValue("contact") == 'open' }
        for(device in openDevices) 
        {
            def newmsg = "${device} is open"
            notification.speak(newmsg)
        }

        if(openDevices && repeatDelay != 0)
        {
            runIn(repeatDelay,notificationHandler)
        }
    }
}