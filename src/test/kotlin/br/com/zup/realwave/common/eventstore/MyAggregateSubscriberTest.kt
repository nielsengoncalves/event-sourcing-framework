package br.com.zup.realwave.common.eventstore

import br.com.zup.realwave.common.eventstore.config.BaseTest
import br.com.zup.realwave.common.eventstore.domain.MyAggregate
import br.com.zup.realwave.common.eventstore.domain.MyAggregateRepository
import br.com.zup.realwave.common.eventstore.domain.MyAggregateSubscriber
import br.com.zup.realwave.common.eventstore.domain.MyEventHandler
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import java.lang.Thread.sleep
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


/**
 * Created by zacacj on 6/21/2017.
 */
class MyAggregateSubscriberTest : BaseTest() {

    @Autowired
    lateinit var eventHandler: MyEventHandler
    @Autowired
    lateinit var myAggregateSubscriber: MyAggregateSubscriber
    @Autowired
    lateinit var myAggregateRepository: MyAggregateRepository

    @Test
    fun shouldHandleWithSuccess() {
        myAggregateSubscriber.start()
        val id = java.util.UUID.randomUUID().toString()
        val myAggregate = MyAggregate(AggregateId(id))
        val metaData = MetaData()

        metaData.set("teste", "teste")
        myAggregateRepository.save(myAggregate, metaData)
        myAggregateSubscriber.start()
        sleep(1000)
        assertNotNull( eventHandler.aggregateId)
        assertEquals(myAggregate.id, eventHandler.aggregateId)
    }

    private fun <T> anyObject(): T {
        return Mockito.anyObject<T>()
    }
}