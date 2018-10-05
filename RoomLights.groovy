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
    page(name: "mainPage", title: "", uninstall: true, install: true) {
        section() {
            paragraph "Manages Lights in a Room"
        }

        section("Lights:") {
            input "lights", "capability.light", title: "Lights to Control", required: true, multiple: true
            input "level", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "colorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "warningDimBy", "number", title: "Warning Dim By (Level)", description: "0...99", required: true, defaultValue: 30
            input "warningTimeout", "number", title: "Warning Timeout (Seconds)", description: "0...9999", required: true, defaultValue: 10
        }

        section("Motion:") {
            input "motionSensors", "capability.motionSensor", title: "Sensor(s)", required: true, multiple: true
            input "motionTimeout", "number", title: "timeout (Seconds)", description: "0...9999", required: true, defaultValue: 60
        }

        section("Night Lights:") {
            input "nightLights", "capability.light", title: "Lights to Control", required: true, multiple: true
            input "nightLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "nightColorTemperature", "number", title: "Color Temperature", description: "2000...65000", required: true, defaultValue: 2700
             input "nightTimeOnly", "bool", title: "Between Sunset and Sunrise", defaultValue: true
        }

        section("Sleep Mode:") {
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

    state.lightsState = 'off' // 'off', 'on', 'warn', 'sleeping'
    state.preWarnLevel = 100
    state.motionActive = false

    unschedule()
    unsubscribe()

    subscribe(motionSensors, "motion", motionHandler)
    subscribe(sleepSwitch, "switch", sleepHandler)

    subscribe(location, "sunrise", sunriseEventHandler)
    subscribe(location, "sunset", sunsetEventHandler)
}

def sunriseEventHandler(evt) {
    if (state.lightsState == 'sleeping') {
        turnOffNightLights()
    }
}

def sunsetEventHandler(evt) {
    if (state.lightsState == 'sleeping') {
        turnOnNightLights()
    }
}

def sleepHandler(evt) {
    if (evt.currentValue("switch") == 'on') {
        turnOffLights()
        state.lightsState = 'sleeping'

        turnOnNightLights()
    }
    else {
        state.lightsState = 'off'
        turnOffNightLights()
        motionMonitor()
    }
}

def motionHandler(evt) {
    def activeDevices = motionSensors.findAll { it?.currentValue("motion") == 'active' }
    if (activeDevices) {
        state.motionActive = true
    }
    else {
        state.motionActive = false
    }

    if (state.lightsState != 'sleeping') {
        motionMonitor()
    }
}

def motionMonitor() {
    if (state.motionActive) {
        turnOnLights()
    }
    else
    {
        if (state.lightsState != 'off')
        {
            if (warningTimeout == 0) {
                runIn(motionTimeout, turnOffLights)
            }
            else {
                runIn(motionTimeout, warnLights)
            }
        }
    }
}

def warnLights() {
    if (state.lightsState == 'on')
    {
        state.lightsState = 'warn'

        for(light in lights) {
            def level = light.currentValue("level")
            def newLevel = level - warningDimBy
            if (newLevel < 0) {
                newLevel = 1
            }

            state.preWarnLevel = level
            light.setLevel(newLevel)
        }

        runIn(warningTimeout, turnOffLights)
    }
}

def turnOnLights() {
    if (state.lightsState == 'off') {
        unsubscribe(turnOffLights)
        unsubscribe(warnLights)
        state.lightsState = 'on'

        lights.on()
        lights.setColorTemperature(colorTemperature)
        lights.setLevel(level)
    }
    else if (state.lightsState == 'warn') {
        unsubscribe(turnOffLights)
        unsubscribe(warnLights)
        state.lightsState = 'on'

        lights.setLevel(state.preWarnLevel)
    }
}

def turnOffLights() {
    unsubscribe(turnOffLights)
    unsubscribe(warnLights)
    state.lightsState = 'off'
    
    lights.off()
}

def turnOnNightLights() {
    if (nightTimeOnly == false || (nightTimeOnly && isNightTime())) {
        nightLights.on()
        nightLights.setColorTemperature(nightColorTemperature)
        nightLights.setLevel(nightLevel)
    }
}

def turnOffNightLights() {
    nightLights.off()
}

def isNightTime() {
    def sunriseAndSunset = getSunriseAndSunset()

    def between = timeOfDayIsBetween(sunriseAndSunset.sunset, sunriseAndSunset.sunrise, new Date(), location.timeZone)
    if (between) {
        return true
    } else {
        return false
    }
}