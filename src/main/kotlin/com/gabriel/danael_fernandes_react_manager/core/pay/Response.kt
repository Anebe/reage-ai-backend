package com.gabriel.danael_fernandes_react_manager.core.pay

import org.json.JSONObject


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
