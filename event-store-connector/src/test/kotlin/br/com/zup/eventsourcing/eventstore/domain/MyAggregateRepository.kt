package br.com.zup.eventsourcing.eventstore.domain

import br.com.zup.eventsourcing.eventstore.EventStoreRepository
import org.springframework.stereotype.Service

@Service
open class MyAggregateRepository : EventStoreRepository<MyAggregateRoot>()