/**
 *  ****************  Room Lights  ****************
 *  10/3/2018 Starting version
 */

import groovy.transform.Field

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
    page(name: "dayModePage")
    page(name: "eveningModePage")
    page(name: "nightModePage")
    page(name: "awayModePage")
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
        }

        section("<b>Motion:</b>") {
            input "motionSensors", "capability.motionSensor", title: "Sensor(s)", required: true, multiple: true
            input "motionTimeout", "number", title: "timeout (Seconds)", description: "0...9999", required: true, defaultValue: 60
        }

        section("<b>Warn Before Turning Off Lights:</b>") {
            input "warningDimBy", "number", title: "Dim By (Level)", description: "0...99", required: true, defaultValue: 30
            input "warningTimeout", "number", title: "Timeout (Seconds)", description: "0...9999", required: true, defaultValue: 10
        }

        section("<b>Modes</b>") {
            href(name: "href", title: "<b>Day Mode</b>", required: false, page: "dayModePage")
            href(name: "href", title: "<b>Evening Mode</b>", required: false, page: "eveningModePage")
            href(name: "href", title: "<b>Night Mode</b>", required: false, page: "nightModePage")
            href(name: "href", title: "<b>Away Mode</b>", required: false, page: "awayModePage")
            href(name: "href", title: "<b>Sleep Mode</b>", required: false, page: "sleepModePage")
        }
    }
}

def dayModePage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "dayModePage", title: "<b>Day Mode</b>") {
        section("<b>Lights</b>") {
            input "dayLights", "capability.light", title: "Lights to Control", required: false, multiple: true
        }
        
        section("<b>Light Settings</b>") {
            input "dayLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "dayColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "dayColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList
        }

        section("<b>Mode Settings</b>") {
            input "dayMaxActivityLevel", "number", title: "Max Activity Level", description: "1...99", required: true, defaultValue: 1
            input "daybyMotion", "bool", title: "By Motion", defaultValue: true
            input "dayNightTimeOnly", "bool", title: "Between Sunset and Sunrise", defaultValue: false
            input "dayTimeout", "number", title: "Timeout (Minutes)", description: "1...1440", required: true, defaultValue: 0
        }
    }
}

def eveningModePage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "eveningModePage", title: "<b>Evening Mode</b>") {
        section("<b>Lights</b>") {
            input "eveningLights", "capability.light", title: "Lights to Control", required: false, multiple: true
        }

        section("<b>Light Settings</b>") {
            input "eveningLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "eveningColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "eveningColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList
        }

        section("<b>Mode Settings</b>") {
            input "eveningMaxActivityLevel", "number", title: "Max Activity Level", description: "1...99", required: true, defaultValue: 1
            input "eveningbyMotion", "bool", title: "By Motion", defaultValue: true
            input "eveningNightTimeOnly", "bool", title: "Between Sunset and Sunrise", defaultValue: false
            input "eveningTimeout", "number", title: "Timeout (Minutes)", description: "1...1440", required: true, defaultValue: 0
        }
    }
}

def nightModePage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "nightModePage", title: "<b>Night Mode</b>") {
        section("<b>Lights</b>") {
            input "nightLights", "capability.light", title: "Lights to Control", required: false, multiple: true
        }

        section("<b>Light Settings</b>") {
            input "nightLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "nightColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "nightColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList 
        }

        section("<b>Mode Settings</b>") {
            input "nightMaxActivityLevel", "number", title: "Max Activity Level", description: "1...99", required: true, defaultValue: 1
            input "nightbyMotion", "bool", title: "By Motion", defaultValue: true
            input "nightNightTimeOnly", "bool", title: "Between Sunset and Sunrise", defaultValue: false
            input "nightTimeout", "number", title: "Timeout (Minutes)", description: "1...1440", required: true, defaultValue: 0
        }
    }
}

