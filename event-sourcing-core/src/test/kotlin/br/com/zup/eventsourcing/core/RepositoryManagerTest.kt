package br.com.zup.eventsourcing.core


import br.com.zup.eventsourcing.core.domain.MyAggregateRoot
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import java.util.*

class RepositoryManagerTest {

    val repository1 = mock<Repository<MyAggregateRoot>> {}
    val repository2 = mock<Repository<MyAggregateRoot>> {}
    val repositoryManager = RepositoryManager(listOf(repository1, repository2))

    @Test
    fun save_withoutMetaData() {
        repositoryManager.save(MyAggregateRoot(AggregateId(UUID.randomUUID())))
        verify(repository1, times(1)).save(any<MyAggregateRoot>(), any<Repository.OptimisticLock>())
        verify(repository2, times(1)).save(any<MyAggregateRoot>(), any<Repository.OptimisticLock>())
    }

    @Test
    fun save_withMetaData() {
        repositoryManager.save(MyAggregateRoot(AggregateId(UUID.randomUUID())), MetaData())
        verify(repository1, times(1)).save(any(), any(), any())
        verify(repository2, times(1)).save(any(), any(), any())
    }

    @Test
    fun get() {
        repositoryManager.get(AggregateId(UUID.randomUUID()))
        verify(repository1, times(1)).get(any())
        verify(repository2, times(0)).get(any())
    }

    @Test
    fun getLastMetaData() {
        repositoryManager.getLastMetaData(AggregateId(UUID.randomUUID()))
        verify(repository1, times(1)).getLastMetaData(any())
        verify(repository2, times(0)).getLastMetaData(any())
    }

}