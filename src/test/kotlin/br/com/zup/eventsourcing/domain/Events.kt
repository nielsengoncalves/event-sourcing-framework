package br.com.zup.eventsourcing.domain

import br.com.zup.eventsourcing.Event

/**
 * Created by zacacj on 6/20/2017.
 */

class CreateEvent(val aggregateId: br.com.zup.eventsourcing.AggregateId) : Event()

class ModifyEvent(val status: String) : Event()
