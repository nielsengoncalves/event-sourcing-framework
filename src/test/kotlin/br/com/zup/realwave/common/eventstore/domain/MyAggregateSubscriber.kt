package br.com.zup.realwave.common.eventstore.domain

import br.com.zup.realwave.common.eventstore.PersistentAggregateSubscriber
import org.springframework.stereotype.Service

/**
 * Created by zacacj on 6/21/2017.
 */

@Service
open class MyAggregateSubscriber : PersistentAggregateSubscriber<MyAggregate>()
