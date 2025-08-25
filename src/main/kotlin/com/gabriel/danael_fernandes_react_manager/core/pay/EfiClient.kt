package com.gabriel.danael_fernandes_react_manager.core.pay

import java.io.FileInputStream
import java.math.BigDecimal
import java.net.URL
import java.security.KeyStore
import java.util.Base64
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.RoundingMode
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.*

@Component
class EfiClient(
    @Value("\${efibank.certificate}")
    private val certPath: String,
    @Value("\${efibank.client_id}")
    private val clientId: String,
    @Value("\${efibank.client_secret}")
    private val clientSecret: String,
    @Value("\${efibank.chave_pix}")
    private val chavePix: String,

): Payment {

    private val mapper = jacksonObjectMapper()
    private val baseUrl = "https://pix-h.api.efipay.com.br"

    private fun createSslContext(): SSLContext {
        val ks = KeyStore.getInstance("PKCS12")
        FileInputStream(certPath).use { ks.load(it, null) } // sem senha

        val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        kmf.init(ks, null)

        val ts = KeyStore.getInstance(KeyStore.getDefaultType())
        FileInputStream(System.getProperty("java.home") + "/lib/security/cacerts").use {
            ts.load(it, "changeit".toCharArray()) // senha padrão do cacerts
        }
        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        tmf.init(ts)

        return SSLContext.getInstance("TLSv1.2").apply {
            init(kmf.keyManagers, tmf.trustManagers, null)
        }
    }
    private fun httpsRequest(url: String, method: String, body: String? = null, headers: Map<String, String> = emptyMap()): String {
        val sslContext = createSslContext()

        val conn = (URL(url).openConnection() as HttpsURLConnection).apply {
            sslSocketFactory = sslContext.socketFactory
            requestMethod = method
            doInput = true
            if (body != null) doOutput = true
            connectTimeout = 10000
            readTimeout = 15000
            if (!headers.containsKey("Accept")) {
                setRequestProperty("Accept", "application/json")
            }
            headers.forEach { (k, v) -> setRequestProperty(k, v) }
        }

        if (body != null) {
            conn.outputStream.use { os -> os.write(body.toByteArray(Charsets.UTF_8)) }
        }
        val responseStream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
//        val response = conn.inputStream.bufferedReader().readText()
        val response = responseStream.bufferedReader().readText()
        conn.disconnect()
        return response
    }
    fun getAccessToken(): String {
        if(!isExpire()){
            return token
        }
        val basic = Base64.getEncoder().encodeToString(
            "$clientId:$clientSecret".toByteArray(StandardCharsets.UTF_8)
        )

        val body = """{"grant_type":"client_credentials"}"""
        val json = httpsRequest(
            url = "$baseUrl/oauth/token",
            method = "POST",
            body = body,
            headers = mapOf(
                "Authorization" to "Basic $basic",
                "Content-Type" to "application/json"
            )
        )
        val map: Map<String, Any?> = mapper.readValue(json)
        val token = map["access_token"] as? String
            ?: error("Não veio access_token na resposta: $json")

        if (Companion.token.isBlank()){
            Companion.token = token
            val expireInSeconds = map["expires_in"] as Int
            expiresIn = LocalDateTime.now().plusSeconds(expireInSeconds.toLong())
        }
        return token
    }
    fun createCharge(value: BigDecimal, chavePix: String): String {
        val token = getAccessToken()
        val body = mapper.writeValueAsString(
            mapOf(
                "calendario" to mapOf("expiracao" to 3600),
                "valor" to mapOf("original" to value.setScale(2, RoundingMode.HALF_UP).toString()),
                "chave" to chavePix,
            )
        )
        return httpsRequest(
            url = "$baseUrl/v2/cob",
            method = "POST",
            body = body,
            headers = mapOf(
                "Authorization" to "Bearer $token",
                "Content-Type" to "application/json"
            )
        )
    }
    fun createQrCode(idLocation: Int): String{
        val result = httpsRequest(
            url = "$baseUrl/v2/loc/$idLocation/qrcode",
            method = "GET",
            headers = mapOf(
                "Authorization" to "Bearer $token",
                "Content-Type" to "application/json"
            )
        )

//        val map: Map<String, Any?> = mapper.readValue(result)
//        val qrCode = map["imagemQrcode"] as String

        return result
    }
    companion object{
        private var token = ""
        private var expiresIn = LocalDateTime.MIN

        private fun isExpire():Boolean{
            return expiresIn.isBefore(LocalDateTime.now())
        }
    }
    override fun charge(price: BigDecimal): PixPagamento {

        var response = createCharge(price, this.chavePix)


        var map: Map<String, Any> = mapper.readValue(response)
        val loc = map["loc"] as Map<*,*>
        val idLocation = loc["id"] as Int
        val pixCopiaECola = map["pixCopiaECola"] as String
        val valor = map["valor"] as Map<*,*>
        val valorOriginal = valor["original"] as String
        val calendario = map["calendario"] as Map<*,*>
        val dateString = calendario["criacao"] as String
        val expiracaoSec = calendario["expiracao"] as Int
        val zonedDateTime = ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
        val expiracao = zonedDateTime.toLocalDateTime().plusSeconds(expiracaoSec.toLong())
        val txid = map["txid"] as String
        response = createQrCode(idLocation)
        map = mapper.readValue(response)
        val qrCodeBase64 = map["imagemQrcode"] as String
        return PixPagamento(
            pixCopiaECola = pixCopiaECola,
            valor = BigDecimal(valorOriginal),
            expiracao = expiracao,
            qrCodeImageBase64 = qrCodeBase64,
            txId = txid
        )
    }
}
