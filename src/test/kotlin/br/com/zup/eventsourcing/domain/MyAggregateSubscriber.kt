package br.com.zup.eventsourcing.domain

import br.com.zup.eventsourcing.PersistentAggregateSubscriber
import org.springframework.stereotype.Service

/**
 * Created by zacacj on 6/21/2017.
 */

@Service
open class MyAggregateSubscriber : PersistentAggregateSubscriber<MyAggregate>()