def awayModePage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "awayModePage", title: "<b>Away Mode</b>") {
        section("<b>Lights</b>") {
            input "awayLights", "capability.light", title: "Lights to Control", required: false, multiple: true
        }

        section("<b>Light Settings</b>") {
            input "awayLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "awayColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "awayColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList   
        }

        section("<b>Mode Settings</b>") { 
            input "awayMaxActivityLevel", "number", title: "Max Activity Level", description: "1...99", required: true, defaultValue: 1        
            input "awaybyMotion", "bool", title: "By Motion", defaultValue: true
            input "awayNightTimeOnly", "bool", title: "Between Sunset and Sunrise", defaultValue: false
            input "awayTimeout", "number", title: "Timeout (Minutes)", description: "1...1440", required: true, defaultValue: 0
        }
    }
}

def sleepModePage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "sleepModePage", title: "<b>Sleep mode</b>") {
        section("<b>Switch</b>") {
            input "sleepSwitch", "capability.switch", title: "Sleep Switch", required: false, multiple: false
            input "sleepSwitchTimeout", "number", title: "Switch Timeout (Minutes)", description: "1...1440", required: true, defaultValue: 900
        }

        section("<b>Lights</b>") {
            input "sleepLights", "capability.light", title: "Lights to Control", required: false, multiple: true
        }

        section("<b>Light Settings</b>") {
            input "sleepLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "sleepColorTemperature", "number", title: "Color Temperature", description: "2000...65000", required: true, defaultValue: 2700
            input "sleepColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList
        }

        section("<b>Mode Settings</b>") {
            input "sleepMaxActivityLevel", "number", title: "Max Activity Level", description: "1...99", required: true, defaultValue: 1
            input "sleepbyMotion", "bool", title: "By Motion", defaultValue: true
            input "sleepNightTimeOnly", "bool", title: "Between Sunset and Sunrise", defaultValue: false
            input "sleepTimeout", "number", title: "Timeout (Minutes)", description: "1...1440", required: true, defaultValue: 0
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

    state.lightsState = 'off' // 'off', 'on', 'warn'
    state.preWarnLevel = 100
    state.motionActive = motionSensors.findAll { it?.currentValue("motion") == 'active' } ? true : false
    state.activeLightMode = 'away'
    state.activityLevel = 1

    subscribe(motionSensors, "motion", motionHandler)

    if (sleepSwitch) {
        subscribe(sleepSwitch, "switch", sleepHandler)
    }

    subscribe(location, "sunrise", sunriseHandler)
    subscribe(location, "sunset", sunsetHandler)
    subscribe(location, "mode", modeHandler)
    
    updateActiveModeIfChanged()
}

def modeHandler(evt)
{
    updateActiveModeIfChanged()
}

def sleepHandler(evt) {
    updateActiveModeIfChanged()

    if (sleepSwitch.currentValue("switch") == 'on') {
        if (sleepSwitchTimeout != 0) {
            runIn(sleepSwitchTimeout*60, turnOffSleepSwitch)
        }
    }
    else
    {
        unschedule(turnOffSleepSwitch)
    }
}

def turnOffSleepSwitch() {
    sleepSwitch.off()
}

def sunriseHandler(evt) {
    def mode = state.activeLightMode
    def modeSettings = getSettingsForMode(mode)
    if (!modeSettings.byMotion && modeSettings.nightTimeOnly) {
        turnOffLights(mode)
    }
}

def sunsetHandler(evt) {
    def mode = state.activeLightMode
    def modeSettings = getSettingsForMode(mode)
    if (!modeSettings.byMotion && modeSettings.nightTimeOnly) {
        turnOnLights(mode)
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

    updateBasedOnMotion()
}

def getCurrentMode() {
    if (sleepSwitch && sleepSwitch.currentValue("switch") == 'on') {
        return "sleep"
    }

    return location.currentMode.name
}

def updateActiveModeIfChanged() {
    def newMode = getCurrentMode()
    if(state.activeLightMode != newMode) {
        turnOffLights(state.activeLightMode)
        state.activeLightMode = newMode

        def modeSettings = getSettingsForMode(newMode)
        if (modeSettings.byMotion) {
            updateBasedOnMotion(newMode)
        }
        else if (!modeSettings.nightTimeOnly || (modeSettings.nightTimeOnly && isNightTime()))
            turnOnLights(newMode)

            if (modeSettings.timeout != 0) {
                runIn(modeSettings.timeout*60, turnOffLights)
            }
        }
    }
}

def updateBasedOnMotion(mode) {
    def modeSettings = getSettingsForMode(mode)
    if (!modeSettings.byMotion) {
        return
    }

    if (state.motionActive) {
        if (state.lightsState == 'off') {
            turnOnLights(state.activeLightMode)
        }
    }
    else
    {
        if (state.lightsState != 'off')
        {
            unschedule(turnOffLights)
            unschedule(warnLights)

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
    def modeSettings = getSettingsForMode(mode)
    def lights = modeSettings.devices
    if (lights == null) {
        return
    }

    unschedule(turnOffLights)
    unschedule(warnLights)

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

def turnOnLights(mode) {
    def modeSettings = getSettingsForMode(mode)
    def lights = modeSettings.devices
    if (lights == null) {
        return
    }

    unschedule(turnOffLights)
    unschedule(warnLights)

    if (state.lightsState == 'warn') {
        state.lightsState = 'on'

        // Increase activity level
        def newActivityLevel = state.activityLevel + 1
        if(newActivityLevel > modeSettings.maxActivityLevel) {
            newActivityLevel = modeSettings.maxActivityLevel
        }
        state.activityLevel = newActivityLevel

        setLevel(lights, state.preWarnLevel)
    }
    else {
        state.lightsState = 'on'

        lights.on()

        setLevel(lights, modeSettings.level)

        if(modeSettings.color) {
            setColor(lights, modeSettings.color)
        }
        else {
            setColorTemperature(lights, modeSettings.colorTemperature)
        }
    }
}

def turnOffLights(mode) {
    def modeSettings = getSettingsForMode(mode)
    def lights = modeSettings.devices
    if (lights == null) {
        return
    }

    unschedule(turnOffLights)
    unschedule(warnLights)

    state.activityLevel = 1
    state.lightsState = 'off'

    lights.off()
}

def isNightTime() {
    def sunriseAndSunset = getSunriseAndSunset()

    return timeOfDayIsBetween(sunriseAndSunset.sunset, sunriseAndSunset.sunrise, new Date(), location.timeZone)
}

def getSettingsForMode(mode)
{
    if(mode == null)
    {
        mode = state.activeLightMode
    }

    switch (mode) {
        case "Day":
            return ["name": "Day", "devices": dayLights, "level": dayLevel, "colorTemperature": dayColorTemperature, "color": dayColor, "byMotion": daybyMotion, "nightTimeOnly": dayNightTimeOnly, "maxActivityLevel": dayMaxActivityLevel, "timeout": dayTimeout]
            break;
        case "Evening":
            return ["name": "Evening", "devices": eveningLights, "level": eveningLevel, "colorTemperature": eveningColorTemperature, "color": eveningColor, "byMotion": eveningbyMotion, "nightTimeOnly": eveningNightTimeOnly, "maxActivityLevel": eveningMaxActivityLevel, "timeout": eveningTimeout]
            break;
        case "Night":
            return ["name": "Night", "devices": nightLights, "level": nightLevel, "colorTemperature": nightColorTemperature, "color": nightColor, "byMotion": nightbyMotion, "nightTimeOnly": nightNightTimeOnly, "maxActivityLevel": nightMaxActivityLevel, "timeout": nightTimeout]
            break;
        case "Away":
            return ["name": "Away", "devices": awayLights, "level": awayLevel, "colorTemperature": awayColorTemperature, "color": awayColor, "byMotion": awaybyMotion, "nightTimeOnly": awayNightTimeOnly, "maxActivityLevel": awayMaxActivityLevel, "timeout": awayTimeout]
            break;
        case "Sleep":
            return ["name": "Sleep", "devices": sleepLights, "level": sleepLevel, "colorTemperature": sleepColorTemperature, "color": sleepColor, "byMotion": sleepbyMotion, "nightTimeOnly": sleepNightTimeOnly, "maxActivityLevel": sleepMaxActivityLevel, "timeout": sleepTimeout]
            break;
    }
}

def setLevel(devices, newLevel) {
    for(device in devices) {
        if (device.hasCommand("setLevel")) {
            device.setLevel(newLevel)
        }
    }
}

def setColorTemperature(devices, colorTemperature) {
    for(device in devices) {
        if (device.hasCommand("setColorTemperature")) {
            device.setColorTemperature(colorTemperature)
        }
    }
}

def setColor(devices, colorName) {
    def color = convertRGBToHueSaturation(colorsRGB[colorName][1])

    for(device in devices) {
        if (device.hasCommand("setColor")) {
            device.setColor(color)
        }
    }
}

//------------------------------------------------------------------------------------------------------------------------//
private convertRGBToHueSaturation(setColorTo)	{
	def str = setColorTo.replaceAll("\\s","").toLowerCase()
	def rgb = (colorsRGB[str][0] ?: colorsRGB['white'][0])

	float r = rgb[0] / 255
	float g = rgb[1] / 255
	float b = rgb[2] / 255
	float max = Math.max(Math.max(r, g), b)
	float min = Math.min(Math.min(r, g), b)
	float h, s, l = (max + min) / 2

	if (max == min)
		h = s = 0 // achromatic
	else    {
		float d = max - min
		s = l > 0.5 ? d / (2 - max - min) : d / (max + min)
		switch (max)	{
			case r:    h = (g - b) / d + (g < b ? 6 : 0);  break
			case g:    h = (b - r) / d + 2;                break
			case b:    h = (r - g) / d + 4;                break
		}
		h /= 6
	}
	return [hue: Math.round(h * 100), saturation: Math.round(s * 100), level: Math.round(l * 100)]
}

@Field final Map    colorsRGB = [
	aliceblue: [[240, 248, 255], 'Alice Blue'],
	antiquewhite: [[250, 235, 215], 'Antique White'],
	aqua: [[0, 255, 255], 'Aqua'],
	aquamarine: [[127, 255, 212], 'Aquamarine'],
	azure: [[240, 255, 255], 'Azure'],
	beige: [[245, 245, 220], 'Beige'],
	bisque: [[255, 228, 196], 'Bisque'],
	black: [[0, 0, 0], 'Black'],
	blanchedalmond: [[255, 235, 205], 'Blanched Almond'],
	blue: [[0, 0, 255], 'Blue'],
	blueviolet: [[138, 43, 226], 'Blue Violet'],
	brown: [[165, 42, 42], 'Brown'],
	burlywood: [[222, 184, 135], 'Burly Wood'],
	cadetblue: [[95, 158, 160], 'Cadet Blue'],
	chartreuse: [[127, 255, 0], 'Chartreuse'],
	chocolate: [[210, 105, 30], 'Chocolate'],
	coral: [[255, 127, 80], 'Coral'],
	cornflowerblue: [[100, 149, 237], 'Corn Flower Blue'],
	cornsilk: [[255, 248, 220], 'Corn Silk'],
	crimson: [[220, 20, 60], 'Crimson'],
	cyan: [[0, 255, 255], 'Cyan'],
	darkblue: [[0, 0, 139], 'Dark Blue'],
	darkcyan: [[0, 139, 139], 'Dark Cyan'],
	darkgoldenrod: [[184, 134, 11], 'Dark Golden Rod'],
	darkgray: [[169, 169, 169], 'Dark Gray'],
	darkgreen: [[0, 100, 0], 'Dark Green'],
	darkgrey: [[169, 169, 169], 'Dark Grey'],
	darkkhaki: [[189, 183, 107], 'Dark Khaki'],
	darkmagenta: [[139, 0, 139],  'Dark Magenta'],
	darkolivegreen: [[85, 107, 47], 'Dark Olive Green'],
	darkorange: [[255, 140, 0], 'Dark Orange'],
	darkorchid: [[153, 50, 204], 'Dark Orchid'],
	darkred: [[139, 0, 0], 'Dark Red'],
	darksalmon: [[233, 150, 122], 'Dark Salmon'],
	darkseagreen: [[143, 188, 143], 'Dark Sea Green'],
	darkslateblue: [[72, 61, 139], 'Dark Slate Blue'],
	darkslategray: [[47, 79, 79], 'Dark Slate Gray'],
	darkslategrey: [[47, 79, 79], 'Dark Slate Grey'],
	darkturquoise: [[0, 206, 209], 'Dark Turquoise'],
	darkviolet: [[148, 0, 211], 'Dark Violet'],
	deeppink: [[255, 20, 147], 'Deep Pink'],
	deepskyblue: [[0, 191, 255], 'Deep Sky Blue'],
	dimgray: [[105, 105, 105], 'Dim Gray'],
	dimgrey: [[105, 105, 105], 'Dim Grey'],
	dodgerblue: [[30, 144, 255], 'Dodger Blue'],
	firebrick: [[178, 34, 34], 'Fire Brick'],
	floralwhite: [[255, 250, 240], 'Floral White'],
	forestgreen: [[34, 139, 34], 'Forest Green'],
	fuchsia: [[255, 0, 255], 'Fuchsia'],
	gainsboro: [[220, 220, 220], 'Gainsboro'],
	ghostwhite: [[248, 248, 255], 'Ghost White'],
	gold: [[255, 215, 0], 'Gold'],
	goldenrod: [[218, 165, 32], 'Golden Rod'],
	gray: [[128, 128, 128], 'Gray'],
	green: [[0, 128, 0], 'Green'],
	greenyellow: [[173, 255, 47], 'Green Yellow'],
	grey: [[128, 128, 128], 'Grey'],
	honeydew: [[240, 255, 240], 'Honey Dew'],
	hotpink: [[255, 105, 180], 'Hot Pink'],
	indianred: [[205, 92, 92], 'Indian Red'],
	indigo: [[75, 0, 130], 'Indigo'],
	ivory: [[255, 255, 240], 'Ivory'],
	khaki: [[240, 230, 140], 'Khaki'],
	lavender: [[230, 230, 250], 'Lavender'],
	lavenderblush: [[255, 240, 245], 'Lavender Blush'],
	lawngreen: [[124, 252, 0], 'Lawn Green'],
	lemonchiffon: [[255, 250, 205], 'Lemon Chiffon'],
	lightblue: [[173, 216, 230], 'Light Blue'],
	lightcoral: [[240, 128, 128], 'Light Coral'],
	lightcyan: [[224, 255, 255], 'Light Cyan'],
	lightgoldenrodyellow: [[250, 250, 210], 'Light Golden Rod Yellow'],
	lightgray: [[211, 211, 211], 'Light Gray'],
	lightgreen: [[144, 238, 144], 'Light Green'],
	lightgrey: [[211, 211, 211], 'Light Grey'],
	lightpink: [[255, 182, 193], 'Light Pink'],
	lightsalmon: [[255, 160, 122], 'Light Salmon'],
	lightseagreen: [[32, 178, 170], 'Light Sea Green'],
	lightskyblue: [[135, 206, 250], 'Light Sky Blue'],
	lightslategray: [[119, 136, 153], 'Light Slate Gray'],
	lightslategrey: [[119, 136, 153], 'Light Slate Grey'],
	lightsteelblue: [[176, 196, 222], 'Ligth Steel Blue'],
	lightyellow: [[255, 255, 224], 'Light Yellow'],
	lime: [[0, 255, 0], 'Lime'],
	limegreen: [[50, 205, 50], 'Lime Green'],
	linen: [[250, 240, 230], 'Linen'],
	magenta: [[255, 0, 255], 'Magenta'],
	maroon: [[128, 0, 0], 'Maroon'],
	mediumaquamarine: [[102, 205, 170], 'Medium Aquamarine'],
	mediumblue: [[0, 0, 205], 'Medium Blue'],
	mediumorchid: [[186, 85, 211], 'Medium Orchid'],
	mediumpurple: [[147, 112, 219], 'Medium Purple'],
	mediumseagreen: [[60, 179, 113], 'Medium Sea Green'],
	mediumslateblue: [[123, 104, 238], 'Medium Slate Blue'],
	mediumspringgreen: [[0, 250, 154], 'Medium Spring Green'],
	mediumturquoise: [[72, 209, 204], 'Medium Turquoise'],
	mediumvioletred: [[199, 21, 133], 'Medium Violet Red'],
	midnightblue: [[25, 25, 112], 'Medium Blue'],
	mintcream: [[245, 255, 250], 'Mint Cream'],
	mistyrose: [[255, 228, 225], 'Misty Rose'],
	moccasin: [[255, 228, 181], 'Moccasin'],
	navajowhite: [[255, 222, 173], 'Navajo White'],
	navy: [[0, 0, 128], 'Navy'],
	oldlace: [[253, 245, 230], 'Old Lace'],
	olive: [[128, 128, 0], 'Olive'],
	olivedrab: [[107, 142, 35], 'Olive Drab'],
	orange: [[255, 165, 0], 'Orange'],
	orangered: [[255, 69, 0], 'Orange Red'],
	orchid: [[218, 112, 214], 'Orchid'],
	palegoldenrod: [[238, 232, 170], 'Pale Golden Rod'],
	palegreen: [[152, 251, 152], 'Pale Green'],
	paleturquoise: [[175, 238, 238], 'Pale Turquoise'],
	palevioletred: [[219, 112, 147], 'Pale Violet Red'],
	papayawhip: [[255, 239, 213], 'Papaya Whip'],
	peachpuff: [[255, 218, 185], 'Peach Cuff'],
	peru: [[205, 133, 63], 'Peru'],
	pink: [[255, 192, 203], 'Pink'],
	plum: [[221, 160, 221], 'Plum'],
	powderblue: [[176, 224, 230], 'Powder Blue'],
	purple: [[128, 0, 128], 'Purple'],
	rebeccapurple: [[102, 51, 153], 'Rebecca Purple'],
	red: [[255, 0, 0], 'Red'],
	rosybrown: [[188, 143, 143], 'Rosy Brown'],
	royalblue: [[65, 105, 225], 'Royal Blue'],
	saddlebrown: [[139, 69, 19], 'Saddle Brown'],
	salmon: [[250, 128, 114], 'Salmon'],
	sandybrown: [[244, 164, 96], 'Sandy Brown'],
	seagreen: [[46, 139, 87], 'Sea Green'],
	seashell: [[255, 245, 238], 'Sea Shell'],
	sienna: [[160, 82, 45], 'Sienna'],
	silver: [[192, 192, 192], 'Silver'],
	skyblue: [[135, 206, 235], 'Sky Blue'],
	slateblue: [[106, 90, 205], 'Slate Blue'],
	slategray: [[112, 128, 144], 'Slate Gray'],
	slategrey: [[112, 128, 144], 'Slate Grey'],
	snow: [[255, 250, 250], 'Snow'],
	springgreen: [[0, 255, 127], 'Spring Green'],
	steelblue: [[70, 130, 180], 'Steel Blue'],
	tan: [[210, 180, 140], 'Tan'],
	teal: [[0, 128, 128], 'Teal'],
	thistle: [[216, 191, 216], 'Thistle'],
	tomato: [[255, 99, 71], 'Tomato'],
	turquoise: [[64, 224, 208], 'Turquoise'],
	violet: [[238, 130, 238], 'Violet'],
	wheat: [[245, 222, 179], 'Wheat'],
	white: [[255, 255, 255], 'White'],
	whitesmoke: [[245, 245, 245], 'White Smoke'],
	yellow: [[255, 255, 0], 'Yellow'],
	yellowgreen: [[154, 205, 50], 'Yellow Green']
]