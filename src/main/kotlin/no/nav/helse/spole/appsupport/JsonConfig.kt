package no.nav.helse.spole.appsupport

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JsonConfig {

    @Bean
    fun objectMapper() = ObjectMapper().findAndRegisterModules().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)

}