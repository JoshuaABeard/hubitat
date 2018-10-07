/**
 *  ****************  Room Lights  ****************
 *  10/3/2018 Starting version
 */

definition(
    name: "Room Lights Child",
    namespace: "jbeard",
    author: "Joshua Beard",
    description: "Manages Lights in a Room",
    category: "Convenience",
    version: "1.0.0",
    parent: "jbeard:Room Lights",
    iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
)


preferences {
    page(name: "mainPage")
    page(name: "lightsDayPage")
    page(name: "lightsEveningPage")
    page(name: "lightsNightPage")
    page(name: "lightsAwayPage")
    page(name: "sleepModePage")
}

def mainPage()
{
    dynamicPage(name: "mainPage", title: "", uninstall: true, install: true) {
        section() {
            paragraph "Manages Lights in a Room"
        }

        section("<b>App Instance:</b>") {
            label title: "Name", required: true
            input "disableApp", "bool", title: "Disable", defaultValue: false
        }

        section("<b>Motion:</b>") {
            input "motionSensors", "capability.motionSensor", title: "Sensor(s)", required: true, multiple: true
            input "motionTimeout", "number", title: "timeout (Seconds)", description: "0...9999", required: true, defaultValue: 60
            input "maxActivityLevel", "number", title: "Max Activity Level", description: "1...99", required: true, defaultValue: 1
        }

        section("<b>Warn Before Turning Off Lights:</b>") {
            input "warningDimBy", "number", title: "Dim By (Level)", description: "0...99", required: true, defaultValue: 30
            input "warningTimeout", "number", title: "Timeout (Seconds)", description: "0...9999", required: true, defaultValue: 10
        }

        section("") {
            href(name: "href", title: "<b>Lights (Day)</b>", required: false, page: "lightsDayPage")
            href(name: "href", title: "<b>Lights (Evening)</b>", required: false, page: "lightsEveningPage")
            href(name: "href", title: "<b>Lights (Night)</b>", required: false, page: "lightsNightPage")
            href(name: "href", title: "<b>Lights (Away)</b>", required: false, page: "lightsAwayPage")
            href(name: "href", title: "<b>Sleep Mode</b>", required: false, page: "sleepModePage")
        }
    }
}

def lightsDayPage()
{
    dynamicPage(name: "lightsDayPage", title: "<b>Lights (Day)</b>") {
        section("") {
            input "dayLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "dayLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "dayColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
        }
    }
}

def lightsEveningPage()
{
    dynamicPage(name: "lightsEveningPage", title: "<b>Lights (Evening)</b>") {
        section("") {
            input "eveningLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "eveningLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "eveningColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
        }
    }
}

def lightsNightPage()
{
    dynamicPage(name: "lightsNightPage", title: "<b>Lights (Night)</b>") {
        section("") {
            input "nightLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "nightLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "nightColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
        }
    }
}

def lightsAwayPage()
{
    dynamicPage(name: "lightsAwayPage", title: "<b>Lights (Away)</b>") {
        section("") {
            input "awayLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "awayLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "awayColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
        }
    }
}

