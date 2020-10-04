package ch.keepcalm.demo.hazelcast

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@SpringBootApplication
@EnableCaching
class DemoHazelcastApplication

fun main(args: Array<String>) {
	runApplication<DemoHazelcastApplication>(*args)
}


@RestController
@RequestMapping(value = ["/api"], produces = [MediaType.APPLICATION_JSON_VALUE])
class Controller(private val cache: Cache) {

	@GetMapping(path = ["/{key}"])
	fun getStuff(@PathVariable("key") key: String) = cache.getUUID(key)
}

@CacheConfig(cacheNames = ["map"])
@Service
class Cache {
	@Cacheable(value = ["map"], key = "#key")
	fun getUUID(key: String): UUID? = UUID.randomUUID().also { println("Generated $it") }
}
