package com.gabriel.danael_fernandes_react_manager.database.repository

import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator
import org.springframework.data.jpa.repository.JpaRepository

interface ContentCreatorRepository : JpaRepository<ContentCreator, Long> {
    fun findByUsername(username: String): ContentCreator?

}