package br.com.zup.eventsourcing.core

class RepositoryManager<T : AggregateRoot>(val repositories: List<Repository<T>>) : Repository<T>() {

    override fun getSavedEvents(aggregateId: AggregateId): List<Event> {
        return repositories.first().getSavedEvents(aggregateId)
    }

    override fun save(aggregateRoot: T, metaData: MetaData, lock: OptimisticLock) {
        repositories.forEach { it.save(aggregateRoot, metaData, lock) }
        aggregateRoot.clearEvents()
    }

    override fun save(aggregateRoot: T, lock: Repository.OptimisticLock) {
        repositories.forEach { it.save(aggregateRoot, lock) }
        aggregateRoot.clearEvents()
    }

    override fun get(aggregateId: AggregateId): T {
        return repositories.first().get(aggregateId)
    }

    override fun getLastMetaData(aggregateId: AggregateId): MetaData {
        return repositories.first().getLastMetaData(aggregateId)
    }
}