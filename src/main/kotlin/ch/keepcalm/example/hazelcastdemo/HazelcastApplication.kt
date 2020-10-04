package ch.keepcalm.example.hazelcastdemo

import com.hazelcast.config.Config
import com.hazelcast.config.EvictionPolicy
import com.hazelcast.config.MapConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import com.hazelcast.config.DiscoveryStrategyConfig
import com.hazelcast.eureka.one.EurekaOneDiscoveryStrategyFactory
import org.springframework.beans.factory.annotation.Value

@SpringBootApplication
@EnableCaching
@EnableDiscoveryClient
class HazelcastDemoApplication {

    @Value("\${hazelcast.network.port:5701}")
    private val hazelcastNetworkPort: Int = 0

    @Value("\${hazelcast.group.name}")
    private val hazelcastGroupName: String = "dev"

    @Value("\${hazelcast.management-center.url}")
    private val hazelcastManagementCenterUrl: String = "http://localhost:8080/hazelcast-mancenter"

    @Value("\${hazelcast.management-center.enabled}")
    private val hazelcastManagementCenterEnabled: Boolean = true

    @Value("\${hazelcast.instance}")
    private val instance: String = "cache-1"

    @Bean
    fun hazelCastConfig() = Config(instance)
            .apply {
                this.managementCenterConfig.isEnabled = hazelcastManagementCenterEnabled
                this.managementCenterConfig.url = hazelcastManagementCenterUrl
                this.networkConfig.port = hazelcastNetworkPort
                this.groupConfig.name = hazelcastGroupName
                //##################################################
                // Cache Configuration
                //##################################################
                // no data go out from system
                this.setProperty("hazelcast.phone.home.enabled", "false")
                        .addMapConfig(MapConfig("map")
                                .apply {
                                    //least recently used
                                    evictionPolicy = EvictionPolicy.LRU
                                    // time to live to 240 seconds
                                    timeToLiveSeconds = 240
                                })


                //##################################################
                // Eureka
                //##################################################
                this.setProperty("hazelcast.discovery.enabled", "true")
                // Discovery Strategy Configuration
                val properties = HashMap<String, Comparable<*>>()
                properties["self-registration"] = "true"
                properties["use-metadata-for-host-and-port"] = "true"
                properties["namespace"] = "hazelcast"

                val discoveryStrategyConfig = DiscoveryStrategyConfig(EurekaOneDiscoveryStrategyFactory(), properties)

                // Join Configuration
                val joinConfig = this.networkConfig.join
                joinConfig.multicastConfig.isEnabled = false  // Disable Multicast Network
                joinConfig.discoveryConfig.addDiscoveryStrategyConfig(discoveryStrategyConfig)

            }
}


fun main(args: Array<String>) {
    runApplication<HazelcastDemoApplication>(*args)
}

@RestController
@RequestMapping(value = ["/api"], produces = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
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