package br.com.zup.eventsourcing.eventstore.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(br.com.zup.eventsourcing.eventstore.config.EventStoreConfig::class)
open class SpringConfigTest
