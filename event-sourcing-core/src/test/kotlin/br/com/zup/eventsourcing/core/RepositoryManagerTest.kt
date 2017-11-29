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
        val aggregateRoot = MyAggregateRoot(AggregateId(UUID.randomUUID()))
        repositoryManager.save(aggregateRoot)
        verify(repository1, times(1)).save(aggregateRoot, Repository.OptimisticLock.ENABLED)
        verify(repository2, times(1)).save(aggregateRoot, Repository.OptimisticLock.ENABLED)
    }

    @Test
    fun save_withMetaData() {
        val aggregateRoot = MyAggregateRoot(AggregateId(UUID.randomUUID()))
        val metaData = MetaData()
        repositoryManager.save(aggregateRoot, metaData, Repository.OptimisticLock.ENABLED)
        verify(repository1, times(1)).save(aggregateRoot, metaData, Repository.OptimisticLock.ENABLED)
        verify(repository2, times(1)).save(aggregateRoot, metaData, Repository.OptimisticLock.ENABLED)
    }

    @Test
    fun get() {
        val aggregateId = AggregateId(UUID.randomUUID())
        repositoryManager.get(aggregateId)
        verify(repository1, times(1)).get(aggregateId)
        verify(repository2, times(0)).get(aggregateId)
    }

    @Test
    fun getLastMetaData() {
        val aggregateId = AggregateId(UUID.randomUUID())
        repositoryManager.getLastMetaData(aggregateId)
        verify(repository1, times(1)).getLastMetaData(aggregateId)
        verify(repository2, times(0)).getLastMetaData(aggregateId)
    }

    @Test
    fun managerMustDelegateDisabledOptimisticLockToRepositories(){
        val aggregateRoot = MyAggregateRoot(AggregateId(UUID.randomUUID()))
        repositoryManager.save(aggregateRoot, Repository.OptimisticLock.DISABLED)
        verify(repository1, times(1)).save(aggregateRoot, Repository.OptimisticLock.DISABLED)
        verify(repository2, times(1)).save(aggregateRoot, Repository.OptimisticLock.DISABLED)
    }

    @Test
    fun delegateDisabledOptimisticLockToRepositories_withMetaData() {
        val aggregateRoot = MyAggregateRoot(AggregateId(UUID.randomUUID()))
        val metaData = MetaData()
        repositoryManager.save(aggregateRoot, metaData, Repository.OptimisticLock.DISABLED)
        verify(repository1, times(1)).save(aggregateRoot, metaData, Repository.OptimisticLock.DISABLED)
        verify(repository2, times(1)).save(aggregateRoot, metaData, Repository.OptimisticLock.DISABLED)
    }

}