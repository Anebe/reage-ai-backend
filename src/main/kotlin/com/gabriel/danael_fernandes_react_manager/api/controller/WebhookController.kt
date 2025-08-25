package com.gabriel.danael_fernandes_react_manager.api.controller

import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeAuthorization
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeClient
import com.gabriel.danael_fernandes_react_manager.core.video.YoutubeUtil
import com.gabriel.danael_fernandes_react_manager.database.entity.StatusPagamento
import com.gabriel.danael_fernandes_react_manager.database.entity.SuggestStatus
import com.gabriel.danael_fernandes_react_manager.database.repository.ContentCreatorRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.PagamentoRepository
import com.gabriel.danael_fernandes_react_manager.database.repository.SuggestRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.net.URL
import java.util.UUID

data class PixRecebido(
    val txId: String,
    val valor: BigDecimal
)
@RestController
@RequestMapping("/api")
class PaymantController(
    private val contentCreatorRepository: ContentCreatorRepository,
    private val suggestRepository: SuggestRepository,
    private val pagamentoRepository: PagamentoRepository,
    private val videoOrderRepository: SuggestRepository,
    private val json: Json,
    private val youtubeClientApi: YoutubeClient,
    private val youtube: YoutubeAuthorization
) {

    //TODO ao confirmar pagamento adicionar video a fila
    @PostMapping("/webhook/pix")
    fun pixRecebido(@RequestBody str: String): ResponseEntity<Void> {
        //TODO melhorar o tratamento de entrada
        val payReceive = json.parseToJsonElement(str).jsonObject
        val pixRecebidos = payReceive["pix"]?.jsonArray?.map {
            val txid = it.jsonObject["txid"]?.jsonPrimitive?.content ?: ""
            val valor = it.jsonObject["valor"]?.jsonPrimitive?.content ?: ""
            PixRecebido(txid, BigDecimal(valor))
        }?: emptyList()

            pixRecebidos.forEach{ pix ->
                val pagamentoOp = pagamentoRepository.findByTxid(pix.txId)
                if (pagamentoOp != null && pagamentoOp.valor == pix.valor){
                    val pagamento = pagamentoOp.copy(status = StatusPagamento.APROVADO)
                    var suggestion = pagamento.suggest
                    val playlistId = suggestion.contentCreator.playlistId
                    val video = YoutubeUtil.extractVideoId(URL(suggestion.link))
                    requireNotNull(video)
                    val playlist = youtubeClientApi.searchPlaylist(playlistId)
                    playlist?.addVideo(video)
                    suggestion = suggestion.copy(status = SuggestStatus.ADICIONADO_FILA)
                    suggestRepository.save(suggestion)

                }
                //TODO preciso fazer um patch pra atualizar o status pra concluido no efi e ver se isso bloqueia pagamentos pra esse txid
            }

        return ResponseEntity.ok().build()
    }

    @GetMapping("/webhook/youtube")
    fun oauth2Callback(
        @RequestParam code: String?,
        @RequestParam state: String?, // Seu user ID, que passamos no 'state'
        @RequestParam error: String?
    ): ResponseEntity<String> {
        if (error != null) {
            return ResponseEntity.badRequest().body("Erro na autorização do YouTube: $error")
        }
        if (code == null) {
            return ResponseEntity.badRequest().body("Código de autorização não recebido.")
        }
        if (state == null) {
            return ResponseEntity.badRequest().body("Estado (user ID) não recebido. Não é possível associar o token.")
        }

        val id = UUID.fromString(state)
        youtube.saveCredentials(code, id)
        return ResponseEntity.ok("Autorização do YouTube concluída com sucesso! Tokens salvos.")
    }
}