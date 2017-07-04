package br.com.zup.eventsourcing.core.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

private object JacksonExtension {

    val jacksonObjectMapper: ObjectMapper by lazy {
        ObjectMapper().registerModule(KotlinModule())
                .registerModule(JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

}


fun <T> String.jsonToObject(t: Class<T>): T =
        JacksonExtension.jacksonObjectMapper.readValue(this, t)

fun <T> T.objectToJson(): String =
        JacksonExtension.jacksonObjectMapper.writeValueAsString(this)
