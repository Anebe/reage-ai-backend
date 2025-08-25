package com.gabriel.danael_fernandes_react_manager.database.repository

import com.gabriel.danael_fernandes_react_manager.database.entity.Pagamento
import com.gabriel.danael_fernandes_react_manager.database.entity.Suggest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface PagamentoRepository: JpaRepository<Pagamento, UUID>{

    fun findByTxid(txid: String): Pagamento?

    @Query("SELECT p FROM Pagamento p WHERE p.status = :status AND p.suggest = :suggest")
    fun findAprovadoOfSuggest(suggest: Suggest): Pagamento?
}
