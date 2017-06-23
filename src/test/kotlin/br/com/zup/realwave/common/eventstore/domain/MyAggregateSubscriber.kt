package br.com.zup.realwave.common.eventstore.domain

import akka.actor.ActorSystem
import br.com.zup.realwave.common.eventstore.PersistentAggregateSubscriber
import br.com.zup.realwave.common.eventstore.config.SpringExtension
import org.springframework.stereotype.Service

/**
 * Created by zacacj on 6/21/2017.
 */

@Service
open class MyAggregateSubscriber(actorSystem: ActorSystem, springExtension: SpringExtension)
    : PersistentAggregateSubscriber<MyAggregate>(actorSystem,springExtension)
