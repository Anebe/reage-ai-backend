package com.gabriel.danael_fernandes_react_manager.entity.rules

import com.gabriel.danael_fernandes_react_manager.entity.Streamer
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.apache.tomcat.util.digester.Rule
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes




class RuleException(message: String) : Exception(message)

data class Video(val durationInMinutes: Int)
data class PricingContext(var price: Double = 0.0)


@Entity
data class CustomRule(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: Streamer,

    @Enumerated(EnumType.STRING)
    val ruleType: RuleType,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    val ruleContent: String
)