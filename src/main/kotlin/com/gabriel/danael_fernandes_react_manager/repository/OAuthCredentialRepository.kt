package com.gabriel.danael_fernandes_react_manager.repository

import com.gabriel.danael_fernandes_react_manager.entity.OAuthCredential
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OAuthCredentialRepository: JpaRepository<OAuthCredential, Long> {
}