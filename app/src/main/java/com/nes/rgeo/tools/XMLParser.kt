package com.nes.rgeo.tools

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader

class XMLParser {
    @Throws(Exception::class, IOException::class, XmlPullParserException::class)
    fun parse(xml: String): HashMap<String, String> {
        val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
        var tagName: String
        var text = ""
        val hm = HashMap<String, String>()

        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()
        val sr = StringReader(xml)

        xpp.setInput(sr)
        var eventType = xpp.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (xpp.attributeCount > 0) {
                    tagName = "${xpp.name}.${xpp.getAttributeName(0)}"
                    hm[tagName] = xpp.getAttributeValue(0)
                }
            } else if (eventType == XmlPullParser.TEXT) {
                text = xpp.text //Pulling out node text
            } else if (eventType == XmlPullParser.END_TAG) {
                tagName = xpp.name

                //prevent from duplicating tags, it will accept the only one <result></result> from array
                if (hm.containsKey(tagName)) {
                    return hm
                }

                hm[tagName] = text

                text = "" //Reset text for the next node
            }
            eventType = xpp.next()
        }
        return hm

    }
}