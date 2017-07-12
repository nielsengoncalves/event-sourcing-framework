package br.com.zup.eventsourcing.relationaldb.config

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles

@Configuration
@EnableAutoConfiguration
@ActiveProfiles(profiles = arrayOf("test", "postgresql"))
@ComponentScan(basePackages = arrayOf("br.com.zup.eventsourcing.relationaldb"))
open class RepositoryTestConfig

