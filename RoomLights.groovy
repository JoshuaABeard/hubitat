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

        section("Motion:") {
            input "motionSensors", "capability.motionSensor", title: "Sensor(s)", required: true, multiple: true
            input "motionTimeout", "number", title: "timeout (Seconds)", description: "0...9999", required: true, defaultValue: 60
        }

        section("Lights (Day):") {
            input "dayLights", "capability.light", title: "Lights to Control", required: true, multiple: true
            input "dayLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "dayColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
        }

        section("Lights (Evening):") {
            input "eveningLights", "capability.light", title: "Lights to Control", required: true, multiple: true
            input "eveningLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "eveningColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
        }

        section("Lights (Night):") {
            input "nightLights", "capability.light", title: "Lights to Control", required: true, multiple: true
            input "nightLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "nightColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
        }

        section("Lights (Away):") {
            input "awayLights", "capability.light", title: "Lights to Control", required: true, multiple: true
            input "awayLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "awayColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
        }

        section("Warn when turning off Lights:") {
            input "warningDimBy", "number", title: "Warning Dim By (Level)", description: "0...99", required: true, defaultValue: 30
            input "warningTimeout", "number", title: "Warning Timeout (Seconds)", description: "0...9999", required: true, defaultValue: 10
        }

        section("Sleep mode:") {
            input "sleepSwitch", "capability.switch", title: "Sleep Switch", required: false, multiple: false
            input "sleepLights", "capability.light", title: "Lights to Control", required: true, multiple: true
            input "sleepLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "sleepColorTemperature", "number", title: "Color Temperature", description: "2000...65000", required: true, defaultValue: 2700
            input "nightTimeOnly", "bool", title: "Between Sunset and Sunrise", defaultValue: true
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
    state.currentMode = location.currentMode.name
    state.lightsMode = location.currentMode.name

    unschedule()
    unsubscribe()

    subscribe(motionSensors, "motion", motionHandler)
    subscribe(sleepSwitch, "switch", sleepHandler)

    subscribe(location, "sunrise", sunriseEventHandler)
    subscribe(location, "sunset", sunsetEventHandler)

    subscribe(location, "mode", modeHandler)
}

def modeHandler(evt)
{
    state.currentMode = location.currentMode.name
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
    if (sleepSwitch.currentValue("switch") == 'on') {
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
        unschedule(turnOffLights)
        unschedule(warnLights)
        state.lightsState = 'on'

        lights.on()
        lights.setColorTemperature(colorTemperature)
        lights.setLevel(level)
    }
    else if (state.lightsState == 'warn') {
        unschedule(turnOffLights)
        unschedule(warnLights)
        state.lightsState = 'on'

        lights.setLevel(state.preWarnLevel)
    }
}

def turnOffLights() {
    unschedule(turnOffLights)
    unschedule(warnLights)
    state.lightsState = 'off'
    
    lights.off()
}

def turnOnNightLights() {
    if (nightTimeOnly == false || (nightTimeOnly && isNightTime())) {
        sleepLights.on()
        sleepLights.setLevel(sleepLevel)
        sleepLights.setColorTemperature(sleepColorTemperature)

    }
}

def turnOffNightLights() {
    sleepLights.off()
}

def isNightTime() {
    def sunriseAndSunset = getSunriseAndSunset()

    return timeOfDayIsBetween(sunriseAndSunset.sunset, sunriseAndSunset.sunrise, new Date(), location.timeZone)
}

def getLightsForMode()
{
    switch (state.mode) {
        case "Day":    
            return ["lights": dayLights, "level": dayLevel, "colorTemperature": dayColorTemperature]
            break;
        case "Evening":
            return ["lights": eveningLights, "level": eveningLevel, "colorTemperature": eveningColorTemperature]
            break;
        case "Night":
            return ["lights": nightLights, "level": nightLevel, "colorTemperature": nightColorTemperature]
            break;
        case "Away":
            return ["lights": awayLights, "level": awayLevel, "colorTemperature": awayColorTemperature]
            break;
    }

}