package com.gabriel.danael_fernandes_react_manager

import com.gabriel.danael_fernandes_react_manager.entity.rules.DurationValidationRule
import com.gabriel.danael_fernandes_react_manager.entity.rules.FixedPriceRule
import com.gabriel.danael_fernandes_react_manager.entity.rules.RuleInterface
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Configuration
class JsonConfig {

    @Bean
    fun json(): Json = Json {
        serializersModule = SerializersModule {
            // Diz ao serializador que PricingRule tem várias subclasses
            polymorphic(RuleInterface::class) {
                subclass(DurationValidationRule::class)
                subclass(FixedPriceRule::class)
            }
        }
        // Este é o campo que o serializador vai procurar no JSON para saber o tipo
        classDiscriminator = "rule_class_id" // Você pode nomear como quiser (ex: "@type")
    }
}