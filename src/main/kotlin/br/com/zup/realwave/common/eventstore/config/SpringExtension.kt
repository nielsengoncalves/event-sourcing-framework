package br.com.zup.realwave.common.eventstore.config

import akka.actor.Extension
import akka.actor.Props
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
open class SpringExtension : Extension {

    private var applicationContext: ApplicationContext? = null

    fun initialize(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    fun props(actorBeanName: String): Props {
        return Props.create(SpringActorProducer::class.java,
                applicationContext, actorBeanName)
    }
}