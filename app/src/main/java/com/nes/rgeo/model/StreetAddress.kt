package com.nes.rgeo.model


class StreetAddress(map: HashMap<String, String>) {
    var distanceToPoint = 0.0
    var streetNumber = ""
    var addressLine = ""
    var city = ""
    var postalCode = ""
    var county = ""
    var state = ""
    var stateCode = ""
    var country = ""
    var countryCode = ""
    var formattedAddress = ""

    init {
        if (map.containsKey("formatted_address"))
            formattedAddress = map["formatted_address"]!!
        if (map.containsKey("address-line"))
            addressLine = map["address-line"]!!
        if (map.containsKey("city"))
            city = map["city"]!!
        if (map.containsKey("postal-code"))
            postalCode = map["postal-code"]!!
        if (map.containsKey("street-number"))
            streetNumber = map["street-number"]!!
        if (map.containsKey("county"))
            county = map["county"]!!
        if (map.containsKey("state"))
            state = map["state"]!!
        if (map.containsKey("country"))
            country = map["country"]!!
        if (map.containsKey("state.code"))
            stateCode = map["state.code"]!!
        if (map.containsKey("country.code"))
            countryCode = map["country.code"]!!
    }

}