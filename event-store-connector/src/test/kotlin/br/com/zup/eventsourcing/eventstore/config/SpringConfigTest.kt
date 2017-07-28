package br.com.zup.eventsourcing.eventstore.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = arrayOf("br.com.zup.eventsourcing.eventstore"))
open class SpringConfigTest
