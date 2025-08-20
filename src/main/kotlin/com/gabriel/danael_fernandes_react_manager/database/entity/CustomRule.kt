package com.gabriel.danael_fernandes_react_manager.database.entity

import com.gabriel.danael_fernandes_react_manager.video.RuleType
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
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal


data class ResultVideoProcess(
    var price: BigDecimal = BigDecimal.ZERO,
    val errors: MutableList<String> = mutableListOf()
)


@Entity
data class CustomRule(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_creator_id", nullable = false)
    val user: ContentCreator,

    @Enumerated(EnumType.STRING)
    val ruleType: RuleType,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    val ruleContent: String
)