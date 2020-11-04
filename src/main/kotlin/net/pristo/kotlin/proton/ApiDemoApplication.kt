package net.pristo.kotlin.proton

import net.pristo.kotlin.proton.config.DominoProperties
import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(DominoProperties::class)
class ApiDemoApplication

fun main(args: Array<String>) {
    runApplication<ApiDemoApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}
