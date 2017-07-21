package br.com.zup.eventsourcing.core

import br.com.zup.eventsourcing.core.domain.MyAggregateRoot
import br.com.zup.eventsourcing.core.domain.MyRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class RepositoryTest {

    val myRepository = MyRepository()
    @Test
    fun getGenericName() {
        assertEquals(MyAggregateRoot::class.simpleName, myRepository.getGenericNameTest())
    }

    @Test
    fun getGenericCanonicalName() {
        assertEquals(MyAggregateRoot::class.qualifiedName, myRepository.getGenericCanonicalNameTest())
    }

    @Test(expected = Repository.NotFoundException::class)
    fun trowNotFoundExceptionTest() {
        myRepository.throwNotFoundExceptionTest()
    }

}