package br.com.zup.realwave.common.eventstore

import akka.actor.ActorSystem
import br.com.zup.realwave.common.eventstore.config.SpringExtension
import eventstore.EventStream
import eventstore.PersistentSubscriptionActor
import eventstore.Settings
import eventstore.tcp.ConnectionActor
import org.springframework.stereotype.Component
import java.lang.reflect.ParameterizedType


/**
 * Created by cleber on 5/30/17.
 */
@Component
abstract class PersistentAggregateSubscriber<T : Aggregate>(var actorSystem: ActorSystem,
                                                            var springExtension: SpringExtension) {

    fun start() {

        val connection = actorSystem.actorOf(ConnectionActor.getProps())

        val subscriptionListener = actorSystem.actorOf(springExtension.props(PersistentSubscriptionListener.BEAN_NAME))

        actorSystem.actorOf(PersistentSubscriptionActor.props(connection,
                subscriptionListener, EventStream.`Id$`.`MODULE$`.apply(getGenericName()),
                "stream-group", Settings.Default().defaultCredentials(), Settings.Default(), false))


    }

    private fun getGenericName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).simpleName
    }
}
