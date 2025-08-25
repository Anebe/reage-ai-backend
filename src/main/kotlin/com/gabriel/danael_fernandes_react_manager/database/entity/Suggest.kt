package com.gabriel.danael_fernandes_react_manager.database.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


enum class SuggestStatus {
    AGUARDANDO_PAGAMENTO,
    ADICIONADO_FILA,
    ASSISTIDO,
    CANCELADO
}

@Entity
data class Suggest(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val link: String,
    val status: SuggestStatus,
    val followerNick: String,
    val followerMessage: String,
    val createdAt: LocalDate = LocalDate.now(),
    val valor: BigDecimal,
    val expireAt: LocalDateTime,
    //val txId: String,

    @ManyToOne
    @JoinColumn(name = "content_creator_id", nullable = false)
    val contentCreator: ContentCreator
)
