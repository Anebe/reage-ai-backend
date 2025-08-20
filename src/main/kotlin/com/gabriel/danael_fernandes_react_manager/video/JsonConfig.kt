package com.gabriel.danael_fernandes_react_manager.video

import com.gabriel.danael_fernandes_react_manager.video.rule.DurationValidationRule
import com.gabriel.danael_fernandes_react_manager.video.rule.FixedPriceRule
import com.gabriel.danael_fernandes_react_manager.video.rule.RuleInterface
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
            // Diz ao serializador que PricingRule tem várias subclasses
            polymorphic(RuleInterface::class) {
                subclass(DurationValidationRule::class)
                subclass(FixedPriceRule::class)
            }
            contextual(BigDecimal::class, BigDecimalSerializer)
        }
        // Este é o campo que o serializador vai procurar no JSON para saber o tipo
        classDiscriminator = "rule_class_id" // Você pode nomear como quiser (ex: "@type")
    }
}

object BigDecimalSerializer : KSerializer<BigDecimal> {

    // Define a forma como o serializador se descreve no JSON.
    // Usamos 'STRING' para indicar que ele será representado como um texto.
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)

    // Converte o BigDecimal para String na hora de serializar para o JSON
    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toPlainString())
    }

    // Converte a String do JSON de volta para um BigDecimal
    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }
}