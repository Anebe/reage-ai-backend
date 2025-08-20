package com.gabriel.danael_fernandes_react_manager.database.repository

import com.gabriel.danael_fernandes_react_manager.database.entity.OAuthCredential
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface OAuthCredentialRepository: JpaRepository<OAuthCredential, UUID> {

    //override fun findById(id: UUID): Optional<OAuthCredential>
}

