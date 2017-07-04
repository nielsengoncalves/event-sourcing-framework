package br.com.zup.eventsourcing.relationaldb.domain

import br.com.zup.eventsourcing.relationaldb.JdbcEventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class MyJdbcEventRepository(@Autowired jdbcTemplate: JdbcTemplate) : JdbcEventRepository<MyAggregate>(jdbcTemplate)