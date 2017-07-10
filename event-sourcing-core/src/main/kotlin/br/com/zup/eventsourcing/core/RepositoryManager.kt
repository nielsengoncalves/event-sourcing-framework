package br.com.zup.eventsourcing.core

class RepositoryManager<T : AggregateRoot>(val repositories: List<Repository<T>>) : Repository<T>() {

    override fun save(aggregateRoot: T, metaData: MetaData) {
        repositories.forEach { it.save(aggregateRoot, metaData) }
        aggregateRoot.clearEvents()
    }

    override fun save(aggregateRoot: T) {
        repositories.forEach { it.save(aggregateRoot) }
        aggregateRoot.clearEvents()
    }

    override fun get(aggregateId: AggregateId): T {
        return repositories.first().get(aggregateId)
    }
}