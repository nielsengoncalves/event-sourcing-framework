package br.com.zup.eventsourcing.relationaldb.domain

import br.com.zup.eventsourcing.core.Settings
import br.com.zup.eventsourcing.relationaldb.JdbcEventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class RepositoryOptimisticLockEnabled(@Autowired jdbcTemplate: JdbcTemplate) :
        JdbcEventRepository<MyAggregateRoot>(jdbcTemplate)

@Service
class RepositoryOptimisticLockDisable(@Autowired jdbcTemplate: JdbcTemplate) :
        JdbcEventRepository<MyAggregateRoot>(jdbcTemplate, settings = Settings(optimisticLockEnabled = false))