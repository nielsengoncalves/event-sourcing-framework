package br.com.zup.eventsourcing.eventstore

import akka.actor.ActorSystem
import br.com.zup.eventsourcing.core.AggregateRoot
import br.com.zup.eventsourcing.core.EventHandler
import eventstore.EventStream
import eventstore.PersistentSubscriptionActor
import eventstore.Settings
import eventstore.tcp.ConnectionActor
import java.lang.reflect.ParameterizedType

abstract class PersistentAggregateSubscriber<T : AggregateRoot>(val subscriptionGroupName: String = "", val eventHandler: EventHandler) {

    val actorSystem = ActorSystem.create()!!

    open fun start() {

        val connection = actorSystem.actorOf(ConnectionActor.getProps())

        val subscriptionListener = actorSystem.actorOf(PersistentSubscriptionListener.props(eventHandler))

        val groupName = getGroupName(subscriptionGroupName)
        actorSystem.actorOf(PersistentSubscriptionActor.props(connection,
                subscriptionListener, EventStream.`Id$`.`MODULE$`.apply(getGenericName()),
                groupName, Settings.Default().defaultCredentials(), Settings.Default(), false))


    }

    private fun getGroupName(subscriptionGroupName: String): String? {
        if (subscriptionGroupName.isBlank()) {
            return getGenericName() + "SubscriptionGroup"
        } else {
            return subscriptionGroupName
        }
    }

    private fun getGenericName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).simpleName
    }
}
