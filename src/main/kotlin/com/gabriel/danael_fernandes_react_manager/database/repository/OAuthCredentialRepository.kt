package com.gabriel.danael_fernandes_react_manager.database.repository

import com.gabriel.danael_fernandes_react_manager.database.entity.OAuthCredential
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface OAuthCredentialRepository: JpaRepository<OAuthCredential, Long> {

    //override fun findById(id: UUID): Optional<OAuthCredential>
    override fun findById(id: Long): Optional<OAuthCredential>

    @Query("SELECT o FROM OAuthCredential o WHERE o.id = 1")

    fun getYoutubeOauth(): OAuthCredential
}

