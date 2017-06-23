package br.com.zup.realwave.common.eventstore.domain

import br.com.zup.realwave.common.eventstore.EventStoreRepository
import org.springframework.stereotype.Service

/**
 * Created by zacacj on 6/20/2017.
 */
@Service
open class MyAggregateRepository : EventStoreRepository<MyAggregate>()