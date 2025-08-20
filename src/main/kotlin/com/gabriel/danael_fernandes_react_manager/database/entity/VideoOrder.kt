package com.gabriel.danael_fernandes_react_manager.database.entity

import jakarta.persistence.*

@Entity
data class VideoOrder(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val link: String,
    val txId: String,

    @ManyToOne
    @JoinColumn(name = "content_creator_id", nullable = false)
    val contentCreator: ContentCreator
)
