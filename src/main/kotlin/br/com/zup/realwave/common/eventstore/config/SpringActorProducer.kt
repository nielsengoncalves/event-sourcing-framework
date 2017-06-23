package br.com.zup.realwave.common.eventstore.config

import akka.actor.Actor
import akka.actor.IndirectActorProducer
import org.springframework.context.ApplicationContext

class SpringActorProducer(private val applicationContext: ApplicationContext,
                          private val actorBeanName: String) : IndirectActorProducer {

    override fun produce(): Actor {
        return applicationContext.getBean(actorBeanName) as Actor
    }

    override fun actorClass(): Class<out Actor> {
        return applicationContext.getType(actorBeanName) as Class<out Actor>
    }
}