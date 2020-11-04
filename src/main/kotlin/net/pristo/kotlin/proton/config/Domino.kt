package net.pristo.kotlin.proton.config

import com.hcl.domino.db.model.Database
import com.hcl.domino.db.model.Server
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.getProperty
import java.io.File
import java.net.URLDecoder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Configuration
class DominoConfig(
    val env: Environment
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun getExecutorService(): ExecutorService {
        val executors = env.getProperty<Int>("domino.executors") ?: 1
        return Executors.newFixedThreadPool(executors)
    }

    @Bean
    fun getDatabase(executorService: ExecutorService): Database {
        log.debug("Initializing Domino connection")
        val hostName = env.getProperty("domino.server") ?: throw IllegalArgumentException("server hostname not specified")
        val port = env.getProperty<Int>("domino.port") ?: throw IllegalArgumentException("server port not specified")
        val database = env.getProperty("domino.database") ?: throw IllegalArgumentException("database not specified")
        val trustFile = env.getProperty("domino.security.trustFile") ?: throw IllegalArgumentException("trust file not specified")
        val certFile = env.getProperty("domino.security.certFile") ?: throw IllegalArgumentException("cert file not specified")
        val keyFile = env.getProperty("domino.security.keyFile") ?: throw IllegalArgumentException("key file not specified")
        val idPassword = env.getProperty("domino.security.idPassword") ?: throw IllegalArgumentException("id password")


        val server = Server(
            hostName, port, getFile(trustFile), getFile(certFile),
            getFile(keyFile), null, idPassword, executorService
        )

        log.debug("Done initializing Domino connection")
        return server.useDatabase(database)
    }

    private fun getFile(fileName: String): File {
        return File(URLDecoder.decode(ClassLoader.getSystemResource(fileName).file, "UTF-8"))
    }
}
