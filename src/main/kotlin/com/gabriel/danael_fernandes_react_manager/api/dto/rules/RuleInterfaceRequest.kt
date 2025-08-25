package com.gabriel.danael_fernandes_react_manager.api.dto.rules

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.gabriel.danael_fernandes_react_manager.core.rule_processor.rule.RuleInterface

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,      // Usa um nome simples como identificador
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"       // Campo no JSON que indica o tipo
)
@JsonSubTypes(
    JsonSubTypes.Type(value = RangeDurationRequestDTO::class, name = "range_duration"),
    JsonSubTypes.Type(value = FixedPriceResquestDTO::class, name = "fixed_price")
)
interface RuleInterfaceRequest {

    fun to(): RuleInterface
}