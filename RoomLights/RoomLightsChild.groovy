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
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "lightsDayPage", title: "<b>Lights (Day)</b>") {
        section("") {
            input "dayLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "dayLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "dayColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "dayColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList
        }
    }
}

def lightsEveningPage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "lightsEveningPage", title: "<b>Lights (Evening)</b>") {
        section("") {
            input "eveningLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "eveningLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "eveningColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "eveningColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList
        }
    }
}

def lightsNightPage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "lightsNightPage", title: "<b>Lights (Night)</b>") {
        section("") {
            input "nightLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "nightLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "nightColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "nightColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList
        }
    }
}

def lightsAwayPage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "lightsAwayPage", title: "<b>Lights (Away)</b>") {
        section("") {
            input "awayLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "awayLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "awayColorTemperature", "number", title: "Color Temperature (Kelvin)", description: "2000...65000", required: true, defaultValue: 2700
            input "awayColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList
        }
    }
}

def sleepModePage()
{
    def colorsList = colorsRGB.collect { [(it.key):it.value[1]] }

    dynamicPage(name: "sleepModePage", title: "<b>Sleep mode</b>") {
        section("<b>Switch</b>") {
            input "sleepSwitch", "capability.switch", title: "Sleep Switch", required: false, multiple: false
            input "sleepTimeout", "number", title: "Timeout (hours)", description: "1...24", required: true, defaultValue: 15
        }

        section("<b>Lights</b>") {
            input "sleepLights", "capability.light", title: "Lights to Control", required: false, multiple: true
            input "sleepLevel", "number", title: "Level", description: "1...100", required: true, defaultValue: 100
            input "sleepColorTemperature", "number", title: "Color Temperature", description: "2000...65000", required: true, defaultValue: 2700
            input "sleepColor", "enum", title: "Color", required: false, multiple: false, defaultValue: null, options: colorsList
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

    if (!disableApp) {
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
            runIn(sleepTimeout*60*60, turnOffSleep)
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

        if(modeLights.color) {
            setLightsColor(lights, modeLights.color)
        }
        else {
            setLightsColorTemperature(lights, modeLights.colorTemperature)
        }
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

            if(sleepColor) {
                setLightsColor(sleepLights, sleepColor)
            }
            else {
                setLightsColorTemperature(sleepLights, sleepColorTemperature)
            }
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
            return ["name": "Day", "devices": dayLights, "level": dayLevel, "colorTemperature": dayColorTemperature, "color": dayColor]
            break;
        case "Evening":
            return ["name": "Evening", "devices": eveningLights, "level": eveningLevel, "colorTemperature": eveningColorTemperature, "color": eveningColor]
            break;
        case "Night":
            return ["name": "Night", "devices": nightLights, "level": nightLevel, "colorTemperature": nightColorTemperature, "color": nightColor]
            break;
        case "Away":
            return ["name": "Away", "devices": awayLights, "level": awayLevel, "colorTemperature": awayColorTemperature, "color": awayColor]
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

def setLightsColor(devices, colorName) {
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