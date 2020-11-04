package net.pristo.kotlin.proton

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiDemoApplication

fun main(args: Array<String>) {
    runApplication<ApiDemoApplication>(*args){
        setBannerMode(Banner.Mode.OFF)
    }
}
