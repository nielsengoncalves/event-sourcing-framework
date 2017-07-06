package br.com.zup.eventsourcing.core

class RepositoryManager<T : AggregateRoot>(val repositories: List<Repository<T>>) {
    fun save(aggregateRoot: T) {
        repositories.forEach { it.save(aggregateRoot) }
        aggregateRoot.clearEvents()
    }

    fun get(aggregateId: AggregateId): T {
        return repositories.first().get(aggregateId)
    }
}