package com.gabriel.danael_fernandes_react_manager.config

import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.DurationValidationRule
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.FixedPriceRule
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.RuleInterface
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import java.math.BigDecimal

@Configuration
class JsonConfig {

    @Bean
    fun json(): Json = Json {
        serializersModule = SerializersModule {
            polymorphic(RuleInterface::class) {

                subclass(DurationValidationRule::class)
                subclass(FixedPriceRule::class)
            }
            contextual(BigDecimal::class, BigDecimalSerializer)
        }
        classDiscriminator = "type"
    }
}

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toPlainString())
    }

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }
}