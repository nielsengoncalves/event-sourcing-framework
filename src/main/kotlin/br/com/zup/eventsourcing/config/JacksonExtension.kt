package br.com.zup.eventsourcing.config

import com.fasterxml.jackson.databind.ObjectMapper

private object JacksonExtension {

    val jacksonObjectMapper: ObjectMapper by lazy {
        ObjectMapper()
    }

}

fun <T> String.jsonToObject(t: Class<T>): T =
        JacksonExtension.jacksonObjectMapper.readValue(this, t)

fun <T> T.objectToJson(): String =
        JacksonExtension.jacksonObjectMapper.writeValueAsString(this)
