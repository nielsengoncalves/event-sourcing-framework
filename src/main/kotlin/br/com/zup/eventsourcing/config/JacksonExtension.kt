package br.com.zup.eventsourcing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.lang.Compiler.disable
import com.fasterxml.jackson.datatype.jsr310.JSR310Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule


private object JacksonExtension {

    val jacksonObjectMapper: ObjectMapper by lazy {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

}

fun <T> String.jsonToObject(t: Class<T>): T =
        JacksonExtension.jacksonObjectMapper.readValue(this, t)

fun <T> T.objectToJson(): String =
        JacksonExtension.jacksonObjectMapper.writeValueAsString(this)
