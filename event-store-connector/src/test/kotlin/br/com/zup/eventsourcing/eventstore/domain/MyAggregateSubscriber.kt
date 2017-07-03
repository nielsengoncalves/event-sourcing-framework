package br.com.zup.eventsourcing.eventstore.domain

import br.com.zup.eventsourcing.eventstore.PersistentAggregateSubscriber
import org.springframework.stereotype.Service

@Service
open class MyAggregateSubscriber : PersistentAggregateSubscriber<MyAggregate>()
