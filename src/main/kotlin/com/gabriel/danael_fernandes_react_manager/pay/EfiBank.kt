package com.gabriel.danael_fernandes_react_manager.pay

import br.com.efi.efisdk.EfiPay
import br.com.efi.efisdk.exceptions.EfiPayException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZonedDateTime

//@Component
class EfiBank : Payment {
    override fun charge(price: BigDecimal): PixPagamento {
        var errorMsg = ""
        val credentials = CredentialsEfiBank()

        val options = JSONObject()
        options.put("client_id", credentials.client_id)
        options.put("client_secret", credentials.client_secret)
        options.put("certificate", credentials.certificate)
        options.put("sandbox", true)

        val body = JSONObject()
        body.put("calendario", JSONObject().put("expiracao", 3600))
        body.put("valor", JSONObject().put("original", price.setScale(2, RoundingMode.HALF_UP).toString()))
        body.put("chave", "485f906a-201c-4241-9aa3-6ad81bbdb579")

//        try {
            val efi = EfiPay(options)
//            val responsePix = withContext(Dispatchers.IO) {
//                PixResponse(efi.call("pixCreateImmediateCharge", HashMap(), body))
//            }
            val responsePix = PixResponse(efi.call("pixCreateImmediateCharge", HashMap(), body))

            val params = HashMap<String, String>()
            params["id"] = responsePix.loc.id.toString()
//            val responseQrCode = withContext(Dispatchers.IO) {
//                QrCodeResponse(efi.call("pixGenerateQRCode", params, JSONObject()))
//            }
            val responseQrCode = QrCodeResponse(efi.call("pixGenerateQRCode", params, JSONObject()))

            val zonedDateTime = ZonedDateTime.parse(responsePix.calendario.criacao)
            val expiracao = zonedDateTime.toLocalDateTime().plusSeconds(responsePix.calendario.expiracao.toLong())

            return PixPagamento(
                valor = BigDecimal(responsePix.valor.original),
                expiracao = expiracao,
                qrCodeImageBase64 = responseQrCode.imagemQrcode,
                pixCopiaECola = responseQrCode.qrcode,
                txId = responsePix.txid
            )
//        } catch (e: EfiPayException) {
//            errorMsg += e.error
//            errorMsg +=  "\n ${e.errorDescription}"
//            errorMsg +=  "\n ${e.message}"
//
//        } catch (e: Exception) {
//            errorMsg += e.message ?: ""
//            errorMsg +=  "\n ${e.localizedMessage}"
//        }
//
//        throw PayException(errorMsg)
    }
}


data class PixResponse(
    val calendario: Calendario,
    val txid: String,
    val revisao: Int,
    val loc: Loc,
    val location: String,
    val status: String,
    val devedor: Devedor?,
    val valor: Valor,
    val chave: String,
    val solicitacaoPagador: String?,
    val pixCopiaECola: String
) {
    // Construtor secundário para criar PixResponse a partir de um JSONObject
    constructor(jsonObject: JSONObject) : this(
        calendario = Calendario(jsonObject.getJSONObject("calendario")),
        txid = jsonObject.getString("txid"),
        revisao = jsonObject.getInt("revisao"),
        loc = Loc(jsonObject.getJSONObject("loc")),
        location = jsonObject.getString("location"),
        status = jsonObject.getString("status"),
        devedor = if (jsonObject.has("devedor")) Devedor(jsonObject.getJSONObject("devedor")) else null,
        valor = Valor(jsonObject.getJSONObject("valor")),
        chave = jsonObject.getString("chave"),
        solicitacaoPagador = if (jsonObject.has("solicitacaoPagador")) jsonObject.getString("solicitacaoPagador") else null,
        pixCopiaECola = jsonObject.getString("pixCopiaECola")
    )
}

data class Calendario(
    val criacao: String,
    val expiracao: Int
) {
    // Construtor secundário para criar Calendario a partir de um JSONObject
    constructor(jsonObject: JSONObject) : this(
        criacao = jsonObject.getString("criacao"),
        expiracao = jsonObject.getInt("expiracao")
    )
}

data class Loc(
    val id: Int,
    val location: String,
    val tipoCob: String
) {
    // Construtor secundário para criar Loc a partir de um JSONObject
    constructor(jsonObject: JSONObject) : this(
        id = jsonObject.getInt("id"),
        location = jsonObject.getString("location"),
        tipoCob = jsonObject.getString("tipoCob")
    )
}

data class Devedor(
    val cnpj: String,
    val nome: String
) {
    // Construtor secundário para criar Devedor a partir de um JSONObject
    constructor(jsonObject: JSONObject) : this(
        cnpj = jsonObject.getString("cnpj"),
        nome = jsonObject.getString("nome")
    )
}

data class Valor(
    val original: String
) {
    // Construtor secundário para criar Valor a partir de um JSONObject
    constructor(jsonObject: JSONObject) : this(
        original = jsonObject.getString("original")
    )
}

data class QrCodeResponse(
    val qrcode: String,
    val imagemQrcode: String,
    val linkVisualizacao: String
) {
    // Construtor secundário para criar QrCodeResponse a partir de um JSONObject
    constructor(jsonObject: JSONObject) : this(
        qrcode = jsonObject.getString("qrcode"),
        imagemQrcode = jsonObject.getString("imagemQrcode"),
        linkVisualizacao = jsonObject.getString("linkVisualizacao")
    )
}
