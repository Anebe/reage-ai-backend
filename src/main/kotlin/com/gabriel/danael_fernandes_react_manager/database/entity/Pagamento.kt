package com.gabriel.danael_fernandes_react_manager.database.entity

import com.gabriel.danael_fernandes_react_manager.config.BigDecimalSerializer
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import kotlinx.serialization.Serializer
import java.math.BigDecimal
import java.util.UUID

enum class StatusPagamento {
    PENDENTE,
    APROVADO,
    RECUSADO,
}
@Entity
data class Pagamento(
    @Id
    val id: UUID = UUID.randomUUID(),
    @Column(unique = true)
    val txid: String,
    val status: StatusPagamento,
    val valor: BigDecimal,

    @OneToOne
    val suggest: Suggest
)
