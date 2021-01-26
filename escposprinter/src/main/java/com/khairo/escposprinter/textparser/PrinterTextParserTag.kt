package com.khairo.escposprinter.textparser

import java.util.*

class PrinterTextParserTag(oldTag: String) {
    var tagName = ""
    val attributes = Hashtable<String, String>()
    var length = 0
    var isCloseTag = false
    fun getAttribute(key: String): String? = attributes[key]

    fun hasAttribute(key: String): Boolean = attributes.containsKey(key)

    init {
        var tag = oldTag
        tag = tag.trim { it <= ' ' }
//        if (!tag.startsWith("<") || !tag.endsWith(">")) {
//            return
//        }
        length = tag.length
        val openTagIndex = tag.indexOf("<")
        val closeTagIndex = tag.indexOf(">")
        val nextSpaceIndex = tag.indexOf(" ")
        if (nextSpaceIndex != -1 && nextSpaceIndex < closeTagIndex) {
            tagName = tag.substring(openTagIndex + 1, nextSpaceIndex).toLowerCase(Locale.ROOT)
            var attributesString = tag.substring(nextSpaceIndex, closeTagIndex).trim { it <= ' ' }
            while (attributesString.contains("='")) {
                val egalPos = attributesString.indexOf("='")
                val endPos = attributesString.indexOf("'", egalPos + 2)
                val attributeName = attributesString.substring(0, egalPos)
                val attributeValue = attributesString.substring(egalPos + 2, endPos)
                if (attributeName != "") {
                    attributes[attributeName] = attributeValue
                }
                attributesString = attributesString.substring(endPos + 1).trim { it <= ' ' }
            }

        } else tagName = tag.substring(openTagIndex + 1, closeTagIndex).toLowerCase(Locale.ROOT)

        if (tagName.startsWith("/")) {
            tagName = tagName.substring(1)
            isCloseTag = true
        }
    }
}
