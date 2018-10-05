/**
 *  ****************  Room Lights Manager  ****************
 *  10/3/2018 Starting version
 */

definition(
    name: "Room Lights Manager",
    namespace: "jbeard",
    author: "Joshua Beard",
    description: "Manages Lights in a Room",
    category: "Convenience",
    version: "1.0.0",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
)


preferences {
    page(name: "mainPage", title: "", uninstall: true, install: true)
    {
        section()
        {
            paragraph "Manages Lights in a Room"
        }

        section()
        {
            input "lights", "capability.light", title: "Lights to Control", required: true, multiple: true
        }

        section()
        {
            input "motionSensors", "capability.motionSensor", title: "Motion Sensor(s)", required: true, multiple: true
            input "timeout", "number", title: "Turn Lights Off After (Seconds)", description: "0...9999", required: true, defaultValue: 60
        }

        section()
        {
            input "sleepSwitch", "capability.switch", title: "Sleep Switch", required: false, multiple: false
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

    state.lightsOn = false

    unschedule()
    unsubscribe()

    subscribe(motionSensors, "motion", motionHandler)
    //subscribe(openSensors, "contact.open", openNotificationHandler)

    //subscribe(closeSensors, "contact.closed", closeNotificationHandler)

    //checkOpenSensors()
}

def motionHandler(evt) {
    if (state.lightsOn == false)
    {
        lights.on()
        state.lightsOn = true
    }

    runIn(timeout, turnOffLights)
}

def turnOffLights()
{
    lights.off()
    state.lightsOn = false
}

// def openNotificationHandler(evt) {
//     if(enableSwitch.currentValue("switch") == 'on')
//     {
//         def newmsg = "${evt.displayName} is open"
//         notification.speak(newmsg)
    
//         if(repeatDelay != 0)
//         {
//             runIn(repeatDelay, checkOpenSensors)
//         }
//     }
// }

// def closeNotificationHandler(evt) {
//     if(enableSwitch.currentValue("switch") == 'on')
//     {
//         def newmsg = "${evt.displayName} is closed"
//         notification.speak(newmsg)
//     }
// }

// def checkOpenSensors()
// {
//     if(enableSwitch.currentValue("switch") == 'on')
//     {
//         def openDevices = openSensors.findAll { it?.currentValue("contact") == 'open' }
//         for(device in openDevices) 
//         {
//             def newmsg = "${device} is open"
//             notification.speak(newmsg)
//         }

//         if(openDevices && repeatDelay != 0)
//         {
//             runIn(repeatDelay, checkOpenSensors)
//         }
//     }
// }