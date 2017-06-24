package br.com.zup.eventsourcing

import akka.actor.ActorSystem
import br.com.zup.eventsourcing.config.SpringExtension
import eventstore.EventStream
import eventstore.PersistentSubscriptionActor
import eventstore.Settings
import eventstore.tcp.ConnectionActor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.lang.reflect.ParameterizedType


/**
 * Created by cleber on 5/30/17.
 */
@Component
abstract class PersistentAggregateSubscriber<T : br.com.zup.eventsourcing.Aggregate>(val subscriptionGroupName : String = "") {

    @Autowired lateinit var actorSystem: ActorSystem
    @Autowired lateinit var springExtension: SpringExtension

    fun start() {

        val connection = actorSystem.actorOf(ConnectionActor.getProps())

        val subscriptionListener = actorSystem.actorOf(springExtension.props(br.com.zup.eventsourcing.PersistentSubscriptionListener.Companion.BEAN_NAME))

        val groupName = getGroupName(subscriptionGroupName)
        actorSystem.actorOf(PersistentSubscriptionActor.props(connection,
                subscriptionListener, EventStream.`Id$`.`MODULE$`.apply(getGenericName()),
                groupName, Settings.Default().defaultCredentials(), Settings.Default(), false))


    }

    private fun  getGroupName(subscriptionGroupName: String): String? {
        if (subscriptionGroupName.isBlank()) {
            return getGenericName() + "SubscriptionGroup"
        }
        else {
            return subscriptionGroupName
        }
    }

    private fun getGenericName(): String {
        return ((javaClass
                .genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>).simpleName
    }
}