def sleepModePage()
{
    dynamicPage(name: "sleepModePage", title: "<b>Sleep mode</b>") {
        section("") {
            input "sleepSwitch", "capability.switch", title: "Sleep Switch", required: false, multiple: false
            input "sleepTimeout", "number", title: "Timeout (hours)", description: "1...24", required: true, defaultValue: 15
            input "sleepLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "sleepLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "sleepColorTemperature", "number", title: "Color Temperature", description: "2000...65000", required: true, defaultValue: 2700
            input "sleepLightsByMotion", "bool", title: "By Motion", defaultValue: true
            input "nightTimeOnly", "bool", title: "Between Sunset and Sunrise", defaultValue: false
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
    state.activeLightMode = location.currentMode.name
    state.activityLevel = 1
    state.nightLightsState = 'off'

    unschedule()
    unsubscribe()

    if (disableApp) {
        subscribe(motionSensors, "motion", motionHandler)

        if (sleepSwitch) {
            subscribe(sleepSwitch, "switch", sleepHandler)
        }

        subscribe(location, "sunrise", sunriseHandler)
        subscribe(location, "sunset", sunsetHandler)
        subscribe(location, "mode", modeHandler)
    }
}

def modeHandler(evt)
{
    state.currentMode = location.currentMode.name

    turnOffLights(state.activeLightMode)

    motionMonitor()
}

def sunriseHandler(evt) {
    if (state.lightsState == 'sleeping' && sleepLightsByMotion == false) {
        turnOffNightLights()
    }
}

def sunsetHandler(evt) {
    if (state.lightsState == 'sleeping' && sleepLightsByMotion == false) {
        turnOnNightLights()
    }
}

def sleepHandler(evt) {
    if (sleepSwitch.currentValue("switch") == 'on') {
        turnOnSleep()
    }
    else {
        turnOffSleep()
    }
}

def turnOnSleep() {
    if (state.lightsState != 'sleeping') {
        turnOffLights(state.activeLightMode)
        state.lightsState = 'sleeping'
        turnOnNightLights()

        if (sleepTimeout != 0)
        {
            runIn(sleepTimeout, turnOffSleep)
        }

        if (sleepSwitch.currentValue("switch") == 'off') {
                sleepSwitch.on()
        }
    }
}

def turnOffSleep() {
        unschedule(turnOffSleep)
        if (state.lightsState == 'sleeping') {
            state.lightsState = 'off'
            turnOffNightLights()

            motionMonitor()

            if (sleepSwitch.currentValue("switch") == 'on') {
                sleepSwitch.off()
            }
        }
}

def motionHandler(evt) {
    if(evt.value == 'active')
    {
        state.motionActive = true
    }
    else {
        // Check if any device is active if so stay active
        def activeDevices = motionSensors.findAll { it?.currentValue("motion") == 'active' }
        if (activeDevices || evt.value == 'active') {
            state.motionActive = true
        }
        else {
            state.motionActive = false
        }
    }

    if (state.lightsState != 'sleeping') {
        motionMonitor()
    }
    else if (sleepLightsByMotion) {
        sleepMotionMonitor()
    }
}

def sleepMotionMonitor() {
    if (state.motionActive) {
        turnOnNightLights()
    }   
    else {
        runIn(motionTimeout, turnOffNightLights)
    }
}

def motionMonitor() {
    if (state.motionActive) {
        turnOnLights(state.currentMode)
    }
    else
    {
        if (state.lightsState != 'off')
        {
            // consider activity level
            def timeout = motionTimeout*state.activityLevel
            if (warningTimeout == 0) {
                runIn(timeout, turnOffLights)
            }
            else {
                runIn(timeout, warnLights)
            }
        }
    }
}

def warnLights(mode) {
    if(mode == null)
    {
        mode = state.activeLightMode
    }
    def modeLights = getLightsForMode(mode)
    def lights = modeLights.devices
    if (lights == null) {
        return
    }

    if (state.lightsState == 'on')
    {
        state.lightsState = 'warn'

        for(light in lights) {
            def level = light.currentValue("level")
            def newLevel = level - warningDimBy
            if (newLevel < 0) {
                newLevel = 1
            }

            // Bug here, we only grab the last bulbs level for restore
            state.preWarnLevel = level

            if (light.hasCommand("setLevel")) {
                light.setLevel(newLevel)
            }
        }

        runIn(warningTimeout, turnOffLights)
    }
}

def turnOnLights(mode) {
    if(mode == null)
    {
        mode = state.activeLightMode
    }

    def modeLights = getLightsForMode(mode)
    def lights = modeLights.devices
    if (lights == null) {
        return
    }

    unschedule(turnOffLights)
    unschedule(warnLights)

    if (state.lightsState == 'off' || (state.lightsState == 'on' && state.activeLightMode != mode)) {
        state.lightsState = 'on'
        state.activeLightMode = mode

        lights.on()

        setLightsLevel(lights, modeLights.level)

        setLightsColorTemperature(lights, modeLights.colorTemperature)
    }
    else if (state.lightsState == 'warn') {
        state.lightsState = 'on'
        
        def newActivityLevel = state.activityLevel + 1
        if(newActivityLevel > maxActivityLevel) {
            newActivityLevel = maxActivityLevel
        }
        state.activityLevel = newActivityLevel


        setLightsLevel(lights, state.preWarnLevel)
    }
}

def turnOffLights(mode) {
    if(mode == null)
    {
        mode = state.activeLightMode
    }

    def modeLights = getLightsForMode(mode)
    def lights = modeLights.devices
    if (lights == null) {
        return
    }

    unschedule(turnOffLights)
    unschedule(warnLights)
    state.activityLevel = 1
    state.lightsState = 'off'

    lights.off()
}

def turnOnNightLights() {  
    unschedule(turnOffNightLights)

    if (nightLights == null) {
        return
    }

    if (state.nightLightsState == 'off') {
        if (nightTimeOnly == false || (nightTimeOnly && isNightTime())) {
            state.nightLightsState = 'on'

            sleepLights.on()
            setLightsLevel(sleepLights, sleepLevel)
            setLightsColorTemperature(sleepLights, sleepColorTemperature)
        }
    }
}

def turnOffNightLights() {
    unschedule(turnOffNightLights)

    if (sleepLights && state.nightLightsState == 'on')
    {
        state.nightLightsState = 'off'
        sleepLights.off()
    }
}

def isNightTime() {
    def sunriseAndSunset = getSunriseAndSunset()

    return timeOfDayIsBetween(sunriseAndSunset.sunset, sunriseAndSunset.sunrise, new Date(), location.timeZone)
}

def getLightsForMode(mode)
{
    switch (mode) {
        case "Day":
            return ["name": "Day", "devices": dayLights, "level": dayLevel, "colorTemperature": dayColorTemperature]
            break;
        case "Evening":
            return ["name": "Evening", "devices": eveningLights, "level": eveningLevel, "colorTemperature": eveningColorTemperature]
            break;
        case "Night":
            return ["name": "Night", "devices": nightLights, "level": nightLevel, "colorTemperature": nightColorTemperature]
            break;
        case "Away":
            return ["name": "Away", "devices": awayLights, "level": awayLevel, "colorTemperature": awayColorTemperature]
            break;
    }
}

def setLightsLevel(devices, newLevel) {
    for(device in devices) {
        if (device.hasCommand("setLevel")) {
            device.setLevel(newLevel)
        }
    }
}

def setLightsColorTemperature(devices, colorTemperature) {
    for(device in devices) {
        if (device.hasCommand("setColorTemperature")) {
            device.setColorTemperature(colorTemperature)
        }
    }
}