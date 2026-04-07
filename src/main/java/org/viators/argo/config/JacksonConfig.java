package org.viators.argo.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures Jackson to support {@link org.openapitools.jackson.nullable.JsonNullable}
 * for PATCH/partial update semantics.
 *
 * <p>Spring Boot auto-discovers Jackson modules declared as beans and registers them
 * with the application's ObjectMapper. The {@link JsonNullableModule} teaches
 * Jackson how to serialize and deserialize {@code JsonNullable<T>} fields, enabling
 * three-state semantics: undefined (absent), explicitly null, and has-value.</p>
 */
@Configuration
public class JacksonConfig {

    /**
     * Registers the JsonNullable Jackson module.
     *
     * <p>This bean is automatically detected by Spring Boot's
     * {@code JacksonAutoConfiguration} and registered with the global ObjectMapper.
     * No additional wiring is needed.</p>
     *
     * @return the JsonNullableModule instance
     */
    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
}
