package br.com.zup.eventsourcing.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Created by zacacj on 6/20/2017.
 */

@Configuration
@Import(br.com.zup.eventsourcing.config.EventStoreConfig::class)
@ComponentScan(basePackages = arrayOf("br.com.zup.realwave.common.eventstore"))
open class SpringConfigTest
