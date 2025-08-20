package com.gabriel.danael_fernandes_react_manager.database.repository

import com.gabriel.danael_fernandes_react_manager.database.entity.VideoOrder
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import javax.swing.text.html.Option

interface VideoOrderRepository: JpaRepository<VideoOrder, Long> {

    fun findByTxId(txid: String): Optional<VideoOrder>
}