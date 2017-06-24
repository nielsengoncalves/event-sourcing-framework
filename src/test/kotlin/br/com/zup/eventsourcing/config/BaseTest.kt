package br.com.zup.eventsourcing.config

import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner

/**
 * Created by zacacj on 6/20/2017.
 */
@SpringBootTest
@RunWith(SpringRunner::class)
@ContextConfiguration(classes = arrayOf(SpringConfigTest::class))
abstract class BaseTest {

    fun logAndFail(message: String, logger: org.slf4j.Logger) {
        logger.error(message)

        org.junit.Assert.fail(message)
    }

    fun logAndFail(message: String, e: Exception, logger: org.slf4j.Logger) {
        logger.error(message, e)

        org.junit.Assert.fail(message)
    }

}