package br.com.zup.realwave.common.eventstore.domain

import br.com.zup.realwave.common.eventstore.AggregateId
import br.com.zup.realwave.common.eventstore.Event

/**
 * Created by zacacj on 6/20/2017.
 */

class CreateEvent(val aggregateId: AggregateId) : Event()

class ModifyEvent(val status: String) : Event()
