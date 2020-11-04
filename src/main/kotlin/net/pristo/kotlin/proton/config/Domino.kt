package net.pristo.kotlin.proton.config

import com.hcl.domino.db.model.Database
import com.hcl.domino.db.model.Server
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.net.URLDecoder
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ConstructorBinding
data class DominoSecurity(
    val trustFile: String,
    val certFile: String,
    val keyFile: String,
    val keyPassword: String?,
    val idPassword: String?
)

@ConstructorBinding
@ConfigurationProperties("domino")
data class DominoProperties(
    val executors: Int = 1,
    val server: String,
    val port: Int,
    val database: String,
    val security : DominoSecurity
)

@Configuration
class DominoConfig(
    val dominoProperties: DominoProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun getExecutorService(): ExecutorService {
        val executors = dominoProperties.executors
        return Executors.newFixedThreadPool(executors)
    }

    @Bean
    fun getDatabase(executorService: ExecutorService): Database {
        log.debug("Initializing Domino connection")

        val server = Server(
            dominoProperties.server, dominoProperties.port, getFile(dominoProperties.security.trustFile), getFile(dominoProperties.security.certFile),
            getFile(dominoProperties.security.keyFile), dominoProperties.security.keyPassword, dominoProperties.security.idPassword, executorService
        )

        log.debug("Done initializing Domino connection")
        return server.useDatabase(dominoProperties.database)
    }

    private fun getFile(fileName: String): File {
        return File(URLDecoder.decode(ClassLoader.getSystemResource(fileName).file, "UTF-8"))
    }
}
