package com.gabriel.danael_fernandes_react_manager.database.repository

import com.gabriel.danael_fernandes_react_manager.database.entity.ContentCreator
import com.gabriel.danael_fernandes_react_manager.database.entity.Suggest
import org.springframework.data.jpa.repository.JpaRepository

interface SuggestRepository: JpaRepository<Suggest, Long>{

    fun findAllByContentCreator(contentCreator: ContentCreator): List<Suggest>
}