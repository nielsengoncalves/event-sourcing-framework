package br.com.zup.eventsourcing.relationaldb.config

import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@RunWith(org.springframework.test.context.junit4.SpringRunner::class)
@ContextConfiguration(classes = arrayOf(RepositoryTestConfig::class))
abstract class RepositoryBaseTest {

    fun logAndFail(message: String, logger: org.slf4j.Logger) {
        logger.error(message)

        org.junit.Assert.fail(message)
    }

    fun logAndFail(message: String, e: Exception, logger: org.slf4j.Logger) {
        logger.error(message, e)

        org.junit.Assert.fail(message)
    }

}
