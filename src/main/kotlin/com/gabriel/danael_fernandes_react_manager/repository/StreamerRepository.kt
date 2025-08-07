package com.gabriel.danael_fernandes_react_manager.repository

import com.gabriel.danael_fernandes_react_manager.entity.Streamer
import org.springframework.data.jpa.repository.JpaRepository

interface StreamerRepository : JpaRepository<Streamer, Long> {
    fun findByUsername(username: String): Streamer?

}