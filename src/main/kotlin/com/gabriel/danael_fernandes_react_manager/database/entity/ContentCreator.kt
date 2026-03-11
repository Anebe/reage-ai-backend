package com.gabriel.danael_fernandes_react_manager.database.entity

import jakarta.persistence.*

@Entity
data class ContentCreator(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val fullname: String,
    @Column(unique = true)
    val username: String,
    val playlistId: String?,
    val displayName: String = username,
    val message: String = "",
    var profilePictureFilename: String
)