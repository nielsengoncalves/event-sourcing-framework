package br.com.zup.eventsourcing.eventstore.domain

import br.com.zup.eventsourcing.eventstore.EventStoreRepository
import br.com.zup.eventsourcing.eventstore.Settings
import org.springframework.stereotype.Service


@Service
open class MyAggregateRepositoryWithoutLocking :
        EventStoreRepository<MyAggregateRoot>(Settings(optimisticLockEnabled = false))