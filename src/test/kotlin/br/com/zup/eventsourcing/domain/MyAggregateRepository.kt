package br.com.zup.eventsourcing.domain

import br.com.zup.eventsourcing.EventStoreRepository
import org.springframework.stereotype.Service

/**
 * Created by zacacj on 6/20/2017.
 */
@Service
open class MyAggregateRepository : EventStoreRepository<MyAggregate>()