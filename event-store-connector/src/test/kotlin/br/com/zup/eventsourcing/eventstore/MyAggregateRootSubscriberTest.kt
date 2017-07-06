package br.com.zup.eventsourcing.eventstore

import br.com.zup.eventsourcing.core.AggregateId
import br.com.zup.eventsourcing.core.MetaData
import br.com.zup.eventsourcing.eventstore.config.BaseTest
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateRepository
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateRoot
import br.com.zup.eventsourcing.eventstore.domain.MyAggregateSubscriber
import br.com.zup.eventsourcing.eventstore.domain.MyEventHandler
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import java.lang.Thread.sleep
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MyAggregateRootSubscriberTest : BaseTest() {

    @Autowired
    lateinit var eventHandler: MyEventHandler
    @Autowired
    lateinit var myAggregateSubscriber: MyAggregateSubscriber
    @Autowired
    lateinit var myAggregateRepository: MyAggregateRepository

    @Test
    fun shouldHandleWithSuccess() {
        myAggregateSubscriber.start()
        val id = java.util.UUID.randomUUID()
        val myAggregate = MyAggregateRoot(AggregateId(id))
        val metaData = MetaData()

        metaData.set("teste", "teste")
        myAggregateRepository.save(myAggregate, metaData)
        for (i in 0..10) sleep(100)
        assertNotNull(eventHandler.aggregateId)
        assertEquals(myAggregate.id, eventHandler.aggregateId)
    }

    private fun <T> anyObject(): T {
        return Mockito.anyObject<T>()
    }
}